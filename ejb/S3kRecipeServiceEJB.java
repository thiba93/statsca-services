package com.carrus.statsca.ejb;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.RaceCardService;
import com.carrus.statsca.S3kRecipeService;
import com.carrus.statsca.dto.AuthorisedPartnerDTO;
import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.FormulationStakeDTO;
import com.carrus.statsca.dto.RaceDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.carrus.statsca.utils.StatsCaUtils;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.partner.AuthorisedPartner;
import com.pmc.club.entity.recipe.BetCodeRecipe;
import com.pmc.club.entity.recipe.FormulationStake;
import com.pmc.club.entity.recipe.S3kRecipeResponse;
import com.pmc.club.exception.MutuelException;
import com.pmc.club.references.RaceRef;
import com.pmc.club.service.RaceService;
import com.pmc.star3000.service.PartnerService;
import com.pmc.star3000.service.RecipeService;

@Singleton
public class S3kRecipeServiceEJB implements S3kRecipeService {

	@Inject
	private RaceService raceService;

	@Inject
	private RaceCardService raceCardService;

	@Inject
	private RecipeService recipeService;

	@Inject
	private PartnerService partnerService;

	private List<BetCodeRecipe> s3kBetCodeRecipes;

	/**
	 * Map stockant les enjeux par paris remontés depuis le s3k pour une course
	 * donnée
	 */
	private Map<Long, List<BetCodeRecipe>> mapS3kBetCodeRecipes = new HashMap<>();

	/**
	 * Map stockant les réponses de demandes d'enjeux par paris fait par le front
	 * pour une course donnée, les réponses sont sous forme de sessions contenant
	 * les enjeux par pari pour une course donnée
	 */
	private Map<Long, SessionDTO> storedResponses = new HashMap<>();

	/** Logger par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(S3kRecipeServiceEJB.class);

	private static final long INTERVAL = 30;

	private static final long ATTEMPT_INTERVAL = 1 * 1000L;

	private static final long NB_MAX_ATTEMPT = 3;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<BetCodeRecipe> getS3kBetCodeRecipes() {
		return s3kBetCodeRecipes;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Map<Long, SessionDTO> getStoredResponses() {
		return storedResponses;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void clearStoredDatas() {
		this.mapS3kBetCodeRecipes = new HashMap<>();
		this.storedResponses = new HashMap<>();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean saveBetCodeRecipes(List<BetCodeRecipe> betCodeRecipe, Long racePk, RaceRef raceRefKey) {
		boolean change = false;
		int size = betCodeRecipe != null ? betCodeRecipe.size() : 0;
		if (betCodeRecipe == null)
			LOGGER.warn("The bet recipes list is null");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("1 - Bet Recipe event received for {} : {} recipe(s) ", raceRefKey, size);
		}
		if (!this.mapS3kBetCodeRecipes.containsKey(racePk)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("new Bet Recipes received {} for {}", betCodeRecipe.size(), raceRefKey);
			}
			this.mapS3kBetCodeRecipes.put(racePk, betCodeRecipe);
			change = true;
		} else {
			List<BetCodeRecipe> newBetRecipes = betCodeRecipe;
			List<BetCodeRecipe> recipesToAdd = new ArrayList<>();
			List<BetCodeRecipe> recipesToModify = new ArrayList<>();
			// rrDTO is a raceref - contractor - cashamount structure
			for (BetCodeRecipe rrDto : newBetRecipes) {
				List<BetCodeRecipe> sameBetRefs = this.mapS3kBetCodeRecipes.get(racePk).stream()
						.filter(srr -> betRecipeEquals(rrDto, srr)).collect(Collectors.toList());

				if (sameBetRefs == null || sameBetRefs.isEmpty()) {
					recipesToAdd.add(rrDto);
				} else {
					BetCodeRecipe rrDTO = sameBetRefs.stream().filter(sc -> sc.getContractor() == rrDto.getContractor())
							.findFirst().orElse(null);

					if (rrDTO == null) {
						recipesToAdd.add(rrDto);
					} else {
						if (rrDTO.getCashAmount() != rrDto.getCashAmount()) {
							recipesToModify.add(rrDto);
						}
					}
				}
			}

			boolean isThereRecipeToAdd = recipesToAdd != null && !recipesToAdd.isEmpty();
			boolean isTheRecipeToModify = recipesToModify != null && !recipesToModify.isEmpty();
			change = isThereRecipeToAdd || isTheRecipeToModify;

			// ajouts
			if (isThereRecipeToAdd) {
				LOGGER.info("**** Adding new bet recipe to models : {} ****", recipesToAdd.size());
				this.mapS3kBetCodeRecipes.get(racePk).addAll(recipesToAdd);
			}
			// modifications
			if (recipesToModify != null && !recipesToModify.isEmpty()) {
				for (BetCodeRecipe rrDto1 : recipesToModify) {
					for (int i = 0; i < this.mapS3kBetCodeRecipes.get(racePk).size(); i++) {
						if (betRecipeEquals(rrDto1, this.mapS3kBetCodeRecipes.get(racePk).get(i))) {
							this.mapS3kBetCodeRecipes.get(racePk).get(i).setCashAmount(rrDto1.getCashAmount());
							this.mapS3kBetCodeRecipes.get(racePk).get(i).setComputedTime(rrDto1.getComputedTime());
						}
					}
				}
			}
		}
		return change;
	}

	@Override
	public boolean isUpdateTimeSameAsServerUpdateTime(SessionDTO session, ZonedDateTime clientUpdateTime) {
		if (clientUpdateTime == null) {
			return false;
		}

		List<AuthorisedPartnerDTO> authorisedPartners = session.getEvents().get(0).getRaces().get(0)
				.getAuthorizedPartners();
		boolean sameUpdateTime = true;

		for (int i = 0; i < authorisedPartners.size() && sameUpdateTime; i++) {
			ZonedDateTime serverUpdateTime = authorisedPartners.get(i).getUpdateDate().truncatedTo(ChronoUnit.SECONDS);
			sameUpdateTime = clientUpdateTime.isAfter(serverUpdateTime) || clientUpdateTime.isEqual(serverUpdateTime);
		}

		return sameUpdateTime;
	}

	private boolean betRecipeEquals(BetCodeRecipe rrDto1, BetCodeRecipe rr) {
		return rr.getRaceRef().toString().equals(rrDto1.getRaceRef().toString())
				&& rr.getBetCodeRef() == rrDto1.getBetCodeRef() && rr.getContractor() == rrDto1.getContractor();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public SessionDTO getBetRecipes(Long racePk) {
		// - Get selected RaceRef
		Race race = raceService.find(racePk);
		if (race != null) {
			RaceRef raceRef = StatsCaUtils.getRaceRefFromRace(race);
			// if( StatsCaUtils.isRaceAfterDeparted(race.getState())) {
			for (int attempt = 0; attempt < NB_MAX_ATTEMPT; attempt++) {
				try {
					SessionDTO sessionDTO = storedResponses.containsKey(racePk) ? storedResponses.get(racePk) : null;
					if (sessionDTO == null) {
						sessionDTO = buildSessionWithMemoryBetRecipes(racePk, raceRef);
						/*
						 * if(sessionDTO == null) { sessionDTO = retrieveBetRecipesFomDB(racePk,
						 * raceRef, race.getExpectedStart()); }
						 */
					}
					return sessionDTO;
				} catch (Exception e) {
					LOGGER.warn("Failed try {} / {} for race R{}C{} {} - Exception encountered : {} ", (attempt + 1),
							NB_MAX_ATTEMPT, race.getEvent().getId(), race.getNumber(), race.getEvent().getDate(),
							e.getMessage());
				}
			}
			// }
			// - Check last stored value
			/*
			 * if(!this.mapS3kBetCodeRecipes.containsKey(raceRef.toString())) {
			 * this.mapS3kBetCodeRecipes.put(raceRef.toString(), new ArrayList<>()); }
			 * List<BetCodeRecipe> betCodeRecipes =
			 * this.mapS3kBetCodeRecipes.get(raceRef.toString()); if
			 * (!betCodeRecipes.isEmpty()) { boolean isRecentData =
			 * !isDatasAncient(betCodeRecipes);
			 * 
			 * // - If data is recent -> send back last data if (isRecentData) { if
			 * (storedResponses.get(racePk) == null) { try {
			 * buildNewRecipes(raceRef,racePk); } catch (MutuelException e) {
			 * LOGGER.error("MutuelException while getting bet recipes : {}",
			 * e.getMessage()); } } } else { // - If old data -> send RecipeRequest try {
			 * buildNewRecipes(raceRef,racePk); } catch (MutuelException e) {
			 * LOGGER.error("MutuelException while getting bet recipes : {}",
			 * e.getMessage()); } }
			 * 
			 * } else { // - If data not yet stored -> send RecipeRequest try {
			 * buildNewRecipes(raceRef, racePk); } catch (MutuelException e) {
			 * LOGGER.error("MutuelException while getting bet recipes : {}",
			 * e.getMessage()); } }
			 */

			// - Send SessionDTO response
			return storedResponses.get(racePk);
		} else {
			LOGGER.warn("WARNING while getting bet recipes: the race {} is unknown on the DB ");
		}
		return null;
	}

	// TODO ????
	private boolean checkIfNewRecipesRequired(Long racePk, ZonedDateTime raceStartTime) {
		boolean newRecipesRequired = false;
		// get authorizedPartner from session
		List<AuthorisedPartner> authorisedPartners = partnerService.getAuthorisedPartnerByRace(racePk);

		for (AuthorisedPartner authorisedPartner : authorisedPartners) {
			if (authorisedPartner.getFormulationStakes() == null
					|| authorisedPartner.getFormulationStakes().isEmpty()) {
				newRecipesRequired = true;
				break;
			}
			for (FormulationStake formulationStake : authorisedPartner.getFormulationStakes()) {
				newRecipesRequired = formulationStake.getUpdateDate().isBefore(raceStartTime);
				if (newRecipesRequired)
					break;
			}
			if (newRecipesRequired)
				break;
		}
		return newRecipesRequired;
	}

	/*private SessionDTO _retrieveBetRecipesFomDB(Long racePk, RaceRef raceRef, ZonedDateTime raceStartTime) {

		boolean newRecipesRequired = checkIfNewRecipesRequired(racePk, raceStartTime);
		if (newRecipesRequired) {
			LOGGER.info("{} - Start {} - New Recipes Required", raceRef, raceStartTime);
			try {
				buildNewRecipes(raceRef, racePk);
			} catch (MutuelException e) {
				LOGGER.error("MutuelException while getting bet recipes : {}", e.getMessage());
			}
		} else {
			// ?? get recipes from DB : should be from memory
			LOGGER.info("{} - Start {} - Getting Recipes from DB", raceRef, raceStartTime);
			// buildFromDB(racePk, raceRef);

			getRecipeFromSession(racePk, raceRef);
		}
		return storedResponses.containsKey(racePk) ? storedResponses.get(racePk) : null;
	}

	private SessionDTO retrieveBetRecipesFomDB(Long racePk, RaceRef raceRef, ZonedDateTime raceStartTime) {
		getRecipeFromSession(racePk, raceRef);
		return storedResponses.containsKey(racePk) ? storedResponses.get(racePk) : null;
	}*/

	/**
	 * recupère les enjeux par pari dans la map mémoire des enjeux par paris S3K
	 * pour en construire une session contenant les enjeux par paris et les stocker
	 * dans la map mémoire des sessions d'enjeux par paris
	 * 
	 * @param racePk  clé technique de la course côté couche mutuel
	 * @param raceRef référence externe de la course
	 * @return la session des enjeux par paris de la course donnée qui ont pu être
	 *         stockés en mémoire
	 */
	private SessionDTO buildSessionWithMemoryBetRecipes(Long racePk, RaceRef raceRef) {
		if (this.mapS3kBetCodeRecipes.containsKey(racePk)) {
			List<BetCodeRecipe> betCodeRecipes = this.mapS3kBetCodeRecipes.get(racePk);
			// loggingBetRecipes(betCodeRecipes, raceRef);

			buildAndSaveBetRecipeSession(raceRef, racePk, betCodeRecipes);
			return storedResponses.get(racePk);
		}
		return null;
	}

	/**
	 * recupère les enjeux par pari dans la map mémoire des enjeux par paris S3K
	 * pour en construire une session contenant les enjeux par paris et les stocker
	 * dans la map mémoire des sessions d'enjeux par paris
	 * 
	 * @param racePk  clé technique de la course côté couche mutuel
	 * @param raceRef référence externe de la course
	 * @return la session des enjeux par paris de la course donnée qui ont pu être
	 *         stockés en mémoire
	 */
	/*private SessionDTO _buildSessionWithMemoryBetRecipes(Long racePk, RaceRef raceRef) {
		if (this.mapS3kBetCodeRecipes.containsKey(raceRef.toString())) {
			List<BetCodeRecipe> betCodeRecipes = this.mapS3kBetCodeRecipes.get(raceRef.toString());
			// loggingBetRecipes(betCodeRecipes, raceRef);

			buildAndSaveBetRecipeSession(raceRef, racePk, betCodeRecipes);
			return storedResponses.get(raceRef.toString());
		}
		// find event
		EventDTO event = this.raceCardService.getRaceCard().getEvents().stream()
				.filter(e -> e.getId() == raceRef.getEventId()).findFirst().orElse(null);
		// find race
		RaceDTO race = event.getRaces().stream().filter(r -> r.getPk() == racePk).findFirst().orElse(null);

		_buildAndSaveBetRecipeSession(raceRef, race);

		return null;
	}*/

	private void loggingBetRecipes(List<BetCodeRecipe> betCodeRecipes, RaceRef raceRef) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String keyDate = raceRef.getEventDate().format(formatter);
		StringBuilder lineLog = new StringBuilder(
				String.format("BetRecipes %s R%dC%d :", keyDate, raceRef.getEventId(), raceRef.getRaceNumber()));
		if (betCodeRecipes != null) {
			for (BetCodeRecipe betCodeRecipe : betCodeRecipes) {
				lineLog.append(String.format(Locale.getDefault(), "Att %d Bet %d amnt %,.2f ;",
						betCodeRecipe.getContractor(), betCodeRecipe.getBetCodeRef(), betCodeRecipe.getCashAmount()));
			}
		}
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(lineLog.toString());
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Race putBetRecipesOnRace(RaceRef raceRef) {
		try {
			// Thread.sleep(2000);
			// get bet recipes from S3K
			S3kRecipeResponse recipes = recipeService.getS3KRecipeResponse(raceRef);
			List<BetCodeRecipe> betCodeRecipes = recipes.getBetCodeRecipe();
			loggingBetRecipes(betCodeRecipes, raceRef);
			// save bet recipes on session
			Race race = raceCardService.updateBetRecipe(betCodeRecipes, raceRef);
			// save bet recipes on map
			if (race != null) {
				boolean change = this.saveBetCodeRecipes(betCodeRecipes, race.getPk(), raceRef);
				if (change) {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("Bet recipe change detected for {}", raceRef);
					}
					this.saveResponseOnMemory(race.getPk(), raceRef);
				}
				return race;
			}
			
		} catch (MutuelException e) {
			LOGGER.error("error while sending bet recipes: {}", e.getMessage());
		} /*
			 * catch (InterruptedException e) {
			 * LOGGER.error("error while sending bet recipes: {}", e.getMessage());
			 * Thread.currentThread().interrupt(); }
			 */
		return null;
	}

	private void saveResponseOnMemory(Long racePk, RaceRef raceRef) {
		// Race race = raceService.getByRef(raceRef);
		/*
		 * if(race != null) {
		 */
		// Long pk = race.getPk();
		SessionDTO session = buildSessionWithMemoryBetRecipes(racePk, raceRef);
		if (session != null) {
			LOGGER.debug("bet recipe session put on memory for race {}", raceRef);
		}
		// }
	}

	private boolean isDatasAncient(List<BetCodeRecipe> s3kBetCodeRecipes) {
		boolean isDataAncient = false;
		for (BetCodeRecipe betCodeRecipe : s3kBetCodeRecipes) {
			long timeIntervalValue = INTERVAL;
			long diffInMillies = Math.abs((new Date()).getTime() - betCodeRecipe.getComputedTime().getTime());
			long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			isDataAncient = timeIntervalValue < diff;
			if (isDataAncient)
				break;
		}
		return isDataAncient;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void buildNewRecipes(RaceRef raceRef, Long pk) throws MutuelException {
		S3kRecipeResponse recipes;
		recipes = recipeService.getS3KRecipeResponse(raceRef);
		List<BetCodeRecipe> betCodeRecipes = recipes.getBetCodeRecipe();
		loggingBetRecipes(betCodeRecipes, raceRef);
		this.saveBetCodeRecipes(betCodeRecipes, pk, raceRef);
		raceCardService.updateBetRecipe(betCodeRecipes, raceRef);
		buildAndSaveBetRecipeSession(raceRef, pk, betCodeRecipes);

	}

	private void buildAndSaveBetRecipeSession(RaceRef raceRef, Long pk, List<BetCodeRecipe> betCodeRecipes) {
		SessionDTO betRecipeSession = new SessionDTO(raceRef.getEventDate());
		for (BetCodeRecipe betCodeRecipe : betCodeRecipes) {
			FormulationStakeDTO fsDTO = new FormulationStakeDTO(betCodeRecipe);
			raceCardService.constructBetRecipeSession(fsDTO, betRecipeSession);
		}
		if (storedResponses.containsKey(pk)) {
			storedResponses.remove(pk);
		}
		storedResponses.put(pk, betRecipeSession);
	}

	private void _buildAndSaveBetRecipeSession(RaceRef raceRef, RaceDTO race) {
		SessionDTO betRecipeSession = new SessionDTO(raceRef.getEventDate());
		/*
		 * for(BetCodeRecipe betCodeRecipe : betCodeRecipes) { FormulationStakeDTO fsDTO
		 * = new FormulationStakeDTO(betCodeRecipe);
		 * raceCardService.constructBetRecipeSession(fsDTO, betRecipeSession); }
		 * if(storedResponses.containsKey(pk)) { storedResponses.remove(pk); }
		 * storedResponses.put(pk, betRecipeSession);
		 */
	}

	private void buildFromDB(Long racePk, RaceRef raceRef) {
		SessionDTO betRecipeSession = new SessionDTO(raceRef.getEventDate());
		List<AuthorisedPartner> authorisedPartners = partnerService.getAuthorisedPartnerByRace(racePk);
		if (authorisedPartners != null && !authorisedPartners.isEmpty()) {
			for (AuthorisedPartner authorisedPartner : authorisedPartners) {
				for (FormulationStake formulationStake : authorisedPartner.getFormulationStakes()) {
					FormulationStakeDTO fsDTO = new FormulationStakeDTO(formulationStake);
					raceCardService.constructBetRecipeSession(fsDTO, betRecipeSession);
				}
			}
		}
		if (storedResponses.containsKey(racePk)) {
			storedResponses.remove(racePk);
		}
		storedResponses.put(racePk, betRecipeSession);
	}

	private void getRecipeFromSession(Long racePk, RaceRef raceRef) {
		SessionDTO betRecipeSession = new SessionDTO(raceRef.getEventDate());
		// Find event from session
		EventDTO event = this.raceCardService.getRaceCard().getEvents().stream()
				.filter(e -> e.getId() == raceRef.getEventId()).findFirst().orElse(null);
		
		if(event !=null) {
		// find race from session
		RaceDTO race = event.getRaces().stream().filter(r -> r.getPk().equals(racePk)).findFirst().orElse(null);
		
		if(race != null)
		{
			List<AuthorisedPartnerDTO> authorisedPartners = race.getAuthorizedPartners();
			if (authorisedPartners != null && !authorisedPartners.isEmpty()) {
				for (AuthorisedPartnerDTO authorisedPartner : authorisedPartners) {
					for (FormulationStakeDTO formulationStake : authorisedPartner.getFormulationStakes()) {
						raceCardService.constructBetRecipeSession(formulationStake, betRecipeSession);
					}
				}
			}
			
			if (storedResponses.containsKey(racePk)) {
				storedResponses.remove(racePk);
			}
			
			
			storedResponses.put(racePk, betRecipeSession);
			}
		}
	}
}

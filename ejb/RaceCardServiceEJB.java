package com.carrus.statsca.ejb;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.MockSessionService;
import com.carrus.statsca.RaceCardService;
import com.carrus.statsca.admin.StoreAdmin;
import com.carrus.statsca.bethistory.RemarkableRaceHistory;
import com.carrus.statsca.dto.AuthorisedFormulationDTO;
import com.carrus.statsca.dto.AuthorisedPartnerDTO;
import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.FormulationStakeDTO;
import com.carrus.statsca.dto.PartnerDTO;
import com.carrus.statsca.dto.RaceDTO;
import com.carrus.statsca.dto.RaceRecipeDTO;
import com.carrus.statsca.dto.RaceRefDTO;
import com.carrus.statsca.dto.RegulatoryBetDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.carrus.statsca.event.RaceRecipeEvt;
import com.carrus.statsca.event.UpdatedRacesRecipesEvt;
import com.carrus.statsca.exceptions.RecipeException;
import com.carrus.statsca.utils.StatsCaUtils;
import com.pmc.club.MutuelService;
import com.pmc.club.entity.AuthorisedFormulation;
import com.pmc.club.entity.Event;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.RaceTrack;
import com.pmc.club.entity.RemarkableRace;
import com.pmc.club.entity.RemarkableRaceRecipe;
import com.pmc.club.entity.Session;
import com.pmc.club.entity.partner.AuthorisedPartner;
import com.pmc.club.entity.partner.Partner;
import com.pmc.club.entity.recipe.BetCodeRecipe;
import com.pmc.club.event.BetChange;
import com.pmc.club.event.EventChange;
import com.pmc.club.event.RaceChange;
import com.pmc.club.references.RaceRef;
import com.pmc.club.service.AuthorisedFormulationService;
import com.pmc.club.service.EventService;
import com.pmc.club.service.RaceService;
import com.pmc.club.service.RaceTrackService;
import com.pmc.star3000.service.PartnerService;

@Singleton
@Startup
public class RaceCardServiceEJB implements RaceCardService {
	private static final int NB_PERIODS_FETCHED = 8;

	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	
	private static final String TIMER_GRANDPRIZE_ID = "timer-grandprize";

	/** Logger de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(RaceCardServiceEJB.class);

	@Resource
	private TimerService timerService;

	@Inject
	private EventService eventService;

	@Inject
	private MutuelService mutuelService;

	@Inject
	private RaceService raceService;

	@Inject
	private AuthorisedFormulationService authorisedFormulationService;

	@Inject
	private PartnerService partnerService;

//	@Inject
//	private RecipeService recipeService;

	@Inject
	private MockSessionService mockService;

	private SessionDTO currentRaceCard;
	
	private SessionDTO yesterdayRaceCard;
	
	private SessionDTO pastRaceCard;

	private SessionDTO currentBetRecipeSession;

	private Map<String, SessionDTO> preSessionBetRecipeSession = new HashMap<>();
	
	private LocalDate firstSessionDate;
	
	@Inject
	private RaceTrackService raceTrackService;

	private List<RemarkableRace> grandPrizes = new ArrayList<>();
	

	@PostConstruct
	private void loadLastRaceCard() {
		LOGGER.info("Loading last loaded session...");
		LocalDate date = LocalDate.now();
		if (mutuelService.getCurrentSessionDate() != null) {
			date = mutuelService.getCurrentSessionDate();
		} else {
			date = date.minusDays(1);
		}

		loadPastRaceCard(date);
//		// Définition du timer pour l'historisation des enjeux de grands prix par heure
//		if (!timerService.getTimers().isEmpty()) {
//			this.stopTimer();
//		}
	}

//	@Timeout
//	public void recordingGrandPrizeRecipe(Timer timer) {
//		if (this.grandPrizes != null && !this.grandPrizes.isEmpty() && this.currentRaceCard != null && LocalDate.now().equals(this.currentRaceCard.getSessionDate())) {
//
//			for (RemarkableRace grandPrize : grandPrizes) {
//				EventDTO eventDTO = this.getEventByRaceTrack(grandPrize.getGrandPrize().getRaceTrack().getPk(), this.currentRaceCard.getEvents());
//				if (eventDTO != null) {
//					LOGGER.info("Recording grand prize recipes");
//					injectEventRecipes(grandPrize, eventDTO);
//				}
//			}
//
//		}
//		else
//		{
//			this.stopTimer();
//			LOGGER.info("There is no grand prize for today, precautionably stop timer");
//		}
//
//	}

//	private void injectEventRecipes(RemarkableRace grandPrize, EventDTO eventDTO) {
//		Map<Integer, Map<Integer, RemarkableRaceRecipe>> entriesMappedByPartnerIdAndBetId = new HashMap<>();
//
//		for (RaceDTO race : eventDTO.getRaces()) {
//			if (race.getAuthorizedPartners() == null) {
//				continue;
//			}
//
//			for (AuthorisedPartnerDTO formulation : race.getAuthorizedPartners()) {
//				if (formulation.getFormulationStakes() == null) {
//					continue;
//				}
//
//				if (!entriesMappedByPartnerIdAndBetId.containsKey(formulation.getPartner().getPartnerId())) {
//					entriesMappedByPartnerIdAndBetId.put(formulation.getPartner().getPartnerId(), new HashMap<>());
//				}
//
//				Map<Integer, RemarkableRaceRecipe> entriesMappedByBetId = entriesMappedByPartnerIdAndBetId
//						.get(formulation.getPartner().getPartnerId());
//
//				for (FormulationStakeDTO betRecipe : formulation.getFormulationStakes()) {
//					addBetStakeOnMap(grandPrize, eventDTO, formulation, entriesMappedByBetId, betRecipe);
//				}
//			}
//		}
//
//		injectHistoryFromMap(entriesMappedByPartnerIdAndBetId);
//	}

//	/**
//	 * @param grandPrize
//	 * @param eventDTO
//	 * @param formulation
//	 * @param entriesMappedByBetId
//	 * @param betRecipe
//	 */
//	private void addBetStakeOnMap(RemarkableRace grandPrize, EventDTO eventDTO, AuthorisedPartnerDTO formulation, Map<Integer, RemarkableRaceRecipe> entriesMappedByBetId, FormulationStakeDTO betRecipe) {
//		if (entriesMappedByBetId.containsKey(betRecipe.getBetCodeRef())) {
//			RemarkableRaceRecipe targetRecipe = entriesMappedByBetId.get(betRecipe.getBetCodeRef());
//			targetRecipe.setStake(targetRecipe.getStake().add(betRecipe.getStake()));
//		} else {
//			putGrandPrizeRecipeOnMap(grandPrize, eventDTO, formulation, entriesMappedByBetId, betRecipe);
//		}
//	}

//	/**
//	 * @param entriesMappedByPartnerIdAndBetId
//	 */
//	private void injectHistoryFromMap(Map<Integer, Map<Integer, RemarkableRaceRecipe>> entriesMappedByPartnerIdAndBetId) {
//		for (Map.Entry<Integer, Map<Integer, RemarkableRaceRecipe>> entriesMappedByBetId : entriesMappedByPartnerIdAndBetId
//				.entrySet()) {
//			for (Map.Entry<Integer, RemarkableRaceRecipe> entry : entriesMappedByBetId.getValue().entrySet()) {
//				RemarkableRaceRecipe grandPrizeRecipe = entry.getValue();
//
//				ZonedDateTime recordTime = ZonedDateTime.now();
//				grandPrizeRecipe.setDateRecipe(recordTime);
//
//				recipeService.injectRemarkableRaceRecipe(grandPrizeRecipe);
//			}
//		}
//	}

//	/**
//	 * @param remRace
//	 * @param eventDTO
//	 * @param formulation
//	 * @param entriesMappedByBetId
//	 * @param betRecipe
//	 */
//	private void putGrandPrizeRecipeOnMap(RemarkableRace remRace, EventDTO eventDTO, AuthorisedPartnerDTO formulation, Map<Integer, RemarkableRaceRecipe> entriesMappedByBetId, FormulationStakeDTO betRecipe) {
//		RemarkableRaceRecipe grandPrizeRecipe = new RemarkableRaceRecipe();
//
//		grandPrizeRecipe.setDateSession(this.currentRaceCard.getSessionDate());
//		//grandPrizeRecipe.setGrandPrizePk(remRace.getGrandPrize().getPk());
//		grandPrizeRecipe.setRemarkable(remRace);
//		grandPrizeRecipe.setId(eventDTO.getId());
//		grandPrizeRecipe.setNumber(0);
//		grandPrizeRecipe.setBetType(betRecipe.getBetCodeRef());
//		grandPrizeRecipe.setStake(betRecipe.getStake());
//		grandPrizeRecipe.setPartnerId(formulation.getPartner().getPartnerId());
//
//		entriesMappedByBetId.put(betRecipe.getBetCodeRef(), grandPrizeRecipe);
//	}

	@Override
	public void setCurrentRaceCard(SessionDTO session) {
		currentRaceCard = session;
	}

	@Override
	public SessionDTO getRaceCard() {
		return currentRaceCard;
	}

	@Override
	public String getLightRaceCard() {
		LOGGER.debug("getLightRaceCard() : currentRaceCard = [{}]",
				currentRaceCard == null ? "NULL" : String.valueOf(currentRaceCard.getSessionId()));
		if (currentRaceCard != null) {
			
			//!!!!!!!!!!!!!!! FOR TEST !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			SessionDTO session = getRaceCard(LocalDate.of(2023, 3, 16));
			session.setSessionDate(LocalDate.now());
			return constructLightSessionEvt(session, false);
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!				
			//return constructLightSessionEvt(currentRaceCard, false);
		}
		return null;
	}

	@Override
	public SessionDTO getRaceCard(LocalDate date) {
		if (date.equals(mutuelService.getCurrentSessionDate())) {
			return currentRaceCard;
		} else {
			
			if(mutuelService.getCurrentSessionDate() != null && date.equals(mutuelService.getCurrentSessionDate().minusDays(1)))
			{
				return retrieveYesterdayRaceCard(date);
			}
			else {
				return retrievePastRaceCard(date);
			}
		}
	}

	public void clearAllSelectRaceCard() {
		this.yesterdayRaceCard = null;
		this.pastRaceCard = null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void loadCurrentRaceCard(LocalDate sessionDate) {
		
		//Chargement de la première date de session
		try {
			firstSessionDate = eventService.getFirstSessionDate();
		LOGGER.info("First session date loaded : {}",firstSessionDate);
		} catch (Exception e) {
			LOGGER.error("error while loading first session date : {}", e.getMessage());
		}
		
		LocalDate currentSessionDate = mutuelService.getCurrentSessionDate();
		if (currentSessionDate == null) {
			LOGGER.info("Current session date set to today... {}", currentSessionDate);
			currentSessionDate = LocalDate.now();
		}
		if (sessionDate == null || (currentSessionDate != null && currentSessionDate.equals(sessionDate))) {
			LOGGER.info("Loading current session, {}", currentSessionDate);
			loadSelectedRaceCard(currentSessionDate, true);
		} else {
			currentSessionDate = sessionDate;
			LOGGER.info("Loading session at selected date, {}", currentSessionDate);
			loadSelectedRaceCard(currentSessionDate, false);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void loadSelectedRaceCard(LocalDate date, boolean current) {

		// JKE mise en place de la session MOCK pour la DEV
		String active = System.getProperty("carrus.statsca.services.mock.active");
		if (active != null && Boolean.parseBoolean(active)) {
			processMockSession();
		} else {
			processRealSession(date, current);
		}
	}

	private SessionDTO retrieveYesterdayRaceCard(LocalDate date) {
		setYesterdayRaceCard(date);
		LOGGER.info("session from yesterday retrieved: {}" , date);
		return this.yesterdayRaceCard;
	}
	
	private SessionDTO retrievePastRaceCard(LocalDate date) {
		setPastRaceCard(date);
		LOGGER.info("past session date {}, session from the past retrieved: {}" , date, this.pastRaceCard != null?this.pastRaceCard.getSessionDate():"EMPTY DATE");
		return this.pastRaceCard;
	}



	/**
	 * @param date
	 * @param current
	 */
	private void processRealSession(LocalDate date, boolean current) {
		if (current) {
			Session session = eventService.getSessionByDate(date);
			if (session != null) {
				SessionDTO sessionDTO = new SessionDTO(session);
				this.loadGrandPrize(sessionDTO);
				setCurrentRaceCard(sessionDTO);
			}
		} else {
			if (mutuelService.getCurrentSessionDate() != null) {
				if (date.equals(mutuelService.getCurrentSessionDate().minusDays(1))) {
					setYesterdayRaceCard(date);
					LOGGER.info("process yesterday session: {}", date);
				} else {
					setPastRaceCard(date);
					LOGGER.info("process session from the past: {}", date);
				}
			} else {
				LOGGER.error("Error process session : the current date is null");
			}
		}
	}

	/**
	 * @param date
	 */
	private void setPastRaceCard(LocalDate date) {
		if(this.pastRaceCard==null || !this.pastRaceCard.getSessionDate().equals(date)) {
			setPastRaceCardFromDate(date);
		}
	}

	private void setPastRaceCardFromDate(LocalDate date) {
//		LocalDateTime start = LocalDateTime.now();
		Session session = eventService.getSessionByDate(date);
//		LocalDateTime end = LocalDateTime.now();
//		Long diff = Duration.between(start, end).getSeconds();
//		LOGGER.info("setPastRaceCardFromDate : Entities session retrieving processed in {} s",diff);
		if(session != null) {
//			LocalDateTime start1 = LocalDateTime.now();
			this.pastRaceCard = new SessionDTO(session);
//			LocalDateTime end1 = LocalDateTime.now();
//			Long diff1 = Duration.between(start1, end1).getSeconds();
//			LOGGER.info("setPastRaceCardFromDate : Session building processed in {} s",diff1);
		} else {
			//void the stored past racecard
			this.pastRaceCard = null;
		}
	}

	/**
	 * @param date
	 */
	private void setYesterdayRaceCard(LocalDate date) {
		if(this.yesterdayRaceCard == null || !this.yesterdayRaceCard.getSessionDate().equals(date) ) {
			setYesterdayRaceCardFromDate(date);
		}
	}

	private void setYesterdayRaceCardFromDate(LocalDate date) {
//		LocalDateTime start = LocalDateTime.now();
		Session session = eventService.getRefreshedSessionByDate(date);
//		LocalDateTime end = LocalDateTime.now();
//		Long diff = Duration.between(start, end).getSeconds();
//		LOGGER.info("setYesterdayRaceCardFromDate : Entities session retrieving processed in {} s",diff);
		if(session != null) {
//			LocalDateTime start1 = LocalDateTime.now();
			this.yesterdayRaceCard = new SessionDTO(session);
//			LocalDateTime end1 = LocalDateTime.now();
//			Long diff1 = Duration.between(start1, end1).getSeconds();
//			LOGGER.info("setYesterdayRaceCardFromDate : Session building processed in {} s",diff1);
		}
	}

	/**
	 * 
	 */
	private void processMockSession() {
		LOGGER.info("Mocking de la session active ( Prévue seulement pour l'environnement DEV)");
		LocalDate sessionDate = LocalDate.now();
		LOGGER.info("Pas de date souhaitée pour la session mockée");
		SessionDTO mockSession = mockService.getMockSessionFromScratch(sessionDate, " mocked ", null, null, null);
		setCurrentRaceCard(mockSession);

	}

	private void loadGrandPrize(SessionDTO session) {
		LocalDate sessionDate = session.getSessionDate();
		this.grandPrizes = raceService.findGrandPrizeByDate(sessionDate);
		// clear Timer method
		if (timerService.getTimers() != null && !timerService.getTimers().isEmpty()) {
			this.stopTimer();
		}
		if (this.grandPrizes != null && !this.grandPrizes.isEmpty()) {
			LOGGER.debug("Grand prix loaded ....... {}", grandPrizes.size());
			session.setRemarkableDay(true);
			// only one remarkableRace per day
			for (RemarkableRace remRace : this.grandPrizes) {
				// find the event based on racetrack
				EventDTO event = this.getEventByRaceTrack(remRace.getGrandPrize().getRaceTrack().getPk(), session.getEvents());
				if (event != null) {
					event.setRemarkableEvent(true);
					LOGGER.debug("Grand prix ....... Set remarkable flag to true");
				}
			}
//			// async
//			startHistoricalTimer();
		}
	}

//	@Asynchronous
//	private void startHistoricalTimer() {
//			LOGGER.debug("START historical timer set to true");
//			TimerConfig config = new TimerConfig();
//			config.setInfo(TIMER_GRANDPRIZE_ID);
//			config.setPersistent(false);
//			//every 5 minute for test
//			ScheduleExpression scheduleExpression = new ScheduleExpression().hour("*").minute("0").second("0");
//			timerService.createCalendarTimer(scheduleExpression, config);
//	}
	
	@Override
	public void stopTimer() {
		for (Timer timer : timerService.getTimers()) {
			if (timer.getInfo().equals(TIMER_GRANDPRIZE_ID)) {
				LOGGER.info("end of hourly grand prize history");
				timer.cancel();
			}
		}
	}

	private Session loadPastRaceCard(LocalDate date) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Trying to load the racecard  from {}", date);
		}
		Session session = eventService.getSessionByDate(date);
		if (session != null) {
			SessionDTO sessionDTO = new SessionDTO(session);
			setCurrentRaceCard(sessionDTO);
		} else {
			if (LOGGER.isWarnEnabled())
				LOGGER.warn("the racecard can't be loaded because the session from {} doesn't exist", date);
		}
		return session;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteBet(BetChange betChangeEvent) {
		LOGGER.info("deleting bet {} to R{}C{} on the racecard", betChangeEvent.getCode(),
				betChangeEvent.getRaceRef().getEventId(), betChangeEvent.getRaceRef().getRaceNumber());
		LocalDate sessionDate = betChangeEvent.getRaceRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {
			Integer eventId = betChangeEvent.getRaceRef().getEventId();
			Integer raceId = betChangeEvent.getRaceRef().getRaceNumber();
			Integer betId = betChangeEvent.getCode();

			EventDTO ev = raceCard.getEvents().stream().filter(e -> e.getId() == eventId).findFirst().orElse(null);
			if (ev != null) {
				RaceDTO race = ev.getRaces().stream().filter(r -> r.getNumber() == raceId).findFirst().orElse(null);
				if (race != null) {
					race.getFormulations().removeIf(formulation -> formulation.getBetCodeRef() == betId);
				}
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addBet(BetChange betChangeEvent) {
		LOGGER.info("adding bet {} to R{}C{} on the racecard", betChangeEvent.getCode(),
				betChangeEvent.getRaceRef().getEventId(), betChangeEvent.getRaceRef().getRaceNumber());
		LocalDate sessionDate = betChangeEvent.getRaceRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {
			Integer eventId = betChangeEvent.getRaceRef().getEventId();
			Integer raceId = betChangeEvent.getRaceRef().getRaceNumber();

			EventDTO ev = raceCard.getEvents().stream().filter(e -> e.getId() == eventId).findFirst().orElse(null);

			if (ev != null) {
				RaceDTO race = ev.getRaces().stream().filter(r -> r.getNumber() == raceId).findFirst().orElse(null);
				if (race != null) {
					// Ajout du pari
					AuthorisedFormulation afEntity = authorisedFormulationService
							.getByRef(betChangeEvent.getFormulationRef());

					if (afEntity != null) {
						AuthorisedFormulationDTO afDTO = new AuthorisedFormulationDTO(afEntity);
						race.getFormulations().add(afDTO);
					}
				}
			}
		}
	}

	/**
	 * @param sessionDate
	 * @return
	 */
	private SessionDTO retrieveRacecard(LocalDate sessionDate) {
		if (sessionDate.equals(mutuelService.getCurrentSessionDate().minusDays(1))) {
			return this.yesterdayRaceCard != null ? this.yesterdayRaceCard : this.retrieveYesterdayRaceCard(sessionDate);
		} else {
			return this.pastRaceCard != null ? this.pastRaceCard : this.retrievePastRaceCard(sessionDate);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteRace(RaceChange raceChangeEvent) {
		LOGGER.info("deleting race R{}C{} on the racecard", raceChangeEvent.getRaceRef().getEventId(),
				raceChangeEvent.getRaceRef().getRaceNumber());
		LocalDate sessionDate = raceChangeEvent.getRaceRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {
			Integer eventId = raceChangeEvent.getRaceRef().getEventId();
			Integer raceId = raceChangeEvent.getRaceRef().getRaceNumber();

			// Obtention des indexs
			List<EventDTO> events = raceCard.getEvents();
			int indexEvent = getIndexEvents(eventId, events);

			// Si la réunion n'existe pas encore, on l'ajoute
			if (indexEvent == -1) {
				Event eventEntity = eventService.getByRef(raceChangeEvent.getRaceRef());
				if (eventEntity != null) {
					EventDTO eventDTO = new EventDTO(eventEntity);
					raceCard.getEvents().add(eventDTO);
					indexEvent = getIndexEvents(eventId, events);
				}
			}

			if (indexEvent != -1) {
				// Suppression de la course dans la racecard sauvegardé en mémoire
				if (!raceCard.getEvents().get(indexEvent).getRaces().isEmpty()) {
					raceCard.getEvents().get(indexEvent).getRaces().removeIf(race -> race.getNumber() == raceId);
				}

			} else {
				LOGGER.error("the event R{} does not exist in the model, deleting a race is impossible ", eventId);
			}
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addRace(RaceChange raceChangeEvent) {
		LOGGER.info("adding race R{}C{} on the racecard", raceChangeEvent.getRaceRef().getEventId(),
				raceChangeEvent.getRaceRef().getRaceNumber());
		LocalDate sessionDate = raceChangeEvent.getRaceRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {
			Integer eventId = raceChangeEvent.getRaceRef().getEventId();

			// Obtention des indexs
			List<EventDTO> events = raceCard.getEvents();
			int indexEvent = getIndexEvents(eventId, events);

			// Si la réunion n'existe pas encore, on l'ajoute
			if (indexEvent == -1) {
				Event eventEntity = eventService.getByRef(raceChangeEvent.getRaceRef());
				if (eventEntity != null) {
					EventDTO eventDTO = new EventDTO(eventEntity);
					raceCard.getEvents().add(eventDTO);
					indexEvent = getIndexEvents(eventId, events);
				}
			}

			// Ajout de la course
			if (indexEvent != -1) {
				Race raceEntity = raceService.getByRef(raceChangeEvent.getRaceRef());
				if (raceEntity != null) {
					RaceDTO raceDTO = new RaceDTO(raceEntity);
					raceCard.getEvents().get(indexEvent).getRaces().add(raceDTO);
				}
			} else {
				LOGGER.error("the event R{} is not found on DB, impossible to add race ", eventId);
			}
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void editRace(RaceChange raceChangeEvent) {
		LOGGER.info("editing race R{}C{} on the racecard", raceChangeEvent.getRaceRef().getEventId(),
				raceChangeEvent.getRaceRef().getRaceNumber());
		LocalDate sessionDate = raceChangeEvent.getRaceRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}

		if (raceCard != null) {
			Integer eventId = raceChangeEvent.getRaceRef().getEventId();
			Integer raceId = raceChangeEvent.getRaceRef().getRaceNumber();

			// Obtention des indexs
			List<EventDTO> events = raceCard.getEvents();
			int indexEvent = getIndexEvents(eventId, events);

			// Si la réunion n'existe pas encore, on l'ajoute
			if (indexEvent == -1) {
				Event eventEntity = eventService.getByRef(raceChangeEvent.getRaceRef());
				if (eventEntity != null) {
					EventDTO eventDTO = new EventDTO(eventEntity);
					raceCard.getEvents().add(eventDTO);
					indexEvent = getIndexEvents(eventId, events);
				}
			}

			// Modification de la course
			if (indexEvent != -1) {
				Race raceEntity = raceService.getByRef(raceChangeEvent.getRaceRef());
				if (raceEntity != null) {
					RaceDTO raceDTO = new RaceDTO(raceEntity);
					int indexRace = getIndexRace(raceId, raceCard.getEvents().get(indexEvent).getRaces());
					try {

						BeanUtils.copyProperties(raceCard.getEvents().get(indexEvent).getRaces().get(indexRace),
								raceDTO);
					} catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
						LOGGER.error("error while editing race : {}", e.getMessage());
					}
				}

			} else {
				LOGGER.error("the event R{} does not exist in the model, editing a race is impossible ", eventId);
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteEvent(EventChange eventChangeEvent) {
		LocalDate sessionDate = eventChangeEvent.getEventRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {
			Integer eventId = eventChangeEvent.getEventRef().getEventId();
			// Suppression de la réunion dans la racecard sauvegardé en mémoire
			raceCard.getEvents().removeIf(event -> event.getId() == eventId);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addEvent(EventChange eventChangeEvent) {
		LocalDate sessionDate = eventChangeEvent.getEventRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {

			// Ajout de la réunion
			Event eventEntity = eventService.getByRef(eventChangeEvent.getEventRef());
			if (eventEntity != null) {
				EventDTO eventDTO = new EventDTO(eventEntity);
				raceCard.getEvents().add(eventDTO);
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void editEvent(EventChange eventChangeEvent) {
		LocalDate sessionDate = eventChangeEvent.getEventRef().getEventDate();
		SessionDTO raceCard = this.getRaceCard();
		if (raceCard != null && !raceCard.getSessionDate().equals(sessionDate)) {
			raceCard = retrieveRacecard(sessionDate);
		}
		if (raceCard != null) {
			Integer eventId = eventChangeEvent.getEventRef().getEventId();
			Event eventEntity = eventService.getByRef(eventChangeEvent.getEventRef());
			if (eventEntity != null) {
				EventDTO eventDTO = new EventDTO(eventEntity);
				int indexEvent = getIndexEvents(eventId, this.getRaceCard().getEvents());
				try {
					BeanUtils.copyProperties(raceCard.getEvents().get(indexEvent), eventDTO);
				} catch (IllegalAccessException | InvocationTargetException e) {
					LOGGER.error("error while editing event : {}", e.getMessage());
				}
			}
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateRaceRecipe(UpdatedRacesRecipesEvt urre) throws RecipeException {
		for (RaceRecipeEvt event : urre.getRaceRecipes()) {
			// use event instead of creating a new DTO
			RaceRecipeDTO recipeDTO = new RaceRecipeDTO(event);
			LocalDate sessionDate = recipeDTO.getRaceRef().getEventDate();
			if (sessionDate.equals(mutuelService.getCurrentSessionDate())) {
				if (currentRaceCard == null) {
					currentRaceCard = new SessionDTO(sessionDate);
				}
				updateRaceRecipeSession(recipeDTO, currentRaceCard);

			} else {
				updateRaceRecipeSession(recipeDTO, retrieveRacecard(sessionDate));
			}

		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Race updateBetRecipe(List<BetCodeRecipe> betCodeRecipes, RaceRef raceRef) {
		List<Integer> authorisedPartnerToInit = new ArrayList<>();
		Race updatedRace = null;
		for (BetCodeRecipe betCoderecipe : betCodeRecipes) {
			FormulationStakeDTO fsDTO = new FormulationStakeDTO(betCoderecipe);
			LocalDate sessionDate = fsDTO.getRaceRef().getEventDate();
			if (sessionDate.equals(mutuelService.getCurrentSessionDate())) {
				if (currentRaceCard == null) {
					currentRaceCard = new SessionDTO(sessionDate);
				}
				updatedRace = updateBetRecipeSession(fsDTO, currentRaceCard, authorisedPartnerToInit);
			} else {
				SessionDTO raceCard = retrieveRacecard(sessionDate);
				updatedRace = updateBetRecipeSession(fsDTO, raceCard, authorisedPartnerToInit);
			}

		}
		return updatedRace;
	}
	
	@Override
	public String getLightSessionRest(SessionDTO session) {
		if(session!=null) {
			return this.constructLightSessionEvt(session, true);
		} 
		return StringUtils.EMPTY;
	}

	private String constructLightSessionEvt(SessionDTO session, boolean formulation) {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("sessionDate", session.getSessionDate().toString());
			if (session.getEvents() != null && !session.getEvents().isEmpty()) {
				JsonArrayBuilder eventsBuilder = Json.createArrayBuilder();
				for (EventDTO event : session.getEvents()) {
					eventsBuilder.add(constructLightEventEvt(event, formulation));
				}
				builder.add("events", eventsBuilder);
			}
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}

	private JsonObjectBuilder constructLightEventEvt(EventDTO event, boolean formulation) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("pk", event.getPk());
		builder.add("id", event.getId());
		if (event.getRaces() != null && !event.getRaces().isEmpty()) {
			JsonArrayBuilder eventsBuilder = Json.createArrayBuilder();
			for (RaceDTO race : event.getRaces()) {
				eventsBuilder.add(constructLightRaceEvt(race, formulation));
			}
			builder.add("races", eventsBuilder);
		}
		return builder;

	}

	private JsonObjectBuilder constructLightRaceEvt(RaceDTO race, boolean formulation) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("pk", race.getPk());
		builder.add("number", race.getNumber());
		builder.add("state", race.getState().toString());
		builder.add("expectedStart", race.getExpectedStart().toString());
		if (race.getAuthorizedPartners() != null && !race.getAuthorizedPartners().isEmpty()) {
			JsonArrayBuilder eventsBuilder = Json.createArrayBuilder();
			for (AuthorisedPartnerDTO ap : race.getAuthorizedPartners()) {
				eventsBuilder.add(constructLightAuthorisedPartnerEvt(ap));
			}
			builder.add("authorizedPartners", eventsBuilder);
		}
		if(formulation) {
			if (race.getFormulations() != null && !race.getFormulations().isEmpty()) {
				JsonArrayBuilder eventsBuilder = Json.createArrayBuilder();
				for (AuthorisedFormulationDTO ap : race.getFormulations()) {
					eventsBuilder.add(constructLightAuthorisedFormulationEvt(ap));
				}
				builder.add("formulations", eventsBuilder);
			}
		}
		return builder;
	}

	private JsonObjectBuilder constructLightAuthorisedFormulationEvt(AuthorisedFormulationDTO af) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("betCodeRef", af.getBetCodeRef());
		if(af.getBetType() != null) {
			builder.add("betType", constructLightBetType(af.getBetType()));
		}
		
		return builder;
	}

	private JsonObjectBuilder constructLightBetType(RegulatoryBetDTO betType) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("name", betType.getName()!=null?betType.getName():"");
		builder.add("longName", betType.getLongName()!=null?betType.getLongName():"");
		return builder;
	}

	private JsonObjectBuilder constructLightAuthorisedPartnerEvt(AuthorisedPartnerDTO ap) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (ap.getPartner() != null) {
			builder.add("partner", constructLightPartnerEvt(ap.getPartner()));
		}
		if (ap.getFormulationStakes() != null && !ap.getFormulationStakes().isEmpty()) {
			JsonArrayBuilder eventsBuilder = Json.createArrayBuilder();
			for (FormulationStakeDTO fs : ap.getFormulationStakes()) {
				eventsBuilder.add(constructFormulationStake(fs));
			}
			builder.add("formulationStakes", eventsBuilder);
		}
		builder.add("stake", ap.getStake());

		return builder;
	}

	private JsonObjectBuilder constructFormulationStake(FormulationStakeDTO fs) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("stake", fs.getStake());
		builder.add("betCodeRef", fs.getBetCodeRef());
		return builder;
	}

	private JsonObjectBuilder constructLightPartnerEvt(PartnerDTO partner) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("partnerId", partner.getPartnerId());
		builder.add("name", partner.getName());
		builder.add("shortName", partner.getShortName());
		builder.add("country", partner.getCountry());
		return builder;
	}

	private int getIndexEvents(Integer eventId, List<EventDTO> events) {
		if (events != null) {
			OptionalInt optionalIntEvent = IntStream.range(0, events.size())
					.filter(i -> events.get(i).getId() == eventId).findFirst();
			return optionalIntEvent.orElse(-1);
		}
		return -1;
	}

	private int getIndexRace(Integer raceId, List<RaceDTO> races) {
		if (races != null) {
			OptionalInt optionalIntRace = IntStream.range(0, races.size())
					.filter(i -> races.get(i).getNumber() == raceId).findFirst();

			return optionalIntRace.orElse(-1);
		}
		return -1;
	}

//	private RaceDTO getIndexedRace(Integer raceId, List<RaceDTO> races) {
//		if (races != null) {
//			return races.stream().filter(r -> r.getNumber() == raceId).findFirst().orElse(null);
//		}
//		return null;
//	}

	private int getIndexPartners(int contractor, List<AuthorisedPartnerDTO> partners) {
		if (partners != null) {
			OptionalInt optionalIntPartner = IntStream.range(0, partners.size())
					.filter(i -> partners.get(i).getPartner() != null
							&& partners.get(i).getPartner().getPartnerId() == contractor)
					.findFirst();

			return optionalIntPartner.orElse(-1);
		}
		return -1;
	}

	private int getIndexBets(int betCodeRef, List<FormulationStakeDTO> events) {
		if (events != null) {
			OptionalInt optionalIntEvent = IntStream.range(0, events.size())
					.filter(i -> events.get(i).getBetCodeRef() == betCodeRef).findFirst();

			return optionalIntEvent.isPresent() ? optionalIntEvent.getAsInt() : -1;
		}
		return -1;
	}

//	private EventDTO getIndexedEvent(Integer eventId, List<EventDTO> events) {
//		if (events != null) {
//			return events.stream().filter(e -> e.getId() == eventId).findFirst().orElse(null);
//		}
//		return null;
//	}
	
	private EventDTO getEventByRaceTrack(long raceTrackPk, List<EventDTO> events) {
		RaceTrack raceTrack = raceTrackService.getByPk(raceTrackPk);
		if (events != null && raceTrack != null) {
			return events.stream().filter(e -> e.getRacetrack().getPk().equals(raceTrackPk)).findFirst().orElse(null);
		}
		return null;
	}

	/**
	 * mise à jour des enjeux globaux dans une session
	 * 
	 * @param recipe
	 * @param raceRecipeSession
	 */
	private void updateRaceRecipeSession(RaceRecipeDTO recipe, SessionDTO raceRecipeSession) throws RecipeException {

		if (raceRecipeSession != null) {
			RaceRefDTO keyRaceRef = recipe.getRaceRef();
			int contractor = recipe.getContractor();
			double cashAmount = recipe.getCashAmount();
			int indexEvent = isEventOnSession(keyRaceRef, raceRecipeSession);

			if (indexEvent == -1) {

				throw new RecipeException("Targeted event not found in the current session");
			} else {
				int indexRace = isRaceOnEvent(keyRaceRef, raceRecipeSession, indexEvent);
				if (indexRace == -1) {

					throw new RecipeException("Targeted Race not found in the current session");

				} else {

					int indexPartner = isPartnerOnRace(contractor, raceRecipeSession, indexEvent, indexRace);

					if (indexPartner == -1) {
						addPartnerRecipe(raceRecipeSession, contractor, cashAmount, indexEvent, indexRace);
					} else {
						modifyPartnerRecipe(raceRecipeSession, cashAmount, indexEvent, indexRace, indexPartner);

					}

				}
			}

		}

	}

	/**
	 * mise à jour des enjeux par pari dans une session
	 * 
	 * @param recipe                  enjeu par pari à inclure dans la session pour
	 *                                une course donnée
	 * @param raceRecipeSession       session contenant les enjeux par pari
	 * @param authorisedPartnerToInit liste des codes de partenaires dont il faut
	 *                                initialiser les enjeux globaux dans la course
	 * 
	 * 
	 */
	private Race updateBetRecipeSession(FormulationStakeDTO recipe, SessionDTO raceRecipeSession,
			List<Integer> authorisedPartnerToInit) {

		if (raceRecipeSession != null) {
			RaceRefDTO keyRaceRef = recipe.getRaceRef();
			int contractor = recipe.getContractor();
			int betCodeRef = recipe.getBetCodeRef();
			int indexEvent = isEventOnSession(keyRaceRef, raceRecipeSession);
			Race race = raceService.getByExternalIds(keyRaceRef.getEventDate(), keyRaceRef.getEventId(),
					keyRaceRef.getRaceNumber());

			Partner partner = partnerService.getPartner(contractor);
			if (partner == null) {
				LOGGER.warn("WARNING the partner {} does not exist on the reference datas, the recipe cant be recorded",
						contractor);

			} else {

				if (indexEvent == -1) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("WARNING update session with bet recipes: the event {} is not on the session {}",
								keyRaceRef.getEventId(), keyRaceRef.getEventDate().format(formatter));
					}
					return null;
				} else {
					int indexRace = isRaceOnEvent(keyRaceRef, raceRecipeSession, indexEvent);
					if (indexRace == -1) {
						if (LOGGER.isWarnEnabled()) {
							LOGGER.warn("WARNING update session with bet recipes: the race {} is not on the event {}",
									keyRaceRef.getRaceNumber(), keyRaceRef.getEventId());
						}
						return null;
					} else {
						checkPartnerThenProcessRecipe(recipe, raceRecipeSession, authorisedPartnerToInit, keyRaceRef,
								contractor, betCodeRef, indexEvent, race, partner, indexRace);

					}
				}
			}
			return race;
		}
		return null;
	}

	/**
	 * @param recipe
	 * @param raceRecipeSession
	 * @param authorisedPartnerToInit
	 * @param keyRaceRef
	 * @param contractor
	 * @param betCodeRef
	 * @param indexEvent
	 * @param race
	 * @param partner
	 * @param indexRace
	 */
	private void checkPartnerThenProcessRecipe(FormulationStakeDTO recipe, SessionDTO raceRecipeSession,
			List<Integer> authorisedPartnerToInit, RaceRefDTO keyRaceRef, int contractor, int betCodeRef,
			int indexEvent, Race race, Partner partner, int indexRace) {
		int indexPartner = isPartnerOnRace(contractor, raceRecipeSession, indexEvent, indexRace);

		if (indexPartner == -1) {
			indexPartner = addAuthorisedPartnerOnSession(raceRecipeSession, authorisedPartnerToInit, race, partner,
					indexEvent, indexRace);
		}

		if (indexPartner == -1) {

			LOGGER.warn("WARNING update session with bet recipes: the authorised partner {} is not on the race R{}C{}",
					contractor, keyRaceRef.getEventId(), keyRaceRef.getRaceNumber());

		} else {
			putBetRecipe(recipe, raceRecipeSession, authorisedPartnerToInit, keyRaceRef, contractor, betCodeRef,
					indexEvent, race, partner, indexRace, indexPartner);

		}

	}

	/**
	 * @param recipe
	 * @param raceRecipeSession
	 * @param authorisedPartnerToInit
	 * @param keyRaceRef
	 * @param contractor
	 * @param betCodeRef
	 * @param indexEvent
	 * @param race
	 * @param partner
	 * @param indexRace
	 * @param indexPartner
	 */
	private void putBetRecipe(FormulationStakeDTO recipe, SessionDTO raceRecipeSession,
			List<Integer> authorisedPartnerToInit, RaceRefDTO keyRaceRef, int contractor, int betCodeRef,
			int indexEvent, Race race, Partner partner, int indexRace, int indexPartner) {
		if (!authorisedPartnerToInit.isEmpty() && authorisedPartnerToInit.contains(contractor)) {
			LOGGER.info("calculating stake on partner recipe to init : Att {} R{}C{} {}", contractor,
					keyRaceRef.getEventId(), keyRaceRef.getRaceNumber(), keyRaceRef.getEventDate());
			double newStake = calculatePartnerRecipe(raceRecipeSession, indexEvent, indexRace, indexPartner, recipe);
			mergeAuthorisedPartnerOnDB(race, partner, newStake);
		}

		int indexBet = isBetRefOnAuthorisedPartner(betCodeRef, raceRecipeSession, indexEvent, indexRace, indexPartner);
		if (indexBet == -1) {
			addBetRecipe(raceRecipeSession, recipe, indexEvent, indexRace, indexPartner);
		} else {
			modifyBetRecipe(raceRecipeSession, recipe, indexEvent, indexRace, indexPartner, indexBet);
		}

	}

	private int addAuthorisedPartnerOnSession(SessionDTO raceRecipeSession, List<Integer> authorisedPartnerToInit,
			Race race, Partner partner, int indexEvent, int indexRace) {

		int indexPartner;
		int partnerId = partner.getPartnerId();
		int id = race.getEvent().getId();
		int number = race.getNumber();
		LocalDate sessionDate = raceRecipeSession.getSessionDate();
		LOGGER.info("adding a new partner recipe on session: Att {} R{}C{} {}", partnerId, id, number, sessionDate);
		authorisedPartnerToInit.add(partnerId);
		addAuthorisedPartnerOnDB(race, partner);
		AuthorisedPartnerDTO apDTO = buildAuthorisedPartnerDTO(partner, 0);
		addPartnerRecipe(raceRecipeSession, apDTO, indexEvent, indexRace);
		indexPartner = isPartnerOnRace(partnerId, raceRecipeSession, indexEvent, indexRace);
		return indexPartner;
	}

	private void addAuthorisedPartnerOnDB(Race race, Partner partner) {
		AuthorisedPartner authorisedPartner = new AuthorisedPartner();
		authorisedPartner.setPartner(partner);
		authorisedPartner.setRace(race);
		authorisedPartner.setStake(BigDecimal.valueOf(0.0d));
		authorisedPartner.setUpdateDate(ZonedDateTime.now());
		partnerService.injectAuthorisedPartner(authorisedPartner);
	}

	private void mergeAuthorisedPartnerOnDB(Race race, Partner partner, double stake) {
		Long partnerPk = partner.getPk();
		Long racePk = race.getPk();
		List<AuthorisedPartner> partnersOnDB = partnerService.getAuthorisedPartner(partnerPk, racePk);
		if (partnersOnDB != null && !partnersOnDB.isEmpty()) {
			if (partnersOnDB.size() > 1) {
				LOGGER.warn(
						"warning: there is more than one authorised partner referring to partner key {} and race key {}",
						partnerPk, racePk);

			}
			AuthorisedPartner partnerOnDB = partnersOnDB.get(0);
			AuthorisedPartner authorisedPartner = new AuthorisedPartner();
			authorisedPartner.setPartner(partner);
			authorisedPartner.setRace(race);
			authorisedPartner.setStake(BigDecimal.valueOf(stake));
			authorisedPartner.setPk(partnerOnDB.getPk());
			authorisedPartner.setUpdateDate(ZonedDateTime.now());
			partnerService.mergeAuthorisedPartner(authorisedPartner);
		}
	}

	/**
	 * Construction d'une session simplifié listant les enjeux par paris en mémoire
	 * 
	 * @param recipe
	 * @param raceRecipeSession
	 */
	@Override
	public void constructBetRecipeSession(FormulationStakeDTO recipe, SessionDTO raceRecipeSession) {
		if (raceRecipeSession != null) {
			RaceRefDTO keyRaceRef = recipe.getRaceRef();
			int contractor = recipe.getContractor();
			int betCodeRef = recipe.getBetCodeRef();
			int indexEvent = isEventOnSession(keyRaceRef, raceRecipeSession);
			if (indexEvent == -1) {
				constructEventFromBetRecipe(raceRecipeSession, recipe);
			} else {

				int indexRace = isRaceOnEvent(keyRaceRef, raceRecipeSession, indexEvent);
				if (indexRace == -1) {

					constructRaceFromBetRecipe(raceRecipeSession, indexEvent, recipe);

				} else {
					putBetRecipe(recipe, raceRecipeSession, keyRaceRef, contractor, betCodeRef, indexEvent, indexRace);

				}
			}

		}

	}

	/**
	 * @param recipe
	 * @param raceRecipeSession
	 * @param keyRaceRef
	 * @param contractor
	 * @param betCodeRef
	 * @param indexEvent
	 * @param indexRace
	 */
	private void putBetRecipe(FormulationStakeDTO recipe, SessionDTO raceRecipeSession, RaceRefDTO keyRaceRef,
			int contractor, int betCodeRef, int indexEvent, int indexRace) {
		int indexPartner = isPartnerOnRace(contractor, raceRecipeSession, indexEvent, indexRace);
		if (indexPartner == -1) {
			constructPartnerFromBetRecipe(raceRecipeSession, indexEvent, indexRace, recipe);
		} else {
			int indexBet = isBetRefOnAuthorisedPartner(betCodeRef, raceRecipeSession, indexEvent, indexRace,
					indexPartner);
			if (indexBet == -1) {
				addBetRecipe(raceRecipeSession, recipe, indexEvent, indexRace, indexPartner);
			} else {
				modifyBetRecipe(raceRecipeSession, recipe, indexEvent, indexRace, indexPartner, indexBet);
			}
		}
	}

	private void modifyBetRecipe(SessionDTO raceRecipeSession, FormulationStakeDTO recipe, int indexEvent,
			int indexRace, int indexPartner, int indexBet) {

		BigDecimal stake = recipe.getStake();
		ZonedDateTime update = recipe.getUpdateDate();

		FormulationStakeDTO formulationStake = raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace)
				.getAuthorizedPartners().get(indexPartner).getFormulationStakes().get(indexBet);

		boolean differentValues = formulationStake.getStake() == null
				|| formulationStake.getStake().doubleValue() != stake.doubleValue();
		boolean differentDates = formulationStake.getUpdateDate() == null
				|| !formulationStake.getUpdateDate().equals(update);
		if (differentValues) {
			formulationStake.setStake(stake);
		}
		if (differentDates) {
			formulationStake.setUpdateDate(update);
		}
	}

	private void modifyPartnerRecipe(SessionDTO raceRecipeSession, double cashAmount, int indexEvent, int indexRace,
			int indexPartner) {

		AuthorisedPartnerDTO partner = raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace)
				.getAuthorizedPartners().get(indexPartner);

		if (partner.getStake().doubleValue() != cashAmount) {
			partner.setStake(BigDecimal.valueOf(cashAmount));
			partner.setUpdateDate(ZonedDateTime.now());
		}
	}

	private void addBetRecipe(SessionDTO raceRecipeSession, FormulationStakeDTO recipe, int indexEvent, int indexRace,
			int indexPartner) {

		AuthorisedPartnerDTO authPart = raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace)
				.getAuthorizedPartners().get(indexPartner);

		if (authPart.getFormulationStakes() == null) {
			authPart.setFormulationStakes(new ArrayList<>());
		}
		authPart.getFormulationStakes().add(recipe);
	}

	private void addPartnerRecipe(SessionDTO raceRecipeSession, int contractor, double cashAmount, int indexEvent,
			int indexRace) {

		RaceDTO race = raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace);
		if (race.getAuthorizedPartners() == null) {
			race.setAuthorizedPartners(new ArrayList<>());
		}
		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(contractor, cashAmount);
		if (authorisedPartnerDTO != null) {
			race.getAuthorizedPartners().add(authorisedPartnerDTO);
		}
	}

	private void addPartnerRecipe(SessionDTO raceRecipeSession, AuthorisedPartnerDTO apDTO, int indexEvent,
			int indexRace) {
		RaceDTO race = raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace);

		if (race.getAuthorizedPartners() == null) {
			race.setAuthorizedPartners(new ArrayList<>());
		}
		race.getAuthorizedPartners().add(apDTO);

	}

	private double calculatePartnerRecipe(SessionDTO raceRecipeSession, int indexEvent, int indexRace, int indexPartner,
			FormulationStakeDTO recipe) {

		AuthorisedPartnerDTO partner = raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace)
				.getAuthorizedPartners().get(indexPartner);

		if (partner != null) {
			double actualStake = partner.getStake().doubleValue();

			partner.setStake(BigDecimal.valueOf(actualStake + recipe.getStake().doubleValue()));
			return partner.getStake().doubleValue();
		}
		return -1;

	}

	private void constructPartnerFromBetRecipe(SessionDTO raceRecipeSession, int indexEvent, int indexRace,
			FormulationStakeDTO recipe) {

		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(recipe.getContractor(), -1);
		if (authorisedPartnerDTO != null) {
			authorisedPartnerDTO.setFormulationStakes(new ArrayList<>());
			authorisedPartnerDTO.getFormulationStakes().add(recipe);
			raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace).getAuthorizedPartners()
					.add(authorisedPartnerDTO);

		}
	}

	private void constructRaceFromBetRecipe(SessionDTO raceRecipeSession, int indexEvent, FormulationStakeDTO recipe) {
		RaceDTO race = new RaceDTO();
		race.setPk(recipe.getRaceRef().getRacePk());
		race.setNumber(recipe.getRaceRef().getRaceNumber());
		race.setAuthorizedPartners(new ArrayList<>());
		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(recipe.getContractor(), -1);
		if (authorisedPartnerDTO != null) {
			authorisedPartnerDTO.setFormulationStakes(new ArrayList<>());
			authorisedPartnerDTO.getFormulationStakes().add(recipe);
			race.getAuthorizedPartners().add(authorisedPartnerDTO);
			raceRecipeSession.getEvents().get(indexEvent).getRaces().add(race);
		}

	}

	private void constructEventFromBetRecipe(SessionDTO raceRecipeSession, FormulationStakeDTO recipe) {
		if (raceRecipeSession.getEvents() == null) {
			raceRecipeSession.setEvents(new ArrayList<>());
		}
		EventDTO event = new EventDTO();
		event.setId(recipe.getRaceRef().getEventId());

		event.setRaces(new ArrayList<>());
		RaceDTO race = new RaceDTO();
		race.setPk(recipe.getRaceRef().getRacePk());
		race.setNumber(recipe.getRaceRef().getRaceNumber());
		race.setAuthorizedPartners(new ArrayList<>());
		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(recipe.getContractor(), -1);
		if (authorisedPartnerDTO != null) {
			authorisedPartnerDTO.setFormulationStakes(new ArrayList<>());
			authorisedPartnerDTO.getFormulationStakes().add(recipe);
			race.getAuthorizedPartners().add(authorisedPartnerDTO);
		}
		event.getRaces().add(race);
		raceRecipeSession.getEvents().add(event);

	}

	private AuthorisedPartnerDTO buildAuthorisedPartnerDTO(int contractor, double cashAmount) {

		Partner partner = partnerService.getPartner(contractor);
		if (partner != null) {
			AuthorisedPartnerDTO authorisedPartnerDTO = new AuthorisedPartnerDTO();
			authorisedPartnerDTO.setPartner(new PartnerDTO(partner));
			authorisedPartnerDTO.setStake(BigDecimal.valueOf(cashAmount));
			authorisedPartnerDTO.setUpdateDate(ZonedDateTime.now());
			return authorisedPartnerDTO;
		}

		return null;
	}

	private AuthorisedPartnerDTO buildAuthorisedPartnerDTO(Partner partner, double cashAmount) {

		if (partner != null) {
			AuthorisedPartnerDTO authorisedPartnerDTO = new AuthorisedPartnerDTO();
			authorisedPartnerDTO.setPartner(new PartnerDTO(partner));
			authorisedPartnerDTO.setStake(BigDecimal.valueOf(cashAmount));
			authorisedPartnerDTO.setUpdateDate(ZonedDateTime.now());
			return authorisedPartnerDTO;
		}

		return null;
	}

	private int isBetRefOnAuthorisedPartner(int betCodeRef, SessionDTO raceRecipeSession, int eventIndex, int raceIndex,
			int partnerIndex) {

		List<EventDTO> events = raceRecipeSession.getEvents();
		List<RaceDTO> races = events.get(eventIndex).getRaces();
		List<AuthorisedPartnerDTO> partners = races.get(raceIndex).getAuthorizedPartners();
		List<FormulationStakeDTO> stakes = partners.get(partnerIndex).getFormulationStakes();
		if (stakes != null && !stakes.isEmpty()) {
			return this.getIndexBets(betCodeRef, stakes);
		}

		return -1;
	}

	private int isPartnerOnRace(int contractor, SessionDTO raceRecipeSession, int eventIndex, int raceIndex) {

		List<EventDTO> events = raceRecipeSession.getEvents();
		if (events != null) {
			List<RaceDTO> races = events.get(eventIndex).getRaces();
			List<AuthorisedPartnerDTO> partners = races.get(raceIndex).getAuthorizedPartners();
			if (partners != null && !partners.isEmpty()) {
				Partner partner = partnerService.getPartner(contractor);
				if (partner != null) {
					return this.getIndexPartners(contractor, partners);
				}
			}
		}
		return -1;
	}

	private int isRaceOnEvent(RaceRefDTO keyRaceRef, SessionDTO raceRecipeSession, int eventIndex) {
		List<EventDTO> events = raceRecipeSession.getEvents();
		if (eventIndex != -1 && events.get(eventIndex) != null) {
			List<RaceDTO> races = events.get(eventIndex).getRaces();
			if (!races.isEmpty()) {
				return this.getIndexRace(keyRaceRef.getRaceNumber(), races);
			}
		}

		return -1;
	}

	private int isEventOnSession(RaceRefDTO keyRaceRef, SessionDTO raceRecipeSession) {
		List<EventDTO> events = raceRecipeSession.getEvents();
		if (events != null && !events.isEmpty()) {
			return this.getIndexEvents(keyRaceRef.getEventId(), events);
		}
		return -1;
	}

	@Override
	public SessionDTO getCurrentBetRecipeSession() {
		return currentBetRecipeSession;
	}

	@Override
	public void setCurrentBetRecipeSession(SessionDTO currentBetRecipeSession) {
		this.currentBetRecipeSession = currentBetRecipeSession;
	}

	@Override
	public Map<String, SessionDTO> getPreSessionBetRecipeSession() {
		return preSessionBetRecipeSession;
	}

	@Override
	public void setPreSessionBetRecipeSession(Map<String, SessionDTO> preSessionBetRecipeSession) {
		this.preSessionBetRecipeSession = preSessionBetRecipeSession;
	}

	public List<RemarkableRace> getGrandPrizes() {
		return grandPrizes;
	}

	public void setGrandPrizes(List<RemarkableRace> grandPrizes) {
		this.grandPrizes = grandPrizes;
	}

	@Override
	public List<RemarkableRace> getUpcomingRacesFromDate(LocalDate date, int limit) {
		return raceService.findRemarkablesAfterDate(date, limit);
	}

	@Override
	public RemarkableRaceHistory getRemarkableRaceHistory(LocalDate date, LocalTime fromHour, LocalTime toHour) {
		List<RemarkableRaceRecipe> recipes = raceService.findAllRemarkableRecipeByDateAndAfterHour(date, fromHour,
				toHour);

		// Exclude Partner import
		List<String> importPartners = StoreAdmin.getInstance().getAttImport();
		List<String> periodsStr = recipes.stream().map(RemarkableRaceRecipe::getDateRecipe).distinct()
				.sorted((o1, o2) -> o1.isAfter(o2) ? 1 : -1).map(e -> e.format(DateTimeFormatter.ISO_LOCAL_TIME))
				.collect(Collectors.toList());

		if (periodsStr.size() > NB_PERIODS_FETCHED && fromHour == null && toHour == null) {
			List<String> targetPeriods = periodsStr.subList(periodsStr.size() - NB_PERIODS_FETCHED, periodsStr.size());

			recipes = recipes.stream().filter(
					elem -> targetPeriods.contains(elem.getDateRecipe().format(DateTimeFormatter.ISO_LOCAL_TIME)))
					.toList();
		}

		RemarkableRaceHistory history = new RemarkableRaceHistory(date, importPartners);

		recipes.forEach(history::addRecipe);

		return history;
	}

	@Override
	public RemarkableRaceHistory getRemarkableRaceHistoryEvent(LocalDate date, LocalTime fromHour, LocalTime toHour) {
		List<RemarkableRaceRecipe> recipes = raceService.findAllRemarkableRecipeByDateAndAfterHourEvent(date, fromHour,
				toHour);

		// Exclude Partner import

		List<String> importPartners = StoreAdmin.getInstance().getAttImport();

		List<String> periodsStr = recipes.stream().map(RemarkableRaceRecipe::getDateRecipe).distinct()
				.sorted((o1, o2) -> o1.isAfter(o2) ? 1 : -1).map(e -> e.format(DateTimeFormatter.ISO_LOCAL_TIME))
				.collect(Collectors.toList());

		if (periodsStr.size() > NB_PERIODS_FETCHED && fromHour == null && toHour == null) {
			List<String> targetPeriods = periodsStr.subList(periodsStr.size() - NB_PERIODS_FETCHED, periodsStr.size());

			recipes = recipes.stream().filter(
					elem -> targetPeriods.contains(elem.getDateRecipe().format(DateTimeFormatter.ISO_LOCAL_TIME)))
					.toList();
		}

		RemarkableRaceHistory history = new RemarkableRaceHistory(date, importPartners);

		recipes.forEach(history::addRecipe);

		return history;
	}

	@Override
	public List<PartnerDTO> getPartnersFromOrganization(Integer orgId) {
		List<Partner> partners;

		if (orgId == null) {
			partners = partnerService.getAllPartners();
		} else {
			partners = partnerService.getPartnersFromOrganization(orgId);
		}

		return partners.stream().map(PartnerDTO::new).toList();
	}

	@Override
	public LocalDate getPreviousDateOfRemarkableRace(RemarkableRace sourceRace) {
		return raceService.findDateOfRemarkableRaceByGrandPrixIdAndYear(sourceRace.getGrandPrize().getPk(),
				sourceRace.getDateSession().getYear() - 1);
	}


	/**
	 * Calculates the equivalent date the year before a given date. The equivalent
	 * date has the same week number and day number ( for example, the first
	 * thursday for the year )
	 * 
	 * @param currentDate The source date
	 * @returns The equivalent date
	 */
	@Override
	public RemarkableRaceHistory getRecipesForEquivalentDayLastYear(LocalDate currentDate) {
		LocalDate previousYearDate = StatsCaUtils.getEquivalentDateFromPreviousYear(currentDate);
		// Exclude Partner import
		
		List<String> importPartners = StoreAdmin.getInstance().getAttImport();

		List<RemarkableRaceRecipe> recipes = raceService
				.findAllRemarkableRecipeByDateAndAfterHourEvent(previousYearDate, null, null);
		RemarkableRaceHistory history = new RemarkableRaceHistory(previousYearDate, importPartners);

		recipes.forEach(history::addRecipe);

		return history;
	}

//	public List<RemarkableRace> getRemarkableRaces() {
//		return raceService.getRemarkableRaces();
//	}
//
//	public Map<Long, Map<Integer, BigDecimal>> getTotalBetsForYears(List<Integer> targetYears) {
//		return raceService.getMockTotalBetsForYears(targetYears);
//		//return raceService.getTotalBetsForYears(targetYears);
//	}

	@Override
	public RaceTrack getRaceTrack(Long pk) {
		return raceTrackService.getByPk(pk);	
	}


	@Override
	public SessionDTO getYesterdayRaceCard() {
		return yesterdayRaceCard;
	}


	@Override
	public void setYesterdayRaceCard(SessionDTO yesterdayRaceCard) {
		this.yesterdayRaceCard = yesterdayRaceCard;
	}


	@Override
	public SessionDTO getPastRaceCard() {
		return pastRaceCard;
	}


	@Override
	public void setPastRaceCard(SessionDTO pastRaceCard) {
		this.pastRaceCard = pastRaceCard;
	}
	
	@Override
	public LocalDate getFirstAvailableSessionDate() {
		if(firstSessionDate==null) {
 			firstSessionDate = eventService.getFirstSessionDate();
 		}
 		return firstSessionDate;
	}

	@Override
	public List<RemarkableRace> getRemarkableRaces() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, Map<Integer, BigDecimal>> getTotalBetsForYears(List<Integer> targetYears) {
		// TODO Auto-generated method stub
		return null;
	}
}

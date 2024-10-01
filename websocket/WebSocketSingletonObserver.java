package com.carrus.statsca.websocket;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.QueueRecipesService;
import com.carrus.statsca.RaceCardService;
import com.carrus.statsca.S3kRecipeService;
import com.carrus.statsca.dto.SessionDTO;
import com.carrus.statsca.event.BetEvt;
import com.carrus.statsca.event.EventChangeEvt;
import com.carrus.statsca.event.Evt;
import com.carrus.statsca.event.RaceEvt;
import com.carrus.statsca.event.RaceRecipeEvt;
import com.carrus.statsca.event.SessionChange;
import com.carrus.statsca.event.SessionChange.SessionChangeTypeEnum;
import com.carrus.statsca.event.SessionEvt;
import com.carrus.statsca.event.UpdatedRacesRecipesEvt;
import com.carrus.statsca.exceptions.RecipeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmc.club.MutuelService;
import com.pmc.club.entity.RaceState;
import com.pmc.club.entity.partner.Partner;
import com.pmc.club.event.BetChange;
import com.pmc.club.event.BetChange.BetChangeTypeEnum;
import com.pmc.club.event.ChronologyLevel;
import com.pmc.club.event.ConnectionChange;
import com.pmc.club.event.EventChange;
import com.pmc.club.event.EventChange.EventStateEnum;
import com.pmc.club.event.Origin;
import com.pmc.club.event.RaceChange;
import com.pmc.club.event.RaceChange.RaceChangeTypeEnum;
import com.pmc.club.event.SessionChange.SessionStateEnum;
import com.pmc.club.event.UpdatedRacesRecipes;
import com.pmc.club.references.RaceRef;
import com.pmc.star3000.service.PartnerService;

@Singleton
@Startup
public class WebSocketSingletonObserver {
	public static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSingletonObserver.class);

	private static final String RACECARD_UPDATED_FROM_BET_CHANGE_BET_FORMAT = "Racecard updated from bet change: BET {} ";

	private static final String RACECARD_UPDATED_FROM_RACE_CHANGE_RACE_FORMAT = "Racecard updated from race change: RACE {} ";

	private static final String RACECARD_UPDATED_FROM_EVENT_CHANGE_EVENT_FORMAT = "Racecard updated from event change: EVENT {} ";

	private static final String ERROR_FORMAT = " error {} : {} ";

	@Inject
	private RaceCardService racecardService;

	@Inject
	private S3kRecipeService recipeService;

	@Inject
	private PartnerService partnerService;

	@Inject
	private MutuelService mutuelService;

	@Inject
	private QueueRecipesService queueRecipesService;

	private LocalDateTime start = null;

	private static final int INTERVAL = 5;

	public void observeChronology(@Observes(during = TransactionPhase.AFTER_COMPLETION) ChronologyLevel chronologyLevel,
			EventMetadata eventMeta) {
		Evt evt = null;
		// LOGGER.warn("inside observer methode");
		// Recherche de l'origine de l'évènement
		String origin = "";
		Set<Annotation> qualifiers = eventMeta.getQualifiers();
		for (Annotation annotation : qualifiers) {
			if (annotation instanceof Origin) {
				origin = ((Origin) annotation).value();
				break;
			}
		}
		if (chronologyLevel instanceof BetChange betChangeEvent) {
			LOGGER.info("Bet change received {} : {}", betChangeEvent.getCode(), betChangeEvent.getBetChangeType());
			racecardUpdateWithBetChange(betChangeEvent);
			evt = new BetEvt(betChangeEvent, origin);
		} else if (chronologyLevel instanceof RaceChange raceChangeEvent) {
			LOGGER.info("Race change received {} - {}", raceChangeEvent.getRaceChangeType(),
					raceChangeEvent.getRaceState());
			racecardUpdateWithRaceChange(raceChangeEvent);
			evt = new RaceEvt(raceChangeEvent);
			// TODO méthode déclencher un appel recipes uniquement sur un racechangeType
			// raceStateEnum, racestate parti
			// -> sleep(2000)
			if (raceChangeEvent.getRaceState() != null && raceChangeEvent.getRaceState().equals(RaceState.STARTED)) {
				RaceRef raceRef = raceChangeEvent.getRaceRef();
				recipeService.putBetRecipesOnRace(raceRef);
			}
		} else if (chronologyLevel instanceof EventChange eventChangeEvent) {
			racecardUpdateWithEventChange(eventChangeEvent);
			evt = new EventChangeEvt(eventChangeEvent);
		} else if (chronologyLevel instanceof com.pmc.club.event.SessionChange sessionChangeEvent) {
			LOGGER.info("Session event received ");
			// update racecard
			racecardUpdateWithSessionChange(sessionChangeEvent);
			SessionChangeTypeEnum type = SessionChangeTypeEnum.SESSION_LOADED; // when loaded
			if (sessionChangeEvent.getSessionState() == SessionStateEnum.UPDATED) // when updated
				type = SessionChangeTypeEnum.SESSION_CHANGE;
			// sessionChangeEvent
			evt = getRacecardEvt(type);

			// Lancement de l'injection des enjeux
//			if (!this.recipeInjectionDone) {
//				this.recipesInjectorService.processRecipesInjection();
//				this.recipeInjectionDone = true;
//			}

//			if (sessionChangeEvent.getSessionState() == SessionStateEnum.CLOSED) {
//				this.racecardService.stopTimer();
//			}
		} else if (chronologyLevel instanceof UpdatedRacesRecipes urrEvent) {
			LOGGER.info("Race Recipe event received {}", urrEvent.getRaceRecipes().size());
			evt = new UpdatedRacesRecipesEvt(urrEvent);
			// TODO MERGE UpdateRacesRecipeEvt to SessionEvt
			UpdatedRacesRecipesEvt urre = (UpdatedRacesRecipesEvt) evt;
			evt = getRecipeUpdateEvt(urre);
			// update racecard with new recipes
			// racecardUpdateWithUpdatedRacesRecipes(urre);
			try {
				// update received recipes
				racecardService.updateRaceRecipe(urre);
				// update in memory and broadcast
				updateBetRecipesFromRacesRecipes(urre);
			} catch (RecipeException re) {
				LOGGER.warn(re.getMessage());
				evt = null;
			}
		} else if (chronologyLevel instanceof ConnectionChange connection) {
			LOGGER.info("Connection Change event received {}", connection.getConnectionState());
		}
		if (evt != null) {
			WebSocketStatsCaEndpoint.diffusionEvt(evt);
		}
	}

	private void racecardUpdateWithEventChange(EventChange eventChangeEvent) {
		if (eventChangeEvent.getEventState() == EventStateEnum.ADDED) {
			eventAddingEventProcessing(eventChangeEvent);
		} else if (eventChangeEvent.getEventState() == EventStateEnum.REMOVED) {
			eventRemovalEventProcessing(eventChangeEvent);
		} else {
			eventEditEventProcessing(eventChangeEvent);
		}
	}

	private boolean eventRemovalEventProcessing(EventChange eventChangeEvent) {
		boolean removed = false;
		try {
			racecardService.deleteEvent(eventChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_EVENT_CHANGE_EVENT_FORMAT, eventChangeEvent.getEventState().name());
			}
			removed = true;
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
		return removed;
	}

	private void eventAddingEventProcessing(EventChange eventChangeEvent) {
		try {
			racecardService.addEvent(eventChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_EVENT_CHANGE_EVENT_FORMAT, eventChangeEvent.getEventState().name());
			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
	}

	private void eventEditEventProcessing(EventChange eventChangeEvent) {
		try {
			racecardService.editEvent(eventChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_EVENT_CHANGE_EVENT_FORMAT, eventChangeEvent.getEventState().name());
			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
	}

	private void racecardUpdateWithRaceChange(RaceChange raceChangeEvent) {
		if (raceChangeEvent.getRaceChangeType() == RaceChangeTypeEnum.ADDED) {
			raceAddingEventProcessing(raceChangeEvent);
		} else if (raceChangeEvent.getRaceChangeType() == RaceChangeTypeEnum.REMOVED) {
			raceRemovalEventProcessing(raceChangeEvent);
		} else {
			raceEditEventProcessing(raceChangeEvent);
		}
	}

	private boolean raceRemovalEventProcessing(RaceChange raceChangeEvent) {

		boolean removed = false;
		try {
			racecardService.deleteRace(raceChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_RACE_CHANGE_RACE_FORMAT, raceChangeEvent.getRaceChangeType().name());
			}
			removed = true;
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
		return removed;
	}

	private void raceAddingEventProcessing(RaceChange raceChangeEvent) {
		try {
			racecardService.addRace(raceChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_RACE_CHANGE_RACE_FORMAT, raceChangeEvent.getRaceChangeType().name());
			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
	}

	private void raceEditEventProcessing(RaceChange raceChangeEvent) {
		try {
			racecardService.editRace(raceChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_RACE_CHANGE_RACE_FORMAT, raceChangeEvent.getRaceChangeType().name());
			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
	}

	private void racecardUpdateWithBetChange(BetChange betChangeEvent) {
		if (betChangeEvent.getBetChangeType() == BetChangeTypeEnum.ADDED) {
			betAddingEventProcessing(betChangeEvent);
		} else if (betChangeEvent.getBetChangeType() == BetChangeTypeEnum.REMOVED) {
			betRemovalEventProcessing(betChangeEvent);
		}
	}

	private void betRemovalEventProcessing(BetChange betChangeEvent) {
		try {
			racecardService.deleteBet(betChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_BET_CHANGE_BET_FORMAT, betChangeEvent.getBetChangeType().name());
			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
	}

	private void betAddingEventProcessing(BetChange betChangeEvent) {
		try {
			racecardService.addBet(betChangeEvent);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(RACECARD_UPDATED_FROM_BET_CHANGE_BET_FORMAT, betChangeEvent.getBetChangeType().name());
			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
	}

	private void racecardUpdateWithSessionChange(com.pmc.club.event.SessionChange sessionChangeEvent) {
		if (sessionChangeEvent.getSessionState() == SessionStateEnum.STARTED
				|| sessionChangeEvent.getSessionState() == SessionStateEnum.UPDATED
				|| sessionChangeEvent.getSessionState() == SessionStateEnum.LOADED) {
			sessionChangeEventProcessing(sessionChangeEvent);
		}
	}

	private void sessionChangeEventProcessing(com.pmc.club.event.SessionChange sessionChangeEvent) {
		try {

			racecardService.loadCurrentRaceCard(sessionChangeEvent.getSessionRef().getEventDate());

			if (sessionChangeEvent.getSessionState() == SessionStateEnum.STARTED
					|| (sessionChangeEvent.getSessionState() == SessionStateEnum.LOADED && sessionChangeEvent
							.getSessionRef().getEventDate().equals(mutuelService.getCurrentSessionDate()))) {

				// JKE: si une nouvelle session est lancée ou bien si une session est rechargée,
				// on met à NULL les listes actuels d'enjeux

				if (recipeService.getS3kBetCodeRecipes() != null || recipeService.getStoredResponses() != null) {
					recipeService.clearStoredDatas();
				}
				LOGGER.info("Recipes cleared due to session changes. session change : {}",
						sessionChangeEvent.getSessionState());
			}
			/*
			 * if(sessionChangeEvent.getSessionState() == SessionStateEnum.UPDATED) { //A ce
			 * jour : Prise en compte uniquement mise à jour heure de départ course
			 * racecardService.updateCurrentRacecard(sessionChangeEvent); }
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Racecard loaded from session change: SESSION {} - {}",
						sessionChangeEvent.getSessionState().name(), sessionChangeEvent.getSessionRef().getEventDate());
			}
			if (sessionChangeEvent.getSessionState() == SessionStateEnum.STARTED
					|| sessionChangeEvent.getSessionState() == SessionStateEnum.LOADED) {
				// ACTIVER les flux financiers

			}
		} catch (Exception e) {
			LOGGER.error(ERROR_FORMAT, e.getClass().getName(), e.getMessage());
		}
		// TO REMOVE : TEST OF RECIPE REQUEST ON THE FIRST RACE OF EACH EVENT
		// async();
	}

	private Evt getRacecardEvt(SessionChangeTypeEnum sessionChangeType) {
		SessionDTO sessionDTO = racecardService.getRaceCard();

		ObjectMapper mapper = new ObjectMapper();
		try {
			if (sessionDTO != null) {
				String jsonString = mapper.writeValueAsString(sessionDTO);
				SessionChange sessionChange = new SessionChange(ZonedDateTime.now(), sessionChangeType, jsonString);
				SessionEvt sessionEvt = new SessionEvt(sessionChange);
				return sessionEvt;
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
		}
		return null;
	}

	private Evt getRecipeUpdateEvt(UpdatedRacesRecipesEvt urre) {

		raceRecipeUpdate(urre);
		return new SessionEvt(
				new SessionChange(ZonedDateTime.now(), SessionChangeTypeEnum.STAKE_CHANGE, urre.toJson()));
	}

	private void raceRecipeUpdate(UpdatedRacesRecipesEvt urrEvent) {
		List<RaceRecipeEvt> recipes = urrEvent.getRaceRecipes().stream().map(this::completePartner).toList();
		urrEvent.setRaceRecipes(recipes);

	}

	private RaceRecipeEvt completePartner(RaceRecipeEvt rr) {
		Partner partner = partnerService.getPartner(rr.getContractor());
		if (partner != null) {
			rr.setContractorName(partner.getName());
			rr.setContractorShortName(partner.getShortName());
		} else {
			LOGGER.warn("PARTNER NOT FOUND {} => RECIPE IGNORED : Please check the missing Partner",
					rr.getContractor());
		}
		return rr;
	}

	/*
	 * private void racecardUpdateWithUpdatedRacesRecipes(UpdatedRacesRecipesEvt
	 * urre) { racecardService.updateRaceRecipe(urre); }
	 */

	private void updateBetRecipesFromRacesRecipes(UpdatedRacesRecipesEvt urre) {
		List<String> raceRefUpdated = new ArrayList<>();
		for (RaceRecipeEvt evt : urre.getRaceRecipes()) {
			RaceRef raceRef = evt.getRaceRef();
			boolean restart = false;
			if (!raceRefUpdated.contains(raceRef.toString())) {

				if (start == null) {
					// à voir si déplacer cette fonction dans un autre EJB est nécessaire
					restart = true;
					// queueRecipesService.processQueue();
					start = LocalDateTime.now();
				} else {
					LocalDateTime t = LocalDateTime.now();
					long between = ChronoUnit.SECONDS.between(start, t);
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("between value {}", between);
					}
					if (between > INTERVAL) {
						restart = true;
						start = LocalDateTime.now();
					}
				}
				if (restart)
					queueRecipesService.processQueue();
				queueRecipesService.putOnQueue(raceRef);
				raceRefUpdated.add(raceRef.toString());
			}
		}
	}

}
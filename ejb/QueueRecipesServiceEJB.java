package com.carrus.statsca.ejb;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Queue;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.QueueRecipesService;
import com.carrus.statsca.S3kRecipeService;
import com.carrus.statsca.dto.SessionDTO;
import com.carrus.statsca.event.SessionChange;
import com.carrus.statsca.event.SessionEvt;
import com.carrus.statsca.websocket.WebSocketStatsCaEndpoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.carrus.statsca.event.SessionChange.SessionChangeTypeEnum;
import com.pmc.club.entity.Race;
import com.pmc.club.references.RaceRef;

@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton
public class QueueRecipesServiceEJB implements QueueRecipesService {

	private static final int THREAD_SLEEP = 5000;

	private static final int INTERVAL = 5 * 1000;

	public static LocalDateTime START;

	Logger LOGGER = LoggerFactory.getLogger(QueueRecipesServiceEJB.class);

	@Inject
	private S3kRecipeService recipeService;

	private Queue<RaceRef> queue = new LinkedList<>();

	private Queue<RaceRef> queueProcessFailed = new LinkedList<>();

	/**
	 * traitement asynchrone avec temps de latence de la file de courses à traiter
	 */
	@Asynchronous
	@Override
	public void processQueue() {

		try {
			Thread.sleep(INTERVAL);
			if (!this.queueProcessFailed.isEmpty()) {
				while (!queueProcessFailed.isEmpty()) {
					RaceRef raceRef = queueProcessFailed.remove();
					if (!queue.contains(raceRef)) {
						this.putOnQueue(raceRef);
					}
				}
			}
			if (!this.queue.isEmpty()) {
				while (!queue.isEmpty()) {
					RaceRef raceRef = queue.remove();
					processRaceRef(raceRef);
				}
				// processing queue finished > reset START to null
				queue.clear();
			}
		} catch (InterruptedException e) {
			LOGGER.error("error while trying to make thread asleep {}", e.getMessage());
			Thread.currentThread().interrupt();
		}

	}

	/**
	 * traitement d'une des courses de la file
	 * 
	 * @param raceRef
	 */
	private void processRaceRef(RaceRef raceRef) {
		try {
			// Thread.sleep(THREAD_SLEEP);
			LOGGER.info("Queue recipes processing: {} - {} raceRefs left on Queue", raceRef, this.queue.size());
			Race race = recipeService.putBetRecipesOnRace(raceRef);
			// broadcast event
			// recipeService.getStoredResponses().get(raceRef);
			if (race != null) {
				SessionDTO sessionDTO = recipeService.getStoredResponses().get(race.getPk());
				if(sessionDTO != null) {
					String sessionJson = "{}";
					try {
						ObjectMapper mapper = new ObjectMapper();
						mapper.registerModule(new JavaTimeModule());
						sessionJson = mapper.writeValueAsString(sessionDTO);
						SessionEvt sessionEvt = new SessionEvt(new SessionChange(ZonedDateTime.now(),
								SessionChangeTypeEnum.STAKE_DETAILS_CHANGE, sessionJson));
						//broadcast event
						WebSocketStatsCaEndpoint.diffusionEvt(sessionEvt);
					} catch (JsonProcessingException e) {
						LOGGER.error("Error while generating json {}", e.getMessage());
					}				
				}			
			}

		} /*
			 * catch (InterruptedException e) {
			 * LOGGER.error("error while processing Queue {}", e.getMessage());
			 * queueProcessFailed.add(raceRef); Thread.currentThread().interrupt(); }
			 */catch (Exception e) {
			LOGGER.error("error while processing Queue {}", e.getMessage());
			queueProcessFailed.add(raceRef);
		}
	}

	/**
	 * ajout de la course dans la file
	 */
	@Override
	public synchronized void putOnQueue(RaceRef raceRef) {
		LOGGER.info("queue recipes adding: {}", raceRef);
		queue.add(raceRef);
	}

	@Override
	public Queue<RaceRef> getQueue() {
		return queue;
	}

}

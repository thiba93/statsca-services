package com.carrus.statsca.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.RaceCardService;
import com.pmc.club.event.ChronologyLevel;

@ApplicationScoped
public class WebSocketManager {

	@Inject
	private RaceCardService racecardService;

	/** Logger par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketManager.class);

	/**
	 * Méthode observant les évènements de chronology publiés via CDI au sein de
	 * l'application On précise la phase AFTER_COMPLETION pour attendre que les
	 * données soient persistées en base.
	 * 
	 * @param chronologyChange le message de chronology reçu par CDI
	 */
	public void observeChronology(@Observes(during = TransactionPhase.AFTER_COMPLETION) ChronologyLevel chronologyLevel) {

		//JKE: aucun codage prévu à ce niveau
	}

}

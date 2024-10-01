package com.carrus.statsca.event;

import com.pmc.club.event.RaceLevel;

/**
 * Classe de factorisation des evènements de chronologie qui portent
 * sur une course particulière. Classe mère de RaceEvt, ParticipantEvt, BetEvt.
 * 
 * @author BT - ARTSYS 2018
 * @since 29 mars 2018
 */
public abstract class RaceEvtAbstract extends EventEvtAbstract {
	/** Clé unique identifiante de la course concernée par l'évènement de chronologie */
	private final Long racePk;

	/** Numéro de la course dans la réunion */
	private final Integer number;
	
	/** Constructeur à partir de l'évènement CDI */
	public RaceEvtAbstract(RaceLevel raceLevel) {
		super(raceLevel.getRace().getEvent(), raceLevel.getCreation());

		this.racePk = raceLevel.getRace().getPk();
		this.number = raceLevel.getRace().getNumber();
	}
	
	/** Constructeur à partir de l'évènement CDI, et de l'origine de l'évènement */
	public RaceEvtAbstract(RaceLevel raceLevel, String origin) {
		super(raceLevel.getRace().getEvent(), raceLevel.getCreation(), origin);

		this.racePk = raceLevel.getRace().getPk();
		this.number = raceLevel.getRace().getNumber();
	}
	
	/**
	 * @return the racePk
	 */
	public Long getRacePk() {
		return racePk;
	}

	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
}
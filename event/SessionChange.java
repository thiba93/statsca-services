/***/
package com.carrus.statsca.event;

import java.time.ZonedDateTime;

/**
 * Evènement changement d'état d'une course dans la racecard
 * Implémentation de l'interface RaceChange
 * 
 * @author BT - ARTSYS 2015
 * @since 31 janvier 2015
 */
public class SessionChange {

	/** Type de changement intervenu sur la session*/
	private final SessionChangeTypeEnum sessionChangeType;
	/** Données liées à la session */
	private final String data;
	
	private final ZonedDateTime creation;
	
	/**
	 * Constructeur à partir d'une référence à une course (qui n'existe plus par exemple)
	 * 
	 * @param creation Date et heure zonée de création originelle de l'évènement
	 * @param raceRef la référence à la course concernée 
	 * @param raceChangeType le type de changement intervenu sur la course
	 */
	public SessionChange(ZonedDateTime creation, SessionChangeTypeEnum sessionChangeTypeEnum, String data) {
		super();
		this.sessionChangeType = sessionChangeTypeEnum;
		this.data = data;
		this.creation = creation;
	}	
	
	/**
	 * Getter du type de changement sur la course
	 * 
	 * @return l'état de la course
	 */
	public SessionChangeTypeEnum getSessionChangeType() {
		return sessionChangeType;
	}

	/**
	 * Enumération interne du type de changement
	 */
	public enum SessionChangeTypeEnum {
	    /** Session change */
	    SESSION_LOADED,
	    /** Session update */
	    SESSION_CHANGE,
	    /** Stake update */
	    STAKE_CHANGE,
	    /** Initial session stake state */
	    GLOBAL_STAKE_CHANGE, 
	    /**formulation Stake details**/
	    STAKE_DETAILS_CHANGE
	}

	public String getData() {
		return data;
	}

	public ZonedDateTime getCreation() {
		return creation;
	}
	
	

}

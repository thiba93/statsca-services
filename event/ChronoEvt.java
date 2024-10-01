package com.carrus.statsca.event;

import java.time.ZonedDateTime;

/**
 * Classe de factorisation des evènements de chronologie.
 * 
 * @author BT - ARTSYS 2018
 * @since 5 janvier 2018
 */
public abstract class ChronoEvt extends AbstractEvt {

	/**
	 * Constructeur à partir de la date de création du message
	 */
	public ChronoEvt(ZonedDateTime creation) {
		super(creation);
	}
	
	/**
	 * Constructeur à partir de la date de création du message
	 * et de l'identifiant du système d'origine du message.
	 */
	public ChronoEvt(ZonedDateTime creation, String origin) {
		super(creation, origin);
	}
	
	/* (non-Javadoc)
	 * @see com.pmc.bis.commons.evt.AbstractEvt#getType()
	 */
	@Override
	public EvtType getType() {
		return EvtType.CHRONO;
	}
	
	/**
	 * Donne le sous-type de l'évènement de chrono,
	 * nécessaire pour transmettre le bon type en JSON.
	 * 
	 * @return Constante; sous-type de chronologie
	 */
	public String getChronoType() {
		return this.getClass().getSimpleName();
	}
}
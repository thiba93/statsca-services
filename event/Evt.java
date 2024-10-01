package com.carrus.statsca.event;

import java.time.ZonedDateTime;

/**
 * Interface commune à tous les évènements du système.
 * 
 * @author BT - ARTSYS 2018
 * @since 8 janvier 2018
 */
public interface Evt {
	/**
	 * @return La date de création de l'évènement 
	 */
	public ZonedDateTime getCreation();
	
	/**
	 * Getter du type principal de ce message d'évènement.
	 * 
	 * @return
	 */
	public EvtType getType();
	
	/**
	 * Getter du système d'origine de l'évènement sous la forme d'une chaine
	 * de caractères identifiante. Chaine vide par défaut pour le TOTE.
	 * 
	 * @return L'identifiant du sytème d'origine de l'évènement
	 */
	public String getOrigin();
	
	/**
	 * Implémentation de la méthode de sérialisation vers JSON
	 * 
	 * @return Chaine de caractères JSON qui répresente l'évènement 
	 */
	public String toJson();
	
}
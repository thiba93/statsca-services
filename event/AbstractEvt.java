package com.carrus.statsca.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

/**
 * Factorisation des propriétés des évènements.
 * 
 * @author BT - ARTSYS 2017
 * @since 27 déc. 2017
 */
public abstract class AbstractEvt implements Evt {
	/** Formatter de date au format ISO 8136 */
	protected final DateFormat dfIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


	/** Date/Heure de génération de l'évènement */
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private final ZonedDateTime creation;
	/** Identifiant du système d'origine de l'évènement */
	private final String origin;
	
	/** Constructeur sans attribut */
	public AbstractEvt() {
		this.creation = ZonedDateTime.now();
		this.origin = "";
	}

	/** Constructeur à partir d'une date de création précise */
	public AbstractEvt(ZonedDateTime creation) {
		this.creation = creation;
		this.origin = "";
	}
	/** Constructeur à partir d'une date de création précise, et de l'origine */
	public AbstractEvt(ZonedDateTime creation, String origin) {
		this.creation = creation;
		this.origin = origin;
	}

	/**
	 * @return La date de création de l'évènement 
	 */
	public ZonedDateTime getCreation() {
		return creation;
	}
	
	/**
	 * Getter du système d'origine de l'évènement sous la forme d'une chaine
	 * de caractères identifiante. Chaine vide par défaut pour le TOTE.
	 * 
	 * @return L'identifiant du sytème d'origine de l'évènement
	 */
	public String getOrigin() {
		return origin;
	}
	
	/* (non-Javadoc)
	 * @see com.pmc.bis.commons.evt.Evt#getType()
	 */
	public abstract EvtType getType();
	
	/* (non-Javadoc)
	 * @see com.pmc.bis.commons.evt.Evt#toJson()
	 */
	public abstract String toJson();

}
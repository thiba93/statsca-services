package com.carrus.statsca.event;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.pmc.club.entity.Event;

/**
 * Classe de factorisation des evènements de chronologie qui portent
 * sur une réunion particulière.
 * 
 * @author BT - ARTSYS 2018
 * @since 5 janvier 2018
 */
public abstract class EventEvtAbstract extends ChronoEvt {
	/** Clé unique indentifiante de la course concernée par l'évènement de chronologie */
	private final Long eventPk;
	/** Date de la session de la réunion */
	private LocalDate date;
	/** Numéro de la réunion dans la session */
	private Integer id;
	
	/**
	 * Constructeur à partir d'une réunion et de la date du message
	 * 
	 * @param event La réunion,
	 * @param creation Date de création du message de chronologie
	 */
	public EventEvtAbstract(Event event, ZonedDateTime creation) {
		super(creation);
		
		this.eventPk = event.getPk();
		this.date = event.getDate();
		this.id = event.getId();
	}

	/**
	 * Constructeur à partir d'une réunion et de la date du message
	 * 
	 * @param event La réunion,
	 * @param creation Date de création du message de chronologie
	 * @param origin Identifiant du système d'origine de l'évènement
	 */
	public EventEvtAbstract(Event event, ZonedDateTime creation, String origin) {
		super(creation, origin);
		
		this.eventPk = event.getPk();
		this.date = event.getDate();
		this.id = event.getId();
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/* (non-Javadoc)
	 * @see com.pmc.bis.commons.evt.AbstractEvt#toJson()
	 */
	public String toJson() {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("chronoType", getChronoType());
			builder.add("eventPk", this.eventPk);
			if (getDate() != null) {
				builder.add("date", getDate().toString());
			}
			builder.add("eventId", getId());
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	};

}
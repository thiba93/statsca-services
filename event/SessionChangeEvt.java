package com.carrus.statsca.event;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.pmc.club.event.SessionChange;
import com.pmc.club.event.SessionChange.SessionStateEnum;
import com.pmc.club.references.SessionRef;

public class SessionChangeEvt extends ChronoEvt {
	/** Type de changement intervenu sur la session */
	private final SessionStateEnum sessionState;
	/** Référence à la session concernée par le changement */
	private final SessionRef sessionRef;
	/** Numéro de la réunion demandée, null si on a demandé toutes les réunions */
	private final Integer eventNumber;
	/** Numéro de la course demandée, null si on a demandé toutes les courses */
	private final Integer raceNumber;
	
	/**
	 * Constructeur d'un évènement de changement quelconque sur la session identifiée
	 * par un SessionRef.
	 * Le type de changement est précisé avec le paramètre sessionState
	 *  
	 * @param creation La date et l'heure zonée de création de l'évènement
	 * @param sessionRef La référence complète à la la session
	 * @param sessionState le nouvel état de la session
	 */
	public SessionChangeEvt(ZonedDateTime creation, SessionRef sessionRef, SessionStateEnum sessionState) {
		super(creation);
		
		this.sessionRef = sessionRef;
		this.eventNumber = null;
		this.raceNumber = null;
		this.sessionState = sessionState;
	}
	
	/**
	 * Constructeur d'un évènement de changement quelconque sur la course.
	 * Le type de changement est précisé avec le paramètre raceStateType ainsi
	 * que la date de la session sur laquelle intervient le changement.
	 *  
	 * @param creation La date et l'heure zonée de création de l'évènement
	 * @param sessionDate La date absolue demandée de chargement de la session
	 * @param eventNb Numéro de la réunion demandée spécifiquement, ou null pour toutes les réunions
	 * @param raceNb Numéro de la course demandée spécifiquement, ou null pour toutes les courses
	 * @param sessionState le nouvel état de la session
	 */
	public SessionChangeEvt(ZonedDateTime creation, LocalDate sessionDate, Integer eventNb, Integer raceNb, SessionStateEnum sessionState) {
		super(creation);
		
		this.sessionRef = new SessionRef(null, sessionDate);
		this.eventNumber = eventNb;
		this.raceNumber = raceNb;
		this.sessionState = sessionState;
	}
	
	/**
	 * Constructeur d'un évènement de changement quelconque sur la session.
	 * Le type de changement est précisé avec le paramètre sessionState
	 *  
	 * @param creation La date et l'heure zonée de création de l'évènement
	 * @param sessionDate La date absolue de la session
	 * @param sessionState le nouvel état de la session
	 */
	public SessionChangeEvt(ZonedDateTime creation, LocalDate sessionDate, SessionStateEnum sessionState) {
		this(creation, sessionDate, null, null, sessionState);
	}
	
	public SessionChangeEvt(SessionChange sessionChangeEvent) {
		super(sessionChangeEvent.getCreation());
		
		this.sessionRef = sessionChangeEvent.getSessionRef();
		this.eventNumber = sessionChangeEvent.getEventNumber();
		this.raceNumber = sessionChangeEvent.getRaceNumber();
		this.sessionState = sessionChangeEvent.getSessionState();
	}

	/**
	 * Getter du nouvel état de la session
	 * 
	 * @return le nouvel état de la session
	 */
	public SessionStateEnum getSessionState() {
		return sessionState;
	}
	
	/**
	 * @return the requestedSessionDate
	 */
	public SessionRef getSessionRef() {
		return sessionRef;
	}

	/**
	 * @return the eventNumber
	 */
	public Integer getEventNumber() {
		return eventNumber;
	}

	/**
	 * @return the raceNumber
	 */
	public Integer getRaceNumber() {
		return raceNumber;
	}
	
	@Override
	public String toJson() {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("chronoType", getChronoType());
			if (getSessionRef() != null && getSessionRef().getEventDate() != null) {
				builder.add("sessionEventDate", getSessionRef().getEventDate().toString());
			}
			if (getSessionRef() != null && getSessionRef().getId() != null) {
				builder.add("sessionId", getSessionRef().getId());
			}
			builder.add("eventNumber", getEventNumber()!=null?getEventNumber():-1);
			builder.add("raceNumber", getRaceNumber()!=null?getRaceNumber():-1);
			builder.add("sessionState", getSessionState().name());
			
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}

}

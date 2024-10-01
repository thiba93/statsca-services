package com.carrus.statsca.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciateList;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pmc.club.entity.Session;

/**
 * Objet de transport de session calqué sur l'entité Session
 * 
 * @author BT - ARTSYS 2021
 * @since 16 septembre 2021
 */
@AutoFillFrom(value = Session.class, fillerPath = "session")
public class SessionDTO implements Serializable {
	/** Numéro de version de sérialisation par défaut */
	private static final long serialVersionUID = 1L;

	/** Identifiant de la session */
	private int sessionId;

	/** Date absolue de la session */
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate sessionDate;
	
	/** Liste des dates des sessions liées en prévente */
	@JsonDeserialize(contentUsing = LocalDateDeserializer.class)
	@JsonSerialize(contentUsing = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonInclude(Include.NON_NULL)
	private List<LocalDate> preSaleSessions;

	/** Liste des réunions de la session */
	private List<EventDTO> events;
	
	@JsonInclude(Include.NON_NULL)
	private Boolean remarkableDay;

	/** Constructeur par défaut pour la désérialisation */
	public SessionDTO() {}

	/** Constructeur par copie à partir d'une session, piloté par le profil */
	public SessionDTO(Session session) {
		AutoFillerEngine.autoFill(this, session);
	}

	public SessionDTO(LocalDate date) {
		this.sessionDate = date;
	}

	/**
	 * @return the sessionId
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	@AutoCopy("getId")
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the sessionDate
	 */
	public LocalDate getSessionDate() {
		return sessionDate;
	}

	/**
	 * @param sessionDate the sessionDate to set
	 */
	@AutoCopy("getDate")
	public void setSessionDate(LocalDate sessionDate) {
		this.sessionDate = sessionDate;
	}

	/**
	 * @return the preSaleSessions
	 */
	public List<LocalDate> getPreSaleSessions() {
		return preSaleSessions;
	}

	/**
	 * @param preSaleSessions the preSaleSessions to set
	 */
	@AutoCopy("getLinkedSessions")
	public void setPreSaleSessions(List<LocalDate> preSaleSessions) {
		this.preSaleSessions = preSaleSessions;
	}

	/**
	 * @return the events
	 */
	public List<EventDTO> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	@AutoInstanciateList(value = "getEvents", activated = true)
	public void setEvents(List<EventDTO> events) {
		this.events = events;
	}

	public Boolean getRemarkableDay() {
		return remarkableDay;
	}

	public void setRemarkableDay(Boolean remarkableDay) {
		this.remarkableDay = remarkableDay;
	}

}
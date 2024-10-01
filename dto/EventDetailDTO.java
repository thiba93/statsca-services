package com.carrus.statsca.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.pmc.club.entity.CircumstanceEnum;
import com.pmc.club.entity.Event;

/**
 * Objet de transport Event calqué sur l'entité Event
 * 
 * @author BT - ARTSYS 2016
 * @since 16 septembre 2016
 */
@AutoFillFrom(value = Event.class, fillerPath = "eventDetail")
public class EventDetailDTO implements Serializable {
	
	/**clé primaire de l'objet dans la base club*/
	private Long pk;
	


	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	/** Numéro de version de sérialisation par défaut */
	private static final long serialVersionUID = 1L;

	/** Identifiant de la source de l'information */
	@JsonInclude(Include.NON_NULL)
	private String sourceId = "";
	/** Clé unique identifiante de l'hippodrome */
	@JsonInclude(Include.NON_NULL)
	private String racetrackKey = "";
	/** Nom de la société de course qui gère l'évènement */
	@JsonInclude(Include.NON_NULL)
	private String racingSociety;
	/** Heure précise de départ de la course */
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@JsonInclude(Include.NON_NULL)
	private ZonedDateTime startTime;
	/** Circonstance DIURNE / NOCTURNE de la course */
	@JsonInclude(Include.NON_NULL)
	private CircumstanceEnum eventCircumstance;
	/** TODO Audience ? à définir */
	@JsonInclude(Include.NON_NULL)
	private String audience = "";
	/** TODO eventSpeciality ? à définir Pour le moment on mets la catégorie de course */
	@JsonInclude(Include.NON_NULL)
	private String eventSpeciality;

	/** Constructeur par défaut pour la désérialisation */
	public EventDetailDTO() {}

	/** Constructeur par copie */
	public EventDetailDTO(Event event) {
		AutoFillerEngine.autoFill(this, event);
		// TODO ajouter le code opérateur aux racetracks - this.racetrackKey = event.getRaceTrack().
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the racetrackKey
	 */
	public String getRacetrackKey() {
		return racetrackKey;
	}

	/**
	 * @param racetrackKey the racetrackKey to set
	 */
	public void setRacetrackKey(String racetrackKey) {
		this.racetrackKey = racetrackKey;
	}

	/**
	 * @return the racingSociety
	 */
	public String getRacingSociety() {
		return racingSociety;
	}

	/**
	 * @param racingSociety the racingSociety to set
	 */
	@AutoCopy("getRacingSociety")
	public void setRacingSociety(String racingSociety) {
		this.racingSociety = racingSociety;
	}

	/**
	 * @return the startTime
	 */
	public ZonedDateTime getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	@AutoCopy("getStart")
	public void setStartTime(ZonedDateTime startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the eventCircumstance
	 */
	public CircumstanceEnum getEventCircumstance() {
		return eventCircumstance;
	}

	/**
	 * @param eventCircumstance the eventCircumstance to set
	 */
	@AutoCopy("getCircumstance")
	public void setEventCircumstance(CircumstanceEnum eventCircumstance) {
		this.eventCircumstance = eventCircumstance;
	}

	/**
	 * @return the audience
	 */
	public String getAudience() {
		return audience;
	}

	/**
	 * @param audience the audience to set
	 */
	public void setAudience(String audience) {
		this.audience = audience;
	}

	/**
	 * @return the eventSpeciality
	 */
	public String getEventSpeciality() {
		return eventSpeciality;
	}

	/**
	 * @param eventSpeciality the eventSpeciality to set
	 */
	@AutoCopy(value = "getRaceCategory", chain = "name")
	public void setEventSpeciality(String eventSpeciality) {
		this.eventSpeciality = eventSpeciality;
	}
	
}
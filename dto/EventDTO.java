package com.carrus.statsca.dto;

import java.io.Serializable;
import java.util.List;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.carrus.statsca.dynaautofiller.AutoInstanciateList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.Event;

/**
 * Objet de transport Event calqué sur l'entité Event
 * 
 * @author BT - ARTSYS 2021
 * @since 1.0.0 (16 septembre 2021)
 */
@AutoFillFrom(value = Event.class, fillerPath = "event")
public class EventDTO implements Serializable, Comparable<EventDTO> {
	
	/**clé primaire de l'objet dans la base club*/
	@JsonInclude(Include.NON_NULL)
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
	
	/** Identifiant externe de l'évènement (Numéro de la réunion dans la journée) */
	private int id;
	/** Nom de l'évènement */
	@JsonInclude(Include.NON_NULL)
	private String name;
	/** Hippodrome sur lequel se déroule l'évènement */
	@JsonInclude(Include.NON_NULL)
	private RacetrackDTO racetrack;
	/** Ensemble des courses que composent cet évènement */
	private List<RaceDTO> races;
	@JsonInclude(Include.NON_NULL)
	private Boolean remarkableEvent;
	/** Détails éventuels de la réunion */
	//@JsonInclude(Include.NON_NULL)
	//private EventDetailDTO eventDetail;

	/** Constructeur par défaut pour la désérialisation */
	public EventDTO() {}

	/** Constructeur par copie à partir d'une réunion, piloté par le profil */
	public EventDTO(Event event) {
		AutoFillerEngine.autoFill(this, event);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@AutoCopy("getId")
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@AutoCopy("getName")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the racetrack
	 */
	public RacetrackDTO getRacetrack() {
		return racetrack;
	}

	/**
	 * @param racetrack the racetrack to set
	 */
	@AutoInstanciate("getRaceTrack")
	public void setRacetrack(RacetrackDTO racetrack) {
		this.racetrack = racetrack;
	}

	/**
	 * @return the races
	 */
	public List<RaceDTO> getRaces() {
		return races;
	}

	/**
	 * @param races the races to set
	 */
	@AutoInstanciateList(value = "getRaces", activated = true)
	public void setRaces(List<RaceDTO> races) {
		this.races = races;
	}

//	/**
//	 * @return the eventDetail
//	 */
//	public EventDetailDTO getEventDetail() {
//		return eventDetail;
//	}
//
//	/**
//	 * @param eventDetail the eventDetail to set
//	 */
//	@AutoInstanciate(activated = false)
//	public void setEventDetail(EventDetailDTO eventDetail) {
//		this.eventDetail = eventDetail;
//	}

	@Override
	public int compareTo(EventDTO o) {
		return Integer.compare(this.getId(), o.getId());
	}

	public Boolean getRemarkableEvent() {
		return remarkableEvent;
	}

	public void setRemarkableEvent(Boolean remarkableEvent) {
		this.remarkableEvent = remarkableEvent;
	}

}
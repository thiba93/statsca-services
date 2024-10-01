package com.carrus.statsca.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pmc.club.entity.Event;

/**
 * Référence externe à une réunion
 * 
 */
public class EventRefDTO {
	/** Date de la réunion */
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    /** Numéro de la réunion dans la session */
    private Integer eventId;

    /** Constructeur par défaut, utile pour la dé/sérialisation */
    public EventRefDTO() {}
    
    /** Constructeur à partir des propriétés individuelles */
    public EventRefDTO(LocalDate eventDate, Integer eventId) {
    	this.eventDate = eventDate;
    	this.eventId = eventId;
    }
	
    /** Constructeur à partir d'une course au format MutuelService */
    public EventRefDTO(final Event event) {
    	this.eventDate = event.getDate();
    	this.eventId = event.getId();
    }
    
	/**
	 * @return the eventDate
	 */
	public LocalDate getEventDate() {
		return eventDate;
	}
	
	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}
	
	/**
	 * @return the eventId
	 */
	public Integer getEventId() {
		return eventId;
	}
	
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

    @Override
	public String toString() {
		return "EventRef [eventDate=" + eventDate + ", eventId=" + eventId + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventDate, eventId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof EventRefDTO)) {
			return false;
		}
		EventRefDTO other = (EventRefDTO) obj;
		return Objects.equals(eventDate, other.eventDate) && Objects.equals(eventId, other.eventId);
	}

}
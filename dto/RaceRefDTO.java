package com.carrus.statsca.dto;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.Race;
import com.pmc.club.references.RaceRef;

/**
 * Référence externe à une course
 * 
 */
public class RaceRefDTO extends EventRefDTO {
    /** Numéro de la course dans la réunion */
    private Integer raceNumber;
    
    /** clé publique côté couche service **/
    @JsonInclude(Include.NON_NULL)
    private Long racePk;

    /** Constructeur par défaut, utile pour la dé/sérialisation */
    public RaceRefDTO() {}
    
    /** Constructeur à partir des propriétés individuelles */
    public RaceRefDTO(LocalDate eventDate, Integer eventId, Integer raceNumber) {
    	super(eventDate, eventId);

    	this.raceNumber = raceNumber;
    }
    
    public RaceRefDTO(final RaceRef raceRef) {
    	super(raceRef.getEventDate(), raceRef.getEventId());

    	this.raceNumber = raceRef.getRaceNumber();
    }
	
    /** Constructeur à partir d'une course au format MutuelService */
    public RaceRefDTO(final Race race) {
    	super(race.getEvent());

    	this.raceNumber = race.getNumber();
    }
	
	/**
	 * @return the raceNumber
	 */
	public Integer getRaceNumber() {
		return raceNumber;
	}
	
	/**
	 * @param raceNumber the raceNumber to set
	 */
	public void setRaceNumber(Integer raceNumber) {
		this.raceNumber = raceNumber;
	}

    public Long getRacePk() {
		return racePk;
	}

	public void setRacePk(Long racePk) {
		this.racePk = racePk;
	}

	@Override
	public String toString() {
		return "RaceRef [eventDate=" + getEventDate() + ", eventId=" + getEventId() + ", raceNumber=" + raceNumber + "]";
	}

	/**
	 * Méthode qui permet la conversion en RaceRef de l'objet de laa classe dérivée de RaceRef
	 * 
	 * @return RaceRef correspondante de l'objet de la classe dérivée de RaceRef 
	 */
	public RaceRefDTO toRaceRef() {
		return new RaceRefDTO(this.getEventDate(), this.getEventId(), this.getRaceNumber());
	}
	
	/** Méthode utilitaire interne de construction d'une RaceRef à partir d'une race */
	public static final RaceRefDTO from(Race race) {
		return new RaceRefDTO(race);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(raceNumber);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RaceRefDTO)) {
			return false;
		}
		RaceRefDTO other = (RaceRefDTO) obj;
		return Objects.equals(raceNumber, other.raceNumber) && Objects.equals(this.getEventDate(), other.getEventDate()) && Objects.equals(this.getEventId(), other.getEventId());
	}
}
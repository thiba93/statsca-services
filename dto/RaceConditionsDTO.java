package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.pmc.club.entity.RaceConditions;
import com.pmc.club.entity.enums.GenderEnum;

/**
 * Objet de transport des conditions de participation à la course
 * 
 * @author BT - ARTSYS 2021
 * @since 21 septembre 2021
 */
@AutoFillFrom(value = RaceConditions.class, fillerPath = "raceConditions")
public class RaceConditionsDTO {
	/** Description générale des conditions de la course */
	private String description;
	/** Description des conditions du jockey */
	private String rider;
	/** Type de participants */
	private GenderEnum genderCode;
	/** Conditions sur l'age */
	private AgeCriteriaDTO ageCriteria;
	/** Conditions de races des chevaux sous format textuel */
	private String breed;
	
	/** Constructeur à partir d'une entité RaceCondition */
	public RaceConditionsDTO(RaceConditions conditions) {
		this.description = conditions.getDescription();
		this.rider = conditions.getRider();
		this.ageCriteria = new AgeCriteriaDTO(conditions.getAgeCriteria());
		this.genderCode = conditions.getGender();
		this.breed = conditions.getBreed();
		
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	@AutoCopy(value = "getDescription")
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the rider
	 */
	public String getRider() {
		return rider;
	}
	
	/**
	 * @param rider the rider to set
	 */
	@AutoCopy(value = "getRider")
	public void setRider(String rider) {
		this.rider = rider;
	}
	
	/**
	 * @return the genderCode
	 */
	public GenderEnum getGenderCode() {
		return genderCode;
	}
	
	/**
	 * @param gendersCode the gendersCode to set
	 */
	@AutoCopy(value = "getGender")
	public void setGenderCode(GenderEnum genderCode) {
		this.genderCode = genderCode;
	}
	
	/**
	 * @return the breed
	 */
	public String getBreed() {
		return breed;
	}
	
	/**
	 * @param breed the breed to set
	 */
	@AutoCopy(value = "getBreed")
	public void setBreed(String breed) {
		this.breed = breed;
	}
	
	/**
	 * @return the ageCriteria
	 */
	public AgeCriteriaDTO getAgeCriteria() {
		return ageCriteria;
	}
	
	/**
	 * @param ageCriteria the ageCriteria to set
	 */
	@AutoInstanciate(value = "getAgeCriteria", useProfil = false)
	public void setAgeCriteria(AgeCriteriaDTO ageCriteria) {
		this.ageCriteria = ageCriteria;
	}   
}

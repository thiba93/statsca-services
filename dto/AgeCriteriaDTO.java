package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.pmc.club.entity.AgeCriteria;
import com.pmc.club.entity.enums.AgeCriteriaEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AgeCriteriaDTO Conditions de participation à la course par rapport
 * à l'age des participants
 * 
 * @author BT - ARTSYS 2021
 * @since 21 septembre 2021
 */
@Schema(name = "Horses_age_criteria",
		description = "Contains all information needed to filter the race")
@AutoFillFrom(value = AgeCriteria.class, fillerPath = "ageCriteria")
public class AgeCriteriaDTO {
	/** Selecteur des critères d'age */
	@Schema(name = "Criteria selector", description = "no age criteria, age min, age max, or age range")
	private AgeCriteriaEnum criteria;
	/** Age minimum */
	@Schema(name = "minimum age", description = "no age criteria, age min, age max, or age range")
	private Byte min;
	/** Age maximum */
	@Schema(name = "maximum age")
	private Byte max;
	
	/** Constructeur à partir de l'entité AgeCriteria */
	public AgeCriteriaDTO(AgeCriteria criteria) {
		this.criteria = criteria.getCriteria();
		this.min = criteria.getMin();
		this.max = criteria.getMax();
	}
	
	/**
	 * @return the criteria
	 */
	public AgeCriteriaEnum getCriteria() {
		return criteria;
	}
	
	/**
	 * @param criteria the criteria to set
	 */
	@AutoCopy("getCriteria")
	public void setCriteria(AgeCriteriaEnum criteria) {
		this.criteria = criteria;
	}
	
	/**
	 * @return the min
	 */
	public Byte getMin() {
		return min;
	}
	
	/**
	 * @param min the min to set
	 */
	@AutoCopy("getMin")
	public void setMin(Byte min) {
		this.min = min;
	}
	
	/**
	 * @return the max
	 */
	public Byte getMax() {
		return max;
	}
	
	/**
	 * @param max the max to set
	 */
	@AutoCopy("getMax")
	public void setMax(Byte max) {
		this.max = max;
	}
}

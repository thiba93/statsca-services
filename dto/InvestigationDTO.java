package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.pmc.club.entity.Investigation;
import com.pmc.club.entity.enums.InvestigationState;

/**
 * Sous-structure invariante de la course qui contient les informations des prix sur cette course
 * 
 * @author BT - ARTSYS 2021
 * @since 22 septembre 2021
 */
@AutoFillFrom(value = Investigation.class, fillerPath = "investigation")
public class InvestigationDTO {
	/** Libellé de l'investigation */
	private String label;
	/** Etat de l'investigation */
	private InvestigationState state;
	
	public InvestigationDTO(Investigation inv) {
		AutoFillerEngine.autoFill(this, inv);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	@AutoCopy("getLabel")
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the state
	 */
	public InvestigationState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	@AutoCopy("getState")
	public void setState(InvestigationState state) {
		this.state = state;
	}

}

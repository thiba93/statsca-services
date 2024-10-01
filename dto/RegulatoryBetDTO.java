package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.pmc.club.entity.AuthorisedFormulation;
import com.pmc.club.entity.BetAbstract;

@AutoFillFrom(value = BetAbstract.class, fillerPath = "betType")
public class RegulatoryBetDTO {
	
	
	public RegulatoryBetDTO(AuthorisedFormulation authorisedFormulation) {
		this.name = authorisedFormulation.getBetType() != null?authorisedFormulation.getBetType().getName():"";
		this.longName = authorisedFormulation.getBetType() != null?authorisedFormulation.getBetType().getLongName():"";
	}

	private String name;
	
	
	private String longName;

	
	public String getName() {
		return name;
	}

	public RegulatoryBetDTO(String name, String longName) {
		super();
		this.name = name;
		this.longName = longName;
	}

	@AutoCopy("getName")
	public void setName(String name) {
		this.name = name;
	}


	public String getLongName() {
		return longName;
	}

	@AutoCopy("getLongName")
	public void setLongName(String longName) {
		this.longName = longName;
	}
}

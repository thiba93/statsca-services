package com.carrus.statsca.admin.dto;

import java.util.List;

public class GrandPrizeDTO {
	
	private Integer grandPrizeID;
	
	private String grandPrizeName;
	
	private List<GrandPrizeRaceDTO> grandPrizeRaces;

	public Integer getGrandPrizeID() {
		return grandPrizeID;
	}

	public void setGrandPrizeID(Integer grandPrizeID) {
		this.grandPrizeID = grandPrizeID;
	}

	public String getGrandPrizeName() {
		return grandPrizeName;
	}

	public void setGrandPrizeName(String grandPrizeName) {
		this.grandPrizeName = grandPrizeName;
	}

	public List<GrandPrizeRaceDTO> getGrandPrizeRaces() {
		return grandPrizeRaces;
	}

	public void setGrandPrizeRaces(List<GrandPrizeRaceDTO> grandPrizeRaces) {
		this.grandPrizeRaces = grandPrizeRaces;
	}
	
	
}

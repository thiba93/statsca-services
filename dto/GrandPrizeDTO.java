package com.carrus.statsca.dto;

import java.io.Serializable;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.GrandPrize;

@AutoFillFrom(value = GrandPrize.class, fillerPath = "grandPrize")
public class GrandPrizeDTO implements Serializable {
	private static final long serialVersionUID = 7505691132426243317L;
	
	@JsonInclude(Include.NON_NULL)
	private Long pk;
	
	@JsonInclude(Include.NON_NULL)
	private String name;
	
	//@JsonInclude(Include.NON_NULL)
	//private int id;
	
	@JsonInclude(Include.NON_NULL)
	private RacetrackDTO raceTrack;
	

	public GrandPrizeDTO() {}
	
	public GrandPrizeDTO(GrandPrize gp) {
		AutoFillerEngine.autoFill(this, gp);
	}
	
	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	public String getName() {
		return name;
	}

	@AutoCopy("getName")
	public void setName(String name) {
		this.name = name;
	}
	

	public RacetrackDTO getRaceTrack() {
		return raceTrack;
	}

	@AutoInstanciate("getRaceTrack")
	public void setRaceTrack(RacetrackDTO raceTrack) {
		this.raceTrack = raceTrack;
	}
}

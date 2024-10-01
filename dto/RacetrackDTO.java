package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.RaceTrack;

/**
 * DataTransfertObject d'un hippodrome
 * 
 * @author BT - ARTSYS 2021
 * @since 20 septembre 2021
 */
@AutoFillFrom(value = RaceTrack.class, fillerPath = "racetrack")
public class RacetrackDTO {
	
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
	
	/** Identifiant externe, trigramme */
	private String id;

	/** Nom de l'hippodrome */
	private String name;
	
	/** Constructeur par défaut */
	public RacetrackDTO() {}
	
	/** Constructeur à partir d'une entité RaceTrack */
	public RacetrackDTO(RaceTrack raceTrack) {
		AutoFillerEngine.autoFill(this, raceTrack);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@AutoCopy("getExternalId")
	public void setId(String id) {
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
}

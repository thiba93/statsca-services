package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.pmc.club.entity.partner.Organization;

@AutoFillFrom(value = Organization.class, fillerPath = "organization")
public class OrganizationDTO {

	/** Constructeur par défaut */
	public OrganizationDTO() {}

	/** Constructeur par copie */
	public OrganizationDTO(Organization organization) {
		AutoFillerEngine.autoFill(this, organization);
	}
	
	
	private Long pk;

	/**
	 * libélée court de l'organisation
	 */
	private String shortName;

	/**
	 * libélée long de l'organisation 
	 */
	private String name;

	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}

	public String getShortName() {
		return shortName;
	}

	@AutoCopy("getShortName")
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	@AutoCopy("getName")
	public void setName(String name) {
		this.name = name;
	}

}

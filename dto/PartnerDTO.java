package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.CurrencyEnum;
import com.pmc.club.entity.partner.Organization;
import com.pmc.club.entity.partner.Partner;

@AutoFillFrom(value = Partner.class, fillerPath = "partner")
public class PartnerDTO {

	/** Constructeur par défaut */
	public PartnerDTO() {}

	/** Constructeur par copie */
	public PartnerDTO(Partner partner) {
		AutoFillerEngine.autoFill(this, partner);
	}
	
	@JsonInclude(Include.NON_NULL)
	private Long pk;
	
	private int partnerId;

	@JsonInclude(Include.NON_NULL)
	private String shortName;

	private String name;
	
	@JsonInclude(Include.NON_NULL)
	private CurrencyEnum currency;
	
	@JsonInclude(Include.NON_NULL)
	private OrganizationDTO organization;
	
	@JsonInclude(Include.NON_NULL)
	private String country;

	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}

	public int getPartnerId() {
		return partnerId;
	}

	@AutoCopy("getPartnerId")
	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
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

	
	public CurrencyEnum getCurrency() {
		return currency;
	}


	@AutoCopy("getCurrency")
	public void setCurrency(CurrencyEnum currency) {
		this.currency = currency;
	}

	public OrganizationDTO getOrganization() {
		return organization;
	}
	
	@AutoCopy("getCountry")
	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}


	@AutoInstanciate(value="getOrganization", caster=Organization.class)
	public void setOrganization(OrganizationDTO organization) {
		this.organization = organization;
	}
}

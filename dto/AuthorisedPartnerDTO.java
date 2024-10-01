package com.carrus.statsca.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.carrus.statsca.dynaautofiller.AutoInstanciateList;
import com.carrus.statsca.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.pmc.club.entity.partner.AuthorisedPartner;
import com.pmc.club.entity.partner.Partner;

@AutoFillFrom(value = AuthorisedPartner.class, fillerPath = "authorizedPartner")
public class AuthorisedPartnerDTO {
	
	/** Constructeur par défaut */
	public AuthorisedPartnerDTO() {}

	/** Constructeur par copie */
	public AuthorisedPartnerDTO(AuthorisedPartner authorisedPartner) {
		AutoFillerEngine.autoFill(this, authorisedPartner);
	}
	
	/** 
	 * Identifiant interne de l'objet en base de données 
	 */
	@JsonInclude(Include.NON_NULL)
	private Long pk;
	
	/**
	 * montant des enjeux de l'attributaire sur la course associée
	 */
	@JsonInclude(Include.NON_NULL)
	private BigDecimal stake;

	/**
	 * Date et heure de la dernière mise à jour de l'attributaire
	 */
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@JsonInclude(Include.NON_NULL)
	private ZonedDateTime updateDate;

	/**
	 * liste des enjeux par pari
	 */
	@JsonInclude(Include.NON_NULL)
	private List<FormulationStakeDTO> formulationStakes;

	/**
	 * Attributaire associé à l'enjeu
	 */
	private PartnerDTO partner;
	
	public PartnerDTO getPartner() {
		return partner;
	}

	@AutoInstanciate(value="getPartner", caster=Partner.class)
	public void setPartner(PartnerDTO partner) {
		this.partner = partner;
	}

	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}

	public BigDecimal getStake() {
		return stake;
	}

	@AutoCopy("getStake")
	public void setStake(BigDecimal stake) {
		this.stake = stake;
	}

	public ZonedDateTime getUpdateDate() {
		return updateDate;
	}

	@AutoCopy("getUpdateDate")
	public void setUpdateDate(ZonedDateTime updateDate) {
		this.updateDate = updateDate;
	}

	public List<FormulationStakeDTO> getFormulationStakes() {
		return formulationStakes;
	}

	@AutoInstanciateList(value = "getFormulationStakes")
	public void setFormulationStakes(List<FormulationStakeDTO> betStakes) {
		this.formulationStakes = betStakes;
	}
}

package com.carrus.statsca.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.pmc.club.entity.recipe.BetCodeRecipe;
import com.pmc.club.entity.recipe.FormulationStake;


@AutoFillFrom(value = FormulationStake.class, fillerPath = "betStake")
public class FormulationStakeDTO {


	
	/** Constructeur par défaut */
	public FormulationStakeDTO() {}

	/** Constructeur par copie */
	public FormulationStakeDTO(FormulationStake formulationStake) {
		AutoFillerEngine.autoFill(this, formulationStake);
		//affectation des champs impossible à passer en autoFill
		this.betCodeRef = -1;
		if(formulationStake.getOffer()!=null && formulationStake.getOffer().getBetType()!=null)
		{
			this.betCodeRef = formulationStake.getOffer().getBetType().getCode();
		}
		if(formulationStake.getAuthorisedpartner()!=null && 
				formulationStake.getAuthorisedpartner().getRace()!=null && 
				formulationStake.getAuthorisedpartner().getRace().getEvent()!=null && 
				formulationStake.getAuthorisedpartner().getPartner()!=null)
		{
			int id = formulationStake.getAuthorisedpartner().getRace().getEvent().getId();
			int number = formulationStake.getAuthorisedpartner().getRace().getNumber();
			LocalDate date = formulationStake.getAuthorisedpartner().getRace().getEvent().getDate();
			int partnerId = formulationStake.getAuthorisedpartner().getPartner().getPartnerId();
			this.raceRef = new RaceRefDTO(date, id, number);
			this.contractor = partnerId;
		}
	}
	
	public FormulationStakeDTO(BetCodeRecipe betCodeRecipe) {
		this.stake = BigDecimal.valueOf(betCodeRecipe.getCashAmount());
		this.betCodeRef = betCodeRecipe.getBetCodeRef();
		this.raceRef = new RaceRefDTO(betCodeRecipe.getRaceRef());
		this.contractor = betCodeRecipe.getContractor();
		this.updateDate = ZonedDateTime.ofInstant(betCodeRecipe.getComputedTime().toInstant(), ZoneId.systemDefault());    //from(betCodeRecipe.getComputedTime())
	}
	
	/** 
	 * Identifiant interne de l'objet en base de données 
	 */
	@JsonInclude(Include.NON_NULL)
	private Long pk;
	
	/**
	 *montant des enjeux
	 */
	private BigDecimal stake;
	
	/**
	 * identifiant Star 3000 du pari
	 */
	private int betCodeRef; 
	
	/**
	 * identifiant de l'attributaire
	 */
	private int contractor;
	
	/**
	 * reférence de course
	 */
	@JsonInclude(Include.NON_NULL)
	private RaceRefDTO raceRef;
	
	/**
	 * date de la dernière mise à jour des enjeux
	 */
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@JsonInclude(Include.NON_NULL)
	private ZonedDateTime updateDate;

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

	public int getBetCodeRef() {
		return betCodeRef;
	}

	public void setBetCodeRef(int betCodeRef) {
		this.betCodeRef = betCodeRef;
	}

	public RaceRefDTO getRaceRef() {
		return raceRef;
	}

	public void setRaceRef(RaceRefDTO raceRef) {
		this.raceRef = raceRef;
	}

	public int getContractor() {
		return contractor;
	}

	public void setContractor(int contractor) {
		this.contractor = contractor;
	}
}

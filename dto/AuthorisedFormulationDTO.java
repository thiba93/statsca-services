package com.carrus.statsca.dto;

import java.math.BigDecimal;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.AuthorisedFormulation;
import com.pmc.club.entity.BetState;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classe de transport des données utiles relatives à l'entité AuthorisedFormulation
 * de la couche service de l'application.
 * Classe invariante car utilisée uniquement pour le transport du serveur vers le client. 
 * 
 * @author BT - ARTSYS 2018
 * @since 9 mai 2018
 */
@AutoFillFrom(value = AuthorisedFormulation.class, fillerPath = "formulation")
public class AuthorisedFormulationDTO {
	
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
	
	/** Le pari correspondant */
	@Schema(description = "Bet code identifier as an integer", nullable = false)
	private int betCodeRef;
	/** Etat du pari */
	@JsonInclude(Include.NON_NULL)
	private BetState stateCode;
	/** Indique si un pari est en remboursement */
	@JsonInclude(Include.NON_NULL)
	private boolean payback;
	/** Liste des rapports (dividends) pour cette formulation */
//	@JsonInclude(Include.NON_EMPTY)
//	private List<DividendDTO> dividends;
//	/** Liste des courses liées dans le cas des paris  */
//	@JsonInclude(Include.NON_EMPTY)
//	private List<RaceRef> linkedRaces;
//	/** Détail éventuel de la formulation */
//	@JsonInclude(Include.NON_NULL)
//	private FormulationDetailDTO authorisedFormulationDetails;
	
//	/** Montant unitaire des enjeux sur lequel sont calculés les rapports */
//	@JsonInclude(Include.NON_NULL)
//	private Double stackAmountUnit;
	
	/** Masse des enjeux sur ce pari */
	@JsonInclude(Include.NON_NULL)
	private BigDecimal stake;
	
	/**	Infos sur le type de pari */
	private RegulatoryBetDTO betType;

	/**
	 * Constructeur par copie à partir d'une entitée AuthorisedFormulation de la couche service
	 * Un booléen permet de préciser si l'on désire le détail des formulations ou pas
	 * 
	 * @param authorisedFormulation Formulation autorisée au format MutuelService
	 */
	public AuthorisedFormulationDTO(AuthorisedFormulation authorisedFormulation) {
		AutoFillerEngine.autoFill(this, authorisedFormulation);
	}

	public AuthorisedFormulationDTO() {
	}

	/**
	 * @return the betCodeRef
	 */
	public int getBetCodeRef() {
		return betCodeRef;
	}

	/**
	 * @param betCodeRef the betCodeRef to set
	 */
	@AutoCopy(value = "getBetType", chain = "getCode")
	public void setBetCodeRef(int betCodeRef) {
		this.betCodeRef = betCodeRef;
	}

	/**
	 * @return the stateCode
	 */
	public BetState getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode the stateCode to set
	 */
	@AutoCopy("getState")
	public void setStateCode(BetState stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the payback
	 */
	public boolean isPayback() {
		return payback;
	}

	/**
	 * @param payback the payback to set
	 */
	@AutoCopy("getPayback")
	public void setPayback(boolean payback) {
		this.payback = payback;
	}

//	/**
//	 * @return the dividends
//	 */
//	public List<DividendDTO> getDividends() {
//		return dividends;
//	}
//
//	/**
//	 * @param dividends the dividends to set
//	 */
//	@AutoInstanciateList(value = "getDividends", useProfil = false, emptyRatherNull = true)
//	public void setDividends(List<DividendDTO> dividends) {
//		this.dividends = dividends;
//	}
//
//	/**
//	 * @return the linkedRaces
//	 */
//	public List<RaceRef> getLinkedRaces() {
//		return linkedRaces;
//	}
//
//	/**
//	 * @param linkedRaces the linkedRaces to set
//	 */
//	@AutoInstanciateList(value = "getLinkedRaces", chain = "getRace", useProfil = false, emptyRatherNull = true)
//	public void setLinkedRaces(List<RaceRef> linkedRaces) {
//		this.linkedRaces = linkedRaces;
//	}

//	/**
//	 * @return the authorisedFormulationDetails
//	 */
//	public FormulationDetailDTO getAuthorisedFormulationDetails() {
//		return authorisedFormulationDetails;
//	}
//
//	/**
//	 * @param authorisedFormulationDetails the authorisedFormulationDetails to set
//	 */
//	@AutoInstanciate(activated = true)
//	public void setAuthorisedFormulationDetails(FormulationDetailDTO authorisedFormulationDetails) {
//		this.authorisedFormulationDetails = authorisedFormulationDetails;
//	}

	public RegulatoryBetDTO getBetType() {
		return betType;
	}

	/**
	 * @param authorisedFormulationDetails the authorisedFormulationDetails to set
	 */
	@AutoInstanciate(activated = true)
	public void setBetType(RegulatoryBetDTO betType) {
		this.betType = betType;
	}

//	public Double getStackAmountUnit() {
//		return stackAmountUnit;
//	}
//
//	@AutoCopy("getStackAmountUnit")
//	public void setStackAmountUnit(Double stackAmountUnit) {
//		this.stackAmountUnit = stackAmountUnit;
//	}

	public BigDecimal getStake() {
		return stake;
	}

	@AutoCopy("getStake")
	public void setStake(BigDecimal stake) {
		this.stake = stake;
	}

}

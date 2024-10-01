package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pmc.club.entity.AuthorisedFormulation;
import com.pmc.club.entity.FinancialAttributes;
import com.pmc.club.entity.OriginEnum;

/**
 * Classe de transport des données utiles relatives à l'entité AuthorisedFormulation
 * de la couche service de l'application.
 * Classe invariante car utilisée uniquement pour le transport du serveur vers le client. 
 * 
 * @author BT - ARTSYS 2021
 * @since 30 septembre 2021
 */
@AutoFillFrom(value = AuthorisedFormulation.class, fillerPath = "authorisedFormulationDetails")
public class FormulationDetailDTO {

	/**clé primaire de l'objet dans la base club*/
	private Long pk;
	


	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	/** Masse des enjeux sur ce pari */
	private Double grosspool;

	/**
	 * Permet de savoir si les chevaux de compléments sont disponibles pour
	 * toutes les formules concernées du pari
	 */
	private boolean replacementAvailable;

	/**
	 * Permet de savoir si les tirages éclair/spot sont disponibles pour toutes
	 * les formules concernées du pari
	 */
	private boolean quickPickAvailable;
	
	/** Montant unitaire des enjeux sur lequel sont calculés les rapports */
	private Double stackAmountUnit;
	
	/** Montant minimum garantie des mises sur ce pari */
	@JsonInclude(Include.NON_NULL)
	private Double grossPoolGuarantee;
	
	/** PMU booster */
	@JsonInclude(Include.NON_NULL)
	private Double pmuBooster;

	/**
	 * Bonus gagnant associé à cette formulation de pari.
	 * N'est renseigné que lorsque l'état de la formulation est Payement
	 */
	@JsonInclude(Include.NON_NULL)
	private Integer winningsBonus;

	/** Montant de la tirelire affecté à ce pari, peut être nul */
	@JsonInclude(Include.NON_NULL)
	private Double assignedPot;
	
	/** Montant de la tirelire non remporté et reporté sur un autre pari */
	@JsonInclude(Include.NON_NULL)
	private Double collectedPot;
	
	/** Attributs financiers éventuels */
	@JsonInclude(Include.NON_NULL)
	private FinancialAttributesDTO financialAttributes;

	/** Origine des données du pari */
	private OriginEnum originCode;
	
	/**
	 * Constructeur par copie à partir d'une entitée AuthorisedFormulation de la couche service
	 * Un booléen permet de préciser si l'on désire le détail des formulations ou pas
	 * 
	 * @param authorisedFormulation Formulation autorisée au format MutuelService
	 * @param detail Booléen qui indique si on doit détailler ou pas la formulation.
	 */
	public FormulationDetailDTO(AuthorisedFormulation authorisedFormulation) {
		AutoFillerEngine.autoFill(this, authorisedFormulation);
	}

	/**
	 * @return the grosspool
	 */
	public Double getGrosspool() {
		return grosspool;
	}

	/**
	 * @param grosspool the grosspool to set
	 */
	@AutoCopy("getGrossPool")
	public void setGrosspool(Double grosspool) {
		this.grosspool = grosspool;
	}

	/**
	 * @return the replacementAvailable
	 */
	public boolean isReplacementAvailable() {
		return replacementAvailable;
	}

	/**
	 * @param replacementAvailable the replacementAvailable to set
	 */
	@AutoCopy("isReplacementAvailable")
	public void setReplacementAvailable(boolean replacementAvailable) {
		this.replacementAvailable = replacementAvailable;
	}

	/**
	 * @return the quickPickAvailable
	 */
	public boolean isQuickPickAvailable() {
		return quickPickAvailable;
	}

	/**
	 * @param quickPickAvailable the quickPickAvailable to set
	 */
	@AutoCopy("isQuickPickAvailable")
	public void setQuickPickAvailable(boolean quickPickAvailable) {
		this.quickPickAvailable = quickPickAvailable;
	}

	/**
	 * @return the stackAmountUnit
	 */
	public Double getStackAmountUnit() {
		return stackAmountUnit;
	}

	/**
	 * @param stackAmountUnit the stackAmountUnit to set
	 */
	@AutoCopy("getStackAmountUnit")
	public void setStackAmountUnit(Double stackAmountUnit) {
		this.stackAmountUnit = stackAmountUnit;
	}

	/**
	 * @return the grossPoolGuarantee
	 */
	public Double getGrossPoolGuarantee() {
		return grossPoolGuarantee;
	}

	/**
	 * @param grossPoolGuarantee the grossPoolGuarantee to set
	 */
	@AutoCopy("getGrossPoolGuarantee")
	public void setGrossPoolGuarantee(Double grossPoolGuarantee) {
		this.grossPoolGuarantee = grossPoolGuarantee;
	}

	/**
	 * @return the pmuBooster
	 */
	public Double getPmuBooster() {
		return pmuBooster;
	}

	/**
	 * @param pmuBooster the pmuBooster to set
	 */
	@AutoCopy("getPmuBooster")
	public void setPmuBooster(Double pmuBooster) {
		this.pmuBooster = pmuBooster;
	}

	/**
	 * @return the winningsBonus
	 */
	public Integer getWinningsBonus() {
		return winningsBonus;
	}

	/**
	 * @param winningsBonus the winningsBonus to set
	 */
	@AutoCopy("getWinningsBonus")
	public void setWinningsBonus(Integer winningsBonus) {
		this.winningsBonus = winningsBonus;
	}

	/**
	 * @return the assignedPot
	 */
	public Double getAssignedPot() {
		return assignedPot;
	}

	/**
	 * @param assignedPot the assignedPot to set
	 */
	@AutoCopy("getAssignedPot")
	public void setAssignedPot(Double assignedPot) {
		this.assignedPot = assignedPot;
	}

	/**
	 * @return the collectedPot
	 */
	public Double getCollectedPot() {
		return collectedPot;
	}

	/**
	 * @param collectedPot the collectedPot to set
	 */
	@AutoCopy("getCollectedPot")
	public void setCollectedPot(Double collectedPot) {
		this.collectedPot = collectedPot;
	}

	/**
	 * @return the financialAttributes
	 */
	public FinancialAttributesDTO getFinancialAttributes() {
		return financialAttributes;
	}

	/**
	 * @param financialAttributes the financialAttributes to set
	 */
	@AutoInstanciate(value="getFinancialAttributes", caster=FinancialAttributes.class)
	public void setFinancialAttributes(FinancialAttributesDTO financialAttributes) {
		this.financialAttributes = financialAttributes;
	}

	/**
	 * @return the originCode
	 */
	public OriginEnum getOriginCode() {
		return originCode;
	}

	/**
	 * @param originCode the originCode to set
	 */
	@AutoCopy("getOrigin")
	public void setOriginCode(OriginEnum originCode) {
		this.originCode = originCode;
	}

}

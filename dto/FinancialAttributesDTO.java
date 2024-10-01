package com.carrus.statsca.dto;

import java.math.BigDecimal;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.pmc.club.entity.FinancialAttributes;

/**
 * DTO FinancialAttributes adaptée pour la sérialisation
 * 
 * @author BT - ARTSYS 2021
 * @since 27 septembre 2021
 */
@AutoFillFrom(value = FinancialAttributes.class, fillerPath = "financialAttributes")
public class FinancialAttributesDTO {
	/** Montant par défaut proposé au parieur */
	private BigDecimal defaultAmount;
	/** Montant par défaut du pari proposé au parieur */
	private BigDecimal cashAmountUnit;
	/** Montant réellement engagé dans la masse mutualisée - Peu utilisé */
	private BigDecimal stakeAmountUnit;
	/** Montant minimum qu'un parieur peut engager sur un type de pari lorsque le pari n'est pas fractionné */
	private BigDecimal minimumCashAmount;
	/** Montant maximum qu'un parieur peut engager sur un type de pari */
	private BigDecimal maximumCashAmount;
	/** Enjeux de fractionnement qu'un parieur peut engager sur un type de pari */
	private BigDecimal fractionAmountUnit;
	/** Enjeux de fractionnement minimum qu'un parieur peut réellement engager sur un type de pari */
	private BigDecimal fractionMiniUnit;
	/** Seuil de l'enjeux de fractionnement qu'un parieur peut réellement engager sur un type de pari */
	private BigDecimal fractionThreshold;
	
	/** Constructeur par défaut */
	public FinancialAttributesDTO() {}

	/** Constructeur pour l'autoFillerEngine */
	public FinancialAttributesDTO(FinancialAttributes financialAttributes) {
		AutoFillerEngine.autoFill(this, financialAttributes);
	}

	/**
	 * @return the defaultAmount
	 */
	public BigDecimal getDefaultAmount() {
		return defaultAmount;
	}

	/**
	 * @param defaultAmount the defaultAmount to set
	 */
	@AutoCopy("getDefaultAmount")
	public void setDefaultAmount(BigDecimal defaultAmount) {
		this.defaultAmount = defaultAmount;
	}

	/**
	 * @return the cashAmountUnit
	 */
	public BigDecimal getCashAmountUnit() {
		return cashAmountUnit;
	}

	/**
	 * @param cashAmountUnit the cashAmountUnit to set
	 */
	@AutoCopy("getCashAmountUnit")
	public void setCashAmountUnit(BigDecimal cashAmountUnit) {
		this.cashAmountUnit = cashAmountUnit;
	}

	/**
	 * @return the stakeAmountUnit
	 */
	public BigDecimal getStakeAmountUnit() {
		return stakeAmountUnit;
	}

	/**
	 * @param stakeAmountUnit the stakeAmountUnit to set
	 */
	@AutoCopy("getStakeAmountUnit")
	public void setStakeAmountUnit(BigDecimal stakeAmountUnit) {
		this.stakeAmountUnit = stakeAmountUnit;
	}

	/**
	 * @return the minimumCashAmount
	 */
	public BigDecimal getMinimumCashAmount() {
		return minimumCashAmount;
	}

	/**
	 * @param minimumCashAmount the minimumCashAmount to set
	 */
	@AutoCopy("getMinimumCashAmount")
	public void setMinimumCashAmount(BigDecimal minimumCashAmount) {
		this.minimumCashAmount = minimumCashAmount;
	}

	/**
	 * @return the maximumCashAmount
	 */
	public BigDecimal getMaximumCashAmount() {
		return maximumCashAmount;
	}

	/**
	 * @param maximumCashAmount the maximumCashAmount to set
	 */
	@AutoCopy("getMaximumCashAmount")
	public void setMaximumCashAmount(BigDecimal maximumCashAmount) {
		this.maximumCashAmount = maximumCashAmount;
	}

	/**
	 * @return the fractionAmountUnit
	 */
	public BigDecimal getFractionAmountUnit() {
		return fractionAmountUnit;
	}

	/**
	 * @param fractionAmountUnit the fractionAmountUnit to set
	 */
	@AutoCopy("getFractionAmountUnit")
	public void setFractionAmountUnit(BigDecimal fractionAmountUnit) {
		this.fractionAmountUnit = fractionAmountUnit;
	}

	/**
	 * @return the fractionMiniUnit
	 */
	public BigDecimal getFractionMiniUnit() {
		return fractionMiniUnit;
	}

	/**
	 * @param fractionMiniUnit the fractionMiniUnit to set
	 */
	@AutoCopy("getFractionMiniUnit")
	public void setFractionMiniUnit(BigDecimal fractionMiniUnit) {
		this.fractionMiniUnit = fractionMiniUnit;
	}

	/**
	 * @return the fractionThreshold
	 */
	public BigDecimal getFractionThreshold() {
		return fractionThreshold;
	}

	/**
	 * @param fractionThreshold the fractionThreshold to set
	 */
	@AutoCopy("getFractionThreshold")
	public void setFractionThreshold(BigDecimal fractionThreshold) {
		this.fractionThreshold = fractionThreshold;
	}

}

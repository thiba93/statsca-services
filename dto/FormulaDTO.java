package com.carrus.statsca.dto;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.pmc.club.entity.Formula;
import com.pmc.club.entity.KeyEnum;
import com.pmc.club.entity.RiskEnum;

/**
 * DTO Formula qui précise les AuthorisedFormulation.
 * Classe invariante
 * 
 * @author BT - ARTSYS 2021
 * @since 30 septembre 2021
 */
@AutoFillFrom(value = Formula.class, fillerPath = "formulas")
public class FormulaDTO {
	
	/**clé primaire de l'objet dans la base club*/
	private Long pk;
	


	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	private KeyEnum keyValue;
	/** Niveau de Risk maximal admis sur le pari */
	private RiskEnum riskValue;
	/** La formule complète est autorisée */
	private boolean box;
	/** Permet de désigner des chevaux de remplacement en cas de non-partant */
	private boolean replacement;
	/** Aide au pari */
	private boolean quickPick;
	
	/** Constructeur par copie */
	public FormulaDTO(com.pmc.club.entity.Formula formula) {
		this.keyValue = formula.getWheel();
		this.riskValue = formula.getRisk();
		this.box = formula.getBox();
		this.replacement = formula.getReplacement();
		this.quickPick = formula.getQuickPick();
	}

	/**
	 * @return the keyValue
	 */
	public KeyEnum getKeyValue() {
		return keyValue;
	}

	/**
	 * @param keyValue the keyValue to set
	 */
	@AutoCopy("getWheel")
	public void setKeyValue(KeyEnum keyValue) {
		this.keyValue = keyValue;
	}

	/**
	 * @return the riskValue
	 */
	public RiskEnum getRiskValue() {
		return riskValue;
	}

	/**
	 * @param riskValue the riskValue to set
	 */
	@AutoCopy("getRisk")
	public void setRiskValue(RiskEnum riskValue) {
		this.riskValue = riskValue;
	}

	/**
	 * @return the box
	 */
	public boolean isBox() {
		return box;
	}

	/**
	 * @param box the box to set
	 */
	@AutoCopy("getBox")
	public void setBox(boolean box) {
		this.box = box;
	}

	/**
	 * @return the replacement
	 */
	public boolean isReplacement() {
		return replacement;
	}

	/**
	 * @param replacement the replacement to set
	 */
	@AutoCopy("getReplacement")
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}

	/**
	 * @return the quickPick
	 */
	public boolean isQuickPick() {
		return quickPick;
	}

	/**
	 * @param quickPick the quickPick to set
	 */
	@AutoCopy("getQuickPick")
	public void setQuickPick(boolean quickPick) {
		this.quickPick = quickPick;
	}

}

package com.carrus.statsca.dto;

import com.pmc.club.entity.Dividend;
import com.pmc.club.entity.InformationEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classe de transport des données utiles relatives à l'entité Dividend
 * de la couche service de l'application.
 * Classe invariante car utilisée uniquement pour le transport du serveur vers le client. 
 * 
 * @author BT - ARTSYS 2021
 * @since 1.0.0 (08 décembre 2021)
 */
@Schema(description = "Dividend (final odd)")
public class DividendDTO {
	/** La combinaison du rapport */
	@Schema(description = "Combination, participant numbers separated by spaces", nullable = false, example = "4 2 9")
	private final String combination;
	/** Information complémentaire sur le rapport */
	@Schema(description = "Information code", nullable = false, defaultValue = "NO_SPECIFICITY")
	private final InformationEnum information;
	/** Valeur du rapport */
	@Schema(description = "Combination value", nullable = false, minimum = "0", defaultValue = "0")
	private final double value;

	/** Constructeur par copie */
	public DividendDTO(Dividend dividend) {
		this.combination = dividend.getCombination();
		this.information = dividend.getInformation();
		this.value = dividend.getValue();
	}

	/**
	 * @return the combination
	 */
	public String getCombination() {
		return combination;
	}

	/**
	 * @return the information
	 */
	public InformationEnum getInformation() {
		return information;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	
}
package com.carrus.statsca.dto;

import java.util.List;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.pmc.club.entity.Prices;

/**
 * Sous-structure invariante de la course qui contient les informations des prix sur cette course
 * 
 * @author BT - ARTSYS 2021
 * @since 22 septembre 2021
 */
@AutoFillFrom(value = Prices.class, fillerPath = "prices")
public class PricesDTO {
	/** Montant théorique total des prix */
	private Double nominalAllowance;
	/** Somme totale des prix attribués à la course */
	private Double total;
	/** Liste des prix sur la course */
	private List<Double> prices;
	
	
	public PricesDTO(Prices prices) {
		this.nominalAllowance = prices.getTotal();
		this.total = prices.getTotal();
		this.prices = prices.getPrices();
	}


	/**
	 * @return the nominalAllowance
	 */
	public Double getNominalAllowance() {
		return nominalAllowance;
	}

	/**
	 * @param nominalAllowance the nominalAllowance to set
	 */
	public void setNominalAllowance(Double nominalAllowance) {
		this.nominalAllowance = nominalAllowance;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	@AutoCopy("getTotal")
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * @return the prices
	 */
	public List<Double> getPrices() {
		return prices;
	}

	/**
	 * @param prices the prices to set
	 */
	@AutoCopy("getPrices")
	public void setPrices(List<Double> prices) {
		this.prices = prices;
	}
	
}

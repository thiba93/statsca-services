package com.carrus.statsca.bethistory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.pmc.club.entity.RemarkableRaceRecipe;


public class RemarkableRaceHistoryPartner {
	private int partnerId;
	
	private BigDecimal totalStake;
	
	private Map<Integer, BigDecimal> perBetType;
	
	public RemarkableRaceHistoryPartner(int partnerId) {
		this.partnerId = partnerId;
		totalStake = new BigDecimal(0);
		perBetType = new HashMap<>();
	}
	
	public boolean addRecipe(RemarkableRaceRecipe recipe) {
		if (recipe.getPartnerId() != partnerId) {
			return false;
		}
		
		Integer betType = recipe.getBetType();
		if (perBetType.containsKey(betType)) {
			return false;
		}
		
		perBetType.put(betType, recipe.getStake());
		totalStake = totalStake.add(recipe.getStake());
		return true;
	}

	public int getPartnerId() {
		return partnerId;
	}

	public BigDecimal getTotalStake() {
		return totalStake;
	}

	public Map<Integer, BigDecimal> getPerBetType() {
		return perBetType;
	}
}

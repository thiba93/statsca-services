package com.carrus.statsca.bethistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.pmc.club.entity.RemarkableRaceRecipe;

public class RemarkableRaceHistoryPeriod {
	private LocalTime periodEndTime;
	
	private BigDecimal totalStake;
	
	private Map<Integer, RemarkableRaceHistoryPartner> perPartner;
	
	public RemarkableRaceHistoryPeriod(LocalTime endDate) {
		periodEndTime = endDate;
		totalStake = new BigDecimal(0);
		perPartner = new HashMap<>();
	}
	
	boolean addRecipe(RemarkableRaceRecipe recipe) {
		LocalTime recordTime = recipe.getDateRecipe().toLocalTime();
		
		if (!periodEndTime.equals(recordTime)) {
			return false;
		}
		
		perPartner.putIfAbsent(recipe.getPartnerId(), new RemarkableRaceHistoryPartner(recipe.getPartnerId()));
		
		if (perPartner.get(recipe.getPartnerId()).addRecipe(recipe)) {
			totalStake = totalStake.add(recipe.getStake());
			return true;
		}
		
		return false;
	}

	public LocalTime getPeriodEndTime() {
		return periodEndTime;
	}

	public BigDecimal getTotalStake() {
		return totalStake;
	}

	public Map<Integer, RemarkableRaceHistoryPartner> getPerPartner() {
		return perPartner;
	}
	
}

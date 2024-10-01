package com.carrus.statsca.bethistory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.pmc.club.entity.RemarkableRaceRecipe;

public class RemarkableRaceHistory {
	private LocalDate raceDate;
	
	private SortedMap<String, RemarkableRaceHistoryPeriod> perPeriod;
	
	private List<String> excludedPartners; 
	
	public RemarkableRaceHistory(LocalDate raceDate, List<String> excluded ) {
		this.raceDate = raceDate;
		this.perPeriod = new TreeMap<>();
		this.excludedPartners = excluded;
	}
	
	public boolean addRecipe(RemarkableRaceRecipe recipe) {
		LocalDate dateSession = recipe.getDateSession();
		
		if (dateSession == null || !dateSession.equals(raceDate) || recipe.getDateRecipe() == null) {
			return false;
		}
		
		LocalTime targetTime = recipe.getDateRecipe().toLocalTime();
		String key = targetTime.format(DateTimeFormatter.ISO_TIME);
		perPeriod.putIfAbsent(key, new RemarkableRaceHistoryPeriod(targetTime));
		//exclude partners import if exists
		if(!excludedPartners.isEmpty() && excludedPartners.indexOf(String.valueOf(recipe.getPartnerId())) >= 0)
			return true;
		return perPeriod.get(key).addRecipe(recipe);
	}

	/*
	public RemarkableRaceHistory getLastPeriods(int nbPeriods) {
		if (nbPeriods < 0) {
			return null;
		}
		
		if (nbPeriods >= getHistoryPerPeriod().size()) {
			return this;
		}
		
		RemarkableRaceHistory history = new RemarkableRaceHistory(raceDate);
		List<String> periodsStr = new ArrayList<>(history.getHistoryPerPeriod().keySet());
		int periodsStrSize = getHistoryPerPeriod().size();
		
		for (int i = 0; i < nbPeriods; i++) {
			String currentPeriod = periodsStr.get(periodsStrSize - nbPeriods + i);
			
			perPeriod.get(currentPeriod).
		}
		
		
		return history;
	}
	*/
	
	public LocalDate getRaceDate() {
		return raceDate;
	}
	
	public Map<String, RemarkableRaceHistoryPeriod> getHistoryPerPeriod() {
		return perPeriod;
	}
}

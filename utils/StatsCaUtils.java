package com.carrus.statsca.utils;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.RaceDTO;
import com.carrus.statsca.dto.RaceRefDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.RaceState;
import com.pmc.club.references.RaceRef;

public class StatsCaUtils {
	public static boolean isRaceAfterDeparted(RaceState state) {
		RaceState[] stateArray = {RaceState.TEMPORARY_ARRIVED, RaceState.INQUIRY, RaceState.DEFINITIVE_ARRIVED, RaceState.PAYMENT_AUTHORIZED, RaceState.PAYMENT_STOPPED, RaceState.CLOSED};
		List<RaceState> stateAfterDeparted = Arrays.asList(stateArray);
		return stateAfterDeparted.contains(state);
	}
	

	public static boolean isRaceOnPayment(RaceState state) {
		RaceState[] stateArray = {RaceState.PAYMENT_AUTHORIZED, RaceState.PAYMENT_STOPPED, RaceState.CLOSED};
		List<RaceState> stateAfterDeparted = Arrays.asList(stateArray);
		return stateAfterDeparted.contains(state);
	}
	
	public static RaceRef getRaceRefFromRace(Race race) {
		return new RaceRef(race.getEvent().getDate(), race.getEvent().getId(), race.getNumber());
	}
	
	public static RaceRef getRaceRefFromRace(RaceDTO race, EventDTO event, SessionDTO session) {
		return new RaceRef(session.getSessionDate(), event.getId(), race.getNumber());
	}
	
	public static RaceRef getRaceRefClubFromRaceRefDTO(RaceRefDTO raceRef) {
		return new RaceRef(raceRef.getEventDate(),raceRef.getEventId(),raceRef.getRaceNumber());
	}
	
	public static LocalDate getEquivalentDateFromPreviousYear(LocalDate currentDate) {
		LocalDate previousYearDate = currentDate.minusYears(1);
		//int diffDays = currentDate.getDayOfWeek().getValue() - previousYearDate.getDayOfWeek().getValue();

		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		
		int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
		//int previousweekNumber = previousYearDate.get(weekFields.weekOfWeekBasedYear());
		
		LocalDate date = LocalDate.now().with(WeekFields.ISO.weekBasedYear(), previousYearDate.getYear()) // year
				.with(WeekFields.ISO.weekOfWeekBasedYear(), weekNumber) // week of year
				.with(WeekFields.ISO.dayOfWeek(), currentDate.getDayOfWeek().getValue()); // day of week
		//previousYearDate = previousYearDate.plusDays(diffDays);
		
		//01-01-2024 --> 02-01-2023
		//16-06-2024 --> 18-06-2023
		return date;
	}
	

	
	public static Integer getNumeric(String strNum) {
	    try {
	    	Integer i = Integer.parseInt(strNum);
	    	return i;
	    } catch (NumberFormatException nfe) {
	        return -1;
	    }
	}
}

package com.carrus.statsca;

import java.time.LocalDate;

import javax.ejb.Local;

import com.carrus.statsca.dto.SessionDTO;

@Local
public interface EventService {
	
	public SessionDTO getEventByRaceTrack(LocalDate date, String raceTrackId);
}

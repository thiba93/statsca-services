package com.carrus.statsca;

import java.time.LocalDate;
import java.util.List;

import javax.ejb.Local;

import com.carrus.statsca.dto.SessionDTO;

 @Local
public interface HistoryService {

	List<SessionDTO> loadHistoryRaceCard(LocalDate startDate, LocalDate endDate);
	
}

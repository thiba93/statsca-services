package com.carrus.statsca;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.carrus.statsca.dto.RegulatoryBetDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.pmc.club.entity.RegulatoryBet;
import com.pmc.club.entity.Session;
import com.pmc.club.entity.enums.SessionState;

public interface MockSessionService {

	SessionDTO getMockSessionFromScratch(LocalDate mockSessionDate, String label, Integer eventCount, Integer raceCount, Map<Integer, RegulatoryBetDTO> betList);

	SessionDTO getMockSessionFromScratch(LocalDate mockSessionDate);

	
	List<SessionDTO> getMockSessionListFromScratch(LocalDate startSessionDate, LocalDate endSessionDate);
	
	List<SessionDTO> getMockSessionListFromScratch(LocalDate startSessionDate, LocalDate endSessionDate, String labelName, Integer eventCount, Integer raceCount,
			Map<Integer, RegulatoryBetDTO> betList);

	Session getMockEntitySessionFromScratch(Integer idSession, LocalDate mockSessionDate, Integer eventCount, Integer raceCount, Integer betCount, List<RegulatoryBet> betList, SessionState state);

	SessionDTO getSessionWithMockedRecipes(LocalDate sessionDate);

//	/**
//	 * 
//	 * @param idSession TODO
//	 * @param mockSessionDate
//	 * @param eventCount
//	 * @param raceCount
//	 * @param betCount
//	 * @param betList
//	 * @param state
//	 * @return
//	 */
//	boolean injectDevSession(Integer idSession, LocalDate mockSessionDate, Integer eventCount, Integer raceCount, Integer betCount, List<RegulatoryBet> betList, SessionState state);
//
//	List<RegulatoryBet> createMockedBetLists(String betCodes);
//
//	SessionState retrieveSessionState(String state);

}

package com.carrus.statsca.ejb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.HistoryService;
import com.carrus.statsca.dto.AuthorisedPartnerDTO;
import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.FormulationStakeDTO;
import com.carrus.statsca.dto.PartnerDTO;
import com.carrus.statsca.dto.RaceDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.pmc.club.entity.Event;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.Session;
import com.pmc.club.entity.partner.AuthorisedPartner;
import com.pmc.club.entity.partner.Partner;
import com.pmc.club.entity.recipe.FormulationStake;
import com.pmc.club.service.EventService;

@Singleton
public class HistoryServiceEJB implements HistoryService {
	
	/** Logger de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceEJB.class);
	
	@Inject
	private EventService eventService;
	
	private SortedMap<LocalDate, SessionDTO> cacheSessions = new TreeMap<>();
	
	private LocalDate minSessionCache;
	
	private LocalDate maxSessionCache;
	
	@Override
	public List<SessionDTO> loadHistoryRaceCard(LocalDate startDate, LocalDate endDate) {

		List<SessionDTO> sessions = new ArrayList<>();
//		List<LocalDate> dateSessions = getDateSessions(startDate,endDate);
//		for(LocalDate date : dateSessions) {
//			Session session = eventService.getSessionByDate(date);
//			if(session!=null) {
//				SessionDTO sessionDTO = constructLightSession(session);
//				sessions.add(sessionDTO);
//			}
//		}
		
		if(cacheSessions.isEmpty())
		{
			List<Session> sessionsS3K = eventService.getSessionsBetweenDates(startDate, endDate);
			processSessions(sessions, sessionsS3K);
		}
		else
		{
			LocalDate cacheStartDate = startDate;
			List<Session> sessionsS3KFromStart = new ArrayList<>();
			if(minSessionCache.isAfter(startDate)) {
				sessionsS3KFromStart = eventService.getSessionsBetweenDates(startDate, minSessionCache.minusDays(1));
				cacheStartDate = minSessionCache;
			}
			
			LocalDate cacheEndDate = endDate;
			List<Session> sessionsS3KToEnd = new ArrayList<>();
			if(maxSessionCache.isBefore(endDate)) {
				sessionsS3KToEnd = eventService.getSessionsBetweenDates(maxSessionCache.plusDays(1), endDate);
				cacheEndDate = maxSessionCache;
			}
			
			processSessionsFromStart(sessions, sessionsS3KFromStart);
			
			processSessionsFromCache(sessions, cacheStartDate, cacheEndDate);
			
			processSessionsToEnd(sessions, sessionsS3KToEnd);
			
		}
		
		return sessions;
	}



	private void processSessionsFromCache(List<SessionDTO> sessions, LocalDate cacheStartDate, LocalDate cacheEndDate) {
		for(Entry<LocalDate, SessionDTO> entry:cacheSessions.entrySet()) {
			if((entry.getKey().isEqual(cacheStartDate) || entry.getKey().isAfter(cacheStartDate)) && 
					(entry.getKey().isEqual(cacheEndDate) || entry.getKey().isBefore(cacheEndDate))) 
			{
				LOGGER.info("cache session to add : {}", entry.getKey());
				sessions.add(entry.getValue());
			}
		}
	}



	private void processSessionsToEnd(List<SessionDTO> sessions, List<Session> sessionsS3KToEnd) {
		if(!sessionsS3KToEnd.isEmpty()) {
			LOGGER.info("history service: sessions to the end : {} sessions", sessionsS3KToEnd.size());
			for(Session session : sessionsS3KToEnd) {
				LOGGER.info("session to add : {}", session.getDate());
				SessionDTO sessionDTO = constructLightSession(session);
				cacheSessions.put(sessionDTO.getSessionDate(), sessionDTO);
				if(minSessionCache==null || sessionDTO.getSessionDate().isBefore(minSessionCache))
				{
					minSessionCache = sessionDTO.getSessionDate();
				}
				
				if(maxSessionCache==null || sessionDTO.getSessionDate().isAfter(minSessionCache))
				{
					maxSessionCache = sessionDTO.getSessionDate();
				}
				sessions.add(sessionDTO);
			}
		}
	}



	private void processSessionsFromStart(List<SessionDTO> sessions, List<Session> sessionsS3KFromStart) {
		if(!sessionsS3KFromStart.isEmpty()) {
			LOGGER.info("history service: sessions from strart : {} sessions", sessionsS3KFromStart.size());
			for(Session session : sessionsS3KFromStart) {
				LOGGER.info("session to add : {}", session.getDate());
				SessionDTO sessionDTO = constructLightSession(session);
				cacheSessions.put(sessionDTO.getSessionDate(), sessionDTO);
				if(minSessionCache==null || sessionDTO.getSessionDate().isBefore(minSessionCache))
				{
					minSessionCache = sessionDTO.getSessionDate();
				}
				
				if(maxSessionCache==null || sessionDTO.getSessionDate().isAfter(maxSessionCache))
				{
					maxSessionCache = sessionDTO.getSessionDate();
				}
				sessions.add(sessionDTO);
			}
		}
	}



	private void processSessions(List<SessionDTO> sessions, List<Session> sessionsS3K) {
		if(sessionsS3K != null && !sessionsS3K.isEmpty())
		{
			for(Session session : sessionsS3K)
			{
				SessionDTO sessionDTO = constructLightSession(session);
				cacheSessions.put(sessionDTO.getSessionDate(), sessionDTO);
				if(minSessionCache==null || sessionDTO.getSessionDate().isBefore(minSessionCache))
				{
					minSessionCache = sessionDTO.getSessionDate();
				}
				
				if(maxSessionCache==null || sessionDTO.getSessionDate().isAfter(minSessionCache))
				{
					maxSessionCache = sessionDTO.getSessionDate();
				}				
				sessions.add(sessionDTO);
			}
		}
	}

	
	
	private SessionDTO constructLightSession(Session session) {
		SessionDTO lightSession = new SessionDTO();
		lightSession.setSessionDate(session.getDate());
		lightSession.setEvents(new ArrayList<>());
		for(Event event: session.getEvents())
		{
			EventDTO lightEvent = constructLightEvent(event);
			lightSession.getEvents().add(lightEvent);
		}
		
		return lightSession;
	}

	private EventDTO constructLightEvent(Event event) {
		EventDTO lightEvent = new EventDTO();
		lightEvent.setId(event.getId());
		lightEvent.setName(event.getName());
		lightEvent.setRaces(new ArrayList<>());
		for(Race race : event.getRaces())
		{
			RaceDTO lightRace = constructLightRace(race);
			lightEvent.getRaces().add(lightRace);
		}
		return lightEvent;
	}

	private RaceDTO constructLightRace(Race race) {
		RaceDTO lightRace = new RaceDTO();
		lightRace.setNumber(race.getNumber());
		lightRace.setName(race.getName());
		lightRace.setExpectedStart(race.getExpectedStart());
		lightRace.setAuthorizedPartners(new ArrayList<>());
		lightRace.setState(race.getState());
		lightRace.setRunnerNumber(race.getRunnerNumber());
		for(AuthorisedPartner ap : race.getAuthorisedPartners())
		{
			AuthorisedPartnerDTO apDTO = constructLightAuthorisedPartner(ap);
			lightRace.getAuthorizedPartners().add(apDTO);
		}
		return lightRace;
	}

	private AuthorisedPartnerDTO constructLightAuthorisedPartner(AuthorisedPartner ap) {
		AuthorisedPartnerDTO apDTO = new AuthorisedPartnerDTO();
		apDTO.setPartner(constructLightPartner(ap.getPartner()));
		apDTO.setStake(ap.getStake());
		apDTO.setFormulationStakes(new ArrayList<>());
		for(FormulationStake fs : ap.getFormulationStakes())
		{
			FormulationStakeDTO fsDTO = constructLightFormulationStake(fs);
			apDTO.getFormulationStakes().add(fsDTO);
		}
		return apDTO;
	}

	private FormulationStakeDTO constructLightFormulationStake(FormulationStake fs) {
		FormulationStakeDTO fsDTO = new FormulationStakeDTO();
		int betCodeRef = (fs.getOffer() != null && fs.getOffer().getBetType() != null)?fs.getOffer().getBetType().getCode():-1;
		fsDTO.setBetCodeRef(betCodeRef);
		fsDTO.setStake(fs.getStake());
		return fsDTO;
	}

	private PartnerDTO constructLightPartner(Partner partner) {
		PartnerDTO partnerDTO = new PartnerDTO();
		partnerDTO.setPartnerId(partner.getPartnerId());
		partnerDTO.setName(partner.getName());
		return partnerDTO;
	}
	
//	private List<LocalDate> getDateSessions(LocalDate startDate, LocalDate endDate) {
//	
//	if(startDate!=null)
//	{
//		long maxSessionHistory = MAX_SESSION_HISTORY;
//		long nbDays = 0;
//		if(endDate!=null)
//		{
//			nbDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
//		}
//		else
//		{
//			nbDays = MAX_SESSION_HISTORY;
//		}
//		long nbMaxIterations = (nbDays <= maxSessionHistory)?nbDays:maxSessionHistory;
//		List<LocalDate> dateSessions = new ArrayList<>();
//		long iteration = 0;
//		while(iteration<nbMaxIterations)
//		{
//			LocalDate nextDate = startDate.plusDays(iteration);
//			dateSessions.add(nextDate);
//			iteration++;
//		}
//		return dateSessions;
//	}
//	else
//	{
//		if(endDate!=null)
//		{
//			long nbMaxIterations = MAX_SESSION_HISTORY;
//			List<LocalDate> dateSessions = new ArrayList<>();
//			long iteration = 0;
//			while(iteration<nbMaxIterations)
//			{
//				LocalDate nextDate = endDate.minusDays(iteration);
//				dateSessions.add(0,nextDate);
//				iteration++;
//			}
//			return dateSessions;
//			
//			
//		}
//	}
//	return new ArrayList<>();
//}
	
}

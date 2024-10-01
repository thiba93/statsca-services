package com.carrus.statsca.ejb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Inject;

import com.carrus.statsca.EventService;
import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.pmc.club.entity.Event;

@Singleton
public class EventServiceEJB implements EventService {
	
//	/**Class logger */
//	private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceEJB.class);
	
	@Inject
	private com.pmc.club.service.EventService eventService;

	@Override
	public SessionDTO getEventByRaceTrack(LocalDate date, String raceTrackId) {
		
//			Event eventEntity = this.eventService.getEventByRaceTrack(date, raceTrackId);
//			if(eventEntity !=null) {
//				EventDTO eventDTO = new EventDTO(eventEntity);
//				SessionDTO sessionWithEvent = buildSessionWithEvent(date, eventDTO);
//				return sessionWithEvent;
//			}
		
		return null;
	}

	private SessionDTO buildSessionWithEvent(LocalDate date, EventDTO eventDTO) {
		SessionDTO sessionDTO = new SessionDTO(date);
		sessionDTO.setEvents(new ArrayList<>());
		sessionDTO.getEvents().add(eventDTO);
		return sessionDTO;
	}


}

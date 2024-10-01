package com.carrus.statsca.event;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.dto.AuthorisedPartnerDTO;
import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.PartnerDTO;
import com.carrus.statsca.dto.RaceDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pmc.club.event.UpdatedRacesRecipes;
import com.pmc.club.references.RaceRef;

public class UpdatedRacesRecipesEvt extends ChronoEvt {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedRacesRecipesEvt.class);

	private List<RaceRecipeEvt> raceRecipes;

	public UpdatedRacesRecipesEvt(UpdatedRacesRecipes urr) {
		super(urr.getCreation());
		raceRecipes = urr.getRaceRecipes().stream().map(RaceRecipeEvt::new).toList();
	}

	@Override
	public String toJson() {
		return buildRecipesEvtJSON(buildRaceRecipesSession());
	}

	public List<RaceRecipeEvt> getRaceRecipes() {
		return raceRecipes;
	}

	public void setRaceRecipes(List<RaceRecipeEvt> raceRecipes) {
		this.raceRecipes = raceRecipes;
	}

	public SessionDTO buildRaceRecipesSession() {
		SessionDTO session = new SessionDTO();
		if (raceRecipes != null && !raceRecipes.isEmpty()) {
			session.setSessionDate(raceRecipes.get(0).getRaceRef().getEventDate());
			session.setEvents(new ArrayList<>());
			for (RaceRecipeEvt evt : raceRecipes) {
				if (!isEventOnSession(evt.getRaceRef(), session)) {
					createEventFromRaceRecipe(session, evt);
				} else {
					modifyEventFromRaceRecipe(session, evt);
				}
			}
		}
		return session;
	}

	public String buildRecipesEvtJSON(SessionDTO session) {
		if (session != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JavaTimeModule());
				return mapper.writeValueAsString(session);
			} catch (JsonProcessingException e) {
				LOGGER.error("Error while generating json {}", e.getMessage());
			}
		}
		return "{}";
	}

	private void createEventFromRaceRecipe(SessionDTO session, RaceRecipeEvt evt) {
		if (session.getEvents() == null) {
			session.setEvents(new ArrayList<>());
		}
		EventDTO event = new EventDTO();
		event.setId(evt.getRaceRef().getEventId());
//		EventDetailDTO eventDetail = new EventDetailDTO();
//		eventDetail.setStartTime(evt.getRaceRef().getEventDate().atStartOfDay(ZoneId.systemDefault()));
//		event.setEventDetail(eventDetail);
		event.setRaces(new ArrayList<>());
		RaceDTO race = new RaceDTO();
		race.setNumber(evt.getRaceRef().getRaceNumber());
		race.setAuthorizedPartners(new ArrayList<>());
		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(evt);
		race.getAuthorizedPartners().add(authorisedPartnerDTO);
		event.getRaces().add(race);
		session.getEvents().add(event);
	}

	private void modifyEventFromRaceRecipe(SessionDTO session, RaceRecipeEvt evt) {
		int indexEvent = this.getIndexEvents(evt.getRaceRef().getEventId(), session.getEvents());
		if (!isRaceOnEvent(evt.getRaceRef(), session)) {
			createRaceFromRecipe(session, indexEvent, evt);
		} else {
			modifRaceFromRecipe(session, evt, indexEvent);
	
		}
	}

	private void createRaceFromRecipe(SessionDTO session, int indexEvent, RaceRecipeEvt event) {
		RaceDTO race = new RaceDTO();
		race.setNumber(event.getRaceRef().getRaceNumber());
		race.setAuthorizedPartners(new ArrayList<>());
		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(event);
		race.getAuthorizedPartners().add(authorisedPartnerDTO);
		session.getEvents().get(indexEvent).getRaces().add(race);
	}

	private void modifRaceFromRecipe(SessionDTO session, RaceRecipeEvt evt, int indexEvent) {
		int indexRace = this.getIndexRace(evt.getRaceRef().getRaceNumber(), session.getEvents().get(indexEvent).getRaces());
		if (!isPartnerOnRace(evt.getRaceRef(), evt.getContractor(), session)) {
			addPartnerRecipe(session, indexEvent, indexRace, evt);
		} else {
			modifyPartnerRecipe(session, indexEvent, indexRace, evt);
		}
	}

	private void addPartnerRecipe(SessionDTO session, int indexEvent, int indexRace, RaceRecipeEvt event) {
		if (session.getEvents().get(indexEvent).getRaces().get(indexRace).getAuthorizedPartners() == null) {
			session.getEvents().get(indexEvent).getRaces().get(indexRace).setAuthorizedPartners(new ArrayList<>());
		}
		AuthorisedPartnerDTO authorisedPartnerDTO = buildAuthorisedPartnerDTO(event);
		session.getEvents().get(indexEvent).getRaces().get(indexRace).getAuthorizedPartners().add(authorisedPartnerDTO);
	}

	private void modifyPartnerRecipe(SessionDTO raceRecipeSession, int indexEvent, int indexRace, RaceRecipeEvt event) {
		int indexPartner = this.getIndexPartners(event.getContractor(), raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace).getAuthorizedPartners());
		raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace).getAuthorizedPartners().get(indexPartner).setStake(BigDecimal.valueOf(event.getCashAmount()));
		raceRecipeSession.getEvents().get(indexEvent).getRaces().get(indexRace).getAuthorizedPartners().get(indexPartner).setUpdateDate(ZonedDateTime.now());
	}

	private boolean isRaceOnEvent(RaceRef raceRef, SessionDTO session) {
		List<EventDTO> events = session.getEvents();
		int indexEvent = this.getIndexEvents(raceRef.getEventId(), events);
		if (indexEvent != -1 && events.get(indexEvent) != null) {
			List<RaceDTO> races = events.get(indexEvent).getRaces();
			if (!races.isEmpty()) {
				int indexRace = this.getIndexRace(raceRef.getRaceNumber(), races);
				return indexRace != -1;
			}
		}
	
		return false;
	}

	private boolean isEventOnSession(RaceRef raceRef, SessionDTO session) {
		List<EventDTO> events = session.getEvents();
		if (events != null && !events.isEmpty()) {
			int indexEvent = this.getIndexEvents(raceRef.getEventId(), events);
			return indexEvent != -1;
		}
		return false;
	}

	private boolean isPartnerOnRace(RaceRef keyRaceRef, int contractor, SessionDTO session) {
		List<EventDTO> events = session.getEvents();
		int indexEvent = this.getIndexEvents(keyRaceRef.getEventId(), events);
		List<RaceDTO> races = events.get(indexEvent).getRaces();
		int indexRace = this.getIndexRace(keyRaceRef.getRaceNumber(), races);
		List<AuthorisedPartnerDTO> partners = races.get(indexRace).getAuthorizedPartners();
		if (!partners.isEmpty()) {
			int indexPartner = this.getIndexPartners(contractor, partners);
			return indexPartner != -1;
		}
	
		return false;
	}

	private int getIndexEvents(Integer eventId, List<EventDTO> events) {
		OptionalInt optionalIntEvent = IntStream.range(0, events.size()).filter(i -> events.get(i).getId() == eventId).findFirst();
		return optionalIntEvent.isPresent() ? optionalIntEvent.getAsInt() : -1;
	}

	private int getIndexRace(Integer raceId, List<RaceDTO> races) {
		OptionalInt optionalIntRace = IntStream.range(0, races.size()).filter(i -> races.get(i).getNumber() == raceId).findFirst();
		return optionalIntRace.isPresent() ? optionalIntRace.getAsInt() : -1;
	}

	private int getIndexPartners(int contractor, List<AuthorisedPartnerDTO> races) {
		OptionalInt optionalIntPartner = IntStream.range(0, races.size()).filter(i -> races.get(i).getPartner() != null && races.get(i).getPartner().getPartnerId() == contractor).findFirst();
		return optionalIntPartner.isPresent() ? optionalIntPartner.getAsInt() : -1;
	}

	private AuthorisedPartnerDTO buildAuthorisedPartnerDTO(RaceRecipeEvt event) {
		AuthorisedPartnerDTO authorisedPartnerDTO = new AuthorisedPartnerDTO();
		PartnerDTO partner = new PartnerDTO();
		if (event != null) {
			partner.setPartnerId(event.getContractor());
			partner.setName(event.getContractorName());
			partner.setShortName(event.getContractorShortName());
			authorisedPartnerDTO.setPartner(partner);
			authorisedPartnerDTO.setStake(BigDecimal.valueOf(event.getCashAmount()));
			authorisedPartnerDTO.setUpdateDate(ZonedDateTime.now());
		}
		return authorisedPartnerDTO;
	}
}

package com.carrus.statsca.event;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.pmc.club.event.EventChange;
import com.pmc.club.event.EventChange.EventStateEnum;
import com.pmc.club.references.EventRef;

public class EventChangeEvt extends ChronoEvt {
	/** Type de changement intervenu sur la session */
	private final EventStateEnum eventState;
	/** Référence à la réunion concernée par le changement */
	private final EventRef eventRef;
	
	public EventChangeEvt(ZonedDateTime creation, LocalDate sessionDate, Integer eventNb, EventStateEnum eventState) {
		super(creation);
		this.eventRef = new EventRef(sessionDate, eventNb);
		this.eventState = eventState;
	}

	public EventChangeEvt(EventChange eventChangeEvent) {
		super(eventChangeEvent.getCreation());
		this.eventRef = eventChangeEvent.getEventRef();
		this.eventState = eventChangeEvent.getEventState();
	}

	public EventStateEnum getEventState() {
		return eventState;
	}

	public EventRef getEventRef() {
		return eventRef;
	}

	@Override
	public String toJson() {
	
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("chronoType", getChronoType());
			if (getEventRef() != null && getEventRef().getEventDate() != null) {
				builder.add("eventDate", getEventRef().getEventDate().toString());
			}
			if (getEventRef() != null && getEventRef().getEventId() != null) {
			builder.add("eventId", getEventRef().getEventId());
			}
			builder.add("eventState", getEventState().name());
			
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
	
}

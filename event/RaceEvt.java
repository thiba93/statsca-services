package com.carrus.statsca.event;

import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import com.pmc.club.entity.RaceState;
import com.pmc.club.event.RaceChange;
import com.pmc.club.event.RaceChange.RaceChangeTypeEnum;

/**
 * Classe des evènements de chronologie qui portent
 * sur une course particulière.
 * 
 * @author BT - ARTSYS 2018
 * @since 5 janvier 2018
 */
public class RaceEvt extends RaceEvtAbstract {
	/** Type de changement sur la course */
	private final RaceChangeTypeEnum raceChangeType;
	/** Nouvel état de la course pour le type RACE_STATE_CHANGE */
	private final RaceState state;
	/** Nouvelle heure de départ de la course pour le type EXPECTED_START */
	private final ZonedDateTime expectedStart;
	
	/** Constructeur à partir de l'évènement CDI */
	public RaceEvt(RaceChange raceChange) {
		super(raceChange);

		this.raceChangeType = raceChange.getRaceChangeType();
		this.state = raceChange.getRaceState();
		this.expectedStart = raceChange.getExpectedStart();
	}
	
	/**
	 * @return the raceChangeType
	 */
	public RaceChangeTypeEnum getRaceChangeType() {
		return raceChangeType;
	}

	/**
	 * @return the state
	 */
	public RaceState getState() {
		return state;
	}

	/**
	 * @return the expectedStart
	 */
	public ZonedDateTime getExpectedStart() {
		return expectedStart;
	}

	/* (non-Javadoc)
	 * @see com.pmc.bis.commons.evt.AbstractEvt#toJson()
	 */
	@Override
	public String toJson() {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("chronoType", getChronoType());
			builder.add("racePk", this.getRacePk());
			if (getDate() != null) {
				builder.add("date", getDate().toString());
			}
			builder.add("eventId", getId());
			builder.add("raceNumber", getNumber());
			builder.add("changeType", getRaceChangeType().name());
			if (getState() != null) {
				builder.add("state", getState().name());
			}
			if (getExpectedStart() != null) {
				builder.add("expectedStart", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(getExpectedStart())/*getExpectedStart().toOffsetDateTime().toString()*/);
			}
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}

}
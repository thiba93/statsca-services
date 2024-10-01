/**
 * 
 */
package com.carrus.statsca.event;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.ZonedDateTime;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.event.SessionChange.SessionChangeTypeEnum;

/**
 * @author hery
 *
 */
public class SessionEvt extends ChronoEvt {
	/** Logger par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionEvt.class);
	
	private final SessionChangeTypeEnum sessionChangeType;
	/** Données relatives a la session*/
	private final String data;
	

	public SessionEvt(ZonedDateTime creation, String data) {
		super(creation);
		//par défaut il s'agit d'un changement lié au programme
		this.sessionChangeType = SessionChangeTypeEnum.SESSION_CHANGE;
		this.data = data;
		//LOGGER.debug("SessionEvt() 1 : data = [{}]", this.data != null ? this.data : "NULL");
	}
	
	public SessionEvt(SessionChange sessionChange) {
		//super(sessionChange);
		super(sessionChange.getCreation());
		this.sessionChangeType = sessionChange.getSessionChangeType();
		this.data = sessionChange.getData();
		//this.expectedStart = raceChange.getExpectedStart();
		//LOGGER.debug("SessionEvt() 2 : data = [{}]", this.data != null ? this.data : "NULL");
	}
	
	@Override
	public String toJson() {
		if (data == null || data.isBlank() || data.isEmpty()) {
			return "{}";
		}
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("chronoType", getChronoType());	
			builder.add("changeType", getSessionChangeType().name());
			try (JsonReader jsonReader = Json.createReader(new StringReader(data)))
			{
				JsonObject body = jsonReader.readObject();
				builder.add("data", body); 
			}
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}
	public SessionChangeTypeEnum getSessionChangeType() {
		return sessionChangeType;
	}
	public String getData() {
		return data;
	}


}

/**
 * 
 */
package com.carrus.statsca.event;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

/**
 * @author h.ramananjara
 *
 */
public class DisconnectionEvt extends AbstractEvt {

	@Override
	public EvtType getType() {
		return EvtType.SYSTEM;
	}

	@Override
	public String toJson() {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}

}

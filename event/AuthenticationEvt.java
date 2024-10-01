package com.carrus.statsca.event;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public class AuthenticationEvt extends AbstractEvt {
	
	String email;
	
	String token;

	@Override
	public EvtType getType() {
		// TODO Auto-generated method stub
		return EvtType.AUTHENTICATION;
	}

	@Override
	public String toJson() {
		StringWriter swriter = new StringWriter();
		try (JsonWriter jsonWrite = Json.createWriter(swriter)) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("creation",  getCreation().toOffsetDateTime().toString());
			builder.add("type", getType().name());
			builder.add("email", getEmail());
			builder.add("token", getToken());
			
			jsonWrite.writeObject(builder.build());
		}
		return swriter.toString();
	}

	public AuthenticationEvt(String email, String token) {
		super();
		this.email = email;
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	
}

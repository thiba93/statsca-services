package com.carrus.statsca.restws.requests;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contains all the criteria needed to search a session on the server")
public class SessionRequest {
	
	public SessionRequest() {
		super();
	}

	private LocalDate sessionDate;

	public SessionRequest(LocalDate sessionDate) {
		super();
		this.sessionDate = sessionDate;
	}

	/**
	 * @return the eventDate
	 */
	public LocalDate getSessionDate() {
		return sessionDate;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setSessionDate(LocalDate eventDate) {
		this.sessionDate = eventDate;
	}


	public String toString() {
		return "SessionRequest [eventDate=" + sessionDate + "]";
	}
	
}

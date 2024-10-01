package com.carrus.statsca.beans.requests;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author h.ramananjara
 * Transporter class for session request
**/
@Schema(description = "Contains all the criteria needed to search a racecard on the server")
public final class SessionRequest {
	
	/** Session date to request */
	@Schema(description = "Session date")
	private LocalDate date = LocalDate.now();

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
}

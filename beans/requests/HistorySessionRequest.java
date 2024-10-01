package com.carrus.statsca.beans.requests;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author h.ramananjara
 * Transporter class for session request
**/
@Schema(description = "Contains all the criteria needed to search a racecard on the server")
public final class HistorySessionRequest {
	
	/** Start date to request */
	@Schema(description = "Start Date")
	private LocalDate startDate = LocalDate.now();

	/** Start date to request */
	@Schema(description = "End Date")
	private LocalDate endDate = LocalDate.now();

	
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate date) {
		this.startDate = date;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
}

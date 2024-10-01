package com.carrus.statsca.admin.dto;

import java.time.LocalDateTime;

import com.carrus.statsca.admin.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

public class GrandPrizeRaceDTO {

	private Integer grandPrizeID;
	

	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private LocalDateTime grandPrizeDate;

	private Integer eventID;

	private Integer raceID;

	public Integer getGrandPrizeID() {
		return grandPrizeID;
	}

	public void setGrandPrizeID(Integer grandPrizeID) {
		this.grandPrizeID = grandPrizeID;
	}

	public LocalDateTime getGrandPrizeDate() {
		return grandPrizeDate;
	}

	public void setGrandPrizeDate(LocalDateTime grandPrizeDate) {
		this.grandPrizeDate = grandPrizeDate;
	}

	public Integer getEventID() {
		return eventID;
	}

	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}

	public Integer getRaceID() {
		return raceID;
	}

	public void setRaceID(Integer raceID) {
		this.raceID = raceID;
	}
	
	
	
}

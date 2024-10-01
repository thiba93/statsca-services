package com.carrus.statsca.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.carrus.statsca.dynaautofiller.AutoCopy;
import com.carrus.statsca.dynaautofiller.AutoFillFrom;
import com.carrus.statsca.dynaautofiller.AutoFillerEngine;
import com.carrus.statsca.dynaautofiller.AutoInstanciate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pmc.club.entity.RemarkableRace;

@AutoFillFrom(value = RemarkableRace.class, fillerPath = "remarkableRace")
public class RemarkableRaceDTO implements Serializable {
	private static final long serialVersionUID = 85858163711198453L;
	
	@JsonInclude(Include.NON_NULL)
	private Long pk;
	
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonInclude(Include.NON_NULL)
	private LocalDate dateSession;
	
	//@JsonInclude(Include.NON_NULL)
	//private int id;
	
	//@JsonInclude(Include.NON_NULL)
	//private int number;
	
	@JsonInclude(Include.NON_NULL)
	private GrandPrizeDTO grandPrize;
	
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonInclude(Include.NON_NULL)
	private LocalDate previousDate;
	
	public RemarkableRaceDTO() {}
	
	public RemarkableRaceDTO(RemarkableRace race) {
		AutoFillerEngine.autoFill(this, race);
	}

	public Long getPk() {
		return pk;
	}

	@AutoCopy("getPk")
	public void setPk(Long pk) {
		this.pk = pk;
	}

	public LocalDate getDateSession() {
		return dateSession;
	}

	@AutoCopy("getDateSession")
	public void setDateSession(LocalDate dateSession) {
		this.dateSession = dateSession;
	}

	public GrandPrizeDTO getGrandPrize() {
		return grandPrize;
	}

	@AutoInstanciate("getGrandPrize")
	public void setGrandPrize(GrandPrizeDTO grandPrize) {
		this.grandPrize = grandPrize;
	}

	public LocalDate getPreviousDate() {
		return previousDate;
	}

	public void setPreviousDate(LocalDate previousDate) {
		this.previousDate = previousDate;
	}
}

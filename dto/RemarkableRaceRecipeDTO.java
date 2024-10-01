package com.carrus.statsca.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.pmc.club.entity.RemarkableRaceRecipe;

@AutoFillFrom(value = RemarkableRaceRecipe.class, fillerPath = "remarkableRaceRecipe")
public class RemarkableRaceRecipeDTO implements Serializable {
	private static final long serialVersionUID = -471768937061723243L;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonInclude(Include.NON_NULL)
	private LocalDate dateSession;
	
	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:SS")
	@JsonInclude(Include.NON_NULL)
	private LocalTime heureSaisie;
	
	private int eventId;
	
	private int raceId;
	
	private RemarkableRaceDTO remarkable;
	
	private int partnerId;
	
	private int betType;
	
	private BigDecimal stake;
	
	public RemarkableRaceRecipeDTO() {
	}
	
	public RemarkableRaceRecipeDTO(RemarkableRaceRecipe recipe) {
		AutoFillerEngine.autoFill(this, recipe);
		this.heureSaisie = recipe.getDateRecipe() == null ? null : recipe.getDateRecipe().toLocalTime();
	}

	public LocalDate getDateSession() {
		return dateSession;
	}

	@AutoCopy("getDateSession")
	public void setDateSession(LocalDate dateSession) {
		this.dateSession = dateSession;
	}

	public LocalTime getHeureSaisie() {
		return heureSaisie;
	}

	public void setHeureSaisie(LocalTime heureSaisie) {
		this.heureSaisie = heureSaisie;
	}

	
	public int getEventId() {
		return eventId;
	}

	@AutoCopy("getId")
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getRaceId() {
		return raceId;
	}

	@AutoCopy("getNumber")
	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

//	public long getGrandPrizePk() {
//		return grandPrizePk;
//	}
//
//	@AutoCopy("getGrandPrizeId")
//	public void setGrandPrizePk(long grandPrizeId) {
//		this.grandPrizePk = grandPrizeId;
//	}
	
	public BigDecimal getStake() {
		return stake;
	}
	
	@AutoCopy("getStake")
	public void setStake(BigDecimal stake) {
		this.stake = stake;
	}

	public int getPartnerId() {
		return partnerId;
	}

	@AutoCopy("getPartnerId")
	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}

	public int getBetType() {
		return betType;
	}

	@AutoCopy("getBetType")
	public void setBetType(int betType) {
		this.betType = betType;
	}
	
	
	@AutoInstanciate("getRemarkable")
	public void setRemarkable(RemarkableRaceDTO rem) {
		this.remarkable = rem;
	}
	
	public RemarkableRaceDTO getRemarkable() {
		return remarkable;
	}
}

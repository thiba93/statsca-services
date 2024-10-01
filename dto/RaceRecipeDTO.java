package com.carrus.statsca.dto;

import java.util.Date;
import java.util.Objects;

import com.carrus.statsca.event.RaceRecipeEvt;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.pmc.club.event.RaceRecipe;

public class RaceRecipeDTO {
	private RaceRefDTO raceRef;
	private int contractor;
	private double cashAmount;
	private byte status;
	

	
	
	@JsonDeserialize(using = DateDeserializer.class)
	@JsonSerialize(using = DateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss:SSSZ")
	private Date computedTime;
	
	
	
	public RaceRecipeDTO(RaceRefDTO raceRef, int contractor, double cashAmount, byte status, Date computedTime) {
		super();
		this.raceRef = raceRef;
		this.contractor = contractor;
		this.cashAmount = cashAmount;
		this.status = status;
		this.computedTime = computedTime;
	}
	
	public RaceRecipeDTO(RaceRecipe s3krr) {
		this.raceRef = new RaceRefDTO(s3krr.getRaceRef());
		this.cashAmount = s3krr.getCashAmount();
		this.status = s3krr.getStatus();
		this.computedTime = s3krr.getComputedTime();
		this.contractor = s3krr.getContractor();
	}
	
	public RaceRecipeDTO(RaceRecipeEvt s3krr) {
		this.raceRef = new RaceRefDTO(s3krr.getRaceRef());
		this.cashAmount = s3krr.getCashAmount();
		this.status = s3krr.getStatus();
		this.computedTime = s3krr.getComputedTime();
		this.contractor = s3krr.getContractor();
	}
	
	public RaceRefDTO getRaceRef() {
		return raceRef;
	}
	public void setRaceRef(RaceRefDTO raceRef) {
		this.raceRef = raceRef;
	}
	
	
	public int getContractor() {
		return contractor;
	}
	public void setContractor(int contractor) {
		this.contractor = contractor;
	}
	
	
	public double getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}
	
	
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	
	
	public Date getComputedTime() {
		return computedTime;
	}
	public void setComputedTime(Date computedTime) {
		this.computedTime = computedTime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contractor, raceRef);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RaceRecipeDTO other = (RaceRecipeDTO) obj;
		return contractor == other.contractor && Objects.equals(raceRef, other.raceRef);
	}
	
	public String toString() {
		return "ATT" + contractor + " E"+this.raceRef.getEventId() + "R"+this.raceRef.getRaceNumber()+ " AMOUNT " + this.cashAmount;
	}
}

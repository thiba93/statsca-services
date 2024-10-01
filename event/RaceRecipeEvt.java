package com.carrus.statsca.event;

import java.util.Date;

import com.pmc.club.event.RaceRecipe;
import com.pmc.club.references.RaceRef;

public class RaceRecipeEvt {
	private RaceRef raceRef;
	private int contractor;
	private String contractorName = "UNKNOWN";
	private String contractorShortName = "ZZ";
	private Long organizationId;
	private String organizationName;
	private String organizationShortName;
	private double cashAmount;
	private byte status;
	private Date computedTime;
	
	
	
	public RaceRecipeEvt(RaceRef raceRef, int contractor, double cashAmount, byte status, Date computedTime) {
		super();
		this.raceRef = raceRef;
		this.contractor = contractor;
		this.cashAmount = cashAmount;
		this.status = status;
		this.computedTime = computedTime;
	}
	
	public RaceRecipeEvt(RaceRecipe s3krr) {
		this.raceRef = s3krr.getRaceRef();
		this.cashAmount = s3krr.getCashAmount();
		this.status = s3krr.getStatus();
		this.computedTime = s3krr.getComputedTime();
		this.contractor = s3krr.getContractor();
	}
	
	public RaceRef getRaceRef() {
		return raceRef;
	}
	public void setRaceRef(RaceRef raceRef) {
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

	public String getContractorName() {
		return contractorName;
	}

	public void setContractorName(String contractorName) {
		this.contractorName = contractorName;
	}

	public String getContractorShortName() {
		return contractorShortName;
	}

	public void setContractorShortName(String contractorShortName) {
		this.contractorShortName = contractorShortName;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationShortName() {
		return organizationShortName;
	}

	public void setOrganizationShortName(String organizationShortName) {
		this.organizationShortName = organizationShortName;
	}
}

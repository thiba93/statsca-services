package com.carrus.statsca.admin.dto;

public class LanguageDTO {
	private Integer languageID;
	private String name;
	private String shortName;
	private String initials;
	
	public Integer getLanguageID() {
		return languageID;
	}
	public void setLanguageID(Integer languageID) {
		this.languageID = languageID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
}

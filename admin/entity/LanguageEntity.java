package com.carrus.statsca.admin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LANGUAGE")
public class LanguageEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_LG")
	private Integer languageID;
	
	@Column(name = "LB_LG")
	private String name;
	

	@Column(name = "AV_LG")
	private String shortName;
	
	@Column(name = "INI_LG")
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
	public void setShortName(String shrotName) {
		this.shortName = shrotName;
	}
	public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}
}

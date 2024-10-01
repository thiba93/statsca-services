package com.carrus.statsca.admin.dto;

import java.time.ZonedDateTime;
import java.util.List;

import com.carrus.statsca.admin.restws.utils.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

public class UserDTO {

	public UserDTO() {
		super();
	}

	private Long userID;

	private String firstName;

	private String lastName;

	private String email;
	
	private String gsm;
	
//	private String token;
	
	private String uuidDevice;
	
//	private List<PreferenceDTO> preference;
	
	private String organization;
	
	private String plateforme;
	
	private RoleDTO role;
	
	private String language;
	
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime lastLoginDate;
	
	public UserDTO(Long userID, String firstName, String lastName, String email, String gsm, /* String token, */ String uuidDevice,
//			List<PreferenceDTO> preference,
			String organization, String plateforme, RoleDTO role, ZonedDateTime lastLoginDate, String language) {
		super();
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.gsm = gsm;
		this.uuidDevice = uuidDevice;
//		this.preference = preference;
		this.organization = organization;
		this.plateforme = plateforme;
		this.role = role;
		this.lastLoginDate = lastLoginDate;
		this.language = language;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String secondName) {
		this.lastName = secondName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getUuidDevice() {
		return uuidDevice;
	}

	public void setUuidDevice(String uuidDevice) {
		this.uuidDevice = uuidDevice;
	}

//	public List<PreferenceDTO> getPreference() {
//		return preference;
//	}
//
//	public void setPreference(List<PreferenceDTO> preference) {
//		this.preference = preference;
//	}

	public RoleDTO getRole() {
		return role;
	}

	public void setRole(RoleDTO role) {
		this.role = role;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public ZonedDateTime getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(ZonedDateTime lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getGsm() {
		return gsm;
	}

	public void setGsm(String gsm) {
		this.gsm = gsm;
	}

	public String getPlateforme() {
		return plateforme;
	}

	public void setPlateforme(String plateforme) {
		this.plateforme = plateforme;
	}

	
	
	
	
}

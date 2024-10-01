package com.carrus.statsca.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
@NamedQuery(name = UserEntity.RETRIEVE_BY_EMAIL, query = "SELECT table FROM UserEntity table "
		+ "WHERE table.email = :email ")
@NamedQuery(name = UserEntity.RETRIEVE_BY_ID, query = "SELECT table FROM UserEntity table "
		+ "WHERE table.userID = :userID")
@NamedQuery(name = UserEntity.RETRIEVE_ALL_USERS, query = "SELECT table FROM UserEntity table ")
public class UserEntity {
	
	public static final String RETRIEVE_BY_EMAIL = "UserEntity.retrieveByEmail";
	public static final String RETRIEVE_BY_ID = "UserEntity.retrieveByID";
	public static final String RETRIEVE_ALL_USERS = "UserEntity.retrieveAllUsers";
	
	
	public static final int DEFAULT_ROLE_ID = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_USR")
	private Long userID;

	@Column(name = "MAIL_USER")
	private String email;

	@Column(name = "LST_LOGIN")
	private LocalDateTime lastLoginDate;

	@Column(name = "ID_ROL")
	private Integer roleID;
	
	@Column(name="DEV_UUID")
	private String deviceUUID;
	
	@Column(name="LB_ORG")
	private String organization;

//	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//	private List<PreferenceEntity> preferences;


	public UserEntity(String email, String hashedPwd) {
		this.email = email;
	}

	public UserEntity() {
		
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	

	public LocalDateTime getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(LocalDateTime lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

//	public List<PreferenceEntity> getPreferences() {
//	    return preferences;
//	}
//
//	public void setPreferences(List<PreferenceEntity> preferences) {
//	    this.preferences = preferences;
//	}


	public Integer getRoleID() {
		return roleID;
	}

	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}

	public String getDeviceUUID() {
		return deviceUUID;
	}

	public void setDeviceUUID(String deviceUUID) {
		this.deviceUUID = deviceUUID;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

}

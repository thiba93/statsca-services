package com.carrus.statsca.admin.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.carrus.statsca.admin.enums.PreferenceTypeEnum;

@Entity
@Table(name = "PREFERENCE")
@NamedQuery(name = "PreferenceEntity.retrieveByUser", query = "SELECT table FROM PreferenceEntity table "
		+ "WHERE table.user.userID = :userID")
public class PreferenceEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PRF")
	private Integer preferenceId;

	
	@Column(name = "TYPE_PREF")
	private PreferenceTypeEnum typePref;

	@Column(name = "VAL_PREF")
	private String valPref;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ID_USR")
	private UserEntity user;


	public Integer getPreferenceId() {
		return preferenceId;
	}

	public void setPreferenceId(Integer preferenceId) {
		this.preferenceId = preferenceId;
	}
	public PreferenceTypeEnum getTypePref() {
		return typePref;
	}

	public void setTypePref(PreferenceTypeEnum preferenceTypeEnum) {
		this.typePref = preferenceTypeEnum;
	}

	public String getValPref() {
		return valPref;
	}

	public void setValPref(String valPref) {
		this.valPref = valPref;
	}


	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
	
}


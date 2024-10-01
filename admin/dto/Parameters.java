package com.carrus.statsca.admin.dto;

import java.io.Serializable;

/**
 *
 * @author b.dacruz
 *
 */
public class Parameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1494771778299854991L;
	private String uuid; 
	private String language;
	private Long userId;

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}

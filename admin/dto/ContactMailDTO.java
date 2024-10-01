package com.carrus.statsca.admin.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ContactMailDTO implements Serializable {
	
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

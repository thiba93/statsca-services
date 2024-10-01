package com.carrus.statsca.admin.mail;

public enum EnumTypeMail {
	CONTACT(1, "contact.html");

	int code;
	
	String fileName;

	private EnumTypeMail(int code, String fileName) {
		this.code = code;
		this.fileName = fileName;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}

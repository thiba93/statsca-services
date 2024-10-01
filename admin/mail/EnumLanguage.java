package com.carrus.statsca.admin.mail;

import java.util.stream.Stream;

public enum EnumLanguage {
	FR("fr"),
	EN("en"),
	DE("de");
	
	private String lang;
	
	EnumLanguage(String lang) {
		this.lang = lang;
	}

	public String getLang() {
		return lang;
	}
	
	public static EnumLanguage getEnum(String lang) {
		return Stream.of(EnumLanguage.values()).filter(value -> value.getLang().equals(lang)).findFirst().orElse(EnumLanguage.FR);
	}
}

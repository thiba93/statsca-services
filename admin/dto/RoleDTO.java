package com.carrus.statsca.admin.dto;

public class RoleDTO {

	private Integer roleID;
	
	private String name;
	
	private String shortName;

	public RoleDTO(Integer roleID, String name, String shortName) {
		super();
		this.roleID = roleID;
		this.name = name;
		this.shortName = shortName;
	}

	public Integer getRoleID() {
		return roleID;
	}

	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
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
	
}

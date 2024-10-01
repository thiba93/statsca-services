package com.carrus.statsca.admin;

import java.util.List;

import javax.ejb.Local;

import com.carrus.statsca.admin.entity.LanguageEntity;
import com.carrus.statsca.admin.entity.OrganizationEntity;
import com.carrus.statsca.admin.entity.ParametreEntity;
import com.carrus.statsca.admin.entity.RoleEntity;
import com.carrus.statsca.admin.entity.UserEntity;

@Local
public interface DataServiceAdmin {
	
	public List<UserEntity> retrieveAllUsers();

	public UserEntity retrieveByID(Long userID);

	// public UserEntity retrieveByCredentials(String cryptedPassword, String email);

	public UserEntity retrieveByEmail(String email);

	public void createUser(UserEntity user);

	public boolean updateUser(UserEntity user);

	public void deleteUser(UserEntity user);

	public void deleteUsers(List<UserEntity> users);
	
	public LanguageEntity retrieveLanguage(Integer id);
	
	public OrganizationEntity retrieveOrganization(Integer id);
	
	public RoleEntity retrieveRole(Integer id);

//	public boolean updateUserWithCredentials(UserEntity userEntity);
	
	public ParametreEntity retrieveParamByKey(String key);

	public List<ParametreEntity> retrieveAllParameters();

}

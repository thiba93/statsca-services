package com.carrus.statsca.admin.ejb;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.DataServiceAdmin;
import com.carrus.statsca.admin.UserService;
import com.carrus.statsca.admin.dto.LanguageDTO;
import com.carrus.statsca.admin.dto.OrganizationDTO;
import com.carrus.statsca.admin.dto.RoleDTO;
import com.carrus.statsca.admin.dto.UserDTO;
import com.carrus.statsca.admin.entity.LanguageEntity;
import com.carrus.statsca.admin.entity.OrganizationEntity;
import com.carrus.statsca.admin.entity.RoleEntity;
import com.carrus.statsca.admin.entity.UserEntity;

@Singleton
public class UserServiceEJB implements UserService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceEJB.class);

	@Inject
	DataServiceAdmin dataServiceAdmin;

	@Override
	public List<UserDTO> retrieveAllUsers() {
		List<UserEntity> entities = dataServiceAdmin.retrieveAllUsers();
		if (entities != null && !entities.isEmpty()) {
			return transformToUserDTO(entities);
		}
		
		return new ArrayList<>();
	}

	@Override
	public UserDTO retrieveByID(Long userID) {
		UserEntity entity = dataServiceAdmin.retrieveByID(userID);
		if (entity != null) {

			return transformToUserDTO(entity);
		}
		return null;
	}

//	@Override
//	public UserDTO retrieveByCredentials(String cryptedPassword, String email) {
//		UserEntity entity = dataServiceAdmin.retrieveByCredentials(cryptedPassword, email);
//		if (entity != null) {
//			return transformToUserDTO(entity);
//		}
//		return null;
//	}

//	@Override
//	public void updateCredentials(String email, String password) {
//		LOGGER.debug("Authentication - Attempt to update Credentials : {}", email);
//		String decryptedPwd = SecurityUtil.decryptPassword(password);
//		String hashedPwd = (decryptedPwd != null) ? SecurityUtil.hashPassword(decryptedPwd) : null;
//		if (!(hashedPwd == null || (dataServiceAdmin.updateUserWithCredentials(new UserEntity(email, hashedPwd))))) {
//			LOGGER.info("Impossible to update Credentials : {}", email);
//			throw new IllegalArgumentException();
//		}
//		LOGGER.info("Credentials updated successfully : {}", email);
//	}

	@Override
	public UserDTO retrieveByEmail(String email) {
		UserEntity entity = dataServiceAdmin.retrieveByEmail(email);
		if (entity != null) {
			return transformToUserDTO(entity);
		}
		return null;
	}

	@Override
	public void createUser(UserDTO user) {
		if (user != null) {
			UserEntity userEntity = transformToUserEntity(user);
			dataServiceAdmin.createUser(userEntity);
		}
	}

	@Override
	public boolean updateUser(UserDTO user) {
		if (user != null) {
			// UserEntity userEntity = transformToUserEntity(user);
			UserEntity userEntity = dataServiceAdmin.retrieveByID(user.getUserID());
			userEntity.setLastLoginDate(LocalDateTime.now());
			userEntity.setDeviceUUID(user.getUuidDevice());
			if ((userEntity.getOrganization() == null || "".equals(userEntity.getOrganization())) && (user.getOrganization() != null && !"".equals(user.getOrganization()))
					|| !user.getOrganization().equals(userEntity.getOrganization())) {
				userEntity.setOrganization(user.getOrganization());
			}
			return dataServiceAdmin.updateUser(userEntity);
		}
		return false;

	}
	@Override
	public boolean updateLanguageUser(UserDTO user) {
		if (user != null) {
			// UserEntity userEntity = transformToUserEntity(user);
			UserEntity userEntity = dataServiceAdmin.retrieveByID(user.getUserID());
			if ((userEntity.getOrganization() == null || "".equals(userEntity.getOrganization())) && (user.getOrganization() != null && !"".equals(user.getOrganization()))
					|| !user.getOrganization().equals(userEntity.getOrganization())) {
				userEntity.setOrganization(user.getOrganization());
			}
			return dataServiceAdmin.updateUser(userEntity);
		}
		return false;

	}

	@Override
	public void deleteUser(UserDTO user) {
		if(user != null)
		{
			UserEntity userEntity = transformToUserEntity(user);
			dataServiceAdmin.deleteUser(userEntity);
		}

	}

	@Override
	public void deleteUsers(List<UserDTO> users) {
		if(users != null && !users.isEmpty())
		{
			List<UserEntity> userEntities = transformToUserEntity(users);
			dataServiceAdmin.deleteUsers(userEntities);
		}
	}

	

	private List<UserDTO> transformToUserDTO(List<UserEntity> entities) {
		ArrayList<UserDTO> dtos = new ArrayList<>();
		for (UserEntity entity : entities) {
			UserDTO dto = transformToUserDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	private List<UserEntity> transformToUserEntity(List<UserDTO> dtos) {
		ArrayList<UserEntity> entities = new ArrayList<>();
		for (UserDTO dto : dtos) {
			UserEntity userEntity = transformToUserEntity(dto);
			entities.add(userEntity);
		}
		return entities;
	}

	private UserDTO transformToUserDTO(UserEntity entity) {

		UserDTO userDTO = new UserDTO();
		userDTO.setEmail(entity.getEmail());
		userDTO.setLastLoginDate(entity.getLastLoginDate() != null ? entity.getLastLoginDate().atZone(ZoneId.systemDefault()) : null);
		userDTO.setUserID(entity.getUserID());
		userDTO.setUuidDevice(entity.getDeviceUUID());
		
		// OrganizationDTO
//		if (entity.getOrganizationID() != null) {
//			OrganizationEntity orgEntity = dataServiceAdmin.retrieveOrganization(entity.getOrganizationID());
//			if (orgEntity != null) {
//				OrganizationDTO orgDTO = transformToOrganizationDTO(orgEntity);
//				userDTO.setOrganization(orgDTO);
//			}
//		}

		// RoleDTO
		if (entity.getRoleID() != null) {
			RoleEntity roleEntity = dataServiceAdmin.retrieveRole(entity.getRoleID());
			if (roleEntity != null) {
				RoleDTO roleDTO = transformToRoleDTO(roleEntity);
				userDTO.setRole(roleDTO);
			}
		}

		// PreferenceDTO
//		if (entity.getPreference() != null) {
//			PreferenceEntity preferenceEntity = entity.getPreference();
//			if (preferenceEntity != null) {
//				PreferenceDTO preferenceDTO = transformToPreferenceDTO(preferenceEntity);
//				userDTO.setPreference(preferenceDTO);
//			}
//		}

		return userDTO;
	}

	private UserEntity transformToUserEntity(UserDTO userDTO) {

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(userDTO.getEmail());
		userEntity.setLastLoginDate(userDTO.getLastLoginDate() != null ? userDTO.getLastLoginDate().toLocalDateTime() : null);
		userEntity.setUserID(userDTO.getUserID());
		userEntity.setDeviceUUID(userDTO.getUuidDevice());
		userEntity.setOrganization(userDTO.getOrganization());
		
		if (userDTO.getRole() != null) {
			userEntity.setRoleID(userDTO.getRole().getRoleID());
		}
		
//		if (userDTO.getPreference() != null) {
//			PreferenceEntity prefEntity = transformToPreferenceEntity(userDTO.getPreference());
		//	userEntity.setPreference(prefEntity);
	//	}

		return userEntity;
	}
	


	private RoleDTO transformToRoleDTO(RoleEntity roleEntity) {
		return new RoleDTO(roleEntity.getRoleID(), roleEntity.getName(), roleEntity.getShortName());
	}

	private OrganizationDTO transformToOrganizationDTO(OrganizationEntity orgEntity) {
		OrganizationDTO orgDTO = new OrganizationDTO();
		orgDTO.setOrganizationDesc(orgEntity.getOrganizationDesc());
		orgDTO.setOrganizationID(orgEntity.getOrganizationID());
		orgDTO.setOrganizationName(orgEntity.getOrganizationName());
		return orgDTO;
	}

	private LanguageDTO transformToLanguageDTO(LanguageEntity languageEntity) {
		LanguageDTO lgDTO = new LanguageDTO();
		lgDTO.setInitials(languageEntity.getInitials());
		lgDTO.setLanguageID(languageEntity.getLanguageID());
		lgDTO.setName(languageEntity.getName());
		lgDTO.setShortName(languageEntity.getShortName());

		return lgDTO;
	}

}

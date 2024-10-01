package com.carrus.statsca.admin.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.DataServiceAdmin;
import com.carrus.statsca.admin.entity.LanguageEntity;
import com.carrus.statsca.admin.entity.OrganizationEntity;
import com.carrus.statsca.admin.entity.ParametreEntity;
import com.carrus.statsca.admin.entity.RoleEntity;
import com.carrus.statsca.admin.entity.UserEntity;

@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Singleton
public class DataServiceAdminEJB implements DataServiceAdmin {

	private static final String ERROR_UPDATING_USER = "error updating User ";

	private static final Logger LOGGER = LoggerFactory.getLogger(DataServiceAdminEJB.class);

	@PersistenceContext
	public EntityManager entityManager;

	@Override
	public List<UserEntity> retrieveAllUsers() {
		try {
			TypedQuery<UserEntity> query = entityManager.createNamedQuery(UserEntity.RETRIEVE_ALL_USERS, UserEntity.class);
			return query.getResultList();
		} catch (Exception e) {
			LOGGER.error("Error retrieveAllUsers: {}", e.getMessage());
		}
		return new ArrayList<>();
	}

	@Override
	public List<ParametreEntity> retrieveAllParameters() {
		try {
		TypedQuery<ParametreEntity> query = entityManager.createNamedQuery("parametreEntity.retrieveAllParameters", ParametreEntity.class);
		return query.getResultList();
		} catch(Exception e) {
			LOGGER.error("Error retrieveAllParameters: {}", e.getMessage());
		}
		return new ArrayList<>();
	}

	@Override
	public UserEntity retrieveByID(Long userID) {
		try {
			TypedQuery<UserEntity> query = entityManager.createNamedQuery(UserEntity.RETRIEVE_BY_ID, UserEntity.class);
			query.setParameter("userID", userID);

			List<UserEntity> entityList = query.getResultList();
			return (entityList != null && !entityList.isEmpty()) ? entityList.get(0) : null;
		} catch (Exception e) {
			LOGGER.error("Error retrieveAllUsers: {}", e.getMessage());
		}
		return null;
	}

//	@Override
//	public UserEntity retrieveByCredentials(String cryptedPassword, String email) {
//		try {
//			TypedQuery<UserEntity> query = entityManager.createNamedQuery("UserEntity.retrieveByCredentials", UserEntity.class);
//			query.setParameter("cryptedPassword", cryptedPassword);
//			query.setParameter("email", email);
//
//			List<UserEntity> entityList = query.getResultList();
//			return (entityList != null && !entityList.isEmpty()) ? entityList.get(0) : null;
//		} catch (Exception e) {
//			LOGGER.error("IGDRDataManager - Error retrieveUserByCredentials: {}", e.getMessage());
//		}
//		return null;
//	}

	@Override
	public UserEntity retrieveByEmail(String email) {

		try {
			TypedQuery<UserEntity> query = entityManager.createNamedQuery(UserEntity.RETRIEVE_BY_EMAIL, UserEntity.class);
			query.setParameter("email", email);

			List<UserEntity> entityList = query.getResultList();
			return (entityList != null && !entityList.isEmpty()) ? entityList.get(0) : null;
		} catch (Exception e) {
			LOGGER.error("IGDRDataManager - Error retrieveByEmail: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public void createUser(UserEntity user) {
		try {
			entityManager.persist(user);
		} catch (Exception e) {
			LOGGER.error(ERROR_UPDATING_USER, e);
		}
	}

	@Override
	public boolean updateUser(UserEntity user) {
		try {
			return entityManager.merge(user) != null;
		} catch (Exception e) {
			LOGGER.error(ERROR_UPDATING_USER, e);
			return false;
		}
	}

	@Override
	public void deleteUser(UserEntity user) {
		try {
			entityManager.remove(user);
		} catch (Exception e) {
			LOGGER.error(ERROR_UPDATING_USER, e);
		}
	}

	@Override
	public void deleteUsers(List<UserEntity> users) {
		for (UserEntity user : users) {
			this.deleteUser(user);
		}

	}

	

	@Override
	public LanguageEntity retrieveLanguage(Integer id) {
		return entityManager.find(LanguageEntity.class, id);
	}

	@Override
	public OrganizationEntity retrieveOrganization(Integer id) {
		return entityManager.find(OrganizationEntity.class, id);
	}

	@Override
	public RoleEntity retrieveRole(Integer id) {
		return entityManager.find(RoleEntity.class, id);
	}

	@Override
	public ParametreEntity retrieveParamByKey(String key) {
		// TODO Auto-generated method stub
		return entityManager.find(ParametreEntity.class, key);
	}

//	@Override
//	public boolean updateUserWithCredentials(UserEntity userEntity) {
//		UserEntity userToUpdate = this.retrieveByEmail(userEntity.getEmail());
//		if (userToUpdate != null) {
//			userToUpdate.setCryptedPassword(userEntity.getCryptedPassword());
//			entityManager.merge(userToUpdate);
//			return true;
//		}
//		return false;
//	}

}

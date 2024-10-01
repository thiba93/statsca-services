package com.carrus.statsca.admin.ejb;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.ejb.interfaces.FcmTokenDataAccessService;
import com.carrus.statsca.admin.entity.FcmToken;

/**
 * This class is an EJB that provides data access operations for FCM (Firebase
 * Cloud Messaging) tokens. It is annotated as a Singleton, indicating that only
 * one instance will exist in the application. The ConcurrencyManagementType is
 * set to BEAN, which means that concurrency is managed at the bean level. The
 * class is also annotated as a Startup bean, which means it will be
 * instantiated when the application starts.
 */
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Singleton
public class FcmTokenDataAccessEJB implements FcmTokenDataAccessService {

	// Logger for logging messages and exceptions related to this class
	private static final Logger LOGGER = LoggerFactory.getLogger(FcmTokenDataAccessEJB.class);
	private static final String VALUE_KEY = "value";
	private static final String PLATEFORME_KEY = "plateforme";
	// EntityManager is used to interact with the JPA (Java Persistence API) for
	// database operations.
	@PersistenceContext
	public EntityManager entityManager;

	/**
	 * Saves the given FCM token in the database.
	 *
	 * @param fcmToken The FcmToken object to be saved.
	 */
	@Override
	public void saveToken(FcmToken fcmToken) {
		if (fcmToken.getPlateforme() != null) {
			Query countQuery = entityManager.createNativeQuery(
					"select count(id) from Fcm_token where value = :value and plateforme = :plateforme");
			countQuery.setParameter(VALUE_KEY, fcmToken.getValue());
			countQuery.setParameter(PLATEFORME_KEY, fcmToken.getPlateforme());
			if (((BigInteger) countQuery.getSingleResult()).compareTo(BigInteger.ZERO) == 0)
				entityManager.persist(fcmToken);
			else
				LOGGER.error("Token already exists");
		} else {
			entityManager.persist(fcmToken);
		}
	}

	/**
	 * Retrieves an FCM token from the database based on its value.
	 *
	 * @param value The value of the FCM token to retrieve.
	 * @return The FcmToken object if found, otherwise null.
	 */
	@Override
	public FcmToken retrieveTokenByValueAndPlateforme(String value, String plateforme) {
		try {
			TypedQuery<FcmToken> query = entityManager.createNamedQuery(FcmToken.RETRIEVE_BY_VALUE_AND_PLATEFORME,
					FcmToken.class);
			query.setParameter(VALUE_KEY, value);
			query.setParameter(PLATEFORME_KEY, plateforme);
			return query.getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving FCM token by value: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public Set<FcmToken> retrieveTokenByValue(String value) {
		try {
			TypedQuery<FcmToken> query = entityManager.createNamedQuery(FcmToken.RETRIEVE_BY_VALUE, FcmToken.class);
			query.setParameter(VALUE_KEY, value);
			List<FcmToken> result = query.getResultList() != null ? query.getResultList() : new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving FCM token by value: {}", e.getMessage());
		}
		return new HashSet<>();
	}

	/**
	 * Retrieves all FCM tokens from the database.
	 *
	 * @return A set containing all FcmToken objects if available, otherwise null.
	 */
	@Override
	public Set<FcmToken> retrieveAllTokens() {
		try {
			TypedQuery<FcmToken> query = entityManager.createNamedQuery(FcmToken.RETRIEVE_ALL_TOKENS, FcmToken.class);
			List<FcmToken> result = query.getResultList();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all FCM tokens: {}", e.getMessage());
		}
		return new HashSet<>();
	}

	/**
	 * Retrieves all FCM tokens from the database associated with a specific
	 * platform.
	 *
	 * @param platform The platform name for which tokens are to be retrieved.
	 * @return A set containing FcmToken objects associated with the platform if
	 *         available, otherwise null.
	 */
	@Override
	public Set<FcmToken> retrieveAllTokensByPlateform(String platform) {
		try {
			TypedQuery<FcmToken> query = entityManager.createNamedQuery(FcmToken.RETRIEVE_ALL_TOKENS_BY_PLATEFORM,
					FcmToken.class);
			query.setParameter(PLATEFORME_KEY, platform);
			List<FcmToken> result = query.getResultList() != null ? query.getResultList() : new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all FCM tokens for platform {}: {}", platform,
					e.getMessage());
		}
		return null;
	}

	/**
	 * Updates the given FCM token in the database.
	 *
	 * @param fcmToken The FcmToken object to be updated.
	 * @return true if the update is successful, false otherwise.
	 */
	@Override
	public boolean updateToken(FcmToken fcmToken) {
		try {
			return entityManager.merge(fcmToken) != null;
		} catch (Exception e) {
			LOGGER.error("Error occurred while updating FCM token: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * delete the given FCM token in the database.
	 *
	 * @param fcmToken The FcmToken object to be updated.
	 * @return true if the delete is successful, false otherwise.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean deleteTokenByValue(String value) {
		try {
			LOGGER.warn("deleting token : {}", value);
			String deleteNotificationFcmTokenQuery = "Delete from Notification_fcm_token where fcm_token_id in (select id from Fcm_token where value = :value)";
			String deleteQuery = "Delete from Fcm_token where value = :value";
			Query query = entityManager.createNativeQuery(deleteQuery);
			Query deleteNotificationFcmToken = entityManager.createNativeQuery(deleteNotificationFcmTokenQuery);
			deleteNotificationFcmToken.setParameter(VALUE_KEY, value);
			deleteNotificationFcmToken.executeUpdate();
			query.setParameter(VALUE_KEY, value);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			LOGGER.error("Error occurred while deleting FCM tokens: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public List<FcmToken> getExpiredTokens() {
		TypedQuery<FcmToken> query = entityManager.createNamedQuery(FcmToken.RETRIEVE_EXPIRED_TOKENS,
				FcmToken.class);
		query.setParameter("oneMonthEarlierDate",
				LocalDateTime.now().minusMonths(1).withHour(0).withMinute(0).withSecond(0));
		return query.getResultList() != null ? query.getResultList() : new ArrayList<>();
	}

	@Override
	public boolean deleteTokensByIds(Set<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}
		LOGGER.warn("deleting expired tokens for cleanup : ");
		String deleteQuery = " delete from Fcm_token  where id in :ids ;";
		Query query = entityManager.createNativeQuery(deleteQuery);
		query.setParameter("ids", ids);
		query.executeUpdate();
		return true;
	}

}
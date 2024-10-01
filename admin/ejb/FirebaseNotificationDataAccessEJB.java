package com.carrus.statsca.admin.ejb;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.carrus.statsca.admin.ejb.interfaces.FirebaseNotificationDataAccessService;
import com.carrus.statsca.admin.entity.FirebaseNotification;

@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Singleton
public class FirebaseNotificationDataAccessEJB implements FirebaseNotificationDataAccessService {

	// Logger for logging messages and exceptions related to this class
	private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseNotificationDataAccessEJB.class);
	private static final String VALUE_KEY = "value";
	// EntityManager is used to interact with the JPA (Java Persistence API) for
	// database operations.
	@PersistenceContext
	public EntityManager entityManager;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveNotification(FirebaseNotification firebaseNotification) {
		if (firebaseNotification.getNotificationFcmTokens() != null) {
			firebaseNotification.getNotificationFcmTokens().forEach(entityManager::persist);
		}
		if (firebaseNotification.getData() != null) {
			firebaseNotification.getData().forEach(md -> {
				entityManager.persist(md);
			});
		}
		entityManager.persist(firebaseNotification);

	}

	@Override
	public boolean updateNotification(FirebaseNotification firebaseNotification) {
		try {
			return entityManager.merge(firebaseNotification) != null;
		} catch (Exception e) {
			LOGGER.error("Error occurred while updating FCM token: {}", e.getMessage());
			return false;
		}
	}

	@Override
	public FirebaseNotification retrieveById(Long id) {
		try {
			TypedQuery<FirebaseNotification> query = entityManager.createNamedQuery(FirebaseNotification.RETRIEVE_BY_ID,
					FirebaseNotification.class);
			query.setParameter("id", id);
			return query.getSingleResult();
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving notification by ID{}", e.getMessage());
		}
		return null;
	}

	@Override
	public Set<FirebaseNotification> retrieveAllNotifications() {
		try {
			TypedQuery<FirebaseNotification> query = entityManager.createNamedQuery(FirebaseNotification.RETRIEVE_ALL,
					FirebaseNotification.class);
			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all FCM tokens for platform {}", e.getMessage());
			return new HashSet<>();
		}
	}

	@Override
	public Set<FirebaseNotification> retrieveByReceiverTokenValue(String token) {
		try {
			TypedQuery<FirebaseNotification> query = entityManager
					.createNamedQuery(FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_VALUE, FirebaseNotification.class);
			query.setParameter(VALUE_KEY, token);
			LOGGER.warn("yesterday is {} ",
					LocalDateTime.now(ZoneOffset.UTC).minusDays(1).withHour(0).withMinute(0).withSecond(0));
			query.setParameter("today", LocalDateTime.now(ZoneOffset.UTC));

			query.setParameter("yesterdayDate",
					LocalDateTime.now(ZoneOffset.UTC).minusDays(1).withHour(0).withMinute(0).withSecond(0));

			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return result.stream().map(t -> {
				t.setNotificationFcmTokens(t.getNotificationFcmTokens().stream()
						.filter(fcmToken -> token.equals(fcmToken.getfcmTokenId().getValue()))
						.collect(Collectors.toSet()));
				return t;
			}).collect(Collectors.toSet());
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all notifications by token {}", e.getMessage());
		}
		return new HashSet<>();
	}

	@Override
	public Set<FirebaseNotification> retrieveUnfetchidNotificationsByTokenValue(String token, Long lastNotifId) {
		try {
			String namedQuery = FirebaseNotification.RETRIEVE_ALL_UNFETCHED_BY_TOKEN_VALUE;
			if (lastNotifId == null) {
				namedQuery = FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_VALUE;
			}
			LOGGER.warn("inside entity manager function | last id = {}", lastNotifId);
			TypedQuery<FirebaseNotification> query = entityManager
					.createNamedQuery(namedQuery, FirebaseNotification.class);
			query.setParameter(VALUE_KEY, token);
			if (lastNotifId != null) {
				query.setParameter("lastNotifId", lastNotifId);
			}
			query.setParameter("today", LocalDateTime.now(ZoneOffset.UTC));

			query.setParameter("yesterdayDate",
					LocalDateTime.now(ZoneOffset.UTC).minusMonths(1).withHour(0).withMinute(0).withSecond(0));

			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return result.stream().map(t -> {
				t.setNotificationFcmTokens(t.getNotificationFcmTokens().stream()
						.filter(fcmToken -> token.equals(fcmToken.getfcmTokenId().getValue()))
						.collect(Collectors.toSet()));
				return t;
			}).collect(Collectors.toSet());
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all notifications by token {}", e.getMessage());
		}
		return new HashSet<>();
	}

	@Override
	public Set<FirebaseNotification> retrieveByReceiverTokenId(Long idToken) {
		try {
			TypedQuery<FirebaseNotification> query = entityManager
					.createNamedQuery(FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_ID, FirebaseNotification.class);
			query.setParameter("id", idToken);

			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all notification for receiver {}", e.getMessage());
		}
		return new HashSet<>();
	}

	@Override
	public Set<FirebaseNotification> retrieveByReceiverTokenIdsList(Set<Long> tokensIds) {
		try {
			TypedQuery<FirebaseNotification> query = entityManager
					.createNamedQuery(FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_IDS_LIST, FirebaseNotification.class);
			query.setParameter("ids", tokensIds);

			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all notification for receiver List {} ", e.getMessage());
		}
		return new HashSet<>();
	}

	@Override
	public Set<FirebaseNotification> retrieveByReceiverTokenValueAndNotSeen(String token) {
		try {
			TypedQuery<FirebaseNotification> query = entityManager.createNamedQuery(
					FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_VALUE_AND_NOT_SEEN, FirebaseNotification.class);
			query.setParameter(VALUE_KEY, token);
			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all FCM tokens for platform {} ", e.getMessage());
		}
		return new HashSet<>();
	}

	@Override
	public Set<FirebaseNotification> retrieveByReceiverTokenIdAndNotSeen(Long idToken) {
		try {
			TypedQuery<FirebaseNotification> query = entityManager.createNamedQuery(
					FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_ID_AND_NOT_SEEN, FirebaseNotification.class);
			query.setParameter("id", idToken);
			List<FirebaseNotification> result = query.getResultList() != null ? query.getResultList()
					: new ArrayList<>();
			return new HashSet<>(result);
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving all FCM tokens for platform {}", e.getMessage());
		}
		return new HashSet<>();
	}

	@Override
	public boolean markNotificationAsSeenByIds(List<Integer> ids) {
		LOGGER.warn("mark notif seen by ids");
		if (ids == null || ids.isEmpty()) {
			return false;
		}
		try {
			Query query = entityManager
					.createNativeQuery("UPDATE Notification_fcm_token " + " SET  seen=1 " + "WHERE id in :ids ;");
			query.setParameter("ids", ids);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			LOGGER.error("could not update notification status ", e);
			return false;
		}
	}

	@Override
	public boolean markNotificationAsSeenByFcmToken(String token) {
		LOGGER.warn("mark notif seen by receiver token");
		if (token == null) {
			return false;
		}
		try {
			Query query = entityManager
					.createNativeQuery("UPDATE Notification_fcm_token " + " SET  seen=1 "
							+ "WHERE fcm_token_id in (select id from Fcm_token where value = :token);");
			query.setParameter("token", token);
			query.executeUpdate();
			return true;
		} catch (Exception e) {
			LOGGER.error("could not update notification status ", e);
			return false;
		}
	}

	@Override
	public boolean deleteNotificationsByTokenIds(Set<Long> tokensIds) {
		if (tokensIds == null || tokensIds.isEmpty()) {
			return false;
		}
		Set<Long> notificationsToDeleteIds = retrieveByReceiverTokenIdsList(tokensIds).stream()
				.filter(n -> n.getNotificationFcmTokens().stream()
						.allMatch(nft -> tokensIds.contains(nft.getfcmTokenId().getId())))
				.map(n -> n.getId()).collect(Collectors.toSet());
		if (notificationsToDeleteIds == null || notificationsToDeleteIds.isEmpty()) {
			return false;
		}
		LOGGER.warn("deleting notifications for cleanup : ");
		String deleteMetaDataQuery = "delete from Notification_metadata where notification_id in :ids ";
		String deleteNotifFcmTokenQuery = " delete from Notification_fcm_token  where notification_id in :ids ";
		String deleteNotifQuery = " delete from FirebaseNotification  where id in :ids";
		Query query1 = entityManager.createNativeQuery(deleteMetaDataQuery);
		Query query2 = entityManager.createNativeQuery(deleteNotifFcmTokenQuery);
		Query query3 = entityManager.createNativeQuery(deleteNotifQuery);
		query1.setParameter("ids", notificationsToDeleteIds);
		query1.executeUpdate();
		query2.setParameter("ids", notificationsToDeleteIds);
		query2.executeUpdate();
		query3.setParameter("ids", notificationsToDeleteIds);
		query3.executeUpdate();
		return true;

	}

	@Override
	public Map<String, String> retrieveAllNotifMessagesMap() {
		Map<String, String> finalResult = new HashMap<>();
		String query = "select * from Notification_message";
		Query query1 = entityManager.createNativeQuery(query);
		List<?> resultList = query1.getResultList();
		for (Object obj : resultList) {
			if (obj instanceof Object[]) {
				Object[] result = (Object[]) obj;
				if (result.length >= 2 && result[0] != null && result[1] != null) {
					finalResult.put(result[1].toString(), result[2].toString());
				}
			}

		}
		return finalResult;
	}
}
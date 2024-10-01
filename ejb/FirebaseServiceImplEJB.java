
package com.carrus.statsca.ejb;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.ejb.interfaces.FcmTokenDataAccessService;
import com.carrus.statsca.admin.ejb.interfaces.FirebaseNotificationDataAccessService;
import com.carrus.statsca.ejb.interfaces.FirebaseService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

/**
 * This class implements the FirebaseService interface and provides methods to
 * interact with
 * Firebase Cloud Messaging (FCM) for sending push notifications to devices.
 *
 * The class is annotated with @Singleton and @Startup, indicating that it's a
 * singleton EJB
 * that should be initialized at application startup.
 */
@Singleton
@Startup
public class FirebaseServiceImplEJB implements FirebaseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseServiceImplEJB.class);

	@Inject
	private FcmTokenDataAccessService fcmTokenDataAccessService;
	@Inject
	private FirebaseNotificationDataAccessService notificationAccessService;

	private Map<String, String> notifsMessage;

	/**
	 * Initializes Firebase with the configuration stored in the "fcmConfig.json"
	 * file, which should be placed in the "firebase" directory in the resources
	 * folder. This method is automatically called after the class is constructed.
	 *
	 * @throws Exception If there is an error while initializing Firebase.
	 */
	@PostConstruct
	public void initFirebase() throws Exception {

		notifsMessage = notificationAccessService.retrieveAllNotifMessagesMap();

		// The following code initializes Firebase with the configuration stored in
		// "fcmConfig.json",
		// the file downloaded from Firebase when configuring the application.
		// It reads the configuration file and sets up Firebase options.
		// The configuration file should be placed in the "firebase" directory in the
		// resources folder.

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials
						.fromStream(getClass().getClassLoader().getResourceAsStream("firebase/fcmConfig.json")))
				.build();
		FirebaseApp.initializeApp(options);
		LOGGER.warn("Firebase FCM initialized, notification size {} ", notifsMessage.size());
	}

	@Override
	public Map<String, String> getNotificationMessages() {
		return notifsMessage;
	}

	/**
	 * Scheduled method to clean up expired Firebase Cloud Messaging (FCM) tokens
	 * and associated notifications.
	 *
	 * This method is scheduled to run once a month using the
	 * `@Schedule` annotation.
	 * It is responsible for cleaning up tokens and notifications where their issue
	 * date is older than 1 month.
	 * The process involves the following steps:
	 *
	 * 1. Retrieve a set of expired token IDs from the FcmTokenDataAccessService.
	 * 2. Delete notifications associated with the expired token IDs using the
	 * NotificationAccessService.
	 * 3. Delete the expired tokens from the database using the
	 * FcmTokenDataAccessService.
	 *
	 * After the cleanup is complete, a log message is generated to indicate the
	 * number of deleted tokens.
	 * Expired tokens are those that haven't been used for a certain period, causing
	 * the user to be considered unsubscribed.
	 *
	 * This method is transactional, using a new transaction for each execution to
	 * ensure data consistency.
	 * It logs information about the cleanup process, including the number of tokens
	 * deleted.
	 *
	 */
	@Schedule(dayOfMonth = "last", hour = "0", minute = "0", second = "0")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public void cleanUpFcmTokenDataBase() {
		try {
			LOGGER.info("CLEANING TOKENS WHERE THEIR ISSUE DATE > 1 month");
			LOGGER.info("Regular task: ");
			// Retrieve a set of expired token IDs
			Set<Long> expiredTokensIds = fcmTokenDataAccessService.getExpiredTokens().stream().map(t -> t.getId())
					.collect(Collectors.toSet());

			// Delete notifications associated with the expired tokens
			if (notificationAccessService.deleteNotificationsByTokenIds(expiredTokensIds)) {
				// if the previous deletion succeed, delete the expired tokens themselves
				fcmTokenDataAccessService.deleteTokensByIds(expiredTokensIds);
				// Log the number of deleted tokens
				LOGGER.warn("End cleaning: {} deleted tokens ", expiredTokensIds.size());
			} else {
				LOGGER.warn("No deletion done");
			}
		} catch (Exception e) {
			LOGGER.error("Error while deleting expired tokens {} ", e.getMessage());
		}
	}

}

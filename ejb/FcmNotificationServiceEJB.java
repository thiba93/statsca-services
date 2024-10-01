package com.carrus.statsca.ejb;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.ejb.interfaces.FcmTokenDataAccessService;
import com.carrus.statsca.admin.ejb.interfaces.FirebaseNotificationDataAccessService;
import com.carrus.statsca.admin.entity.FcmToken;
import com.carrus.statsca.admin.entity.FirebaseNotification;
import com.carrus.statsca.admin.entity.FirebaseNotification.Builder;
import com.carrus.statsca.admin.entity.NotificationFcmToken;
import com.carrus.statsca.admin.entity.NotificationFcmToken.EnumStatut;
import com.carrus.statsca.admin.entity.NotificationMetadata;
import com.carrus.statsca.admin.enums.NotificationTypeEnum;
import com.carrus.statsca.dto.FireBaseNotificationDTO;
import com.carrus.statsca.ejb.interfaces.FcmNotificationService;
import com.carrus.statsca.ejb.interfaces.FirebaseService;
import com.carrus.statsca.restws.requests.SendNotificationsRequest;
import com.carrus.statsca.utils.FirebaseNotificationUtils;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.AndroidNotification.Priority;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

/**
 * FcmNotificationServiceEJB is an implementation of the Firebase Cloud
 * Messaging (FCM) notification service. It handles the sending of push
 * notifications and in-app notifications to mobile devices using Firebase Cloud
 * Messaging.
 */
@Stateless
public class FcmNotificationServiceEJB implements FcmNotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FcmNotificationServiceEJB.class);
	private static final String NEW_TOKEN_KEY = "newToken";
	private static final String PLATEFORME_KEY = "plateforme";
	private static final String TITLE_KEY = "title";
	private static final String NOTIFICATIO_TYPE_SYSTEM = "SYSTEM";
	private static final String NOTIFICATIO_TYPE_METIER = "METIER";

	//private static int COUNT_NOTIF = 0;

	@Inject
	private FcmTokenDataAccessService fcmTokenDataAccessService;
	@Inject
	private FirebaseNotificationDataAccessService notificationAccessService;
	@Inject
	private FirebaseService firebaseService;

	/**
	 * Saves the device token and platform information to the database for future
	 * use.
	 *
	 * @param tokenPayload A map containing the token and platform information.
	 */
	@Override
	public void saveTokenToDataBase(Map<String, String> tokenPayload) {
		fcmTokenDataAccessService.saveToken(FcmToken.builder().setValue(tokenPayload.get(NEW_TOKEN_KEY))
				.setPlateforme(tokenPayload.get(PLATEFORME_KEY))
				.setLanguagePref(tokenPayload.get("lang") != null ? tokenPayload.get("lang") : "fr").buildIssueDateNow()
				.build());
	}

	/**
	 * Updates the device token in the database based on the provided request map.
	 * If the old token is found, it is replaced with the new token and updated with
	 * the current date. If the old token is not found, a new token is saved in the
	 * database with the provided platform information.
	 *
	 * @param request A map containing the old token, new token, and platform
	 *                information.
	 */
	@Override
	public void updateTokenInDataBase(Map<String, String> request) {
		if (request.get(NEW_TOKEN_KEY) == null) {
			throw new BadRequestException("La valeur du token ne peut pas être null");
		}
		Set<FcmToken> tokens = fcmTokenDataAccessService.retrieveTokenByValue(request.get("oldToken"));
		if (tokens != null && !tokens.isEmpty()) {
			tokens.stream().map(t -> {
				t.setValue(request.get(NEW_TOKEN_KEY));
				t.setIssueDate(LocalDateTime.now());
				t.setLanguagePref(request.get("lang") != null ? request.get("lang") : "fr");
				return t;
			}).forEach(fcmTokenDataAccessService::updateToken);
		} else if (request.get(PLATEFORME_KEY) != null) {
			this.saveTokenToDataBase(request);
		}
	}

	/**
	 * Retrieves notifications for a given device token from the database and
	 * returns them as a set of FirebaseNotificationDTO objects.
	 *
	 * @param token The device token for which notifications are retrieved.
	 * @return A set of FirebaseNotificationDTO objects representing the retrieved
	 *         notifications.
	 */
	@Override
	public Set<FireBaseNotificationDTO> retrieveNotificationsByTokenValue(String token) {
		return FirebaseNotificationUtils
				.mapNotificationSetToDTOSet(notificationAccessService.retrieveByReceiverTokenValue(token));
	}

	/**
	 * retrieves unfetched notifications for a given device token from the database
	 * base on the last fetched notification ID and returns them as a set of
	 * firebasenotificationdto objects.
	 *
	 * @param token the device token for which notifications are retrieved.
	 * @return a set of firebasenotificationdto objects representing the retrieved
	 *         notifications.
	 */
	@Override
	public Set<FireBaseNotificationDTO> retrieveUnfetchidNotificationsByTokenValue(String token, Long lastNotifId) {
		return FirebaseNotificationUtils.mapNotificationSetToDTOSet(
				notificationAccessService.retrieveUnfetchidNotificationsByTokenValue(token, lastNotifId));
	}

	/**
	 * Marks notifications as seen based on the provided mapping of notification
	 * IDs.
	 *
	 * @param request A map where the keys are strings representing categories or
	 *                types of notifications, and the corresponding values are sets
	 *                of integers representing notification IDs to be marked as
	 *                seen.
	 * @throws SomeExceptionType If there is an issue while marking notifications as
	 *                           seen.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void markAsSeen(Map<String, Object> request) {
		Object ids = request.get("ids");
		if (ids != null && !((ArrayList<Integer>) ids).isEmpty()) {
			notificationAccessService.markNotificationAsSeenByIds((ArrayList<Integer>) ids);
		} else
			notificationAccessService.markNotificationAsSeenByFcmToken((String) request.get("token"));

	}

	/**
	 * Sends a custom notification based on the provided request parameters.
	 *
	 * @param request A map containing the notification details: - "title": The
	 *                title of the notification (not null). - "body": The body
	 *                content of the notification (not null). - PLATEFORME_KEY: The
	 *                platform for which the notification is intended (not null). -
	 *                "type": The type of notification ("IN_APP" or other) to
	 *                determine its display format. - "minutesToLives": Optional,
	 *                the duration in minutes until the notification expires.
	 * @throws Exception Thrown if any of the mandatory parameters are missing.
	 */
	@Asynchronous
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void sendCustomNotification(SendNotificationsRequest request) throws BadRequestException {
		validateNotificationRequest(request);

		FirebaseNotification.Builder notifBuilder = new FirebaseNotification.Builder();

		if (NotificationTypeEnum.IN_APP.name().equals(request.getType())) {
			buildInAppNotification(notifBuilder, request);
		} else {
			buildPushNotification(notifBuilder, request);
		}

		notifBuilder.maxSendigDate(getMaxSendingDate(request));

		LOGGER.warn("Performing send notification");

		Set<FcmToken> tokens = fcmTokenDataAccessService.retrieveAllTokensByPlateform(request.getPlateforme());

		if (tokens != null && !tokens.isEmpty()) {
			FirebaseNotification notif = notifBuilder.build();
			sendNotificationsToTokens(notif, tokens, request);
			notificationAccessService.saveNotification(notif);
		}
	}

	@Asynchronous
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void performSendingNotification(Builder notifBuilder, Object... params) {
		Set<FcmToken> tokens = fcmTokenDataAccessService.retrieveAllTokens();
		if (tokens != null && !tokens.isEmpty()) {
			FirebaseNotification notif = notifBuilder.build();
			Set<FcmToken> filtredTokensList = tokens.stream().filter(t -> this.resolveTokensToSendNotifFilter(t, notif))
					.collect(Collectors.toSet());
			if (!filtredTokensList.isEmpty()) {
				filtredTokensList.forEach(t -> {
					NotificationFcmToken fcmNotif = new NotificationFcmToken();
					fcmNotif.setnotification(notif);
					fcmNotif.setfcmTokenId(t);
					fcmNotif.setStatut(EnumStatut.WAITING);
					fcmNotif.setSeen(false);
					fcmNotif.setSendDate(LocalDateTime.now(ZoneOffset.UTC));
					if (notif.getMaxSendingDate() != null) {
						// if the notification have specific max send time we set it
						fcmNotif.setMaxSendingDate(notif.getMaxSendingDate());
					} else {
						// default max sending time is 2 days
						fcmNotif.setMaxSendingDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(2));
						notif.setMaxSendingDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(2));
					}
					notif.addReceiver(fcmNotif);
					try {
						sendNotification(notif, t, params);
					} catch (FirebaseMessagingException e) {
						LOGGER.error("Error when sending FCM notification {} {}", e.getMessage(),
								e.getLocalizedMessage());
					}
				});
				notificationAccessService.saveNotification(notif);
			}
		}

	}

	/*
	 * @Asynchronous
	 * 
	 * @Override
	 * 
	 * @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) public void
	 * performTestSendingNotification(Builder notifBuilder, Object... params) {
	 * 
	 * FirebaseNotification notif = notifBuilder.build(); // FcmToken t = new
	 * FcmToken(23787238782378L, "PPI", //
	 * "e8lbh7isr0fQv5o81-sYk3:APA91bFnS7lNI32ikFNakw-0ltDh6W5Vg0mbxi4psGmarwG8sqJP9nTFhBWSw3Y8lbbs-BcuNJ-hI1pDLVA_J0cp0nh_-zfMAvWFuAYzFijZPAjyI-_lvnpLuoQBt_QOujK82yBGqSss",
	 * // LocalDateTime.now()); FcmToken t = new FcmToken(23787238782378L, "PPI",
	 * "dOg6ORGwiU6djWKPjHzj_d:APA91bEIffHj5zXTwXw2_qkS7UCNPIZ3jO868aEEDRmK7Ymb78m5VdECkSKYdCUY2kFh29ILVXdLyr92HU_8Ua-j1ajqmYL1zAfb7C0fmX03gtaVXPXFd8D6VfDa6dHAUtIsyF3EBtVG",
	 * LocalDateTime.now()); t.setLanguagePref("en");
	 * 
	 * //FcmToken t = new FcmToken(23787238782378L, "PPI", //
	 * "fE1Ia-CQROOriTBK-VVbFj:APA91bFDietTbZioCP41EWMSNxj5ZCarcsXeEF3AWE8GaP1gBSrQLyfLJf6leD--fOo4Uo175scrmIMVY42lro2CLfqi3w_hKe3ozV-5o0A9n5VxlraPLNy656ff1q9qgOFWLI2figFl",
	 * //LocalDateTime.now());
	 * 
	 * NotificationFcmToken fcmNotif = new NotificationFcmToken();
	 * fcmNotif.setnotification(notif); fcmNotif.setfcmTokenId(t);
	 * fcmNotif.setStatut(EnumStatut.WAITING); fcmNotif.setSeen(false);
	 * fcmNotif.setSendDate(LocalDateTime.now(ZoneOffset.UTC)); if
	 * (notif.getMaxSendingDate() != null) { // if the notification have specific
	 * max send time we set it
	 * fcmNotif.setMaxSendingDate(notif.getMaxSendingDate()); } else { // default
	 * max sending time is 2 days
	 * fcmNotif.setMaxSendingDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(2));
	 * notif.setMaxSendingDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(2)); }
	 * notif.addReceiver(fcmNotif); try { sendNotification(notif, t, params); }
	 * catch (FirebaseMessagingException e) {
	 * LOGGER.error("Error when sending FCM notification {} {}", e.getMessage(),
	 * e.getLocalizedMessage()); }
	 * //notificationAccessService.saveNotification(notif); }
	 */

	/**
	 * Sends a notification to a specific device token using Firebase Cloud
	 * Messaging. The notification is built based on the provided
	 * FirebaseNotification object.
	 *
	 * @param notification The FirebaseNotification object containing the
	 *                     notification data.
	 * @param token        The device token to which the notification is sent.
	 * @return A string representing the message ID returned by Firebase after
	 *         sending the notification.
	 * @throws FirebaseMessagingException If an error occurs while sending the
	 *                                    notification.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String sendNotification(FirebaseNotification notification, FcmToken token, Object... params)
			throws FirebaseMessagingException {
		// This method builds a push notification using the provided dataMessage and
		// sends it using Firebase Cloud Messaging.
		Notification.Builder builder = Notification.builder();

		Map<String, String> notifsMessage = firebaseService.getNotificationMessages();
		int count = 0;
		Locale locale = null;
		if (notification.getTitle() != null) {
			String title = FirebaseNotificationUtils.convertTechnicalMessageToUserMessage(notification.getTitle(),
					notifsMessage, token.getLanguagePref());
			if (params != null && params.length > 0) {
				locale = getUserLocale(token.getLanguagePref());
				translate(params, locale);
				//count parameters taken for title
				count = countFormatParameters(title);
				if (count > 0)
					title = String.format(locale, title, params);
			}
			builder.setTitle(title);
		}
		if (notification.getBody() != null) {
			String body = FirebaseNotificationUtils.convertTechnicalMessageToUserMessage(notification.getBody(),
					notifsMessage, token.getLanguagePref());

			if (params != null && params.length > 0) {
				params = Arrays.copyOfRange(params, count, params.length);
				//get params left for body
				body = String.format(locale, body, params);
			}
			builder.setBody(body);
		}

		Message.Builder messageBuilder = Message.builder().setNotification(builder.build()).setToken(token.getValue())
				.putAllData(FirebaseNotificationUtils.mapNotifMEtadataToMap(notification.getData()));
		AndroidConfig.Builder androidConfigBuilder = AndroidConfig.builder();
		ApnsConfig.Builder apnsConfigBuilder = ApnsConfig.builder().setAps(Aps.builder().setSound("default").build());

		if (notification.getBody() != null && notification.getTitle() != null) {
			androidConfigBuilder
					.setNotification(AndroidNotification.builder().setPriority(Priority.HIGH).setDefaultSound(true)
							.setSticky(false).build())
					.setPriority(com.google.firebase.messaging.AndroidConfig.Priority.HIGH);
		}
		if (notification.getMaxSendingDate() != null) {
			Duration duration = Duration.between(LocalDateTime.now(ZoneOffset.UTC), notification.getMaxSendingDate());
			LOGGER.warn("duration  = {} ", duration.toMillis());

			long millisecondsDifference = duration.toMillis();
			LOGGER.warn("difff {}", millisecondsDifference);
			androidConfigBuilder.setTtl(millisecondsDifference);
			apnsConfigBuilder.putHeader("apns-expiration", String.valueOf(duration.toMillis()));
		}
		LOGGER.warn("max date = {}", notification.getMaxSendingDate());
		messageBuilder.setAndroidConfig(androidConfigBuilder.build());
		messageBuilder.setApnsConfig(apnsConfigBuilder.build());
		try {
			LOGGER.warn("sending notification to token: {}", token.getValue());

			String res = FirebaseMessaging.getInstance().send(messageBuilder.build());

			notification.getNotificationFcmTokens().stream()
					.filter(t -> token.getValue().equals(t.getfcmTokenId().getValue())).findFirst()
					.ifPresent(t -> t.setStatut(EnumStatut.SENT));

			return res;
		} catch (FirebaseMessagingException ex) {
			handleSendingMessageErrors(ex.getMessagingErrorCode(), token.getValue(), notification);
			throw ex;
		}
	}

	/**
	 * Get the user locale from the preferred language
	 * 
	 * @param userLang
	 * @return
	 */
	private Locale getUserLocale(String userLang) {
		if (userLang.equals("fr")) {
			return Locale.FRENCH;
		} else if (userLang.equals("en")) {
			return Locale.ENGLISH;
		} else if (userLang.equals("de")) {
			return Locale.GERMAN;
		} else
			return Locale.getDefault();
	}

	private void translate(Object[] params, Locale locale) {
		// translate parameters : for parameter to be translated it must be prefixed by
		// '&' ex. COMBINED -> &COMBINED
		ResourceBundle exampleBundle = ResourceBundle.getBundle("i3label", locale);
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof String str && str.startsWith("&")) {
				params[i] = exampleBundle.getString(str.substring(1));
			}
		}
	}

	private static int countFormatParameters(String formatString) {
		// Regular expression to match format specifiers
		String regex = "%(?:\\d+\\$)?[\\d.]*[a-zA-Z]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(formatString);

		int count = 0;
		while (matcher.find()) {
			count++;
		}

		return count;
	}

	/**
	 * Handles various error scenarios that may occur while sending notifications
	 * using Firebase Cloud Messaging (FCM).
	 *
	 * @param code         The error code indicating the type of messaging error.
	 * @param token        The device token to which the notification was being
	 *                     sent.
	 * @param notification
	 */
	private void handleSendingMessageErrors(MessagingErrorCode code, String token, FirebaseNotification notification) {
		LOGGER.warn("handling error with code : {}", code);
		switch (code) {
		case THIRD_PARTY_AUTH_ERROR:
			LOGGER.error(
					"error while sending notification to {}, please verify your APNS certificate and FCM configuration",
					token);
			break;
		case UNREGISTERED:
		case SENDER_ID_MISMATCH:
		case INVALID_ARGUMENT:
			LOGGER.error("TOKEN Is NOT VALID, PERFORM TOKEN DELETE");
			notification.setNotificationFcmTokens(notification.getNotificationFcmTokens().stream()
					.filter(t -> !t.getfcmTokenId().getValue().equals(token)).collect(Collectors.toSet()));
			this.fcmTokenDataAccessService.deleteTokenByValue(token);
			break;
		case UNAVAILABLE:
			LOGGER.error("FCM IS NOT AVAILABLE NOW DUE TO A PROBLEM WE WILL RETRY SENDING THE NOTIF LATER");
			break;
		case QUOTA_EXCEEDED:
			LOGGER.error("Sending limit exceeded for the message target. token {} : ", token);
			break;
		default:
			break;
		}
		notification.getNotificationFcmTokens().stream().filter(t -> t.getfcmTokenId().getValue().equals(token))
				.findFirst().ifPresent(t -> t.setStatut(EnumStatut.ERROR));

	}

	/**
	 * Validates the notification request to ensure that required fields are not
	 * null.
	 *
	 * @param request The notification request containing TITLE_KEY, body, and
	 *                PLATEFORME_KEY.
	 * @throws BadRequestException If TITLE_KEY, body, or PLATEFORME_KEY is null.
	 */
	private void validateNotificationRequest(SendNotificationsRequest request) throws BadRequestException {
		if (request.getTitle() == null || request.getBody() == null || request.getPlateforme() == null) {
			throw new BadRequestException("BAD_ARGUMENTS: TITLE AND BODY AND PLATEFORME MUST NOT BE NULL");
		}
	}

	/**
	 * Builds an in-app notification using the provided notification builder and
	 * request data.
	 *
	 * @param notifBuilder The FirebaseNotification.Builder to build the
	 *                     notification.
	 * @param request      The request containing notification data.
	 */
	private void buildInAppNotification(FirebaseNotification.Builder notifBuilder, SendNotificationsRequest request) {
		notifBuilder.addDataFromBuilder(new NotificationMetadata.Builder().key("inapp_title").value(request.getTitle()))
				.addDataFromBuilder(new NotificationMetadata.Builder().key("whereToShow").value("TOASTR"))
				.addDataFromBuilder(new NotificationMetadata.Builder().key("inapp_body").value(request.getBody()))
				.type(NotificationTypeEnum.IN_APP);
	}

	/**
	 * Builds a push notification using the provided notification builder and
	 * request data.
	 *
	 * @param notifBuilder The FirebaseNotification.Builder to build the
	 *                     notification.
	 * @param request      The request containing notification data.
	 */
	private void buildPushNotification(FirebaseNotification.Builder notifBuilder, SendNotificationsRequest request) {
		notifBuilder.title(request.getTitle()).body(request.getBody()).type(NotificationTypeEnum.PUSH);
	}

	/**
	 * Calculates and returns the maximum sending date for the notification.
	 *
	 * @param request The request containing minutesToLives data.
	 * @return The maximum sending date as a LocalDateTime.
	 */
	private LocalDateTime getMaxSendingDate(SendNotificationsRequest request) {
		Long minutesToLives = request.getTimeToLives() != null ? request.getTimeToLives() : 2880;
		return LocalDateTime.now(ZoneOffset.UTC).plusMinutes(Long.valueOf(minutesToLives));
	}

	/**
	 * Sends notifications to the specified tokens based on the request data.
	 *
	 * @param notif   The FirebaseNotification to send.
	 * @param tokens  The set of FcmTokens to receive the notification.
	 * @param request The request data containing sentBy information.
	 */
	private void sendNotificationsToTokens(FirebaseNotification notif, Set<FcmToken> tokens,
			SendNotificationsRequest request) {
		tokens.stream().filter(
				t -> t.getValue() != null && (request.getSentBy() == null || !request.getSentBy().equals(t.getValue())))
				.forEach(t -> {
					NotificationFcmToken fcmNotif = createFcmNotification(notif, t);
					notif.addReceiver(fcmNotif);
					try {
						sendNotification(notif, t);
					} catch (FirebaseMessagingException e) {
						LOGGER.error("Error when sending FCM notification {} {}", e.getMessage(),
								e.getLocalizedMessage());
					}
				});
	}

	/**
	 * Creates a NotificationFcmToken object for the given notification and
	 * FcmToken.
	 *
	 * @param notif The FirebaseNotification associated with the token.
	 * @param t     The FcmToken to receive the notification.
	 * @return A NotificationFcmToken object.
	 */
	private NotificationFcmToken createFcmNotification(FirebaseNotification notif, FcmToken t) {
		NotificationFcmToken fcmNotif = new NotificationFcmToken();
		fcmNotif.setnotification(notif);
		fcmNotif.setfcmTokenId(t);
		fcmNotif.setStatut(EnumStatut.WAITING);
		fcmNotif.setSeen(false);
		fcmNotif.setSendDate(LocalDateTime.now(ZoneOffset.UTC));
		if (notif.getMaxSendingDate() != null) {
			fcmNotif.setMaxSendingDate(notif.getMaxSendingDate());
		} else {
			fcmNotif.setMaxSendingDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(2));
			notif.setMaxSendingDate(LocalDateTime.now(ZoneOffset.UTC).plusDays(2));
		}
		return fcmNotif;
	}

	private Boolean resolveTokensToSendNotifFilter(FcmToken token, FirebaseNotification notif) {
		return !(token.getValue() == null
				|| (BooleanUtils.isTrue(token.getIsMetierNotifDisabled())
						&& NOTIFICATIO_TYPE_METIER.equals(notif.getOriginType()))
				|| (BooleanUtils.isTrue(token.getIsSystemNotifDisabled())
						&& NOTIFICATIO_TYPE_SYSTEM.equals(notif.getOriginType())));
	}

	@Override
	public void deleteToken(String token) {
		this.fcmTokenDataAccessService.deleteTokenByValue(token);
	}

}

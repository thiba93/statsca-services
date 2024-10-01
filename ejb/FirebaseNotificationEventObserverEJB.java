package com.carrus.statsca.ejb;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.entity.FirebaseNotification;
import com.carrus.statsca.admin.entity.NotificationMetadata;
import com.carrus.statsca.admin.enums.NotificationTypeEnum;
import com.carrus.statsca.ejb.interfaces.FcmNotificationService;
import com.carrus.statsca.ejb.interfaces.FirebaseNotificationEventObserverService;
import com.carrus.statsca.utils.FirebaseNotificationUtils;
import com.pmc.club.entity.BetState;
import com.pmc.club.entity.RaceState;
import com.pmc.club.event.BetChange;
import com.pmc.club.event.BigPayOffChange;
import com.pmc.club.event.ChronologyLevel;
import com.pmc.club.event.ConnectionChange;
import com.pmc.club.event.ConnectionChange.ConnectionStateEnum;
import com.pmc.club.event.RaceChange;
import com.pmc.club.service.AuthorisedFormulationService;
import com.pmc.star3000.service.PartnerService;

/**
 * FirebaseNotificationEventObserverEJB is an implementation of the Firebase
 * Notification Event Observer service. It observes the completion of various
 * events and generates Firebase notifications based on the event type. These
 * notifications are sent to Firebase for delivery to end users as mobile
 * notifications.
 */
@Singleton
@Startup
public class FirebaseNotificationEventObserverEJB implements FirebaseNotificationEventObserverService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseNotificationEventObserverEJB.class);
	private static final String CLICK_ACTION_KEY = "CLICK_ACTION";
	private static final String CLICK_ACTION_JSON_DATA_KEY = "CLICK_ACTION_JSON_DATA";
	private static final String WHERE_TO_SHOW_INAPP_KEY = "whereToShow";
	private static final String OPEN_RACE_PAGE = "OPEN_RACE_PAGE";
	private static final String BIGPAYOFF_JSON_DATA_KEY = "BIG_PAY_OFF_DATA";

	@Inject
	private FcmNotificationService fcmNotificationService;

	@EJB
	private PartnerService partnerService;

	@EJB
	private AuthorisedFormulationService betService;

	/*
	 * @PostConstruct void init() { FirebaseNotification.Builder notifBuilder =
	 * null;
	 * 
	 * int betCodeRef = 36; // QIP int partnerId = 234; // LORO
	 * 
	 * Partner partner = partnerService.getPartner(partnerId); Bet bet =
	 * betService.getBet(betCodeRef);
	 * 
	 * BigPayOffChange bpoc = new BigPayOffChange(232323232L, partner);
	 * bpoc.setBet(bet); bpoc.setPayOff(123423); bpoc.setAmount(2.5);
	 * bpoc.setWheel(KeyEnum.COMBINED); bpoc.setRisk(RiskEnum.IN6);
	 * 
	 * Object[] params = extractParamsFromBigPayOffEvent(bpoc); notifBuilder =
	 * bigPayOffChangeHandler(bpoc);
	 * 
	 * fcmNotificationService.performTestSendingNotification(notifBuilder, params);
	 * }
	 */

	/**
	 * Observes the event of a completed transaction and logs it as a warning. This
	 * method is called during the AFTER_COMPLETION phase of the transaction. Based
	 * on the event type, it generates a FirebaseNotification object and sends it to
	 * Firebase to ensure its delivery to the end user as a mobile notification.
	 *
	 * @param chronologyLevel The level of chronology for the event.
	 * @param eventMeta       The metadata of the event.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void observeChronology(@Observes(during = TransactionPhase.AFTER_COMPLETION) ChronologyLevel chronologyLevel,
			EventMetadata eventMeta) {
		LOGGER.warn("Firebase EJB and event is fired {}", eventMeta.getType());
		// TODO when adding multi plateform
		// get admin notification configuration depends on which plateform this
		// notification should be delevired using
		// fetchNotificationConfigForAdmin(plateform) methode implemented on
		// NotificationConfigServiceEJB :
		// then depends on the config decide if we create the notification or no
		Object[] params = null;
		FirebaseNotification.Builder notifBuilder = null;
		/*
		 * LocalTime now = LocalTime.now(); LocalTime startTime = LocalTime.of(2, 0);
		 * LocalTime endTime = LocalTime.of(22, 0);
		 * LOGGER.warn("handling connection event before date test"); if
		 * (now.isBefore(startTime) || now.isAfter(endTime)) {
		 * LOGGER.warn("ignored notifications, we are between 22PM and 2AM");
		 * notifBuilder = null; } else
		 */
		// HRA 20240313 Désactivation des notifications demandée par MDA
		/*
		 * if (chronologyLevel instanceof ConnectionChange connection) { notifBuilder =
		 * this.connectionChangeHandler(connection); } else
		 */
		if (chronologyLevel instanceof RaceChange raceChange) {
			notifBuilder = this.raceChangeHandler(raceChange);
		} else if (chronologyLevel instanceof BetChange betChange) {
			notifBuilder = betChangeHandler(betChange);
		} /**
			 * FOR TEST else if (chronologyLevel instanceof SessionChange sessionChange) {
			 * notifBuilder = sessionChangeHandler(sessionChange);
			 * fcmNotificationService.performTestSendingNotification(notifBuilder); return;
			 * }
			 **/
		else if (chronologyLevel instanceof BigPayOffChange bigPayOffChange) {
			notifBuilder = bigPayOffChangeHandler(bigPayOffChange);
			params = extractParamsFromBigPayOffEvent(bigPayOffChange);

		}
		if (notifBuilder != null) {
			LOGGER.warn("performing send notification in an asynchronous way");
			// delegate sending to EJB service
			//LOGGER.debug("{} notifications sent , sending one more ", COUNT_BPN++);
			fcmNotificationService.performSendingNotification(notifBuilder, params);
			//LOGGER.debug("{} notifications sent , Limit reached ", COUNT_BPN);
		}
	}

	private Object[] extractParamsFromBigPayOffEvent(BigPayOffChange paymentChange) {
		// Format the integers as currency strings
		String formattedPayOff = FirebaseNotificationUtils.formatCurrencyAmount(paymentChange.getPayOff(), 0, 0);
		String formattedAmount = FirebaseNotificationUtils.formatCurrencyAmount(paymentChange.getAmount(), 0, 1);
		return new Object[] { /* for title */
				formattedPayOff, /* for body */ paymentChange.getPartner().getName(), formattedPayOff,
				formattedAmount, paymentChange.getBet().getLongName(),
				"&" + paymentChange.getWheel().name() };
	}

	/**
	 * Handles the race change event and generates a FirebaseNotification.Builder
	 * based on the RaceChange object.
	 *
	 * @param raceChange The RaceChange object representing the race change event.
	 * @return A FirebaseNotification.Builder configured for the race change event,
	 *         or null if the event type does not match any handled cases.
	 */
	private FirebaseNotification.Builder raceChangeHandler(RaceChange raceChange) {
		FirebaseNotification.Builder notifBuilder = null;
		LocalTime now = LocalTime.now();
		LocalTime startTime = LocalTime.of(2, 0);
		LocalTime endTime = LocalTime.of(22, 0);
		// LOGGER.warn("handling connection event before date test");
		boolean inFrame = !now.isBefore(startTime) && !now.isAfter(endTime);

		LOGGER.info("RACE CHANGE EVENT WITH STATE : {}", raceChange.getRaceState());

		/*
		 * if (RaceState.SALE_STOPPED.equals(raceChange.getRaceState())) { notifBuilder
		 * = new FirebaseNotification.Builder();
		 * notifBuilder.title("${notification.title.raceChange.sale_stopped}")
		 * .body("${notification.body.raceChange.sale_stopped} " +
		 * raceChange.getRace().getShortName() + " " + raceChange.getRace().getName())
		 * .addDataFromBuilder(new
		 * NotificationMetadata.Builder().value(OPEN_RACE_PAGE).key(CLICK_ACTION_KEY))
		 * .addDataFromBuilder(new NotificationMetadata.Builder()
		 * .value(FirebaseNotificationUtils.buildRaceNotifActionDataPayload(raceChange.
		 * getRace())) .key(CLICK_ACTION_JSON_DATA_KEY))
		 * .type(NotificationTypeEnum.PUSH); } else if
		 * (RaceState.STARTED_INTERUPTED.equals(raceChange.getRaceState())) {
		 * notifBuilder = new FirebaseNotification.Builder();
		 * notifBuilder.title("${notification.title.raceChange.started_interupted}")
		 * .body("${notification.body.raceChange.started_interupted} " +
		 * raceChange.getRace().getShortName() + " " + raceChange.getRace().getName())
		 * .addDataFromBuilder(new
		 * NotificationMetadata.Builder().value(OPEN_RACE_PAGE).key(CLICK_ACTION_KEY))
		 * .addDataFromBuilder(new NotificationMetadata.Builder()
		 * .value(FirebaseNotificationUtils.buildRaceNotifActionDataPayload(raceChange.
		 * getRace())) .key(CLICK_ACTION_JSON_DATA_KEY))
		 * .type(NotificationTypeEnum.PUSH); } else if
		 * (RaceState.CANCELED.equals(raceChange.getRaceState()) && inFrame) {
		 * notifBuilder = new FirebaseNotification.Builder();
		 * notifBuilder.title("${notification.title.raceChange.canceled}")
		 * .body("${notification.body.raceChange.canceled} " +
		 * raceChange.getRace().getShortName() + " " + raceChange.getRace().getName())
		 * .addDataFromBuilder(new
		 * NotificationMetadata.Builder().value(OPEN_RACE_PAGE).key(CLICK_ACTION_KEY))
		 * .addDataFromBuilder(new NotificationMetadata.Builder()
		 * .value(FirebaseNotificationUtils.buildRaceNotifActionDataPayload(raceChange.
		 * getRace())) .key(CLICK_ACTION_JSON_DATA_KEY))
		 * .type(NotificationTypeEnum.PUSH); } else
		 */
		if (RaceState.PAYMENT_STOPPED.equals(raceChange.getRaceState()) && inFrame) {
			notifBuilder = new FirebaseNotification.Builder();
			notifBuilder.title("${notification.title.raceChange.payement_stopped}")
					.body("${notification.body.raceChange.payement_stopped} " + raceChange.getRace().getShortName()
							+ " " + raceChange.getRace().getName())
					.addDataFromBuilder(new NotificationMetadata.Builder().value(OPEN_RACE_PAGE).key(CLICK_ACTION_KEY))
					.addDataFromBuilder(new NotificationMetadata.Builder()
							.value(FirebaseNotificationUtils.buildRaceNotifActionDataPayload(raceChange.getRace()))
							.key(CLICK_ACTION_JSON_DATA_KEY))
					.type(NotificationTypeEnum.PUSH);
		}
		if (notifBuilder != null) {
			notifBuilder.originType("METIER").maxSendigDate(LocalDateTime.now(ZoneOffset.UTC).plusHours(2));
		}
		return notifBuilder;
	}

	/**
	 * Handles the race change event and generates a FirebaseNotification.Builder
	 * based on the RaceChange object.
	 *
	 * @param raceChange The RaceChange object representing the race change event.
	 * @return A FirebaseNotification.Builder configured for the race change event,
	 *         or null if the event type does not match any handled cases.
	 */
//	private FirebaseNotification.Builder raceChangeHandler(RaceChange raceChange) {
//		FirebaseNotification.Builder notifBuilder = null;
//		LocalTime now = LocalTime.now();
//		LocalTime startTime = LocalTime.of(2, 0);
//		LocalTime endTime = LocalTime.of(22, 0);
//		//LOGGER.warn("handling connection event before date test");
//		boolean inFrame = !now.isBefore(startTime) && !now.isAfter(endTime);
//		
//		LOGGER.info("RACE CHANGE EVENT WITH STATE : {}", raceChange.getRaceState());
//		
//		//"notification.title.raceChange.".concat(RaceState.SALE_STOPPED.name())
//		
//		
//		if (RaceState.SALE_STOPPED.equals(raceChange.getRaceState())) {
//			notifBuilder = new FirebaseNotification.Builder();
//			notifBuilder.title("${notification.title.raceChange.sale_stopped}")
//					.body("${notification.body.raceChange.sale_stopped} " + raceChange.getRace().getShortName() + " "
//							+ raceChange.getRace().getName())
//					.addDataFromBuilder(new NotificationMetadata.Builder().value(OPEN_RACE_PAGE).key(CLICK_ACTION_KEY))
//					.addDataFromBuilder(new NotificationMetadata.Builder()
//							.value(FirebaseNotificationUtils.buildRaceNotifActionDataPayload(raceChange.getRace()))
//							.key(CLICK_ACTION_JSON_DATA_KEY))
//					.type(NotificationTypeEnum.PUSH);
//		} 
//		if (notifBuilder != null) {
//			notifBuilder.originType("METIER").maxSendigDate(LocalDateTime.now(ZoneOffset.UTC).plusHours(2));
//		}
//		return notifBuilder;
//	}

	/**
	 * Handles the bet change event and generates a FirebaseNotification.Builder
	 * based on the BetChange object.
	 *
	 * @param betChange The BetChange object representing the bet change event.
	 * @return A FirebaseNotification.Builder configured for the bet change event,
	 *         or null if the event type does not match the handled case.
	 */
	private FirebaseNotification.Builder betChangeHandler(BetChange betChange) {
		FirebaseNotification.Builder notifBuilder = null;
		LOGGER.info("RACE CHANGE EVENT WITH STATE : {}", betChange.getFormulation().getState());
		if (BetState.PAYMENT_INTERRUPTED.equals(betChange.getFormulation().getState())) {
			notifBuilder = new FirebaseNotification.Builder();
			notifBuilder.title("${notification.title.betChange.payement_interrupted}")
					.body("${notification.body.betChange.payement_interrupted} " + betChange.getRace().getShortName()
							+ " " + betChange.getRace().getName())
					.addDataFromBuilder(
							new NotificationMetadata.Builder().value("OPEN_EVENT_PAGE").key(CLICK_ACTION_KEY))
					.addDataFromBuilder(new NotificationMetadata.Builder()
							.value(FirebaseNotificationUtils.buildRaceNotifActionDataPayload(betChange.getRace()))
							.key(CLICK_ACTION_JSON_DATA_KEY))
					.type(NotificationTypeEnum.PUSH);
		}
		if (notifBuilder != null) {
			notifBuilder.originType("METIER").maxSendigDate(LocalDateTime.now(ZoneOffset.UTC).plusHours(2));
		}
		return notifBuilder;
	}

	private FirebaseNotification.Builder bigPayOffChangeHandler(BigPayOffChange paymentChange) {

		FirebaseNotification.Builder notifBuilder = null;
		LOGGER.info("Big Pay Off CHANGE EVENT : {}", "BIGPAYOFF CHANGE");
		notifBuilder = new FirebaseNotification.Builder();

		notifBuilder.title("@emoji{partyPopper} ${notification.title.paymentChange.bigPayOff}")
				.body("${notification.body.paymentChange.bigPayOff}")
				.addDataFromBuilder(new NotificationMetadata.Builder()
						.value(FirebaseNotificationUtils.buildBigPayOffNotifDataPayload(paymentChange))
						.key(BIGPAYOFF_JSON_DATA_KEY))
				.type(NotificationTypeEnum.PUSH);
		notifBuilder.originType("METIER").maxSendigDate(LocalDateTime.now(ZoneOffset.UTC).plusHours(10));
		return notifBuilder;
	}

	/**
	 * Handles the connection change event and generates a
	 * FirebaseNotification.Builder based on the ConnectionChange object.
	 *
	 * @param connection The ConnectionChange object representing the connection
	 *                   change event.
	 * @return A FirebaseNotification.Builder configured for the connection change
	 *         event, or null if the event type does not match the handled case.
	 */
	private FirebaseNotification.Builder connectionChangeHandler(ConnectionChange connection) {
		FirebaseNotification.Builder notifBuilder = null;

		// send the notif
		// LOGGER.warn("handling connection event after date test");
		LocalTime now = LocalTime.now();
		LocalTime startTime = LocalTime.of(2, 0);
		LocalTime endTime = LocalTime.of(22, 0);
		// LOGGER.warn("handling connection event before date test");
		if (!now.isBefore(startTime) && !now.isAfter(endTime)) {

			notifBuilder = new FirebaseNotification.Builder();
			notifBuilder
					.addDataFromBuilder(new NotificationMetadata.Builder().key("inapp_title")
							.value("${notification.title.connectionChange}"))
					.addDataFromBuilder(new NotificationMetadata.Builder().key(WHERE_TO_SHOW_INAPP_KEY).value("TOASTR"))
					.addDataFromBuilder(new NotificationMetadata.Builder().key("inapp_body")
							.value(ConnectionStateEnum.DISCONNECTED.equals(connection.getConnectionState())
									? "${notification.body.connectionChange.disconnected}"
									: "${notification.body.connectionChange.connected}"))
					.type(NotificationTypeEnum.IN_APP).maxSendigDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5))
					.originType("SYSTEM");
		} else {
			LOGGER.info("Ignored connection change notification");
		}
		return notifBuilder;
	}

}

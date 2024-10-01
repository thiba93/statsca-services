package com.carrus.statsca.admin.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents the NotificationFcmToken entity.
 *
 * The NotificationFcmToken class is a POJO representing a many-to-many relation
 * table between Notification and FcmToken entities. It holds information about
 * the association between a Notification and an FcmToken. It includes
 * attributes such as id, notificationId, fcmTokenId, seen, sendDate,
 * maxSendingDate, and statut. 
 */
@Table(name = "Notification_fcm_token")
@Entity
public class NotificationFcmToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fcm_token_id")
	private FcmToken fcmToken;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id")
	private FirebaseNotification notification;
	@Column(name = "seen")
	private Boolean seen;
	@Column(name = "send_date", columnDefinition = "DATE")
	private LocalDateTime sendDate;
	@Column(name = "max_sending_date", columnDefinition = "DATE")
	private LocalDateTime maxSendingDate;
	@Column(name = "statut")
	private EnumStatut statut;

	/**
	 * Enum representing the status of the NotificationFcmToken entity.
	 */
	public enum EnumStatut {
		SENT, WAITING, ERROR
	}

	/**
	 * Default constructor.
	 */
	public NotificationFcmToken() {
	}

	/**
	 * Parameterized constructor to create a new NotificationFcmToken instance set
	 * the given properties.
	 *
	 * @param id             The unique identifier for the NotificationFcmToken.
	 * @param notificationId The ID of the associated Notification.
	 * @param fcmTokenId     The ID of the associated FcmToken.
	 * @param seen           Flag to indicate if the NotificationFcmToken has been
	 *                       seen.
	 * @param sendDate       The date of sending the notification.
	 * @param maxSendingDate The maximum date for sending the notification.
	 * @param statut         The status of the NotificationFcmToken.
	 */
	public NotificationFcmToken(Long id, FirebaseNotification notification, FcmToken fcmToken, Boolean seen,
			LocalDateTime sendDate, LocalDateTime maxSendingDate, EnumStatut statut) {
		this.id = id;
		this.notification = notification;
		this.fcmToken = fcmToken;
		this.seen = seen;
		this.sendDate = sendDate;
		this.maxSendingDate = maxSendingDate;
		this.statut = statut;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FcmToken getfcmTokenId() {
		return fcmToken;
	}

	public void setfcmTokenId(FcmToken fcmToken) {
		this.fcmToken = fcmToken;
	}

	public FirebaseNotification getnotification() {
		return notification;
	}

	public void setnotification(FirebaseNotification notification) {
		this.notification = notification;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
	}

	public LocalDateTime getMaxSendingDate() {
		return maxSendingDate;
	}

	public void setMaxSendingDate(LocalDateTime maxSendingDate) {
		this.maxSendingDate = maxSendingDate;
	}

	public EnumStatut getStatut() {
		return statut;
	}

	public void setStatut(EnumStatut statut) {
		this.statut = statut;
	}
}
package com.carrus.statsca.admin.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.carrus.statsca.admin.enums.NotificationTypeEnum;

/**
 * Represents a Notification entity.
 *
 * The Notification class is used to store information about notifications,
 * including their title, body, associated data, topics, FCM tokens, priority,
 * and type.
 *
 * This class follows the Builder design pattern, allowing flexible and
 * expressive construction of Notification objects. The Builder class provides
 * methods to set each property of the Notification, and the build() method
 * constructs the final Notification instance.
 *
 * Please note that only push notifications have a title and body. In-app
 * notifications should have 'inapp_title' and 'inapp_body' inside the metadata.
 * Below are two examples demonstrating the construction of different types
 * of notifications:
 *
 *
 * @example
 * 
 * @Example 1: Creating a push notification with title and body
 *          Notification.Builder pushNotificationBuilder = new
 *          Notification.Builder();
 *          pushNotificationBuilder.title("push title")
 *          .body("push body")
 *          .type(NotificationTypeEnum.PUSH);
 * 
 *          Notification pushNotification = pushNotificationBuilder.build();
 * 
 * @Example 2: Creating an in-app notification with inapp_title and inapp_body
 *          metadata
 *          Notification.Builder inAppNotificationBuilder = new
 *          Notification.Builder();
 *          inAppNotificationBuilder.addDataFromBuilder(new
 *          NotificationMetadata.Builder()
 *          .key("inapp_title")
 *          .value("in app title example"))
 *          .addDataFromBuilder(new NotificationMetadata.Builder()
 *          .key(WHERE_TO_SHOW_INAPP_KEY)
 *          .value("TOASTR"))
 *          .addDataFromBuilder(new NotificationMetadata.Builder()
 *          .key("inapp_body")
 *          .value("in-app body example"))
 *          .type(NotificationTypeEnum.IN_APP);
 * 
 *          Notification inAppNotification = inAppNotificationBuilder.build();
 */
@Entity
@Table(name = "FirebaseNotification")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL, query = "SELECT fn FROM FirebaseNotification fn")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_VALUE, query = "SELECT fn FROM FirebaseNotification fn "
		+ "LEFT JOIN fn.notificationFcmTokens nft "
		+ "LEFT JOIN nft.fcmToken ft "
		+ "WHERE ft.value = :value and ((nft.sendDate >= :yesterdayDate " +
		"  AND nft.sendDate <= :today) or nft.seen = false)")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL_UNFETCHED_BY_TOKEN_VALUE, query = "SELECT fn FROM FirebaseNotification fn "
		+ "LEFT JOIN fn.notificationFcmTokens nft "
		+ "LEFT JOIN nft.fcmToken ft "
		+ "WHERE ft.value = :value and ((nft.sendDate >= :yesterdayDate " +
		"  AND nft.sendDate <= :today) or nft.seen = false) and fn.id > :lastNotifId")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_ID, query = "SELECT fn FROM FirebaseNotification fn "
		+ "LEFT JOIN fn.notificationFcmTokens nft "
		+ "LEFT JOIN nft.fcmToken ft "
		+ "WHERE ft.id = :id")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_IDS_LIST, query = "SELECT fn FROM FirebaseNotification fn "
		+ "LEFT JOIN fn.notificationFcmTokens nft "
		+ "LEFT JOIN nft.fcmToken ft "
		+ "WHERE ft.id in :ids")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_ID_AND_NOT_SEEN, query = "SELECT fn FROM FirebaseNotification fn "
		+ "LEFT JOIN fn.notificationFcmTokens nft "
		+ "LEFT JOIN nft.fcmToken ft "
		+ "WHERE ft.id = :id AND nft.seen = false")
@NamedQuery(name = FirebaseNotification.RETRIEVE_ALL_BY_TOKEN_VALUE_AND_NOT_SEEN, query = "SELECT fn FROM FirebaseNotification fn "
		+ "LEFT JOIN fn.notificationFcmTokens nft "
		+ "LEFT JOIN nft.fcmToken ft "
		+ "WHERE ft.id = :id AND nft.seen = false")
public class FirebaseNotification {
	/*
	 * named queries constants
	 */
	public static final String RETRIEVE_BY_ID = "FirebaseNotification.retrieveById";
	public static final String RETRIEVE_ALL = "FirebaseNotification.retrieveAllNotifications";
	public static final String RETRIEVE_ALL_BY_TOKEN_VALUE = "FirebaseNotification.retrieveAllNotificationsByTokenValue";
	public static final String RETRIEVE_ALL_UNFETCHED_BY_TOKEN_VALUE = "FirebaseNotification.retrieveAllUnfetchedNotificationsByTokenValue";
	public static final String RETRIEVE_ALL_BY_TOKEN_ID = "FirebaseNotification.retrieveAllNotificationsByTokenID";
	public static final String RETRIEVE_ALL_BY_TOKEN_IDS_LIST = "FirebaseNotification.retrieveAllNotificationsByTokenIdsList";
	public static final String RETRIEVE_ALL_BY_TOKEN_ID_AND_NOT_SEEN = "FirebaseNotification.retrieveAllNotificationsByTokenIDAndNotSeen";
	public static final String RETRIEVE_ALL_BY_TOKEN_VALUE_AND_NOT_SEEN = "FirebaseNotification.retrieveAllNotificationsByTokenValueAndNotSeen";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "title")
	private String title;
	@Column(name = "body")
	private String body;
	@OneToMany(mappedBy = "notification")
	private Set<NotificationMetadata> data;
	@Column(name = "topics")
	private String topics;
	@OneToMany(mappedBy = "notification", cascade = CascadeType.ALL)
	private Set<NotificationFcmToken> notificationFcmTokens;
	@Column(name = "priority")
	private Integer priority;
	@Column(name = "type")
	private NotificationTypeEnum type;
	@Column(name = "max_sending_date", columnDefinition = "DATE")
	private LocalDateTime maxSendingDate;
	@Transient
	private String originType;

	// Default constructor
	public FirebaseNotification() {
		super();
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Set<NotificationMetadata> getData() {
		return data;
	}

	public void setData(Set<NotificationMetadata> data) {
		this.data = data;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public Set<NotificationFcmToken> getNotificationFcmTokens() {
		return notificationFcmTokens;
	}

	public void setNotificationFcmTokens(Set<NotificationFcmToken> notificationFcmTokens) {
		this.notificationFcmTokens = notificationFcmTokens;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public NotificationTypeEnum getType() {
		return type;
	}

	public void setType(NotificationTypeEnum type) {
		this.type = type;
	}

	public LocalDateTime getMaxSendingDate() {
		return maxSendingDate;
	}

	public void setMaxSendingDate(LocalDateTime maxSendingDate) {
		this.maxSendingDate = maxSendingDate;
	}

	public void addReceiver(NotificationFcmToken fcmNotif) {
		if (this.notificationFcmTokens == null) {
			this.notificationFcmTokens = new HashSet<>();
		}
		fcmNotif.setnotification(this);
		this.notificationFcmTokens.add(fcmNotif);

	}

	public void addData(NotificationMetadata metadata) {
		if (this.data == null) {
			this.data = new HashSet<>();
		}
		metadata.setNotification(this);
		this.data.add(metadata);
	}

	

	public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }



    /**
	 * FirebaseNotification builder
	 */
	public static class Builder {
		private Long id;
		private String title;
		private String body;
		private Set<NotificationMetadata> data = new HashSet<>();
		private String topics;
		private Set<NotificationFcmToken> notificationFcmTokens = new HashSet<>();
		private Integer priority;
		private NotificationTypeEnum type;
		private Set<NotificationMetadata.Builder> metadataBuilders = new HashSet<>();
		private LocalDateTime maxSendingDate;
		private String originType;

		public Builder() {
			// empty constructor
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder addData(NotificationMetadata metadata) {
			this.data.add(metadata);
			return this;
		}

		public Builder addDataFromBuilder(NotificationMetadata.Builder metadataBuilder) {
			metadataBuilders.add(metadataBuilder);
			return this;
		}

		public Builder addAllData(Set<NotificationMetadata> metadata) {
			this.data.addAll(metadata);
			return this;
		}

		public Builder topics(String topics) {
			this.topics = topics;
			return this;
		}

		public Builder addFcmToken(NotificationFcmToken fcmToken) {
			this.notificationFcmTokens.add(fcmToken);
			return this;
		}

		public Builder addAllFcmToken(Set<NotificationFcmToken> fcmToken) {
			this.notificationFcmTokens.addAll(fcmToken);
			return this;
		}

		public Builder priority(Integer priority) {
			this.priority = priority;
			return this;
		}

		public Builder maxSendigDate(LocalDateTime maxSendingDate) {
			this.maxSendingDate = maxSendingDate;
			return this;
		}

		public Builder type(NotificationTypeEnum type) {
			this.type = type;
			return this;
		}
		
		public Builder originType(String type) {
			this.originType = type;
			return this;
		}

		public FirebaseNotification build() {
			FirebaseNotification notification = new FirebaseNotification();
			notification.setId(id);
			notification.setTitle(title);
			notification.setBody(body);
			if (this.metadataBuilders.size() > 0) {
				this.metadataBuilders.forEach(b -> {
					b.notification(notification);
					data.add(b.build());
				});
			}
			notification.setData(data);
			notification.setTopics(topics);
			notification.setNotificationFcmTokens(notificationFcmTokens);
			notification.setPriority(priority);
			notification.setType(type);
			notification.setMaxSendingDate(maxSendingDate);
			notification.setOriginType(originType);
			return notification;
		}
	}

}
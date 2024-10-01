package com.carrus.statsca.admin.entity;

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
 * Represents the NotificationMetadata entity.
 *
 * The NotificationMetadata class is a POJO representing the data payload of a
 * notification. It has a Many-to-One relation with the entity
 * FirebaseNotification. It holds two principal attributes:
 * - key: the data attribute key
 * - value: the attribute value
 * Finally, a set of NotificationMetadata instances will represent the final
 * notification data payload.
 */
@Table(name = "Notification_metadata")
@Entity
public class NotificationMetadata {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "metadata_key")
	private String key;
	@Column(name = "value")
	private String value;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id")
	private FirebaseNotification notification;

	/**
	 * Default constructor.
	 */
	public NotificationMetadata() {
	}

	/**
	 * Parameterized constructor to create a new NotificationMetadata instance with
	 * the given properties.
	 *
	 * @param id           The unique identifier for the metadata.
	 * @param key          The key associated with the metadata.
	 * @param value        The value associated with the metadata.
	 * @param notification The notification to which this metadata belongs.
	 */
	public NotificationMetadata(Long id, String key, String value, FirebaseNotification notification) {
		this.id = id;
		this.key = key;
		this.value = value;
		this.notification = notification;
	}

	// private constructor used by the builder pattern

	// Getters and Setters for id, key, value, and notification properties
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FirebaseNotification getNotification() {
		return notification;
	}

	public void setNotification(FirebaseNotification notification) {
		this.notification = notification;
	}

	public static class Builder {
		private Long id;
		private String key;
		private String value;
		private FirebaseNotification notification;

		public Builder() {
			// empty constructor
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder key(String key) {
			this.key = key;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder notification(FirebaseNotification notification) {
			this.notification = notification;
			return this;
		}

		public NotificationMetadata build() {
			NotificationMetadata metadata = new NotificationMetadata();
			metadata.setId(id);
			metadata.setKey(key);
			metadata.setValue(value);
			metadata.setNotification(notification);
			return metadata;
		}
	}

}
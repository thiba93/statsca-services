package com.carrus.statsca.admin.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Represents the FcmToken entity.
 *
 * The FcmToken class is a POJO representing a database entity that holds
 * information about Firebase Cloud Messaging (FCM) tokens. It includes
 * attributes such as id, plateforme, value, and issueDate. It also provides a
 * Builder pattern to construct FcmToken instances.
 */
@Entity
@Table(name = "Fcm_token")
@NamedQuery(name = FcmToken.RETRIEVE_BY_VALUE, query = "SELECT table FROM FcmToken table where table.value = :value")
@NamedQuery(name = FcmToken.RETRIEVE_BY_VALUE_AND_PLATEFORME, query = "SELECT table FROM FcmToken table where table.value = :value and table.plateforme = :plateforme")
@NamedQuery(name = FcmToken.RETRIEVE_ALL_TOKENS, query = "SELECT table FROM FcmToken table where table.value is not null")
@NamedQuery(name = FcmToken.RETRIEVE_EXPIRED_TOKENS, query = "SELECT table FROM FcmToken table where table.issueDate <= :oneMonthEarlierDate")
@NamedQuery(name = FcmToken.RETRIEVE_ALL_TOKENS_BY_PLATEFORM, query = "SELECT table FROM FcmToken table where table.plateforme = :plateforme")
public class FcmToken {

	public static final String RETRIEVE_BY_VALUE = "FcmToken.retrieveByValue";
	public static final String RETRIEVE_ALL_TOKENS = "FcmToken.retrieveAllTokens";
	public static final String RETRIEVE_ALL_TOKENS_BY_PLATEFORM = "FcmToken.retrieveAllTokensByPlateform";
	public static final String RETRIEVE_BY_VALUE_AND_PLATEFORME = "FcmToken.retrieveAllTokensByPlateformAndValue";
	public static final String RETRIEVE_EXPIRED_TOKENS = "FcmToken.retrieveAllExpiredTokens";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// palteforme = l'affaire au quel l'utilisateur s'inscrit
	@Column(name = "plateforme")
	private String plateforme;

	@Column(name = "value")
	private String value;

	@Column(name = "language_pref")
	private String languagePref;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ID_USR")
	private UserEntity user;

	@Column(name = "issue_date", columnDefinition = "DATE")
	private LocalDateTime issueDate;
	@Column(name = "sys_notif_disabled", columnDefinition = "DATE")
	private Boolean isSystemNotifDisabled;
	@Column(name = "metier_notif_disabled", columnDefinition = "DATE")
	private Boolean isMetierNotifDisabled;

	@OneToMany(mappedBy = "fcmToken", cascade = CascadeType.ALL)
	private List<NotificationFcmToken> notificationFcmTokens;

	/**
	 * Default constructor.
	 */
	public FcmToken() {
		super();
	}

	/**
	 * Parameterized constructor to create a new FcmToken instance with the given
	 * properties.
	 *
	 * @param id         The unique identifier for the FcmToken.
	 * @param plateforme The platform associated with the FcmToken.
	 * @param value      The value of the FcmToken.
	 * @param issueDate  The date when the FcmToken was issued.
	 */
	public FcmToken(Long id, String plateforme, String value, LocalDateTime issueDate) {
		super();
		this.id = id;
		this.plateforme = plateforme;
		this.value = value;
		this.issueDate = issueDate;
	}

	/**
	 * private constructor used by the builder pattern
	 */
	private FcmToken(Builder builder) {
		super();
		this.plateforme = builder.plateforme;
		this.value = builder.value;
		this.issueDate = builder.issueDate;
		this.languagePref = builder.languagePref;
	}

	// Getters and Setters for id, plateforme, value, and issueDate

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public String getLanguagePref() {
		return languagePref;
	}

	public String getPlateforme() {
		return plateforme;
	}

	public LocalDateTime getIssueDate() {
		return issueDate;
	}

	public void setValue(String value) {
		this.value = value;

	}

	public void setLanguagePref(String value) {
		this.languagePref = value;

	}

	/**
	 * Sets the platform for the FcmToken.
	 *
	 * @param plateforme The platform associated with the FcmToken.
	 * @return The Builder instance with the platform set.
	 */
	public void setPlateforme(String plateforme) {
		this.plateforme = plateforme;

	}
	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public void setIssueDate(LocalDateTime issueDate) {
		this.issueDate = issueDate;

	}

	public Boolean getIsSystemNotifDisabled() {
		return isSystemNotifDisabled;
	}

	public void setIsSystemNotifDisabled(Boolean isSystemNotifDisabled) {
		this.isSystemNotifDisabled = isSystemNotifDisabled;
	}

	public Boolean getIsMetierNotifDisabled() {
		return isMetierNotifDisabled;
	}

	public void setIsMetierNotifDisabled(Boolean isMetierNotifDisabled) {
		this.isMetierNotifDisabled = isMetierNotifDisabled;
	}

	/**
	 * The Builder class for constructing FcmToken objects using the builder design
	 * pattern.
	 */
	public static class Builder {

		private String value;
		private String plateforme;
		private String languagePref;
		private LocalDateTime issueDate;

		/**
		 * Sets the value for the FcmToken.
		 *
		 * @param value The value of the FcmToken.
		 * @return The Builder instance with the value set.
		 */
		public Builder setValue(String value) {
			this.value = value;
			return this;
		}

		/**
		 * Sets the value for the FcmToken.
		 *
		 * @param value The value of the FcmToken.
		 * @return The Builder instance with the value set.
		 */
		public Builder setLanguagePref(String value) {
			this.languagePref = value;
			return this;
		}

		/**
		 * Sets the platform for the FcmToken.
		 *
		 * @param plateforme The platform associated with the FcmToken.
		 * @return The Builder instance with the platform set.
		 */
		public Builder setPlateforme(String plateforme) {
			this.plateforme = plateforme;
			return this;
		}

		/**
		 * Sets the issue date for the FcmToken to the current date.
		 *
		 * @return The Builder instance with the issue date set to the current date.
		 */
		public Builder buildIssueDateNow() {
			this.issueDate = LocalDateTime.now();
			return this;
		}

		/**
		 * Sets the issue date for the FcmToken.
		 *
		 * @param issueDate The date when the FcmToken was issued.
		 * @return The Builder instance with the issue date set.
		 */
		public Builder setIssueDate(LocalDateTime issueDate) {
			this.issueDate = issueDate;
			return this;
		}

		/**
		 * Constructs a new FcmToken instance with the provided properties.
		 *
		 * @return A new FcmToken object.
		 */
		public FcmToken build() {
			return new FcmToken(this);
		}
	}

	/**
	 * Static function to return the Builder for FcmToken objects.
	 *
	 * @return The Builder instance for FcmToken.
	 */
	public static Builder builder() {
		return new Builder();
	}
}
package com.carrus.statsca.admin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "IgnoredNotificationAdminConfiguration")
@NamedQuery(name = IgnoredNotificationAdminConfiguration.RETRIEVE_ALL_BY_PLATEFORME, query = "SELECT no FROM IgnoredNotificationAdminConfiguration no where no.platform = :plateform ")
@NamedQuery(name = IgnoredNotificationAdminConfiguration.RETRIEVE_BY_ID, query = "SELECT no FROM IgnoredNotificationAdminConfiguration no where id = :id ")
@NamedQuery(name = IgnoredNotificationAdminConfiguration.RETRIEVE_BY_ORIGIN, query = "SELECT inac FROM IgnoredNotificationAdminConfiguration inac left join  inac.notificationOrigins no where no.origin = :origin")
public class IgnoredNotificationAdminConfiguration {

    public static final String RETRIEVE_ALL_BY_PLATEFORME = "IgnoredNotificationAdminConfiguration.retrieveAllByPlateform";
    public static final String RETRIEVE_BY_ID = "IgnoredNotificationAdminConfiguration.retrieveAllByID";
    public static final String RETRIEVE_BY_ORIGIN = "IgnoredNotificationAdminConfiguration.retrieveByOrigin";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_origin_id")
    private NotificationOrigins notificationOrigins;
    @Column(name = "platform")
    private String platform;

    public IgnoredNotificationAdminConfiguration() {
        // empty constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationOrigins getNotificationOrigins() {
        return notificationOrigins;
    }

    public void setNotificationOrigins(NotificationOrigins notificationsOrigins) {
        this.notificationOrigins = notificationsOrigins;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((notificationOrigins == null) ? 0 : notificationOrigins.hashCode());
        result = prime * result + ((platform == null) ? 0 : platform.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IgnoredNotificationAdminConfiguration other = (IgnoredNotificationAdminConfiguration) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (notificationOrigins == null) {
            if (other.notificationOrigins != null)
                return false;
        } else if (!notificationOrigins.equals(other.notificationOrigins))
            return false;
        if (platform == null) {
            if (other.platform != null)
                return false;
        } else if (!platform.equals(other.platform))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IgnoredNotificationAdminConfiguration [id=" + id + ", notificationsOrigins=" + notificationOrigins
                + ", platform=" + platform + "]";
    }

}

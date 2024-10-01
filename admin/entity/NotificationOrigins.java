package com.carrus.statsca.admin.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "NotificationOrigins")
@NamedQuery(name = NotificationOrigins.RETRIEVE_ALL, query = "SELECT no FROM NotificationOrigins no ")
@NamedQuery(name = NotificationOrigins.RETRIEVE_BY_ORIGIN, query = "SELECT no FROM NotificationOrigins no where no.origin = :origin ")
public class NotificationOrigins {

    public static final String RETRIEVE_ALL = "NotificationsOrigins.retrieveAll";
    public static final String RETRIEVE_BY_ORIGIN = "NotificationsOrigins.retrieveByType";


    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
    @Column(name = "origin")
    private String origin;
    @Column(name = "type")
    private String type;
	@OneToMany(mappedBy = "notificationOrigins", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<IgnoredNotificationAdminConfiguration> adminConfigurations;

    public NotificationOrigins() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

       public Set<IgnoredNotificationAdminConfiguration> getAdminConfigurations() {
        return adminConfigurations;
    }

    public void setAdminConfigurations(Set<IgnoredNotificationAdminConfiguration> adminConfigurations) {
        this.adminConfigurations = adminConfigurations;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        NotificationOrigins other = (NotificationOrigins) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (origin == null) {
            if (other.origin != null)
                return false;
        } else if (!origin.equals(other.origin))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "NotificationsOrigin [id=" + id + ", origin=" + origin + ", type=" + type + "]";
    }

 
}

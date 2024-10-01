package com.carrus.statsca.admin.dto;

public class NotificationConfigDTO {

    private Long notificationOriginId;
    private String notificationOrigin;
    private Long adminConfigId;
    private Boolean activated;
    private String plateforme;

    public NotificationConfigDTO() {
        // empty constructor
    }

    public Long getNotificationOriginId() {
        return notificationOriginId;
    }

    public void setNotificationOriginId(Long notificationOriginId) {
        this.notificationOriginId = notificationOriginId;
    }

    public String getNotificationOrigin() {
        return notificationOrigin;
    }

    public void setNotificationOrigin(String notificationOrigin) {
        this.notificationOrigin = notificationOrigin;
    }

    public Long getAdminConfigId() {
        return adminConfigId;
    }

    public void setAdminConfigId(Long adminConfigId) {
        this.adminConfigId = adminConfigId;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean isIgnored) {
        this.activated = isIgnored;
    }

    public String getPlateforme() {
        return plateforme;
    }

    public void setPlateforme(String plateforme) {
        this.plateforme = plateforme;
    }
    
    
}

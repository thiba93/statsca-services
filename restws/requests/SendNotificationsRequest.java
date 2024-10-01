package com.carrus.statsca.restws.requests;

import java.io.Serializable;

import com.carrus.statsca.admin.enums.NotificationTypeEnum;

/**
 * Represents a request to send notifications.
 * This class contains the necessary information for sending notifications.
 */
public class SendNotificationsRequest implements Serializable {

    /** The title of the notification. (Required) */
    private String title;

    /** The body content of the notification. (Required) */
    private String body;

    /** The platform on which the notification will be sent. (Required) */
    private String plateforme;

    /** The type of notification. (Optional) */
    private NotificationTypeEnum type;

    /**
     * The time duration in minutes for which the notification will be live.
     * (Optional)
     */
    private Long timeToLives;

    /**
     * The token responsible for sending the notification. (if the client is
     * subscribed to FCM ) (Optional)
     */
    private String sentBy;

    /**
     * Get the title of the notification.
     * 
     * @return The title of the notification.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the notification.
     * 
     * @param title The title of the notification.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the body content of the notification.
     * 
     * @return The body content of the notification.
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the body content of the notification.
     * 
     * @param body The body content of the notification.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get the platform on which the notification will be sent.
     * 
     * @return The platform of the notification.
     */
    public String getPlateforme() {
        return plateforme;
    }

    /**
     * Set the platform on which the notification will be sent.
     * 
     * @param platform The platform of the notification.
     */
    public void setPlateforme(String platform) {
        this.plateforme = platform;
    }

    /**
     * Get the type of the notification. (Optional)
     * 
     * @return The type of the notification.
     */
    public NotificationTypeEnum getType() {
        return type;
    }

    /**
     * Set the type of the notification. (Optional)
     * 
     * @param type The type of the notification.
     */
    public void setType(NotificationTypeEnum type) {
        this.type = type;
    }


    /**
     * Get the entity responsible for sending the notification. (Optional)
     * 
     * @return The entity responsible for sending the notification.
     */
    public String getSentBy() {
        return sentBy;
    }

    /**
     * Set the entity responsible for sending the notification. (Optional)
     * 
     * @param sentBy The entity responsible for sending the notification.
     */
    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Long getTimeToLives() {
        return timeToLives;
    }

    public void setTimeToLives(Long timeToLives) {
        this.timeToLives = timeToLives;
    }

    
}
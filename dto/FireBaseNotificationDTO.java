package com.carrus.statsca.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.carrus.statsca.admin.enums.NotificationTypeEnum;

public class FireBaseNotificationDTO {
	private Long id;
	private String title;
	private String body;
	private Map<String,String> data;
	private String topics;
	private Set<NotificationFcmTokenDTO> notificationFcmTokens;
	private Integer priority;
	private NotificationTypeEnum type;

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

	
	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	public void addData(String key,String value) {
		if(data == null) {
			data = new HashMap<>();
		}
		data.put(key, value);
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public Set<NotificationFcmTokenDTO> getNotificationFcmTokens() {
		return notificationFcmTokens;
	}

	public void setNotificationFcmTokens(Set<NotificationFcmTokenDTO> notificationFcmTokens) {
		this.notificationFcmTokens = notificationFcmTokens;
	}

	public void addNotificationFcmTokens(NotificationFcmTokenDTO fcmToken) {
		if (this.notificationFcmTokens == null) {
			this.notificationFcmTokens = new HashSet<>();
		}
		this.notificationFcmTokens.add(fcmToken);
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

}
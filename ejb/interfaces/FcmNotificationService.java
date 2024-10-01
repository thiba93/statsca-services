package com.carrus.statsca.ejb.interfaces;

import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ws.rs.BadRequestException;

import com.carrus.statsca.admin.entity.FcmToken;
import com.carrus.statsca.admin.entity.FirebaseNotification;
import com.carrus.statsca.admin.entity.FirebaseNotification.Builder;
import com.carrus.statsca.dto.FireBaseNotificationDTO;
import com.carrus.statsca.restws.requests.SendNotificationsRequest;
import com.google.firebase.messaging.FirebaseMessagingException;

@Local
public interface FcmNotificationService {

	public void saveTokenToDataBase(Map<String, String> tokenPayload);

	public void updateTokenInDataBase(Map<String, String> request);

	public Set<FireBaseNotificationDTO> retrieveNotificationsByTokenValue(String token);

	public Set<FireBaseNotificationDTO> retrieveUnfetchidNotificationsByTokenValue(String token, Long lastNotifId);

	public void markAsSeen(Map<String, Object> request);

	public void sendCustomNotification(SendNotificationsRequest request) throws BadRequestException;
	
	public void performSendingNotification(Builder notifBuilder, Object...params);
	
    public String sendNotification(FirebaseNotification notification, FcmToken token, Object... params) throws FirebaseMessagingException;

    public void deleteToken(String token);

}
package com.carrus.statsca.admin.ejb.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;

import com.carrus.statsca.admin.entity.FirebaseNotification;

@Local
public interface FirebaseNotificationDataAccessService {

	public void saveNotification(FirebaseNotification firebaseNotification);

	public boolean updateNotification(FirebaseNotification firebaseNotification);

	public FirebaseNotification retrieveById(Long id);

	public Set<FirebaseNotification> retrieveAllNotifications();

	public Set<FirebaseNotification> retrieveByReceiverTokenValue(String token);

	public Set<FirebaseNotification> retrieveByReceiverTokenId(Long idToken);

	public Set<FirebaseNotification> retrieveByReceiverTokenIdsList(Set<Long> tokensIds);

	public Set<FirebaseNotification> retrieveByReceiverTokenValueAndNotSeen(String token);

	public Set<FirebaseNotification> retrieveByReceiverTokenIdAndNotSeen(Long idToken);

	public boolean markNotificationAsSeenByIds(List<Integer> ids);

	public boolean markNotificationAsSeenByFcmToken(String token);

	public Set<FirebaseNotification> retrieveUnfetchidNotificationsByTokenValue(String token, Long lastNotifId);

	public boolean deleteNotificationsByTokenIds(Set<Long> tokensIds);

	public Map<String, String> retrieveAllNotifMessagesMap();
}
package com.carrus.statsca.ejb.interfaces;

import java.util.Map;

public interface FirebaseService {
    public void initFirebase() throws Exception;
	public void cleanUpFcmTokenDataBase();
	public Map<String, String> getNotificationMessages();
    
}

package com.carrus.statsca.ejb.interfaces;

import java.util.List;
import java.util.Map;

import com.carrus.statsca.admin.dto.NotificationConfigDTO;

public interface NotificationConfigService {
    public Map<String, Boolean> fetchUserNotificationConfig(String fcmToken);

    public List<NotificationConfigDTO> fetchNotificationConfigForAdmin(String plateform);

    public void enableDisableUserNotificationConfig(String fcmToken, String notificationOriginsType);

    public void enableDisableUserNotificationForPlateforme(NotificationConfigDTO notificationConfigDTO);
}

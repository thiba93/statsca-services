package com.carrus.statsca.admin.ejb.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.carrus.statsca.admin.entity.IgnoredNotificationAdminConfiguration;
import com.carrus.statsca.admin.entity.NotificationOrigins;

@Local
public interface NotificationConfigDataAccess {

    public List<NotificationOrigins> fetchAllNotificationOrigins();

    public List<IgnoredNotificationAdminConfiguration> fetchAllIgnoredNotificationOriginsByPlateform(String plateform);

    public NotificationOrigins fetchNotifOriginByOrigin(String notificationOrigin);

    public void persistIgnoredNotificationConfig(IgnoredNotificationAdminConfiguration iNotificationAdminConfiguration);

    public boolean deleteIgnoredNotificationConfig(
            IgnoredNotificationAdminConfiguration iNotificationAdminConfiguration);

    public IgnoredNotificationAdminConfiguration fetchIgnoredNotificationConfigById(Long id);

    public IgnoredNotificationAdminConfiguration fetchIgnoredNotificationConfigByOrigin(String origin);

}

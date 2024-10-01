package com.carrus.statsca.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.dto.NotificationConfigDTO;
import com.carrus.statsca.admin.ejb.interfaces.FcmTokenDataAccessService;
import com.carrus.statsca.admin.ejb.interfaces.NotificationConfigDataAccess;
import com.carrus.statsca.admin.entity.FcmToken;
import com.carrus.statsca.admin.entity.IgnoredNotificationAdminConfiguration;
import com.carrus.statsca.admin.entity.NotificationOrigins;
import com.carrus.statsca.ejb.interfaces.NotificationConfigService;

@Singleton
@Startup
public class NotificationConfigServiceEJB implements NotificationConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConfigServiceEJB.class);

    private static final String NOTIFICATIO_TYPE_SYSTEM = "SYSTEM";
    private static final String NOTIFICATIO_TYPE_METIER = "METIER";

    @Inject
    private NotificationConfigDataAccess notificationConfigDataAccess;
    @Inject
    private FcmTokenDataAccessService fcmTokenDataAccessService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, Boolean> fetchUserNotificationConfig(String fcmToken) throws InternalError {

        Map<String, Boolean> result = new HashMap<>();
        result.put(NOTIFICATIO_TYPE_METIER, true);
        result.put(NOTIFICATIO_TYPE_SYSTEM, true);
        Set<FcmToken> token = this.fcmTokenDataAccessService.retrieveTokenByValue(fcmToken);
        if (token != null) {
            token.stream().findFirst().ifPresent(t -> {
                if (BooleanUtils.isTrue(t.getIsMetierNotifDisabled())) {
                    result.put(NOTIFICATIO_TYPE_METIER, false);
                }
                if (BooleanUtils.isTrue(t.getIsSystemNotifDisabled())) {
                    result.put(NOTIFICATIO_TYPE_SYSTEM, false);
                }
            });
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enableDisableUserNotificationConfig(String fcmToken, String notificationOriginsType) {
        Set<FcmToken> tokenList = this.fcmTokenDataAccessService.retrieveTokenByValue(fcmToken);
        if (notificationOriginsType.equals(NOTIFICATIO_TYPE_SYSTEM)) {
            tokenList.forEach(t -> {
                t.setIsSystemNotifDisabled(BooleanUtils.isNotTrue(t.getIsSystemNotifDisabled()));
                this.fcmTokenDataAccessService.updateToken(t);
            });
        } else if (notificationOriginsType.equals(NOTIFICATIO_TYPE_METIER)) {
            tokenList.forEach(t -> {
                t.setIsMetierNotifDisabled(BooleanUtils.isNotTrue(t.getIsMetierNotifDisabled()));
                this.fcmTokenDataAccessService.updateToken(t);
            });
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<NotificationConfigDTO> fetchNotificationConfigForAdmin(String plateform) {
        List<NotificationOrigins> notificationOrigins = this.notificationConfigDataAccess.fetchAllNotificationOrigins();
        List<IgnoredNotificationAdminConfiguration> ignoredNotificationAdminConfigurations = this.notificationConfigDataAccess
                .fetchAllIgnoredNotificationOriginsByPlateform(plateform);
        List<NotificationConfigDTO> configDTOs = new ArrayList<>();
        notificationOrigins.forEach(no -> {
            NotificationConfigDTO notifConfig = new NotificationConfigDTO();
            notifConfig.setNotificationOrigin(no.getOrigin());
            notifConfig.setNotificationOriginId(no.getId());
            notifConfig.setActivated(true);
            notifConfig.setPlateforme(plateform);
            Optional<IgnoredNotificationAdminConfiguration> optional = ignoredNotificationAdminConfigurations.stream()
                    .filter((ingac) -> {
                        return ingac.getNotificationOrigins() != null
                                && no.getId().equals(ingac.getNotificationOrigins().getId());
                    }).findFirst();
            if (optional.isPresent()) {
                notifConfig.setAdminConfigId(optional.get().getId());
                notifConfig.setActivated(false);
            }
            configDTOs.add(notifConfig);
        });
        return configDTOs;
    }

    @Override
    public void enableDisableUserNotificationForPlateforme(NotificationConfigDTO notificationConfigDTO) {
        LOGGER.warn("origin admin id  is {}", notificationConfigDTO.getAdminConfigId());
        IgnoredNotificationAdminConfiguration configToDelete = this.notificationConfigDataAccess
                .fetchIgnoredNotificationConfigByOrigin(notificationConfigDTO.getNotificationOrigin());
        if (configToDelete != null) {
            this.notificationConfigDataAccess.deleteIgnoredNotificationConfig(configToDelete);
        } else {
            NotificationOrigins notificationOrigins = this.notificationConfigDataAccess
                    .fetchNotifOriginByOrigin(notificationConfigDTO.getNotificationOrigin());
            LOGGER.warn("origin type is {}", notificationConfigDTO.getNotificationOrigin());
            if (notificationOrigins != null) {
                IgnoredNotificationAdminConfiguration ignoredNotificationAdminConfiguration = new IgnoredNotificationAdminConfiguration();
                ignoredNotificationAdminConfiguration.setNotificationOrigins(notificationOrigins);
                ignoredNotificationAdminConfiguration.setPlatform(notificationConfigDTO.getPlateforme());
                this.notificationConfigDataAccess
                        .persistIgnoredNotificationConfig(ignoredNotificationAdminConfiguration);
            } else {
                throw new UnsupportedOperationException("Notification origin does not exists");
            }
        }
    }

}

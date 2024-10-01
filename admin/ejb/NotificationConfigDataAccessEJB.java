package com.carrus.statsca.admin.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.ejb.interfaces.NotificationConfigDataAccess;
import com.carrus.statsca.admin.entity.IgnoredNotificationAdminConfiguration;
import com.carrus.statsca.admin.entity.NotificationOrigins;

@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Singleton
public class NotificationConfigDataAccessEJB implements NotificationConfigDataAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConfigDataAccessEJB.class);

    @PersistenceContext
    public EntityManager entityManager;

    @Override
    public List<NotificationOrigins> fetchAllNotificationOrigins() {
        try {
            TypedQuery<NotificationOrigins> query = entityManager.createNamedQuery(
                    NotificationOrigins.RETRIEVE_ALL, NotificationOrigins.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error retrieve notificaiton origins: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<IgnoredNotificationAdminConfiguration> fetchAllIgnoredNotificationOriginsByPlateform(String plateform) {
        try {
            TypedQuery<IgnoredNotificationAdminConfiguration> query = entityManager.createNamedQuery(
                    IgnoredNotificationAdminConfiguration.RETRIEVE_ALL_BY_PLATEFORME,
                    IgnoredNotificationAdminConfiguration.class);
            query.setParameter("plateform", plateform);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error retrieve ignored notificaiton origins: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public NotificationOrigins fetchNotifOriginByOrigin(String notificationOrigin) {
        try {
            TypedQuery<NotificationOrigins> query = entityManager.createNamedQuery(
                    NotificationOrigins.RETRIEVE_BY_ORIGIN, NotificationOrigins.class);
            query.setParameter("origin", notificationOrigin);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error retrieve notificaiton origins: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void persistIgnoredNotificationConfig(
            IgnoredNotificationAdminConfiguration iNotificationAdminConfiguration) {
        entityManager.persist(iNotificationAdminConfiguration);
    }

    @Override
    public boolean deleteIgnoredNotificationConfig(
            IgnoredNotificationAdminConfiguration iNotificationAdminConfiguration) {
        try {
            Query deleteQuery = entityManager
                    .createNativeQuery("delete from IgnoredNotificationAdminConfiguration where id = :id");
            deleteQuery.setParameter("id", iNotificationAdminConfiguration.getId());
            deleteQuery.executeUpdate();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Override
    public IgnoredNotificationAdminConfiguration fetchIgnoredNotificationConfigById(Long id) {
        try {
            TypedQuery<IgnoredNotificationAdminConfiguration> query = entityManager.createNamedQuery(
                    IgnoredNotificationAdminConfiguration.RETRIEVE_BY_ID, IgnoredNotificationAdminConfiguration.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error retrieve notificaiton origins: {}", e.getMessage());
        }
        return null;
    }
    
    @Override
    public IgnoredNotificationAdminConfiguration fetchIgnoredNotificationConfigByOrigin(String origin) {
        try {
            TypedQuery<IgnoredNotificationAdminConfiguration> query = entityManager.createNamedQuery(
                    IgnoredNotificationAdminConfiguration.RETRIEVE_BY_ORIGIN, IgnoredNotificationAdminConfiguration.class);
            query.setParameter("origin", origin);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error retrieve notificaiton origins: {}", e.getMessage());
        }
        return null;
    }

}

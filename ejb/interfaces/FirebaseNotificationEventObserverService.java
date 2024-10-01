package com.carrus.statsca.ejb.interfaces;

import javax.enterprise.inject.spi.EventMetadata;

import com.pmc.club.event.ChronologyLevel;

public interface FirebaseNotificationEventObserverService {
    public void observeChronology(ChronologyLevel chronologyLevel,EventMetadata eventMeta);
}

package com.carrus.statsca;
import java.util.Queue;

import javax.ejb.Local;

import com.pmc.club.references.RaceRef;

@Local
public interface QueueRecipesService {

	public void putOnQueue(RaceRef raceRef);

	public Queue<RaceRef> getQueue();

	public void processQueue();

}

package com.carrus.statsca;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.carrus.statsca.dto.SessionDTO;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.recipe.BetCodeRecipe;
import com.pmc.club.exception.MutuelException;
import com.pmc.club.references.RaceRef;

@Local
public interface S3kRecipeService {

	/**
	 * récupération des enjeux par pari en format S3K stockés en mémoire
	 * @return
	 */
	public List<BetCodeRecipe> getS3kBetCodeRecipes();

	/**
	 * nettoyages des enjeux stockés en mémoire
	 */
	public void clearStoredDatas();

	/**
	 * récupération des enjeux par pari pour une course donnée
	 * @param racePk
	 * @return
	 */
	public SessionDTO getBetRecipes(Long racePk);

	/**
	 * récupération des enjeux par pari en format Session stockés en mémoire
	 * @return
	 */
	public Map<Long, SessionDTO> getStoredResponses();

	/**
	 * sauvegarde en mémoire des enjeux rattachés à une course
	 * @param betCodeRecipe
	 * @param raceRefKey
	 * @return
	 */
	boolean saveBetCodeRecipes(List<BetCodeRecipe> betCodeRecipe,Long pk, RaceRef raceRef);

	/**
	 * ajoute ou met à jour les enjeux de paris d'une course dans la sauvegarde mémoire
	 * @param raceRef
	 */
	Race putBetRecipesOnRace(RaceRef raceRef);

	void buildNewRecipes(RaceRef raceRef, Long pk) throws MutuelException;

	public boolean isUpdateTimeSameAsServerUpdateTime(SessionDTO session, ZonedDateTime clientUpdateTime);
	
}

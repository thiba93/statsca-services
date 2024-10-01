package com.carrus.statsca;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.carrus.statsca.bethistory.RemarkableRaceHistory;
import com.carrus.statsca.dto.FormulationStakeDTO;
import com.carrus.statsca.dto.PartnerDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.carrus.statsca.event.UpdatedRacesRecipesEvt;
import com.carrus.statsca.exceptions.RecipeException;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.RaceTrack;
import com.pmc.club.entity.RemarkableRace;
import com.pmc.club.entity.recipe.BetCodeRecipe;
import com.pmc.club.event.BetChange;
import com.pmc.club.event.EventChange;
import com.pmc.club.event.RaceChange;
import com.pmc.club.references.RaceRef;

@Local
public interface RaceCardService {

	public void setCurrentRaceCard(SessionDTO session);
	
	public SessionDTO getRaceCard();
	
	public SessionDTO getRaceCard(LocalDate date);
	
	public void loadCurrentRaceCard(LocalDate sessionDate);

	public void loadSelectedRaceCard(LocalDate date, boolean current);

	public void deleteBet(BetChange betChangeEvent);

	public void addBet(BetChange betChangeEvent);

	public void deleteEvent(EventChange eventChangeEvent);

	public void addEvent(EventChange eventChangeEvent);

	public void editEvent(EventChange eventChangeEvent);

	public void deleteRace(RaceChange raceChangeEvent);

	public void addRace(RaceChange raceChangeEvent);

	public void editRace(RaceChange raceChangeEvent);

	void updateRaceRecipe(UpdatedRacesRecipesEvt urre) throws RecipeException;

	Race updateBetRecipe(List<BetCodeRecipe> betCodeRecipes, RaceRef raceRef);

	SessionDTO getCurrentBetRecipeSession();

	void setCurrentBetRecipeSession(SessionDTO currentBetRecipeSession);

	Map<String, SessionDTO> getPreSessionBetRecipeSession();

	void setPreSessionBetRecipeSession(Map<String, SessionDTO> preSessionBetRecipeSession);

	/**
	 * Construction d'une session simplifié listant les enjeux par paris en mémoire
	 * 
	 * @param recipe
	 * @param raceRecipeSession
	 */
	void constructBetRecipeSession(FormulationStakeDTO recipe, SessionDTO raceRecipeSession);

	String getLightRaceCard();
	
	RemarkableRaceHistory getRemarkableRaceHistory(LocalDate date, LocalTime fromHour, LocalTime toHour);

	RemarkableRaceHistory getRemarkableRaceHistoryEvent(LocalDate date, LocalTime fromHour, LocalTime toHour);
	
	List<RemarkableRace> getUpcomingRacesFromDate(LocalDate date, int limit);
	
	public LocalDate getPreviousDateOfRemarkableRace(RemarkableRace sourceRace);

	List<PartnerDTO> getPartnersFromOrganization(Integer orgId);

	RemarkableRaceHistory getRecipesForEquivalentDayLastYear(LocalDate currentDate);

	void stopTimer();

	RaceTrack getRaceTrack(Long externalPk);

	String getLightSessionRest(SessionDTO session);

	SessionDTO getYesterdayRaceCard();

	void setYesterdayRaceCard(SessionDTO yesterdayRaceCard);

	SessionDTO getPastRaceCard();

	void setPastRaceCard(SessionDTO pastRaceCard);
	
	LocalDate getFirstAvailableSessionDate();

	List<RemarkableRace> getRemarkableRaces();

	Map<Long, Map<Integer, BigDecimal>> getTotalBetsForYears(List<Integer> targetYears);
}

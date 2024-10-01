package com.carrus.statsca.ejb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.MockSessionService;
import com.carrus.statsca.dto.AuthorisedFormulationDTO;
import com.carrus.statsca.dto.AuthorisedPartnerDTO;
import com.carrus.statsca.dto.EventDTO;
import com.carrus.statsca.dto.FormulationStakeDTO;
import com.carrus.statsca.dto.PartnerDTO;
import com.carrus.statsca.dto.RaceDTO;
import com.carrus.statsca.dto.RaceRefDTO;
import com.carrus.statsca.dto.RacetrackDTO;
import com.carrus.statsca.dto.RegulatoryBetDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.pmc.club.entity.AuthorisedFormulation;
import com.pmc.club.entity.BetState;
import com.pmc.club.entity.CircumstanceEnum;
import com.pmc.club.entity.Event;
import com.pmc.club.entity.ExternalRaceState;
import com.pmc.club.entity.GroundConditionEnum;
import com.pmc.club.entity.OriginEnum;
import com.pmc.club.entity.Race;
import com.pmc.club.entity.RaceCategory;
import com.pmc.club.entity.RaceDiscipline;
import com.pmc.club.entity.RaceState;
import com.pmc.club.entity.RaceTrack;
import com.pmc.club.entity.RegulatoryBet;
import com.pmc.club.entity.Session;
import com.pmc.club.entity.enums.MatterEnum;
import com.pmc.club.entity.enums.RailEnum;
import com.pmc.club.entity.enums.SessionState;
import com.pmc.club.entity.enums.StartType;
import com.pmc.club.entity.partner.AuthorisedPartner;
import com.pmc.club.entity.partner.Partner;
import com.pmc.club.entity.recipe.FormulationStake;
import com.pmc.club.service.AuthorisedFormulationService;
import com.pmc.club.service.EventService;
import com.pmc.club.service.RaceTrackService;
import com.pmc.star3000.service.PartnerService;
import com.pmc.star3000.service.ProgramSynchroniser;

public class MockSessionServiceEJB implements MockSessionService {

	private static final String DEFAULT_LABEL = "Mocked Label";
	private static final Logger LOGGER = LoggerFactory.getLogger(MockSessionServiceEJB.class);
	private static final int DEFAULT_EVENT_COUNT = 4;
	private static final int DEFAULT_RACE_COUNT = 5;
	private static final int DEFAULT_FORMULATION_COUNT = 5;
	private static final Map<Integer, RegulatoryBetDTO> DEFAULT_BETS = Map.of(1, new RegulatoryBetDTO("GAG", "Gagnant"), 3, new RegulatoryBetDTO("PLA", "Placé"));
	private Random random = new Random();
	private List<RacetrackDTO> racetracks = new ArrayList<>();
	private List<RaceTrack> racetrackEntities = new ArrayList<>();
	private List<RegulatoryBet> betEntities = new ArrayList<>();

	
	@Inject
	private PartnerService partnerService;
	
	@Inject
	private RaceTrackService raceTrackService;

	@Inject
	private AuthorisedFormulationService authorisedFormulationService;
	
	@Inject
	private ProgramSynchroniser programSynchroniserService;

	@Inject
	private EventService eventService;
	
	@Override
	public SessionDTO getMockSessionFromScratch(LocalDate mockSessionDate) {
		return this.getMockSessionFromScratch(mockSessionDate, DEFAULT_LABEL, null, null, DEFAULT_BETS);
	}
	
	@Override
    public SessionDTO getSessionWithMockedRecipes(LocalDate sessionDate) {
		Session session = eventService.getSessionByDate(sessionDate);
		if(session == null) {
			session = eventService.getSessionByDate(LocalDate.of(2023, 3, 16));
		}
		SessionDTO sessionDTO = new SessionDTO(session);
		if(sessionDTO.getSessionDate() != sessionDate) {
			sessionDTO.setSessionDate(sessionDate);
		}
		sessionDTO.getEvents().stream().forEach(this::modifyEventRecipes);
		
		return sessionDTO;
    }
	
	private void modifyEventRecipes(EventDTO e) {
		e.getRaces().stream().peek(r -> this.modifyRaceRecipes(r, e)).collect(Collectors.toList());
	}

	private void modifyRaceRecipes(RaceDTO race, EventDTO event) {
		race.setAuthorizedPartners(new ArrayList<>());
		int size = partnerService.getPartnersCache().size();
		Partner contractor = partnerService.getPartnersCache().get(this.random.nextInt(size));
		AuthorisedPartnerDTO recipe = new AuthorisedPartnerDTO();
		PartnerDTO partner;
		partner = new PartnerDTO(contractor);
		
		recipe.setPartner(partner);
		BigDecimal stake = BigDecimal.valueOf(Math.random() * 1000.00).setScale(2, RoundingMode.CEILING);
		recipe.setStake(stake);
		recipe.setUpdateDate(ZonedDateTime.now());
		BigDecimal betStake = BigDecimal.valueOf(recipe.getStake().doubleValue() / race.getFormulations().size());
		recipe.setFormulationStakes(new ArrayList<>());
		for (AuthorisedFormulationDTO form : race.getFormulations()) {
			mockBetRecipeFromScratch(event, race, recipe, betStake, form);
		}
		race.getAuthorizedPartners().add(recipe);
	}

	@PostConstruct
	public void init() {
		List<String> racetrackIds = raceTrackService.getExternalIds();
		for(String id : racetrackIds) {
			RaceTrack rt = raceTrackService.getByExternalId(id);
			this.racetrackEntities.add(rt);
			RacetrackDTO rtDTO = new RacetrackDTO(rt);
			this.racetracks.add(rtDTO);
		}
		this.betEntities = authorisedFormulationService.getAllRegulatoryBets();
	}

	@Override
	public SessionDTO getMockSessionFromScratch(LocalDate mockSessionDate, String labelName, Integer eventCount, Integer raceCount, Map<Integer, RegulatoryBetDTO> betList) {

		if (mockSessionDate != null) {
			
			if (labelName == null)
				labelName = DEFAULT_LABEL;
			
			if (eventCount == null)
				eventCount = DEFAULT_EVENT_COUNT;

			if (raceCount == null)
				raceCount = DEFAULT_RACE_COUNT;

			if (betList == null || betList.isEmpty())
				betList = DEFAULT_BETS;

			long eventPk = 0;
			long racePk = 0;
			long betPk = 0;
			long recipePk = 0;
			SessionDTO session = new SessionDTO(mockSessionDate);
			session.setEvents(new ArrayList<>());
			for (int i = 0; i < eventCount && i < racetracks.size(); i++) {
				EventDTO event = new EventDTO();
				event.setId(i);
				event.setName("Event "+labelName +" " + i);
				event.setPk(++eventPk);
				RacetrackDTO racetrack = racetracks.get(i);
				event.setRacetrack(racetrack);
				event.setRaces(new ArrayList<>());
				for (int j = 1; j <= raceCount; j++) {
					++racePk;
					++recipePk;
					betPk = mockedRaceFromScratch(raceCount, labelName, betList, racePk, betPk, recipePk, i, event, j);
				}
				session.getEvents().add(event);
			}
			
			return session;
		}
		return null;
	}

	@Override
	public Session getMockEntitySessionFromScratch(Integer idSession, LocalDate mockSessionDate, Integer eventCount, Integer raceCount, Integer betCount, List<RegulatoryBet> betList, SessionState state) {

		if (mockSessionDate != null) {
			
			if (eventCount == null)
				eventCount = DEFAULT_EVENT_COUNT;

			if (raceCount == null)
				raceCount = DEFAULT_RACE_COUNT;
			
			if (betCount == null)
				betCount = DEFAULT_FORMULATION_COUNT;

			if (betList == null || betList.isEmpty())
				betList = this.betEntities;
			Session session = new Session();
			session.setId(idSession);
			session.setDate(mockSessionDate);
			session.setEvents(new ArrayList<>());
			session.setLinkedSessions(new ArrayList<>());
			session.setState(state);
			for (int i = 0; i < eventCount && i < racetrackEntities.size(); i++) {
				mockedEntityEventFromScratch(raceCount, betCount, betList, session, i, mockSessionDate);
			}
			
			return session;
		}
		return null;
	}

	/**
	 * @param raceCount
	 * @param betCount
	 * @param betList
	 * @param session
	 * @param i
	 * @param eventDate
	 */
	private void mockedEntityEventFromScratch(Integer raceCount, Integer betCount, List<RegulatoryBet> betList, Session session, int i, LocalDate eventDate) {
		Event event = new Event();
		Integer eventId = i + 1;
		event.setId(eventId);
		event.setName("Event " + eventId);
		event.setDate(eventDate);
		RaceTrack racetrack = racetrackEntities.get(i);
		event.setRaceTrack(racetrack);
		event.setRaces(new ArrayList<>());
		for (int j = 0; j < raceCount; j++) {
			mockedEntityRaceFromScratch(raceCount, betCount, betList, i, event, j);
		}
		event.setStart(event.getRaces().get(0).getExpectedStart());
		event.setRaceCategory(RaceCategory.ALL);
		event.setCircumstance(CircumstanceEnum.UNKNOWN);
		event.setGroundCondition(GroundConditionEnum.UNKNOWN);
		event.setGroundConditionDate(new Date());
		session.getEvents().add(event);
	}
	
	private void mockedEntityRaceFromScratch(Integer raceCount, Integer betCount, List<RegulatoryBet> betList, int indexEvent, Event event, int indexRace) {
		Race race = new Race();
		Integer raceNumber = indexRace+1;
		Integer eventId = indexEvent+1;
		race.setName("Race " + raceNumber);
		race.setNumber(indexRace+1);
		race.setShortName("R" + eventId + " C" + raceNumber);
		race.setRunnerNumber(Short.valueOf("" + (random.nextInt(raceCount) + 3)));
		race.setFormulations(new ArrayList<>());
		race.setState(RaceState.ON_SALE);
		race.setExternalState(ExternalRaceState.UNKNOWN);
		race.setMatter(MatterEnum.UNKNOWN);
		race.setDiscipline(RaceDiscipline.UNKNOWN);
		race.setCategory(RaceCategory.ALL);
		race.setStartType(StartType.UNKNOWN);
		race.setDistance(random.nextInt(2000)+1000);
		race.setRail(RailEnum.UNKNOWN);
		race.setExpectedStart(ZonedDateTime.now().withHour(random.nextInt(24)).withMinute(random.nextInt(60)).withSecond(random.nextInt(60)));
		race.setOperationsStart(ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0));
		race.setPriority(false);
		race.setDuration(0);
		race.setGroundCondition(GroundConditionEnum.UNKNOWN);
		for ( int k =0; k < betCount && k < betList.size(); k++) {
			mockedEntityFormulationFromScratch( race, betList.get(k));
		}
		race.setAuthorisedPartners(new ArrayList<>());
		int size = partnerService.getPartnersCache().size();
		Partner partner = partnerService.getPartnersCache().get(this.random.nextInt(size));
		AuthorisedPartner recipe = new AuthorisedPartner();
		
		recipe.setPartner(partner);
		BigDecimal stake = BigDecimal.valueOf(Math.random() * 1000.00).setScale(2, RoundingMode.CEILING);
		recipe.setStake(stake);
		recipe.setUpdateDate(ZonedDateTime.now());
		BigDecimal betStake = BigDecimal.valueOf(recipe.getStake().doubleValue() / race.getFormulations().size());
		recipe.setFormulationStakes(new ArrayList<>());
		for (AuthorisedFormulation form : race.getFormulations()) {
			mockedEntityBetRecipeFromScratch(recipe, betStake, form);
			LOGGER.info("Mocked Bet Recipe from R{}C{} - Bet {} ({}) - Stake {}",event.getId(), race.getNumber(), form.getBetType().getName(), form.getBetType().getCode(), betStake.doubleValue());
		}
		race.getAuthorisedPartners().add(recipe);
		LOGGER.info("Mocked Race Recipe from R{}C{} - Partner {} ({}) - Stake {}",event.getId(), race.getNumber(), partner.getName(), partner.getPartnerId(), recipe.getStake().doubleValue());
		event.getRaces().add(race);
	}

	private void mockedEntityBetRecipeFromScratch(AuthorisedPartner recipe, BigDecimal betStake, AuthorisedFormulation form) {
		FormulationStake betRecipe = new FormulationStake();
		betRecipe.setOffer(form);
		betRecipe.setAuthorisedpartner(recipe);
		betRecipe.setStake(betStake.setScale(2, RoundingMode.CEILING));
		betRecipe.setUpdateDate(ZonedDateTime.now());
		recipe.getFormulationStakes().add(betRecipe);
		
	}

	private void mockedEntityFormulationFromScratch(Race race, RegulatoryBet bet) {
		AuthorisedFormulation form = new AuthorisedFormulation();
		form.setBetType(bet);
		form.setRace(race);
		form.setOrigin(OriginEnum.PMU);
		form.setState(BetState.ON_SALE);
		form.setPayback(false);
		form.setReplacementAvailable(false);
		form.setQuickPickAvailable(false);
		form.setWinningsBonus(0);
		form.setStackAmountUnit(0.01);
		form.setGrossPool(0.00);
		race.getFormulations().add(form);
		
	}

	@Override
	public List<SessionDTO> getMockSessionListFromScratch(LocalDate startSessionDate, LocalDate endSessionDate) {
		return this.getMockSessionListFromScratch(startSessionDate, endSessionDate, DEFAULT_LABEL, null, null, DEFAULT_BETS);
	}

	@Override
	public List<SessionDTO> getMockSessionListFromScratch(LocalDate startSessionDate, LocalDate endSessionDate, String labelName, Integer eventCount, Integer raceCount, Map<Integer, RegulatoryBetDTO> betList) {
		

		
		ArrayList<SessionDTO> arrayList = new ArrayList<>();
		LOGGER.info("génération d'une liste de sessions mock en partant de rien");
		if(startSessionDate!=null && endSessionDate!=null && startSessionDate.isBefore(endSessionDate) )
		{
			LocalDate dateIteration = startSessionDate;
			while(!dateIteration.isAfter(endSessionDate)) {
				LOGGER.info("session mock du {}", dateIteration);
				arrayList.add(this.getMockSessionFromScratch(dateIteration, labelName, eventCount, raceCount, betList));
				dateIteration = dateIteration.plusDays(1);
			}
		}
		return arrayList;
	}
	
	

	/**
	 * @param raceCount
	 * @param labelName
	 * @param betList
	 * @param racePk
	 * @param betPk
	 * @param recipePk
	 * @param i
	 * @param event
	 * @param j
	 * @return
	 */
	private long mockedRaceFromScratch(Integer raceCount, String labelName, Map<Integer, RegulatoryBetDTO> betList, long racePk, long betPk, long recipePk, int i, EventDTO event, int j) {
		RaceDTO race = new RaceDTO();
		race.setName("Race "+labelName+" " + i);
		race.setNumber(j);
		race.setPk(racePk);
		race.setShortName(labelName + i);
		race.setRunnerNumber(Short.valueOf("" + (random.nextInt(raceCount) + 3)));
		race.setFormulations(new ArrayList<>());
		race.setState(RaceState.ON_SALE);
		race.setExpectedStart(ZonedDateTime.now().withHour(random.nextInt(24)).withMinute(random.nextInt(60)).withSecond(random.nextInt(60)));
		for (Entry<Integer, RegulatoryBetDTO> entry : betList.entrySet()) {
			++betPk;
			mockFormulationFromScratch(betPk, race, entry);
		}
		race.setAuthorizedPartners(new ArrayList<>());
		int size = partnerService.getPartnersCache().size();
		Partner contractor = partnerService.getPartnersCache().get(this.random.nextInt(size));
		AuthorisedPartnerDTO recipe = new AuthorisedPartnerDTO();
		PartnerDTO partner;
		partner = new PartnerDTO(contractor);
		
		recipe.setPartner(partner);
		BigDecimal stake = BigDecimal.valueOf(Math.random() * 1000.00).setScale(2, RoundingMode.CEILING);
		recipe.setStake(stake);
		recipe.setPk(recipePk);
		recipe.setUpdateDate(ZonedDateTime.now());
		BigDecimal betStake = BigDecimal.valueOf(recipe.getStake().doubleValue() / race.getFormulations().size());
		recipe.setFormulationStakes(new ArrayList<>());
		for (AuthorisedFormulationDTO form : race.getFormulations()) {
			mockBetRecipeFromScratch(event, race, recipe, betStake, form);
			LOGGER.info("Mocked Bet Recipe from R{}C{} - Bet {} ({}) - Stake {}",event.getId(), race.getNumber(), form.getBetType().getName(), form.getBetCodeRef(), betStake.doubleValue());
		}
		race.getAuthorizedPartners().add(recipe);
		LOGGER.info("Mocked Race Recipe from R{}C{} - Partner {} ({}) - Stake {}",event.getId(), race.getNumber(), partner.getName(), partner.getPartnerId(), recipe.getStake().doubleValue());
		event.getRaces().add(race);
		return betPk;
	}
	
	/**
	 * @param event
	 * @param race
	 * @param recipe
	 * @param betStake
	 * @param form
	 */
	private void mockBetRecipeFromScratch(EventDTO event, RaceDTO race, AuthorisedPartnerDTO recipe, BigDecimal betStake, AuthorisedFormulationDTO form) {
		FormulationStakeDTO betRecipe = new FormulationStakeDTO();
		betRecipe.setBetCodeRef(form.getBetCodeRef());
		betRecipe.setContractor(recipe.getPartner().getPartnerId());
		betRecipe.setStake(betStake.setScale(2, RoundingMode.CEILING));
		betRecipe.setUpdateDate(ZonedDateTime.now());
		betRecipe.setRaceRef(new RaceRefDTO(race.getExpectedStart().toLocalDate(), event.getId(), race.getNumber()));
		recipe.getFormulationStakes().add(betRecipe);
	}

	/**
	 * @param betPk
	 * @param race
	 * @param entry
	 * @return
	 */
	private void mockFormulationFromScratch(long betPk, RaceDTO race, Entry<Integer, RegulatoryBetDTO> entry) {
		AuthorisedFormulationDTO form = new AuthorisedFormulationDTO();
		form.setBetCodeRef(entry.getKey());
		form.setBetType(entry.getValue());
		form.setPk(betPk);
		race.getFormulations().add(form);
	}

	
//	/**
//	 * 
//	 * @param mockSessionDate
//	 * @param eventCount
//	 * @param raceCount
//	 * @param betCount
//	 * @param betList
//	 * @param state
//	 * @return
//	 */
//	@Override
//	public boolean injectDevSession(Integer idSession, LocalDate mockSessionDate, Integer eventCount, Integer raceCount, Integer betCount, List<RegulatoryBet> betList, SessionState state) {
//		Session session = getMockEntitySessionFromScratch(idSession, mockSessionDate, eventCount, raceCount, betCount, betList, state);
//		try {
//			programSynchroniserService.injectDevSession(session);
//		}
//		catch(Exception e) {
//			LOGGER.error("error while injecting session: {} ",e.getMessage());
//			return false;
//		}
//		return true;
//	}

//	@Override
//	public List<RegulatoryBet> createMockedBetLists(String betCodes) {
//		String[] betCodesArray = betCodes.split("-");
//		List<String> betCodesList = Arrays.asList(betCodesArray);
//		return betEntities.stream().filter(element -> betCodesList.contains(element.getCode()+"")).toList();
//	}
//
//	@Override
//	public SessionState retrieveSessionState(String state) {
//		try {
//		return SessionState.valueOf(state);
//		} catch(Exception e) {
//			LOGGER.error("error while retrieving session state: {} - the session is put ",e.getMessage());
//			return SessionState.CLOSED;
//		}
//	}

}

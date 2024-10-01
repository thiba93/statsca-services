package com.carrus.statsca.restws;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.EventService;
import com.carrus.statsca.HistoryService;
import com.carrus.statsca.MockSessionService;
import com.carrus.statsca.RaceCardService;
import com.carrus.statsca.ResponseService;
import com.carrus.statsca.S3kRecipeService;
import com.carrus.statsca.admin.restws.utils.SwaggerGlobalDeclarations;
import com.carrus.statsca.beans.requests.BetRecipeRequest;
import com.carrus.statsca.beans.requests.HistorySessionRequest;
import com.carrus.statsca.bethistory.RemarkableRaceHistory;
import com.carrus.statsca.dto.PartnerDTO;
import com.carrus.statsca.dto.RacetrackDTO;
import com.carrus.statsca.dto.RemarkableRaceDTO;
import com.carrus.statsca.dto.RemarkableRaceDateInfoDTO;
import com.carrus.statsca.dto.RemarkableRaceHistoryDTO;
import com.carrus.statsca.dto.RemarkableRaceInfosDTO;
import com.carrus.statsca.dto.RemarkableRaceTotalBetDTO;
import com.carrus.statsca.dto.SessionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmc.club.MutuelService;
import com.pmc.club.entity.RaceTrack;
import com.pmc.club.entity.RemarkableRace;
import com.pmc.club.exception.MutuelException;
import com.pmc.club.recipes.injector.data.InjectionResponse;
import com.pmc.club.recipes.injector.service.RecipesInjectorService;
import com.pmc.club.service.ParametreService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Stateless
@PermitAll
@SwaggerGlobalDeclarations
@Path("/racecard")
@OpenAPIDefinition(info = @Info(title = "Racecard Services", description = "Provides all the function allowing to extract the racecard", version = "3.0.0"))
public class RacecardEndpoint {
	/** Class logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(RacecardEndpoint.class);

	private static final DecimalFormat df = new DecimalFormat("0.0000");

	public static final String INJECTOR_FILESYSTEM = "club.services.recipes.injector.path";

	public static final String SESSIONS_FILENAME = "sessions.txt";

	@Inject
	private RaceCardService raceCardService;

	@Inject
	MutuelService mutuelService;

	@Inject
	private S3kRecipeService recipeService;

	@Inject
	private HistoryService historyService;

	@Inject
	private MockSessionService mockService;

	@Inject
	private RecipesInjectorService recipesInjectorService;

	@Inject
	private ParametreService parametreService;

	@EJB
	ResponseService responseService;

	@Inject
	private EventService eventService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/session")
	@Operation(summary = "This function is used to get the session of the day", tags = "Racecard Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "Array with the session")
	public Response getSession(
			@Parameter(name = "SessionRequest", required = true, description = "Session request with a session date") com.carrus.statsca.beans.requests.SessionRequest request) {
		long start = System.currentTimeMillis();
		LOGGER.info("Session requested for {} ...", request.getDate());
		SessionDTO sessionDTO = raceCardService.getRaceCard(request.getDate());

		String jsonString = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (sessionDTO != null)
				jsonString = mapper.writeValueAsString(sessionDTO);
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("Service Session request executed in {} s", df.format(seconds));
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/mockSession")
	@Operation(summary = "This function is used to get the mock session of the selected day", tags = "Racecard Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "the mocked session")
	public Response getMockSession(
			@Parameter(name = "SessionRequest", required = true, description = "Session request with a session date") com.carrus.statsca.beans.requests.SessionRequest request) {
		long start = System.currentTimeMillis();
		LOGGER.info("Mock Session requested for {} ...", request.getDate());
		SessionDTO sessionDTO = mockService.getSessionWithMockedRecipes(request.getDate());
		String jsonString = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (sessionDTO != null)
				jsonString = mapper.writeValueAsString(sessionDTO);
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("Mock Session request executed in {} s", df.format(seconds));
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO light Session request with specific date which returns a light Session
	// data

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lightSession")
	@Operation(summary = "This function is used to get the light session of the day", tags = "Racecard Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "Array with the session")
	public Response getLightSession(
			@Parameter(name = "SessionRequest", required = true, description = "Session request with a session date") com.carrus.statsca.beans.requests.SessionRequest request) {
		long start = System.currentTimeMillis();
		LOGGER.info("Session requested for {} ...", request.getDate());
		SessionDTO sessionDTO = raceCardService.getRaceCard(request.getDate());

		String jsonString = StringUtils.EMPTY;
		try {
			if (sessionDTO != null)
				jsonString = raceCardService.getLightSessionRest(sessionDTO);
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("Service Session request executed in {} s", df.format(seconds));
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (Exception e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/history")
	@Operation(summary = "This function is used to get the session history", tags = "Racecard Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "Array with the history sessions")
	public Response getHistorySession(
			@Parameter(name = "HistorySessionRequest", required = true, description = "Session request with a session history dates") HistorySessionRequest request) {
		long start = System.currentTimeMillis();
		if (request.getStartDate() != null && request.getEndDate() != null) {
			if (!request.getStartDate().equals(request.getEndDate())) {
				LOGGER.info("History requested from {} to {} ...", request.getStartDate(), request.getEndDate());
			} else {
				LOGGER.info("History requested for {}", request.getStartDate());
			}
		} else {
			if (request.getStartDate() != null) {
				LOGGER.info("History requested after {} ...", request.getStartDate());
			} else {
				if (request.getEndDate() != null) {
					LOGGER.info("History requested before {} ...", request.getEndDate());
				}
			}
		}
		List<SessionDTO> history = historyService.loadHistoryRaceCard(request.getStartDate(), request.getEndDate());

		String jsonString = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (history != null)
				jsonString = mapper.writeValueAsString(history);
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("History request executed in {} s", df.format(seconds));
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/betrecipes")
	@Operation(summary = "This function is used to get recipe details for a given race", tags = "Recipe Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "Array with the session")
	@ApiResponse(responseCode = "204", description = "If no updates has been made since last call for a given race, we don't send any race data")
	public Response getBetRecipes(
			@Parameter(name = "betRecipeRequest", required = true, description = "race primary key") BetRecipeRequest request) {
		long start = System.currentTimeMillis();
		LOGGER.info("Bet Recipes requested for {} ...", request.getRacePk());

		SessionDTO sessionDTO = recipeService.getBetRecipes(request.getRacePk());

		String jsonString = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (sessionDTO != null) {
				if (request.getUpdateTime() != null
						&& recipeService.isUpdateTimeSameAsServerUpdateTime(sessionDTO, request.getUpdateTime())) {
					return responseService.buildResponse(Response.Status.NO_CONTENT);
				}
				jsonString = mapper.writeValueAsString(sessionDTO);
			}
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("Service Session request executed in {} s", df.format(seconds));
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO : Enlever ce endpoint ?
	@GET
	@Path("/remarkable")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "This function is used to retrieve the remarkable races after a given date, if no limit is given all races fulfilling the filter are returned", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "A list of all remarkable races races after the given date")
	@ApiResponse(responseCode = "400", description = "The given limit parameter is a negative value")
	public Response getRemarkableRaces(@QueryParam("date") String dateRaw, @QueryParam("limit") int limit) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			LocalDate date;

			if (dateRaw == null) {
				date = LocalDate.now();
			} else {
				date = LocalDate.parse(dateRaw);
			}

			List<RemarkableRace> races = raceCardService.getUpcomingRacesFromDate(date, limit);
			List<RemarkableRaceDTO> racesDto = new ArrayList<>();

			for (RemarkableRace race : races) {
				if (race == null) {
					continue;
				}

				LocalDate previousRaceDate = raceCardService.getPreviousDateOfRemarkableRace(race);

				RemarkableRaceDTO raceDto = new RemarkableRaceDTO(race);

				if (previousRaceDate != null) {
					raceDto.setPreviousDate(previousRaceDate);
				}

				racesDto.add(raceDto);
			}

			String jsonString = mapper.writeValueAsString(racesDto);
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (IllegalArgumentException | DateTimeParseException e) {
			return responseService.buildResponse(Response.Status.BAD_REQUEST);
		}
	}

	@GET
	@Path("/remarkable/comingup")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "This function is used to retrieve the next remarkable race from a given date (or today if no date is given)")
	@ApiResponse(responseCode = "200", description = "The next remarkable race")
	@ApiResponse(responseCode = "204", description = "There is no remarkable races coming up")
	public Response getComingUpRemarkableRaces(@QueryParam("date") String dateRaw) {
		ObjectMapper mapper = new ObjectMapper();
		LocalDate date = dateRaw == null ? LocalDate.now() : LocalDate.parse(dateRaw);

		try {
			List<RemarkableRace> races = raceCardService.getUpcomingRacesFromDate(date, 1);

			if (races.isEmpty()) {
				return responseService.buildResponse(Response.Status.NO_CONTENT);
			}

			RemarkableRace comingUpRace = races.get(0);
			LocalDate previousRaceDate = raceCardService.getPreviousDateOfRemarkableRace(comingUpRace);

			RemarkableRaceDTO raceDto = new RemarkableRaceDTO(comingUpRace);
			if (previousRaceDate != null) {
				raceDto.setPreviousDate(previousRaceDate);
			}

			RaceTrack raceTrack = comingUpRace.getGrandPrize().getRaceTrack();

			if (raceTrack != null) {
				raceDto.getGrandPrize().setRaceTrack(new RacetrackDTO(raceTrack));
			}

			String jsonString = mapper.writeValueAsString(raceDto);
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/remarkables")
	@Operation(summary = "This function is used to get all remarkable races", security = {@SecurityRequirement(name = "api_key_access")})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllRemarkables() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			List<RemarkableRace> races = raceCardService.getRemarkableRaces();

			if (races.isEmpty()) {
				return responseService.buildResponse(Response.Status.NO_CONTENT);
			}

			Map<Long, RemarkableRaceInfosDTO> infos = new HashMap<>();

			for (RemarkableRace race : races) {
				Long gpId = race.getGrandPrize().getPk();

				if (infos.get(gpId) == null) {
					infos.put(gpId, new RemarkableRaceInfosDTO(race.getGrandPrize()));
				}

	//			infos.get(gpId).addDate(new RemarkableRaceDateInfoDTO(race.getDateSession(), race.getRaceNumber()));
			}

			String jsonString = mapper.writeValueAsString(infos.values());
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/remarkable/totalBets")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTotalBets(@QueryParam("y") String years) {
		ObjectMapper mapper = new ObjectMapper();

		List<Integer> yearsParsed;

		if (years.isBlank()) {
			yearsParsed = List.of(LocalDate.now().getYear());
		} else {
			String[] yearsSeparated = years.split(",");
			yearsParsed = Arrays.stream(yearsSeparated).map(Integer::parseInt).toList();
		}

		try {
			Map<Long, Map<Integer, BigDecimal>> bets = raceCardService.getTotalBetsForYears(yearsParsed);
			List<RemarkableRaceTotalBetDTO> betsDto = bets.entrySet().stream()
					.map(elem -> new RemarkableRaceTotalBetDTO(elem.getKey(), elem.getValue())).toList();

			String jsonString = mapper.writeValueAsString(betsDto);
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/remarkable/history")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRemarkableRaceHistory(@QueryParam("date") String dateRaw,
			@QueryParam("startHour") String startHourRaw, @QueryParam("endHour") String endHourRaw) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			LocalDate date = dateRaw == null ? LocalDate.now() : LocalDate.parse(dateRaw);
			LocalTime startHour = startHourRaw == null ? null : LocalTime.parse(startHourRaw);
			LocalTime endHour = endHourRaw == null ? null : LocalTime.parse(endHourRaw);

			RemarkableRaceHistory history = raceCardService.getRemarkableRaceHistory(date, startHour, endHour);
			String jsonString = mapper.writeValueAsString(new RemarkableRaceHistoryDTO(history));

			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (DateTimeParseException e) {
			LOGGER.error("Error while parsing date/time {}", e.getMessage());
			return responseService.buildResponse(Response.Status.BAD_REQUEST);
		}

	}

	@GET
	@Path("/remarkable/history/event")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRemarkableRaceEventHistory(@QueryParam("date") String dateRaw,
			@QueryParam("startHour") String startHourRaw, @QueryParam("endHour") String endHourRaw) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			LocalDate date = dateRaw == null ? LocalDate.now() : LocalDate.parse(dateRaw);
			LocalTime startHour = startHourRaw == null ? null : LocalTime.parse(startHourRaw);
			LocalTime endHour = endHourRaw == null ? null : LocalTime.parse(endHourRaw);

			RemarkableRaceHistory history = raceCardService.getRemarkableRaceHistoryEvent(date, startHour, endHour);
			String jsonString = mapper.writeValueAsString(new RemarkableRaceHistoryDTO(history));

			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (DateTimeParseException e) {
			LOGGER.error("Error while parsing date/time {}", e.getMessage());
			return responseService.buildResponse(Response.Status.BAD_REQUEST);
		}

	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/remarkable/events")
	@Operation(summary = "This function is used to get the sessions with the given event info and the date array", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "Array with the session")
	public Response getEvents(@QueryParam("dates") String dates,
			@QueryParam("eventInfo") String eventInfoRaw) {

		String[] datesArray = dates.split(",");
		//List<LocalDate> sessionDates = Arrays.stream(datesArray).map(LocalDate::parse).collect(Collectors.toList());
		
		long start = System.currentTimeMillis();
		List<SessionDTO> sessionDTOs = new ArrayList<>();
		String raceTrackId;

		if (eventInfoRaw != null && eventInfoRaw.length() == 3) {
			raceTrackId = eventInfoRaw;
			// info de réunion
		} else {
			LOGGER.warn("getEvents : the raceTrack ID {} is incorrect", eventInfoRaw);
			return responseService.buildResponse(StringUtils.EMPTY, Response.Status.BAD_REQUEST);
		}

		for (String dateRaw : datesArray) {
			// date de la réunion
			LocalDate date = null;// LocalDate.now();
			try {
				date = LocalDate.parse(dateRaw);
				//FOR TEST 
				LocalDate mockdate = LocalDate.of(2023, 3, 16);
				//TEST
				SessionDTO sessionDTO = eventService.getEventByRaceTrack(mockdate, raceTrackId);
				sessionDTO.setSessionDate(date);
				
				if (sessionDTO != null) {
					LOGGER.warn("getEvents : there is no event session with this event infos : {} - {}", date,
							raceTrackId);
					sessionDTOs.add(sessionDTO);
				}
			} catch (DateTimeParseException dtpe) {
				LOGGER.warn("getEvents : the date {} is not in correct format: {}", dateRaw, dtpe.getMessage());
				return responseService.buildResponse(StringUtils.EMPTY, Response.Status.BAD_REQUEST);
			}
		}

		String jsonString = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		try {
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("Event Session request executed in {} s", df.format(seconds));
			if ( !sessionDTOs.isEmpty()) {
				jsonString = mapper.writeValueAsString(sessionDTOs);
				return responseService.buildResponse(jsonString, Response.Status.OK);
			}
			else {
				return responseService.buildResponse(StringUtils.EMPTY, Response.Status.NO_CONTENT);
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	@GET
	@Path("/partners")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartners(@QueryParam("orgId") Integer orgId) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<PartnerDTO> partners = raceCardService.getPartnersFromOrganization(orgId);

			return responseService.buildResponse(mapper.writeValueAsString(partners), Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/mock")
	@Operation(summary = "This function is used to get the mock sessions", tags = "Racecard Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessionDTO.class))), description = "Array with the mock ssessions")
	public Response getMockSession(
			@Parameter(name = "HistorySessionRequest", required = true, description = "Session request with mock session dates") HistorySessionRequest request) {
		long start = System.currentTimeMillis();
		if (request.getStartDate() != null && request.getEndDate() != null) {
			if (!request.getStartDate().equals(request.getEndDate())) {
				LOGGER.info("Mock requested from {} to {} ...", request.getStartDate(), request.getEndDate());
			} else {
				LOGGER.info("Mock requested for {}", request.getStartDate());
			}
		} else {
			if (request.getStartDate() != null) {
				LOGGER.info("Mock requested after {} ...", request.getStartDate());
			} else {
				if (request.getEndDate() != null) {
					LOGGER.info("Mock requested before {} ...", request.getEndDate());
				}
			}
		}
		List<SessionDTO> mock = mockService.getMockSessionListFromScratch(request.getStartDate(), request.getEndDate());

		String jsonString = StringUtils.EMPTY;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (mock != null)
				jsonString = mapper.writeValueAsString(mock);
			long end = System.currentTimeMillis();
			double seconds = (end - start) / 1000F;
			LOGGER.info("History request executed in {} s", df.format(seconds));
			return responseService.buildResponse(jsonString, Response.Status.OK);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("/injectrecipe")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "This function inject recipes from the past session", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "A response with a 200 code")
	@ApiResponse(responseCode = "500", description = "An error or an error list")
	public Response injectRecipes() {
		try {
			String filesystemPath = parametreService.getParameterValue(INJECTOR_FILESYSTEM, String.class);
			if (filesystemPath != null) {
				String ordinarySessionsFilePath = filesystemPath + SESSIONS_FILENAME;
				List<InjectionResponse> responses = recipesInjectorService
						.processFileInjection(ordinarySessionsFilePath);
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(responses);
				if (!responses.isEmpty())
					return responseService.buildResponse(jsonString, Response.Status.INTERNAL_SERVER_ERROR);
				else
					return responseService.buildResponse(Response.Status.OK);
			} else {
				LOGGER.error("the session date file is not found");
				return responseService.buildResponse(Response.Status.NO_CONTENT);
			}

		} catch (IllegalArgumentException | DateTimeParseException | IOException | MutuelException e) {
			LOGGER.error("Error while injecting recipes : {} - {}", e.getClass().getName(), e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

//	@GET
//	@Path("/injectDevSession")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Operation(summary = "This function inject a mocked session", security = {@SecurityRequirement(name = "api_key_access")})
//	public Response injectDevSession(@QueryParam("idSession") int idSession, @QueryParam("mockDate") String dateRaw, @QueryParam("eventCount") int eventCount, @QueryParam("raceCount") int raceCount, @QueryParam("betCount") int betCount, @QueryParam("betCodes") String betCodes, @QueryParam("state") String state) {
//		try {
//		LocalDate date = dateRaw == null ? LocalDate.now() : LocalDate.parse(dateRaw);
//		List<RegulatoryBet> betLists = mockService.createMockedBetLists(betCodes);
//		SessionState sessionState = mockService.retrieveSessionState(state);
//		boolean result = mockService.injectDevSession(idSession, date, eventCount, raceCount, betCount, betLists, sessionState);
//		if(result)
//			return responseService.buildResponse(Response.Status.OK);
//		else
//			return responseService.buildResponse(Response.Status.BAD_REQUEST);
//		} catch (Exception e) {
//			LOGGER.error("Error while injecting mocked sessions : {} - {}", e.getClass().getName(), e.getMessage());
//			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
//		}
//	}

	@GET
	@Path("/firstDate")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "This function is used to retrieve the first available session date", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "first available session date")
	public Response getFirstAvailableSessionDate() {
		LocalDate date = raceCardService.getFirstAvailableSessionDate();
		JsonbBuilder jsonBuilder = JsonbBuilder.newBuilder();
		Jsonb jsonb = jsonBuilder.build();
		if (date != null) {
			return responseService.buildResponse(jsonb.toJson(date), Status.OK);
		} else {
			return responseService.buildResponse(Status.NO_CONTENT);
		}
	}
}

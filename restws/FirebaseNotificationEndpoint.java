package com.carrus.statsca.restws;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.ResponseService;
import com.carrus.statsca.admin.restws.utils.SwaggerGlobalDeclarations;
import com.carrus.statsca.ejb.interfaces.FcmNotificationService;
import com.carrus.statsca.restws.requests.SendNotificationsRequest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Stateless
@PermitAll
@SwaggerGlobalDeclarations
@Path("/notification")
@OpenAPIDefinition(info = @Info(title = "notification service", description = "Provides all the function to subscribe to push notification and maintain tokens", version = "1.0.0"))
public class FirebaseNotificationEndpoint {
	private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseNotificationEndpoint.class);

	@Inject
	private FcmNotificationService fcmNotificationService;

	@EJB
	ResponseService responseService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/saveToken")
	@Operation(summary = "This function is used to save FCM token", tags = "firebase notification Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "201", description = "Token created")
	public Response saveToken(
			@Parameter(name = "fcmToken", required = true, description = "A map contains fcm token + user plateforme") Map<String, String> request) {
		fcmNotificationService.saveTokenToDataBase(request);
		//return Response.status(Response.Status.CREATED).build();
		return responseService.buildResponse(Response.Status.CREATED);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateToken")
	@Operation(summary = "This function is used to save FCM token", tags = "firebase notification Services")
	@ApiResponse(responseCode = "201", description = "Token created")
	public Response updateToken(
			@Parameter(name = "fcmToken", required = true, description = "A map contains fcm token + user plateforme") Map<String, String> request) {
		fcmNotificationService.updateTokenInDataBase(request);
		//return Response.status(Response.Status.CREATED).build();
		return responseService.buildResponse(Response.Status.CREATED);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/retrieveNotificationsByTokenValue")
	@Operation(summary = "This function is used to all notifications for a device", tags = "firebase notification Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "notification Set")
	public Response fetchAllNotificationByToken(@QueryParam("token") String token) {
		LOGGER.warn("fetching notification {}" ,token);
		return responseService.buildResponse(fcmNotificationService.retrieveNotificationsByTokenValue(token), Response.Status.OK);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/retrieveUnfetchedNotificationsByTokenValue")
	@Operation(summary = "This function is used to all notifications for a device", tags = "firebase notification Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "notification Set")
	public Response fetchAllUnfetchedNotificationByToken(
			@Parameter(name = "payload", required = true, description = "A map contains fcm token + fetched notif ids") Map<String, Object> request) {
//		return Response.ok(
//				fcmNotificationService.retrieveUnfetchidNotificationsByTokenValue((String) request.get("token"),
//						request.get("lastNotifId") != null ? ((BigDecimal) request.get("lastNotifId")).longValue()
//								: null))
//				.build();
		return responseService.buildResponse(fcmNotificationService.retrieveUnfetchidNotificationsByTokenValue((String) request.get("token"),
				request.get("lastNotifId") != null ? ((BigDecimal) request.get("lastNotifId")).longValue()
						: null), Response.Status.OK);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/markAsSeen")
	@Operation(summary = "This function is used to mark notifications as seen", tags = "firebase notification Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "done")
	public Response markAsSeen(
			@Parameter(name = "notificationIds", required = true, description = "A map contains fcm token + user plateforme") Map<String, Object> request) {
		fcmNotificationService.markAsSeen(request);
		return responseService.buildResponse(Response.Status.OK);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sendNotification")
	@Operation(summary = "This function is used send a custom notification", tags = "firebase notification Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "200", description = "done")
	public Response sendCustomNotification(
			@Parameter(name = "notification payload", required = true, description = "A notification request payload") SendNotificationsRequest request) throws BadRequestException {
		//make Async service to avoid blocking response
		fcmNotificationService.sendCustomNotification(request);
		return responseService.buildResponse(Response.Status.OK);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deleteToken")
	@Operation(summary = "This function is used to an fcm token", tags = "firebase notification Services", security = {
			@SecurityRequirement(name = "api_key_access") })
	@ApiResponse(responseCode = "204", description = "token deleted")
	public Response deleteToken(
			@Parameter(name = "fcm token payload", required = true, description = "A map contains fcm token") Map<String, String> request) {
		LOGGER.warn("Deleting fcm token {}", request.get("token"));
		fcmNotificationService.deleteToken(request.get("token"));
		return responseService.buildResponse(Response.Status.NO_CONTENT);
	}

}
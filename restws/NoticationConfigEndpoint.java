package com.carrus.statsca.restws;

import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

import com.carrus.statsca.admin.dto.NotificationConfigDTO;
import com.carrus.statsca.admin.restws.utils.SwaggerGlobalDeclarations;
import com.carrus.statsca.ejb.interfaces.NotificationConfigService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Stateless
@PermitAll
@SwaggerGlobalDeclarations
@Path("/notificationConfig")
@OpenAPIDefinition(info = @Info(title = "notification service", description = "Provides all the function to configure notification preferences", version = "1.0.0"))
public class NoticationConfigEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticationConfigEndpoint.class);

    @Inject
    private NotificationConfigService notificationConfigService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/retrieveConfigurationForUser")
    @Operation(summary = "This function is used to retrieve notification config for an fcm token", tags = "NoticationConfigEndpoint", security = {
            @SecurityRequirement(name = "api_key_access") })
    @ApiResponse(responseCode = "200", description = "done")
    public Response retrieveNotificationConfigForToken(@QueryParam("token") String token) {
        LOGGER.info("fetching notification config for token: {}", token);
        return Response.ok(notificationConfigService.fetchUserNotificationConfig(token)).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fetchNotificationConfigForAdmin")
    @Operation(summary = "This function is used to retrieve notification config for a given plateform", tags = "NoticationConfigEndpoint", security = {
            @SecurityRequirement(name = "api_key_access") })
    @ApiResponse(responseCode = "200", description = "done")
    public Response fetchNotificationConfigForAdmin(@QueryParam("plateform") String plateform) {
        LOGGER.info("fetching notification config for plateform: {}", plateform);
        return Response.ok(notificationConfigService.fetchNotificationConfigForAdmin(plateform)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/enableDisableNotificationTypeForUser")
    @Operation(summary = "This function is used enable/disable notification type for an fcm token", tags = "NoticationConfigEndpoint", security = {
            @SecurityRequirement(name = "api_key_access") })
    @ApiResponse(responseCode = "200", description = "done")
    public Response enableDisableUserNotificationConfig(
            @Parameter(name = "payload", required = true, description = "A map contains fcm token + notification type") Map<String, String> request) {
        notificationConfigService.enableDisableUserNotificationConfig(request.get("token"), request.get("type"));
        return Response.status(Response.Status.OK).build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/enableDisableUserNotificationForPlateforme")
    @Operation(summary = "This function is used enable/disable notification type for agiven plateform", tags = "NoticationConfigEndpoint", security = {
            @SecurityRequirement(name = "api_key_access") })
    @ApiResponse(responseCode = "200", description = "done")
    public Response enableDisableUserNotificationForPlateforme(
            @Parameter(name = "payload", required = true, description = "An object contains a notification config") NotificationConfigDTO configDTO) {
        notificationConfigService.enableDisableUserNotificationForPlateforme(configDTO);
        return Response.status(Response.Status.OK).build();
    }
}

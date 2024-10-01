package com.carrus.statsca.admin.restws;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.ResponseService;
import com.carrus.statsca.admin.Authentication;
import com.carrus.statsca.admin.StoreAdmin;
import com.carrus.statsca.admin.UserService;
import com.carrus.statsca.admin.dto.Parameters;
import com.carrus.statsca.admin.dto.ParametreDTO;
import com.carrus.statsca.admin.dto.UserDTO;
import com.carrus.statsca.admin.restws.utils.SwaggerGlobalDeclarations;
import com.carrus.statsca.jwt.JWTRequestFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@PermitAll
@SwaggerGlobalDeclarations
@Path("/authentication")
@OpenAPIDefinition(info = @Info(title = "Authentication Services", description = "provides all the functions concerning the authentication on the STATSCA application ", version = "3.0.0"))
public class AuthenticationEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEndpoint.class);
	
	@Inject
	Authentication authentication;
	
	@Inject
	UserService userService;
	
//    @Inject
//    private NotificationPreferenceService notificationPreferenceService;

	@EJB
	ResponseService responseService;

	@Context
	HttpServletRequest httpServletRequest;

	@GET
	@Path("/retrieveParameters")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Retrieve application parameters", tags = "Authentication Services")
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParametreDTO.class))), description = "Application parameters")
	public Response retrieveParameters() {

		ObjectMapper mapper = new ObjectMapper();
		try {
			Set<ParametreDTO> params = StoreAdmin.getInstance().retrieveParameters();
			if (CollectionUtils.isEmpty(params)) {
				throw new Exception("Empty list parameters");
			}
			
			String jsonString = mapper.writeValueAsString(params);
			LOGGER.debug("retrieveParameters() : params = [{}]", jsonString);
			return responseService.buildResponse(jsonString, Response.Status.OK);	

		} catch (JsonProcessingException e) {
			LOGGER.error(String.format("error while generating json %s", e.getMessage()));
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.UNAUTHORIZED);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.FORBIDDEN);
		}
	}
	
	
	@POST
	@Path("/authenticate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Authenticate an user with the given parameters", tags = "Authentication Services", security = {@SecurityRequirement(name = "api_key_access")})
	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), description = "The user authenticated")
	public Response authenticate(@Parameter(name = "authenticateParameters", required = true, description = "Authentication parameters") Parameters parameters) {

		try {
			String uuid = parameters.getUuid();
			UserDTO user = (UserDTO) httpServletRequest.getAttribute(JWTRequestFilter.USER);
			ObjectMapper mapper = new ObjectMapper();
			//LOGGER.debug("authenticate() : user = [{}]", mapper.writeValueAsString(user));

			authentication.authenticateWithCredentials(user, uuid);
			String jsonString = mapper.writeValueAsString(user);
			LOGGER.debug("authenticate() : user = [{}]", jsonString);
			
			this.httpServletRequest.setAttribute("user", user);
			return responseService.buildResponse(jsonString, Response.Status.OK);	

		} catch (JsonProcessingException e) {
			LOGGER.error(String.format("error while generating json %s", e.getMessage()));
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.UNAUTHORIZED);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.FORBIDDEN);
		}
	}
	
//	@POST
//	@Path("/settingLanguageUser")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes({ MediaType.APPLICATION_JSON })
//	@Operation(summary = "Setting the language of the users", tags = "User Services", security = {@SecurityRequirement(name = "api_key_access")})
//	@ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))), description = "The user language")
//	public Response settingLanguageUser(@Parameter(name = "UserLanguageParameters", required = true, description = "User parameters") Parameters parameters) {
//
//		try {
//			String language = parameters.getLanguage();
//			UserDTO user = (UserDTO) httpServletRequest.getAttribute(JWTRequestFilter.USER);
//			Long UserId = user.getUserID();
//			user.setLanguage(language);
//			ObjectMapper mapper = new ObjectMapper();
//			//LOGGER.debug("authenticate() : user = [{}]", mapper.writeValueAsString(user));
//			userService.updateLanguageUser(user);
//			
//			String jsonString = mapper.writeValueAsString(user);
//			LOGGER.debug("authenticate() : user = [{}]", jsonString);
//			
//			this.httpServletRequest.setAttribute("user", user);
//			return responseService.buildResponse(jsonString, Response.Status.OK);	
//
//		} catch (JsonProcessingException e) {
//			LOGGER.error(String.format("error while generating json %s", e.getMessage()));
//			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
//		} catch (IllegalStateException e) {
//			LOGGER.error(e.getMessage());
//			return responseService.buildResponse(Response.Status.UNAUTHORIZED);
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage());
//			return responseService.buildResponse(Response.Status.FORBIDDEN);
//		}
//	}
}

package com.carrus.statsca.admin.restws;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.ResponseService;
import com.carrus.statsca.admin.MailService;
import com.carrus.statsca.admin.dto.ContactMailDTO;
import com.carrus.statsca.admin.dto.UserDTO;
import com.carrus.statsca.admin.mail.EnumLanguage;
import com.carrus.statsca.admin.restws.utils.SwaggerGlobalDeclarations;
import com.carrus.statsca.jwt.JWTRequestFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@PermitAll
@SwaggerGlobalDeclarations
@Path("/mail")
@OpenAPIDefinition(info = @Info(title = "Mail Services", description = "provides all the functions concerning the mail on the STATSCA application ", version = "3.0.0"))
public class MailEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailEndpoint.class);
	
	@Context
	HttpServletRequest httpServletRequest;

	@EJB
	ResponseService responseService;
	
	@EJB
	MailService mailService;

	@POST
	@Path("/contact")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Send a mail to contact email", tags = "Mail Services", security = {@SecurityRequirement(name = "api_key_access")})
	@ApiResponse(responseCode = "200")
	public Response contact(@Parameter(name = "contactMail", required = true, description = "Contact mail attributes") ContactMailDTO dto) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			LOGGER.debug("contact() : dto = [{}]", mapper.writeValueAsString(dto));
			
			UserDTO user = (UserDTO) httpServletRequest.getAttribute(JWTRequestFilter.USER);
			LOGGER.debug("contact() : user = [{}]", mapper.writeValueAsString(user));

			mailService.sendContactMail(user, dto, EnumLanguage.FR);
			return responseService.buildResponse(Response.Status.OK);	

		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (IllegalStateException e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.UNAUTHORIZED);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return responseService.buildResponse(Response.Status.FORBIDDEN);
		}
	}

}

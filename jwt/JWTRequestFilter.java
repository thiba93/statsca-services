package com.carrus.statsca.jwt;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.UserService;
import com.carrus.statsca.admin.dto.RoleDTO;
import com.carrus.statsca.admin.dto.UserDTO;
import com.carrus.statsca.admin.entity.UserEntity;
import com.carrus.statsca.admin.restws.utils.SecurityUtil;
import com.carrus.statsca.admin.restws.utils.SwaggerGlobalDeclarations;
import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTRequestFilter implements ContainerRequestFilter {

	public final static String USER = "USER";

	/** Logguer par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTRequestFilter.class);

	/** Injection du context WS RS pour récupérer l'annotation sur la méthode */
	@Context
	private ResourceInfo resourceInfo;

	/** Information sur l'URL d'accès au service */
	@Context
	private UriInfo uriInfo;

	@Inject
	UserService userService;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		if ("/authentication/retrieveParameters".equals(requestContext.getUriInfo().getPath())) {
			return;
		}
		if ("/notification/updateToken".equals(requestContext.getUriInfo().getPath())) {
			return;
		}

		// On recherche l'annotation liée à la méthode, sinon à la classe
		SwaggerGlobalDeclarations security = resourceInfo.getResourceMethod().getAnnotation(SwaggerGlobalDeclarations.class);
		if (security == null) {
			security = resourceInfo.getResourceClass().getAnnotation(SwaggerGlobalDeclarations.class);
		}
		if (security != null && security.value()) { 
			if (LOGGER.isDebugEnabled()) {
				requestContext.getHeaders().entrySet().stream().forEach(entry -> {
					LOGGER.debug("filter() : key = [{}] value = ", entry.getKey());
					entry.getValue().forEach(value -> LOGGER.debug("\t\t[{}]", value));
				});
			}
			
			
			// Le jeton JWT est obligatoire, on le recherche dans le header de la requête
			final String authorizationHeader = requestContext.getHeaderString(SwaggerGlobalDeclarations.ACCESS_HEADER);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("filter() : Request with authorization header : [{}]", authorizationHeader);
			}
			requestContext.getHeaders().remove(SwaggerGlobalDeclarations.ACCESS_HEADER);

			// authorizationHeader = null; // test de l'inexistence du header

			// Check if the HTTP Authorization header is present and formatted correctly
			if ((authorizationHeader == null || "".equals(authorizationHeader)) || !isTokenBasedAuthentication(authorizationHeader)) {
				LOGGER.error("filter() : Invalid authorization header : [{}]", authorizationHeader);
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity("error.invalid.authorization.header")
						.build());
				return;
			}

			// Extract the token from the HTTP Authorization header
			final String applicationToken = authorizationHeader.substring(SwaggerGlobalDeclarations.AUTHENTICATION_SCHEME.length()).trim();

			// applicationToken = null; // test de l'inexistence du token

			if ((applicationToken == null || "".equals(applicationToken))) {
				LOGGER.error("filter() : authenticationToken = [{}]", applicationToken);
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity("error.invalid.application.token")
						.build());
				return;
			}

			// applicationToken = null; // test du décodage du token

			JWTPayload jwtPayload;
			try {
				jwtPayload = JWTControl.getInformationFromAuthJWT(applicationToken);
			} catch (InvalidJwtException ije) {
				LOGGER.error("filter() : error = [{}]", ije.getMessage());
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity("error.invalid.application.token")
						.build());
				return;
			} catch (MalformedClaimException mce) {
				LOGGER.error("filter() : error = [{}]", mce.getMessage());
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity("error.invalid.application.token")
						.build());
				return;
			}

			// jwtPayload.setExp(1); // test de l'expiration du jeton 

			if (JWTControl.isJWTTokenExpired(jwtPayload)) {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity("error.expired.token")
						.build());
				return;
			}

			// jwtPayload.setSub(""); // test de l'invalidité du token

			try {
				if (!JWTControl.isJWTTokenValid(jwtPayload)) {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
							.type(MediaType.APPLICATION_JSON_TYPE)
							.entity("error.invalid.application.token")
							.build());
					return;
				}
			} catch (Exception e) {
				LOGGER.error("filter() : error = [{}]", e.getMessage());
				requestContext.abortWith(Response.status(Response.Status.PRECONDITION_FAILED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity(e.getMessage())
						.build());
				return;
			}

			// jwtPayload.setEmail(""); // test de l'invalidité du user

			if (jwtPayload.getEmail() == null || !SecurityUtil.isValidEmail(jwtPayload.getEmail())) {
				LOGGER.error("filter() : invalid email = [{}]", jwtPayload.getEmail());
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
						.type(MediaType.APPLICATION_JSON_TYPE)
						.entity("error.invalid.user")
						.build());
				return;
			}

			//  Authenticate the user using the credentials provided
			UserDTO user = userService.retrieveByEmail(jwtPayload.getEmail());

			// UserDTO user = null; // test de la nullité du user

			if (user == null) {
				LOGGER.warn("filter() : unreferenced user = [{}]", jwtPayload.getEmail());
//				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
//						.type(MediaType.APPLICATION_JSON_TYPE)
//						.entity("error.invalid.user")
//						.build());
//				return;
				user = new UserDTO();
				user.setEmail(jwtPayload.getEmail());
				user.setRole(new RoleDTO(UserEntity.DEFAULT_ROLE_ID, "", ""));
				userService.createUser(user);
				user = userService.retrieveByEmail(jwtPayload.getEmail());
				ObjectMapper mapper = new ObjectMapper();
				LOGGER.warn("filter() : new created user = [{}]", mapper.writeValueAsString(user));
			}
			user.setGsm(jwtPayload.getGsm());
			user.setFirstName(jwtPayload.getFirstname());
			user.setOrganization(jwtPayload.getOrganization());
			user.setPlateforme(jwtPayload.getPlateforme());
			user.setLastName(jwtPayload.getLastname());

			requestContext.setProperty(USER, user);
			if (LOGGER.isDebugEnabled()) {
				requestContext.getPropertyNames().forEach(key -> LOGGER.debug("filter() : header key = [{}] value = [{}]", key, requestContext.getProperty(key)));
			}
		}
	}

	private boolean isTokenBasedAuthentication(String authorizationHeader) {
		// Check if the Authorization header is valid
		// It must not be null and must be prefixed with "Bearer" plus a whitespace
		// The authentication scheme comparison must be case-insensitive
		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(SwaggerGlobalDeclarations.AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}


}

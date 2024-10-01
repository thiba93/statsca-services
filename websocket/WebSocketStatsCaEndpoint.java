package com.carrus.statsca.websocket;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.RaceCardService;
import com.carrus.statsca.admin.restws.utils.SecurityUtil;
import com.carrus.statsca.dto.SessionDTO;
import com.carrus.statsca.event.DisconnectionEvt;
import com.carrus.statsca.event.Evt;
import com.carrus.statsca.event.RaceEvt;
import com.carrus.statsca.event.RaceEvtAbstract;
import com.carrus.statsca.event.SessionChange;
import com.carrus.statsca.event.SessionChange.SessionChangeTypeEnum;
import com.carrus.statsca.event.SessionEvt;
import com.carrus.statsca.jwt.JWTControl;
import com.carrus.statsca.jwt.JWTPayload;
import com.carrus.statsca.messages.SessionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmc.club.event.RaceChange.RaceChangeTypeEnum;

@ServerEndpoint(value = "/chronology", encoders = { EvtEncoder.class })
public class WebSocketStatsCaEndpoint {

	@Inject
	private RaceCardService racecardService;

	/** Logger par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketStatsCaEndpoint.class);

	/**
	 * Map des sessions actifs (en attendant d'avoir la possibilité de mapper avec
	 * un indentifiant de session IHM, on mappe pour le moment avec l'identifiant de
	 * session Websocket)
	 */
	private static final Map<String, WebSocketStatsCaEndpoint> ACTIVES_SESSIONS = Collections
			.synchronizedMap(new HashMap<>());

	/** Queue pour toutes les sessions authentifiées de websocket */
	private Session session = null;

	/**
	 * Broadcast event to all registered sessions
	 * 
	 * @param evt Evt to broadcast
	 */
	public static void diffusionEvt(Evt evt) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Broadcasting event {} . to {} sessions", evt, ACTIVES_SESSIONS.values().size());
			}
			// Broadcast de l'évènement à toutes les sessions actives
			for (WebSocketStatsCaEndpoint endpoint : ACTIVES_SESSIONS.values()) {
				endpoint.sendEvt(evt);
			}
		} catch (Exception e) {
			LOGGER.warn("Echec lors de la diffusion du message " + evt, e);
		}
	}

	/**
	 * Push event to the specified session endpoint
	 * 
	 * @param evt     Event to broadcast
	 * @param session session option to target
	 */
	public static void diffusionEvt(Evt evt, Session session) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Broadcasting event {} . to {} ", evt, session.getId());

			}
			// Broadcast de l'évènement à la session en paramètre
			WebSocketStatsCaEndpoint endpoint = ACTIVES_SESSIONS.get(session.getId());
			endpoint.sendEvt(evt);
			// }
		} catch (Exception e) {
			LOGGER.warn("Echec lors de la diffusion du message " + evt, e);
		}
	}

	/** Callback de l'ouverture de la websocket par le client */
	@OnOpen
	public void openConnection(Session session, EndpointConfig config) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Connection WS opened for session : {}", session.getId());
		}
		
		//sauvegarde la dernière session qui a été ouverte
		this.session = session;
		// TODO On Open WEBSOCKET connection
		// 1/ Check token sent through message
		// 2/ If token valid => push racecard with stakes u
	}

	/** Callback de la fermeture de la websocket vers le client */
	@OnClose
	public void onClose(Session session, CloseReason c) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Closing websocket session : {} for reason {} ", session.getId(), c.getReasonPhrase());
		}
		this.session = null;
		if (ACTIVES_SESSIONS.containsKey(session.getId())) {
			ACTIVES_SESSIONS.remove(session.getId());
		}
	}

	/**
	 * Méthode invoquée à la réception de message sur la WebSocket, en provenance de
	 * la console.
	 * 
	 * @param message Message reçu sous la forme d'une chaine de caractères
	 * @param session La session correspondante au WebSocket qui recoit le message
	 * @throws BISException
	 */
	@OnMessage
	private void onMessage(String message, Session session) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reception WS : {} , session {} ", message, session.getId());
		}
		JsonReader jsonReader = null;
		try {
			jsonReader = Json.createReader(new StringReader(message));
			JsonObject jsonObject = jsonReader.readObject();
			String typeEvt = jsonObject.getString("type");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onMessage() : typeEvt = [{}]", typeEvt);
			}

			if ("AUTHENTICATION".equals(typeEvt) || "REAUTHENTICATION".equals(typeEvt)) {
				// Il s'agit d'un message d'authentification on le traite
				try {
					SessionRequest request = (new SessionDecoder()).decode(message);
					// check Token from back admin if present
					boolean authenticated = _authenticate(session, request);
					Evt sessionResponse = null;
					if (authenticated) {
						if ("AUTHENTICATION".equals(typeEvt)) {
							// send complete racecard
							sessionResponse = getRacecardEvt(SessionChangeTypeEnum.SESSION_LOADED);
						} else {
							// send light racecard
							sessionResponse = getLightRacecardEvt(SessionChangeTypeEnum.SESSION_LOADED);
						}
						// add to active sessions
						if (!ACTIVES_SESSIONS.containsKey(session.getId())) {
							ACTIVES_SESSIONS.put(session.getId(), this);
						}

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Connection (re)opened for session : {}", session.getId());
						}
					} else {
						// if not authenticated, send DisconnectionEvt
						sessionResponse = new DisconnectionEvt();
					}
					// On retourne l'évènement correspondant à la console
					if (sessionResponse != null) {
						if (LOGGER.isDebugEnabled()) {
							ObjectMapper mapper = new ObjectMapper();
							LOGGER.debug("Authentication response to {}", session.getId());
						}
						// On ne passe pas par la méthode de diffusion pour retourner la réponse
						// d'authentification
						// car la console n'est pas encore enregistrée dans les consoles actives
						try {
							session.getBasicRemote().sendObject(sessionResponse);
							// also send racecard and recipe event for now
							/*
							 * Evt sessionEvt = getRacecardEvt(); if(sessionEvt != null)
							 * session.getBasicRemote().sendObject(sessionEvt);
							 */
							/*
							 * Evt recipeEvt = getCurrentRaceRecipeEvt(); if (recipeEvt != null)
							 * session.getBasicRemote().sendObject(recipeEvt);
							 */
						} catch (IOException | EncodeException e) {
							LOGGER.error("Error during authentication broadcast event", e);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Authentication request failed", e);
				}

				/*
				 * case "SYSTEM": // Il s'agit d'un message de KEEPALIVE, on retient l'heure de
				 * réception session.getUserProperties().put(LAST_KEEPALIVE_KEY, new Date());
				 * break;
				 */
			} else {
				// Pour les autres types de messages, on les loggue simplement
				LOGGER.warn("Unexpected message received : {}", message);
			}
		} finally {
			if (jsonReader != null)
				jsonReader.close();
		}
	}

	private boolean _authenticate(Session session, SessionRequest request) {
		String token = request.getToken();
		boolean disconnection = false;
		if (token != null) {
			JWTPayload jwtPayload;
			try {
				jwtPayload = JWTControl.getInformationFromAuthJWT(token);
			} catch (InvalidJwtException | MalformedClaimException e) {
				LOGGER.error("Invalid JWT Token : [{}]", e.getMessage());
				return false;
			}

			if (JWTControl.isJWTTokenExpired(jwtPayload)) {
				LOGGER.error("Expired JWT Token");
				disconnection = true;
			} else if (!JWTControl.isJWTTokenValid(jwtPayload)) {
				LOGGER.error("Invalid JWT Token");
				disconnection = true;
			} else if (jwtPayload.getEmail() == null || !SecurityUtil.isValidEmail(jwtPayload.getEmail())) {
				LOGGER.error("Unknown user inside JWT Token");
				disconnection = true;
			}
			/*
			 * if (disconnection) { return new DisconnectionEvt(); } else { UserDTO user =
			 * userService.retrieveByEmail(jwtPayload.getEmail()); if (user != null &&
			 * user.getEmail().equals(request.getEmail())) { return
			 * getRacecardEvt(SessionChangeTypeEnum.SESSION_LOADED); } }
			 */
		}
		return !disconnection;
	}

	// -- FIN SERVICES A MIGRER
	private void sendEvt(Evt evt) {
		try {
			if (session != null && session.isOpen()) {
				if (evt instanceof RaceEvtAbstract raceEvt) {
					// La course est dans le context, si l'évènement a un impact sur la course
					if (evt instanceof RaceEvt
							&& ((RaceEvt) evt).getRaceChangeType() == RaceChangeTypeEnum.EXPECTED_START) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Changement d'heure de départ pour R {} C {}", raceEvt.getId(),
									raceEvt.getNumber());
						}
					}
				}
				session.getBasicRemote().sendObject(evt);
			} else {
				if (session == null) {
					LOGGER.debug("La session est null envoi impossible");
				} else if (!session.isOpen()) {
					LOGGER.debug("La session n'est pas ouverte envoi impossible");

				}
			}
		} catch (IOException | EncodeException e) {
			LOGGER.error("Erreur de diffusion d'un message sur le WebSocket", e);
		}
	}

	private Evt getRacecardEvt(SessionChangeTypeEnum sessionChangeType) {
		SessionDTO sessionDTO = racecardService.getRaceCard();

		ObjectMapper mapper = new ObjectMapper();
		try {
			if (sessionDTO != null) {
				String jsonString = mapper.writeValueAsString(sessionDTO);
				SessionChange sessionChange = new SessionChange(ZonedDateTime.now(), sessionChangeType, jsonString);
				return new SessionEvt(sessionChange);
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while generating json {}", e.getMessage());
		}
		return null;
	}

	private Evt getLightRacecardEvt(SessionChangeTypeEnum sessionChangeType) {
		String lightRacecard = racecardService.getLightRaceCard();
		LOGGER.debug("getLightRacecardEvt() : sessionChangeType = [{}] lightRacecard = [{}]", sessionChangeType,
				lightRacecard);
		SessionChange sessionChange = new SessionChange(ZonedDateTime.now(), sessionChangeType, lightRacecard);
		return new SessionEvt(sessionChange);
	}

}

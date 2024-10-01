/**
 * 
 */
package com.carrus.statsca.websocket;


import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.messages.SessionRequest;


/**
 * @author h.ramananjara
 *
 */
public class SessionDecoder implements Decoder.Text<SessionRequest> {
	/** Logger par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionDecoder.class);
	
	/* (non-Javadoc)
	 * @see javax.websocket.Decoder#init(javax.websocket.EndpointConfig)
	 */
	@Override
	public void init(EndpointConfig ec) {}

	/* (non-Javadoc)
	 * @see javax.websocket.Decoder#destroy()
	 */
	@Override
	public void destroy() {}

	/* (non-Javadoc)
	 * @see javax.websocket.Decoder.Text#decode(java.lang.String)
	 */
	@Override
	public SessionRequest decode(String s) throws DecodeException {
		LOGGER.debug("decode() : input = [{}]", s);
		try(JsonReader jsonReader = Json.createReader(new StringReader(s));) {	
			JsonObject jsonObject = jsonReader.readObject();
			LOGGER.debug("decode() : email = [{}] token = [{}]", jsonObject.getString("email"), jsonObject.getString("token"));
			return new SessionRequest(jsonObject.getString("email"), jsonObject.getString("token"));
			
		} catch (JsonParsingException e) {
			throw new DecodeException(e.getLocation().toString() ,"Erreur de format JSON en entrée", e);
		}
	}

	@Override
	public boolean willDecode(String s) {
		return s != null && !s.isEmpty();
	}
}

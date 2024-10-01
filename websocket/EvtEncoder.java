package com.carrus.statsca.websocket;

import com.carrus.statsca.event.Evt;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class EvtEncoder implements Encoder.Text<Evt>{

	@Override
	public void init(EndpointConfig config) {}

	/** On appelle la méthode toJson de l'objet */
	@Override
	public String encode(Evt object) throws EncodeException {
		return object.toJson();
	}

	@Override
	public void destroy() {}
}
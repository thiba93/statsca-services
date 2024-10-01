package com.carrus.statsca.ejb;

import javax.ejb.Stateless;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.carrus.statsca.ResponseService;

@Stateless
public class ResponseServiceEJB implements ResponseService {
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

	private static final String ORIGIN_CONTENT_TYPE_ACCEPT_AUTHORIZATION = "origin, content-type, accept, authorization";

	private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

	private static final String GET_POST_PUT_DELETE_OPTIONS_HEAD = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

	private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

	private static final String ACCESS_CONTROL_MAX_AGE_VALUE = "1209600";


	@Override
	public Response buildResponse(Object entity, Status status) {
		ResponseBuilder responseBuilder = Response.status(status)
				// CORS
				.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				.header(ACCESS_CONTROL_ALLOW_HEADERS, ORIGIN_CONTENT_TYPE_ACCEPT_AUTHORIZATION)
				.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
				.header(ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD)
				.header(ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_VALUE);


		if(entity != null) {
			responseBuilder = responseBuilder.entity(entity);
		}

		return responseBuilder.build();

	}

	@Override
	public Response buildResponse(Object entity, Status status, NewCookie... cookies) {
		ResponseBuilder responseBuilder = Response.status(status)
				// CORS
				.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				.header(ACCESS_CONTROL_ALLOW_HEADERS, ORIGIN_CONTENT_TYPE_ACCEPT_AUTHORIZATION)
				.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
				.header(ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD)
				.header(ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_VALUE);

		if(entity != null) {
			responseBuilder = responseBuilder.entity(entity).cookie(cookies);
		}

		if(cookies != null) {
			responseBuilder = responseBuilder.cookie(cookies);
		}

		return responseBuilder.build();

	}

	@Override
	public Response buildResponse(Status status) {
		return Response.status(status)
				// CORS
				.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				.header(ACCESS_CONTROL_ALLOW_HEADERS, ORIGIN_CONTENT_TYPE_ACCEPT_AUTHORIZATION)
				.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
				.header(ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD)
				.header(ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_VALUE).build();
	}

	@Override
	public Response buildResponse(Status status, NewCookie... cookies) {
		ResponseBuilder responseBuilder = Response.status(status)
				// CORS
				.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				.header(ACCESS_CONTROL_ALLOW_HEADERS, ORIGIN_CONTENT_TYPE_ACCEPT_AUTHORIZATION)
				.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
				.header(ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD)
				.header(ACCESS_CONTROL_MAX_AGE, ACCESS_CONTROL_MAX_AGE_VALUE);

		if(cookies != null) {
			responseBuilder = responseBuilder.cookie(cookies);
		}

		return responseBuilder.build();
	}
}

package com.carrus.statsca;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public interface ResponseService {

	Response buildResponse(Object entity, Status status);

	Response buildResponse(Object entity, Status status, NewCookie... cookies);

	Response buildResponse(Status status);

	Response buildResponse(Status status, NewCookie... cookies);

}

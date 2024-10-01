@OpenAPIDefinition(
	info = @Info(title = "STATSCA ADMIN Services REST",
		description = "STATSCA ADMIN Services REST",
		version = "1.0.0",
		license = @License(name = "MIT")),
	servers = { @Server(description = "Serveur de développement ADMIN STATSCA", url = "http://localhost:3801/admin"),
		} )

@JsonbNillable(true)
package com.carrus.statsca.admin.restws;

import javax.json.bind.annotation.JsonbNillable;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
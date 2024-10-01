@OpenAPIDefinition(
	info = @Info(title = "STATSCA Services REST",
		description = "STATSCA Services REST",
		version = "1.0.0",
		license = @License(name = "MIT")),
	servers = { @Server(description = "Serveur de développement interne", url = "http://localhost:3801/rest"),
		} )

@JsonbNillable(true)
package com.carrus.statsca.restws;

import javax.json.bind.annotation.JsonbNillable;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
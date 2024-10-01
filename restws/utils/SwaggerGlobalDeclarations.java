package com.carrus.statsca.restws.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

/**
 * Cette annotation permet de factoriser les déclarations communes à tous les services
 * WS de l'application, pour la documentation Swagger, OpenAPI.
 * En particulier les définitions des schémas de sécurité de l'application.
 * 
 * @author BT - ARTSYS 2022
 * @since 20 avril 2022
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SecuritySchemes({
	@SecurityScheme(name = "api_key_access", type = SecuritySchemeType.APIKEY,
			in=SecuritySchemeIn.HEADER, paramName = "Auth",
			description = "Properties JWS Token")
	})
@Deprecated
public @interface SwaggerGlobalDeclarations {
	
}

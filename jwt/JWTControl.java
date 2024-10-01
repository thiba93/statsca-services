package com.carrus.statsca.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.StoreAdmin;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTControl {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTControl.class);
	
    /**
     * Méthode interne qui permet de valider et d'extraire les informations à partir du jeton applicatif.
     *
     * @param authToken Le jeton JWT qui contient les informations machines
     * 
     * @return null si tout c'est bien passé, et que la machine est complétée, une réponse en erreur sinon
     * @throws InvalidJwtException 
     * @throws MalformedClaimException 
     */
    public static JWTPayload getInformationFromAuthJWT(String authToken) throws MalformedClaimException, InvalidJwtException {
    	// JWTPayload jwtPayload = parseJWT(authToken);
    	
    	JWTPayload jwtPayload = parseJWTJOSE4J(authToken);
    	
    	// JWTPayload jwtPayload = parseJWTJJWT(authToken);

        // Tout c'est bien passé, on retourne null
        return jwtPayload;
    }
    
//    private static JWTPayload parseJWT(String authToken) {
//    	Base64.Decoder decoder = Base64.getUrlDecoder();
//    	ObjectMapper mapper = new ObjectMapper();
//        try {
//        	String[] chunks = authToken.split("\\.");
//        	String header = new String(decoder.decode(chunks[0]));
//        	String payload = new String(decoder.decode(chunks[1]));        	
//        	LOGGER.info("parseJWT() : header = [{}]\tpayload=[{}]", header, payload);
//			return mapper.readValue(payload, JWTPayload.class);
//		} catch (JsonMappingException e1) {
//			LOGGER.error("parseJWT() : mapping error", e1);
//		} catch (JsonProcessingException e1) {
//			LOGGER.error("parseJWT() : unmarshalling error", e1);
//		}        	
//    	return null;
//    }
    
    @SuppressWarnings("unused")
	private static JWTPayload parseJWTJOSE4J(String authToken) throws InvalidJwtException, MalformedClaimException {
    	ObjectMapper mapper = new ObjectMapper();
        // try {
        	// String[] chunks = authToken.split("\\.");
            JwtConsumer consumer = new JwtConsumerBuilder()
                	.setRequireExpirationTime()
                    .setRequireSubject()
            		.setRequireJwtId()
            		.setVerificationKey(StoreAdmin.getInstance().getJwtPublicKey())
        			.build();
        	
        	JwtClaims claims = consumer.processToClaims(authToken);
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug("Valid token : " + authToken);
            }
            // claims.getClaimsMap().entrySet().stream().forEach(entry -> LOGGER.info("parseJWTJOSE4J() : key = [{}] value = [{}]", entry.getKey(), entry.getValue()));
            
            JWTPayload jwtPayload = new JWTPayload();
            jwtPayload.setSub(claims.getClaimValueAsString("sub"));
            jwtPayload.setJti(claims.getClaimValueAsString("jti"));
            jwtPayload.setIss(claims.getClaimValueAsString("iss"));
            jwtPayload.setIat(((long) claims.getClaimValue("iat")) * 1000);
            jwtPayload.setExp(claims.getExpirationTime().getValueInMillis());
            jwtPayload.setGsm(claims.getClaimValueAsString("gsm"));
            jwtPayload.setFirstname(claims.getClaimValueAsString("firstname"));
            jwtPayload.setOrganization(claims.getClaimValueAsString("organization"));
            jwtPayload.setPlateforme(claims.getClaimValueAsString("plateform"));
            jwtPayload.setEmail(claims.getClaimValueAsString("email"));
            jwtPayload.setLastname(claims.getClaimValueAsString("lastname"));
            
            // LOGGER.info("jwtPayload = [{}]", mapper.writeValueAsString(jwtPayload));
            return jwtPayload;
        // } catch (InvalidJwtException e) {
        //	LOGGER.error("JOSE4j Invalid JWT : " + e.getMessage());
        // } catch (Exception e) {
        //	LOGGER.error("JOSE4j JWT Token validation error : " + authToken + " : " + e.getMessage());
        // }
        // return null;
    }    

//    @SuppressWarnings("unused")
//	private static void parseJWTJJWT(String authToken) {
//    	ObjectMapper mapper = new ObjectMapper();
//        try {
//			JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(sigKey).build();
//			Claims claims = jwtParser.parseClaimsJws(authToken).getBody();
//			// claims.entrySet().forEach(entry -> LOGGER.info("parseJWTJJWT() : key = [{}] value = [{}]", entry.getKey(), entry.getValue()));
//			
//            JWTPayload jwtPayload = new JWTPayload();
//            jwtPayload.setExp(claims.getExpiration().getTime());
//            jwtPayload.setIat(((long) claims.get("iat", Long.class)) * 1000);
//            jwtPayload.setIss(claims.get("iss", String.class));
//            jwtPayload.setJti(claims.get("jti", String.class));
//            jwtPayload.setEmail(claims.get("email", String.class));
//            jwtPayload.setOrg(claims.get("org", String.class));
//            jwtPayload.setPlateforme(claims.get("plateforme", String.class));
//            jwtPayload.setSub(claims.get("sub", String.class));
//            // LOGGER.info("jwtPayload = [{}]", mapper.writeValueAsString(jwtPayload));
//
//        } catch (Exception e) {
//        	LOGGER.error("JJWT JWT Token validation error : " + authToken + " : " + e.getMessage());
//        }
//    }
    
    public static boolean isJWTTokenExpired(JWTPayload jwtPayload) {
    	if (jwtPayload != null && jwtPayload.getExp() > 0) {
    		LocalDateTime expiration = LocalDateTime.ofInstant(Instant.ofEpochMilli(jwtPayload.getExp()), TimeZone.getDefault().toZoneId());
    		LOGGER.error("isJWTTokenExpired() : payload.exp = [{}] expiration = [{}]", jwtPayload.getExp(), expiration);
    		return expiration.isBefore(LocalDateTime.now());
    	}
    	return false;
    }

    public static boolean isJWTTokenValid(JWTPayload jwtPayload) {
    	String appName = StoreAdmin.getInstance().getApplicationName();
    	if (appName == null || "".equals(appName)) {
    		throw new IllegalArgumentException("error.nameless.application");
    	}
    	if (jwtPayload != null && jwtPayload.getSub() != null) {
    		LOGGER.error("isJWTTokenValid() : sub = [{}] application = [{}]", jwtPayload.getSub(), StoreAdmin.getInstance().getApplicationName());
    		return appName.equals(jwtPayload.getSub());
    	}
    	return false;
    }

}

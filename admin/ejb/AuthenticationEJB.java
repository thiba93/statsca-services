package com.carrus.statsca.admin.ejb;

import java.security.Key;
import java.security.SignatureException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.Authentication;
import com.carrus.statsca.admin.UserService;
import com.carrus.statsca.admin.dto.UserDTO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class AuthenticationEJB implements Authentication {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEJB.class);
	public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

//	@Inject
//	DataServiceAdmin dataServiceAdmin;
//	public static final int EXPIRATION_AUTH = 120;

	@Inject
	UserService userService;
	
	@Override
	public UserDTO authenticateWithCredentials(UserDTO user, String uuid) {

		if (user != null) {
			checkUUID(user, uuid);
			if (!userService.updateUser(user)) {
				// Don't throw error - User is still authorized
				LOGGER.info("Authentication - Impossible to update user");
			}
			LOGGER.info("Authentication - Logged in : {}", user.getEmail());

			return user;

		} else {
			LOGGER.error("authentication error: Invalid credentials");
			throw new IllegalArgumentException("authentication error: Invalid credentials");
		}
	}
	
	
//	@Override
//	public UserDTO authenticateWithCredentials(String email, String password, String uuid) {
//		if (!SecurityUtil.isValidEmail(email))
//			throw new IllegalArgumentException(SecurityUtil.NULL);
//
		// Authenticate the user using the credentials provided
//		UserDTO user = userService.retrieveByEmail(email);
//
//		if (user != null) {
//			String decryptedPwd = SecurityUtil.decryptPassword(password);
//			if (decryptedPwd == null || !SecurityUtil.verifyPassword(decryptedPwd, user.getPassword())) {
//				if(LOGGER.isDebugEnabled()) {
//					LOGGER.debug("User : {} , Pwd : {}, user pwd : {}", email, decryptedPwd, user.getPassword());
//				}
//				LOGGER.error("AuthenticationEndpoint.authenticate : Authentication - Invalid Credentials : {}", email);
//				throw new IllegalArgumentException("AuthenticationEndpoint.authenticate : Authentication - Invalid Credentials :" + email);
//			}
//			checkUUID(email, uuid, user);
//
			// Invalidate tokens - User might be already connected
//			invalidateTokens(user.getUserID());

			// Issue a valid token
//			user.setToken(issueToken(user, EXPIRATION_AUTH, null));
//			return user;
//
//		} else {
//			LOGGER.error("authentication error: Invalid credentials {}", email);
//			throw new IllegalArgumentException("authentication error: Invalid credentials " + email);
//		}
//	}
	


	private void checkUUID(final UserDTO user, final String uuid) {
		if (null == user.getUuidDevice()) {
			user.setUuidDevice(uuid);
		}
	}

	// @Schedule(hour = "2", dayOfWeek = "*", persistent = false)
//	@Override
//	public void cleanTokens() {
//		LOGGER.debug("Authentication - Purging tokens :: {}", StoreAdmin.getInstance().getTokensMap().size());
//		StoreAdmin.getInstance().getTokensMap().keySet().removeIf(e -> null == parseToken(e));
//		LOGGER.debug("Authentication - Remaining tokens :: {}", StoreAdmin.getInstance().getTokensMap().size());
//	}

	@Override
	public String parseToken(String token) {
		try {
			// Check if the token was issued by the server and if it's not expired
			return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody().getSubject();

		} catch (Exception e) {
			if (ExpiredJwtException.class.equals(e.getClass())) {
				LOGGER.debug("Authentication - Token : JWT Expired"); // Token has expired
			} else if (SignatureException.class.equals(e.getClass())) {
				LOGGER.debug("Authentication - Token : JWS Signature failed"); // Token's secret is not valid
			} else {
				LOGGER.debug("Authentication - Token : Unsupported || malformed JWS"); // Object is not a token
			}
			return null;
		}
	}

//	@Override
//	public UserDTO getUserFromToken(String token) {
//		if (StoreAdmin.getInstance().getTokensMap().containsKey(token)) {
//			return StoreAdmin.getInstance().getTokensMap().get(token);
//		} else {
//			return null;
//		}
//	}

	/**
	 * Invalidate tokens of the user based on his id
	 * 
	 * @param id
	 */
//	@Override
//	public void invalidateTokens(Long userId) {
//		LOGGER.debug("Authentication - Token list's size before token invalidation is  : {}", StoreAdmin.getInstance().getTokensMap().size());
//		for (var entry : StoreAdmin.getInstance().getTokensMap().entrySet()) {
//			// Invalidate tokens for a specific user based on userId
//			if (entry.getValue().getUserID().equals(userId)) {
//				StoreAdmin.getInstance().getTokensMap().remove(entry.getKey());
//			}
//		}
//		LOGGER.debug("Authentication - Token list's size after token invalidation is  : {}", StoreAdmin.getInstance().getTokensMap().size());
//	}

	/**
	 * To invalidate a single Token - Set email to null. <Br>
	 * To invalidate Tokens for a specific user - Set token to null.
	 * 
	 * @param token
	 * @param email
	 */
//	@Override
//	public void invalidateTokens(String token, String email) {
//		LOGGER.debug("Authentication - Token list's size before token invalidation is  : {}", StoreAdmin.getInstance().getTokensMap().size());
//		if (null == email) {
//			StoreAdmin.getInstance().getTokensMap().remove(token);
//		}
//
//		for (var entry : StoreAdmin.getInstance().getTokensMap().entrySet()) {
//
//			// Invalidate tokens for a specific user
//			if (null == token && entry.getValue().getEmail().equals(email)) {
//				StoreAdmin.getInstance().getTokensMap().remove(entry.getKey());
//			}
//		}
//		LOGGER.debug("Authentication - Token list's size after token invalidation is  : {}", StoreAdmin.getInstance().getTokensMap().size());
//	}

//	@Override
//	public UserDTO validateToken(String token) {
//		// Check if token is blackListed
//		if (!StoreAdmin.getInstance().getTokensMap().containsKey(token)) {
//			LOGGER.debug("Authentication - Token is Blacklisted");
//			return null;
//
//		} else {
//			String user = StoreAdmin.getInstance().getTokensMap().get(token).getEmail();
//			String expediter = parseToken(token);
//
//			// Compare token's creator && expediter
//			if (user.equals(expediter)) {
//				return StoreAdmin.getInstance().getTokensMap().get(token);
//			}
//
//			return null;
//		}
//	}

//	@Override
//	public boolean validateCode(final String code) {
//		Iterator<Entry<String, UserDTO>> iterator = StoreAdmin.getInstance().getTokensMap().entrySet().iterator();
//
//		while (iterator.hasNext()) {
//			Entry<String, UserDTO> entry = iterator.next();
//
//			// Remove token from tokensMap
//			if (code.equals(entry.getValue().getToken()) && code.equals(parseToken(entry.getKey()))) {
//				// Invalidate Code
//				iterator.remove();
//
//				// Change User's IP in DB
//				dataServiceAdmin.updateUser(new UserEntity(entry.getValue().getEmail(), entry.getValue().getUserUUID()));
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * Creates a valid token based on a claim (user's email or code). If no
	 * expiration Time was given (0), the token won't expire. Set code to null if
	 * not used.
	 */
//	@Override
//	public String issueToken(UserDTO us, int expirationTime, String code) {
//		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
//		Date expiration = (0 == expirationTime) ? null : Date.from(issuedAt.plus(expirationTime, ChronoUnit.MINUTES));
//
//		LOGGER.info("Authentication - Token Expires at : {} for {}", expiration, us.getEmail());
//
//		String token = Jwts.builder().setSubject(null != code ? code : us.getEmail()).setIssuedAt(Date.from(issuedAt))
//				.setExpiration(expiration).signWith(KEY).compact();
//		StoreAdmin.getInstance().getTokensMap().put(token,
//				new UserDTO(us.getUserID(), 
//						null == code ? us.getFirstName() : null,
//						null == code ? us.getLastName() : null, 
//						us.getEmail(), 
//						null == code ? us.getPassword() : null, 
//						null == code ? us.getActive() : null, 
//						null == code ? us.getUserUUID() : null, 
//						null != code ? code : null,
//						LocalDateTime.now(),
//						null == code ? us.getUuidDevice() : null, 
//						null == code ? us.getPreference() : null, 
//						null == code ? us.getOrganization() : null, 
//						null == code ? us.getRole() : null, 
//						null == code ? us.getLanguage() : null));
//
//		return token;
//	}

//	@Override
//	public void logout(String token, String email) {	
//		/* Invalidate user tokens */
//		invalidateTokens(token,email);
//		/*TODO Remove from ACTIVE_SESSIONS WS user session*/
//		LOGGER.info("the user "+email+"have been logged out");
//	}
}

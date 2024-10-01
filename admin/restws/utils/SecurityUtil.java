package com.carrus.statsca.admin.restws.utils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);

	public static final String NULL = "null or empty or not valid";

	//private static final int SALT_LENGTH = 32; // 32 bytes : 256-bit // default: 16 bytes : 128-bit
	//private static final int HASH_LENGTH = 64; // 64 bytes : 512-bit // default: 32 bytes : 256-bit
	private static final int VERSION = 19;

	private static final int MEMORY = 128; // 128 mb
	private static final int NB_ITERATION = 8;
	private static final int PARALLELISM = 4;

	//private static final String RSA = "RSA";
//	private static final String RSA_DECRYPT = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
//	private static final String RSA_DECRYPT = "RSA";
//	private static final String KEY_DER = "auth/private_key.der";
//	private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmoFP9DacVBZmvPvUf29g+YgEXIgLm0VIkp6RuGyjwqA+MTRGKy33D0r9V9CCUgQXvUi/3PpBbrdYGTI1qnNezqUbk3cF2vwgKeR2WC13DnAwO6Zl0lEkWNpWbK1ViY/Wp+K0MT0s0OM6ZpsFg6MDqgW1bXnVEswX4BNx1ehxVowIDAQAB";

	private static final int EMAIL_LENGHT = 37; // Email max length

//	private static Argon2 argon2;
//	private static String constant;
//	private static PrivateKey privateKey;

//	static {
//		argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, SALT_LENGTH, HASH_LENGTH);
//		constant = initArgon();
//		privateKey = retrievePrivateKey();
//	}

	private SecurityUtil() {
	}

//	public static String hashPassword(String password) {
//		String hash = null;
//		try {
////			if (!isValidPwd(password))
////				throw new IllegalArgumentException(NULL);
//
//			char[] pwdCharArray = password.toCharArray();
//
//			Instant beginHash = Instant.now();
//			LOGGER.debug("Creating hash for password '%s' : {}", pwdCharArray);
//
//			hash = argon2.hash(NB_ITERATION, MEMORY * 1024, PARALLELISM, pwdCharArray);
//
//			LOGGER.debug("Encoded constant length is '%s' : {}", constant.length());
//			LOGGER.debug("Encoded hash length is '%s' : {}", hash.length());
//			LOGGER.debug("Encoded hash is '%s' : {}", hash);
//
//			hash = hash.substring(constant.length(), hash.length());
//			LOGGER.debug("Encoded hash is '%s' : {}", hash);
//
//			Instant endHash = Instant.now();
//			LOGGER.debug("Process took %f s : {}", Duration.between(beginHash, endHash).toMillis() / 1024.0);
//
//		} catch (Exception e) {
//			LOGGER.error("Error when hashing password {} : {}", password, e.getMessage());
//		}
//		return hash;
//	}

//	public static boolean verifyPassword(String requestedPassword, String userPassword) {
//
//		boolean success = false;
//
//		try {
//
//			if (/* !isValidPwd(requestedPassword) || */ userPassword == null || userPassword.isBlank())
//				throw new IllegalArgumentException(NULL);
//
//			Instant beginVerify = Instant.now();
//			LOGGER.debug("Verifying hash...");
//
//			// prepare userPassword
//			StringBuilder userPwd = new StringBuilder();
//			userPwd.append(constant);
//			userPwd.append(userPassword);
//
//			// prepare requestedPassword
//			char[] pwdCharArray = requestedPassword.toCharArray();
//			success = argon2.verify(userPwd.toString(), pwdCharArray);
//			LOGGER.debug(success ? "Success!" : "Failure!");
//			Instant endVerify = Instant.now();
//			LOGGER.debug("Process took {} s ", Duration.between(beginVerify, endVerify).toMillis() / 1024.0);
//
//		} catch (Exception e) {
//			LOGGER.error("Error when verifying password {} : {}", requestedPassword, e.getMessage());
//		}
//		return success;
//	}

	private static boolean isValidPwd(String password) {
		try {
			if (password == null || password.isBlank()) {
				return false;
			} else {
				String regex = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[@$!%*#?&_\\\\-])"
						+ "(?=\\S+$).{12,24}$";
//					Password form control regex :
//						Must be from 12 to 24 characters long and contain no spaces
//						Must contain at least one: uppercase, lowercase, number, specialchar
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(password);
				return m.matches();
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static String initArgon() {
		StringBuilder sb = new StringBuilder();
		sb.append("$argon2id$v=").append(VERSION).append("$m=").append(MEMORY * 1024).append(",t=").append(NB_ITERATION)
				.append(",p=").append(PARALLELISM).append("$");
		return sb.toString();
	}

//	private static PrivateKey retrievePrivateKey() {
//
//		try (InputStream resourceAsStream = SecurityUtil.class.getClassLoader().getResourceAsStream(KEY_DER)) {
//
//			byte[] privKeyByteArray = resourceAsStream.readAllBytes();
//			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyByteArray);
//			KeyFactory keyFactory = KeyFactory.getInstance(RSA);
//			return keyFactory.generatePrivate(keySpec);
//
//		} catch (Exception e) {
//			LOGGER.error("DER file not found: {}", e.getMessage());
//			return null;
//		}
//	}

//	public static String decryptPassword(String cryptedPassword) {
//
//		try {
//			//PublicKey publicKey = getKey(PUBLIC_KEY);
//			Cipher cipher = Cipher.getInstance(RSA_DECRYPT);
//			cipher.init(Cipher.DECRYPT_MODE, privateKey);
//			//cipher.init(Cipher.DECRYPT_MODE, publicKey);
//			return new String(cipher.doFinal(Base64.decode(cryptedPassword)), StandardCharsets.UTF_8);
//		} catch (Exception e) {
//			LOGGER.error("Decrypting requested password failed: {}", e.getMessage());
//			return null;
//		}
//	}
//	
//	public static String encryptPassword(String cryptedPassword) {
//		
//		try {
//			PublicKey publicKey = getKey(PUBLIC_KEY);
//			Cipher cipher = Cipher.getInstance(RSA_DECRYPT);
//			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//			byte[] bytes = cipher.doFinal(cryptedPassword.getBytes(StandardCharsets.UTF_8));
//		    return new String(Base64.encode(bytes));
//
//		} catch (Exception e) {
//			LOGGER.error("Encrypting requested password failed: {}", e.getMessage());
//			return null;
//		}
//	}

	public static boolean isValidEmail(String email) {
		try {
			if (email == null || email.isBlank() || email.length() > EMAIL_LENGHT) {
				return false;
			} else {
				String regex = "^[a-z0-9._-]+(@[a-z0-9.-]+\\.[a-z]{2,4})?$";
//        				Email form control regex :
//                				Can contain : uppercase, lowercase, number
//						Must contain no spaces and be no longer than 37 characters
//			    			Top-level domain must be from 2 to 4 characteres
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(email);
				return m.matches();
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public static PublicKey getKey(String key){
	    try{
	        byte[] byteKey = Base64.decode(key.getBytes());
	        X509EncodedKeySpec x509publicKey = new X509EncodedKeySpec(byteKey);
	        KeyFactory kf = KeyFactory.getInstance("RSA");

	        return kf.generatePublic(x509publicKey);
	    }
	    catch(Exception e){
	    	LOGGER.error("Exception when getting key : {}", e.getMessage());
	    }
	    return null;
	}
}

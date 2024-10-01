/**
 * 
 */
package com.carrus.statsca.messages;

/**
 * 
 * Requête d'authentification provenant d'une console.
 * Contient les propriétés nécessaires à l'authentification
 *  
 * @author h.ramananjara
 *
 */
public class SessionRequest {
	/** Identifiant */
	private final String email;
	/** Mot de passe */
	private final String token;
	
	/** Constructeur unique de la classe */
	public SessionRequest(String email, String token) {
		this.email = email;
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public String getToken() {
		return token;
	}

}

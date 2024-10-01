package com.carrus.statsca.dynaautofiller;

import java.io.Serial;

/**
 * Classe d'exception dédiée au moteur de remplissage dynamique et automatique
 * des beans DTO de l'application TAS, à partir des entitées issues de la couche
 * de service de l'application.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (9 mai 2022)
 */
public class DynaautofillerException extends Exception {
	/** Numéro de version pour la sérialisation */
    @Serial
    static final long serialVersionUID = 1L;
    
	public DynaautofillerException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DynaautofillerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DynaautofillerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DynaautofillerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DynaautofillerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}

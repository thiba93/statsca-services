package com.carrus.statsca.admin.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Enumération des différents types que peuvent prendre les paramètres
 * de l'application stockées dans la base de données, table 'Parametre'.
 * 
 * @author BT - ARTSYS 2014
 * @since 3 juillet 2014
 */
public enum ParametreTypeEnum {
	/** Chaine de caractères */
	STRING(String.class),
	/** Nombre entier */
	INTEGER(Integer.class),
	/** Booléen */
	BOOLEAN(Boolean.class),
	/** Date simple, toujours au format YYYYmmDD */
	DATE(Date.class);

	/** Logger de la classe */
	private static final Log LOGGER = LogFactory.getLog(ParametreTypeEnum.class);
	/** Format de date attendu en entrée */
	private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	/** Classe de l'objet correspondant à ce type de paramètre */
	private final Class<?> clazz;
	
	private ParametreTypeEnum(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Méthode de convertion d'un paramètre en valeur textuelle, vers son type
	 * exact, défini par cette énumération.
	 */
	public Object convert(String textValue) {
		if (textValue == null || (textValue.isEmpty() && !this.equals(STRING))) {
			return null;
		} else {
			switch (this) {
			case STRING:
				return textValue;
	
			case INTEGER:
				return Integer.decode(textValue);
	
			case BOOLEAN:
				return Boolean.valueOf(textValue);
	
			case DATE:
				try {
					return dateFormat.parse(textValue);
				} catch (ParseException e) {
					LOGGER.warn("Mauvais format de date pour le paramètre. Utilisez YYYYmmDD " + textValue);
					return null;
				}
			default:
				return textValue;
			}
		}
	}

	/**
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}
}

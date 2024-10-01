package com.carrus.statsca.admin.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entité qui représente un paramètre de l'application. Stocké en base de données.
 * 
 * @author BT - ARTSYS 2014
 * @since 3 juillet 2014
 */
@Entity
@Cacheable
@Table(name="PARAMETRE")
//@NamedQuery(name = "ParametreEntity.retrieveByID", query = "SELECT table FROM ParametreEntity table "
//		+ "WHERE table.id = :key")
@NamedQuery(name = "parametreEntity.retrieveAllParameters", query = "SELECT table FROM ParametreEntity table")
public class ParametreEntity {

	/** Clé d'accès au paramètre, identifiant */
	@Id
	@Column(name="ID_PAR")
	private String id;

	/** Type du paramètre, issu de l'énumération ParametreType */
	@Enumerated(EnumType.STRING)
	@Column(name="TYPE", nullable=false)
	private ParametreTypeEnum type;

	/** Valeur textuelle du paramètre */
	@Column(name="VALUE")
	private String textValue;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public ParametreTypeEnum getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ParametreTypeEnum type) {
		this.type = type;
	}

	/**
	 * @return the textValue
	 */
	public String getTextValue() {
		return textValue;
	}

	/**
	 * @param textValue the textValue to set
	 */
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	/**
	 * Méthode qui permet d'obtenir la valeur du paramètre dans son
	 * type réel.
	 * 
	 * @param retourne la valeur réelle du paramètre
	 */
	public Object getValue() {
		return type.convert(textValue);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Parameter [name=" + id + ", type=" + type.toString() +
				", text value= " + textValue + "]";
	}
}
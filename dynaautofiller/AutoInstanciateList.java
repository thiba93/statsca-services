package com.carrus.statsca.dynaautofiller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cette anotation de niveau méthode, permet de préciser le setter à utiliser pour
 * être construite par instanciation d'un autre type de DTO, à partir de la valeur obtenue
 * par la méthode de l'entité source qui est précisée en valeur de l'annotation.
 * 
 * Si activated = false, alors le moteur AutoFillerEngine ne renseigne pas le setter
 * correspondant, c'est à la charge du programme de le faire. Utilisé dans le cas
 * de contrôles supplémentaires.
 * 
 * La classe à instancier est déterminée par le type de la propriété à remplir.
 *
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (5 mai 2022)
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoInstanciateList {
	/** Activateur du moteur AutoFillerEngine */
	boolean activated() default true;
	/** Getter principal de la source */
	String value();
	/** Chaine de méthodes à appliquer éventuellement après le getter principal */
	String[] chain() default {};
	/** Permet de préciser si le filtrage par profil doit se faire pour cette propriété */
	boolean useProfil() default true;
	/** Permet de préciser si on injecte une liste vide dans la cible, plutot qu'une valeur null au cas ou la source est vide */
	boolean emptyRatherNull() default false;
}

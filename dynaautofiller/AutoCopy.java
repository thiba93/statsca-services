package com.carrus.statsca.dynaautofiller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cette anotation de niveau méthode, permet de déterminer, pour le DTO qui en est décoré
 * la méthode de l'entité source qui permet d'obtenir la valeur à injecter dans le setter
 * à l'initialisation automatiquement.
 *
 * Si activated = false, alors le moteur AutoFillerEngine ne renseigne pas le setter
 * correspondant, c'est à la charge du programme de le faire. Utilisé dans le cas
 * de contrôles supplémentaires.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (5 mai 2022)
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoCopy {
	/** Activateur du moteur AutoFillerEngine */
	boolean activated() default true;
	/** Getter principal de la source */
	String value();
	/** Chaine de méthodes à appliquer éventuellement après le getter principal */
	String[] chain() default {};
}

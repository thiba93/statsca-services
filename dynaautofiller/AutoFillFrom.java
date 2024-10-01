package com.carrus.statsca.dynaautofiller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cette anotation de niveau classe, permet de déterminer, pour le DTO qui en est décoré
 * la classe entité à partir de laquelle le DTO devra s'initialiser automatiquement
 * et dynamiquement. 
 *
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (5 mai 2022)
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoFillFrom {
	/** Classe à partir de laquelle on peut remplir la classe annotée */
	Class<?> value();
	/** Tronçon de chemin TAS correspondant à cette classe */
	String fillerPath();
}

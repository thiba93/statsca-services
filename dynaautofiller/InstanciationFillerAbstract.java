package com.carrus.statsca.dynaautofiller;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Classe de factorisation abstraite des comportements des classes de modélisation
 * des moyens de remplissage automatiques des propriétés annotées sur les DTOs.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (6 mai 2022)
 */
abstract class InstanciationFillerAbstract extends Filler {
	/** No getter value, valeur de la chaine getter dans l'annotation lorsqu'il n'y a pas de getter à utiliser */
	protected static final String NO_GETTER_VALUE = "";

	/** Constructeur du DTO à instancier */
	protected final Constructor<?> dtoConstructor;
	/** Classe éventuelle dans laquelle caster le résultat du getter, avant d'appeler le constructeur du DTO */
	protected final Class<?> caster;
	/** Permet de préciser si l'on doit créer une instance à partir d'une valeur null ou pas */
	protected final boolean forceEmptyCreation;
	/** Indique si le profil doit être utilisé pour filtrer ce filler */
	protected final boolean useProfil;
	
	/** Constructeur par défaut */
	InstanciationFillerAbstract(boolean activated, Constructor<?> dtoConstructor, Method getterMethod, List<Method> chainMethods,
			Class<?> caster, Method targetSetter, boolean forceEmptyCreation, boolean useProfil) {
		super(activated, getterMethod, chainMethods, targetSetter);

		this.dtoConstructor = dtoConstructor;
		this.caster = caster;
		this.forceEmptyCreation = forceEmptyCreation;
		this.useProfil = useProfil;
	}

}

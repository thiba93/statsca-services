package com.carrus.statsca.dynaautofiller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe de factorisation abstraite des comportements des classes de modélisation
 * des moyens de remplissage automatiques des propriétés annotées sur les DTOs.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (8 mai 2022)
 */
abstract class Filler {
	/** Logguer par défaut de la classe */
	protected static final Logger LOGGER = LoggerFactory.getLogger(Filler.class);
	
	/** Est ce que le moteur de remplissage doit se déclencher sur cette annotation ? */
	protected final boolean activated;
	/** Getter de l'objet source */
	protected final Method getterMethod;
	/**
	 * Chaine ordonnée des méthodes éventuelles à appliquer sur le getter de l'objet
	 * source nécessaire au remplissage de la propriété cible
	 * Si il n'y a pas de traitement supplémentaire, cette chaine est vide, mais pas null.
	 */
	protected final List<Method> chainMethods;
	/** Méthode setter sur l'objet cible nécessaire au remplissage de la propriété cible */
	protected final Method targetSetter;
	
	/** Constructeur par défaut */
	Filler(boolean activated, Method getterMethod, List<Method> chainMethods, Method targetSetter) {
		this.activated = activated;
		this.getterMethod = getterMethod;
		this.chainMethods = chainMethods;
		this.targetSetter = targetSetter;
	}

	/**
	 * Getter de l'activation du filler 
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * Méthode d'execution du remplissage de la propriété cible
	 * à l'aide de la méthode portée par l'extension de l'objet.
	 * 
	 * @param source L'objet à partir duquel on remplit la propriété
	 * @param target L'objet pour lequel on remplit la propriété
	 * @param profil Le profil machine à utiliser pour le remplissage
	 * 
	 * @throws DynaautofillerException En cas de problème lors du calcul ou de l'injection de la propriété
	 */
	abstract void process(Object source, Object target) throws DynaautofillerException;
	
	/**
	 * Méthode interne de factorisation du traitement de la source par la chaine
	 * de méthodes
	 * 
	 * @throws DynaautofillerException Si une erreur a lieu au moment du traitement de la source de données
	 */
	protected Object processGetterAndChainSource(Object source) throws DynaautofillerException {
		Object sourceValue = source;
		
		// Extraction de la valeur du getter éventuel
		if (getterMethod != null) {
			try {
				// On commence par rechercher la valeur de la source
				sourceValue = getterMethod.invoke(sourceValue);
			} catch (IllegalAccessException | IllegalArgumentException | NullPointerException e) {
				throw new DynaautofillerException("Echec de l'extraction de la valeur source avec " + getterMethod, e);
			} catch (InvocationTargetException e) {
				throw new DynaautofillerException("L'appel au getter " + getterMethod.getName() + " de l'objet "+ sourceValue +" a déclenché une erreur.", e.getTargetException());
			}

		}
		
		// Calcul de la valeur en bout de chaine de traitement
		if (this.chainMethods != null && !this.chainMethods.isEmpty() && sourceValue != null) {
			for (Method nextMethod : this.chainMethods) {
				try {
					sourceValue = nextMethod.invoke(sourceValue);
				} catch (IllegalAccessException | IllegalArgumentException | NullPointerException e) {
					throw new DynaautofillerException("Echec du calcul de la chaine de transformation pour la méthode " +
							nextMethod.getName() + " à partir de la valeur " + sourceValue, e);
				} catch (InvocationTargetException e) {
					throw new DynaautofillerException("L'appel de la méthode de chaine " + nextMethod.getName() +
							" à partir de l'objet " + sourceValue + " a déclenché une erreur.", e.getTargetException());
				}
			}
		}
		
		return sourceValue;
	}
	
	/**
	 * Méthode interne qui permet de rechercher la méthode correspondante au getter
	 * à partir d'une classe source,et de son nom.
	 * 
	 * @param sourceClass Classe de la source de l'information
	 * @param methodName Noms de la méthode getter sur la classe source
	 * 
	 * @return La méthode correspondante au getter à appliquer à un objet de la classe source
	 * 
	 * @throws DynaautofillerException En cas de problème lors du scan des structures pour déterminer le filler
	 */
	static protected Method findGetterMethod(Class<?> sourceClass, String methodName)
			throws DynaautofillerException {
		try {
			return sourceClass.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			// Absence de la méthode getter
			throw new DynaautofillerException("Méthode " + methodName + " absente de " + sourceClass);
		}
	}
	
	/**
	 * Méthode interne de traitement d'une chaine de méthodes pour retourner toutes les méthodes
	 * successives.
	 * 
	 * @param sourceClass Classe de la source de l'information
	 * @param methodNames Tableau des noms de méthodes successives à appliquer à partir de la classe source
	 * 
	 * @return Le tableau successif des méthodes à appliquer à un objet de la classe source pour aboutir à la cible
	 * 
	 * @throws DynaautofillerException En cas de problème lors du scan des structures pour déterminer le filler
	 */
	static protected List<Method> findMethodChain(Class<?> sourceClass, String[] methodNames)
			throws DynaautofillerException {
		Class<?> chainClass = sourceClass;
		List<Method> chain = new ArrayList<>();

		// On recherche ensuite les méthodes à chainer
		for (String nextMethodName: methodNames) {
			try {
				Method nextMethod = chainClass.getMethod(nextMethodName);
				chainClass = nextMethod.getReturnType();
				chain.add(nextMethod);
			} catch (NoSuchMethodException e) {
				// Absence de la méthode intermédiaire
				throw new DynaautofillerException("Méthode " + nextMethodName + " absente de " + chainClass);
			}
		}

		return chain;
	}
	
}

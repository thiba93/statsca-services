package com.carrus.statsca.dynaautofiller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.lang.model.type.NullType;

/**
 * Classe de modélisation du remplissage d'une propriété automatique par instanciation
 * d'un nouveau DTO à partir du retour d'une autre méthode sur l'objet d'une autre classe.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (6 mai 2022)
 */
class InstanciationFiller extends InstanciationFillerAbstract {
	
	/** Constructeur par défaut */
	InstanciationFiller(boolean activated, Constructor<?> dtoConstructor, Method getterMethod, List<Method> chainMethods,
			Class<?> caster, Method targetSetter, boolean forceEmptyCreation, boolean useProfil) {
		super(activated, dtoConstructor, getterMethod, chainMethods, caster, targetSetter, forceEmptyCreation, useProfil);
	}
	
	@Override
	void process(Object source, Object target) throws DynaautofillerException {
		Object sourceValue = processGetterAndChainSource(source);
		// Si un caster est défini, on l'applique
		if (caster != null) {
			sourceValue = caster.cast(sourceValue);
		}
		
		// On construit le DTO que si la valeur n'est pas nulle
		if (sourceValue != null) {
			Object newDto = null;
			
			try {
				newDto = dtoConstructor.newInstance(sourceValue);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new DynaautofillerException("Echec de l'instanciation de " + dtoConstructor +
						" avec la valeur source " + sourceValue, e);
			} catch (InvocationTargetException e) {
				throw new DynaautofillerException("Le constructeur " + dtoConstructor +
						" avec la valeur source " + sourceValue + " & émis l'exception : ", e.getTargetException());
			}
			
			try {
				// Que l'on affecte au setter de la propriété cible
				targetSetter.invoke(target, newDto);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new DynaautofillerException("Echec du remplissage de " + targetSetter + " par la valeur " + newDto +
						" avec le DTO instancié par " + dtoConstructor);
			}

		} else if (forceEmptyCreation) {
			// TODO créer une instance vide
		} else {
			// Sinon on n'initialise pas la propriété
		}
	}

	/**
	 * Méthode de création d'un InstanciationFiller à partir de l'annotation relevée
	 * sur une propriété de l'objet à traiter, et de cet objet.
	 * Retourne une nouvelle instance de ChainFiller.
	 * 
	 * @param setter Méthode setter sur l'objet cible qui est décorée de l'annotation
	 * @param annotation de type AutoInstanciate
	 * @param sourceClass Classe de l'objet source des données concerné par l'annotation
	 * 
	 * @return nouvelle instance de InstanciationFiller pour traiter cette propriété de l'objet
	 * 
	 * @throws DynaautofillerException En cas de problème lors du scan des structures pour déterminer le filler
	 */
	static InstanciationFiller newInstance(Method setter, AutoInstanciate annotation, Class<?> sourceClass)
			throws DynaautofillerException {
		boolean activated = annotation.activated();
		Class<?> toInstanciate = setter.getParameterTypes()[0];
		String getterName = annotation.value();
		Method getter = (getterName.equals(NO_GETTER_VALUE) ? null : findGetterMethod(sourceClass, getterName));
		if (!getterName.equals(NO_GETTER_VALUE)) getter = findGetterMethod(sourceClass, getterName);
		String[] chainNames = annotation.chain();
		boolean forceEmptyCreation = annotation.forceEmptyCreation();
		boolean useProfil = annotation.useProfil();
		
		// Recherche de la classe à partir de laquelle on réalise l'instantiation
		Class<?> classFromInstanciate = (getter == null ? sourceClass : getter.getReturnType());
		// Si il n'y a pas de getter, on ignore l'éventuelle chaine de méthode
		List<Method> chain = (getter == null ? Collections.emptyList() : findMethodChain(getter.getReturnType(), chainNames));
		if (!chain.isEmpty()) {
			classFromInstanciate = chain.get(chain.size() - 1).getReturnType();
		}
		// Recherche de la classe pour caster le résultat du getter, si elle n'est pas null
		Class<?> caster = annotation.caster().equals(NullType.class) ? null : annotation.caster();
		if (caster != null) {
			if (caster.isAssignableFrom(classFromInstanciate)) {
				classFromInstanciate = caster;
			} else {
				throw new DynaautofillerException("La classe " + classFromInstanciate.getName() +
						" ne peut pas être casté en " + caster.getName());
			}
		}
	
		// Recherche du contructeur de la classe à instancier
		try {
			Constructor<?> constructor;
			constructor = toInstanciate.getConstructor(classFromInstanciate);
			
	
			return new InstanciationFiller(activated, constructor, getter, chain, caster, setter, forceEmptyCreation, useProfil);
			
		} catch (NoSuchMethodException e) {
			// Absence du constructeur adéquate
			throw new DynaautofillerException("Constructeur " + toInstanciate.getName() + "(" +
					classFromInstanciate.getName() +  ") absent");
		}
	}

}

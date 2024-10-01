package com.carrus.statsca.dynaautofiller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Classe de modélisation du remplissage d'une propriété automatique grâce
 * au retour d'une autre méthode sur l'objet d'une autre classe.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (6 mai 2022)
 */
class ChainFiller extends Filler {
	/** Constructeur par défaut */
	ChainFiller(boolean activated, Method getterMethod, List<Method> chainMethods, Method targetSetter) {
		super(activated, getterMethod, chainMethods, targetSetter);
	}
	
	@Override
	void process(Object source, Object target) throws DynaautofillerException {
		try {
			// Calcul de la chaine complète de méthodes de la source
			Object sourceValue = processGetterAndChainSource(source);
			
			// Et on affecte la valeur en bout de chaine au setter de la cible
			targetSetter.invoke(target, sourceValue);
		} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			throw new DynaautofillerException("Echec du remplissage de " + targetSetter);
		}
	}

	/**
	 * Méthode de création d'un ChainFiller à partir de l'annotation relevée
	 * sur une propriété de l'objet à traiter, et de cet objet.
	 * Retourne une nouvelle instance de ChainFiller.
	 * 
	 * @param setter Méthode setter sur l'objet cible qui est décorée de l'annotation
	 * @param annotation de type AutoCopy
	 * @param sourceClass Classe de l'objet source des données concerné par l'annotation
	 * 
	 * @return nouvelle instance de ChainFiller pour traiter cette propriété de l'objet
	 * 
	 * @throws DynaautofillerException En cas de problème lors du scan des structures pour déterminer le filler
	 */
	static ChainFiller newInstance(Method setter, AutoCopy annotation, Class<?> sourceClass) throws DynaautofillerException {
		boolean activated = annotation.activated();
		String getterName = annotation.value();
		Method getter = findGetterMethod(sourceClass, getterName);
		String[] chainNames = annotation.chain();
		List<Method> chain = findMethodChain(getter.getReturnType(), chainNames);
		
		// On créé l'instance et on la retourne
		return new ChainFiller(activated, getter, chain, setter);
	}
	
}

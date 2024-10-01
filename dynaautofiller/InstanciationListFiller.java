package com.carrus.statsca.dynaautofiller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Classe de modélisation du remplissage d'une propriété automatique par instanciation
 * d'un nouveau DTO à partir du retour d'une autre méthode sur l'objet d'une autre classe.
 * La valeur attendue dans la méthode cible doit être une liste d'objets instanciés à partir
 * de la liste des valeurs de la méthode source.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (6 mai 2022)
 */
class InstanciationListFiller extends InstanciationFillerAbstract {
	/** Forcer une liste vide si la source est vide */
	private boolean forceEmptyList;
	
	/**
	 * Constructeur par défaut
	 * 
	 * 
	 */
	InstanciationListFiller(boolean activated, Constructor<?> dtoConstructor, Method listGetter, List<Method> chainMethods, Method targetSetter, boolean useProfil, boolean forceEmptyList) {
		super(activated, dtoConstructor, listGetter, chainMethods, null, targetSetter, false, useProfil);
		this.forceEmptyList = forceEmptyList;
	}
	
	/**
	 * Méthode interne de factorisation du traitement de la source par la chaine
	 * de méthodes à partir de l'éventuelle seconde méthode.
	 * Permet d'obtenir les valeurs correspondantes à chacun des éléments de la
	 * liste retournée par la première méthode sur la source
	 * 
	 */
	private Object processChainSource(Object source) {
		Object sourceValue = source;
		try {
			for (Method nextMethod : this.chainMethods) {
				if (sourceValue != null) {
					sourceValue = nextMethod.invoke(sourceValue);
				} else {
					// Inutile d'exécuter le reste de la chaine de méthodes
					break;
				}
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.warn("Echec du remplissage de " + targetSetter);
		}
		
		return sourceValue;
	}

	@Override
	void process(Object source, Object target) throws DynaautofillerException {
		// La première méthode sur la source donne une liste d'objets
		List<?> sourceList = null;
		try {
			Method firstMethod = this.getterMethod;
			sourceList = (List<?>) firstMethod.invoke(source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new DynaautofillerException("Echec du remplissage de " + targetSetter);
		}

		try {
			if (sourceList != null && !sourceList.isEmpty()) {
				List<? super Object> results = new ArrayList<>(sourceList.size());
				for (Object sourceValue: sourceList) {
					try {
						Object result = processChainSource(sourceValue);
						// On construit le DTO que si la valeur n'est pas nulle
						if (result != null) {
							results.add(dtoConstructor.newInstance(result));
						}
						
					} catch (InstantiationException e) {
						throw new DynaautofillerException("Echec de l'instanciation de " + dtoConstructor + " pour la valeur " + sourceValue, e);
					}
				}
				// On affecte la liste des instanciations au setter cible
				targetSetter.invoke(target, results);
				
			} else if (forceEmptyList) {
				targetSetter.invoke(target, Collections.emptyList());
			} // Sinon on n'initialise pas la propriété
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new DynaautofillerException("Echec du remplissage de " + targetSetter);
		}
	}

	/**
	 * Méthode de création d'un InstanciationListFiller à partir de l'annotation relevée
	 * sur une propriété de l'objet à traiter, et de cet objet.
	 * Retourne une nouvelle instance de ChainFiller.
	 * 
	 * @param setter Méthode setter sur l'objet cible qui est décorée de l'annotation
	 * @param annotation de type AutoInstanciateList
	 * @param sourceClass Classe de l'objet source des données concerné par l'annotation
	 * 
	 * @return nouvelle instance de InstanciationListFiller pour traiter cette propriété de l'objet
	 * 
	 * @throws DynaautofillerException En cas de problème lors du scan des structures pour déterminer le filler
	 */
	static InstanciationListFiller newInstance(Method setter, AutoInstanciateList annotation, Class<?> sourceClass)
			throws DynaautofillerException {
		boolean activated = annotation.activated();
		Type listToInstanciateType = setter.getGenericParameterTypes()[0];
		String getterName = annotation.value();
		Method getter = findGetterMethod(sourceClass, getterName);
		String[] chainNames = annotation.chain();
		boolean useProfil = annotation.useProfil();
		boolean forceEmptyList = annotation.emptyRatherNull();

		// Vérification qu'il s'agit bien d'une liste, et recherche de la classe nichée dans la liste
		Class<?> classToInstanciate = null;
		if (listToInstanciateType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) listToInstanciateType).getRawType();
			if ((rawType instanceof Class<?> && List.class.isAssignableFrom((Class<?>) rawType)) || 
					(rawType instanceof Class<?> && Collection.class.isAssignableFrom((Class<?>) rawType))) {
				ParameterizedType listToInstanciatePType = (ParameterizedType) listToInstanciateType;
				if (listToInstanciatePType.getActualTypeArguments().length == 1) {
					if (listToInstanciatePType.getActualTypeArguments()[0] instanceof Class<?>) {
						classToInstanciate = (Class<?>) listToInstanciatePType.getActualTypeArguments()[0];
					} else {
						throw new DynaautofillerException("La classe " + listToInstanciatePType.getActualTypeArguments()[0].getTypeName()
								+ " de la liste du setter "  + setter.getDeclaringClass().getName() + "/" + setter.getName()
								+ " n'est pas une classe instanciable");
					}
				} else {
					throw new DynaautofillerException("Absence de type générique pour le setter de liste " + setter.getDeclaringClass().getName() + "/" + setter.getName());
				}
			} else {
				throw new DynaautofillerException("Problème sur le setter " + setter.getDeclaringClass().getName() + "/" + setter.getName() + " n'admet pas de liste en paramètre");
			}
		} else {
			throw new DynaautofillerException("Le setter " + setter.getDeclaringClass().getName() + "/" + setter.getName() + " n'admet pas de liste en paramètre");
		}
		
		// Recherche de la classe à partir de laquelle on réalise l'instantiation
		Type listFromInstanciateType = getter.getGenericReturnType();
		Class<?> classFromInstanciate = null;
		if (listFromInstanciateType instanceof ParameterizedType) {
			ParameterizedType listFromInstanciatePType = (ParameterizedType) listFromInstanciateType;
			Type listRawType = listFromInstanciatePType.getRawType();
			if ((listRawType instanceof Class<?> && List.class.isAssignableFrom((Class<?>) listRawType)) ||
					(listRawType instanceof Class<?> && Collection.class.isAssignableFrom((Class<?>) listRawType))) {
				if (LOGGER.isDebugEnabled()) LOGGER.debug("Le retour de la chaine de méthode est une liste.");
				if (listFromInstanciatePType.getActualTypeArguments().length == 1) {
					if (LOGGER.isDebugEnabled()) LOGGER.debug("avec un seul type paramétré");
					if (listFromInstanciatePType.getActualTypeArguments()[0] instanceof Class<?>) {
						classFromInstanciate = (Class<?>) listFromInstanciatePType.getActualTypeArguments()[0];
						if (LOGGER.isDebugEnabled()) LOGGER.debug("Qui est instanciable en " + classToInstanciate.getName());
					} else {
						throw new DynaautofillerException("La classe " + listFromInstanciatePType.getActualTypeArguments()[0].getTypeName()
								+ " de la liste du getter "  + sourceClass.getName() + "/" + setter.getName()
								+ " n'est pas une classe instanciable");
					}
				} else {
					throw new DynaautofillerException("Absence de type générique pour le setter de liste " + sourceClass.getName() + "/" + setter.getName());
				}
				
				// Recherche du contructeur de la classe à instancier
				try {
					// On recherche le type final du getter, après traitement par la chaine de méthodes éventuelle
					List<Method> chain = findMethodChain(classFromInstanciate, chainNames);
					if (!chain.isEmpty()) classFromInstanciate = chain.get(chain.size() - 1).getReturnType();
					
					Constructor<?> constructor;
					constructor = classToInstanciate.getConstructor(classFromInstanciate);
					

					return new InstanciationListFiller(activated, constructor, getter, chain, setter, useProfil, forceEmptyList);
					
				} catch (NoSuchMethodException e) {
					// Absence du constructeur adéquate dans la classe qui compose la liste du setter
					throw new DynaautofillerException("Constructeur " + classToInstanciate.getName() + "(" +
							classFromInstanciate.getName()  + ") absent");
				}

			} else {
				throw new DynaautofillerException(classFromInstanciate + " n'est pas de type liste générique pour le setter de liste " +
						sourceClass.getName() + "/" + setter.getName());
			}
		} else {
			throw new DynaautofillerException("La méthode " + sourceClass.getName() + "/" + setter.getName() +
					" n'attends pas une liste générique.");
		}
	}

}

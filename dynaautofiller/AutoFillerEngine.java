package com.carrus.statsca.dynaautofiller;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cette classe contient l'ensemble des fonctions utilitaires qui permettent de mettre
 * en place la mécanique d'auto-remplissage dynamique des DTOs de la racecard.
 * 
 * @author BT - ARTSYS 2022
 * @since 1.0.0 (5 mai 2022)
 */
public final class AutoFillerEngine {
	/** Logguer par défaut de la classe */
	private static final Logger LOGGER = LoggerFactory.getLogger(AutoFillerEngine.class);
	/** Stockage des paramètres de chaque classe AutoFilled */
	/** Tableau propriété Field / Méthode */
	private static final Map<Class<?>, Map<Method, ? super Filler>> FILLABLE_PROPERTIES = new HashMap<>();
	/** Préfix standard d'une méthode Setter Javabean */
	private static final String SETTER_PREFIX = "set";

	/** On empêche l'instanciation de cette classe qui est statique */
	private AutoFillerEngine() {}
	
	/**
	 * Cette méthode permet de parcourir une classe DTO et de lire les annotations
	 * d'auto-remplissage afin de constituer les méta-paramètres qui permettront
	 * dans une seconde étape de remplir le DTO.
	 * 
	 * @param currentClass
	 * 
	 * @return Les méta-paramètres d'autoremplissage de la classe
	 */
	static private Map<Method, ? super Filler> initialiseAutoFill(Class<?> currentClass) {
		Map<Method, ? super Filler> result = new HashMap<>();
		
		// On lit tout d'abord la classe source pour l'ensemble du DTO
		AutoFillFrom autoFillFrom = currentClass.getAnnotation(AutoFillFrom.class);
		
		if (autoFillFrom != null) {
			// Classe source des informations
			Class<?> sourceClass = autoFillFrom.value();

			// On parcours alors les méthodes dotées d'annotation d'autofill
			for (Method setter: currentClass.getMethods()) {
				try {
					// On scrute les différentes annotations possibles
					if (setter.getAnnotation(AutoCopy.class) != null) {
						result.put(setter, ChainFiller.newInstance(setter, setter.getAnnotation(AutoCopy.class), sourceClass));
	
					} else if (setter.getAnnotation(AutoInstanciate.class) != null) {
						result.put(setter, InstanciationFiller.newInstance(setter, setter.getAnnotation(AutoInstanciate.class), sourceClass));
	
					} else if (setter.getAnnotation(AutoInstanciateList.class) != null) {
						result.put(setter, InstanciationListFiller.newInstance(setter, setter.getAnnotation(AutoInstanciateList.class), sourceClass));
					}
				} catch (DynaautofillerException e) {
					LOGGER.warn(e.getMessage());
				}
			}
			
			return result;
			
		} else {
			// La classe n'est pas auto-remplissable dynamiquement
			LOGGER.warn("La classe " + currentClass + " n'est pas auto-remplissable, elle sera ignorée.");
			return null;
		}
	}

	/**
	 * Cette méthode utilitaire permet de renseigner dynamiquement et automatiquement
	 * une classe DTO prévue pour l'autoremplissage.
	 * Elle permet d'initialiser la recherche des méta-paramètres de la classe cible
	 * si celle-ci n'a pas encore été scannée. Sinon elle utilise directement des paramètres.
	 * 
	 * @param target - l'objet cible à remplir
	 * @param source - L'objet source qui permet de renseigner la cible
	 * @param profil - Le profil de filtre à appliquer lors de l'opération d'autofill
	 */
	static public void autoFill(Object target, Object source) {
		Map<Method, ? super Filler> metaProp = null;
		
		// On récupère les méta-paramètres autofill de la classe
		if (FILLABLE_PROPERTIES.containsKey(target.getClass())) {
			/// Méta-propriétés de la clase
			metaProp = FILLABLE_PROPERTIES.get(target.getClass());
			if (metaProp == null) {
				// La classe n'est pas autofilled, on ne fait rien
				LOGGER.warn("Le DTO " + target.getClass().getSimpleName() + " n'est pas auto-remplissable.");
			}
		} else {
			// La classe cible n'a pas encore été scannée
			metaProp = AutoFillerEngine.initialiseAutoFill(target.getClass());
			FILLABLE_PROPERTIES.put(target.getClass(), metaProp);
		}
		
		if (metaProp != null) {
			// On appelle la méthode d'autoFill
			processAutoFill(target, source, metaProp);
		}
	}

	/**
	 * Cette méthode utilitaire privée permet de renseigner dynamiquement et automatiquement
	 * une classe DTO prévue pour l'autoremplissage, à partir de tous les paramètres nécessaires.
	 * 
	 * @param target - l'objet cible à remplir
	 * @param source - L'objet source qui permet de renseigner la cible
	 * @param profil - Le profil de filtre à appliquer lors de l'opération d'autofill
	 */
	static private void processAutoFill(Object target, Object source, Map<Method, ? super Filler> params) {
		
			// On renseigne tous les champs autorisés dans le profil
			for (Entry<Method, ? super Filler> entry : params.entrySet()) {
				// On déclenche l'autoFill uniquement si l'autoremplissage de la propriété est activée
				if (((Filler) entry.getValue()).isActivated()) {
					try {
						((Filler) entry.getValue()).process(source, target);
					} catch (DynaautofillerException e) {
						LOGGER.warn(e.getMessage() );
					}
				}
				// Sinon la propriété n'est pas auto-fillable
			}
	}

	/**
	 * Recherche du nom de la propriété JavaBeans correspondante au setter passé en paramètre
	 * Si la méthode ne correspond pas à un setter, retourne null.
	 * 
	 * @param setter Method java correpondante à un setter
	 * 
	 * @return le nom logique de la propriété Javabean correspondante au setter
	 */
	static private String getSetterPropertyName(Method setter) {
		if (setter != null) {
			if (setter.getName().startsWith(SETTER_PREFIX)) {
				return Introspector.decapitalize(setter.getName().substring(SETTER_PREFIX.length()));
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}

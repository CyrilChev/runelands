package com.cyg.tools.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;

/**
 * =================================================================================================================
 * Helper pour la gestion des génériques
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class GenericsHelper {

    // Constantes publiques
    public static final String                                      ERR_GENERICS_INDEX_MISMATCH = "La classe ne contient que {0} arguments génériques : l''index demandé {1} n''existe pas";

    // Membres statiques internes
    private static final GenericsHelper                             instance = new GenericsHelper();

    /**
     * Constructeur privé
     */
    private GenericsHelper(){
    }

    // ------------ Méthodes statiques publiques ---------------
    /**
     * Retourne la seule instance de cette classe
     * @return GenericsHelper
     * @since 0.0.1
     */
    public static GenericsHelper getInstance() {
        return instance;
    }

    /**
     * Permet de retourner la classe d'un type générique pour une classe donnée
     * @param clazz Classe à traiter
     * @param argIndex Index de l'argument (basé sur 0)
     * @return Class
     * @since 0.0.1
     */
    public static <T> Class<T> getGenericArgumentClass(Class<?> clazz,int argIndex) {
        return getInstance().getGenericArgumentForClass(clazz, argIndex);
    }

    /**
     * Effectue une recherche d'argument générique pour l'interface passée en paramètre
     * @param clazz Classe à traiter
     * @param argIndex Index de l'argument
     * @return Class
     * @since 0.0.1
     */
    public static <T> Class<T> getGenericArgumentInterface(Class<?> clazz, int argIndex) {
        return getInstance().searchGenericArgumentForInterface(clazz, argIndex);
    }

    // --------------------------------------- Méthodes publiques ---------------------------------------------
    /**
     * Effectue une recherche d'argument générique pour la classe passée en paramètre
     * @param clazz Classe à traiter
     * @param argIndex Index de l'argument (basé sur 0)
     * @return Class
     * @since 0.0.1
     */
    public <T> Class<T> getGenericArgumentForClass(Class<?> clazz, int argIndex) {
        return this.searchGenericArgumentForClass(clazz, argIndex);
    }

    /**
     * Effectue une recherche d'argument générique pour l'interface passée en paramètre
     * @param clazz Classe à traiter
     * @param argIndex Index de l'argument
     * @return Class
     * @since 0.0.1
     */
    public <T> Class<T> getGenericArgumentForInterface(Class<?> clazz, int argIndex) {
        return this.searchGenericArgumentForInterface(clazz, argIndex);
    }

    // --------------------------------------- Méthodes privées ---------------------------------------------
    /**
     * Effectue une recherche d'argument générique sur une classe
     * @param clazz Classe
     * @param argIndex Index (basé sur 0) de l'argument générique
     * @return Class
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    private <T> Class<T> searchGenericArgumentForClass(Class<?> clazz, int argIndex) {
        Class<T> result = null;
        Type supertype = clazz.getGenericSuperclass();
        if (supertype instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType)supertype).getActualTypeArguments();
            if (types.length <= argIndex) {
                throw new RuntimeException(MessageFormat.format(ERR_GENERICS_INDEX_MISMATCH, types.length, argIndex));
            }
            result = (Class<T>)types[argIndex];
        } else if (clazz.getSuperclass()!=null) {
            result = searchGenericArgumentForClass(clazz.getSuperclass(), argIndex);
        }
        return result;
    }

    /**
     * Recherche un argument générique pour une interface
     * @param clazz Classe de l'interface sur laquelle on fait la recherche
     * @param argIndex Index de l'argument
     * @return Class
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    private <T> Class<T> searchGenericArgumentForInterface(Class<?> clazz, int argIndex) {
        Class<T> result = null;
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces!=null && genericInterfaces.length > 0) {
            for (Type supertype : genericInterfaces) {
                if (supertype instanceof ParameterizedType) {
                    Type[] types = ((ParameterizedType)supertype).getActualTypeArguments();
                    if (types.length <= argIndex) {
                        throw new RuntimeException(MessageFormat.format(ERR_GENERICS_INDEX_MISMATCH, types.length, argIndex));
                    }
                    result = (Class<T>)types[argIndex];
                } else if (clazz.getSuperclass()!=null) {
                    result = searchGenericArgumentForInterface(clazz.getSuperclass(), argIndex);
                }
                if (result!=null) {
                    break;
                }
            }
        }
        return result;
    }


}

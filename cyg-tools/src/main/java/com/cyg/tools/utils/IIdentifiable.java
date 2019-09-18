package com.cyg.tools.utils;

import java.io.Serializable;

/**
 * =================================================================================================================
 * Interface représentant un objet identifiable
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public interface IIdentifiable<I extends Comparable<I> & Serializable> {

    /**
     * Retourne l'identifiant
     * @return
     * @since 0.0.1
     */
    public I getId();

    /**
     * Positionne l'identifiant
     * @param id
     * @since 0.0.1
     */
    public void setId(I id);
}

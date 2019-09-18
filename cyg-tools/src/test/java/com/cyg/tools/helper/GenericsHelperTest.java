package com.cyg.tools.helper;

import org.junit.Test;

import java.text.MessageFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * =================================================================================================================
 * Tests unitaires sur la classe GenericsHelper
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class GenericsHelperTest {

    // ------------------------------------------- Tests -------------------------------------------
    /**
     * Teste si la fonction retourne bien la valeur attendue
     * @since 0.0.1
     */
    @Test
    public void whenValidGenerics_shouldReturnGenericClass(){
        Class<?> result = GenericsHelper.getGenericArgumentClass(ImplClass.class, 0);
        assertThat(result).isEqualTo(String.class);
        result = GenericsHelper.getGenericArgumentClass(ImplClass.class, 1);
        assertThat(result).isEqualTo(Long.class);
    }

    /**
     * Teste si la fonction retourne bien null lorsque l'index est incorrect
     * @since 0.0.1
     */
    @Test
    public void whenGenericsInvalidIndex_shouldReturnNull(){
        Throwable throwable = catchThrowable(() -> GenericsHelper.getGenericArgumentClass(ImplClass.class, 2));
        assertThat(throwable).isInstanceOf(RuntimeException.class);
        assertThat(throwable).hasMessageContaining(MessageFormat.format(GenericsHelper.ERR_GENERICS_INDEX_MISMATCH, 2, 2));
    }

    /**
     * Teste si la fonction retourne bien null lorsque l'objet n'a pas d'information de généricité
     * @since 0.0.1
     */
    @Test
    public void whenGenericsInvalidGeneric_shouldReturnNull(){
        Class<?> result = GenericsHelper.getGenericArgumentClass(GenericClass.class, 0);
        assertThat(result).isNull();
    }

    /**
     * Teste si la fonction retourne bien la valeur attendue
     * @since 0.0.1
     */
    @Test
    public void whenValidGenericsInterface_shouldReturnGenericClass(){
        Class<?> result = GenericsHelper.getGenericArgumentInterface(ConcreteInterface.class, 0);
        assertThat(result).isEqualTo(String.class);
        result = GenericsHelper.getGenericArgumentInterface(ConcreteInterface.class, 1);
        assertThat(result).isEqualTo(Long.class);
    }

    // ---------------------- Classes internes ----------------

    /**
     * Classe générique de base de test
     * @author ccr
     * @since 0.0.1
     * @param <T>
     */
    private static class GenericClass<T,E> {

    }

    /**
     * Implémentation de la classe générique
     * @author ccr
     * @since 0.0.1
     */
    private static class ImplClass extends GenericClass<String,Long> {

    }

    /**
     *
     * <p>
     *              Interface générique de test
     * </p>
     *
     * @author ccr
     * @since 0.0.1
     */
    private static interface GenericInterface<T,E> {

    }

    /**
     *
     * <p>
     *              Interface concrète
     * </p>
     *
     * @author ccr
     * @since 0.0.1
     */
    private static interface ConcreteInterface extends GenericInterface<String,Long> {

    }
}

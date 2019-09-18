package com.cyg.tools.helper;

import com.cyg.tools.test.beans.MyBean;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * =================================================================================================================
 * Tests unitaires sur BeansHelper
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class BeansHelperTest {

    // Constantes internes
    private static final MyBean BEANS[] = MyBean.createBeans().toJavaArray(MyBean.class);

    private static final MyBean                     ORDERED_BY_NOM[] = new MyBean[]{
            BEANS[2],BEANS[5],BEANS[3],BEANS[4],BEANS[0],BEANS[1]
    };
    private static final MyBean                     ORDERED_BY_NOM_DSC[] = new MyBean[]{
            BEANS[0],BEANS[1],BEANS[4],BEANS[3],BEANS[5],BEANS[2]
    };
    private static final MyBean                     ORDERED_BY_NOM_PRENOM[] = new MyBean[]{
            BEANS[2],BEANS[5],BEANS[3],BEANS[4],BEANS[1],BEANS[0]
    };
    private static final MyBean                     ORDERED_BY_NOM_ASC_DATE_DSC[] = new MyBean[]{
            BEANS[2],BEANS[5],BEANS[3],BEANS[4],BEANS[1],BEANS[0]
    };
    private static final MyBean                     ORDERED_BY_NB_ENFANTS[] = new MyBean[]{
            BEANS[1],BEANS[3],BEANS[4],BEANS[0],BEANS[5],BEANS[2]
    };

    // ------------------------------------------ Tests ----------------------------------------------
    /**
     * Teste un comparateur ascendant sur un seul champ
     * @since 0.0.1
     */
    @Test
    public void whenSingleSortAscInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "nom"));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM);
    }

    /**
     * Teste un comparateur descendant sur un seul champ
     * @since 0.0.1
     */
    @Test
    public void whenSingleSortDscInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class,false, "nom"));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM_DSC);
    }

    /**
     * Teste un comparateur ascendant sur plusieurs champs
     * @since 0.0.1
     */
    @Test
    public void whenMultiSortAscInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "nom", "prenom"));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM_PRENOM);
    }

    /**
     * Teste un comparateur ascendant/descendant sur plusieurs champs
     * @since 0.0.1
     */
    @Test
    public void whenMultiSortAscDscInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, Tuple.of(true, "nom"), Tuple.of(false, "dateNaissance")));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM_ASC_DATE_DSC);
    }

    /**
     * Teste un comparateur avec un champ dont le nom n'existe pas
     * @since 0.0.1
     */
    @Test
    public void whenInvalidSortInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "nom", "toto","prenom"));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM_PRENOM);
        sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "nom", null,"prenom"));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM_PRENOM);
    }

    /**
     * Teste un comparateur sans aucune information
     * @since 0.0.1
     */
    @Test
    public void whenEmptySortInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "toto", null));
        assertThat(sorted).containsExactly(BEANS);
    }

    /**
     * Teste un comparateur avec un type non comparable
     * @since 0.0.1
     */
    @Test
    public void whenIncomparableSortInfo_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "nom", "incomparable"));
        assertThat(sorted).containsExactly(ORDERED_BY_NOM);
    }

    /**
     * Teste un comparateur avec un type non comparable
     * @since 0.0.1
     */
    @Test
    public void whenSortOnNullData_thenShouldReturnValidComparator(){
        Seq<MyBean> sorted = List.of(BEANS).sorted(BeansHelper.getComparator(MyBean.class, "nombreEnfants","id"));
        assertThat(sorted).containsExactly(ORDERED_BY_NB_ENFANTS);
    }
}

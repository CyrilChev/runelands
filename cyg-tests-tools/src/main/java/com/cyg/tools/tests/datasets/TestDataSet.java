package com.cyg.tools.tests.datasets;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * =================================================================================================================
 * Bean représentant un jeu de données
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode
@Builder
@ToString
@SuppressWarnings("rawtypes")
public class TestDataSet {

    // Membres internes
    private String name;
    private Class dataClass;
    private List data;

}

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
 * Bean représentant une collection de jeux de données
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
public class TestDataSetCollection {

    // Membres internes
    private Class<?> dataClass;
    private List<TestDataSet> sets;

}

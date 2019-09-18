package com.cyg.tools.tests.test.model;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * =================================================================================================================
 * Exemple de modèle : produit
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode(of= {"id"})
@Builder(toBuilder=true)
@ToString
public class Product {

    // Membres internes
    private Long id;
    private String name;
    private List<Price> prices;

}

package com.cyg.tools.tests.test.model;

import com.cyg.tools.utils.IIdentifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * =================================================================================================================
 * Exemple de modèle : adresse
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
public class Adresse implements IIdentifiable<Long> {

    // Membres internes
    private Long id;
    private String town;
    private String cp;
}

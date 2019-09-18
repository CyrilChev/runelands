package com.cyg.tools.tests.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * =================================================================================================================
 * Exemple de modèle : ligne de commande
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
@ToString(exclude= {"command"})
public class CommandLine {

    // Membres internes
    private Long id;
    private int quantity;
    private Product product;
    private Command command;
}

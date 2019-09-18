package com.cyg.tools.test.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =================================================================================================================
 * Objet de test incomparable
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class UncomparableData {
    // Membres internes
    private Long                    id;
}

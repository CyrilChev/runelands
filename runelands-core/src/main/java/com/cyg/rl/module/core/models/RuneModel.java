package com.cyg.rl.module.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =================================================================================================================
 * Classe représentant un modèle de Rune
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
public class RuneModel {
    // Membres internes
    private Long id;
}

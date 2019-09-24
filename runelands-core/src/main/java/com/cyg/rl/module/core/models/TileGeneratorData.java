package com.cyg.rl.module.core.models;

import com.cyg.rl.module.core.types.TileGeneratorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =================================================================================================================
 * Modèle de base pour les données relatives au générateur d'entrée de tuile
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder(toBuilder = true)
public class TileGeneratorData {
    // Membres internes
    private TileGeneratorType           type;
}

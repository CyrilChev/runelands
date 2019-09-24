package com.cyg.rl.module.core.models;

import com.cyg.rl.module.core.types.TileColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =================================================================================================================
 * Modèle pour une tuile
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Tile {
    // Membres internes
    private TileColor color;
}

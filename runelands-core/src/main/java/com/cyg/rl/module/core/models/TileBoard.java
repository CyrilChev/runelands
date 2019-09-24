package com.cyg.rl.module.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =================================================================================================================
 * Modèle pour un plateau de tuiles
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class TileBoard {

    // Membres internes
    private TileGenerator               generator;
    private int                         width;
    private int                         height;
    private Tile[][]                    tiles;

}

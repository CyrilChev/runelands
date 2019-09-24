package com.cyg.rl.module.core.models;

import com.cyg.rl.module.core.types.EffectTarget;
import com.cyg.rl.module.core.types.EffectType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * =================================================================================================================
 * Classe de base représentant un modification de propriété
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
public class Effect {

    // Membres internes
    private EffectType type;
    private EffectTarget target;
}

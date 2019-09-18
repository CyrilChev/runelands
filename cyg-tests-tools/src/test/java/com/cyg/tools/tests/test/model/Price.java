package com.cyg.tools.tests.test.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * =================================================================================================================
 * Exemple de modèle : prix
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
@ToString(exclude= {"product"})
public class Price {

    // Membres internes
    private Long id;
    private Product product;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
    private LocalDate date;
    private BigDecimal price;

}

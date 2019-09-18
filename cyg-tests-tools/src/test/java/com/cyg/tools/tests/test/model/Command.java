package com.cyg.tools.tests.test.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

/**
 * =================================================================================================================
 * Exemple de modèle : commande
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
public class Command {

    // Membres internes
    private Long id;
    private String reference;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
    private LocalDate date;
    private Personne customer;
    private List<CommandLine> lines;
}

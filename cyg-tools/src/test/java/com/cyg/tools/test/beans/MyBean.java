package com.cyg.tools.test.beans;

import com.cyg.tools.utils.IIdentifiable;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * =================================================================================================================
 * Bean de test
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Data
@Builder
@EqualsAndHashCode(of={"id"})
@NoArgsConstructor
@AllArgsConstructor(staticName="of")
public class MyBean implements IIdentifiable<Long> {

    // Membres internes
    private Long                    id;
    private String                  nom;
    private String                  prenom;
    private LocalDate               dateNaissance;
    private Integer                 nombreEnfants;
    private UncomparableData        incomparable;

    // ---------------------------------- Méthodes statiques publiques ---------------------------------------
    /**
     * Crée une liste de beans de test
     * @return
     * @since 0.0.1
     */
    public static List<MyBean> createBeans() {
        return List.of(
                MyBean.of(0L, "VALJEAN", "Jean", LocalDate.of(1965, 5, 25), 1, UncomparableData.of(0L)),
                MyBean.of(1L, "VALJEAN", "Anne", LocalDate.of(1969, 2, 12), null, UncomparableData.of(0L)),
                MyBean.of(2L, "AYME", "Marcel", LocalDate.of(1973, 12, 25), 2, UncomparableData.of(0L)),
                MyBean.of(3L, "LEBLANC", "Maurice", LocalDate.of(1932, 8, 16), null, UncomparableData.of(0L)),
                MyBean.of(4L, "LUPIN", "Arsène", LocalDate.of(1945, 1, 18), 0, UncomparableData.of(0L)),
                MyBean.of(5L, "HUGO", "Victor", LocalDate.of(1932, 8, 16), 1, UncomparableData.of(0L))
        );
    }

}

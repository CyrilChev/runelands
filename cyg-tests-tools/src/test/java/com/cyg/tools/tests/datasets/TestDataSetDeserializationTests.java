package com.cyg.tools.tests.datasets;

import com.cyg.tools.jackson.JacksonConfigurer;
import com.cyg.tools.tests.test.model.Adresse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * =================================================================================================================
 * Tests de la désérialisation des jeux de données
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
@Slf4j
public class TestDataSetDeserializationTests {

    // ----------------------------------------------- Tests -------------------------------------------------------
    /**
     * Teste que la lecture d'un fichier correct (sans référence externe) fonctionne
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    @Test
    @SneakyThrows
    public void whenSimpleJsonResourceIsOk_thenShouldReadDataSet() {
        // On crée le résultat attendu
        TestDataSetCollection waitedResult = TestDataSetCollection.of(
                Adresse.class,
                List.of(
                        TestDataSet.of("adresses1", Adresse.class, List.of(
                                Adresse.of(1L, "Juvisy-sur-Orge", "91260"),
                                Adresse.of(2L, "Paris", "75001"),
                                Adresse.of(3L, "Marseille", "75003")
                        )),
                        TestDataSet.of("adresses2", Adresse.class, List.of(
                                Adresse.of(4L, "Paris", "75013")
                        )),
                        TestDataSet.of("adresseToAdd", Adresse.class, List.of(
                                Adresse.of(null, "Paris", "75002")
                        )),
                        TestDataSet.of("adresseToAddSaved", Adresse.class, List.of(
                                Adresse.of(4L, "Paris", "75002")
                        ))
                )
        );

        // On effectue la lecture
        ObjectMapper mapper = JacksonConfigurer.getInstance().createMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TestDataSetCollection.class, new TestDataSetCollectionDeserializer());
        module.addDeserializer(TestDataSet.class, new TestDataSetDeserializer());
        mapper.registerModule(module);
        InputStream is = this.getClass().getResourceAsStream("/adresseTestSet.json");
        TestDataSetCollection collection = mapper.readValue(is, TestDataSetCollection.class);
        log.info("Collection: {}",collection);

        assertThat(collection).isNotNull();
        assertThat(collection).isEqualTo(waitedResult);
        assertThat(collection.getSets().get(0).getData()).usingFieldByFieldElementComparator().containsExactlyElementsOf(waitedResult.getSets().get(0).getData());
        assertThat(collection.getSets().get(1).getData()).usingFieldByFieldElementComparator().containsExactlyElementsOf(waitedResult.getSets().get(1).getData());
    }
}

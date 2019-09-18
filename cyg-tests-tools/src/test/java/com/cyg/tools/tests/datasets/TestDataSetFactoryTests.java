package com.cyg.tools.tests.datasets;

import com.cyg.tools.tests.test.model.Adresse;
import com.cyg.tools.tests.test.model.Command;
import com.cyg.tools.tests.test.model.CommandLine;
import com.cyg.tools.tests.test.model.Personne;
import com.cyg.tools.tests.test.model.Price;
import com.cyg.tools.tests.test.model.Product;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * =================================================================================================================
 * Tests unitaires sur TestDataSetFactory
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class TestDataSetFactoryTests {


    // --------------------------------------------- Tests ---------------------------------------------------
    /**
     * Teste que quand on demande si un jeu de données avec un nom null est enregistré, la fabrique répond correctement
     * @since 0.0.1
     */
    @Test
    public void whenAskForNullDatasetNameRegistered_shouldReturnFalse() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear();
        assertThat(factory.isTestSetRegistered(null)).isFalse();
    }

    /**
     * Teste que quand on enregistre un jeu de données null, la fabrique répond correctement
     * @since 0.0.1
     */
    @Test
    public void whenRegisterNullDataSet_shouldNotFailed() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerDataSet(null);
        assertThat(factory.isEmpty()).isTrue();
    }

    /**
     * Teste que quand on enregistre un jeu de données, il se trouve bien dans la fabrique
     * @since 0.0.1
     */
    @Test
    public void whenRegisterDataSet_shouldContainsDataSet() {
        TestDataSet dataSet = createTestDataSet();
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerDataSet(dataSet);

        assertThat(factory.isTestSetRegistered("testSet")).isTrue();
    }

    /**
     * Teste que quand on désenregistre un jeu de données, il ne se trouve plus dans la fabrique
     * @since 0.0.1
     */
    @Test
    public void whenUnregisterDataSet_shouldNotContainsDataSet() {
        TestDataSet dataSet = createTestDataSet();
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerDataSet(dataSet);
        factory.unregisterDataSet("testSet");

        assertThat(factory.isTestSetRegistered("testSet")).isFalse();
    }

    /**
     * Teste que lorsqu'on demande un jeu de donnée inconnu, la récupération de données retourne null
     * @since 0.0.1
     */
    @Test
    public void whenAskForDataInUnknownDataSet_shouldReturnNull() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear();
        Object result = factory.getData("unknown", 0);
        assertThat(result).isNull();

        Seq<Object> seqResult = factory.getDataSeq("unknown");
        assertThat(seqResult).isEmpty();
    }

    /**
     * Teste que lorsqu'on demande un jeu de donnée inconnu, la récupération de données retourne null
     * @since 0.0.1
     */
    @Test
    public void whenAskForUnknownDataSet_shouldReturnEmptySeq() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear();
        Seq<Object> seqResult = factory.getDataSeq("unknown");
        assertThat(seqResult).isEmpty();
    }

    /**
     * Teste que lorsqu'on demande un jeu de donnée connu avec un mauvais index, la récupération de données retourne null
     * @since 0.0.1
     */
    @Test
    public void whenAskForDataSetWithBadIndex_shouldReturnNull() {
        TestDataSet dataSet = createTestDataSet();
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerDataSet(dataSet);
        Object result = factory.getData("testSet", 5);
        assertThat(result).isNull();
    }

    /**
     * Teste que lorsqu'on demande un jeu de donnée connu avec un index correct, la récupération de données doit fonctionner
     * @since 0.0.1
     */
    @Test
    public void whenAskForDataSetWithIndex_shouldReturnDuplicatedData() {
        TestDataSet dataSet = createTestDataSet();
        Adresse waitedResult = (Adresse) dataSet.getData().get(0);
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerDataSet(dataSet);
        Adresse result = factory.getData("testSet", 0);

        assertThat(result).isNotNull();
        assertThat(result).isEqualToComparingFieldByField(waitedResult);
        assertThat(result).isNotSameAs(waitedResult);

        result = factory.getData("testSet");

        assertThat(result).isNotNull();
        assertThat(result).isEqualToComparingFieldByField(waitedResult);
        assertThat(result).isNotSameAs(waitedResult);
    }

    /**
     * Teste que lorsqu'on demande un jeu de donnée connu,  la récupération de données doit fonctionner
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    @Test
    public void whenAskForDataSet_shouldReturnDuplicatedSeqData() {
        TestDataSet dataSet = createTestDataSet();
        List<Adresse> waitedResult = (List<Adresse>) dataSet.getData();
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerDataSet(dataSet);
        Seq<Adresse> result = factory.getDataSeq("testSet");

        assertThat(result).isNotNull();
        assertThat(result).isNotSameAs(waitedResult);
        assertThat(result).usingFieldByFieldElementComparator().containsExactlyElementsOf(waitedResult);
        result.forEach(adresse -> assertThat(adresse).isNotSameAs(waitedResult.find(a -> a.equals(adresse)).get()));
    }

    /**
     * Teste que lorsqu'on enregistre un jeu de données depuis un fichier JSON, celui-ci se trouve bien dans la fabrique
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    @Test
    public void whenRegisterSimpleJson_shouldContainDataSet() {
        TestDataSet dataSet = this.createTestDataSet();
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("adresseTestSet");

        assertThat(factory.isTestSetRegistered("adresses1")).isTrue();
        assertThat(factory.isTestSetRegistered("adresses2")).isTrue();

        Adresse adresse1 = factory.getData("adresses1", 0);
        assertThat(adresse1).isNotNull();
        assertThat(adresse1).isEqualToComparingFieldByField(dataSet.getData().get(0));

        Seq<Adresse> adresses = factory.getDataSeq("adresses1");
        assertThat(adresses).isNotNull();
        assertThat(adresses).isNotEmpty();
        assertThat(adresses).usingFieldByFieldElementComparator().containsExactlyElementsOf(dataSet.getData());
    }

    /**
     * Teste que lorsqu'on enregistre un jeu de données depuis un fichier JSON avec inclusion et références, celui-ci se trouve bien dans la fabrique
     * @since 0.0.1
     */
    @Test
    public void whenRegisterRefJson_shouldContainDataSet() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("personneTestSet");

        assertThat(factory.isTestSetRegistered("personnes1")).isTrue();
        assertThat(factory.isTestSetRegistered("personnes2")).isTrue();
        assertThat(factory.isTestSetRegistered("adresses1")).isTrue();
        assertThat(factory.isTestSetRegistered("adresses2")).isTrue();

        TestDataSet dataSet1 = this.createPersonneTestDataSet();
        Personne personne = factory.getData("personnes1", 0);
        assertThat(personne).isNotNull();
        assertThat(personne).isEqualToComparingFieldByFieldRecursively(dataSet1.getData().get(0));

        TestDataSet dataSet2 = this.createPersonneTestDataSetWithAdresses();
        Personne personne1 = factory.getData("personnes2", 0);
        assertThat(personne1).isNotNull();
        assertThat(personne1).isEqualToComparingFieldByFieldRecursively(dataSet2.getData().get(0));

        Personne personne2 = factory.getData("personnes2", 1);
        assertThat(personne2).isNotNull();
        assertThat(personne2).isEqualToComparingFieldByFieldRecursively(dataSet2.getData().get(1));

        Personne personne3 = factory.getData("personnes2", 2);
        assertThat(personne3).isNotNull();
        assertThat(personne3).isEqualToComparingFieldByFieldRecursively(dataSet2.getData().get(1));
    }

    /**
     * Teste que lorsqu'on enregistre un jeu de données depuis un fichier JSON avec une mauvaise inclusion, une exception est levée
     * @since 0.0.1
     */
    @Test
    public void whenRegisterBadIncludeJson_shouldRaiseException() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear();
        Throwable thrown = catchThrowable(() -> factory.registerJson("personneTestSetBadInclude"));
        assertThat(thrown).isInstanceOf(IOException.class);
        assertThat(thrown.getMessage()).isEqualTo("java.io.IOException: Impossible d'enregistrer le jeu de test : la ressource adresseTestSetUnknown.json est introuvable.");
    }

    /**
     * Teste que lorsqu'on enregistre un jeu de données depuis un fichier JSON avec une mauvaise inclusion, une exception est levée
     * @since 0.0.1
     */
    @Test
    public void whenRegisterBadReferenceJson_shouldRaiseException() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear();
        Throwable thrown = catchThrowable(() -> factory.registerJson("personneTestSetBadReference"));
        assertThat(thrown).isInstanceOf(IOException.class);
        assertThat(thrown.getMessage()).contains("Impossible d'interpréter correctement la référence unreferenced pour le champ address");

        thrown = catchThrowable(() -> factory.registerJson("personneTestSetBadReferenceField"));
        assertThat(thrown).isInstanceOf(IOException.class);
        assertThat(thrown.getMessage()).contains(" La propriété unknownField est introuvable dans l'objet com.cyg.tools.tests.test.model.Personne");
    }

    /**
     * Teste que lorsqu'on enregistre un jeu de données depuis un fichier JSON avec des inclusions circulaires, tout fonctionne correctement
     * @since 0.0.1
     */
    @Test
    public void whenRegisterCircularIncludeJson_shouldNotLoop() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("personneTestSetCircularInclude");
        assertThat(factory.isTestSetRegistered("personnes1")).isTrue();
        assertThat(factory.isTestSetRegistered("adresses1")).isTrue();
        assertThat(factory.isTestSetRegistered("adresses2")).isTrue();
    }

    /**
     * Teste que lorsqu'on enregistre un jeu de données complexe depuis un fichier JSON avec inclusion et références, celui-ci se trouve bien dans la fabrique
     * @since 0.0.1
     */
    @Test
    public void whenRegisterComplexJson_shouldContainDataSet() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("commandTestSet");

        assertThat(factory.isTestSetRegistered("commands1")).isTrue();
        assertThat(factory.isTestSetRegistered("products1")).isTrue();
        assertThat(factory.isTestSetRegistered("personnes1")).isTrue();
        assertThat(factory.isTestSetRegistered("adresses1")).isTrue();
        assertThat(factory.isTestSetRegistered("prices1")).isTrue();

        List<Product> waitedProducts = this.createProducts1();
        Product product1 = factory.getData("products1", 0);
        assertThat(product1).isNotNull();
        assertThat(product1).isEqualToComparingFieldByFieldRecursively(waitedProducts.get(0));
        assertThat(product1.getPrices()).usingFieldByFieldElementComparator().containsExactlyElementsOf(waitedProducts.get(0).getPrices());

        Product product2 = factory.getData("products1", 1);
        assertThat(product2).isNotNull();
        assertThat(product2).isEqualToComparingFieldByFieldRecursively(waitedProducts.get(1));
        assertThat(product2.getPrices()).usingFieldByFieldElementComparator().containsExactlyElementsOf(waitedProducts.get(1).getPrices());

        List<Command> waitedCommands = this.createCommand1();

        Command command = factory.getData("commands1", 0);
        assertThat(command).isNotNull();
        assertThat(command).isEqualToComparingFieldByFieldRecursively(waitedCommands.get(0));
        assertThat(command.getLines()).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedCommands.get(0).getLines());
    }

    /**
     * Teste que quand on demande si un jeu de données avec un nom null est enregistré, la fabrique répond correctement
     * @since 0.0.1
     */
    @Test
    public void whenAskForDatasetClass_shouldReturnRightClass() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("personneTestSet");
        assertThat(factory.getDataSetClass("personnes1")).isNotNull().isEqualTo(Personne.class);
        assertThat(factory.getDataSetClass("unknown")).isNull();
    }

    /**
     * Teste la récupération d'éléments d'un jeu de données avec index
     * @since 0.0.1
     */
    @Test
    public void whenAskForDataSeqWithIndexes_shouldReturnRightDataSeq() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("personneTestSet");
        Seq<Personne> personnes = factory.getDataSeq("personnes3");
        Seq<Personne> waitedPersonnes = List.of(personnes.get(0), personnes.get(2));

        Seq<Personne> result = factory.getDataSeq("personnes3", 0, 2);
        assertThat(result).isNotNull().usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedPersonnes);

        result = factory.getDataSeq("personnes3", List.ofAll(0, 2));
        assertThat(result).isNotNull().usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedPersonnes);

        result = factory.getDataSeq("personnes3", false, 0, 2);
        assertThat(result).isNotNull().usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedPersonnes);
    }

    /**
     * Teste la récupération d'éléments d'un jeu de données à l'exception de ceux dont l'index est passé
     * @since 0.0.1
     */
    @Test
    public void whenAskForDataSeqExceptIndexes_shouldReturnRightDataSeq() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("personneTestSet");
        Seq<Personne> personnes = factory.getDataSeq("personnes3");
        Seq<Personne> waitedPersonnes = List.of(personnes.get(0), personnes.get(3));

        Seq<Personne> result = factory.getDataSeqExcept("personnes3", 1, 2);
        assertThat(result).isNotNull().usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedPersonnes);

        result = factory.getDataSeqExcept("personnes3", List.ofAll(1,2));
        assertThat(result).isNotNull().usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedPersonnes);

        result = factory.getDataSeqExcept("personnes3", false, 1, 2);
        assertThat(result).isNotNull().usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedPersonnes);
    }

    /**
     * Teste que le traitement de l'instruction Now de date du jour fonctionne correctement
     *
     * @since 1.0.2
     */
    @Test
    public void whenUseNowInstruction_shouldProcessCorrectly() {
        TestDataSetFactory factory = TestDataSetFactory.getInstance().clear().registerJson("commandTestSet");

        List<Command> waitedCommands = this.createCommand2();

        Command command = factory.getData("commands2", 0);
        assertThat(command).isNotNull();
        assertThat(command).isEqualToComparingFieldByFieldRecursively(waitedCommands.get(0));
        assertThat(command.getLines()).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(waitedCommands.get(0).getLines());
    }

    // --------------------------------------- Méthodes privées ---------------------------------------------
    /**
     * Crée un jeu de données
     * @return
     * @since 0.0.1
     */
    private TestDataSet createTestDataSet() {
        return TestDataSet.of("testSet", Adresse.class, this.createAdresses());
    }

    /**
     * Crée une liste d'adresses conforme au jeu de données adresses1
     * @return
     * @since 0.0.1
     */
    private List<Adresse> createAdresses() {
        return List.of(
                Adresse.of(1L, "Juvisy-sur-Orge", "91260"),
                Adresse.of(2L, "Paris", "75001"),
                Adresse.of(3L, "Marseille", "75003")
        );
    }

    /**
     * Crée un jeu de données personne
     * @return
     * @since 0.0.1
     */
    private TestDataSet createPersonneTestDataSet() {
        return TestDataSet.of("personnes1", Personne.class, List.of(
                Personne.of(1L, "Cyril", "Chevalier", 45, this.createAdresses().get(1), null)
        ));
    }

    /**
     * Crée un jeu de tests conforme au jeu de test personnes2 du fichier json
     * @return
     * @since 0.0.1
     */
    private TestDataSet createPersonneTestDataSetWithAdresses() {
        List<Adresse> adresses = this.createAdresses();
        return TestDataSet.of("personnes2", Personne.class, List.of(
                Personne.of(1L, "Cyril", "Chevalier", 45, adresses.get(0), adresses),
                Personne.of(2L, "Paul", "Martin", 53, adresses.get(2), List.of(adresses.get(2), adresses.get(0)))
        ));
    }

    /**
     * Crée une liste de produits équivalente au jeu de test products1
     * @return
     * @since 0.0.1
     */
    private List<Product> createProducts1() {
        return List
                .of(
                        Product.of(1L, "Produit 1", List.of(
                                Price.of(1L, null, LocalDate.of(2019, 1, 1), new BigDecimal("301.15"))
                        )),
                        Product.of(2L, "Produit 2", List.of(
                                Price.of(2L, null, LocalDate.of(2019, 1, 1), new BigDecimal("28.12")),
                                Price.of(3L, null, LocalDate.of(2018, 1, 1), new BigDecimal("27.54")),
                                Price.of(4L, null, LocalDate.of(2017, 1, 1), new BigDecimal("27.21"))
                        ))
                )
                .map(this::mapPrices);
    }

    /**
     * Mappe les prix d'un produit pour gérer les références correctement
     * @param product
     * @return
     * @since 0.0.1
     */
    private Product mapPrices(Product product) {
        product.getPrices().forEach(price -> price.setProduct(product));
        return product;
    }

    /**
     * Crée une jeu de commandes équivalent à commands1
     * @return
     * @since 0.0.1
     */
    private List<Command> createCommand1() {
        List<Product> products = this.createProducts1();
        return List.of(Command.of(  1L,
                "C000001",
                LocalDate.of(2019, 1, 17),
                Personne.of(1L, "Cyril", "Chevalier", 45, this.createAdresses().get(1), null),
                List.of(
                        CommandLine.of(1L, 1, products.get(0), null),
                        CommandLine.of(2L, 2, products.get(1), null)

                )
        ))
                .map(this::mapLines);
    }

    /**
     * Crée une jeu de commandes équivalent à commands2
     * @return
     * @since 0.0.1
     */
    private List<Command> createCommand2() {
        List<Product> products = this.createProducts1();
        return List.of(Command.of(  1L,
                "C000001",
                LocalDate.now(),
                Personne.of(1L, "Cyril", "Chevalier", 45, this.createAdresses().get(1), null),
                List.of(
                        CommandLine.of(1L, 1, products.get(0), null),
                        CommandLine.of(2L, 2, products.get(1), null)

                )
        ))
                .map(this::mapLines);
    }

    /**
     * Mappe les lignes de commande pour gérer les références correctement
     * @param command
     * @return
     * @since 0.0.1
     */
    private Command mapLines(Command command) {
        command.getLines().forEach(line -> line.setCommand(command));
        return command;
    }
}

package com.cyg.tools.tests.datasets;

import com.cyg.tools.helper.BeansHelper;
import com.cyg.tools.jackson.JacksonConfigurer;
import com.cyg.tools.tests.helper.TestDateTimeProvider;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.rits.cloning.Cloner;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

/**
 * =================================================================================================================
 * Classe représentant la fabrique de jeux de tests centralisée.
 * Cette classe est un singleton.
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class TestDataSetFactory {

    // Membres internes statiques
    private static final TestDataSetFactory                         instance = new TestDataSetFactory();

    // Membres internes
    private Map<String, TestDataSet>                testDataSets;
    private ObjectMapper                            mapper;
    private List<String>                            jsonResourceProcessStack;
    private List<TestDataReference>                 references;
    private Cloner cloner;

    /**
     * Constructeur
     */
    private TestDataSetFactory() {
        this.testDataSets = HashMap.empty();
        this.jsonResourceProcessStack = List.empty();
        this.references = List.empty();
        this.cloner = new Cloner();
        this.createJacksonMapper();
        JacksonConfigurer.getInstance().addChangeListener(c -> this.createJacksonMapper());
    }

    // ---------------------------------- Méthodes statiques publiques ---------------------------------------
    /**
     * Retourne la seule instance de cette classe
     * @return TestDateSetFactory
     * @since 0.0.1
     */
    public static TestDataSetFactory getInstance() {
        return instance;
    }

    // --------------------------------------- Méthodes publiques ---------------------------------------------
    /**
     * Supprime tous les jeux de données de cette fabrique
     * @return TestDataSetFactory
     * @since 0.0.1
     */
    public synchronized TestDataSetFactory clear() {
        this.testDataSets = HashMap.empty();
        this.jsonResourceProcessStack = List.empty();
        this.references = List.empty();
        // On réinitialise aussi le fournisseur de dates
        TestDateTimeProvider.getInstance().reset();
        return this;
    }

    /**
     * Retourne vrai si la fabrique ne contient aucun jeu de test
     * @return boolean
     * @since 0.0.1
     */
    public synchronized boolean isEmpty() {
        return this.testDataSets.isEmpty();
    }

    /**
     * Enregistre un jeu de données
     * @param dataSet Jeu de données
     * @return TestDataSetFactory
     * @since 0.0.1
     */
    public synchronized TestDataSetFactory registerDataSet(TestDataSet dataSet) {
        if (dataSet!=null) {
            this.testDataSets = this.testDataSets.put(dataSet.getName(), dataSet);
        }
        return this;
    }

    /**
     * Enregistre une ressource JSON
     * @param jsonResource Ressource JSON
     * @return TestDataSetFactory
     * @since 0.0.1
     */
    @SneakyThrows
    public synchronized TestDataSetFactory registerJson(String ... jsonResource ) {
        List.of(jsonResource)
                .map(r -> r.endsWith(".json") ? r : r + ".json")
                .forEach(resource -> {
                    if (!this.jsonResourceProcessStack.contains(resource)) {
                        this.processRegisterJson(resource);
                        this.checkReferences();
                    }
                });
        return this;
    }

    /**
     * Retourne le type de données stocké par un jeu de données
     * @param dataSetName
     * @return Class
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> Class<T> getDataSetClass(String dataSetName) {
        return (Class<T>)this.testDataSets.get(dataSetName).map(TestDataSet::getDataClass).getOrElse((Class<T>)null);

    }

    /**
     * Retourne vrai si la fabrique a enregistré un jeu de test
     * @param dataSetName Nom du jeu de tests
     * @return boolean
     * @since 0.0.1
     */
    public synchronized boolean isTestSetRegistered(String dataSetName) {
        return dataSetName != null && this.testDataSets.containsKey(dataSetName);
    }

    /**
     * Supprime un jeu de données
     * @param dataSetName Nom du jeu de données
     * @return TestDataSetFactory
     * @since 0.0.1
     */
    public synchronized TestDataSetFactory unregisterDataSet(String dataSetName) {
        if (dataSetName!=null) {
            this.testDataSets = this.testDataSets.remove(dataSetName);
        }
        return null;
    }

    /**
     * Retourne une donnée d'un jeu de données pour le premier index
     * @param dataSetName Nom du jeu de données
     * @return T
     * @since 0.0.1
     */
    public synchronized <T> T getData(String dataSetName) {
        return this.getData(dataSetName, 0);
    }

    /**
     * Retourne une donnée d'un jeu de données à un index donné
     * @param dataSetName Nom du jeu de données
     * @param index Index de la donnée
     * @return T
     * @since 0.0.1
     */
    public synchronized <T> T getData(String dataSetName,int index) {
        return this.getData(dataSetName, index, true);
    }

    /**
     * Retourne une liste de données d'un jeu de données
     * @param dataSetName Nom du jeu de données
     * @return Seq<T>
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeq(String dataSetName) {
        return this.getDataSeq(dataSetName, true);
    }

    /**
     * Retourne une liste de données d'un jeu de données en filtrant par index
     * @param dataSetName Nom du jeu de données
     * @param indexes Liste des index
     * @return Seq<T>
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeq(String dataSetName,int ... indexes) {
        return this.getDataSeq(dataSetName, true, indexes);
    }

    /**
     * Retourne une liste de données d'un jeu de données en filtrant par index
     * @param dataSetName Nom du jeu de données
     * @param indexes Liste des index
     * @return Seq
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeq(String dataSetName,Seq<Integer> indexes) {
        return this.getDataSeq(dataSetName, true, indexes);
    }

    /**
     * Retourne une liste de données d'un jeu de données en retournant tous les éléments sauf ceux dont l'index
     * est passé en paramètre
     * @param dataSetName Nom du jeu de données
     * @param indexes Liste des index
     * @return Seq
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeqExcept(String dataSetName,int ... indexes) {
        return this.getDataSeqExcept(dataSetName, true, indexes);
    }

    /**
     * Retourne une liste de données d'un jeu de données en retournant tous les éléments sauf ceux dont l'index
     * est passé en paramètre
     * @param dataSetName Nom du jeu de données
     * @param indexes Liste des index
     * @return Seq
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeqExcept(String dataSetName,Seq<Integer> indexes) {
        return this.getDataSeqExcept(dataSetName, true, indexes);
    }

    /**
     * Retourne une donnée d'un jeu de données à un index donné
     * @param dataSetName Nom du jeu de données
     * @param index Index de la donnée
     * @param deepCopy Indique si on doit faire une copie
     * @return Seq
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T getData(String dataSetName,int index, boolean deepCopy) {
        // On récupère l'objet
        Option<TestDataSet> dataSet = this.testDataSets.get(dataSetName);
        Option<T> result = dataSet.flatMap(ds -> Option.of(ds.getData().length()>index ? ((List<T>)ds.getData()).get(index) : null));
        return deepCopy ? result.map(obj -> (T)this.deepCopy(obj)).getOrNull() : result.getOrNull();
    }

    /**
     * Retourne une liste de données d'un jeu de données
     * @param dataSetName Nom du jeu de données
     * @param deepCopy Indique si on doit faire une copie
     * @return Seq
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> Seq<T> getDataSeq(String dataSetName, boolean deepCopy) {
        Option<TestDataSet> dataSet = this.testDataSets.get(dataSetName);
        Option<Seq<T>> result = dataSet.map(TestDataSet::getData);
        return deepCopy ? result.getOrElse(List.empty()).map(obj -> (T)this.deepCopy(obj)) : result.getOrElse(List.empty());
    }

    /**
     * Retourne une liste de données d'un jeu de données en filtrant par index
     * @param dataSetName Nom du jeu de données
     * @param deepCopy Indique si on doit faire une copie
     * @param indexes Liste des indexs
     * @return Seq
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeq(String dataSetName, boolean deepCopy, int ... indexes) {
        return this.getDataSeq(dataSetName, deepCopy, List.ofAll(indexes));
    }

    /**
     * Retourne une liste de données d'un jeu de données en filtrant par index
     * @param dataSetName Nom du jeu de données
     * @param deepCopy Indique si on doit faire une copie
     * @param indexes Liste des indexs
     * @return Seq
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> Seq<T> getDataSeq(String dataSetName, boolean deepCopy, Seq<Integer> indexes) {
        Option<TestDataSet> dataSet = this.testDataSets.get(dataSetName);
        Option<Seq<T>> result = dataSet.map(ds -> indexes.map(i -> (T)ds.getData().get(i)));
        return deepCopy ? result.getOrElse(List.empty()).map(obj -> (T)this.deepCopy(obj)) : result.getOrElse(List.empty());
    }

    /**
     * Retourne une liste de données d'un jeu de données en retournant tous les éléments sauf ceux dont l'index
     * est passé en paramètre
     * @param dataSetName Nom du jeu de données
     * @param deepCopy Indique si on doit faire une copie
     * @param indexes Liste des indexs
     * @return
     * @since 0.0.1
     */
    public synchronized <T> Seq<T> getDataSeqExcept(String dataSetName, boolean deepCopy, int ... indexes) {
        return this.getDataSeqExcept(dataSetName, deepCopy, List.ofAll(indexes));
    }

    /**
     * Retourne une liste de données d'un jeu de données en retournant tous les éléments sauf ceux dont l'index
     * est passé en paramètre
     * @param dataSetName Nom du jeu de données
     * @param deepCopy Indique si on doit faire une copie
     * @param indexes Liste des indexs
     * @return
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> Seq<T> getDataSeqExcept(String dataSetName, boolean deepCopy, Seq<Integer> indexes) {
        Option<TestDataSet> dataSet = this.testDataSets.get(dataSetName);
        Option<Seq<T>> result = dataSet
                .map(ds -> ((List<T>)ds.getData())
                        .zipWithIndex()
                        .filter(t -> !indexes.contains(t._2()))
                        .map(Tuple2::_1));
        return deepCopy ? result.getOrElse(List.empty()).map(obj -> (T)this.deepCopy(obj)) : result.getOrElse(List.empty());
    }

    // --------------------------------------- Méthodes privées ---------------------------------------------
    /**
     * Crée le mapper jackson
     * @return
     * @since 1.0.4
     */
    private void createJacksonMapper() {
        this.mapper = JacksonConfigurer.getInstance().createMapper().addHandler(new TestDataSetDeserializationProblemHandler());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TestDataSetCollection.class, new TestDataSetCollectionDeserializer());
        module.addDeserializer(TestDataSet.class, new TestDataSetDeserializer());
        this.mapper.registerModule(module);
    }

    /**
     * Effectue une copie "profonde" d'un objet
     * @param toCopy
     * @return
     * @since 0.0.1
     */
    @SneakyThrows
    private <T> T deepCopy(T toCopy) {
        return this.cloner.deepClone(toCopy);
    }

    /**
     * Effectue le traitement d'une ressource JSON pour l'enregistrer dans la fabrique
     * @param resource
     * @throws IOException
     * @since 0.0.1
     */
    @SneakyThrows
    private void processRegisterJson(String resource) {
        try {
            this.jsonResourceProcessStack = this.jsonResourceProcessStack.push(resource);
            InputStream is = this.getClass().getResourceAsStream("/"+resource);
            if (is == null) {
                throw new IOException("Impossible d'enregistrer le jeu de test : la ressource " + resource + " est introuvable.");
            }
            TestDataSetCollection collection = mapper.readValue(is, TestDataSetCollection.class);
            collection.getSets().forEach(this::registerDataSet);
        } finally {
            this.jsonResourceProcessStack = this.jsonResourceProcessStack.pop();
        }
    }

    /**
     * Vérifie si on doit traiter les références
     * @throws IOException
     * @since 0.0.1
     */
    @SneakyThrows
    private void checkReferences() {
        // Si la pile des fichiers à traiter est vide
        if (this.jsonResourceProcessStack.isEmpty()) {
            // On traite toutes les références en attente
            this.references.forEach(this::processReference);
            this.references = List.empty();
        }
    }

    /**
     * Traite une référence
     * @param reference Référence à traiter
     * @throws IOException
     * @since 0.0.1
     */
    @SneakyThrows
    private void processReference(TestDataReference reference) {
        Class<?> fieldType = reference.getField().getType();
        if (Seq.class.isAssignableFrom(fieldType)) {
            this.processSeqReference(reference);
        }
        else if (Collection.class.isAssignableFrom(fieldType)) {
            this.processCollectionReference(reference);
        }
        else {
            this.processObjectReference(reference);
        }
    }

    /**
     * Traite une référence sous format de séquence
     * @param reference
     * @throws IOException
     * @since 0.0.1
     */
    @SuppressWarnings("rawtypes")
    @SneakyThrows
    private void processSeqReference(TestDataReference reference) throws IOException {
        Seq result = this.getReferenceSeq(reference);
        if (result!=null) {
            reference.getField().set(reference.getData(), result);
        }
        else {
            emitReferenceFormatException(reference.getReference(), reference.getField().getName());
        }
    }

    /**
     * Traite une référence sous format de collection
     * @param reference
     * @throws IOException
     * @since 0.0.1
     */
    @SuppressWarnings("rawtypes")
    @SneakyThrows
    private void processCollectionReference(TestDataReference reference) throws IOException {
        Seq result = this.getReferenceSeq(reference);
        if (result!=null) {
            reference.getField().set(reference.getData(), result.asJava());
        }
        else {
            emitReferenceFormatException(reference.getReference(), reference.getField().getName());
        }
    }

    /**
     * Récupère la séquence correspondant à une référence
     * @param reference
     * @return
     * @throws IOException
     * @since 1.0.3
     */
    @SuppressWarnings("rawtypes")
    private Seq getReferenceSeq(TestDataReference reference) throws IOException {
        String[] refArgs = reference.getReference().split(",");
        Seq result = null;
        if (refArgs.length>0) {
            String testSetName = refArgs[0];
            if (refArgs.length>1) {
                Seq data = getDataSeq(testSetName, false);
                result = List.of(refArgs)
                        .subSequence(1)
                        .filter(s -> s.matches("-?\\d+(\\.\\d+)?"))
                        .map(Integer::valueOf)
                        .filter(i -> i<data.length())
                        .map(data::get);
            }
            else {
                result = getDataSeq(testSetName, false);
            }
        }
        return result;
    }

    /**
     * Traite une référence sous forme d'objet
     * @param reference Référence
     * @throws IOException Exception
     * @since 0.0.1
     */
    @SneakyThrows
    private void processObjectReference(TestDataReference reference) throws IOException {
        // On va découper
        String[] refArgs = reference.getReference().split(",");
        Object result = null;
        if (refArgs.length==1) {
            result = getData(refArgs[0], 0, false);
        }
        else if (refArgs.length==2) {
            result = getData(refArgs[0], Integer.parseInt(refArgs[1]), false);
        }
        if (result!=null) {
            reference.getField().set(reference.getData(), result);
        }
        else {
            emitReferenceFormatException(reference.getReference(), reference.getField().getName());
        }
    }

    /**
     * Emet une exception de format de référence
     * @param reference Nom de la référence
     * @param fieldName Nom du champ
     * @throws IOException Exception
     * @since 0.0.1
     */
    private void emitReferenceFormatException(String reference, String fieldName) throws IOException {
        throw new IOException("Impossible d'interpréter correctement la référence " + reference + " pour le champ " + fieldName);
    }

    // ---------------------------------- Classes internes ---------------------------------------
    /**
     * <p>
     *                      Classe interne pour gérer les références sur un item
     * </p>
     *
     * @author ccr
     * @since 0.0.1
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName="of")
    @EqualsAndHashCode
    @Builder(toBuilder=true)
    @ToString
    private static class TestDataReference {

        // Membres interes
        private Object                  data;
        private Field                   field;
        private String                  reference;

    }

    /**
     *
     * <p>
     *                  Classe interne de gestion des problème de désérialisation pour traiter les cas
     *                  particuliers lors de la désérialisation des fichiers json de jeux de tests :
     *                  références, etc.
     * </p>
     *
     * @author ccr
     * @since 0.0.1
     */
    private class TestDataSetDeserializationProblemHandler extends DeserializationProblemHandler {

        // ------------------------------------------- Surcharges -------------------------------------------
        /* (non-Javadoc)
         * @see com.fasterxml.jackson.databind.deser.DeserializationProblemHandler#handleUnknownProperty(com.fasterxml.jackson.databind.DeserializationContext, com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.JsonDeserializer, java.lang.Object, java.lang.String)
         */
        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
            boolean handled = false;
            if (propertyName.startsWith("->")) {
                handled = this.processReference(ctxt, p, deserializer, beanOrClass, propertyName);
            }
            if (propertyName.startsWith("*")) {
                handled = this.handleInstruction(ctxt, p, deserializer, beanOrClass, propertyName);
            }
            return handled;
        }

        // --------------------------------------- Méthodes privées ---------------------------------------------
        /**
         * Traite les objets de type référence (commençant par "->")
         * @param ctxt Contexte
         * @param p Parseur
         * @param deserializer Deserialiseur
         * @param beanOrClass Objet
         * @param propertyName Nom de propriété
         * @return
         * @since 0.0.1
         */
        private boolean processReference(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
            try {
                String realProperty = propertyName.substring(2);
                Field f = BeansHelper.getField(beanOrClass.getClass(), realProperty);
                if (f == null) {
                    throw new NoSuchFieldException();
                }
                f.setAccessible(true);

                // On ajoute la référence
                references = references.append(TestDataReference.of(beanOrClass, f, p.getText()));
                return true;

            } catch(NoSuchFieldException e) {
                throw new IOException("La propriété " + propertyName.substring(2) + " est introuvable dans l'objet " + beanOrClass.getClass().getName());
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        /**
         * Traite une instruction dans le fichier json
         * @param ctxt Contexte
         * @param p Parseur
         * @param deserializer Deserialiseur
         * @param beanOrClass Objet
         * @param propertyName Nom de propriété
         * @return
         * @since 1.0.2
         */
        @SneakyThrows
        private boolean handleInstruction(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) {
            String realProperty = propertyName.substring(1);
            Field f = BeansHelper.getField(beanOrClass.getClass(), realProperty);
            f.setAccessible(true);
            String instruction = p.getText();
            boolean result = false;
            if (instruction.equalsIgnoreCase("now")) {
                result = this.processNowInstruction(f, beanOrClass);
            }
            else if (instruction.equalsIgnoreCase("real_now")) {
                result = this.processRealNowInstruction(f, beanOrClass);
            }
            return result;
        }

        /**
         * Traite une instruction now (définit une date courante)
         * @param f Champ
         * @param beanOrClass Objet
         * @return boolean
         * @since 1.0.4
         */
        @SneakyThrows
        private boolean processNowInstruction(Field f, Object beanOrClass) {
            boolean result = true;
            if (f.getType().equals(LocalDate.class)) {
                f.set(beanOrClass, TestDateTimeProvider.getInstance().provideCurrentDate());
            }
            else if (f.getType().equals(LocalDateTime.class)) {
                f.set(beanOrClass, TestDateTimeProvider.getInstance().provideCurrentDateTime());
            }
            else if (f.getType().equals(Date.class)) {
                f.set(beanOrClass, TestDateTimeProvider.getInstance().provideCurrentJdkDate());
            }
            else {
                result = false;
            }
            return result;
        }

        /**
         * Traite une instruction real_now (définit une date courante absolue --> correspondant à la réalité au moment de l'exécution)
         * @param f Champ
         * @param beanOrClass Classe de bean
         * @return boolean
         * @since 1.0.4
         */
        @SneakyThrows
        private boolean processRealNowInstruction(Field f, Object beanOrClass) {
            boolean result = true;
            if (f.getType().equals(LocalDate.class)) {
                f.set(beanOrClass, LocalDate.now());
            }
            else if (f.getType().equals(LocalDateTime.class)) {
                f.set(beanOrClass, LocalDateTime.now());
            }
            else if (f.getType().equals(Date.class)) {
                f.set(beanOrClass, new Date());
            }
            else {
                result = false;
            }
            return result;
        }
    }
}

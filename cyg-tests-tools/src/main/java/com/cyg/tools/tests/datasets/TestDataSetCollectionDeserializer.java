package com.cyg.tools.tests.datasets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import io.vavr.collection.List;

import java.io.IOException;

/**
 * =================================================================================================================
 * Implémentation d'un désérialiseur JSON pour les collections de jeux de test
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class TestDataSetCollectionDeserializer extends StdNodeBasedDeserializer<TestDataSetCollection> {

    // Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public TestDataSetCollectionDeserializer() {
        this(null);
    }

    /**
     * @param targetType
     */
    protected TestDataSetCollectionDeserializer(Class<TestDataSetCollection> targetType) {
        super(targetType);
    }

    // ------------------------------------------- Surcharges -------------------------------------------
    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer#convert(com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public TestDataSetCollection convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        try {
            // On regarde d'abord s'il y a des inclusions d'autres fichiers
            if (root.has("*include")) {
                String includes = root.get("*include").textValue();
                if (includes!=null) {
                    this.processIncludedJson(includes);
                }
            }

            String dataClassName = root.get("dataClass").textValue();
            Class<?> dataClass = Class.forName(dataClassName);
            ctxt.setAttribute("TestDataSetCollectionDataClass", dataClass);
            JavaType type = ctxt.getTypeFactory().constructArrayType(TestDataSet.class);
            JsonDeserializer<?> deserializer = ctxt.findRootValueDeserializer(type);
            JsonParser nodeParser = root.get("sets").traverse(ctxt.getParser().getCodec());
            nodeParser.nextToken();
            TestDataSet[] sets = (TestDataSet[])deserializer.deserialize(nodeParser, ctxt);
            return TestDataSetCollection.of(dataClass, List.of(sets));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    // --------------------------------------- Méthodes privées ---------------------------------------------
    /**
     * Traite le ou les fichiers JSON inclus
     * @param includes
     * @since 0.0.1
     */
    private void processIncludedJson(String includes) {
        String[] includeArray = includes.split(",");
        List<String> includeJsonList = List.of(includeArray).map(s -> s.endsWith(".json") ? s : s + ".json");
        includeJsonList.forEach(s -> TestDataSetFactory.getInstance().registerJson(s));
    }
}

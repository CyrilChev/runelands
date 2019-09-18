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
 * Déserialiseur Jackson pour les jeux de test
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class TestDataSetDeserializer extends StdNodeBasedDeserializer<TestDataSet> {

    // Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur
     */
    public TestDataSetDeserializer() {
        this(null);
    }

    /**
     * @param vc
     */
    protected TestDataSetDeserializer(Class<TestDataSet> vc) {
        super(vc);
    }

    // ------------------------------------------- Surcharges -------------------------------------------
    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer#convert(com.fasterxml.jackson.databind.JsonNode, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public TestDataSet convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        try {
            String name = root.get("name").textValue();
            Class<?> dataClass = (Class<?>) ctxt.getAttribute("TestDataSetCollectionDataClass");
            if (root.has("dataClass")) {
                String dataClassName = root.get("dataClass").textValue();
                dataClass = Class.forName(dataClassName);
            }
            JavaType type = ctxt.getTypeFactory().constructArrayType(dataClass);
            JsonDeserializer<?> deserializer = ctxt.findRootValueDeserializer(type);
            JsonParser nodeParser = root.get("data").traverse(ctxt.getParser().getCodec());
            nodeParser.nextToken();
            Object[] data = (Object[]) deserializer.deserialize(nodeParser, ctxt);
            return TestDataSet.of(name, dataClass, List.of(data));

        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}

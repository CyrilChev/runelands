package com.cyg.tools.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * =================================================================================================================
 * Désérialiseur Jackson pour conserver un morceau de JSON sous forme de chaîne
 * Réf : https://stackoverflow.com/questions/4783421/how-can-i-include-raw-json-in-an-object-using-jackson
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class KeepAsJsonDeserializer extends JsonDeserializer<String> {

    // ----------------------------------------------------- JsonDeserializer<String> --------------------------------------------
    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return p.getCodec().readTree(p).toString();
    }

}

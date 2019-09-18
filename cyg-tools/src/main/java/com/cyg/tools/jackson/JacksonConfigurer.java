package com.cyg.tools.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.jackson.datatype.VavrModule;
import lombok.NonNull;

import java.text.DateFormat;
import java.util.function.Consumer;

/**
 * =================================================================================================================
 * Singleton permettant de définir une configuration par défaut pour les mapper Jackson
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class JacksonConfigurer {

    // Membres statiques internes
    private static final JacksonConfigurer                          instance = new JacksonConfigurer();

    // Membres internes
    private Seq<Module>                                             jacksonModules;
    private Seq<Consumer<JacksonConfigurer>> changeListeners;

    /**
     * Constructeur
     */
    private JacksonConfigurer() {
        this.jacksonModules = List.empty();
        this.changeListeners = List.empty();
        this.addModule(new VavrModule());
        this.addModule(new JavaTimeModule());
    }

    // ---------------------------------- Méthodes statiques publiques ---------------------------------------
    /**
     * Retourne la seule instance de cette classe
     *
     * @since 0.0.1
     */
    public static JacksonConfigurer getInstance() {
        return instance;
    }

    // --------------------------------------- Méthodes publiques ---------------------------------------------
    /**
     * Crée un nouveau mapper Jackson
     * @return ObjectMapper
     * @since 0.0.1
     */
    public ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(DateFormat.getDateInstance());
        this.jacksonModules.forEach(mapper::registerModule);
        return mapper;
    }

    /**
     * Ajoute un module dans la création d'objets par défaut
     * @param module Module
     * @return JacksonConfigurer
     * @since 0.0.1
     */
    public JacksonConfigurer addModule(@NonNull Module ... module) {
        int oldSize = this.jacksonModules.size();
        this.jacksonModules = this.jacksonModules.appendAll(List.of(module)).distinctBy(Module::getTypeId);
        if (this.jacksonModules.size() != oldSize) {
            this.changeListeners.forEach(c -> c.accept(this));
        }
        return this;
    }

    /**
     * Ajoute un listener sur les changements
     * @param listener Listener
     * @return JacksonConfigurer
     * @since 0.0.1
     */
    public JacksonConfigurer addChangeListener(@NonNull Consumer<JacksonConfigurer> listener) {
        this.changeListeners = this.changeListeners.append(listener);
        return this;
    }
}

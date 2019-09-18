package com.cyg.tools.tests.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * =================================================================================================================
 * Classe singleton permettant de fournir des dates aux classes de test, notamment pour déterminer une date courante
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public class TestDateTimeProvider {

    // Membres statiques internes
    private static final TestDateTimeProvider   instance = new TestDateTimeProvider();

    // Membres internes
    private LocalDateTime                   currentDateTime;
    private LocalDate                       currentDate;
    private Date                            currentJdkDate;

    /**
     * Constructeur
     */
    private TestDateTimeProvider() {
        this.reset();
    }

    // ---------------------------------- Méthodes statiques publiques ---------------------------------------
    /**
     * Retourne l'instance unique de cette classe
     * @return TestDateTimeProvider
     * @since 0.0.1
     */
    public static TestDateTimeProvider getInstance() {
        return instance;
    }

    // --------------------------------------- Méthodes publiques ---------------------------------------------
    /**
     * Fournit la date/heure courante
     * @return LocalDateTime
     * @since 0.0.1
     */
    public synchronized LocalDateTime provideCurrentDateTime() {
        return currentDateTime;
    }

    /**
     * Fournit la date courante
     * @return LocalDate
     * @since 0.0.1
     */
    public synchronized LocalDate provideCurrentDate() {
        return currentDate;
    }

    /**
     * Fournit la date courante
     * @return Date
     * @since 0.0.1
     */
    public synchronized Date provideCurrentJdkDate() {
        return currentJdkDate;
    }

    /**
     * Réinitialise les valeurs de date courantes
     * @return TestDateTimeProvider
     * @since 0.0.1
     */
    public synchronized TestDateTimeProvider reset() {
        this.currentDate = LocalDate.now();
        this.currentDateTime = LocalDateTime.now();
        this.currentJdkDate = new Date();
        return this;
    }
}

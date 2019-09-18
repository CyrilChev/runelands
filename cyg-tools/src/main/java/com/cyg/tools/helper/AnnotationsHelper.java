package com.cyg.tools.helper;

import io.vavr.collection.List;
import io.vavr.collection.Queue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * =================================================================================================================
 * Helper pour la gestion des annotations
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public abstract class AnnotationsHelper {

    /**
     *
     */
    private AnnotationsHelper() {
    }

    // ----------------------- Méthodes publiques ---------------------
    /**
     * Retourne une file d'annotations, la première étant la plus haute dans la hiérarchie des classes
     * @param annotedClass Classe annotée
     * @param annotationClass Classe d'annotation
     * @return Queue
     * @since 0.0.1
     */
    public static <T extends Annotation> Queue<T> getAnnotations(Class<?> annotedClass, Class<T> annotationClass){
        Queue<T> result = Queue.empty();
        Class<?> consideredClass = annotedClass;
        while (consideredClass!=null){
            T annotation = consideredClass.getAnnotation(annotationClass);
            if (annotation!=null){
                result = result.prepend(annotation);
            }
            consideredClass = consideredClass.getSuperclass();
        }
        return result;
    }

    /**
     * Retourne la liste des champs de la classe annotés par une annotation spécifique. Cette méthode retourne aussi les champs annotés
     * des classes héritées.
     * @param annotedClass Classe annotée
     * @param annotation Annotation à rechercer
     * @return List
     * @since 0.0.1
     */
    public static <T extends Annotation> List<AnnotedField<T>> getAnnotedFields(Class<?> annotedClass,Class<T> annotation) {
        List<AnnotedField<T>> result = List.empty();
        // Recherche des champs de la classe
        Class<?> consideredClass = annotedClass;
        while (consideredClass!=null) {
            Field[] fields = consideredClass.getDeclaredFields();
            if (fields!=null && fields.length>0) {
                result = processFields(result,fields,annotation);
            }
            consideredClass = consideredClass.getSuperclass();
        }

        return result;
    }


    // ------------------------------ Méthodes statiques privées ----------------------------
    /**
     * Traite tous les champs d'une classe dont on veut rechercher l'annotation
     * @param annotedFields Liste contenant les champs annotés en résultat
     * @param fields Champs à traiter
     * @param annotation Annotation à rechercher
     * @return List
     * @since 0.0.1
     */
    private static <T extends Annotation> List<AnnotedField<T>> processFields(List<AnnotedField<T>> annotedFields,Field[] fields,Class<T> annotation) {
        List<AnnotedField<T>> result = annotedFields;
        for (Field f : fields) {
            T a = f.getAnnotation(annotation);
            if (a!=null) {
                f.setAccessible(true);
                result = result.append(AnnotedField.of(a, f));
            }
        }
        return result;
    }

    // --------------------------------- Classes incluses -----------------------------------
    /**
     * Classe représentant un champ annoté
     *
     * @author ccr
     * @since 0.0.1
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(staticName="of")
    public static class AnnotedField<T extends Annotation> {

        // Membres internes
        private T               annotation;
        private Field           field;
    }

}

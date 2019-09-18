package com.cyg.tools.helper;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * =================================================================================================================
 * Helper pour les beans Java
 *
 * @author Cyril Chevalier
 * @since 0.0.1
 * =================================================================================================================
 */
public abstract class BeansHelper {

    // Membres statiques internes
    private static Map<Class<?>, BeanInfo<?>> beansMap = HashMap.empty();

    /**
     * Constructeur privé
     */
    private BeansHelper() {
    }

    // ------------ Méthodes statiques publiques ---------------
    /**
     * Renvoie vrai si deux beans sont égaux, faux dans le cas contraire
     * @param bean1 Bean 1
     * @param bean2 Bean 2
     * @return Boolean
     * @since 0.0.1
     */
    public static <T> Boolean beansAreEquals(T bean1, T bean2) {
        return bean1 != null && bean2 != null ?
                bean1.equals(bean2) :
                bean1 == null && bean2 == null;
    }

    /**
     * Retourne un comparateur sur le bean, basé sur les champs et l'ordre fournis en paramètre
     * @param beanClass Classe du bean
     * @param sort Informations de tri
     * @return Comparator
     * @since 0.0.1
     */
    @SafeVarargs
    public static <T> Comparator<T> getComparator(Class<T> beanClass, Tuple2<Boolean, String>... sort){
        BeanInfo<T> beanInfo = getBeanInfo(beanClass);
        return beanInfo.getComparator(sort);
    }

    /**
     * Retourne un comparateur sur le bean, basé sur les champs passés avec l'ordre croissant
     * @param beanClass Classe de bean
     * @param sort Champs à trier
     * @return Comparator
     * @since 0.0.1
     */
    public static <T> Comparator<T> getComparator(Class<T> beanClass,String ...sort){
        return getComparator(beanClass,true,sort);
    }

    /**
     * Retourne un comparateur sur le bean, basé sur les champs passés avec l'ordre fourni dans direction
     * @param beanClass Classe de bean
     * @param direction Direction
     * @param sort Champs à trier
     * @return Comparator
     * @since 0.0.1
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Comparator<T> getComparator(Class<T> beanClass,boolean direction,String ...sort){
        return getComparator(beanClass, List.of(sort).map(s->(Tuple2) Tuple.of(direction, s)).toJavaArray(Tuple2.class));
    }

    /**
     * Retourne la valeur d'un champ de bean
     * @param bean Bean à traiter
     * @param fieldName Nom du champ
     * @return La valeur
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    public static <T,R> R getFieldValue(T bean,String fieldName){
        return bean!=null && fieldName!=null ? (R)Try.of(()->getBeanInfo(bean.getClass()).getField(fieldName).get(bean)).getOrElse((R)null) : null;
    }

    /**
     * Positionne la valeur d'un champ de bean
     * @param bean Bean à traiter
     * @param fieldName Nom du champ
     * @param value Valeur à positionner
     * @return
     * @since 0.0.1
     */
    @SneakyThrows
    public static <T,R> T setFieldValue(T bean,String fieldName, R value) {
        if (bean!=null && fieldName!=null) {
            getBeanInfo(bean.getClass()).getField(fieldName).set(bean, value);
        }
        return bean;
    }

    /**
     * Retourne un champ dans une classe. La recherche peut se faire hiérarchiquement
     * @param beanClass Classe du bean
     * @param fieldName Nom du champ à rechercher
     * @return Field
     * @since 1.0.2
     */
    public static <T> Field getField(Class<T> beanClass, String fieldName) {
        return beanClass!=null && fieldName!=null ? getBeanInfo(beanClass).getField(fieldName) : null;
    }

    // ------------- Méthodes statiques privées -------------

    /**
     * Retourne les informations liées au bean
     * @param beanClass Classe de bean
     * @return
     * @since 0.0.1
     */
    @SuppressWarnings("unchecked")
    private static <T> BeanInfo<T> getBeanInfo(Class<T> beanClass){
        synchronized(BeansHelper.class){
            Tuple2<BeanInfo<?>,Map<Class<?>, BeanInfo<?>>> result = (Tuple2<BeanInfo<?>, Map<Class<?>, BeanInfo<?>>>) beansMap.computeIfAbsent(beanClass, BeanInfo::new);
            beansMap = result._2();
            return (BeanInfo<T>) result._1();
        }
    }

    // ---------------------- Classes internes ----------------
    /**
     * Informations sur un bean
     *
     * @author ccr
     * @since 0.0.1
     */
    private static class BeanInfo<T> {

        // Membres internes
        private Class<T>                            beanClass;
        private Map<String, Field>                  beanFields;

        /**
         * Constructeur
         * @param beanClass Classe du bean
         */
        public BeanInfo(Class<T> beanClass){
            this.beanClass = beanClass;
            this.beanFields = HashMap.empty();
        }

        // ----------------------- Méthodes publiques ---------------------
        /**
         * Construit un comparateur sur le bean, basé sur les champs et l'ordre fournis en paramètre
         * @param sort Informations de tri sous la forme d'un ensemble de tuples contenant l'ordre (true=ascendant,false=descendant) et le champ
         * @return Comparator
         * @since 0.0.1
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Comparator<T> getComparator(Tuple2<Boolean, String> ... sort){
            Seq<Tuple2<Boolean, Field>> fields = List.of(sort)
                    .map(s->s.map2(this::getField))
                    .filter(s->s._2()!=null && Comparable.class.isAssignableFrom(s._2().getType()));
            return (b1,b2)->
                    fields.foldLeft(0, (result,field)->{
                        if (result==0){
                            result = Try.of(()->{
                                Comparable c1 = (Comparable)(field._2().get(b1));
                                Comparable c2 = (Comparable)(field._2().get(b2));
                                if (c1==null){
                                    if (c2==null) return 0;
                                    return field._1() ? -1 : 1;
                                }
                                else if (c2==null){
                                    return field._1() ? 1 : -1;
                                }
                                return field._1() ? c1.compareTo(c2) : c2.compareTo(c1);
                            }).getOrElse(0);
                        }
                        return result;
                    });

        }

        /**
         * Trouve le champ
         * @param name Nom du champ
         * @return Field
         * @since 0.0.1
         */
        public Field getField(String name){
            Field result = this.beanFields.getOrElse(name, null);
            if (result==null){
                result = this.findFieldInHierarchy(beanClass, name)
                                .onSuccess(f->{f.setAccessible(true);this.beanFields = this.beanFields.put(name, f);})
                                .getOrElse((Field)null);
            }
            return result;
        }

        // --------------------------------------- Méthodes privées ---------------------------------------------
        /**
         * Trouve un champ dans la hiérarchie
         * @param clazz Classe de rechercher
         * @param name Nom du champ
         * @return Try<Field>
         * @since 1.0.2
         */
        private Try<Field> findFieldInHierarchy(Class<?> clazz, String name) {
            return Try.of(()-> clazz.getDeclaredField(name))
                    .recoverWith(e -> Option
                            .of(clazz.getSuperclass())
                            .map(sc -> this.findFieldInHierarchy(sc,name))
                            .getOrElse(Try.failure(e)));
        }


    }
}

/*****************************************************************
 * Copyright (c) 2017 EcoleTree. All Rights Reserved.
 *
 * Author : Hyungseok Kim
 * Create Date : 2018. 7. 26.
 * DESC : Map 객체 생성 유틸
 *****************************************************************/
package kr.co.ecoletree.common.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MapBuilder<K, V> {
    private final Map<K, V> map;

    private MapBuilder() { map = new HashMap<K,V>(); }
    private MapBuilder(final Map<K, V> map) { this.map = new HashMap<K,V>(map); }

    /**
     * Immediate method puts a key-value pair.
     * @param key
     * @param value
     * @return
     */
    public MapBuilder<K, V> put(final K key, final V value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * Immediate method puts a key-value pair from key-value Suppliers.
     *
     * @param keySupplier
     * @param valueSupplier
     * @return
     */
    public MapBuilder<K, V> put(final Supplier<Optional<K>> keySupplier, final Supplier<Optional<V>> valueSupplier) {
        final Optional<K> maybeKey = keySupplier.get();
        final Optional<V> maybeValue = valueSupplier.get();
        if (maybeKey.isPresent() && maybeValue.isPresent()) {
            return put(maybeKey.get(), maybeValue.get());
        }
        return this;
    }

    /**
     * Immediate method puts a key-value pair when {@link Predicate#test(Object)} is true.
     * @param key
     * @param value
     * @param kvPredicate
     * @return
             */
    public MapBuilder<K, V> putIfPresent(final K key, final V value, final BiPredicate<K, V> kvPredicate) {
        if (kvPredicate.test(key, value)) return put(key, value);
        else return this;
    }

    /**
     * Immediate method put all key-value pairs in map.
     * @param map
     * @return
     */
    public MapBuilder<K, V> putAll(final Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    /**
     * Generate a new HashMap.
     *
     * @return
     */
    public Map<K, V> build() {
        return map;
    }

    /**
     * Create MapBuilder with parameterizedTypes.
     * If you don`t clarify(define or declare) Type parameter than this builder will generate a {@link HashMap<Object, Object>}
     *
     * @param <K> - The type parameter for map`s key.
     * @param <V> - The type parameter for map`s value.
     * @return
     */
    public static <K, V> MapBuilder<K, V> createBuilder() {
        return new MapBuilder<K, V>();
    }

    /**
     * Create MapBuilder with a pair of key and value.
     *
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> MapBuilder<K, V> createBuilder(final K key, final V value) {
        return new MapBuilder<K, V>().put(key, value);
    }

    /**
     * Create MapBuilder with another Map.
     *
     * @param map - Generate MapBuilder contains this map`s every entries {@link Map.Entry}.
     * @param <K> - The type parameter for Map`s key
     * @param <V> - The type parameter for Map`s value
     * @return
     */
    public static <K, V> MapBuilder<K, V> createBuilder(final Map<K, V> map) {
        return new MapBuilder<K, V>(map);
    }

    // Factory Methods
    // Key-value pair(s) 1 ~ 10
    /**
     * Generate a new HashMap with another map.
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> of(final Map<K, V> map) {
        return createBuilder(map).build();
    }

    /**
     * Generate a new HashMap with a pair of key and value.
     *
     * @param k - key
     * @param v - value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> of(final K k, final V v) {
        return createBuilder(k, v).build();
    }

    /**
     * Generate a new HashMap with two pairs of key and value.
     *
     * @param k1 - key 1
     * @param v1 - value 1
     * @param k2 - key 2
     * @param v2 - value 2
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2)
    {
        return createBuilder(k1, v1).put(k2, v2).build();
    }

    /**
     * Generate a new HashMap with three pairs of key and value.
     * @param k1 - key 1
     * @param v1 - value 1
     * @param k2 - key 2
     * @param v2 - value 2
     * @param k3 - key 3
     * @param v3 - value 3
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).build();
    }

    /**
     * Generate a new HashMap with four pairs of key and value.
     *
     * @param k1 - key 1
     * @param v1 - value 1
     * @param k2 - key 2
     * @param v2 - value 2
     * @param k3 - key 3
     * @param v3 - value 3
     * @param k4 - key 4
     * @param v4 - value 4
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3
                                    , final K k4, final V v4)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
            , final K k2, final V v2
            , final K k3, final V v3
            , final K k4, final V v4
            , final K k5, final V v5)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3
                                    , final K k4, final V v4
                                    , final K k5, final V v5
                                    , final K k6, final V v6)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3
                                    , final K k4, final V v4
                                    , final K k5, final V v5
                                    , final K k6, final V v6
                                    , final K k7, final V v7)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).put(k7, v7).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3
                                    , final K k4, final V v4
                                    , final K k5, final V v5
                                    , final K k6, final V v6
                                    , final K k7, final V v7
                                    , final K k8, final V v8)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).put(k7, v7).put(k8, v8).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3
                                    , final K k4, final V v4
                                    , final K k5, final V v5
                                    , final K k6, final V v6
                                    , final K k7, final V v7
                                    , final K k8, final V v8
                                    , final K k9, final V v9)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).put(k7, v7).put(k8, v8).put(k9, v9).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
                                    , final K k2, final V v2
                                    , final K k3, final V v3
                                    , final K k4, final V v4
                                    , final K k5, final V v5
                                    , final K k6, final V v6
                                    , final K k7, final V v7
                                    , final K k8, final V v8
                                    , final K k9, final V v9
                                    , final K k10, final V v10)
    {
        return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).put(k7, v7).put(k8, v8).put(k9, v9).put(k10, v10).build();
    }
    public static <K, V> Map<K, V> of(final K k1, final V v1
						    		, final K k2, final V v2
						    		, final K k3, final V v3
						    		, final K k4, final V v4
						    		, final K k5, final V v5
						    		, final K k6, final V v6
						    		, final K k7, final V v7
						    		, final K k8, final V v8
						    		, final K k9, final V v9
						    		, final K k10, final V v10
						    		, final K k11, final V v11
						    		, final K k12, final V v12
						    		, final K k13, final V v13)
    {
    	return createBuilder(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).put(k6, v6).put(k7, v7).put(k8, v8).put(k9, v9).put(k10, v10).put(k11, v11).put(k12, v12).put(k13, v13).build();
    }
}

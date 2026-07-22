package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class LinkedHashtable<K, V> extends Hashtable<K, V> {
    private static final long serialVersionUID = 1;
    private final LinkedHashMap<K, V> map;

    public LinkedHashtable() {
        this.map = new LinkedHashMap<>();
    }

    public LinkedHashtable(int i) {
        this.map = new LinkedHashMap<>(i);
    }

    public LinkedHashtable(int i, float f) {
        this.map = new LinkedHashMap<>(i, f);
    }

    public LinkedHashtable(Map<K, V> map) {
        this.map = new LinkedHashMap<>(map);
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized void clear() {
        this.map.clear();
    }

    @Override // java.util.Hashtable
    public boolean contains(Object obj) {
        return containsKey(obj);
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized boolean containsKey(Object obj) {
        return this.map.containsKey(obj);
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized boolean containsValue(Object obj) {
        return this.map.containsValue(obj);
    }

    @Override // java.util.Hashtable, java.util.Dictionary
    public Enumeration<V> elements() {
        return CollectionUtils.asEnumeration(values().iterator());
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized boolean equals(Object obj) {
        return this.map.equals(obj);
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized V get(Object obj) {
        return this.map.get(obj);
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized int hashCode() {
        return this.map.hashCode();
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.Hashtable, java.util.Dictionary
    public Enumeration<K> keys() {
        return CollectionUtils.asEnumeration(keySet().iterator());
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized Set<K> keySet() {
        return this.map.keySet();
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized V put(K k, V v) {
        return this.map.put(k, v);
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized void putAll(Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized V remove(Object obj) {
        return this.map.remove(obj);
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public synchronized int size() {
        return this.map.size();
    }

    @Override // java.util.Hashtable
    public synchronized String toString() {
        return this.map.toString();
    }

    @Override // java.util.Hashtable, java.util.Map
    public synchronized Collection<V> values() {
        return this.map.values();
    }
}

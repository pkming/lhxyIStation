package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public class CollectionUtils {

    @Deprecated
    public static final List EMPTY_LIST = Collections.EMPTY_LIST;

    public static boolean equals(Vector<?> vector, Vector<?> vector2) {
        if (vector == vector2) {
            return true;
        }
        if (vector == null || vector2 == null) {
            return false;
        }
        return vector.equals(vector2);
    }

    public static boolean equals(Dictionary<?, ?> dictionary, Dictionary<?, ?> dictionary2) {
        if (dictionary == dictionary2) {
            return true;
        }
        if (dictionary == null || dictionary2 == null || dictionary.size() != dictionary2.size()) {
            return false;
        }
        Enumeration<?> enumerationKeys = dictionary.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            Object obj = dictionary.get(objNextElement);
            Object obj2 = dictionary2.get(objNextElement);
            if (obj2 == null || !obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    public static String flattenToString(Collection<?> collection) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : collection) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(obj);
        }
        return sb.toString();
    }

    public static <K, V> void putAll(Dictionary<? super K, ? super V> dictionary, Dictionary<? extends K, ? extends V> dictionary2) {
        Enumeration<? extends K> enumerationKeys = dictionary2.keys();
        while (enumerationKeys.hasMoreElements()) {
            K kNextElement = enumerationKeys.nextElement();
            dictionary.put(kNextElement, dictionary2.get(kNextElement));
        }
    }

    public static final class EmptyEnumeration<E> implements Enumeration<E> {
        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        @Override // java.util.Enumeration
        public E nextElement() throws NoSuchElementException {
            throw new NoSuchElementException();
        }
    }

    public static <E> Enumeration<E> append(Enumeration<E> enumeration, Enumeration<E> enumeration2) {
        return new CompoundEnumeration(enumeration, enumeration2);
    }

    public static <E> Enumeration<E> asEnumeration(final Iterator<E> it) {
        return new Enumeration<E>() { // from class: org.apache.tools.ant.util.CollectionUtils.1
            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override // java.util.Enumeration
            public E nextElement() {
                return (E) it.next();
            }
        };
    }

    public static <E> Iterator<E> asIterator(final Enumeration<E> enumeration) {
        return new Iterator<E>() { // from class: org.apache.tools.ant.util.CollectionUtils.2
            @Override // java.util.Iterator
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override // java.util.Iterator
            public E next() {
                return (E) enumeration.nextElement();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Collection<T> asCollection(Iterator<? extends T> it) {
        ArrayList arrayList = new ArrayList();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        return arrayList;
    }

    private static final class CompoundEnumeration<E> implements Enumeration<E> {
        private final Enumeration<E> e1;
        private final Enumeration<E> e2;

        public CompoundEnumeration(Enumeration<E> enumeration, Enumeration<E> enumeration2) {
            this.e1 = enumeration;
            this.e2 = enumeration2;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.e1.hasMoreElements() || this.e2.hasMoreElements();
        }

        @Override // java.util.Enumeration
        public E nextElement() throws NoSuchElementException {
            if (this.e1.hasMoreElements()) {
                return this.e1.nextElement();
            }
            return this.e2.nextElement();
        }
    }

    public static int frequency(Collection<?> collection, Object obj) {
        int i = 0;
        if (collection != null) {
            for (Object obj2 : collection) {
                if (obj == null) {
                    if (obj2 == null) {
                        i++;
                    }
                } else if (obj.equals(obj2)) {
                    i++;
                }
            }
        }
        return i;
    }
}

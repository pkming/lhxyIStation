package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/* JADX INFO: loaded from: classes3.dex */
public class IdentityStack<E> extends Stack<E> {
    private static final long serialVersionUID = -5555522620060077046L;

    public static <E> IdentityStack<E> getInstance(Stack<E> stack) {
        if (stack instanceof IdentityStack) {
            return (IdentityStack) stack;
        }
        IdentityStack<E> identityStack = new IdentityStack<>();
        if (stack != null) {
            identityStack.addAll(stack);
        }
        return identityStack;
    }

    public IdentityStack() {
    }

    public IdentityStack(E e) {
        push(e);
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    @Override // java.util.Vector
    public synchronized int indexOf(Object obj, int i) {
        int size = size();
        while (i < size) {
            if (get(i) == obj) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override // java.util.Vector
    public synchronized int lastIndexOf(Object obj, int i) {
        while (i >= 0) {
            if (get(i) == obj) {
                return i;
            }
            i--;
        }
        return -1;
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean removeAll(Collection<?> collection) {
        if (!(collection instanceof Set)) {
            collection = new HashSet((Collection<? extends Object>) collection);
        }
        return super.removeAll(collection);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean retainAll(Collection collection) {
        if (!(collection instanceof Set)) {
            collection = new HashSet(collection);
        }
        return super.retainAll(collection);
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean containsAll(Collection<?> collection) {
        IdentityHashMap identityHashMap;
        identityHashMap = new IdentityHashMap();
        Iterator it = iterator();
        while (it.hasNext()) {
            identityHashMap.put(it.next(), Boolean.TRUE);
        }
        return identityHashMap.keySet().containsAll(collection);
    }
}

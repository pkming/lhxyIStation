package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public final class VectorSet<E> extends Vector<E> {
    private static final long serialVersionUID = 1;
    private final HashSet<E> set;

    public VectorSet() {
        this.set = new HashSet<>();
    }

    public VectorSet(int i) {
        super(i);
        this.set = new HashSet<>();
    }

    public VectorSet(int i, int i2) {
        super(i, i2);
        this.set = new HashSet<>();
    }

    public VectorSet(Collection<? extends E> collection) {
        this.set = new HashSet<>();
        if (collection != null) {
            Iterator<? extends E> it = collection.iterator();
            while (it.hasNext()) {
                add(it.next());
            }
        }
    }

    @Override // java.util.Vector, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean add(E e) {
        if (this.set.contains(e)) {
            return false;
        }
        doAdd(size(), e);
        return true;
    }

    @Override // java.util.Vector, java.util.AbstractList, java.util.List
    public void add(int i, E e) {
        doAdd(i, e);
    }

    private synchronized void doAdd(int i, E e) {
        if (this.set.add(e)) {
            int size = size();
            ensureCapacity(size + 1);
            if (i != size) {
                System.arraycopy(this.elementData, i, this.elementData, i + 1, size - i);
            }
            this.elementData[i] = e;
            this.elementCount++;
        }
    }

    @Override // java.util.Vector
    public synchronized void addElement(E e) {
        doAdd(size(), e);
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean addAll(Collection<? extends E> collection) {
        boolean zAdd;
        zAdd = false;
        Iterator<? extends E> it = collection.iterator();
        while (it.hasNext()) {
            zAdd |= add(it.next());
        }
        return zAdd;
    }

    @Override // java.util.Vector, java.util.AbstractList, java.util.List
    public synchronized boolean addAll(int i, Collection<? extends E> collection) {
        LinkedList linkedList = new LinkedList();
        for (E e : collection) {
            if (this.set.add(e)) {
                linkedList.add(e);
            }
        }
        if (linkedList.isEmpty()) {
            return false;
        }
        int size = size();
        ensureCapacity(linkedList.size() + size);
        if (i != size) {
            System.arraycopy(this.elementData, i, this.elementData, linkedList.size() + i, size - i);
        }
        Iterator<E> it = linkedList.iterator();
        while (it.hasNext()) {
            this.elementData[i] = it.next();
            i++;
        }
        this.elementCount += linkedList.size();
        return true;
    }

    @Override // java.util.Vector, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized void clear() {
        super.clear();
        this.set.clear();
    }

    @Override // java.util.Vector
    public Object clone() {
        VectorSet vectorSet = (VectorSet) super.clone();
        vectorSet.set.addAll(this.set);
        return vectorSet;
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean contains(Object obj) {
        return this.set.contains(obj);
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean containsAll(Collection<?> collection) {
        return this.set.containsAll(collection);
    }

    @Override // java.util.Vector
    public void insertElementAt(E e, int i) {
        doAdd(i, e);
    }

    @Override // java.util.Vector, java.util.AbstractList, java.util.List
    public synchronized E remove(int i) {
        E e;
        e = get(i);
        remove(e);
        return e;
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object obj) {
        return doRemove(obj);
    }

    private synchronized boolean doRemove(Object obj) {
        if (!this.set.remove(obj)) {
            return false;
        }
        int iIndexOf = indexOf(obj);
        if (iIndexOf < this.elementData.length - 1) {
            System.arraycopy(this.elementData, iIndexOf + 1, this.elementData, iIndexOf, (this.elementData.length - iIndexOf) - 1);
        }
        this.elementCount--;
        return true;
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean removeAll(Collection<?> collection) {
        boolean zRemove;
        zRemove = false;
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            zRemove |= remove(it.next());
        }
        return zRemove;
    }

    @Override // java.util.Vector
    public synchronized void removeAllElements() {
        this.set.clear();
        super.removeAllElements();
    }

    @Override // java.util.Vector
    public boolean removeElement(Object obj) {
        return doRemove(obj);
    }

    @Override // java.util.Vector
    public synchronized void removeElementAt(int i) {
        remove(get(i));
    }

    @Override // java.util.Vector, java.util.AbstractList
    public synchronized void removeRange(int i, int i2) {
        while (i2 > i) {
            i2--;
            remove(i2);
        }
    }

    @Override // java.util.Vector, java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized boolean retainAll(Collection<?> collection) {
        if (!(collection instanceof Set)) {
            collection = new HashSet((Collection<? extends Object>) collection);
        }
        LinkedList linkedList = new LinkedList();
        for (E e : this) {
            if (!collection.contains(e)) {
                linkedList.addLast(e);
            }
        }
        if (linkedList.isEmpty()) {
            return false;
        }
        removeAll(linkedList);
        return true;
    }

    @Override // java.util.Vector, java.util.AbstractList, java.util.List
    public synchronized E set(int i, E e) {
        E e2;
        e2 = get(i);
        if (this.set.add(e)) {
            this.elementData[i] = e;
            this.set.remove(e2);
        } else {
            int iIndexOf = indexOf(e);
            remove(e);
            remove(e2);
            if (iIndexOf <= i) {
                i--;
            }
            add(i, e);
        }
        return e2;
    }

    @Override // java.util.Vector
    public void setElementAt(E e, int i) {
        set(i, e);
    }
}

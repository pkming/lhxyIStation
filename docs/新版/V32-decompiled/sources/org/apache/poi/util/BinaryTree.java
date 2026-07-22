package org.apache.poi.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public final class BinaryTree extends AbstractMap {
    private static final int _INDEX_COUNT = 2;
    private static final int _INDEX_SUM = 1;
    private static final int _KEY = 0;
    private static final int _MINIMUM_INDEX = 0;
    private static final int _VALUE = 1;
    private static final String[] _data_name = {"key", "value"};
    private Set[] _entry_set;
    private Set[] _key_set;
    private int _modifications;
    private Node[] _root;
    private int _size;
    private Collection[] _value_collection;

    private int oppositeIndex(int i) {
        return 1 - i;
    }

    public BinaryTree() {
        this._root = new Node[]{null, null};
        this._size = 0;
        this._modifications = 0;
        this._key_set = new Set[]{null, null};
        this._entry_set = new Set[]{null, null};
        this._value_collection = new Collection[]{null, null};
    }

    public BinaryTree(Map map) throws IllegalArgumentException, ClassCastException, NullPointerException {
        this._root = new Node[]{null, null};
        this._size = 0;
        this._modifications = 0;
        this._key_set = new Set[]{null, null};
        this._entry_set = new Set[]{null, null};
        this._value_collection = new Collection[]{null, null};
        putAll(map);
    }

    public Object getKeyForValue(Object obj) throws ClassCastException, NullPointerException {
        return doGet((Comparable) obj, 1);
    }

    public Object removeValue(Object obj) {
        return doRemove((Comparable) obj, 1);
    }

    public Set entrySetByValue() {
        Set[] setArr = this._entry_set;
        if (setArr[1] == null) {
            setArr[1] = new AnonymousClass1();
        }
        return this._entry_set[1];
    }

    /* JADX INFO: renamed from: org.apache.poi.util.BinaryTree$1, reason: invalid class name */
    class AnonymousClass1 extends AbstractSet {
        AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator iterator() {
            return new BinaryTreeIterator(this, 1) { // from class: org.apache.poi.util.BinaryTree.2
                private final /* synthetic */ AnonymousClass1 this$1;

                {
                    BinaryTree binaryTree = BinaryTree.this;
                    this.this$1 = this;
                }

                @Override // org.apache.poi.util.BinaryTree.BinaryTreeIterator
                protected Object doGetNext() {
                    return this._last_returned_node;
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            Node nodeLookup = BinaryTree.this.lookup((Comparable) entry.getValue(), 1);
            return nodeLookup != null && nodeLookup.getData(0).equals(key);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            Node nodeLookup = BinaryTree.this.lookup((Comparable) entry.getValue(), 1);
            if (nodeLookup == null || !nodeLookup.getData(0).equals(key)) {
                return false;
            }
            BinaryTree.this.doRedBlackDelete(nodeLookup);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return BinaryTree.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            BinaryTree.this.clear();
        }
    }

    public Set keySetByValue() {
        Set[] setArr = this._key_set;
        if (setArr[1] == null) {
            setArr[1] = new AnonymousClass3();
        }
        return this._key_set[1];
    }

    /* JADX INFO: renamed from: org.apache.poi.util.BinaryTree$3, reason: invalid class name */
    class AnonymousClass3 extends AbstractSet {
        AnonymousClass3() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator iterator() {
            return new BinaryTreeIterator(this, 1) { // from class: org.apache.poi.util.BinaryTree.4
                private final /* synthetic */ AnonymousClass3 this$1;

                {
                    BinaryTree binaryTree = BinaryTree.this;
                    this.this$1 = this;
                }

                @Override // org.apache.poi.util.BinaryTree.BinaryTreeIterator
                protected Object doGetNext() {
                    return this._last_returned_node.getData(0);
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return BinaryTree.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return BinaryTree.this.containsKey(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            int i = BinaryTree.this._size;
            BinaryTree.this.remove(obj);
            return BinaryTree.this._size != i;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            BinaryTree.this.clear();
        }
    }

    public Collection valuesByValue() {
        Collection[] collectionArr = this._value_collection;
        if (collectionArr[1] == null) {
            collectionArr[1] = new AnonymousClass5();
        }
        return this._value_collection[1];
    }

    /* JADX INFO: renamed from: org.apache.poi.util.BinaryTree$5, reason: invalid class name */
    class AnonymousClass5 extends AbstractCollection {
        AnonymousClass5() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator iterator() {
            return new BinaryTreeIterator(this, 1) { // from class: org.apache.poi.util.BinaryTree.6
                private final /* synthetic */ AnonymousClass5 this$1;

                {
                    BinaryTree binaryTree = BinaryTree.this;
                    this.this$1 = this;
                }

                @Override // org.apache.poi.util.BinaryTree.BinaryTreeIterator
                protected Object doGetNext() {
                    return this._last_returned_node.getData(1);
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return BinaryTree.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object obj) {
            return BinaryTree.this.containsValue(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object obj) {
            int i = BinaryTree.this._size;
            BinaryTree.this.removeValue(obj);
            return BinaryTree.this._size != i;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean removeAll(Collection collection) {
            Iterator it = collection.iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (BinaryTree.this.removeValue(it.next()) != null) {
                    z = true;
                }
            }
            return z;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            BinaryTree.this.clear();
        }
    }

    private Object doRemove(Comparable comparable, int i) {
        Node nodeLookup = lookup(comparable, i);
        if (nodeLookup == null) {
            return null;
        }
        Comparable data = nodeLookup.getData(oppositeIndex(i));
        doRedBlackDelete(nodeLookup);
        return data;
    }

    private Object doGet(Comparable comparable, int i) {
        checkNonNullComparable(comparable, i);
        Node nodeLookup = lookup(comparable, i);
        if (nodeLookup == null) {
            return null;
        }
        return nodeLookup.getData(oppositeIndex(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node lookup(Comparable comparable, int i) {
        Node left = this._root[i];
        while (left != null) {
            int iCompare = compare(comparable, left.getData(i));
            if (iCompare == 0) {
                return left;
            }
            left = iCompare < 0 ? left.getLeft(i) : left.getRight(i);
        }
        return null;
    }

    private static int compare(Comparable comparable, Comparable comparable2) {
        return comparable.compareTo(comparable2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Node leastNode(Node node, int i) {
        if (node != null) {
            while (node.getLeft(i) != null) {
                node = node.getLeft(i);
            }
        }
        return node;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Node nextGreater(Node node, int i) {
        if (node == null) {
            return null;
        }
        if (node.getRight(i) != null) {
            return leastNode(node.getRight(i), i);
        }
        Node parent = node.getParent(i);
        while (true) {
            Node node2 = parent;
            Node node3 = node;
            node = node2;
            if (node == null || node3 != node.getRight(i)) {
                return node;
            }
            parent = node.getParent(i);
        }
    }

    private static void copyColor(Node node, Node node2, int i) {
        if (node2 != null) {
            if (node == null) {
                node2.setBlack(i);
            } else {
                node2.copyColor(node, i);
            }
        }
    }

    private static boolean isRed(Node node, int i) {
        if (node == null) {
            return false;
        }
        return node.isRed(i);
    }

    private static boolean isBlack(Node node, int i) {
        if (node == null) {
            return true;
        }
        return node.isBlack(i);
    }

    private static void makeRed(Node node, int i) {
        if (node != null) {
            node.setRed(i);
        }
    }

    private static void makeBlack(Node node, int i) {
        if (node != null) {
            node.setBlack(i);
        }
    }

    private static Node getGrandParent(Node node, int i) {
        return getParent(getParent(node, i), i);
    }

    private static Node getParent(Node node, int i) {
        if (node == null) {
            return null;
        }
        return node.getParent(i);
    }

    private static Node getRightChild(Node node, int i) {
        if (node == null) {
            return null;
        }
        return node.getRight(i);
    }

    private static Node getLeftChild(Node node, int i) {
        if (node == null) {
            return null;
        }
        return node.getLeft(i);
    }

    private static boolean isLeftChild(Node node, int i) {
        return node == null || (node.getParent(i) != null && node == node.getParent(i).getLeft(i));
    }

    private static boolean isRightChild(Node node, int i) {
        return node == null || (node.getParent(i) != null && node == node.getParent(i).getRight(i));
    }

    private void rotateLeft(Node node, int i) {
        Node right = node.getRight(i);
        node.setRight(right.getLeft(i), i);
        if (right.getLeft(i) != null) {
            right.getLeft(i).setParent(node, i);
        }
        right.setParent(node.getParent(i), i);
        if (node.getParent(i) == null) {
            this._root[i] = right;
        } else if (node.getParent(i).getLeft(i) == node) {
            node.getParent(i).setLeft(right, i);
        } else {
            node.getParent(i).setRight(right, i);
        }
        right.setLeft(node, i);
        node.setParent(right, i);
    }

    private void rotateRight(Node node, int i) {
        Node left = node.getLeft(i);
        node.setLeft(left.getRight(i), i);
        if (left.getRight(i) != null) {
            left.getRight(i).setParent(node, i);
        }
        left.setParent(node.getParent(i), i);
        if (node.getParent(i) == null) {
            this._root[i] = left;
        } else if (node.getParent(i).getRight(i) == node) {
            node.getParent(i).setRight(left, i);
        } else {
            node.getParent(i).setLeft(left, i);
        }
        left.setRight(node, i);
        node.setParent(left, i);
    }

    private void doRedBlackInsert(Node node, int i) {
        makeRed(node, i);
        while (node != null && node != this._root[i] && isRed(node.getParent(i), i)) {
            if (isLeftChild(getParent(node, i), i)) {
                Node rightChild = getRightChild(getGrandParent(node, i), i);
                if (isRed(rightChild, i)) {
                    makeBlack(getParent(node, i), i);
                    makeBlack(rightChild, i);
                    makeRed(getGrandParent(node, i), i);
                    node = getGrandParent(node, i);
                } else {
                    if (isRightChild(node, i)) {
                        node = getParent(node, i);
                        rotateLeft(node, i);
                    }
                    makeBlack(getParent(node, i), i);
                    makeRed(getGrandParent(node, i), i);
                    if (getGrandParent(node, i) != null) {
                        rotateRight(getGrandParent(node, i), i);
                    }
                }
            } else {
                Node leftChild = getLeftChild(getGrandParent(node, i), i);
                if (isRed(leftChild, i)) {
                    makeBlack(getParent(node, i), i);
                    makeBlack(leftChild, i);
                    makeRed(getGrandParent(node, i), i);
                    node = getGrandParent(node, i);
                } else {
                    if (isLeftChild(node, i)) {
                        node = getParent(node, i);
                        rotateRight(node, i);
                    }
                    makeBlack(getParent(node, i), i);
                    makeRed(getGrandParent(node, i), i);
                    if (getGrandParent(node, i) != null) {
                        rotateLeft(getGrandParent(node, i), i);
                    }
                }
            }
        }
        makeBlack(this._root[i], i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRedBlackDelete(Node node) {
        for (int i = 0; i < 2; i++) {
            if (node.getLeft(i) != null && node.getRight(i) != null) {
                swapPosition(nextGreater(node, i), node, i);
            }
            Node left = node.getLeft(i) != null ? node.getLeft(i) : node.getRight(i);
            if (left != null) {
                left.setParent(node.getParent(i), i);
                if (node.getParent(i) == null) {
                    this._root[i] = left;
                } else if (node == node.getParent(i).getLeft(i)) {
                    node.getParent(i).setLeft(left, i);
                } else {
                    node.getParent(i).setRight(left, i);
                }
                node.setLeft(null, i);
                node.setRight(null, i);
                node.setParent(null, i);
                if (isBlack(node, i)) {
                    doRedBlackDeleteFixup(left, i);
                }
            } else if (node.getParent(i) == null) {
                this._root[i] = null;
            } else {
                if (isBlack(node, i)) {
                    doRedBlackDeleteFixup(node, i);
                }
                if (node.getParent(i) != null) {
                    if (node == node.getParent(i).getLeft(i)) {
                        node.getParent(i).setLeft(null, i);
                    } else {
                        node.getParent(i).setRight(null, i);
                    }
                    node.setParent(null, i);
                }
            }
        }
        shrink();
    }

    private void doRedBlackDeleteFixup(Node node, int i) {
        while (node != this._root[i] && isBlack(node, i)) {
            if (isLeftChild(node, i)) {
                Node rightChild = getRightChild(getParent(node, i), i);
                if (isRed(rightChild, i)) {
                    makeBlack(rightChild, i);
                    makeRed(getParent(node, i), i);
                    rotateLeft(getParent(node, i), i);
                    rightChild = getRightChild(getParent(node, i), i);
                }
                if (isBlack(getLeftChild(rightChild, i), i) && isBlack(getRightChild(rightChild, i), i)) {
                    makeRed(rightChild, i);
                    node = getParent(node, i);
                } else {
                    if (isBlack(getRightChild(rightChild, i), i)) {
                        makeBlack(getLeftChild(rightChild, i), i);
                        makeRed(rightChild, i);
                        rotateRight(rightChild, i);
                        rightChild = getRightChild(getParent(node, i), i);
                    }
                    copyColor(getParent(node, i), rightChild, i);
                    makeBlack(getParent(node, i), i);
                    makeBlack(getRightChild(rightChild, i), i);
                    rotateLeft(getParent(node, i), i);
                    node = this._root[i];
                }
            } else {
                Node leftChild = getLeftChild(getParent(node, i), i);
                if (isRed(leftChild, i)) {
                    makeBlack(leftChild, i);
                    makeRed(getParent(node, i), i);
                    rotateRight(getParent(node, i), i);
                    leftChild = getLeftChild(getParent(node, i), i);
                }
                if (isBlack(getRightChild(leftChild, i), i) && isBlack(getLeftChild(leftChild, i), i)) {
                    makeRed(leftChild, i);
                    node = getParent(node, i);
                } else {
                    if (isBlack(getLeftChild(leftChild, i), i)) {
                        makeBlack(getRightChild(leftChild, i), i);
                        makeRed(leftChild, i);
                        rotateLeft(leftChild, i);
                        leftChild = getLeftChild(getParent(node, i), i);
                    }
                    copyColor(getParent(node, i), leftChild, i);
                    makeBlack(getParent(node, i), i);
                    makeBlack(getLeftChild(leftChild, i), i);
                    rotateRight(getParent(node, i), i);
                    node = this._root[i];
                }
            }
        }
        makeBlack(node, i);
    }

    private void swapPosition(Node node, Node node2, int i) {
        Node parent = node.getParent(i);
        Node left = node.getLeft(i);
        Node right = node.getRight(i);
        Node parent2 = node2.getParent(i);
        Node left2 = node2.getLeft(i);
        Node right2 = node2.getRight(i);
        boolean z = node.getParent(i) != null && node == node.getParent(i).getLeft(i);
        boolean z2 = node2.getParent(i) != null && node2 == node2.getParent(i).getLeft(i);
        if (node == parent2) {
            node.setParent(node2, i);
            if (z2) {
                node2.setLeft(node, i);
                node2.setRight(right, i);
            } else {
                node2.setRight(node, i);
                node2.setLeft(left, i);
            }
        } else {
            node.setParent(parent2, i);
            if (parent2 != null) {
                if (z2) {
                    parent2.setLeft(node, i);
                } else {
                    parent2.setRight(node, i);
                }
            }
            node2.setLeft(left, i);
            node2.setRight(right, i);
        }
        if (node2 == parent) {
            node2.setParent(node, i);
            if (z) {
                node.setLeft(node2, i);
                node.setRight(right2, i);
            } else {
                node.setRight(node2, i);
                node.setLeft(left2, i);
            }
        } else {
            node2.setParent(parent, i);
            if (parent != null) {
                if (z) {
                    parent.setLeft(node2, i);
                } else {
                    parent.setRight(node2, i);
                }
            }
            node.setLeft(left2, i);
            node.setRight(right2, i);
        }
        if (node.getLeft(i) != null) {
            node.getLeft(i).setParent(node, i);
        }
        if (node.getRight(i) != null) {
            node.getRight(i).setParent(node, i);
        }
        if (node2.getLeft(i) != null) {
            node2.getLeft(i).setParent(node2, i);
        }
        if (node2.getRight(i) != null) {
            node2.getRight(i).setParent(node2, i);
        }
        node.swapColors(node2, i);
        Node[] nodeArr = this._root;
        if (nodeArr[i] == node) {
            nodeArr[i] = node2;
        } else if (nodeArr[i] == node2) {
            nodeArr[i] = node;
        }
    }

    private static void checkNonNullComparable(Object obj, int i) {
        if (obj == null) {
            throw new NullPointerException(new StringBuffer().append(_data_name[i]).append(" cannot be null").toString());
        }
        if (!(obj instanceof Comparable)) {
            throw new ClassCastException(new StringBuffer().append(_data_name[i]).append(" must be Comparable").toString());
        }
    }

    private static void checkKey(Object obj) {
        checkNonNullComparable(obj, 0);
    }

    private static void checkValue(Object obj) {
        checkNonNullComparable(obj, 1);
    }

    private static void checkKeyAndValue(Object obj, Object obj2) {
        checkKey(obj);
        checkValue(obj2);
    }

    private void modify() {
        this._modifications++;
    }

    private void grow() {
        modify();
        this._size++;
    }

    private void shrink() {
        modify();
        this._size--;
    }

    private void insertValue(Node node) throws IllegalArgumentException {
        Node left = this._root[1];
        while (true) {
            int iCompare = compare(node.getData(1), left.getData(1));
            if (iCompare == 0) {
                throw new IllegalArgumentException(new StringBuffer().append("Cannot store a duplicate value (\"").append(node.getData(1)).append("\") in this Map").toString());
            }
            if (iCompare < 0) {
                if (left.getLeft(1) != null) {
                    left = left.getLeft(1);
                } else {
                    left.setLeft(node, 1);
                    node.setParent(left, 1);
                    doRedBlackInsert(node, 1);
                    return;
                }
            } else if (left.getRight(1) != null) {
                left = left.getRight(1);
            } else {
                left.setRight(node, 1);
                node.setParent(left, 1);
                doRedBlackInsert(node, 1);
                return;
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this._size;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) throws ClassCastException, NullPointerException {
        checkKey(obj);
        return lookup((Comparable) obj, 0) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object obj) {
        checkValue(obj);
        return lookup((Comparable) obj, 1) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object get(Object obj) throws ClassCastException, NullPointerException {
        return doGet((Comparable) obj, 0);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object put(Object obj, Object obj2) throws IllegalArgumentException, ClassCastException, NullPointerException {
        checkKeyAndValue(obj, obj2);
        Node left = this._root[0];
        if (left == null) {
            Node node = new Node((Comparable) obj, (Comparable) obj2);
            Node[] nodeArr = this._root;
            nodeArr[0] = node;
            nodeArr[1] = node;
            grow();
            return null;
        }
        while (true) {
            Comparable comparable = (Comparable) obj;
            int iCompare = compare(comparable, left.getData(0));
            if (iCompare == 0) {
                throw new IllegalArgumentException(new StringBuffer().append("Cannot store a duplicate key (\"").append(obj).append("\") in this Map").toString());
            }
            if (iCompare < 0) {
                if (left.getLeft(0) == null) {
                    Node node2 = new Node(comparable, (Comparable) obj2);
                    insertValue(node2);
                    left.setLeft(node2, 0);
                    node2.setParent(left, 0);
                    doRedBlackInsert(node2, 0);
                    grow();
                    return null;
                }
                left = left.getLeft(0);
            } else {
                if (left.getRight(0) == null) {
                    Node node3 = new Node(comparable, (Comparable) obj2);
                    insertValue(node3);
                    left.setRight(node3, 0);
                    node3.setParent(left, 0);
                    doRedBlackInsert(node3, 0);
                    grow();
                    return null;
                }
                left = left.getRight(0);
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Object remove(Object obj) {
        return doRemove((Comparable) obj, 0);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        modify();
        this._size = 0;
        Node[] nodeArr = this._root;
        nodeArr[0] = null;
        nodeArr[1] = null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set keySet() {
        Set[] setArr = this._key_set;
        if (setArr[0] == null) {
            setArr[0] = new AnonymousClass7();
        }
        return this._key_set[0];
    }

    /* JADX INFO: renamed from: org.apache.poi.util.BinaryTree$7, reason: invalid class name */
    class AnonymousClass7 extends AbstractSet {
        AnonymousClass7() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator iterator() {
            return new BinaryTreeIterator(this, 0) { // from class: org.apache.poi.util.BinaryTree.8
                private final /* synthetic */ AnonymousClass7 this$1;

                {
                    BinaryTree binaryTree = BinaryTree.this;
                    this.this$1 = this;
                }

                @Override // org.apache.poi.util.BinaryTree.BinaryTreeIterator
                protected Object doGetNext() {
                    return this._last_returned_node.getData(0);
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return BinaryTree.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return BinaryTree.this.containsKey(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            int i = BinaryTree.this._size;
            BinaryTree.this.remove(obj);
            return BinaryTree.this._size != i;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            BinaryTree.this.clear();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection values() {
        Collection[] collectionArr = this._value_collection;
        if (collectionArr[0] == null) {
            collectionArr[0] = new AnonymousClass9();
        }
        return this._value_collection[0];
    }

    /* JADX INFO: renamed from: org.apache.poi.util.BinaryTree$9, reason: invalid class name */
    class AnonymousClass9 extends AbstractCollection {
        AnonymousClass9() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator iterator() {
            return new BinaryTreeIterator(this, 0) { // from class: org.apache.poi.util.BinaryTree.10
                private final /* synthetic */ AnonymousClass9 this$1;

                {
                    BinaryTree binaryTree = BinaryTree.this;
                    this.this$1 = this;
                }

                @Override // org.apache.poi.util.BinaryTree.BinaryTreeIterator
                protected Object doGetNext() {
                    return this._last_returned_node.getData(1);
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return BinaryTree.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object obj) {
            return BinaryTree.this.containsValue(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object obj) {
            int i = BinaryTree.this._size;
            BinaryTree.this.removeValue(obj);
            return BinaryTree.this._size != i;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean removeAll(Collection collection) {
            Iterator it = collection.iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (BinaryTree.this.removeValue(it.next()) != null) {
                    z = true;
                }
            }
            return z;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            BinaryTree.this.clear();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set entrySet() {
        Set[] setArr = this._entry_set;
        if (setArr[0] == null) {
            setArr[0] = new AnonymousClass11();
        }
        return this._entry_set[0];
    }

    /* JADX INFO: renamed from: org.apache.poi.util.BinaryTree$11, reason: invalid class name */
    class AnonymousClass11 extends AbstractSet {
        AnonymousClass11() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator iterator() {
            return new BinaryTreeIterator(this, 0) { // from class: org.apache.poi.util.BinaryTree.12
                private final /* synthetic */ AnonymousClass11 this$1;

                {
                    BinaryTree binaryTree = BinaryTree.this;
                    this.this$1 = this;
                }

                @Override // org.apache.poi.util.BinaryTree.BinaryTreeIterator
                protected Object doGetNext() {
                    return this._last_returned_node;
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object value = entry.getValue();
            Node nodeLookup = BinaryTree.this.lookup((Comparable) entry.getKey(), 0);
            return nodeLookup != null && nodeLookup.getData(1).equals(value);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object value = entry.getValue();
            Node nodeLookup = BinaryTree.this.lookup((Comparable) entry.getKey(), 0);
            if (nodeLookup == null || !nodeLookup.getData(1).equals(value)) {
                return false;
            }
            BinaryTree.this.doRedBlackDelete(nodeLookup);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return BinaryTree.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            BinaryTree.this.clear();
        }
    }

    private abstract class BinaryTreeIterator implements Iterator {
        private int _expected_modifications;
        protected Node _last_returned_node = null;
        private Node _next_node;
        private int _type;

        protected abstract Object doGetNext();

        BinaryTreeIterator(int i) {
            this._type = i;
            this._expected_modifications = BinaryTree.this._modifications;
            Node[] nodeArr = BinaryTree.this._root;
            int i2 = this._type;
            this._next_node = BinaryTree.leastNode(nodeArr[i2], i2);
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this._next_node != null;
        }

        @Override // java.util.Iterator
        public final Object next() throws NoSuchElementException, ConcurrentModificationException {
            if (this._next_node != null) {
                if (BinaryTree.this._modifications != this._expected_modifications) {
                    throw new ConcurrentModificationException();
                }
                Node node = this._next_node;
                this._last_returned_node = node;
                this._next_node = BinaryTree.this.nextGreater(node, this._type);
                return doGetNext();
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public final void remove() throws IllegalStateException, ConcurrentModificationException {
            if (this._last_returned_node != null) {
                if (BinaryTree.this._modifications == this._expected_modifications) {
                    BinaryTree.this.doRedBlackDelete(this._last_returned_node);
                    this._expected_modifications++;
                    this._last_returned_node = null;
                    return;
                }
                throw new ConcurrentModificationException();
            }
            throw new IllegalStateException();
        }
    }

    private static final class Node implements Map.Entry {
        private Comparable[] _data;
        private int _hashcode;
        private Node[] _left = {null, null};
        private Node[] _right = {null, null};
        private Node[] _parent = {null, null};
        private boolean[] _black = {true, true};
        private boolean _calculated_hashcode = false;

        Node(Comparable comparable, Comparable comparable2) {
            this._data = new Comparable[]{comparable, comparable2};
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Comparable getData(int i) {
            return this._data[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLeft(Node node, int i) {
            this._left[i] = node;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node getLeft(int i) {
            return this._left[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRight(Node node, int i) {
            this._right[i] = node;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node getRight(int i) {
            return this._right[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setParent(Node node, int i) {
            this._parent[i] = node;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Node getParent(int i) {
            return this._parent[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void swapColors(Node node, int i) {
            boolean[] zArr = this._black;
            boolean z = zArr[i];
            boolean[] zArr2 = node._black;
            zArr[i] = z ^ zArr2[i];
            zArr2[i] = zArr2[i] ^ zArr[i];
            zArr[i] = zArr2[i] ^ zArr[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isBlack(int i) {
            return this._black[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isRed(int i) {
            return !this._black[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setBlack(int i) {
            this._black[i] = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRed(int i) {
            this._black[i] = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void copyColor(Node node, int i) {
            this._black[i] = node._black[i];
        }

        @Override // java.util.Map.Entry
        public Object getKey() {
            return this._data[0];
        }

        @Override // java.util.Map.Entry
        public Object getValue() {
            return this._data[1];
        }

        @Override // java.util.Map.Entry
        public Object setValue(Object obj) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            return this._data[0].equals(entry.getKey()) && this._data[1].equals(entry.getValue());
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            if (!this._calculated_hashcode) {
                this._hashcode = this._data[0].hashCode() ^ this._data[1].hashCode();
                this._calculated_hashcode = true;
            }
            return this._hashcode;
        }
    }
}

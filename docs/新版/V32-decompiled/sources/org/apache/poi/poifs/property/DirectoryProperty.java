package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes3.dex */
public class DirectoryProperty extends Property implements Parent {
    private List _children;
    private Set _children_names;

    /* JADX INFO: renamed from: org.apache.poi.poifs.property.DirectoryProperty$1, reason: invalid class name */
    class AnonymousClass1 {
    }

    @Override // org.apache.poi.poifs.property.Property
    public boolean isDirectory() {
        return true;
    }

    public DirectoryProperty(String str) {
        this._children = new ArrayList();
        this._children_names = new HashSet();
        setName(str);
        setSize(0);
        setPropertyType((byte) 1);
        setStartBlock(0);
        setNodeColor((byte) 1);
    }

    protected DirectoryProperty(int i, byte[] bArr, int i2) {
        super(i, bArr, i2);
        this._children = new ArrayList();
        this._children_names = new HashSet();
    }

    public boolean changeName(Property property, String str) {
        String name = property.getName();
        property.setName(str);
        String name2 = property.getName();
        if (this._children_names.contains(name2)) {
            property.setName(name);
            return false;
        }
        this._children_names.add(name2);
        this._children_names.remove(name);
        return true;
    }

    public boolean deleteChild(Property property) {
        boolean zRemove = this._children.remove(property);
        if (zRemove) {
            this._children_names.remove(property.getName());
        }
        return zRemove;
    }

    private class PropertyComparator implements Comparator {
        @Override // java.util.Comparator
        public boolean equals(Object obj) {
            return this == obj;
        }

        private PropertyComparator() {
        }

        /* synthetic */ PropertyComparator(DirectoryProperty directoryProperty, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // java.util.Comparator
        public int compare(Object obj, Object obj2) {
            String name = ((Property) obj).getName();
            String name2 = ((Property) obj2).getName();
            int length = name.length() - name2.length();
            return length == 0 ? name.compareTo(name2) : length;
        }
    }

    @Override // org.apache.poi.poifs.property.Property
    protected void preWrite() {
        if (this._children.size() > 0) {
            Child[] childArr = (Property[]) this._children.toArray(new Property[0]);
            Arrays.sort(childArr, new PropertyComparator(this, null));
            int length = childArr.length / 2;
            setChildProperty(childArr[length].getIndex());
            childArr[0].setPreviousChild(null);
            childArr[0].setNextChild(null);
            for (int i = 1; i < length; i++) {
                childArr[i].setPreviousChild(childArr[i - 1]);
                childArr[i].setNextChild(null);
            }
            if (length != 0) {
                childArr[length].setPreviousChild(childArr[length - 1]);
            }
            if (length != childArr.length - 1) {
                Property property = childArr[length];
                int i2 = length + 1;
                property.setNextChild(childArr[i2]);
                while (i2 < childArr.length - 1) {
                    childArr[i2].setPreviousChild(null);
                    Child child = childArr[i2];
                    i2++;
                    child.setNextChild(childArr[i2]);
                }
                childArr[childArr.length - 1].setPreviousChild(null);
                childArr[childArr.length - 1].setNextChild(null);
                return;
            }
            childArr[length].setNextChild(null);
        }
    }

    @Override // org.apache.poi.poifs.property.Parent
    public Iterator getChildren() {
        return this._children.iterator();
    }

    @Override // org.apache.poi.poifs.property.Parent
    public void addChild(Property property) throws IOException {
        String name = property.getName();
        if (this._children_names.contains(name)) {
            throw new IOException(new StringBuffer().append("Duplicate name \"").append(name).append("\"").toString());
        }
        this._children_names.add(name);
        this._children.add(property);
    }
}

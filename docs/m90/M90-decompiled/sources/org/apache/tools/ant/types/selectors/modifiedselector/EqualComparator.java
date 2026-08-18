package org.apache.tools.ant.types.selectors.modifiedselector;

import java.util.Comparator;

/* JADX INFO: loaded from: classes3.dex */
public class EqualComparator implements Comparator<Object> {
    public String toString() {
        return "EqualComparator";
    }

    @Override // java.util.Comparator
    public int compare(Object obj, Object obj2) {
        return obj == null ? obj2 == null ? 1 : 0 : !obj.equals(obj2) ? 1 : 0;
    }
}

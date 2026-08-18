package org.apache.tools.ant.types.selectors.modifiedselector;

import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public interface Cache {
    void delete();

    Object get(Object obj);

    boolean isValid();

    Iterator<String> iterator();

    void load();

    void put(Object obj, Object obj2);

    void save();
}

package org.apache.poi.poifs.dev;

import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public interface POIFSViewable {
    String getShortDescription();

    Object[] getViewableArray();

    Iterator getViewableIterator();

    boolean preferArray();
}

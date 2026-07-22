package org.apache.tools.ant.types;

import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public interface ResourceCollection extends Iterable<Resource> {
    boolean isFilesystemOnly();

    @Override // java.lang.Iterable
    Iterator<Resource> iterator();

    int size();
}

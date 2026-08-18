package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public interface Parent extends Child {
    void addChild(Property property) throws IOException;

    Iterator getChildren();

    @Override // org.apache.poi.poifs.property.Child
    void setNextChild(Child child);

    @Override // org.apache.poi.poifs.property.Child
    void setPreviousChild(Child child);
}

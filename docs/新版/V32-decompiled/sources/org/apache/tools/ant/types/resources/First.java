package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class First extends SizeLimitCollection {
    @Override // org.apache.tools.ant.types.resources.BaseResourceCollectionWrapper
    protected Collection<Resource> getCollection() {
        int validCount = getValidCount();
        Iterator<Resource> it = getResourceCollection().iterator();
        ArrayList arrayList = new ArrayList(validCount);
        for (int i = 0; i < validCount && it.hasNext(); i++) {
            arrayList.add(it.next());
        }
        return arrayList;
    }
}

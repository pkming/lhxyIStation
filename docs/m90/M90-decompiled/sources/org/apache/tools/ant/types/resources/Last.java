package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class Last extends SizeLimitCollection {
    @Override // org.apache.tools.ant.types.resources.BaseResourceCollectionWrapper
    protected Collection<Resource> getCollection() {
        int validCount = getValidCount();
        ResourceCollection resourceCollection = getResourceCollection();
        Iterator<Resource> it = resourceCollection.iterator();
        int size = resourceCollection.size();
        int i = validCount;
        while (i < size) {
            it.next();
            i++;
        }
        ArrayList arrayList = new ArrayList(validCount);
        while (it.hasNext()) {
            arrayList.add(it.next());
            i++;
        }
        int size2 = arrayList.size();
        if (size2 == validCount || (size < validCount && size2 == size)) {
            return arrayList;
        }
        String str = "Resource collection " + resourceCollection + " reports size " + size + " but returns " + i + " elements.";
        if (size2 > validCount) {
            log(str, 1);
            return arrayList.subList(size2 - validCount, size2);
        }
        throw new BuildException(str);
    }
}

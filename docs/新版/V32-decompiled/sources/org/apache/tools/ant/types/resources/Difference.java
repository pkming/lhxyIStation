package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class Difference extends BaseResourceCollectionContainer {
    @Override // org.apache.tools.ant.types.resources.BaseResourceCollectionContainer
    protected Collection<Resource> getCollection() {
        List<ResourceCollection> resourceCollections = getResourceCollections();
        int size = resourceCollections.size();
        if (size < 2) {
            throw new BuildException("The difference of " + size + " resource collection" + (size == 1 ? "" : "s") + " is undefined.");
        }
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        Iterator<ResourceCollection> it = resourceCollections.iterator();
        while (it.hasNext()) {
            for (Resource resource : it.next()) {
                if (hashSet.add(resource)) {
                    arrayList.add(resource);
                } else {
                    arrayList.remove(resource);
                }
            }
        }
        return arrayList;
    }
}

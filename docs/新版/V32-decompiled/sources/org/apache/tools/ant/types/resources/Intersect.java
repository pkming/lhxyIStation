package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class Intersect extends BaseResourceCollectionContainer {
    @Override // org.apache.tools.ant.types.resources.BaseResourceCollectionContainer
    protected Collection<Resource> getCollection() {
        List<ResourceCollection> resourceCollections = getResourceCollections();
        int size = resourceCollections.size();
        if (size < 2) {
            throw new BuildException("The intersection of " + size + " resource collection" + (size == 1 ? "" : "s") + " is undefined.");
        }
        ArrayList arrayList = new ArrayList();
        Iterator<ResourceCollection> it = resourceCollections.iterator();
        arrayList.addAll(collect(it.next()));
        while (it.hasNext()) {
            arrayList.retainAll(collect(it.next()));
        }
        return arrayList;
    }

    private List<Resource> collect(ResourceCollection resourceCollection) {
        ArrayList arrayList = new ArrayList();
        Iterator<Resource> it = resourceCollection.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        return arrayList;
    }
}

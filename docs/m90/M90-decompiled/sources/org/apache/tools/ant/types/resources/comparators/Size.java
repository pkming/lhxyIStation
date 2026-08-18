package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class Size extends ResourceComparator {
    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    protected int resourceCompare(Resource resource, Resource resource2) {
        long size = resource.getSize() - resource2.getSize();
        if (size > 0) {
            return 1;
        }
        return size == 0 ? 0 : -1;
    }
}

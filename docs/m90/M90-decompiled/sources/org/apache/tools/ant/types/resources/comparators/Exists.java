package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class Exists extends ResourceComparator {
    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    protected int resourceCompare(Resource resource, Resource resource2) {
        boolean zIsExists = resource.isExists();
        if (zIsExists == resource2.isExists()) {
            return 0;
        }
        return zIsExists ? 1 : -1;
    }
}

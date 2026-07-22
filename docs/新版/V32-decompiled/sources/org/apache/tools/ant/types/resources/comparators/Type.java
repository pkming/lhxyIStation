package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class Type extends ResourceComparator {
    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    protected int resourceCompare(Resource resource, Resource resource2) {
        boolean zIsDirectory = resource.isDirectory();
        if (zIsDirectory == resource2.isDirectory()) {
            return 0;
        }
        return zIsDirectory ? 1 : -1;
    }
}

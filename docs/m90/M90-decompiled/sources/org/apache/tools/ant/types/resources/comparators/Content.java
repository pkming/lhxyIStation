package org.apache.tools.ant.types.resources.comparators;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.ResourceUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Content extends ResourceComparator {
    private boolean binary = true;

    public void setBinary(boolean z) {
        this.binary = z;
    }

    public boolean isBinary() {
        return this.binary;
    }

    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    protected int resourceCompare(Resource resource, Resource resource2) {
        try {
            return ResourceUtils.compareContent(resource, resource2, !this.binary);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }
}

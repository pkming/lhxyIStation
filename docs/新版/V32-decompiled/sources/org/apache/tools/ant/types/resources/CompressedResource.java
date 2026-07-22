package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public abstract class CompressedResource extends ContentTransformingResource {
    protected abstract String getCompressionName();

    protected CompressedResource() {
    }

    protected CompressedResource(ResourceCollection resourceCollection) {
        addConfigured(resourceCollection);
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public String toString() {
        return getCompressionName() + " compressed " + super.toString();
    }
}

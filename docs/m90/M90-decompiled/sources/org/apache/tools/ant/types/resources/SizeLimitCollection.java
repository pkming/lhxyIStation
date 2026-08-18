package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SizeLimitCollection extends BaseResourceCollectionWrapper {
    private static final String BAD_COUNT = "size-limited collection count should be set to an int >= 0";
    private int count = 1;

    public synchronized void setCount(int i) {
        checkAttributesAllowed();
        this.count = i;
    }

    public synchronized int getCount() {
        return this.count;
    }

    @Override // org.apache.tools.ant.types.resources.AbstractResourceCollectionWrapper, org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        int size;
        size = getResourceCollection().size();
        int validCount = getValidCount();
        if (size >= validCount) {
            size = validCount;
        }
        return size;
    }

    protected int getValidCount() {
        int count = getCount();
        if (count >= 0) {
            return count;
        }
        throw new BuildException(BAD_COUNT);
    }
}

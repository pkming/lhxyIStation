package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractResourceCollectionWrapper extends DataType implements ResourceCollection, Cloneable {
    private static final String ONE_NESTED_MESSAGE = " expects exactly one nested resource collection.";
    private boolean cache = true;
    private ResourceCollection rc;

    protected abstract Iterator<Resource> createIterator();

    protected abstract int getSize();

    public synchronized void setCache(boolean z) {
        this.cache = z;
    }

    public synchronized boolean isCache() {
        return this.cache;
    }

    public synchronized void add(ResourceCollection resourceCollection) throws BuildException {
        Project project;
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (resourceCollection == null) {
            return;
        }
        if (this.rc != null) {
            throw oneNested();
        }
        this.rc = resourceCollection;
        if (Project.getProject(resourceCollection) == null && (project = getProject()) != null) {
            project.setProjectReference(this.rc);
        }
        setChecked(false);
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public final synchronized Iterator<Resource> iterator() {
        if (isReference()) {
            return ((AbstractResourceCollectionWrapper) getCheckedRef()).iterator();
        }
        dieOnCircularReference();
        return new FailFast(this, createIterator());
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        if (isReference()) {
            return ((AbstractResourceCollectionWrapper) getCheckedRef()).size();
        }
        dieOnCircularReference();
        return getSize();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized boolean isFilesystemOnly() {
        if (isReference()) {
            return ((BaseResourceCollectionContainer) getCheckedRef()).isFilesystemOnly();
        }
        dieOnCircularReference();
        ResourceCollection resourceCollection = this.rc;
        if (resourceCollection != null && !resourceCollection.isFilesystemOnly()) {
            Iterator<Resource> it = iterator();
            while (it.hasNext()) {
                if (it.next().as(FileProvider.class) == null) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            Object obj = this.rc;
            if (obj instanceof DataType) {
                pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
            }
            setChecked(true);
        }
    }

    protected final synchronized ResourceCollection getResourceCollection() {
        ResourceCollection resourceCollection;
        dieOnCircularReference();
        resourceCollection = this.rc;
        if (resourceCollection == null) {
            throw oneNested();
        }
        return resourceCollection;
    }

    @Override // org.apache.tools.ant.types.DataType
    public synchronized String toString() {
        if (isReference()) {
            return getCheckedRef().toString();
        }
        if (getSize() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Resource resource : this) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(resource);
        }
        return sb.toString();
    }

    private BuildException oneNested() {
        return new BuildException(super.toString() + ONE_NESTED_MESSAGE);
    }
}

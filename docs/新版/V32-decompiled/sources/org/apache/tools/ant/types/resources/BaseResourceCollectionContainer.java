package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseResourceCollectionContainer extends DataType implements ResourceCollection, Cloneable {
    private List<ResourceCollection> rc = new ArrayList();
    private Collection<Resource> coll = null;
    private boolean cache = true;

    protected abstract Collection<Resource> getCollection();

    public BaseResourceCollectionContainer() {
    }

    public BaseResourceCollectionContainer(Project project) {
        setProject(project);
    }

    public synchronized void setCache(boolean z) {
        this.cache = z;
    }

    public synchronized boolean isCache() {
        return this.cache;
    }

    public synchronized void clear() throws BuildException {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.rc.clear();
        FailFast.invalidate(this);
        this.coll = null;
        setChecked(false);
    }

    public synchronized void add(ResourceCollection resourceCollection) throws BuildException {
        Project project;
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (resourceCollection == null) {
            return;
        }
        if (Project.getProject(resourceCollection) == null && (project = getProject()) != null) {
            project.setProjectReference(resourceCollection);
        }
        this.rc.add(resourceCollection);
        FailFast.invalidate(this);
        this.coll = null;
        setChecked(false);
    }

    public synchronized void addAll(Collection<? extends ResourceCollection> collection) throws BuildException {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        try {
            Iterator<? extends ResourceCollection> it = collection.iterator();
            while (it.hasNext()) {
                add(it.next());
            }
        } catch (ClassCastException e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public final synchronized Iterator<Resource> iterator() {
        if (isReference()) {
            return ((BaseResourceCollectionContainer) getCheckedRef()).iterator();
        }
        dieOnCircularReference();
        return new FailFast(this, cacheCollection().iterator());
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        if (isReference()) {
            return ((BaseResourceCollectionContainer) getCheckedRef(BaseResourceCollectionContainer.class, getDataTypeName())).size();
        }
        dieOnCircularReference();
        return cacheCollection().size();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized boolean isFilesystemOnly() {
        if (isReference()) {
            return ((BaseResourceCollectionContainer) getCheckedRef()).isFilesystemOnly();
        }
        dieOnCircularReference();
        Iterator<ResourceCollection> it = this.rc.iterator();
        boolean zIsFilesystemOnly = true;
        while (zIsFilesystemOnly && it.hasNext()) {
            zIsFilesystemOnly = it.next().isFilesystemOnly();
        }
        if (zIsFilesystemOnly) {
            return true;
        }
        Iterator<Resource> it2 = cacheCollection().iterator();
        while (it2.hasNext()) {
            if (it2.next().as(FileProvider.class) == null) {
                return false;
            }
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
            for (Object obj : this.rc) {
                if (obj instanceof DataType) {
                    pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
                }
            }
            setChecked(true);
        }
    }

    public final synchronized List<ResourceCollection> getResourceCollections() {
        dieOnCircularReference();
        return Collections.unmodifiableList(this.rc);
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        try {
            BaseResourceCollectionContainer baseResourceCollectionContainer = (BaseResourceCollectionContainer) super.clone();
            baseResourceCollectionContainer.rc = new ArrayList(this.rc);
            baseResourceCollectionContainer.coll = null;
            return baseResourceCollectionContainer;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.types.DataType
    public synchronized String toString() {
        if (isReference()) {
            return getCheckedRef().toString();
        }
        if (cacheCollection().size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Resource resource : this.coll) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(resource);
        }
        return sb.toString();
    }

    private synchronized Collection<Resource> cacheCollection() {
        if (this.coll == null || !isCache()) {
            this.coll = getCollection();
        }
        return this.coll;
    }
}

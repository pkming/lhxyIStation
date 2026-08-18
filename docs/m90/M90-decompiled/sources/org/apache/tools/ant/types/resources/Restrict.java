package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer;

/* JADX INFO: loaded from: classes3.dex */
public class Restrict extends ResourceSelectorContainer implements ResourceCollection {
    private LazyResourceCollectionWrapper w = new LazyResourceCollectionWrapper() { // from class: org.apache.tools.ant.types.resources.Restrict.1
        @Override // org.apache.tools.ant.types.resources.LazyResourceCollectionWrapper
        protected boolean filterResource(Resource resource) {
            Iterator<ResourceSelector> selectors = Restrict.this.getSelectors();
            while (selectors.hasNext()) {
                if (!selectors.next().isSelected(resource)) {
                    return true;
                }
            }
            return false;
        }
    };

    public synchronized void add(ResourceCollection resourceCollection) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (resourceCollection == null) {
            return;
        }
        this.w.add(resourceCollection);
        setChecked(false);
    }

    public synchronized void setCache(boolean z) {
        this.w.setCache(z);
    }

    public synchronized boolean isCache() {
        return this.w.isCache();
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer
    public synchronized void add(ResourceSelector resourceSelector) {
        if (resourceSelector == null) {
            return;
        }
        super.add(resourceSelector);
        FailFast.invalidate(this);
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public final synchronized Iterator<Resource> iterator() {
        if (isReference()) {
            return ((Restrict) getCheckedRef()).iterator();
        }
        dieOnCircularReference();
        return this.w.iterator();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        if (isReference()) {
            return ((Restrict) getCheckedRef()).size();
        }
        dieOnCircularReference();
        return this.w.size();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized boolean isFilesystemOnly() {
        if (isReference()) {
            return ((Restrict) getCheckedRef()).isFilesystemOnly();
        }
        dieOnCircularReference();
        return this.w.isFilesystemOnly();
    }

    @Override // org.apache.tools.ant.types.DataType
    public synchronized String toString() {
        if (isReference()) {
            return getCheckedRef().toString();
        }
        dieOnCircularReference();
        return this.w.toString();
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer, org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) {
        if (isChecked()) {
            return;
        }
        super.dieOnCircularReference(stack, project);
        if (!isReference()) {
            pushAndInvokeCircularReferenceCheck(this.w, stack, project);
            setChecked(true);
        }
    }
}

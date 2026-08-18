package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.CollectionUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Resources extends DataType implements ResourceCollection {
    private boolean cache = false;
    private Collection<Resource> coll;
    private Vector<ResourceCollection> rc;
    public static final ResourceCollection NONE = new ResourceCollection() { // from class: org.apache.tools.ant.types.resources.Resources.1
        @Override // org.apache.tools.ant.types.ResourceCollection
        public boolean isFilesystemOnly() {
            return true;
        }

        @Override // org.apache.tools.ant.types.ResourceCollection
        public int size() {
            return 0;
        }

        @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
        public Iterator<Resource> iterator() {
            return Resources.EMPTY_ITERATOR;
        }
    };
    public static final Iterator<Resource> EMPTY_ITERATOR = new Iterator<Resource>() { // from class: org.apache.tools.ant.types.resources.Resources.2
        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public Resource next() {
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    private class MyCollection extends AbstractCollection<Resource> {
        private Collection<Resource> cached;

        MyCollection() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return getCache().size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Resource> iterator() {
            return getCache().iterator();
        }

        private synchronized Collection<Resource> getCache() {
            Collection<Resource> collectionAsCollection;
            collectionAsCollection = this.cached;
            if (collectionAsCollection == null) {
                collectionAsCollection = CollectionUtils.asCollection(new MyIterator());
                if (Resources.this.cache) {
                    this.cached = collectionAsCollection;
                }
            }
            return collectionAsCollection;
        }

        private class MyIterator implements Iterator<Resource> {
            private Iterator<ResourceCollection> rci;
            private Iterator<Resource> ri;

            private MyIterator() {
                this.rci = Resources.this.getNested().iterator();
                this.ri = null;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                Iterator<Resource> it = this.ri;
                boolean zHasNext = it != null && it.hasNext();
                while (!zHasNext && this.rci.hasNext()) {
                    Iterator<Resource> it2 = this.rci.next().iterator();
                    this.ri = it2;
                    zHasNext = it2.hasNext();
                }
                return zHasNext;
            }

            @Override // java.util.Iterator
            public Resource next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.ri.next();
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    public Resources() {
    }

    public Resources(Project project) {
        setProject(project);
    }

    public synchronized void setCache(boolean z) {
        this.cache = z;
    }

    public synchronized void add(ResourceCollection resourceCollection) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (resourceCollection == null) {
            return;
        }
        if (this.rc == null) {
            this.rc = new Vector<>();
        }
        this.rc.add(resourceCollection);
        invalidateExistingIterators();
        this.coll = null;
        setChecked(false);
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public synchronized Iterator<Resource> iterator() {
        if (isReference()) {
            return getRef().iterator();
        }
        validate();
        return new FailFast(this, this.coll.iterator());
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        if (isReference()) {
            return getRef().size();
        }
        validate();
        return this.coll.size();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        if (isReference()) {
            return getRef().isFilesystemOnly();
        }
        validate();
        Iterator<ResourceCollection> it = getNested().iterator();
        while (it.hasNext()) {
            if (!it.next().isFilesystemOnly()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.tools.ant.types.DataType
    public synchronized String toString() {
        if (isReference()) {
            return getCheckedRef().toString();
        }
        validate();
        Collection<Resource> collection = this.coll;
        if (collection != null && !collection.isEmpty()) {
            StringBuffer stringBuffer = new StringBuffer();
            for (Resource resource : this.coll) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(File.pathSeparatorChar);
                }
                stringBuffer.append(resource);
            }
            return stringBuffer.toString();
        }
        return "";
    }

    @Override // org.apache.tools.ant.types.DataType
    protected void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
            return;
        }
        for (Object obj : getNested()) {
            if (obj instanceof DataType) {
                pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
            }
        }
        setChecked(true);
    }

    protected void invalidateExistingIterators() {
        FailFast.invalidate(this);
    }

    private ResourceCollection getRef() {
        return (ResourceCollection) getCheckedRef(ResourceCollection.class, "ResourceCollection");
    }

    private synchronized void validate() {
        dieOnCircularReference();
        Collection<Resource> myCollection = this.coll;
        if (myCollection == null) {
            myCollection = new MyCollection();
        }
        this.coll = myCollection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized List<ResourceCollection> getNested() {
        List<ResourceCollection> listEmptyList;
        listEmptyList = this.rc;
        if (listEmptyList == null) {
            listEmptyList = Collections.emptyList();
        }
        return listEmptyList;
    }
}

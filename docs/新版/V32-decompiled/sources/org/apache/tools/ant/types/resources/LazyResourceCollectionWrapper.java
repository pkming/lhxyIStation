package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class LazyResourceCollectionWrapper extends AbstractResourceCollectionWrapper {
    private final List<Resource> cachedResources = new ArrayList();
    private FilteringIterator filteringIterator;

    protected boolean filterResource(Resource resource) {
        return false;
    }

    @Override // org.apache.tools.ant.types.resources.AbstractResourceCollectionWrapper
    protected Iterator<Resource> createIterator() {
        if (isCache()) {
            if (this.filteringIterator == null) {
                this.filteringIterator = new FilteringIterator(getResourceCollection().iterator());
            }
            return new CachedIterator(this.filteringIterator);
        }
        return new FilteringIterator(getResourceCollection().iterator());
    }

    @Override // org.apache.tools.ant.types.resources.AbstractResourceCollectionWrapper
    protected int getSize() {
        Iterator<Resource> itCreateIterator = createIterator();
        int i = 0;
        while (itCreateIterator.hasNext()) {
            itCreateIterator.next();
            i++;
        }
        return i;
    }

    private class FilteringIterator implements Iterator<Resource> {
        protected final Iterator<Resource> it;
        Resource next = null;
        boolean ended = false;

        public FilteringIterator(Iterator<Resource> it) {
            this.it = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.ended) {
                return false;
            }
            while (this.next == null) {
                if (!this.it.hasNext()) {
                    this.ended = true;
                    return false;
                }
                Resource next = this.it.next();
                this.next = next;
                if (LazyResourceCollectionWrapper.this.filterResource(next)) {
                    this.next = null;
                }
            }
            return true;
        }

        @Override // java.util.Iterator
        public Resource next() {
            if (!hasNext()) {
                throw new UnsupportedOperationException();
            }
            Resource resource = this.next;
            this.next = null;
            return resource;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class CachedIterator implements Iterator<Resource> {
        int cusrsor = 0;
        private final Iterator<Resource> it;

        public CachedIterator(Iterator<Resource> it) {
            this.it = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            synchronized (LazyResourceCollectionWrapper.this.cachedResources) {
                if (LazyResourceCollectionWrapper.this.cachedResources.size() > this.cusrsor) {
                    return true;
                }
                if (!this.it.hasNext()) {
                    return false;
                }
                LazyResourceCollectionWrapper.this.cachedResources.add(this.it.next());
                return true;
            }
        }

        @Override // java.util.Iterator
        public Resource next() {
            Resource resource;
            if (hasNext()) {
                synchronized (LazyResourceCollectionWrapper.this.cachedResources) {
                    List list = LazyResourceCollectionWrapper.this.cachedResources;
                    int i = this.cusrsor;
                    this.cusrsor = i + 1;
                    resource = (Resource) list.get(i);
                }
                return resource;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

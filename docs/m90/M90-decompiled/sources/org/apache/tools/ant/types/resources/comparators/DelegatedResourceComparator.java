package org.apache.tools.ant.types.resources.comparators;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class DelegatedResourceComparator extends ResourceComparator {
    private List<ResourceComparator> resourceComparators = null;

    public synchronized void add(ResourceComparator resourceComparator) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (resourceComparator == null) {
            return;
        }
        List<ResourceComparator> vector = this.resourceComparators;
        if (vector == null) {
            vector = new Vector<>();
        }
        this.resourceComparators = vector;
        vector.add(resourceComparator);
        setChecked(false);
    }

    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator, java.util.Comparator
    public synchronized boolean equals(Object obj) {
        boolean zEquals = true;
        if (obj == this) {
            return true;
        }
        if (isReference()) {
            return getCheckedRef().equals(obj);
        }
        if (!(obj instanceof DelegatedResourceComparator)) {
            return false;
        }
        List<ResourceComparator> list = ((DelegatedResourceComparator) obj).resourceComparators;
        List<ResourceComparator> list2 = this.resourceComparators;
        if (list2 != null) {
            zEquals = list2.equals(list);
        } else if (list != null) {
            zEquals = false;
        }
        return zEquals;
    }

    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    public synchronized int hashCode() {
        if (isReference()) {
            return getCheckedRef().hashCode();
        }
        List<ResourceComparator> list = this.resourceComparators;
        return list == null ? 0 : list.hashCode();
    }

    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    protected synchronized int resourceCompare(Resource resource, Resource resource2) {
        List<ResourceComparator> list = this.resourceComparators;
        if (list != null && !list.isEmpty()) {
            int iResourceCompare = 0;
            Iterator<ResourceComparator> it = this.resourceComparators.iterator();
            while (iResourceCompare == 0 && it.hasNext()) {
                iResourceCompare = it.next().resourceCompare(resource, resource2);
            }
            return iResourceCompare;
        }
        return resource.compareTo(resource2);
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
        List<ResourceComparator> list = this.resourceComparators;
        if (list != null && !list.isEmpty()) {
            for (ResourceComparator resourceComparator : this.resourceComparators) {
                if (resourceComparator instanceof DataType) {
                    pushAndInvokeCircularReferenceCheck(resourceComparator, stack, project);
                }
            }
        }
        setChecked(true);
    }
}

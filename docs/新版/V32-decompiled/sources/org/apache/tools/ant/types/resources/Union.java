package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class Union extends BaseResourceCollectionContainer {
    public static Union getInstance(ResourceCollection resourceCollection) {
        return resourceCollection instanceof Union ? (Union) resourceCollection : new Union(resourceCollection);
    }

    public Union() {
    }

    public Union(Project project) {
        super(project);
    }

    public Union(ResourceCollection resourceCollection) {
        this(Project.getProject(resourceCollection), resourceCollection);
    }

    public Union(Project project, ResourceCollection resourceCollection) {
        super(project);
        add(resourceCollection);
    }

    public String[] list() {
        if (isReference()) {
            return ((Union) getCheckedRef(Union.class, getDataTypeName())).list();
        }
        Collection<String> allToStrings = getAllToStrings();
        return (String[]) allToStrings.toArray(new String[allToStrings.size()]);
    }

    public Resource[] listResources() {
        if (isReference()) {
            return ((Union) getCheckedRef(Union.class, getDataTypeName())).listResources();
        }
        Set<Resource> allResources = getAllResources();
        return (Resource[]) allResources.toArray(new Resource[allResources.size()]);
    }

    @Override // org.apache.tools.ant.types.resources.BaseResourceCollectionContainer
    protected Collection<Resource> getCollection() {
        return getAllResources();
    }

    @Deprecated
    protected <T> Collection<T> getCollection(boolean z) {
        return z ? (Collection<T>) getAllToStrings() : getAllResources();
    }

    protected Collection<String> getAllToStrings() {
        Set<Resource> allResources = getAllResources();
        ArrayList arrayList = new ArrayList(allResources.size());
        Iterator<Resource> it = allResources.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().toString());
        }
        return arrayList;
    }

    protected Set<Resource> getAllResources() {
        List<ResourceCollection> resourceCollections = getResourceCollections();
        if (resourceCollections.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(resourceCollections.size() * 2);
        Iterator<ResourceCollection> it = resourceCollections.iterator();
        while (it.hasNext()) {
            Iterator<Resource> it2 = it.next().iterator();
            while (it2.hasNext()) {
                linkedHashSet.add(it2.next());
            }
        }
        return linkedHashSet;
    }
}

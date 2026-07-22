package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.MergingMapper;

/* JADX INFO: loaded from: classes3.dex */
public class MappedResourceCollection extends DataType implements ResourceCollection, Cloneable {
    private ResourceCollection nested = null;
    private Mapper mapper = null;
    private boolean enableMultipleMappings = false;
    private boolean cache = false;
    private Collection<Resource> cachedColl = null;

    public synchronized void add(ResourceCollection resourceCollection) throws BuildException {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.nested != null) {
            throw new BuildException("Only one resource collection can be nested into mappedresources", getLocation());
        }
        setChecked(false);
        this.cachedColl = null;
        this.nested = resourceCollection;
    }

    public Mapper createMapper() throws BuildException {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.mapper != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS, getLocation());
        }
        setChecked(false);
        Mapper mapper = new Mapper(getProject());
        this.mapper = mapper;
        this.cachedColl = null;
        return mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }

    public void setEnableMultipleMappings(boolean z) {
        this.enableMultipleMappings = z;
    }

    public void setCache(boolean z) {
        this.cache = z;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        if (isReference()) {
            return ((MappedResourceCollection) getCheckedRef()).isFilesystemOnly();
        }
        checkInitialized();
        return false;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        if (isReference()) {
            return ((MappedResourceCollection) getCheckedRef()).size();
        }
        checkInitialized();
        return cacheCollection().size();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        if (isReference()) {
            return ((MappedResourceCollection) getCheckedRef()).iterator();
        }
        checkInitialized();
        return cacheCollection().iterator();
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (this.nested != null || this.mapper != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        try {
            MappedResourceCollection mappedResourceCollection = (MappedResourceCollection) super.clone();
            mappedResourceCollection.nested = this.nested;
            mappedResourceCollection.mapper = this.mapper;
            mappedResourceCollection.cachedColl = null;
            return mappedResourceCollection;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            checkInitialized();
            Mapper mapper = this.mapper;
            if (mapper != null) {
                pushAndInvokeCircularReferenceCheck(mapper, stack, project);
            }
            Object obj = this.nested;
            if (obj instanceof DataType) {
                pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
            }
            setChecked(true);
        }
    }

    private void checkInitialized() {
        if (this.nested == null) {
            throw new BuildException("A nested resource collection element is required", getLocation());
        }
        dieOnCircularReference();
    }

    private synchronized Collection<Resource> cacheCollection() {
        if (this.cachedColl == null || !this.cache) {
            this.cachedColl = getCollection();
        }
        return this.cachedColl;
    }

    private Collection<Resource> getCollection() {
        ArrayList arrayList = new ArrayList();
        Mapper mapper = this.mapper;
        FileNameMapper implementation = mapper != null ? mapper.getImplementation() : new IdentityMapper();
        for (Resource resource : this.nested) {
            if (this.enableMultipleMappings) {
                String[] strArrMapFileName = implementation.mapFileName(resource.getName());
                if (strArrMapFileName != null) {
                    for (String str : strArrMapFileName) {
                        arrayList.add(new MappedResource(resource, new MergingMapper(str)));
                    }
                }
            } else {
                arrayList.add(new MappedResource(resource, implementation));
            }
        }
        return arrayList;
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReference()) {
            return getCheckedRef().toString();
        }
        Iterator<Resource> it = iterator();
        if (!it.hasNext()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (it.hasNext()) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(File.pathSeparatorChar);
            }
            stringBuffer.append(it.next());
        }
        return stringBuffer.toString();
    }
}

package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.PropertyOutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyResource extends Resource {
    private static final int PROPERTY_MAGIC = Resource.getMagicNumber("PropertyResource".getBytes());
    private static final InputStream UNSET = new InputStream() { // from class: org.apache.tools.ant.types.resources.PropertyResource.1
        @Override // java.io.InputStream
        public int read() {
            return -1;
        }
    };

    public PropertyResource() {
    }

    public PropertyResource(Project project, String str) {
        super(str);
        setProject(project);
    }

    public String getValue() {
        if (isReference()) {
            return ((PropertyResource) getCheckedRef()).getValue();
        }
        Project project = getProject();
        if (project == null) {
            return null;
        }
        return project.getProperty(getName());
    }

    public Object getObjectValue() {
        if (isReference()) {
            return ((PropertyResource) getCheckedRef()).getObjectValue();
        }
        Project project = getProject();
        if (project == null) {
            return null;
        }
        return PropertyHelper.getProperty(project, getName());
    }

    @Override // org.apache.tools.ant.types.Resource
    public boolean isExists() {
        if (isReferenceOrProxy()) {
            return getReferencedOrProxied().isExists();
        }
        return getObjectValue() != null;
    }

    @Override // org.apache.tools.ant.types.Resource
    public long getSize() {
        if (isReferenceOrProxy()) {
            return getReferencedOrProxied().getSize();
        }
        if (getObjectValue() == null) {
            return 0L;
        }
        return String.valueOf(r0).length();
    }

    @Override // org.apache.tools.ant.types.Resource
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        return isReferenceOrProxy() && getReferencedOrProxied().equals(obj);
    }

    @Override // org.apache.tools.ant.types.Resource
    public int hashCode() {
        if (isReferenceOrProxy()) {
            return getReferencedOrProxied().hashCode();
        }
        return super.hashCode() * PROPERTY_MAGIC;
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReferenceOrProxy()) {
            return getReferencedOrProxied().toString();
        }
        return getValue();
    }

    @Override // org.apache.tools.ant.types.Resource
    public InputStream getInputStream() throws IOException {
        if (isReferenceOrProxy()) {
            return getReferencedOrProxied().getInputStream();
        }
        Object objectValue = getObjectValue();
        return objectValue == null ? UNSET : new ByteArrayInputStream(String.valueOf(objectValue).getBytes());
    }

    @Override // org.apache.tools.ant.types.Resource
    public OutputStream getOutputStream() throws IOException {
        if (isReferenceOrProxy()) {
            return getReferencedOrProxied().getOutputStream();
        }
        if (isExists()) {
            throw new ImmutableResourceException();
        }
        return new PropertyOutputStream(getProject(), getName());
    }

    protected boolean isReferenceOrProxy() {
        return isReference() || (getObjectValue() instanceof Resource);
    }

    protected Resource getReferencedOrProxied() {
        if (isReference()) {
            return (Resource) getCheckedRef(Resource.class, "resource");
        }
        Object objectValue = getObjectValue();
        if (objectValue instanceof Resource) {
            return (Resource) objectValue;
        }
        throw new IllegalStateException("This PropertyResource does not reference or proxy another Resource");
    }
}

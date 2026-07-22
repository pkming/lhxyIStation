package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;

/* JADX INFO: loaded from: classes3.dex */
public class StringResource extends Resource {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int STRING_MAGIC = Resource.getMagicNumber("StringResource".getBytes());
    private String encoding;

    public StringResource() {
        this.encoding = "UTF-8";
    }

    public StringResource(String str) {
        this(null, str);
    }

    public StringResource(Project project, String str) {
        this.encoding = "UTF-8";
        setProject(project);
        setValue(project != null ? project.replaceProperties(str) : str);
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized void setName(String str) {
        if (getName() != null) {
            throw new BuildException(new ImmutableResourceException());
        }
        super.setName(str);
    }

    public synchronized void setValue(String str) {
        setName(str);
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized String getName() {
        return super.getName();
    }

    public synchronized String getValue() {
        return getName();
    }

    @Override // org.apache.tools.ant.types.Resource
    public boolean isExists() {
        return getValue() != null;
    }

    public void addText(String str) {
        checkChildrenAllowed();
        setValue(getProject().replaceProperties(str));
    }

    public synchronized void setEncoding(String str) {
        checkAttributesAllowed();
        this.encoding = str;
    }

    public synchronized String getEncoding() {
        return this.encoding;
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized long getSize() {
        return isReference() ? ((Resource) getCheckedRef()).getSize() : getContent().length();
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized int hashCode() {
        if (isReference()) {
            return getCheckedRef().hashCode();
        }
        return super.hashCode() * STRING_MAGIC;
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public String toString() {
        return String.valueOf(getContent());
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        String content = getContent();
        if (content == null) {
            throw new IllegalStateException("unset string value");
        }
        String str = this.encoding;
        return new ByteArrayInputStream(str == null ? content.getBytes() : content.getBytes(str));
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized OutputStream getOutputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getOutputStream();
        }
        if (getValue() != null) {
            throw new ImmutableResourceException();
        }
        return new StringResourceFilterOutputStream();
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (this.encoding != "UTF-8") {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    protected synchronized String getContent() {
        return getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setValueFromOutputStream(String str) {
        if (getProject() != null) {
            str = getProject().replaceProperties(str);
        }
        setValue(str);
    }

    private class StringResourceFilterOutputStream extends FilterOutputStream {
        private final ByteArrayOutputStream baos;

        public StringResourceFilterOutputStream() {
            super(new ByteArrayOutputStream());
            this.baos = (ByteArrayOutputStream) this.out;
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            super.close();
            StringResource.this.setValueFromOutputStream(StringResource.this.encoding == null ? this.baos.toString() : this.baos.toString(StringResource.this.encoding));
        }
    }
}

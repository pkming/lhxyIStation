package org.apache.tools.ant.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.tools.ant.types.resources.FileProvider;

/* JADX INFO: loaded from: classes3.dex */
public class Resource extends DataType implements Comparable<Resource>, ResourceCollection {
    protected static final int MAGIC = getMagicNumber("Resource".getBytes());
    private static final int NULL_NAME = getMagicNumber("null name".getBytes());
    public static final long UNKNOWN_DATETIME = 0;
    public static final long UNKNOWN_SIZE = -1;
    private Boolean directory;
    private Boolean exists;
    private Long lastmodified;
    private String name;
    private Long size;

    protected static int getMagicNumber(byte[] bArr) {
        return new BigInteger(bArr).intValue();
    }

    public Resource() {
        this.name = null;
        this.exists = null;
        this.lastmodified = null;
        this.directory = null;
        this.size = null;
    }

    public Resource(String str) {
        this(str, false, 0L, false);
    }

    public Resource(String str, boolean z, long j) {
        this(str, z, j, false);
    }

    public Resource(String str, boolean z, long j, boolean z2) {
        this(str, z, j, z2, -1L);
    }

    public Resource(String str, boolean z, long j, boolean z2, long j2) {
        this.name = null;
        this.exists = null;
        this.lastmodified = null;
        this.directory = null;
        this.size = null;
        this.name = str;
        setName(str);
        setExists(z);
        setLastModified(j);
        setDirectory(z2);
        setSize(j2);
    }

    public String getName() {
        return isReference() ? ((Resource) getCheckedRef()).getName() : this.name;
    }

    public void setName(String str) {
        checkAttributesAllowed();
        this.name = str;
    }

    public boolean isExists() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).isExists();
        }
        Boolean bool = this.exists;
        return bool == null || bool.booleanValue();
    }

    public void setExists(boolean z) {
        checkAttributesAllowed();
        this.exists = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public long getLastModified() {
        Long l;
        if (isReference()) {
            return ((Resource) getCheckedRef()).getLastModified();
        }
        if (!isExists() || (l = this.lastmodified) == null) {
            return 0L;
        }
        long jLongValue = l.longValue();
        if (jLongValue < 0) {
            return 0L;
        }
        return jLongValue;
    }

    public void setLastModified(long j) {
        checkAttributesAllowed();
        this.lastmodified = new Long(j);
    }

    public boolean isDirectory() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).isDirectory();
        }
        Boolean bool = this.directory;
        return bool != null && bool.booleanValue();
    }

    public void setDirectory(boolean z) {
        checkAttributesAllowed();
        this.directory = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setSize(long j) {
        checkAttributesAllowed();
        if (j <= -1) {
            j = -1;
        }
        this.size = new Long(j);
    }

    public long getSize() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getSize();
        }
        if (!isExists()) {
            return 0L;
        }
        Long l = this.size;
        if (l != null) {
            return l.longValue();
        }
        return -1L;
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException unused) {
            throw new UnsupportedOperationException("CloneNotSupportedException for a Resource caught. Derived classes must support cloning.");
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(Resource resource) {
        if (isReference()) {
            return ((Resource) getCheckedRef()).compareTo(resource);
        }
        return toString().compareTo(resource.toString());
    }

    public boolean equals(Object obj) {
        if (isReference()) {
            return getCheckedRef().equals(obj);
        }
        return obj != null && obj.getClass().equals(getClass()) && compareTo((Resource) obj) == 0;
    }

    public int hashCode() {
        if (isReference()) {
            return getCheckedRef().hashCode();
        }
        String name = getName();
        return MAGIC * (name == null ? NULL_NAME : name.hashCode());
    }

    public InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        throw new UnsupportedOperationException();
    }

    public OutputStream getOutputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getOutputStream();
        }
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        return isReference() ? ((Resource) getCheckedRef()).iterator() : new Iterator<Resource>() { // from class: org.apache.tools.ant.types.Resource.1
            private boolean done = false;

            @Override // java.util.Iterator
            public boolean hasNext() {
                return !this.done;
            }

            @Override // java.util.Iterator
            public Resource next() {
                if (this.done) {
                    throw new NoSuchElementException();
                }
                this.done = true;
                return Resource.this;
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).size();
        }
        return 1;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        return (isReference() && ((Resource) getCheckedRef()).isFilesystemOnly()) || as(FileProvider.class) != null;
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReference()) {
            return getCheckedRef().toString();
        }
        String name = getName();
        return name == null ? "(anonymous)" : name;
    }

    public final String toLongString() {
        return isReference() ? ((Resource) getCheckedRef()).toLongString() : getDataTypeName() + " \"" + toString() + '\"';
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (this.name != null || this.exists != null || this.lastmodified != null || this.directory != null || this.size != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    public <T> T as(Class<T> cls) {
        if (cls.isAssignableFrom(getClass())) {
            return cls.cast(this);
        }
        return null;
    }
}

package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipFile;

/* JADX INFO: loaded from: classes3.dex */
public class ZipResource extends ArchiveResource {
    private String encoding;
    private ZipExtraField[] extras;
    private int method;

    public ZipResource() {
    }

    public ZipResource(File file, String str, ZipEntry zipEntry) {
        super(file, true);
        setEncoding(str);
        setEntry(zipEntry);
    }

    public void setZipfile(File file) {
        setArchive(file);
    }

    public File getZipfile() {
        return ((FileProvider) getArchive().as(FileProvider.class)).getFile();
    }

    @Override // org.apache.tools.ant.types.resources.ArchiveResource
    public void addConfigured(ResourceCollection resourceCollection) {
        super.addConfigured(resourceCollection);
        if (!resourceCollection.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
    }

    public void setEncoding(String str) {
        checkAttributesAllowed();
        this.encoding = str;
    }

    public String getEncoding() {
        return isReference() ? ((ZipResource) getCheckedRef()).getEncoding() : this.encoding;
    }

    @Override // org.apache.tools.ant.types.resources.ArchiveResource, org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) {
        if (this.encoding != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.Resource
    public InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        final ZipFile zipFile = new ZipFile(getZipfile(), getEncoding());
        ZipEntry entry = zipFile.getEntry(getName());
        if (entry == null) {
            zipFile.close();
            throw new BuildException("no entry " + getName() + " in " + getArchive());
        }
        return new FilterInputStream(zipFile.getInputStream(entry)) { // from class: org.apache.tools.ant.types.resources.ZipResource.1
            @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                FileUtils.close(this.in);
                zipFile.close();
            }

            protected void finalize() throws Throwable {
                try {
                    close();
                } finally {
                    super.finalize();
                }
            }
        };
    }

    @Override // org.apache.tools.ant.types.Resource
    public OutputStream getOutputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getOutputStream();
        }
        throw new UnsupportedOperationException("Use the zip task for zip output.");
    }

    public ZipExtraField[] getExtraFields() {
        if (isReference()) {
            return ((ZipResource) getCheckedRef()).getExtraFields();
        }
        checkEntry();
        ZipExtraField[] zipExtraFieldArr = this.extras;
        return zipExtraFieldArr == null ? new ZipExtraField[0] : zipExtraFieldArr;
    }

    public int getMethod() {
        return this.method;
    }

    @Override // org.apache.tools.ant.types.resources.ArchiveResource
    protected void fetchEntry() throws Throwable {
        Throwable th;
        IOException e;
        try {
            try {
                ZipFile zipFile = new ZipFile(getZipfile(), getEncoding());
                try {
                    setEntry(zipFile.getEntry(getName()));
                    ZipFile.closeQuietly(zipFile);
                } catch (IOException e2) {
                    e = e2;
                    log(e.getMessage(), 4);
                    throw new BuildException(e);
                }
            } catch (Throwable th2) {
                th = th2;
                ZipFile.closeQuietly(null);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (Throwable th3) {
            th = th3;
            ZipFile.closeQuietly(null);
            throw th;
        }
    }

    private void setEntry(ZipEntry zipEntry) {
        if (zipEntry == null) {
            setExists(false);
            return;
        }
        setName(zipEntry.getName());
        setExists(true);
        setLastModified(zipEntry.getTime());
        setDirectory(zipEntry.isDirectory());
        setSize(zipEntry.getSize());
        setMode(zipEntry.getUnixMode());
        this.extras = zipEntry.getExtraFields(true);
        this.method = zipEntry.getMethod();
    }
}

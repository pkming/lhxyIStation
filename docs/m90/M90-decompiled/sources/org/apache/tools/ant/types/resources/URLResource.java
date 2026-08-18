package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class URLResource extends Resource implements URLProvider {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final int NULL_URL = Resource.getMagicNumber("null URL".getBytes());
    private URL baseURL;
    private URLConnection conn;
    private String relPath;
    private URL url;

    public URLResource() {
    }

    public URLResource(URL url) {
        setURL(url);
    }

    public URLResource(URLProvider uRLProvider) {
        setURL(uRLProvider.getURL());
    }

    public URLResource(File file) {
        setFile(file);
    }

    public URLResource(String str) {
        this(newURL(str));
    }

    public synchronized void setURL(URL url) {
        checkAttributesAllowed();
        this.url = url;
    }

    public synchronized void setFile(File file) {
        try {
            setURL(FILE_UTILS.getFileURL(file));
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }

    public synchronized void setBaseURL(URL url) {
        checkAttributesAllowed();
        if (this.url != null) {
            throw new BuildException("can't define URL and baseURL attribute");
        }
        this.baseURL = url;
    }

    public synchronized void setRelativePath(String str) {
        checkAttributesAllowed();
        if (this.url != null) {
            throw new BuildException("can't define URL and relativePath attribute");
        }
        this.relPath = str;
    }

    @Override // org.apache.tools.ant.types.resources.URLProvider
    public synchronized URL getURL() {
        if (isReference()) {
            return ((URLResource) getCheckedRef()).getURL();
        }
        if (this.url == null && this.baseURL != null) {
            if (this.relPath == null) {
                throw new BuildException("must provide relativePath attribute when using baseURL.");
            }
            try {
                this.url = new URL(this.baseURL, this.relPath);
            } catch (MalformedURLException e) {
                throw new BuildException(e);
            }
        }
        return this.url;
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public synchronized void setRefid(Reference reference) {
        if (this.url != null || this.baseURL != null || this.relPath != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized String getName() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getName();
        }
        String file = getURL().getFile();
        if (!"".equals(file)) {
            file = file.substring(1);
        }
        return file;
    }

    @Override // org.apache.tools.ant.types.Resource, org.apache.tools.ant.types.DataType
    public synchronized String toString() {
        return isReference() ? getCheckedRef().toString() : String.valueOf(getURL());
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized boolean isExists() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).isExists();
        }
        return isExists(false);
    }

    private synchronized boolean isExists(boolean z) {
        if (getURL() == null) {
            return false;
        }
        try {
            connect(3);
            URLConnection uRLConnection = this.conn;
            if (uRLConnection instanceof HttpURLConnection) {
                boolean z2 = ((HttpURLConnection) uRLConnection).getResponseCode() < 400;
                if (z) {
                    close();
                }
                return z2;
            }
            if (this.url.getProtocol().startsWith("ftp")) {
                try {
                    try {
                        FileUtils.close(this.conn.getInputStream());
                        z = true;
                    } catch (Throwable th) {
                        FileUtils.close((InputStream) null);
                        throw th;
                    }
                } catch (IOException unused) {
                    z = true;
                    if (z) {
                        close();
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    z = true;
                    if (z) {
                        close();
                    }
                    throw th;
                }
            }
            if (z) {
                close();
            }
            return true;
        } catch (IOException unused2) {
        } catch (Throwable th3) {
            th = th3;
        }
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized long getLastModified() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getLastModified();
        }
        if (!isExists(false)) {
            return 0L;
        }
        return this.conn.getLastModified();
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized boolean isDirectory() {
        return isReference() ? ((Resource) getCheckedRef()).isDirectory() : getName().endsWith("/");
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized long getSize() {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getSize();
        }
        if (!isExists(false)) {
            return 0L;
        }
        try {
            connect();
            long contentLength = this.conn.getContentLength();
            close();
            return contentLength;
        } catch (IOException unused) {
            return -1L;
        }
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized boolean equals(Object obj) {
        boolean zEquals = true;
        if (this == obj) {
            return true;
        }
        if (isReference()) {
            return getCheckedRef().equals(obj);
        }
        if (obj != null && obj.getClass().equals(getClass())) {
            URLResource uRLResource = (URLResource) obj;
            if (getURL() != null) {
                zEquals = getURL().equals(uRLResource.getURL());
            } else if (uRLResource.getURL() != null) {
                zEquals = false;
            }
            return zEquals;
        }
        return false;
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized int hashCode() {
        if (isReference()) {
            return getCheckedRef().hashCode();
        }
        return MAGIC * (getURL() == null ? NULL_URL : getURL().hashCode());
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized InputStream getInputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        connect();
        try {
            return this.conn.getInputStream();
        } finally {
            this.conn = null;
        }
    }

    @Override // org.apache.tools.ant.types.Resource
    public synchronized OutputStream getOutputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getOutputStream();
        }
        connect();
        try {
            return this.conn.getOutputStream();
        } finally {
            this.conn = null;
        }
    }

    protected void connect() throws IOException {
        connect(0);
    }

    protected synchronized void connect(int i) throws IOException {
        URL url = getURL();
        if (url == null) {
            throw new BuildException("URL not set");
        }
        if (this.conn == null) {
            try {
                URLConnection uRLConnectionOpenConnection = url.openConnection();
                this.conn = uRLConnectionOpenConnection;
                uRLConnectionOpenConnection.connect();
            } catch (IOException e) {
                log(e.toString(), i);
                this.conn = null;
                throw e;
            }
        }
    }

    private synchronized void close() {
        try {
            FileUtils.close(this.conn);
        } finally {
            this.conn = null;
        }
    }

    private static URL newURL(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }
}

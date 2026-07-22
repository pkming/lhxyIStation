package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/* JADX INFO: loaded from: classes3.dex */
public class TarResource extends ArchiveResource {
    private int gid;
    private String groupName;
    private int uid;
    private String userName;

    public TarResource() {
        this.userName = "";
        this.groupName = "";
    }

    public TarResource(File file, TarEntry tarEntry) {
        super(file, true);
        this.userName = "";
        this.groupName = "";
        setEntry(tarEntry);
    }

    public TarResource(Resource resource, TarEntry tarEntry) {
        super(resource, true);
        this.userName = "";
        this.groupName = "";
        setEntry(tarEntry);
    }

    @Override // org.apache.tools.ant.types.Resource
    public InputStream getInputStream() throws IOException {
        TarEntry nextEntry;
        if (isReference()) {
            return ((Resource) getCheckedRef()).getInputStream();
        }
        TarInputStream tarInputStream = new TarInputStream(getArchive().getInputStream());
        do {
            nextEntry = tarInputStream.getNextEntry();
            if (nextEntry == null) {
                FileUtils.close(tarInputStream);
                throw new BuildException("no entry " + getName() + " in " + getArchive());
            }
        } while (!nextEntry.getName().equals(getName()));
        return tarInputStream;
    }

    @Override // org.apache.tools.ant.types.Resource
    public OutputStream getOutputStream() throws IOException {
        if (isReference()) {
            return ((Resource) getCheckedRef()).getOutputStream();
        }
        throw new UnsupportedOperationException("Use the tar task for tar output.");
    }

    public String getUserName() {
        if (isReference()) {
            return ((TarResource) getCheckedRef()).getUserName();
        }
        checkEntry();
        return this.userName;
    }

    public String getGroup() {
        if (isReference()) {
            return ((TarResource) getCheckedRef()).getGroup();
        }
        checkEntry();
        return this.groupName;
    }

    public int getUid() {
        if (isReference()) {
            return ((TarResource) getCheckedRef()).getUid();
        }
        checkEntry();
        return this.uid;
    }

    public int getGid() {
        if (isReference()) {
            return ((TarResource) getCheckedRef()).getGid();
        }
        checkEntry();
        return this.gid;
    }

    @Override // org.apache.tools.ant.types.resources.ArchiveResource
    protected void fetchEntry() throws Throwable {
        TarEntry nextEntry;
        TarInputStream tarInputStream = null;
        try {
            try {
                TarInputStream tarInputStream2 = new TarInputStream(getArchive().getInputStream());
                do {
                    try {
                        nextEntry = tarInputStream2.getNextEntry();
                        if (nextEntry == null) {
                            FileUtils.close(tarInputStream2);
                            setEntry(null);
                            return;
                        }
                    } catch (IOException e) {
                        e = e;
                        tarInputStream = tarInputStream2;
                        log(e.getMessage(), 4);
                        throw new BuildException(e);
                    } catch (Throwable th) {
                        th = th;
                        tarInputStream = tarInputStream2;
                        if (tarInputStream != null) {
                            FileUtils.close(tarInputStream);
                        }
                        throw th;
                    }
                } while (!nextEntry.getName().equals(getName()));
                setEntry(nextEntry);
                FileUtils.close(tarInputStream2);
            } catch (IOException e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void setEntry(TarEntry tarEntry) {
        if (tarEntry == null) {
            setExists(false);
            return;
        }
        setName(tarEntry.getName());
        setExists(true);
        setLastModified(tarEntry.getModTime().getTime());
        setDirectory(tarEntry.isDirectory());
        setSize(tarEntry.getSize());
        setMode(tarEntry.getMode());
        this.userName = tarEntry.getUserName();
        this.groupName = tarEntry.getGroupName();
        this.uid = tarEntry.getUserId();
        this.gid = tarEntry.getGroupId();
    }
}

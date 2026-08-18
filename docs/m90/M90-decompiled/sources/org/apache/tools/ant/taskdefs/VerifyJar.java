package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;

/* JADX INFO: loaded from: classes3.dex */
public class VerifyJar extends AbstractJarSignerTask {
    public static final String ERROR_NO_FILE = "Not found :";
    public static final String ERROR_NO_VERIFY = "Failed to verify ";
    private static final String VERIFIED_TEXT = "jar verified.";
    private boolean certificates = false;
    private BufferingOutputFilter outputCache = new BufferingOutputFilter();

    public void setCertificates(boolean z) {
        this.certificates = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (!(this.jar != null) && !hasResources()) {
            throw new BuildException(AbstractJarSignerTask.ERROR_NO_SOURCE);
        }
        beginExecution();
        RedirectorElement redirector = getRedirector();
        redirector.setAlwaysLog(true);
        redirector.createOutputFilterChain().add(this.outputCache);
        try {
            Iterator<Resource> it = createUnifiedSourcePath().iterator();
            while (it.hasNext()) {
                verifyOneJar(((FileProvider) it.next().as(FileProvider.class)).getFile());
            }
        } finally {
            endExecution();
        }
    }

    private void verifyOneJar(File file) {
        if (!file.exists()) {
            throw new BuildException(ERROR_NO_FILE + file);
        }
        ExecTask execTaskCreateJarSigner = createJarSigner();
        setCommonOptions(execTaskCreateJarSigner);
        bindToKeystore(execTaskCreateJarSigner);
        addValue(execTaskCreateJarSigner, "-verify");
        if (this.certificates) {
            addValue(execTaskCreateJarSigner, "-certs");
        }
        addValue(execTaskCreateJarSigner, file.getPath());
        log("Verifying JAR: " + file.getAbsolutePath());
        this.outputCache.clear();
        BuildException e = null;
        try {
            execTaskCreateJarSigner.execute();
        } catch (BuildException e2) {
            e = e2;
        }
        String string = this.outputCache.toString();
        if (e != null) {
            if (string.indexOf("zip file closed") >= 0) {
                log("You are running jarsigner against a JVM with a known bug that manifests as an IllegalStateException.", 1);
            } else {
                throw e;
            }
        }
        if (string.indexOf(VERIFIED_TEXT) < 0) {
            throw new BuildException(ERROR_NO_VERIFY + file);
        }
    }

    private static class BufferingOutputFilter implements ChainableReader {
        private BufferingOutputFilterReader buffer;

        private BufferingOutputFilter() {
        }

        @Override // org.apache.tools.ant.filters.ChainableReader
        public Reader chain(Reader reader) {
            BufferingOutputFilterReader bufferingOutputFilterReader = new BufferingOutputFilterReader(reader);
            this.buffer = bufferingOutputFilterReader;
            return bufferingOutputFilterReader;
        }

        public String toString() {
            return this.buffer.toString();
        }

        public void clear() {
            BufferingOutputFilterReader bufferingOutputFilterReader = this.buffer;
            if (bufferingOutputFilterReader != null) {
                bufferingOutputFilterReader.clear();
            }
        }
    }

    private static class BufferingOutputFilterReader extends Reader {
        private StringBuffer buffer = new StringBuffer();
        private Reader next;

        public BufferingOutputFilterReader(Reader reader) {
            this.next = reader;
        }

        @Override // java.io.Reader
        public int read(char[] cArr, int i, int i2) throws IOException {
            int i3 = this.next.read(cArr, i, i2);
            this.buffer.append(cArr, i, i2);
            return i3;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.next.close();
        }

        public String toString() {
            return this.buffer.toString();
        }

        public void clear() {
            this.buffer = new StringBuffer();
        }
    }
}

package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Get extends Task {
    private static final int BIG_BUFFER_SIZE = 102400;
    private static final String DEFAULT_AGENT_PREFIX = "Apache Ant";
    private static final int DOTS_PER_LINE = 50;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final String GZIP_CONTENT_ENCODING = "gzip";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final int HTTP_MOVED_TEMP = 307;
    private static final int NUMBER_RETRIES = 3;
    private static final int REDIRECT_LIMIT = 25;
    private File destination;
    private Resources sources = new Resources();
    private boolean verbose = false;
    private boolean quiet = false;
    private boolean useTimestamp = false;
    private boolean ignoreErrors = false;
    private String uname = null;
    private String pword = null;
    private long maxTime = 0;
    private int numberRetries = 3;
    private boolean skipExisting = false;
    private boolean httpUseCaches = true;
    private Mapper mapperElement = null;
    private String userAgent = System.getProperty(MagicNames.HTTP_AGENT_PROPERTY, "Apache Ant/" + Main.getShortAntVersion());

    public interface DownloadProgress {
        void beginDownload();

        void endDownload();

        void onTick();
    }

    public static class NullProgress implements DownloadProgress {
        @Override // org.apache.tools.ant.taskdefs.Get.DownloadProgress
        public void beginDownload() {
        }

        @Override // org.apache.tools.ant.taskdefs.Get.DownloadProgress
        public void endDownload() {
        }

        @Override // org.apache.tools.ant.taskdefs.Get.DownloadProgress
        public void onTick() {
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(9:5|(1:42)(2:7|(6:43|9|(1:11)|12|(1:14)|15)(3:16|(3:44|18|55)(3:40|19|(3:46|21|54)(3:45|22|(3:48|24|53)(2:47|25)))|50))|26|(1:28)|38|29|51|50|3) */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00e5, code lost:
    
        r1 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00e6, code lost:
    
        log("Error getting " + r2 + " to " + r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0108, code lost:
    
        if (r8.ignoreErrors != false) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0115, code lost:
    
        throw new org.apache.tools.ant.BuildException(r1, getLocation());
     */
    @Override // org.apache.tools.ant.Task
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void execute() throws org.apache.tools.ant.BuildException {
        /*
            Method dump skipped, instruction units count: 279
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.Get.execute():void");
    }

    public boolean doGet(int i, DownloadProgress downloadProgress) throws IOException {
        checkAttributes();
        Iterator<Resource> it = this.sources.iterator();
        if (it.hasNext()) {
            return doGet(((URLProvider) it.next().as(URLProvider.class)).getURL(), this.destination, i, downloadProgress);
        }
        return false;
    }

    public boolean doGet(URL url, File file, int i, DownloadProgress downloadProgress) throws IOException {
        long j;
        boolean z;
        if (file.exists() && this.skipExisting) {
            log("Destination already exists (skipping): " + file.getAbsolutePath(), i);
            return true;
        }
        DownloadProgress nullProgress = downloadProgress == null ? new NullProgress() : downloadProgress;
        log("Getting: " + url, i);
        log("To: " + file.getAbsolutePath(), i);
        if (this.useTimestamp && file.exists()) {
            long jLastModified = file.lastModified();
            if (this.verbose) {
                log("local file date : " + new Date(jLastModified).toString(), i);
            }
            j = jLastModified;
            z = true;
        } else {
            j = 0;
            z = false;
        }
        GetThread getThread = new GetThread(url, file, z, j, nullProgress, i, this.userAgent);
        getThread.setDaemon(true);
        getProject().registerThreadTask(getThread, this);
        getThread.start();
        try {
            getThread.join(this.maxTime * 1000);
        } catch (InterruptedException unused) {
            log("interrupted waiting for GET to finish", 3);
        }
        if (getThread.isAlive()) {
            String str = "The GET operation took longer than " + this.maxTime + " seconds, stopping it.";
            if (this.ignoreErrors) {
                log(str);
            }
            getThread.closeStreams();
            if (this.ignoreErrors) {
                return false;
            }
            throw new BuildException(str);
        }
        return getThread.wasSuccessful();
    }

    @Override // org.apache.tools.ant.Task, org.apache.tools.ant.ProjectComponent
    public void log(String str, int i) {
        if (!this.quiet || i >= 0) {
            super.log(str, i);
        }
    }

    private void checkAttributes() {
        String str = this.userAgent;
        if (str == null || str.trim().length() == 0) {
            throw new BuildException("userAgent may not be null or empty");
        }
        if (this.sources.size() == 0) {
            throw new BuildException("at least one source is required", getLocation());
        }
        Iterator<Resource> it = this.sources.iterator();
        while (it.hasNext()) {
            if (((URLProvider) it.next().as(URLProvider.class)) == null) {
                throw new BuildException("Only URLProvider resources are supported", getLocation());
            }
        }
        File file = this.destination;
        if (file == null) {
            throw new BuildException("dest attribute is required", getLocation());
        }
        if (file.exists() && this.sources.size() > 1 && !this.destination.isDirectory()) {
            throw new BuildException("The specified destination is not a directory", getLocation());
        }
        if (this.destination.exists() && !this.destination.canWrite()) {
            throw new BuildException("Can't write to " + this.destination.getAbsolutePath(), getLocation());
        }
        if (this.sources.size() <= 1 || this.destination.exists()) {
            return;
        }
        this.destination.mkdirs();
    }

    public void setSrc(URL url) {
        add(new URLResource(url));
    }

    public void add(ResourceCollection resourceCollection) {
        this.sources.add(resourceCollection);
    }

    public void setDest(File file) {
        this.destination = file;
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
    }

    public void setIgnoreErrors(boolean z) {
        this.ignoreErrors = z;
    }

    public void setUseTimestamp(boolean z) {
        this.useTimestamp = z;
    }

    public void setUsername(String str) {
        this.uname = str;
    }

    public void setPassword(String str) {
        this.pword = str;
    }

    public void setMaxTime(long j) {
        this.maxTime = j;
    }

    public void setRetries(int i) {
        this.numberRetries = i;
    }

    public void setSkipExisting(boolean z) {
        this.skipExisting = z;
    }

    public void setUserAgent(String str) {
        this.userAgent = str;
    }

    public void setHttpUseCaches(boolean z) {
        this.httpUseCaches = z;
    }

    public Mapper createMapper() throws BuildException {
        if (this.mapperElement != null) {
            throw new BuildException(Expand.ERROR_MULTIPLE_MAPPERS, getLocation());
        }
        Mapper mapper = new Mapper(getProject());
        this.mapperElement = mapper;
        return mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }

    protected static class Base64Converter extends org.apache.tools.ant.util.Base64Converter {
        protected Base64Converter() {
        }
    }

    public static class VerboseProgress implements DownloadProgress {
        private int dots = 0;
        PrintStream out;

        public VerboseProgress(PrintStream printStream) {
            this.out = printStream;
        }

        @Override // org.apache.tools.ant.taskdefs.Get.DownloadProgress
        public void beginDownload() {
            this.dots = 0;
        }

        @Override // org.apache.tools.ant.taskdefs.Get.DownloadProgress
        public void onTick() {
            this.out.print(".");
            int i = this.dots;
            this.dots = i + 1;
            if (i > 50) {
                this.out.flush();
                this.dots = 0;
            }
        }

        @Override // org.apache.tools.ant.taskdefs.Get.DownloadProgress
        public void endDownload() {
            this.out.println();
            this.out.flush();
        }
    }

    private class GetThread extends Thread {
        private URLConnection connection;
        private final File dest;
        private final boolean hasTimestamp;
        private final int logLevel;
        private final DownloadProgress progress;
        private final URL source;
        private final long timestamp;
        private String userAgent;
        private boolean success = false;
        private IOException ioexception = null;
        private BuildException exception = null;
        private InputStream is = null;
        private OutputStream os = null;
        private int redirections = 0;

        GetThread(URL url, File file, boolean z, long j, DownloadProgress downloadProgress, int i, String str) {
            this.userAgent = null;
            this.source = url;
            this.dest = file;
            this.hasTimestamp = z;
            this.timestamp = j;
            this.progress = downloadProgress;
            this.logLevel = i;
            this.userAgent = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                this.success = get();
            } catch (IOException e) {
                this.ioexception = e;
            } catch (BuildException e2) {
                this.exception = e2;
            }
        }

        private boolean get() throws IOException, BuildException {
            URLConnection uRLConnectionOpenConnection = openConnection(this.source);
            this.connection = uRLConnectionOpenConnection;
            if (uRLConnectionOpenConnection == null) {
                return false;
            }
            boolean zDownloadFile = downloadFile();
            if (zDownloadFile && Get.this.useTimestamp) {
                updateTimeStamp();
            }
            return zDownloadFile;
        }

        private boolean redirectionAllowed(URL url, URL url2) {
            if (!url.getProtocol().equals(url2.getProtocol()) && (!Get.HTTP.equals(url.getProtocol()) || !Get.HTTPS.equals(url2.getProtocol()))) {
                String str = "Redirection detected from " + url.getProtocol() + " to " + url2.getProtocol() + ". Protocol switch unsafe, not allowed.";
                if (Get.this.ignoreErrors) {
                    Get.this.log(str, this.logLevel);
                    return false;
                }
                throw new BuildException(str);
            }
            int i = this.redirections + 1;
            this.redirections = i;
            if (i <= 25) {
                return true;
            }
            if (Get.this.ignoreErrors) {
                Get.this.log("More than 25 times redirected, giving up", this.logLevel);
                return false;
            }
            throw new BuildException("More than 25 times redirected, giving up");
        }

        private URLConnection openConnection(URL url) throws IOException {
            URLConnection uRLConnectionOpenConnection = url.openConnection();
            if (this.hasTimestamp) {
                uRLConnectionOpenConnection.setIfModifiedSince(this.timestamp);
            }
            uRLConnectionOpenConnection.addRequestProperty("User-Agent", this.userAgent);
            if (Get.this.uname != null || Get.this.pword != null) {
                uRLConnectionOpenConnection.setRequestProperty("Authorization", "Basic " + new Base64Converter().encode((Get.this.uname + ":" + Get.this.pword).getBytes()));
            }
            uRLConnectionOpenConnection.setRequestProperty("Accept-Encoding", Get.GZIP_CONTENT_ENCODING);
            boolean z = uRLConnectionOpenConnection instanceof HttpURLConnection;
            if (z) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) uRLConnectionOpenConnection;
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.setUseCaches(Get.this.httpUseCaches);
            }
            try {
                uRLConnectionOpenConnection.connect();
                if (z) {
                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) uRLConnectionOpenConnection;
                    int responseCode = httpURLConnection2.getResponseCode();
                    if (responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307) {
                        String headerField = httpURLConnection2.getHeaderField("Location");
                        Get.this.log(url + (responseCode == 301 ? " permanently" : "") + " moved to " + headerField, this.logLevel);
                        URL url2 = new URL(url, headerField);
                        if (redirectionAllowed(url, url2)) {
                            return openConnection(url2);
                        }
                        return null;
                    }
                    long lastModified = httpURLConnection2.getLastModified();
                    if (responseCode == 304 || (lastModified != 0 && this.hasTimestamp && this.timestamp >= lastModified)) {
                        Get.this.log("Not modified - so not downloaded", this.logLevel);
                        return null;
                    }
                    if (responseCode == 401) {
                        if (Get.this.ignoreErrors) {
                            Get.this.log("HTTP Authorization failure", this.logLevel);
                            return null;
                        }
                        throw new BuildException("HTTP Authorization failure");
                    }
                }
                return uRLConnectionOpenConnection;
            } catch (NullPointerException e) {
                throw new BuildException("Failed to parse " + this.source.toString(), e);
            }
        }

        private boolean downloadFile() throws IOException {
            int i;
            for (int i2 = 0; i2 < Get.this.numberRetries; i2++) {
                try {
                    this.is = this.connection.getInputStream();
                    break;
                } catch (IOException e) {
                    Get.this.log("Error opening connection " + e, this.logLevel);
                }
            }
            if (this.is == null) {
                Get.this.log("Can't get " + this.source + " to " + this.dest, this.logLevel);
                if (Get.this.ignoreErrors) {
                    return false;
                }
                throw new BuildException("Can't get " + this.source + " to " + this.dest, Get.this.getLocation());
            }
            if (Get.GZIP_CONTENT_ENCODING.equals(this.connection.getContentEncoding())) {
                this.is = new GZIPInputStream(this.is);
            }
            this.os = new FileOutputStream(this.dest);
            this.progress.beginDownload();
            try {
                byte[] bArr = new byte[Get.BIG_BUFFER_SIZE];
                while (!isInterrupted() && (i = this.is.read(bArr)) >= 0) {
                    this.os.write(bArr, 0, i);
                    this.progress.onTick();
                }
                boolean z = !isInterrupted();
                FileUtils.close(this.os);
                FileUtils.close(this.is);
                if (!z) {
                    this.dest.delete();
                }
                this.progress.endDownload();
                return true;
            } catch (Throwable th) {
                FileUtils.close(this.os);
                FileUtils.close(this.is);
                this.dest.delete();
                throw th;
            }
        }

        private void updateTimeStamp() {
            long lastModified = this.connection.getLastModified();
            if (Get.this.verbose) {
                Get.this.log("last modified = " + new Date(lastModified).toString() + (lastModified == 0 ? " - using current time instead" : ""), this.logLevel);
            }
            if (lastModified != 0) {
                Get.FILE_UTILS.setFileLastModified(this.dest, lastModified);
            }
        }

        boolean wasSuccessful() throws IOException, BuildException {
            IOException iOException = this.ioexception;
            if (iOException != null) {
                throw iOException;
            }
            BuildException buildException = this.exception;
            if (buildException != null) {
                throw buildException;
            }
            return this.success;
        }

        void closeStreams() {
            interrupt();
            FileUtils.close(this.os);
            FileUtils.close(this.is);
            if (this.success || !this.dest.exists()) {
                return;
            }
            this.dest.delete();
        }
    }
}

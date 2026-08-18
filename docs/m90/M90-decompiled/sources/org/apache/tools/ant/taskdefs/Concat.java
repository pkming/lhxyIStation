package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.taskdefs.FixCRLF;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Intersect;
import org.apache.tools.ant.types.resources.LogOutputResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ReaderInputStream;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Concat extends Task implements ResourceCollection {
    private static final int BUFFER_SIZE = 8192;
    private static final ResourceSelector EXISTS;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final ResourceSelector NOT_EXISTS;
    private boolean append;
    private boolean binary;
    private Resource dest;
    private String encoding;
    private String eolString;
    private Vector<FilterChain> filterChains;
    private TextElement footer;
    private TextElement header;
    private String outputEncoding;
    private Resources rc;
    private String resourceName;
    private StringBuffer textBuffer;
    private boolean forceOverwrite = true;
    private boolean force = false;
    private boolean fixLastLine = false;
    private Writer outputWriter = null;
    private boolean ignoreEmpty = true;
    private ReaderFactory<Resource> resourceReaderFactory = new ReaderFactory<Resource>() { // from class: org.apache.tools.ant.taskdefs.Concat.1
        @Override // org.apache.tools.ant.taskdefs.Concat.ReaderFactory
        public Reader getReader(Resource resource) throws IOException {
            InputStream inputStream = resource.getInputStream();
            return new BufferedReader(Concat.this.encoding == null ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, Concat.this.encoding));
        }
    };
    private ReaderFactory<Reader> identityReaderFactory = new ReaderFactory<Reader>() { // from class: org.apache.tools.ant.taskdefs.Concat.2
        @Override // org.apache.tools.ant.taskdefs.Concat.ReaderFactory
        public Reader getReader(Reader reader) {
            return reader;
        }
    };

    private interface ReaderFactory<S> {
        Reader getReader(S s) throws IOException;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        return false;
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        return 1;
    }

    static {
        Exists exists = new Exists();
        EXISTS = exists;
        NOT_EXISTS = new Not(exists);
    }

    public static class TextElement extends ProjectComponent {
        private String value = "";
        private boolean trimLeading = false;
        private boolean trim = false;
        private boolean filtering = true;
        private String encoding = null;

        public void setFiltering(boolean z) {
            this.filtering = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean getFiltering() {
            return this.filtering;
        }

        public void setEncoding(String str) {
            this.encoding = str;
        }

        public void setFile(File file) throws BuildException {
            BufferedReader bufferedReader;
            if (!file.exists()) {
                throw new BuildException("File " + file + DirectoryScanner.DOES_NOT_EXIST_POSTFIX);
            }
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    if (this.encoding == null) {
                        bufferedReader = new BufferedReader(new FileReader(file));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.encoding));
                    }
                    bufferedReader2 = bufferedReader;
                    this.value = FileUtils.safeReadFully(bufferedReader2);
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            } finally {
                FileUtils.close(bufferedReader2);
            }
        }

        public void addText(String str) {
            this.value += getProject().replaceProperties(str);
        }

        public void setTrimLeading(boolean z) {
            this.trimLeading = z;
        }

        public void setTrim(boolean z) {
            this.trim = z;
        }

        /* JADX WARN: Removed duplicated region for block: B:26:0x004b  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.String getValue() {
            /*
                r8 = this;
                java.lang.String r0 = r8.value
                java.lang.String r1 = ""
                if (r0 != 0) goto L8
                r8.value = r1
            L8:
                java.lang.String r0 = r8.value
                java.lang.String r0 = r0.trim()
                int r0 = r0.length()
                if (r0 != 0) goto L16
                r8.value = r1
            L16:
                boolean r0 = r8.trimLeading
                if (r0 == 0) goto L53
                java.lang.String r0 = r8.value
                char[] r0 = r0.toCharArray()
                java.lang.StringBuffer r1 = new java.lang.StringBuffer
                int r2 = r0.length
                r1.<init>(r2)
                r2 = 0
                r3 = 1
                r4 = r2
                r5 = r3
            L2a:
                int r6 = r0.length
                if (r4 >= r6) goto L4d
                int r6 = r4 + 1
                char r4 = r0[r4]
                if (r5 == 0) goto L40
                r7 = 32
                if (r4 == r7) goto L3e
                r7 = 9
                if (r4 != r7) goto L3c
                goto L3e
            L3c:
                r5 = r2
                goto L40
            L3e:
                r4 = r6
                goto L2a
            L40:
                r1.append(r4)
                r7 = 10
                if (r4 == r7) goto L4b
                r7 = 13
                if (r4 != r7) goto L3e
            L4b:
                r5 = r3
                goto L3e
            L4d:
                java.lang.String r0 = r1.toString()
                r8.value = r0
            L53:
                boolean r0 = r8.trim
                if (r0 == 0) goto L5f
                java.lang.String r0 = r8.value
                java.lang.String r0 = r0.trim()
                r8.value = r0
            L5f:
                java.lang.String r0 = r8.value
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.Concat.TextElement.getValue():java.lang.String");
        }
    }

    private final class MultiReader<S> extends Reader {
        private ReaderFactory<S> factory;
        private char[] lastChars;
        private int lastPos;
        private boolean needAddSeparator;
        private Reader reader;
        private Iterator<S> readerSources;

        private MultiReader(Iterator<S> it, ReaderFactory<S> readerFactory) {
            this.reader = null;
            this.lastPos = 0;
            this.lastChars = new char[Concat.this.eolString.length()];
            this.needAddSeparator = false;
            this.readerSources = it;
            this.factory = readerFactory;
        }

        private Reader getReader() throws IOException {
            if (this.reader == null && this.readerSources.hasNext()) {
                this.reader = this.factory.getReader(this.readerSources.next());
                Arrays.fill(this.lastChars, (char) 0);
            }
            return this.reader;
        }

        private void nextReader() throws IOException {
            close();
            this.reader = null;
        }

        @Override // java.io.Reader
        public int read() throws IOException {
            if (this.needAddSeparator) {
                if (this.lastPos < Concat.this.eolString.length()) {
                    String str = Concat.this.eolString;
                    int i = this.lastPos;
                    this.lastPos = i + 1;
                    return str.charAt(i);
                }
                this.lastPos = 0;
                this.needAddSeparator = false;
            }
            while (getReader() != null) {
                int i2 = getReader().read();
                if (i2 == -1) {
                    nextReader();
                    if (isFixLastLine() && isMissingEndOfLine()) {
                        this.needAddSeparator = true;
                        this.lastPos = 1;
                        return Concat.this.eolString.charAt(0);
                    }
                } else {
                    addLastChar((char) i2);
                    return i2;
                }
            }
            return -1;
        }

        @Override // java.io.Reader
        public int read(char[] cArr, int i, int i2) throws IOException {
            int i3 = 0;
            while (true) {
                if (getReader() == null && !this.needAddSeparator) {
                    if (i3 == 0) {
                        return -1;
                    }
                    return i3;
                }
                if (this.needAddSeparator) {
                    String str = Concat.this.eolString;
                    int i4 = this.lastPos;
                    this.lastPos = i4 + 1;
                    cArr[i] = str.charAt(i4);
                    if (this.lastPos >= Concat.this.eolString.length()) {
                        this.lastPos = 0;
                        this.needAddSeparator = false;
                    }
                    i2--;
                    i++;
                    i3++;
                    if (i2 == 0) {
                        return i3;
                    }
                } else {
                    int i5 = getReader().read(cArr, i, i2);
                    if (i5 == -1 || i5 == 0) {
                        nextReader();
                        if (isFixLastLine() && isMissingEndOfLine()) {
                            this.needAddSeparator = true;
                            this.lastPos = 0;
                        }
                    } else {
                        if (isFixLastLine()) {
                            for (int i6 = i5; i6 > i5 - this.lastChars.length && i6 > 0; i6--) {
                                addLastChar(cArr[(i + i6) - 1]);
                            }
                        }
                        i2 -= i5;
                        i += i5;
                        i3 += i5;
                        if (i2 == 0) {
                            return i3;
                        }
                    }
                }
            }
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            Reader reader = this.reader;
            if (reader != null) {
                reader.close();
            }
        }

        private void addLastChar(char c) {
            for (int length = this.lastChars.length - 2; length >= 0; length--) {
                char[] cArr = this.lastChars;
                cArr[length] = cArr[length + 1];
            }
            this.lastChars[r0.length - 1] = c;
        }

        private boolean isMissingEndOfLine() {
            int i = 0;
            while (true) {
                char[] cArr = this.lastChars;
                if (i >= cArr.length) {
                    return false;
                }
                if (cArr[i] != Concat.this.eolString.charAt(i)) {
                    return true;
                }
                i++;
            }
        }

        private boolean isFixLastLine() {
            return Concat.this.fixLastLine && Concat.this.textBuffer == null;
        }
    }

    private final class ConcatResource extends Resource {
        private ResourceCollection c;

        private ConcatResource(ResourceCollection resourceCollection) {
            this.c = resourceCollection;
        }

        @Override // org.apache.tools.ant.types.Resource
        public InputStream getInputStream() throws IOException {
            if (!Concat.this.binary) {
                Reader filteredReader = Concat.this.getFilteredReader(new MultiReader(this.c.iterator(), Concat.this.resourceReaderFactory));
                if (Concat.this.header != null || Concat.this.footer != null) {
                    int i = 1;
                    int i2 = Concat.this.header != null ? 2 : 1;
                    if (Concat.this.footer != null) {
                        i2++;
                    }
                    Reader[] readerArr = new Reader[i2];
                    if (Concat.this.header != null) {
                        readerArr[0] = new StringReader(Concat.this.header.getValue());
                        if (Concat.this.header.getFiltering()) {
                            readerArr[0] = Concat.this.getFilteredReader(readerArr[0]);
                        }
                    } else {
                        i = 0;
                    }
                    int i3 = i + 1;
                    readerArr[i] = filteredReader;
                    if (Concat.this.footer != null) {
                        readerArr[i3] = new StringReader(Concat.this.footer.getValue());
                        if (Concat.this.footer.getFiltering()) {
                            readerArr[i3] = Concat.this.getFilteredReader(readerArr[i3]);
                        }
                    }
                    filteredReader = new MultiReader(Arrays.asList(readerArr).iterator(), Concat.this.identityReaderFactory);
                }
                return Concat.this.outputEncoding == null ? new ReaderInputStream(filteredReader) : new ReaderInputStream(filteredReader, Concat.this.outputEncoding);
            }
            ConcatResourceInputStream concatResourceInputStream = new ConcatResourceInputStream(this.c);
            concatResourceInputStream.setManagingComponent(this);
            return concatResourceInputStream;
        }

        @Override // org.apache.tools.ant.types.Resource
        public String getName() {
            return Concat.this.resourceName == null ? "concat (" + String.valueOf(this.c) + ")" : Concat.this.resourceName;
        }
    }

    public Concat() {
        reset();
    }

    public void reset() {
        this.append = false;
        this.forceOverwrite = true;
        this.dest = null;
        this.encoding = null;
        this.outputEncoding = null;
        this.fixLastLine = false;
        this.filterChains = null;
        this.footer = null;
        this.header = null;
        this.binary = false;
        this.outputWriter = null;
        this.textBuffer = null;
        this.eolString = StringUtils.LINE_SEP;
        this.rc = null;
        this.ignoreEmpty = true;
        this.force = false;
    }

    public void setDestfile(File file) {
        setDest(new FileResource(file));
    }

    public void setDest(Resource resource) {
        this.dest = resource;
    }

    public void setAppend(boolean z) {
        this.append = z;
    }

    public void setEncoding(String str) {
        this.encoding = str;
        if (this.outputEncoding == null) {
            this.outputEncoding = str;
        }
    }

    public void setOutputEncoding(String str) {
        this.outputEncoding = str;
    }

    public void setForce(boolean z) {
        this.forceOverwrite = z;
    }

    public void setOverwrite(boolean z) {
        setForce(z);
    }

    public void setForceReadOnly(boolean z) {
        this.force = z;
    }

    public void setIgnoreEmpty(boolean z) {
        this.ignoreEmpty = z;
    }

    public void setResourceName(String str) {
        this.resourceName = str;
    }

    public Path createPath() {
        Path path = new Path(getProject());
        add(path);
        return path;
    }

    public void addFileset(FileSet fileSet) {
        add(fileSet);
    }

    public void addFilelist(FileList fileList) {
        add(fileList);
    }

    public void add(ResourceCollection resourceCollection) {
        synchronized (this) {
            if (this.rc == null) {
                Resources resources = new Resources();
                this.rc = resources;
                resources.setProject(getProject());
                this.rc.setCache(true);
            }
        }
        this.rc.add(resourceCollection);
    }

    public void addFilterChain(FilterChain filterChain) {
        if (this.filterChains == null) {
            this.filterChains = new Vector<>();
        }
        this.filterChains.addElement(filterChain);
    }

    public void addText(String str) {
        if (this.textBuffer == null) {
            this.textBuffer = new StringBuffer(str.length());
        }
        this.textBuffer.append(str);
    }

    public void addHeader(TextElement textElement) {
        this.header = textElement;
    }

    public void addFooter(TextElement textElement) {
        this.footer = textElement;
    }

    public void setFixLastLine(boolean z) {
        this.fixLastLine = z;
    }

    public void setEol(FixCRLF.CrLf crLf) {
        String value = crLf.getValue();
        if (value.equals("cr") || value.equals(Os.FAMILY_MAC)) {
            this.eolString = "\r";
            return;
        }
        if (value.equals("lf") || value.equals(Os.FAMILY_UNIX)) {
            this.eolString = "\n";
        } else if (value.equals("crlf") || value.equals(Os.FAMILY_DOS)) {
            this.eolString = "\r\n";
        }
    }

    public void setWriter(Writer writer) {
        this.outputWriter = writer;
    }

    public void setBinary(boolean z) {
        this.binary = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        validate();
        if (this.binary && this.dest == null) {
            throw new BuildException("dest|destfile attribute is required for binary concatenation");
        }
        ResourceCollection resources = getResources();
        if (isUpToDate(resources)) {
            log(this.dest + " is up-to-date.", 3);
            return;
        }
        if (resources.size() == 0 && this.ignoreEmpty) {
            return;
        }
        try {
            ConcatResource concatResource = new ConcatResource(resources);
            Resource logOutputResource = this.dest;
            if (logOutputResource == null) {
                logOutputResource = new LogOutputResource(this, 1);
            }
            ResourceUtils.copyResource(concatResource, logOutputResource, null, null, true, false, this.append, null, null, getProject(), this.force);
        } catch (IOException e) {
            throw new BuildException("error concatenating content to " + this.dest, e);
        }
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        validate();
        return Collections.singletonList(new ConcatResource(getResources())).iterator();
    }

    private void validate() {
        sanitizeText();
        if (this.binary) {
            if (this.textBuffer != null) {
                throw new BuildException("Nested text is incompatible with binary concatenation");
            }
            if (this.encoding != null || this.outputEncoding != null) {
                throw new BuildException("Setting input or output encoding is incompatible with binary concatenation");
            }
            if (this.filterChains != null) {
                throw new BuildException("Setting filters is incompatible with binary concatenation");
            }
            if (this.fixLastLine) {
                throw new BuildException("Setting fixlastline is incompatible with binary concatenation");
            }
            if (this.header != null || this.footer != null) {
                throw new BuildException("Nested header or footer is incompatible with binary concatenation");
            }
        }
        if (this.dest != null && this.outputWriter != null) {
            throw new BuildException("Cannot specify both a destination resource and an output writer");
        }
        Resources resources = this.rc;
        if (resources == null && this.textBuffer == null) {
            throw new BuildException("At least one resource must be provided, or some text.");
        }
        if (resources != null && this.textBuffer != null) {
            throw new BuildException("Cannot include inline text when using resources.");
        }
    }

    private ResourceCollection getResources() {
        if (this.rc == null) {
            return new StringResource(getProject(), this.textBuffer.toString());
        }
        if (this.dest != null) {
            Intersect intersect = new Intersect();
            intersect.setProject(getProject());
            intersect.add(this.rc);
            intersect.add(this.dest);
            if (intersect.size() > 0) {
                throw new BuildException("Destination resource " + this.dest + " was specified as an input resource.");
            }
        }
        Restrict restrict = new Restrict();
        restrict.add(NOT_EXISTS);
        restrict.add(this.rc);
        Iterator<Resource> it = restrict.iterator();
        while (it.hasNext()) {
            log(it.next() + DirectoryScanner.DOES_NOT_EXIST_POSTFIX, 0);
        }
        Restrict restrict2 = new Restrict();
        restrict2.add(EXISTS);
        restrict2.add(this.rc);
        return restrict2;
    }

    private boolean isUpToDate(ResourceCollection resourceCollection) {
        if (this.dest == null || this.forceOverwrite) {
            return false;
        }
        Iterator<Resource> it = resourceCollection.iterator();
        while (it.hasNext()) {
            if (SelectorUtils.isOutOfDate(it.next(), this.dest, FILE_UTILS.getFileTimestampGranularity())) {
                return false;
            }
        }
        return true;
    }

    private void sanitizeText() {
        StringBuffer stringBuffer = this.textBuffer;
        if (stringBuffer == null || !"".equals(stringBuffer.toString().trim())) {
            return;
        }
        this.textBuffer = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Reader getFilteredReader(Reader reader) {
        if (this.filterChains == null) {
            return reader;
        }
        ChainReaderHelper chainReaderHelper = new ChainReaderHelper();
        chainReaderHelper.setBufferSize(8192);
        chainReaderHelper.setPrimaryReader(reader);
        chainReaderHelper.setFilterChains(this.filterChains);
        chainReaderHelper.setProject(getProject());
        return chainReaderHelper.getAssembledReader();
    }
}

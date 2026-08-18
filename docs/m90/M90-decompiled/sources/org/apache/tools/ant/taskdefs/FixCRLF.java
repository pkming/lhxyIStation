package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.filters.FixCrLfFilter;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class FixCRLF extends MatchingTask implements ChainableReader {
    public static final String ERROR_FILE_AND_SRCDIR = "<fixcrlf> error: srcdir and file are mutually exclusive";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final String FIXCRLF_ERROR = "<fixcrlf> error: ";
    private File file;
    private File srcDir;
    private boolean preserveLastModified = false;
    private File destDir = null;
    private FixCrLfFilter filter = new FixCrLfFilter();
    private Vector<FilterChain> fcv = null;
    private String encoding = null;
    private String outputEncoding = null;

    @Override // org.apache.tools.ant.filters.ChainableReader
    public final Reader chain(Reader reader) {
        return this.filter.chain(reader);
    }

    public void setSrcdir(File file) {
        this.srcDir = file;
    }

    public void setDestdir(File file) {
        this.destDir = file;
    }

    public void setJavafiles(boolean z) {
        this.filter.setJavafiles(z);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setEol(CrLf crLf) {
        this.filter.setEol(FixCrLfFilter.CrLf.newInstance(crLf.getValue()));
    }

    public void setCr(AddAsisRemove addAsisRemove) {
        log("DEPRECATED: The cr attribute has been deprecated,", 1);
        log("Please use the eol attribute instead", 1);
        String value = addAsisRemove.getValue();
        CrLf crLf = new CrLf();
        if (value.equals("remove")) {
            crLf.setValue("lf");
        } else if (value.equals("asis")) {
            crLf.setValue("asis");
        } else {
            crLf.setValue("crlf");
        }
        setEol(crLf);
    }

    public void setTab(AddAsisRemove addAsisRemove) {
        this.filter.setTab(FixCrLfFilter.AddAsisRemove.newInstance(addAsisRemove.getValue()));
    }

    public void setTablength(int i) throws BuildException {
        try {
            this.filter.setTablength(i);
        } catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public void setEof(AddAsisRemove addAsisRemove) {
        this.filter.setEof(FixCrLfFilter.AddAsisRemove.newInstance(addAsisRemove.getValue()));
    }

    public void setEncoding(String str) {
        this.encoding = str;
    }

    public void setOutputEncoding(String str) {
        this.outputEncoding = str;
    }

    public void setFixlast(boolean z) {
        this.filter.setFixlast(z);
    }

    public void setPreserveLastModified(boolean z) {
        this.preserveLastModified = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        validate();
        String str = this.encoding;
        if (str == null) {
            str = "default";
        }
        StringBuilder sbAppend = new StringBuilder().append("options: eol=").append(this.filter.getEol().getValue()).append(" tab=").append(this.filter.getTab().getValue()).append(" eof=").append(this.filter.getEof().getValue()).append(" tablength=").append(this.filter.getTablength()).append(" encoding=").append(str).append(" outputencoding=");
        String str2 = this.outputEncoding;
        if (str2 != null) {
            str = str2;
        }
        log(sbAppend.append(str).toString(), 3);
        for (String str3 : super.getDirectoryScanner(this.srcDir).getIncludedFiles()) {
            processFile(str3);
        }
    }

    private void validate() throws BuildException {
        if (this.file != null) {
            if (this.srcDir != null) {
                throw new BuildException(ERROR_FILE_AND_SRCDIR);
            }
            this.fileset.setFile(this.file);
            this.srcDir = this.file.getParentFile();
        }
        File file = this.srcDir;
        if (file == null) {
            throw new BuildException("<fixcrlf> error: srcdir attribute must be set!");
        }
        if (!file.exists()) {
            throw new BuildException("<fixcrlf> error: srcdir does not exist: '" + this.srcDir + "'");
        }
        if (!this.srcDir.isDirectory()) {
            throw new BuildException("<fixcrlf> error: srcdir is not a directory: '" + this.srcDir + "'");
        }
        File file2 = this.destDir;
        if (file2 != null) {
            if (!file2.exists()) {
                throw new BuildException("<fixcrlf> error: destdir does not exist: '" + this.destDir + "'");
            }
            if (!this.destDir.isDirectory()) {
                throw new BuildException("<fixcrlf> error: destdir is not a directory: '" + this.destDir + "'");
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [org.apache.tools.ant.util.FileUtils] */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void processFile(String str) throws Throwable {
        Vector<FilterChain> vector;
        String str2;
        String str3;
        File file;
        FileUtils fileUtils;
        boolean z;
        File file2 = new File(this.srcDir, str);
        long jLastModified = file2.lastModified();
        File file3 = this.destDir;
        if (file3 == null) {
            file3 = this.srcDir;
        }
        File file4 = file3;
        if (this.fcv == null) {
            FilterChain filterChain = new FilterChain();
            filterChain.add(this.filter);
            Vector<FilterChain> vector2 = new Vector<>(1);
            this.fcv = vector2;
            vector2.add(filterChain);
        }
        FileUtils fileUtils2 = FILE_UTILS;
        File file5 = fileUtils2;
        File fileCreateTempFile = file5.createTempFile("fixcrlf", "", null, true, true);
        try {
            try {
                vector = this.fcv;
                str2 = this.encoding;
                str3 = this.outputEncoding;
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        } catch (Throwable th2) {
            th = th2;
            file5 = fileCreateTempFile;
        }
        try {
            fileUtils2.copyFile(file2, fileCreateTempFile, (FilterSetCollection) null, (Vector) vector, true, false, str2, str3 == null ? str2 : str3, getProject());
            File file6 = new File(file4, str);
            if (file6.exists()) {
                log("destFile " + file6 + " exists", 4);
                file = fileCreateTempFile;
                fileUtils = fileUtils2;
                try {
                    z = !fileUtils.contentEquals(file6, file);
                    log(file6 + (z ? " is being written" : " is not written, as the contents are identical"), 4);
                } catch (IOException e2) {
                    e = e2;
                    throw new BuildException("error running fixcrlf on file " + file2, e);
                }
            } else {
                file = fileCreateTempFile;
                fileUtils = fileUtils2;
                z = true;
            }
            if (z) {
                fileUtils.rename(file, file6);
                if (this.preserveLastModified) {
                    log("preserved lastModified for " + file6, 4);
                    fileUtils.setFileLastModified(file6, jLastModified);
                }
            }
            if (file == null || !file.exists()) {
                return;
            }
            fileUtils.tryHardToDelete(file);
        } catch (IOException e3) {
            e = e3;
        } catch (Throwable th3) {
            th = th3;
            file5 = fileCreateTempFile;
            if (file5 != 0 && file5.exists()) {
                FILE_UTILS.tryHardToDelete(file5);
            }
            throw th;
        }
    }

    protected class OneLiner implements Enumeration<Object> {
        private static final char CTRLZ = 26;
        private static final int INBUFLEN = 8192;
        private static final int LINEBUFLEN = 200;
        private static final int LOOKING = 1;
        private static final int NOTJAVA = 0;
        private static final int UNDEF = -1;
        private BufferedReader reader;
        private File srcFile;
        private int state;
        private StringBuffer eolStr = new StringBuffer(200);
        private StringBuffer eofStr = new StringBuffer();
        private StringBuffer line = new StringBuffer();
        private boolean reachedEof = false;

        public OneLiner(File file) throws BuildException {
            this.state = FixCRLF.this.filter.getJavafiles() ? 1 : 0;
            this.srcFile = file;
            try {
                this.reader = new BufferedReader(FixCRLF.this.encoding == null ? new FileReader(file) : new InputStreamReader(new FileInputStream(file), FixCRLF.this.encoding), 8192);
                nextLine();
            } catch (IOException e) {
                throw new BuildException(file + ": " + e.getMessage(), e, FixCRLF.this.getLocation());
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:32:0x0085 A[Catch: IOException -> 0x00c3, TryCatch #0 {IOException -> 0x00c3, blocks: (B:3:0x000e, B:8:0x001f, B:11:0x002f, B:13:0x0037, B:15:0x003a, B:32:0x0085, B:33:0x008b, B:35:0x008e, B:38:0x0099, B:40:0x00a2, B:42:0x00b5, B:43:0x00bd, B:19:0x0043, B:23:0x0059, B:24:0x005f, B:26:0x0068, B:27:0x0071, B:28:0x0077, B:29:0x007d), top: B:48:0x000e }] */
        /* JADX WARN: Removed duplicated region for block: B:54:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void nextLine() throws org.apache.tools.ant.BuildException {
            /*
                Method dump skipped, instruction units count: 237
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.FixCRLF.OneLiner.nextLine():void");
        }

        public String getEofStr() {
            return this.eofStr.substring(0);
        }

        public int getState() {
            return this.state;
        }

        public void setState(int i) {
            this.state = i;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return !this.reachedEof;
        }

        @Override // java.util.Enumeration
        public Object nextElement() throws NoSuchElementException {
            if (!hasMoreElements()) {
                throw new NoSuchElementException("OneLiner");
            }
            BufferLine bufferLine = new BufferLine(this.line.toString(), this.eolStr.substring(0));
            nextLine();
            return bufferLine;
        }

        public void close() throws IOException {
            BufferedReader bufferedReader = this.reader;
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        class BufferLine {
            private int column;
            private String eolStr;
            private String line;
            private int lookahead = -1;
            private int next;

            public BufferLine(String str, String str2) throws BuildException {
                this.next = 0;
                this.column = 0;
                this.next = 0;
                this.column = 0;
                this.line = str;
                this.eolStr = str2;
            }

            public int getNext() {
                return this.next;
            }

            public void setNext(int i) {
                this.next = i;
            }

            public int getLookahead() {
                return this.lookahead;
            }

            public void setLookahead(int i) {
                this.lookahead = i;
            }

            public char getChar(int i) {
                return this.line.charAt(i);
            }

            public char getNextChar() {
                return getChar(this.next);
            }

            public char getNextCharInc() {
                int i = this.next;
                this.next = i + 1;
                return getChar(i);
            }

            public int getColumn() {
                return this.column;
            }

            public void setColumn(int i) {
                this.column = i;
            }

            public int incColumn() {
                int i = this.column;
                this.column = i + 1;
                return i;
            }

            public int length() {
                return this.line.length();
            }

            public int getEolLength() {
                return this.eolStr.length();
            }

            public String getLineString() {
                return this.line;
            }

            public String getEol() {
                return this.eolStr;
            }

            public String substring(int i) {
                return this.line.substring(i);
            }

            public String substring(int i, int i2) {
                return this.line.substring(i, i2);
            }

            public void setState(int i) {
                OneLiner.this.setState(i);
            }

            public int getState() {
                return OneLiner.this.getState();
            }
        }
    }

    public static class AddAsisRemove extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"add", "asis", "remove"};
        }
    }

    public static class CrLf extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"asis", "cr", "lf", "crlf", Os.FAMILY_MAC, Os.FAMILY_UNIX, Os.FAMILY_DOS};
        }
    }
}

package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.EnumeratedAttribute;

/* JADX INFO: loaded from: classes3.dex */
public final class FixCrLfFilter extends BaseParamFilterReader implements ChainableReader {
    private static final char CTRLZ = 26;
    private static final int DEFAULT_TAB_LENGTH = 8;
    private static final int MAX_TAB_LENGTH = 80;
    private static final int MIN_TAB_LENGTH = 2;
    private AddAsisRemove ctrlz;
    private CrLf eol;
    private boolean fixlast;
    private boolean initialized;
    private boolean javafiles;
    private int tabLength;
    private AddAsisRemove tabs;

    public FixCrLfFilter() {
        this.tabLength = 8;
        this.javafiles = false;
        this.fixlast = true;
        this.initialized = false;
        this.tabs = AddAsisRemove.ASIS;
        if (!Os.isFamily(Os.FAMILY_MAC) || Os.isFamily(Os.FAMILY_UNIX)) {
            if (Os.isFamily(Os.FAMILY_DOS)) {
                this.ctrlz = AddAsisRemove.ASIS;
                setEol(CrLf.DOS);
                return;
            } else {
                this.ctrlz = AddAsisRemove.REMOVE;
                setEol(CrLf.UNIX);
                return;
            }
        }
        this.ctrlz = AddAsisRemove.REMOVE;
        setEol(CrLf.MAC);
    }

    public FixCrLfFilter(Reader reader) throws IOException {
        super(reader);
        this.tabLength = 8;
        this.javafiles = false;
        this.fixlast = true;
        this.initialized = false;
        this.tabs = AddAsisRemove.ASIS;
        if (!Os.isFamily(Os.FAMILY_MAC) || Os.isFamily(Os.FAMILY_UNIX)) {
            if (Os.isFamily(Os.FAMILY_DOS)) {
                this.ctrlz = AddAsisRemove.ASIS;
                setEol(CrLf.DOS);
                return;
            } else {
                this.ctrlz = AddAsisRemove.REMOVE;
                setEol(CrLf.UNIX);
                return;
            }
        }
        this.ctrlz = AddAsisRemove.REMOVE;
        setEol(CrLf.MAC);
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        try {
            FixCrLfFilter fixCrLfFilter = new FixCrLfFilter(reader);
            fixCrLfFilter.setJavafiles(getJavafiles());
            fixCrLfFilter.setEol(getEol());
            fixCrLfFilter.setTab(getTab());
            fixCrLfFilter.setTablength(getTablength());
            fixCrLfFilter.setEof(getEof());
            fixCrLfFilter.setFixlast(getFixlast());
            fixCrLfFilter.initInternalFilters();
            return fixCrLfFilter;
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    public AddAsisRemove getEof() {
        return this.ctrlz.newInstance();
    }

    public CrLf getEol() {
        return this.eol.newInstance();
    }

    public boolean getFixlast() {
        return this.fixlast;
    }

    public boolean getJavafiles() {
        return this.javafiles;
    }

    public AddAsisRemove getTab() {
        return this.tabs.newInstance();
    }

    public int getTablength() {
        return this.tabLength;
    }

    private static String calculateEolString(CrLf crLf) {
        return (crLf == CrLf.CR || crLf == CrLf.MAC) ? "\r" : (crLf == CrLf.CRLF || crLf == CrLf.DOS) ? "\r\n" : "\n";
    }

    private void initInternalFilters() {
        this.in = this.ctrlz == AddAsisRemove.REMOVE ? new RemoveEofFilter(this.in) : this.in;
        if (this.eol != CrLf.ASIS) {
            this.in = new NormalizeEolFilter(this.in, calculateEolString(this.eol), getFixlast());
        }
        if (this.tabs != AddAsisRemove.ASIS) {
            if (getJavafiles()) {
                this.in = new MaskJavaTabLiteralsFilter(this.in);
            }
            this.in = this.tabs == AddAsisRemove.ADD ? new AddTabFilter(this.in, getTablength()) : new RemoveTabFilter(this.in, getTablength());
        }
        this.in = this.ctrlz == AddAsisRemove.ADD ? new AddEofFilter(this.in) : this.in;
        this.initialized = true;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public synchronized int read() throws IOException {
        if (!this.initialized) {
            initInternalFilters();
        }
        return this.in.read();
    }

    public void setEof(AddAsisRemove addAsisRemove) {
        this.ctrlz = addAsisRemove.resolve();
    }

    public void setEol(CrLf crLf) {
        this.eol = crLf.resolve();
    }

    public void setFixlast(boolean z) {
        this.fixlast = z;
    }

    public void setJavafiles(boolean z) {
        this.javafiles = z;
    }

    public void setTab(AddAsisRemove addAsisRemove) {
        this.tabs = addAsisRemove.resolve();
    }

    public void setTablength(int i) throws IOException {
        if (i < 2 || i > 80) {
            throw new IOException("tablength must be between 2 and 80");
        }
        this.tabLength = i;
    }

    private static class SimpleFilterReader extends Reader {
        private static final int PREEMPT_BUFFER_LENGTH = 16;
        private Reader in;
        private int[] preempt = new int[16];
        private int preemptIndex = 0;

        public SimpleFilterReader(Reader reader) {
            this.in = reader;
        }

        public void push(char c) {
            push((int) c);
        }

        public void push(int i) {
            try {
                int[] iArr = this.preempt;
                int i2 = this.preemptIndex;
                this.preemptIndex = i2 + 1;
                iArr[i2] = i;
            } catch (ArrayIndexOutOfBoundsException unused) {
                int[] iArr2 = this.preempt;
                int[] iArr3 = new int[iArr2.length * 2];
                System.arraycopy(iArr2, 0, iArr3, 0, iArr2.length);
                this.preempt = iArr3;
                push(i);
            }
        }

        public void push(char[] cArr, int i, int i2) {
            for (int i3 = (i2 + i) - 1; i3 >= i; i3--) {
                push(cArr[i3]);
            }
        }

        public void push(char[] cArr) {
            push(cArr, 0, cArr.length);
        }

        public boolean editsBlocked() {
            Reader reader = this.in;
            return (reader instanceof SimpleFilterReader) && ((SimpleFilterReader) reader).editsBlocked();
        }

        @Override // java.io.Reader
        public int read() throws IOException {
            int i = this.preemptIndex;
            if (i <= 0) {
                return this.in.read();
            }
            int[] iArr = this.preempt;
            int i2 = i - 1;
            this.preemptIndex = i2;
            return iArr[i2];
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.in.close();
        }

        @Override // java.io.Reader
        public void reset() throws IOException {
            this.in.reset();
        }

        @Override // java.io.Reader
        public boolean markSupported() {
            return this.in.markSupported();
        }

        @Override // java.io.Reader
        public boolean ready() throws IOException {
            return this.in.ready();
        }

        @Override // java.io.Reader
        public void mark(int i) throws IOException {
            this.in.mark(i);
        }

        @Override // java.io.Reader
        public long skip(long j) throws IOException {
            return this.in.skip(j);
        }

        @Override // java.io.Reader
        public int read(char[] cArr) throws IOException {
            return read(cArr, 0, cArr.length);
        }

        @Override // java.io.Reader
        public int read(char[] cArr, int i, int i2) throws IOException {
            int i3 = 0;
            int i4 = 0;
            while (true) {
                int i5 = i2 - 1;
                if (i2 <= 0 || (i4 = read()) == -1) {
                    break;
                }
                cArr[i] = (char) i4;
                i3++;
                i++;
                i2 = i5;
            }
            if (i3 == 0 && i4 == -1) {
                return -1;
            }
            return i3;
        }
    }

    private static class MaskJavaTabLiteralsFilter extends SimpleFilterReader {
        private static final int IN_CHAR_CONST = 2;
        private static final int IN_MULTI_COMMENT = 5;
        private static final int IN_SINGLE_COMMENT = 4;
        private static final int IN_STR_CONST = 3;
        private static final int JAVA = 1;
        private static final int TRANS_FROM_MULTI = 8;
        private static final int TRANS_TO_COMMENT = 6;
        private boolean editsBlocked;
        private int state;

        public MaskJavaTabLiteralsFilter(Reader reader) {
            super(reader);
            this.editsBlocked = false;
            this.state = 1;
        }

        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader
        public boolean editsBlocked() {
            return this.editsBlocked || super.editsBlocked();
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader, java.io.Reader
        public int read() throws IOException {
            int i = super.read();
            int i2 = this.state;
            this.editsBlocked = i2 == 2 || i2 == 3;
            switch (i2) {
                case 1:
                    if (i == 34) {
                        this.state = 3;
                    } else if (i == 39) {
                        this.state = 2;
                    } else if (i == 47) {
                        this.state = 6;
                    }
                    return i;
                case 2:
                    if (i == 39) {
                        this.state = 1;
                    }
                    return i;
                case 3:
                    if (i == 34) {
                        this.state = 1;
                    }
                    return i;
                case 4:
                    if (i == 10 || i == 13) {
                        this.state = 1;
                    }
                    return i;
                case 5:
                    if (i == 42) {
                        this.state = 8;
                    }
                    return i;
                case 6:
                    if (i == 34) {
                        this.state = 3;
                    } else if (i == 39) {
                        this.state = 2;
                    } else if (i == 42) {
                        this.state = 5;
                    } else if (i == 47) {
                        this.state = 4;
                    } else {
                        this.state = 1;
                    }
                    return i;
                case 7:
                default:
                    return i;
                case 8:
                    if (i == 47) {
                        this.state = 1;
                    }
                    return i;
            }
        }
    }

    private static class NormalizeEolFilter extends SimpleFilterReader {
        private char[] eol;
        private boolean fixLast;
        private int normalizedEOL;
        private boolean previousWasEOL;

        public NormalizeEolFilter(Reader reader, String str, boolean z) {
            super(reader);
            this.normalizedEOL = 0;
            this.eol = null;
            this.eol = str.toCharArray();
            this.fixLast = z;
        }

        /* JADX WARN: Code restructure failed: missing block: B:37:0x005b, code lost:
        
            if (r7.previousWasEOL == false) goto L38;
         */
        /* JADX WARN: Removed duplicated region for block: B:32:0x0052  */
        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader, java.io.Reader
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public int read() throws java.io.IOException {
            /*
                r7 = this;
                int r0 = super.read()
                int r1 = r7.normalizedEOL
                r2 = 1
                if (r1 != 0) goto L82
                r1 = -1
                r3 = 0
                if (r0 == r1) goto L55
                r4 = 10
                if (r0 == r4) goto L52
                r5 = 13
                if (r0 == r5) goto L32
                r4 = 26
                if (r0 == r4) goto L1a
                goto L2f
            L1a:
                int r4 = super.read()
                if (r4 != r1) goto L2c
                boolean r1 = r7.fixLast
                if (r1 == 0) goto L5f
                boolean r1 = r7.previousWasEOL
                if (r1 != 0) goto L5f
                r7.push(r0)
                goto L5d
            L2c:
                r7.push(r4)
            L2f:
                r1 = r3
            L30:
                r4 = r1
                goto L61
            L32:
                int r1 = super.read()
                int r6 = super.read()
                if (r1 != r5) goto L3f
                if (r6 != r4) goto L3f
                goto L52
            L3f:
                if (r1 != r5) goto L46
                r1 = 2
                r7.push(r6)
                goto L53
            L46:
                if (r1 != r4) goto L4c
                r7.push(r6)
                goto L52
            L4c:
                r7.push(r6)
                r7.push(r1)
            L52:
                r1 = r2
            L53:
                r4 = r3
                goto L61
            L55:
                boolean r1 = r7.fixLast
                if (r1 == 0) goto L5f
                boolean r1 = r7.previousWasEOL
                if (r1 != 0) goto L5f
            L5d:
                r1 = r2
                goto L30
            L5f:
                r4 = r2
                r1 = r3
            L61:
                if (r1 <= 0) goto L7d
            L63:
                int r0 = r1 + (-1)
                if (r1 <= 0) goto L76
                char[] r1 = r7.eol
                r7.push(r1)
                int r1 = r7.normalizedEOL
                char[] r3 = r7.eol
                int r3 = r3.length
                int r1 = r1 + r3
                r7.normalizedEOL = r1
                r1 = r0
                goto L63
            L76:
                r7.previousWasEOL = r2
                int r0 = r7.read()
                goto L85
            L7d:
                if (r4 != 0) goto L85
                r7.previousWasEOL = r3
                goto L85
            L82:
                int r1 = r1 - r2
                r7.normalizedEOL = r1
            L85:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.filters.FixCrLfFilter.NormalizeEolFilter.read():int");
        }
    }

    private static class AddEofFilter extends SimpleFilterReader {
        private int lastChar;

        public AddEofFilter(Reader reader) {
            super(reader);
            this.lastChar = -1;
        }

        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader, java.io.Reader
        public int read() throws IOException {
            int i = super.read();
            if (i == -1) {
                if (this.lastChar != 26) {
                    this.lastChar = 26;
                    return 26;
                }
            } else {
                this.lastChar = i;
            }
            return i;
        }
    }

    private static class RemoveEofFilter extends SimpleFilterReader {
        private int lookAhead;

        public RemoveEofFilter(Reader reader) {
            super(reader);
            this.lookAhead = -1;
            try {
                this.lookAhead = reader.read();
            } catch (IOException unused) {
                this.lookAhead = -1;
            }
        }

        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader, java.io.Reader
        public int read() throws IOException {
            int i = super.read();
            if (i == -1 && this.lookAhead == 26) {
                return -1;
            }
            int i2 = this.lookAhead;
            this.lookAhead = i;
            return i2;
        }
    }

    private static class AddTabFilter extends SimpleFilterReader {
        private int columnNumber;
        private int tabLength;

        public AddTabFilter(Reader reader, int i) {
            super(reader);
            this.columnNumber = 0;
            this.tabLength = 0;
            this.tabLength = i;
        }

        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader, java.io.Reader
        public int read() throws IOException {
            int i;
            int i2 = super.read();
            if (i2 == 9) {
                int i3 = this.columnNumber;
                int i4 = this.tabLength;
                this.columnNumber = (((i3 + i4) - 1) / i4) * i4;
            } else if (i2 == 10 || i2 == 13) {
                this.columnNumber = 0;
            } else if (i2 == 32) {
                this.columnNumber++;
                if (!editsBlocked()) {
                    int i5 = this.columnNumber;
                    int i6 = this.tabLength;
                    int i7 = (((i5 + i6) - 1) / i6) * i6;
                    int i8 = 1;
                    int i9 = 0;
                    while (true) {
                        int i10 = super.read();
                        if (i10 == -1) {
                            break;
                        }
                        if (i10 == 9) {
                            this.columnNumber = i7;
                            i9++;
                            i = this.tabLength;
                        } else if (i10 == 32) {
                            int i11 = this.columnNumber + 1;
                            this.columnNumber = i11;
                            if (i11 == i7) {
                                i9++;
                                i = this.tabLength;
                            } else {
                                i8++;
                            }
                        } else {
                            push(i10);
                            break;
                        }
                        i7 += i;
                        i8 = 0;
                    }
                    while (true) {
                        int i12 = i8 - 1;
                        if (i8 <= 0) {
                            break;
                        }
                        push(' ');
                        this.columnNumber--;
                        i8 = i12;
                    }
                    while (true) {
                        int i13 = i9 - 1;
                        if (i9 <= 0) {
                            break;
                        }
                        push('\t');
                        this.columnNumber -= this.tabLength;
                        i9 = i13;
                    }
                    i2 = super.read();
                    if (i2 == 9) {
                        this.columnNumber += this.tabLength;
                    } else if (i2 == 32) {
                        this.columnNumber++;
                    }
                }
            } else {
                this.columnNumber++;
            }
            return i2;
        }
    }

    private static class RemoveTabFilter extends SimpleFilterReader {
        private int columnNumber;
        private int tabLength;

        public RemoveTabFilter(Reader reader, int i) {
            super(reader);
            this.columnNumber = 0;
            this.tabLength = 0;
            this.tabLength = i;
        }

        @Override // org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader, java.io.Reader
        public int read() throws IOException {
            int i = super.read();
            if (i == 9) {
                int i2 = this.tabLength;
                int i3 = i2 - (this.columnNumber % i2);
                if (!editsBlocked()) {
                    while (i3 > 1) {
                        push(' ');
                        i3--;
                    }
                    i = 32;
                }
                this.columnNumber += i3;
            } else if (i == 10 || i == 13) {
                this.columnNumber = 0;
            } else {
                this.columnNumber++;
            }
            return i;
        }
    }

    public static class AddAsisRemove extends EnumeratedAttribute {
        private static final AddAsisRemove ASIS = newInstance("asis");
        private static final AddAsisRemove ADD = newInstance("add");
        private static final AddAsisRemove REMOVE = newInstance("remove");

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"add", "asis", "remove"};
        }

        public boolean equals(Object obj) {
            return (obj instanceof AddAsisRemove) && getIndex() == ((AddAsisRemove) obj).getIndex();
        }

        public int hashCode() {
            return getIndex();
        }

        AddAsisRemove resolve() throws IllegalStateException {
            AddAsisRemove addAsisRemove = ASIS;
            if (equals(addAsisRemove)) {
                return addAsisRemove;
            }
            AddAsisRemove addAsisRemove2 = ADD;
            if (equals(addAsisRemove2)) {
                return addAsisRemove2;
            }
            AddAsisRemove addAsisRemove3 = REMOVE;
            if (equals(addAsisRemove3)) {
                return addAsisRemove3;
            }
            throw new IllegalStateException("No replacement for " + this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public AddAsisRemove newInstance() {
            return newInstance(getValue());
        }

        public static AddAsisRemove newInstance(String str) {
            AddAsisRemove addAsisRemove = new AddAsisRemove();
            addAsisRemove.setValue(str);
            return addAsisRemove;
        }
    }

    public static class CrLf extends EnumeratedAttribute {
        private static final CrLf ASIS = newInstance("asis");
        private static final CrLf CR = newInstance("cr");
        private static final CrLf CRLF = newInstance("crlf");
        private static final CrLf DOS = newInstance(Os.FAMILY_DOS);
        private static final CrLf LF = newInstance("lf");
        private static final CrLf MAC = newInstance(Os.FAMILY_MAC);
        private static final CrLf UNIX = newInstance(Os.FAMILY_UNIX);

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"asis", "cr", "lf", "crlf", Os.FAMILY_MAC, Os.FAMILY_UNIX, Os.FAMILY_DOS};
        }

        public boolean equals(Object obj) {
            return (obj instanceof CrLf) && getIndex() == ((CrLf) obj).getIndex();
        }

        public int hashCode() {
            return getIndex();
        }

        CrLf resolve() {
            CrLf crLf = ASIS;
            if (equals(crLf)) {
                return crLf;
            }
            CrLf crLf2 = CR;
            if (!equals(crLf2) && !equals(MAC)) {
                crLf2 = CRLF;
                if (!equals(crLf2) && !equals(DOS)) {
                    crLf2 = LF;
                    if (!equals(crLf2) && !equals(UNIX)) {
                        throw new IllegalStateException("No replacement for " + this);
                    }
                }
            }
            return crLf2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CrLf newInstance() {
            return newInstance(getValue());
        }

        public static CrLf newInstance(String str) {
            CrLf crLf = new CrLf();
            crLf.setValue(str);
            return crLf;
        }
    }
}

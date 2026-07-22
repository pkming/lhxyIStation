package org.apache.commons.csv;

import android.telephony.PhoneNumberUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

/* JADX INFO: loaded from: classes3.dex */
public final class CSVFormat implements Serializable {
    public static final CSVFormat DEFAULT;
    public static final CSVFormat EXCEL;
    public static final CSVFormat INFORMIX_UNLOAD;
    public static final CSVFormat INFORMIX_UNLOAD_CSV;
    public static final CSVFormat MYSQL;
    public static final CSVFormat POSTGRESQL_CSV;
    public static final CSVFormat POSTGRESQL_TEXT;
    public static final CSVFormat RFC4180;
    public static final CSVFormat TDF;
    private static final long serialVersionUID = 1;
    private final boolean allowMissingColumnNames;
    private final Character commentMarker;
    private final char delimiter;
    private final Character escapeCharacter;
    private final String[] header;
    private final String[] headerComments;
    private final boolean ignoreEmptyLines;
    private final boolean ignoreHeaderCase;
    private final boolean ignoreSurroundingSpaces;
    private final String nullString;
    private final Character quoteCharacter;
    private final QuoteMode quoteMode;
    private final String recordSeparator;
    private final boolean skipHeaderRecord;
    private final boolean trailingDelimiter;
    private final boolean trim;

    private static boolean isLineBreak(char c) {
        return c == '\n' || c == '\r';
    }

    public enum Predefined {
        Default(CSVFormat.DEFAULT),
        Excel(CSVFormat.EXCEL),
        InformixUnload(CSVFormat.INFORMIX_UNLOAD),
        InformixUnloadCsv(CSVFormat.INFORMIX_UNLOAD_CSV),
        MySQL(CSVFormat.MYSQL),
        PostgreSQLCsv(CSVFormat.POSTGRESQL_CSV),
        PostgreSQLText(CSVFormat.POSTGRESQL_TEXT),
        RFC4180(CSVFormat.RFC4180),
        TDF(CSVFormat.TDF);

        private final CSVFormat format;

        Predefined(CSVFormat cSVFormat) {
            this.format = cSVFormat;
        }

        public CSVFormat getFormat() {
            return this.format;
        }
    }

    static {
        CSVFormat cSVFormat = new CSVFormat(PhoneNumberUtils.PAUSE, Constants.DOUBLE_QUOTE_CHAR, null, null, null, false, true, "\r\n", null, null, null, false, false, false, false, false);
        DEFAULT = cSVFormat;
        EXCEL = cSVFormat.withIgnoreEmptyLines(false).withAllowMissingColumnNames();
        INFORMIX_UNLOAD = cSVFormat.withDelimiter('|').withEscape('\\').withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
        INFORMIX_UNLOAD_CSV = cSVFormat.withDelimiter(PhoneNumberUtils.PAUSE).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n');
        MYSQL = cSVFormat.withDelimiter('\t').withEscape('\\').withIgnoreEmptyLines(false).withQuote((Character) null).withRecordSeparator('\n').withNullString("\\N").withQuoteMode(QuoteMode.ALL_NON_NULL);
        POSTGRESQL_CSV = cSVFormat.withDelimiter(PhoneNumberUtils.PAUSE).withEscape(Constants.DOUBLE_QUOTE_CHAR).withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n').withNullString("").withQuoteMode(QuoteMode.ALL_NON_NULL);
        POSTGRESQL_TEXT = cSVFormat.withDelimiter('\t').withEscape(Constants.DOUBLE_QUOTE_CHAR).withIgnoreEmptyLines(false).withQuote(Constants.DOUBLE_QUOTE_CHAR).withRecordSeparator('\n').withNullString("\\N").withQuoteMode(QuoteMode.ALL_NON_NULL);
        RFC4180 = cSVFormat.withIgnoreEmptyLines(false);
        TDF = cSVFormat.withDelimiter('\t').withIgnoreSurroundingSpaces();
    }

    private static boolean isLineBreak(Character ch) {
        return ch != null && isLineBreak(ch.charValue());
    }

    public static CSVFormat newFormat(char c) {
        return new CSVFormat(c, null, null, null, null, false, false, null, null, null, null, false, false, false, false, false);
    }

    public static CSVFormat valueOf(String str) {
        return Predefined.valueOf(str).getFormat();
    }

    private CSVFormat(char c, Character ch, QuoteMode quoteMode, Character ch2, Character ch3, boolean z, boolean z2, String str, String str2, Object[] objArr, String[] strArr, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7) {
        this.delimiter = c;
        this.quoteCharacter = ch;
        this.quoteMode = quoteMode;
        this.commentMarker = ch2;
        this.escapeCharacter = ch3;
        this.ignoreSurroundingSpaces = z;
        this.allowMissingColumnNames = z4;
        this.ignoreEmptyLines = z2;
        this.recordSeparator = str;
        this.nullString = str2;
        this.headerComments = toStringArray(objArr);
        this.header = strArr == null ? null : (String[]) strArr.clone();
        this.skipHeaderRecord = z3;
        this.ignoreHeaderCase = z5;
        this.trailingDelimiter = z7;
        this.trim = z6;
        validate();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CSVFormat cSVFormat = (CSVFormat) obj;
        if (this.delimiter != cSVFormat.delimiter || this.quoteMode != cSVFormat.quoteMode) {
            return false;
        }
        Character ch = this.quoteCharacter;
        if (ch == null) {
            if (cSVFormat.quoteCharacter != null) {
                return false;
            }
        } else if (!ch.equals(cSVFormat.quoteCharacter)) {
            return false;
        }
        Character ch2 = this.commentMarker;
        if (ch2 == null) {
            if (cSVFormat.commentMarker != null) {
                return false;
            }
        } else if (!ch2.equals(cSVFormat.commentMarker)) {
            return false;
        }
        Character ch3 = this.escapeCharacter;
        if (ch3 == null) {
            if (cSVFormat.escapeCharacter != null) {
                return false;
            }
        } else if (!ch3.equals(cSVFormat.escapeCharacter)) {
            return false;
        }
        String str = this.nullString;
        if (str == null) {
            if (cSVFormat.nullString != null) {
                return false;
            }
        } else if (!str.equals(cSVFormat.nullString)) {
            return false;
        }
        if (!Arrays.equals(this.header, cSVFormat.header) || this.ignoreSurroundingSpaces != cSVFormat.ignoreSurroundingSpaces || this.ignoreEmptyLines != cSVFormat.ignoreEmptyLines || this.skipHeaderRecord != cSVFormat.skipHeaderRecord) {
            return false;
        }
        String str2 = this.recordSeparator;
        if (str2 == null) {
            if (cSVFormat.recordSeparator != null) {
                return false;
            }
        } else if (!str2.equals(cSVFormat.recordSeparator)) {
            return false;
        }
        return true;
    }

    public String format(Object... objArr) {
        StringWriter stringWriter = new StringWriter();
        try {
            CSVPrinter cSVPrinter = new CSVPrinter(stringWriter, this);
            try {
                cSVPrinter.printRecord(objArr);
                String strTrim = stringWriter.toString().trim();
                cSVPrinter.close();
                return strTrim;
            } finally {
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean getAllowMissingColumnNames() {
        return this.allowMissingColumnNames;
    }

    public Character getCommentMarker() {
        return this.commentMarker;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public Character getEscapeCharacter() {
        return this.escapeCharacter;
    }

    public String[] getHeader() {
        String[] strArr = this.header;
        if (strArr != null) {
            return (String[]) strArr.clone();
        }
        return null;
    }

    public String[] getHeaderComments() {
        String[] strArr = this.headerComments;
        if (strArr != null) {
            return (String[]) strArr.clone();
        }
        return null;
    }

    public boolean getIgnoreEmptyLines() {
        return this.ignoreEmptyLines;
    }

    public boolean getIgnoreHeaderCase() {
        return this.ignoreHeaderCase;
    }

    public boolean getIgnoreSurroundingSpaces() {
        return this.ignoreSurroundingSpaces;
    }

    public String getNullString() {
        return this.nullString;
    }

    public Character getQuoteCharacter() {
        return this.quoteCharacter;
    }

    public QuoteMode getQuoteMode() {
        return this.quoteMode;
    }

    public String getRecordSeparator() {
        return this.recordSeparator;
    }

    public boolean getSkipHeaderRecord() {
        return this.skipHeaderRecord;
    }

    public boolean getTrailingDelimiter() {
        return this.trailingDelimiter;
    }

    public boolean getTrim() {
        return this.trim;
    }

    public int hashCode() {
        int i = (this.delimiter + 31) * 31;
        QuoteMode quoteMode = this.quoteMode;
        int iHashCode = (i + (quoteMode == null ? 0 : quoteMode.hashCode())) * 31;
        Character ch = this.quoteCharacter;
        int iHashCode2 = (iHashCode + (ch == null ? 0 : ch.hashCode())) * 31;
        Character ch2 = this.commentMarker;
        int iHashCode3 = (iHashCode2 + (ch2 == null ? 0 : ch2.hashCode())) * 31;
        Character ch3 = this.escapeCharacter;
        int iHashCode4 = (iHashCode3 + (ch3 == null ? 0 : ch3.hashCode())) * 31;
        String str = this.nullString;
        int iHashCode5 = (((((((((iHashCode4 + (str == null ? 0 : str.hashCode())) * 31) + (this.ignoreSurroundingSpaces ? 1231 : 1237)) * 31) + (this.ignoreHeaderCase ? 1231 : 1237)) * 31) + (this.ignoreEmptyLines ? 1231 : 1237)) * 31) + (this.skipHeaderRecord ? 1231 : 1237)) * 31;
        String str2 = this.recordSeparator;
        return ((iHashCode5 + (str2 != null ? str2.hashCode() : 0)) * 31) + Arrays.hashCode(this.header);
    }

    public boolean isCommentMarkerSet() {
        return this.commentMarker != null;
    }

    public boolean isEscapeCharacterSet() {
        return this.escapeCharacter != null;
    }

    public boolean isNullStringSet() {
        return this.nullString != null;
    }

    public boolean isQuoteCharacterSet() {
        return this.quoteCharacter != null;
    }

    public CSVParser parse(Reader reader) throws IOException {
        return new CSVParser(reader, this);
    }

    public CSVPrinter print(Appendable appendable) throws IOException {
        return new CSVPrinter(appendable, this);
    }

    public CSVPrinter printer() throws IOException {
        return new CSVPrinter(System.out, this);
    }

    public CSVPrinter print(File file, Charset charset) throws IOException {
        return new CSVPrinter(new OutputStreamWriter(new FileOutputStream(file), charset), this);
    }

    public CSVPrinter print(Path path, Charset charset) throws IOException {
        return print(Files.newBufferedWriter(path, charset, new OpenOption[0]));
    }

    public void print(Object obj, Appendable appendable, boolean z) throws IOException {
        CharSequence string;
        if (obj == null) {
            if (this.nullString == null) {
                string = "";
            } else if (QuoteMode.ALL == this.quoteMode) {
                string = this.quoteCharacter + this.nullString + this.quoteCharacter;
            } else {
                string = this.nullString;
            }
        } else {
            string = obj instanceof CharSequence ? (CharSequence) obj : obj.toString();
        }
        if (getTrim()) {
            string = trim(string);
        }
        CharSequence charSequence = string;
        print(obj, charSequence, 0, charSequence.length(), appendable, z);
    }

    private void print(Object obj, CharSequence charSequence, int i, int i2, Appendable appendable, boolean z) throws IOException {
        if (!z) {
            appendable.append(getDelimiter());
        }
        if (obj == null) {
            appendable.append(charSequence);
            return;
        }
        if (isQuoteCharacterSet()) {
            printAndQuote(obj, charSequence, i, i2, appendable, z);
        } else if (isEscapeCharacterSet()) {
            printAndEscape(charSequence, i, i2, appendable);
        } else {
            appendable.append(charSequence, i, i2 + i);
        }
    }

    private void printAndEscape(CharSequence charSequence, int i, int i2, Appendable appendable) throws IOException {
        int i3 = i2 + i;
        char delimiter = getDelimiter();
        char cCharValue = getEscapeCharacter().charValue();
        int i4 = i;
        while (i < i3) {
            char cCharAt = charSequence.charAt(i);
            if (cCharAt == '\r' || cCharAt == '\n' || cCharAt == delimiter || cCharAt == cCharValue) {
                if (i > i4) {
                    appendable.append(charSequence, i4, i);
                }
                if (cCharAt == '\n') {
                    cCharAt = 'n';
                } else if (cCharAt == '\r') {
                    cCharAt = 'r';
                }
                appendable.append(cCharValue);
                appendable.append(cCharAt);
                i4 = i + 1;
            }
            i++;
        }
        if (i > i4) {
            appendable.append(charSequence, i4, i);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x007b A[PHI: r11
      0x007b: PHI (r11v8 int) = (r11v7 int), (r11v9 int) binds: [B:47:0x0070, B:49:0x0078] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x007e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void printAndQuote(java.lang.Object r8, java.lang.CharSequence r9, int r10, int r11, java.lang.Appendable r12, boolean r13) throws java.io.IOException {
        /*
            r7 = this;
            int r0 = r10 + r11
            char r1 = r7.getDelimiter()
            java.lang.Character r2 = r7.getQuoteCharacter()
            char r2 = r2.charValue()
            org.apache.commons.csv.QuoteMode r3 = r7.getQuoteMode()
            if (r3 != 0) goto L16
            org.apache.commons.csv.QuoteMode r3 = org.apache.commons.csv.QuoteMode.MINIMAL
        L16:
            int[] r4 = org.apache.commons.csv.CSVFormat.AnonymousClass1.$SwitchMap$org$apache$commons$csv$QuoteMode
            int r5 = r3.ordinal()
            r4 = r4[r5]
            r5 = 1
            if (r4 == r5) goto La2
            r6 = 2
            if (r4 == r6) goto La2
            r6 = 3
            if (r4 == r6) goto L9f
            r8 = 4
            if (r4 == r8) goto L9b
            r8 = 5
            if (r4 != r8) goto L82
            r8 = 0
            if (r11 > 0) goto L36
            if (r13 == 0) goto L33
        L32:
            goto L34
        L33:
            r5 = r8
        L34:
            r11 = r10
            goto L7c
        L36:
            char r11 = r9.charAt(r10)
            r3 = 32
            r4 = 35
            if (r13 == 0) goto L55
            if (r11 < r3) goto L34
            r13 = 33
            if (r11 <= r13) goto L48
            if (r11 < r4) goto L34
        L48:
            r13 = 43
            if (r11 <= r13) goto L50
            r13 = 45
            if (r11 < r13) goto L34
        L50:
            r13 = 126(0x7e, float:1.77E-43)
            if (r11 <= r13) goto L55
            goto L32
        L55:
            if (r11 > r4) goto L58
            goto L32
        L58:
            r11 = r10
        L59:
            if (r11 >= r0) goto L70
            char r13 = r9.charAt(r11)
            r4 = 10
            if (r13 == r4) goto L6f
            r4 = 13
            if (r13 == r4) goto L6f
            if (r13 == r2) goto L6f
            if (r13 != r1) goto L6c
            goto L6f
        L6c:
            int r11 = r11 + 1
            goto L59
        L6f:
            r8 = r5
        L70:
            if (r8 != 0) goto L7b
            int r11 = r0 + (-1)
            char r13 = r9.charAt(r11)
            if (r13 > r3) goto L7b
            goto L7c
        L7b:
            r5 = r8
        L7c:
            if (r5 != 0) goto La3
            r12.append(r9, r10, r0)
            return
        L82:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Unexpected Quote value: "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.StringBuilder r9 = r9.append(r3)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9)
            throw r8
        L9b:
            r7.printAndEscape(r9, r10, r11, r12)
            return
        L9f:
            boolean r8 = r8 instanceof java.lang.Number
            r5 = r5 ^ r8
        La2:
            r11 = r10
        La3:
            if (r5 != 0) goto La9
            r12.append(r9, r10, r0)
            return
        La9:
            r12.append(r2)
        Lac:
            if (r11 >= r0) goto Lbd
            char r8 = r9.charAt(r11)
            if (r8 != r2) goto Lba
            int r8 = r11 + 1
            r12.append(r9, r10, r8)
            r10 = r11
        Lba:
            int r11 = r11 + 1
            goto Lac
        Lbd:
            r12.append(r9, r10, r11)
            r12.append(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.csv.CSVFormat.printAndQuote(java.lang.Object, java.lang.CharSequence, int, int, java.lang.Appendable, boolean):void");
    }

    /* JADX INFO: renamed from: org.apache.commons.csv.CSVFormat$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$csv$QuoteMode;

        static {
            int[] iArr = new int[QuoteMode.values().length];
            $SwitchMap$org$apache$commons$csv$QuoteMode = iArr;
            try {
                iArr[QuoteMode.ALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$QuoteMode[QuoteMode.ALL_NON_NULL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$QuoteMode[QuoteMode.NON_NUMERIC.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$QuoteMode[QuoteMode.NONE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$QuoteMode[QuoteMode.MINIMAL.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public void println(Appendable appendable) throws IOException {
        if (getTrailingDelimiter()) {
            appendable.append(getDelimiter());
        }
        String str = this.recordSeparator;
        if (str != null) {
            appendable.append(str);
        }
    }

    public void printRecord(Appendable appendable, Object... objArr) throws IOException {
        int i = 0;
        while (i < objArr.length) {
            print(objArr[i], appendable, i == 0);
            i++;
        }
        println(appendable);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Delimiter=<").append(this.delimiter).append('>');
        if (isEscapeCharacterSet()) {
            sb.append(' ');
            sb.append("Escape=<").append(this.escapeCharacter).append('>');
        }
        if (isQuoteCharacterSet()) {
            sb.append(' ');
            sb.append("QuoteChar=<").append(this.quoteCharacter).append('>');
        }
        if (isCommentMarkerSet()) {
            sb.append(' ');
            sb.append("CommentStart=<").append(this.commentMarker).append('>');
        }
        if (isNullStringSet()) {
            sb.append(' ');
            sb.append("NullString=<").append(this.nullString).append('>');
        }
        if (this.recordSeparator != null) {
            sb.append(' ');
            sb.append("RecordSeparator=<").append(this.recordSeparator).append('>');
        }
        if (getIgnoreEmptyLines()) {
            sb.append(" EmptyLines:ignored");
        }
        if (getIgnoreSurroundingSpaces()) {
            sb.append(" SurroundingSpaces:ignored");
        }
        if (getIgnoreHeaderCase()) {
            sb.append(" IgnoreHeaderCase:ignored");
        }
        sb.append(" SkipHeaderRecord:").append(this.skipHeaderRecord);
        if (this.headerComments != null) {
            sb.append(' ');
            sb.append("HeaderComments:").append(Arrays.toString(this.headerComments));
        }
        if (this.header != null) {
            sb.append(' ');
            sb.append("Header:").append(Arrays.toString(this.header));
        }
        return sb.toString();
    }

    private String[] toStringArray(Object[] objArr) {
        if (objArr == null) {
            return null;
        }
        String[] strArr = new String[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            Object obj = objArr[i];
            strArr[i] = obj == null ? null : obj.toString();
        }
        return strArr;
    }

    private CharSequence trim(CharSequence charSequence) {
        if (charSequence instanceof String) {
            return ((String) charSequence).trim();
        }
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) <= ' ') {
            i++;
        }
        int i2 = length;
        while (i < i2 && charSequence.charAt(i2 - 1) <= ' ') {
            i2--;
        }
        return (i > 0 || i2 < length) ? charSequence.subSequence(i, i2) : charSequence;
    }

    private void validate() throws IllegalArgumentException {
        if (isLineBreak(this.delimiter)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }
        Character ch = this.quoteCharacter;
        if (ch != null && this.delimiter == ch.charValue()) {
            throw new IllegalArgumentException("The quoteChar character and the delimiter cannot be the same ('" + this.quoteCharacter + "')");
        }
        Character ch2 = this.escapeCharacter;
        if (ch2 != null && this.delimiter == ch2.charValue()) {
            throw new IllegalArgumentException("The escape character and the delimiter cannot be the same ('" + this.escapeCharacter + "')");
        }
        Character ch3 = this.commentMarker;
        if (ch3 != null && this.delimiter == ch3.charValue()) {
            throw new IllegalArgumentException("The comment start character and the delimiter cannot be the same ('" + this.commentMarker + "')");
        }
        Character ch4 = this.quoteCharacter;
        if (ch4 != null && ch4.equals(this.commentMarker)) {
            throw new IllegalArgumentException("The comment start character and the quoteChar cannot be the same ('" + this.commentMarker + "')");
        }
        Character ch5 = this.escapeCharacter;
        if (ch5 != null && ch5.equals(this.commentMarker)) {
            throw new IllegalArgumentException("The comment start and the escape character cannot be the same ('" + this.commentMarker + "')");
        }
        if (this.escapeCharacter == null && this.quoteMode == QuoteMode.NONE) {
            throw new IllegalArgumentException("No quotes mode set but no escape character is set");
        }
        if (this.header != null) {
            HashSet hashSet = new HashSet();
            for (String str : this.header) {
                if (!hashSet.add(str)) {
                    throw new IllegalArgumentException("The header contains a duplicate entry: '" + str + "' in " + Arrays.toString(this.header));
                }
            }
        }
    }

    public CSVFormat withAllowMissingColumnNames() {
        return withAllowMissingColumnNames(true);
    }

    public CSVFormat withAllowMissingColumnNames(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, z, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withCommentMarker(char c) {
        return withCommentMarker(Character.valueOf(c));
    }

    public CSVFormat withCommentMarker(Character ch) {
        if (isLineBreak(ch)) {
            throw new IllegalArgumentException("The comment start marker character cannot be a line break");
        }
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, ch, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withDelimiter(char c) {
        if (isLineBreak(c)) {
            throw new IllegalArgumentException("The delimiter cannot be a line break");
        }
        return new CSVFormat(c, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withEscape(char c) {
        return withEscape(Character.valueOf(c));
    }

    public CSVFormat withEscape(Character ch) {
        if (isLineBreak(ch)) {
            throw new IllegalArgumentException("The escape character cannot be a line break");
        }
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, ch, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withFirstRecordAsHeader() {
        return withHeader(new String[0]).withSkipHeaderRecord();
    }

    public CSVFormat withHeader(Class<? extends Enum<?>> cls) {
        String[] strArr;
        if (cls != null) {
            Enum[] enumArr = (Enum[]) cls.getEnumConstants();
            strArr = new String[enumArr.length];
            for (int i = 0; i < enumArr.length; i++) {
                strArr[i] = enumArr[i].name();
            }
        } else {
            strArr = null;
        }
        return withHeader(strArr);
    }

    public CSVFormat withHeader(ResultSet resultSet) throws SQLException {
        return withHeader(resultSet != null ? resultSet.getMetaData() : null);
    }

    public CSVFormat withHeader(ResultSetMetaData resultSetMetaData) throws SQLException {
        String[] strArr;
        if (resultSetMetaData != null) {
            int columnCount = resultSetMetaData.getColumnCount();
            strArr = new String[columnCount];
            int i = 0;
            while (i < columnCount) {
                int i2 = i + 1;
                strArr[i] = resultSetMetaData.getColumnLabel(i2);
                i = i2;
            }
        } else {
            strArr = null;
        }
        return withHeader(strArr);
    }

    public CSVFormat withHeader(String... strArr) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, strArr, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withHeaderComments(Object... objArr) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, objArr, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withIgnoreEmptyLines() {
        return withIgnoreEmptyLines(true);
    }

    public CSVFormat withIgnoreEmptyLines(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, z, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withIgnoreHeaderCase() {
        return withIgnoreHeaderCase(true);
    }

    public CSVFormat withIgnoreHeaderCase(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, z, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withIgnoreSurroundingSpaces() {
        return withIgnoreSurroundingSpaces(true);
    }

    public CSVFormat withIgnoreSurroundingSpaces(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, z, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withNullString(String str) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, str, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withQuote(char c) {
        return withQuote(Character.valueOf(c));
    }

    public CSVFormat withQuote(Character ch) {
        if (isLineBreak(ch)) {
            throw new IllegalArgumentException("The quoteChar cannot be a line break");
        }
        return new CSVFormat(this.delimiter, ch, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withQuoteMode(QuoteMode quoteMode) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withRecordSeparator(char c) {
        return withRecordSeparator(String.valueOf(c));
    }

    public CSVFormat withRecordSeparator(String str) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, str, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withSkipHeaderRecord() {
        return withSkipHeaderRecord(true);
    }

    public CSVFormat withSkipHeaderRecord(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, z, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, this.trailingDelimiter);
    }

    public CSVFormat withTrailingDelimiter() {
        return withTrailingDelimiter(true);
    }

    public CSVFormat withTrailingDelimiter(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, this.trim, z);
    }

    public CSVFormat withTrim() {
        return withTrim(true);
    }

    public CSVFormat withTrim(boolean z) {
        return new CSVFormat(this.delimiter, this.quoteCharacter, this.quoteMode, this.commentMarker, this.escapeCharacter, this.ignoreSurroundingSpaces, this.ignoreEmptyLines, this.recordSeparator, this.nullString, this.headerComments, this.header, this.skipHeaderRecord, this.allowMissingColumnNames, this.ignoreHeaderCase, z, this.trailingDelimiter);
    }
}

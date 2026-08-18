package org.apache.commons.csv;

import android.provider.MediaStore;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import org.apache.commons.csv.Token;

/* JADX INFO: loaded from: classes3.dex */
public final class CSVParser implements Iterable<CSVRecord>, Closeable {
    private final long characterOffset;
    private final CSVFormat format;
    private final Map<String, Integer> headerMap;
    private final Lexer lexer;
    private final List<String> recordList;
    private long recordNumber;
    private final Token reusableToken;

    public static CSVParser parse(File file, Charset charset, CSVFormat cSVFormat) throws IOException {
        Assertions.notNull(file, "file");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        return new CSVParser(new InputStreamReader(new FileInputStream(file), charset), cSVFormat);
    }

    public static CSVParser parse(InputStream inputStream, Charset charset, CSVFormat cSVFormat) throws IOException {
        Assertions.notNull(inputStream, "inputStream");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        return parse(new InputStreamReader(inputStream, charset), cSVFormat);
    }

    public static CSVParser parse(Path path, Charset charset, CSVFormat cSVFormat) throws IOException {
        Assertions.notNull(path, "path");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        return parse(Files.newBufferedReader(path, charset), cSVFormat);
    }

    public static CSVParser parse(Reader reader, CSVFormat cSVFormat) throws IOException {
        return new CSVParser(reader, cSVFormat);
    }

    public static CSVParser parse(String str, CSVFormat cSVFormat) throws IOException {
        Assertions.notNull(str, "string");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        return new CSVParser(new StringReader(str), cSVFormat);
    }

    public static CSVParser parse(URL url, Charset charset, CSVFormat cSVFormat) throws IOException {
        Assertions.notNull(url, "url");
        Assertions.notNull(charset, "charset");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        return new CSVParser(new InputStreamReader(url.openStream(), charset), cSVFormat);
    }

    public CSVParser(Reader reader, CSVFormat cSVFormat) throws IOException {
        this(reader, cSVFormat, 0L, 1L);
    }

    public CSVParser(Reader reader, CSVFormat cSVFormat, long j, long j2) throws IOException {
        this.recordList = new ArrayList();
        this.reusableToken = new Token();
        Assertions.notNull(reader, "reader");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        this.format = cSVFormat;
        this.lexer = new Lexer(cSVFormat, new ExtendedBufferedReader(reader));
        this.headerMap = initializeHeader();
        this.characterOffset = j;
        this.recordNumber = j2 - 1;
    }

    private void addRecordValue(boolean z) {
        String string = this.reusableToken.content.toString();
        if (this.format.getTrim()) {
            string = string.trim();
        }
        if (z && string.isEmpty() && this.format.getTrailingDelimiter()) {
            return;
        }
        String nullString = this.format.getNullString();
        List<String> list = this.recordList;
        if (string.equals(nullString)) {
            string = null;
        }
        list.add(string);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Lexer lexer = this.lexer;
        if (lexer != null) {
            lexer.close();
        }
    }

    public long getCurrentLineNumber() {
        return this.lexer.getCurrentLineNumber();
    }

    public String getFirstEndOfLine() {
        return this.lexer.getFirstEol();
    }

    public Map<String, Integer> getHeaderMap() {
        if (this.headerMap == null) {
            return null;
        }
        return new LinkedHashMap(this.headerMap);
    }

    public long getRecordNumber() {
        return this.recordNumber;
    }

    public List<CSVRecord> getRecords() throws IOException {
        ArrayList arrayList = new ArrayList();
        while (true) {
            CSVRecord cSVRecordNextRecord = nextRecord();
            if (cSVRecordNextRecord == null) {
                return arrayList;
            }
            arrayList.add(cSVRecordNextRecord);
        }
    }

    private Map<String, Integer> initializeHeader() throws IOException {
        String[] header = this.format.getHeader();
        if (header == null) {
            return null;
        }
        Map<String, Integer> treeMap = this.format.getIgnoreHeaderCase() ? new TreeMap<>(String.CASE_INSENSITIVE_ORDER) : new LinkedHashMap<>();
        if (header.length == 0) {
            CSVRecord cSVRecordNextRecord = nextRecord();
            header = cSVRecordNextRecord != null ? cSVRecordNextRecord.values() : null;
        } else if (this.format.getSkipHeaderRecord()) {
            nextRecord();
        }
        if (header != null) {
            for (int i = 0; i < header.length; i++) {
                String str = header[i];
                boolean zContainsKey = treeMap.containsKey(str);
                boolean z = str == null || str.trim().isEmpty();
                if (zContainsKey && (!z || !this.format.getAllowMissingColumnNames())) {
                    throw new IllegalArgumentException("The header contains a duplicate name: \"" + str + "\" in " + Arrays.toString(header));
                }
                treeMap.put(str, Integer.valueOf(i));
            }
        }
        return treeMap;
    }

    public boolean isClosed() {
        return this.lexer.isClosed();
    }

    @Override // java.lang.Iterable
    public Iterator<CSVRecord> iterator() {
        return new Iterator<CSVRecord>() { // from class: org.apache.commons.csv.CSVParser.1
            private CSVRecord current;

            private CSVRecord getNextRecord() {
                try {
                    return CSVParser.this.nextRecord();
                } catch (IOException e) {
                    throw new IllegalStateException(e.getClass().getSimpleName() + " reading next record: " + e.toString(), e);
                }
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                if (CSVParser.this.isClosed()) {
                    return false;
                }
                if (this.current == null) {
                    this.current = getNextRecord();
                }
                return this.current != null;
            }

            @Override // java.util.Iterator
            public CSVRecord next() {
                if (CSVParser.this.isClosed()) {
                    throw new NoSuchElementException("CSVParser has been closed");
                }
                CSVRecord nextRecord = this.current;
                this.current = null;
                if (nextRecord == null && (nextRecord = getNextRecord()) == null) {
                    throw new NoSuchElementException("No more CSV records available");
                }
                return nextRecord;
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    CSVRecord nextRecord() throws IOException {
        this.recordList.clear();
        long characterPosition = this.lexer.getCharacterPosition() + this.characterOffset;
        StringBuilder sb = null;
        do {
            this.reusableToken.reset();
            this.lexer.nextToken(this.reusableToken);
            int i = AnonymousClass2.$SwitchMap$org$apache$commons$csv$Token$Type[this.reusableToken.type.ordinal()];
            if (i == 1) {
                addRecordValue(false);
            } else if (i == 2) {
                addRecordValue(true);
            } else if (i != 3) {
                if (i == 4) {
                    throw new IOException("(line " + getCurrentLineNumber() + ") invalid parse sequence");
                }
                if (i == 5) {
                    if (sb == null) {
                        sb = new StringBuilder();
                    } else {
                        sb.append('\n');
                    }
                    sb.append((CharSequence) this.reusableToken.content);
                    this.reusableToken.type = Token.Type.TOKEN;
                } else {
                    throw new IllegalStateException("Unexpected Token type: " + this.reusableToken.type);
                }
            } else if (this.reusableToken.isReady) {
                addRecordValue(true);
            }
        } while (this.reusableToken.type == Token.Type.TOKEN);
        if (this.recordList.isEmpty()) {
            return null;
        }
        this.recordNumber++;
        String string = sb != null ? sb.toString() : null;
        List<String> list = this.recordList;
        return new CSVRecord((String[]) list.toArray(new String[list.size()]), this.headerMap, string, this.recordNumber, characterPosition);
    }

    /* JADX INFO: renamed from: org.apache.commons.csv.CSVParser$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$csv$Token$Type;

        static {
            int[] iArr = new int[Token.Type.values().length];
            $SwitchMap$org$apache$commons$csv$Token$Type = iArr;
            try {
                iArr[Token.Type.TOKEN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$Token$Type[Token.Type.EORECORD.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$Token$Type[Token.Type.EOF.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$Token$Type[Token.Type.INVALID.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$org$apache$commons$csv$Token$Type[Token.Type.COMMENT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }
}

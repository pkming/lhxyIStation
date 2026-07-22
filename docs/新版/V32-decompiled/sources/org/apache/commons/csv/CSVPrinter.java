package org.apache.commons.csv;

import android.provider.MediaStore;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public final class CSVPrinter implements Flushable, Closeable {
    private final CSVFormat format;
    private boolean newRecord = true;
    private final Appendable out;

    public CSVPrinter(Appendable appendable, CSVFormat cSVFormat) throws IOException {
        Assertions.notNull(appendable, "out");
        Assertions.notNull(cSVFormat, MediaStore.Files.FileColumns.FORMAT);
        this.out = appendable;
        this.format = cSVFormat;
        if (cSVFormat.getHeaderComments() != null) {
            for (String str : cSVFormat.getHeaderComments()) {
                if (str != null) {
                    printComment(str);
                }
            }
        }
        if (cSVFormat.getHeader() == null || cSVFormat.getSkipHeaderRecord()) {
            return;
        }
        printRecord(cSVFormat.getHeader());
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Appendable appendable = this.out;
        if (appendable instanceof Closeable) {
            ((Closeable) appendable).close();
        }
    }

    @Override // java.io.Flushable
    public void flush() throws IOException {
        Appendable appendable = this.out;
        if (appendable instanceof Flushable) {
            ((Flushable) appendable).flush();
        }
    }

    public Appendable getOut() {
        return this.out;
    }

    public void print(Object obj) throws IOException {
        this.format.print(obj, this.out, this.newRecord);
        this.newRecord = false;
    }

    public void printComment(String str) throws IOException {
        if (this.format.isCommentMarkerSet()) {
            if (!this.newRecord) {
                println();
            }
            this.out.append(this.format.getCommentMarker().charValue());
            this.out.append(' ');
            int i = 0;
            while (i < str.length()) {
                char cCharAt = str.charAt(i);
                if (cCharAt == '\n') {
                    println();
                    this.out.append(this.format.getCommentMarker().charValue());
                    this.out.append(' ');
                } else if (cCharAt == '\r') {
                    int i2 = i + 1;
                    if (i2 < str.length() && str.charAt(i2) == '\n') {
                        i = i2;
                    }
                    println();
                    this.out.append(this.format.getCommentMarker().charValue());
                    this.out.append(' ');
                } else {
                    this.out.append(cCharAt);
                }
                i++;
            }
            println();
        }
    }

    public void println() throws IOException {
        this.format.println(this.out);
        this.newRecord = true;
    }

    public void printRecord(Iterable<?> iterable) throws IOException {
        Iterator<?> it = iterable.iterator();
        while (it.hasNext()) {
            print(it.next());
        }
        println();
    }

    public void printRecord(Object... objArr) throws IOException {
        this.format.printRecord(this.out, objArr);
        this.newRecord = true;
    }

    public void printRecords(Iterable<?> iterable) throws IOException {
        for (Object obj : iterable) {
            if (obj instanceof Object[]) {
                printRecord((Object[]) obj);
            } else if (obj instanceof Iterable) {
                printRecord((Iterable<?>) obj);
            } else {
                printRecord(obj);
            }
        }
    }

    public void printRecords(Object... objArr) throws IOException {
        for (Object obj : objArr) {
            if (obj instanceof Object[]) {
                printRecord((Object[]) obj);
            } else if (obj instanceof Iterable) {
                printRecord((Iterable<?>) obj);
            } else {
                printRecord(obj);
            }
        }
    }

    public void printRecords(ResultSet resultSet) throws SQLException, IOException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                print(resultSet.getObject(i));
            }
            println();
        }
    }
}

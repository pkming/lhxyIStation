package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.net.util.Charsets;

/* JADX INFO: loaded from: classes3.dex */
public class FTPListParseEngine {
    private ListIterator<String> _internalIterator;
    private List<String> entries;
    private final FTPFileEntryParser parser;

    public FTPListParseEngine(FTPFileEntryParser fTPFileEntryParser) {
        LinkedList linkedList = new LinkedList();
        this.entries = linkedList;
        this._internalIterator = linkedList.listIterator();
        this.parser = fTPFileEntryParser;
    }

    public void readServerList(InputStream inputStream, String str) throws IOException {
        this.entries = new LinkedList();
        readStream(inputStream, str);
        this.parser.preParse(this.entries);
        resetIterator();
    }

    private void readStream(InputStream inputStream, String str) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.toCharset(str)));
        String nextEntry = this.parser.readNextEntry(bufferedReader);
        while (nextEntry != null) {
            this.entries.add(nextEntry);
            nextEntry = this.parser.readNextEntry(bufferedReader);
        }
        bufferedReader.close();
    }

    public FTPFile[] getNext(int i) {
        LinkedList linkedList = new LinkedList();
        while (i > 0 && this._internalIterator.hasNext()) {
            linkedList.add(this.parser.parseFTPEntry(this._internalIterator.next()));
            i--;
        }
        return (FTPFile[]) linkedList.toArray(new FTPFile[linkedList.size()]);
    }

    public FTPFile[] getPrevious(int i) {
        LinkedList linkedList = new LinkedList();
        while (i > 0 && this._internalIterator.hasPrevious()) {
            linkedList.add(0, this.parser.parseFTPEntry(this._internalIterator.previous()));
            i--;
        }
        return (FTPFile[]) linkedList.toArray(new FTPFile[linkedList.size()]);
    }

    public FTPFile[] getFiles() throws IOException {
        return getFiles(FTPFileFilters.NON_NULL);
    }

    public FTPFile[] getFiles(FTPFileFilter fTPFileFilter) throws IOException {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = this.entries.iterator();
        while (it.hasNext()) {
            FTPFile fTPEntry = this.parser.parseFTPEntry(it.next());
            if (fTPFileFilter.accept(fTPEntry)) {
                arrayList.add(fTPEntry);
            }
        }
        return (FTPFile[]) arrayList.toArray(new FTPFile[arrayList.size()]);
    }

    public boolean hasNext() {
        return this._internalIterator.hasNext();
    }

    public boolean hasPrevious() {
        return this._internalIterator.hasPrevious();
    }

    public void resetIterator() {
        this._internalIterator = this.entries.listIterator();
    }

    @Deprecated
    public void readServerList(InputStream inputStream) throws IOException {
        readServerList(inputStream, null);
    }
}

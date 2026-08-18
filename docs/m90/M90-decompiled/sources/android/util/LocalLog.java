package android.util;

import android.text.format.Time;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ListIterator;

/* JADX INFO: loaded from: classes.dex */
public final class LocalLog {
    private int mMaxLines;
    private LinkedList<String> mLog = new LinkedList<>();
    private Time mNow = new Time();

    public LocalLog(int i) {
        this.mMaxLines = i;
    }

    public synchronized void log(String str) {
        if (this.mMaxLines > 0) {
            this.mNow.setToNow();
            this.mLog.add(this.mNow.format("%H:%M:%S") + " - " + str);
            while (this.mLog.size() > this.mMaxLines) {
                this.mLog.remove();
            }
        }
    }

    public synchronized void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ListIterator<String> listIterator = this.mLog.listIterator(0);
        while (listIterator.hasNext()) {
            printWriter.println(listIterator.next());
        }
    }
}

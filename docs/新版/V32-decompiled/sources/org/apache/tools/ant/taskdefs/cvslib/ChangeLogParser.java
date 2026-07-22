package org.apache.tools.ant.taskdefs.cvslib;

import android.text.format.Time;
import com.lianhexinye.m90.gps.Function;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.util.CollectionUtils;

/* JADX INFO: loaded from: classes3.dex */
class ChangeLogParser {
    private static final SimpleDateFormat CVS1129_INPUT_DATE;
    private static final int GET_COMMENT = 3;
    private static final int GET_DATE = 2;
    private static final int GET_FILE = 1;
    private static final int GET_PREVIOUS_REV = 5;
    private static final int GET_REVISION = 4;
    private static final SimpleDateFormat INPUT_DATE;
    private String author;
    private String comment;
    private String date;
    private final Hashtable entries;
    private String file;
    private final int[] moduleNameLengths;
    private final String[] moduleNames;
    private String previousRevision;
    private final boolean remote;
    private String revision;
    private int status;

    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Function.FORMAT_DATE_TIME_SECOND, Locale.US);
        INPUT_DATE = simpleDateFormat;
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        CVS1129_INPUT_DATE = simpleDateFormat2;
        TimeZone timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC);
        simpleDateFormat.setTimeZone(timeZone);
        simpleDateFormat2.setTimeZone(timeZone);
    }

    public ChangeLogParser() {
        this(false, "", CollectionUtils.EMPTY_LIST);
    }

    public ChangeLogParser(boolean z, String str, List list) {
        this.status = 1;
        this.entries = new Hashtable();
        this.remote = z;
        ArrayList arrayList = new ArrayList();
        if (str != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str);
            while (stringTokenizer.hasMoreTokens()) {
                arrayList.add(stringTokenizer.nextToken());
            }
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((AbstractCvsTask.Module) it.next()).getName());
        }
        String[] strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
        this.moduleNames = strArr;
        this.moduleNameLengths = new int[strArr.length];
        int i = 0;
        while (true) {
            String[] strArr2 = this.moduleNames;
            if (i >= strArr2.length) {
                return;
            }
            this.moduleNameLengths[i] = strArr2[i].length();
            i++;
        }
    }

    public CVSEntry[] getEntrySetAsArray() {
        CVSEntry[] cVSEntryArr = new CVSEntry[this.entries.size()];
        Enumeration enumerationElements = this.entries.elements();
        int i = 0;
        while (enumerationElements.hasMoreElements()) {
            cVSEntryArr[i] = (CVSEntry) enumerationElements.nextElement();
            i++;
        }
        return cVSEntryArr;
    }

    public void stdout(String str) {
        int i = this.status;
        if (i == 1) {
            reset();
            processFile(str);
            return;
        }
        if (i == 2) {
            processDate(str);
            return;
        }
        if (i == 3) {
            processComment(str);
        } else if (i == 4) {
            processRevision(str);
        } else {
            if (i != 5) {
                return;
            }
            processGetPreviousRevision(str);
        }
    }

    private void processComment(String str) {
        String property = System.getProperty("line.separator");
        if (str.equals("=============================================================================")) {
            this.comment = this.comment.substring(0, this.comment.length() - property.length());
            saveEntry();
            this.status = 1;
            return;
        }
        if (str.equals("----------------------------")) {
            this.comment = this.comment.substring(0, this.comment.length() - property.length());
            this.status = 5;
            return;
        }
        this.comment += str + property;
    }

    private void processFile(String str) {
        if (!this.remote && str.startsWith("Working file:")) {
            this.file = str.substring(14, str.length());
            this.status = 4;
            return;
        }
        if (this.remote && str.startsWith("RCS file:")) {
            int i = 0;
            int i2 = 0;
            while (true) {
                String[] strArr = this.moduleNames;
                if (i2 >= strArr.length) {
                    break;
                }
                int iIndexOf = str.indexOf(strArr[i2]);
                if (iIndexOf >= 0) {
                    i = iIndexOf + this.moduleNameLengths[i2] + 1;
                    break;
                }
                i2++;
            }
            int iIndexOf2 = str.indexOf(",v");
            if (iIndexOf2 == -1) {
                this.file = str.substring(i);
            } else {
                this.file = str.substring(i, iIndexOf2);
            }
            this.status = 4;
        }
    }

    private void processRevision(String str) {
        if (str.startsWith("revision")) {
            this.revision = str.substring(9);
            this.status = 2;
        } else if (str.startsWith("======")) {
            this.status = 1;
        }
    }

    private void processDate(String str) {
        if (str.startsWith("date:")) {
            int iIndexOf = str.indexOf(59);
            this.date = str.substring(6, iIndexOf);
            int iIndexOf2 = str.indexOf("author: ", iIndexOf + 1);
            this.author = str.substring(8 + iIndexOf2, str.indexOf(59, iIndexOf2 + 1));
            this.status = 3;
            this.comment = "";
        }
    }

    private void processGetPreviousRevision(String str) {
        if (!str.startsWith("revision ")) {
            throw new IllegalStateException("Unexpected line from CVS: " + str);
        }
        this.previousRevision = str.substring(9);
        saveEntry();
        this.revision = this.previousRevision;
        this.status = 2;
    }

    private void saveEntry() {
        CVSEntry cVSEntry;
        String str = this.date + this.author + this.comment;
        if (!this.entries.containsKey(str)) {
            cVSEntry = new CVSEntry(parseDate(this.date), this.author, this.comment);
            this.entries.put(str, cVSEntry);
        } else {
            cVSEntry = (CVSEntry) this.entries.get(str);
        }
        cVSEntry.addFile(this.file, this.revision, this.previousRevision);
    }

    private Date parseDate(String str) {
        try {
            try {
                return INPUT_DATE.parse(str);
            } catch (ParseException unused) {
                return CVS1129_INPUT_DATE.parse(str);
            }
        } catch (ParseException unused2) {
            throw new IllegalStateException("Invalid date format: " + str);
        }
    }

    public void reset() {
        this.file = null;
        this.date = null;
        this.author = null;
        this.comment = null;
        this.revision = null;
        this.previousRevision = null;
    }
}

package org.apache.tools.ant;

import java.io.Serializable;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: classes3.dex */
public class Location implements Serializable {
    private static final long serialVersionUID = 1;
    private final int columnNumber;
    private final String fileName;
    private final int lineNumber;
    public static final Location UNKNOWN_LOCATION = new Location();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private Location() {
        this(null, 0, 0);
    }

    public Location(String str) {
        this(str, 0, 0);
    }

    public Location(Locator locator) {
        this(locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
    }

    public Location(String str, int i, int i2) {
        if (str != null && str.startsWith("file:")) {
            this.fileName = FILE_UTILS.fromURI(str);
        } else {
            this.fileName = str;
        }
        this.lineNumber = i;
        this.columnNumber = i2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String str = this.fileName;
        if (str != null) {
            stringBuffer.append(str);
            if (this.lineNumber != 0) {
                stringBuffer.append(":");
                stringBuffer.append(this.lineNumber);
            }
            stringBuffer.append(": ");
        }
        return stringBuffer.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == getClass()) {
            return toString().equals(obj.toString());
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }
}

package org.apache.poi.hpsf.wellknown;

import java.util.HashMap;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyIDMap extends HashMap {
    public static final int PID_APPNAME = 18;
    public static final int PID_AUTHOR = 4;
    public static final int PID_BYTECOUNT = 4;
    public static final int PID_CATEGORY = 2;
    public static final int PID_CHARCOUNT = 16;
    public static final int PID_CODEPAGE = 1;
    public static final int PID_COMMENTS = 6;
    public static final int PID_COMPANY = 15;
    public static final int PID_CREATE_DTM = 12;
    public static final int PID_DICTIONARY = 0;
    public static final int PID_DOCPARTS = 13;
    public static final int PID_EDITTIME = 10;
    public static final int PID_HEADINGPAIR = 12;
    public static final int PID_HIDDENCOUNT = 9;
    public static final int PID_KEYWORDS = 5;
    public static final int PID_LASTAUTHOR = 8;
    public static final int PID_LASTPRINTED = 11;
    public static final int PID_LASTSAVE_DTM = 13;
    public static final int PID_LINECOUNT = 5;
    public static final int PID_LINKSDIRTY = 16;
    public static final int PID_MANAGER = 14;
    public static final int PID_MMCLIPCOUNT = 10;
    public static final int PID_NOTECOUNT = 8;
    public static final int PID_PAGECOUNT = 14;
    public static final int PID_PARCOUNT = 6;
    public static final int PID_PRESFORMAT = 3;
    public static final int PID_REVNUMBER = 9;
    public static final int PID_SCALE = 11;
    public static final int PID_SECURITY = 19;
    public static final int PID_SLIDECOUNT = 7;
    public static final int PID_SUBJECT = 3;
    public static final int PID_TEMPLATE = 7;
    public static final int PID_THUMBNAIL = 17;
    public static final int PID_TITLE = 2;
    public static final int PID_WORDCOUNT = 15;
    private static PropertyIDMap documentSummaryInformationProperties;
    private static PropertyIDMap summaryInformationProperties;

    public PropertyIDMap(int i, float f) {
        super(i, f);
    }

    public Object put(int i, String str) {
        return put(new Integer(i), str);
    }

    public Object get(int i) {
        return get(new Integer(i));
    }

    public static PropertyIDMap getSummaryInformationProperties() {
        if (summaryInformationProperties == null) {
            PropertyIDMap propertyIDMap = new PropertyIDMap(18, 1.0f);
            propertyIDMap.put(2, "PID_TITLE");
            propertyIDMap.put(3, "PID_SUBJECT");
            propertyIDMap.put(4, "PID_AUTHOR");
            propertyIDMap.put(5, "PID_KEYWORDS");
            propertyIDMap.put(6, "PID_COMMENTS");
            propertyIDMap.put(7, "PID_TEMPLATE");
            propertyIDMap.put(8, "PID_LASTAUTHOR");
            propertyIDMap.put(9, "PID_REVNUMBER");
            propertyIDMap.put(10, "PID_EDITTIME");
            propertyIDMap.put(11, "PID_LASTPRINTED");
            propertyIDMap.put(12, "PID_CREATE_DTM");
            propertyIDMap.put(13, "PID_LASTSAVE_DTM");
            propertyIDMap.put(14, "PID_PAGECOUNT");
            propertyIDMap.put(15, "PID_WORDCOUNT");
            propertyIDMap.put(16, "PID_CHARCOUNT");
            propertyIDMap.put(17, "PID_THUMBNAIL");
            propertyIDMap.put(18, "PID_APPNAME");
            propertyIDMap.put(19, "PID_SECURITY");
            summaryInformationProperties = propertyIDMap;
        }
        return summaryInformationProperties;
    }

    public static PropertyIDMap getDocumentSummaryInformationProperties() {
        if (documentSummaryInformationProperties == null) {
            PropertyIDMap propertyIDMap = new PropertyIDMap(17, 1.0f);
            propertyIDMap.put(0, "PID_DICTIONARY");
            propertyIDMap.put(1, "PID_CODEPAGE");
            propertyIDMap.put(2, "PID_CATEGORY");
            propertyIDMap.put(3, "PID_PRESFORMAT");
            propertyIDMap.put(4, "PID_BYTECOUNT");
            propertyIDMap.put(5, "PID_LINECOUNT");
            propertyIDMap.put(6, "PID_PARCOUNT");
            propertyIDMap.put(7, "PID_SLIDECOUNT");
            propertyIDMap.put(8, "PID_NOTECOUNT");
            propertyIDMap.put(9, "PID_HIDDENCOUNT");
            propertyIDMap.put(10, "PID_MMCLIPCOUNT");
            propertyIDMap.put(11, "PID_SCALE");
            propertyIDMap.put(12, "PID_HEADINGPAIR");
            propertyIDMap.put(13, "PID_DOCPARTS");
            propertyIDMap.put(14, "PID_MANAGER");
            propertyIDMap.put(15, "PID_COMPANY");
            propertyIDMap.put(16, "PID_LINKSDIRTY");
            documentSummaryInformationProperties = propertyIDMap;
        }
        return documentSummaryInformationProperties;
    }

    public static void main(String[] strArr) {
        PropertyIDMap summaryInformationProperties2 = getSummaryInformationProperties();
        PropertyIDMap documentSummaryInformationProperties2 = getDocumentSummaryInformationProperties();
        System.out.println(new StringBuffer().append("s1: ").append(summaryInformationProperties2).toString());
        System.out.println(new StringBuffer().append("s2: ").append(documentSummaryInformationProperties2).toString());
    }
}

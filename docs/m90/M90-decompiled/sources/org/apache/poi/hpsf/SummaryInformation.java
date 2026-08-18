package org.apache.poi.hpsf;

import java.util.Date;

/* JADX INFO: loaded from: classes3.dex */
public class SummaryInformation extends SpecialPropertySet {
    public static final String DEFAULT_STREAM_NAME = "\u0005SummaryInformation";

    public SummaryInformation(PropertySet propertySet) throws UnexpectedPropertySetTypeException {
        super(propertySet);
        if (!isSummaryInformation()) {
            throw new UnexpectedPropertySetTypeException(new StringBuffer().append("Not a ").append(getClass().getName()).toString());
        }
    }

    public String getTitle() {
        return (String) getProperty(2);
    }

    public String getSubject() {
        return (String) getProperty(3);
    }

    public String getAuthor() {
        return (String) getProperty(4);
    }

    public String getKeywords() {
        return (String) getProperty(5);
    }

    public String getComments() {
        return (String) getProperty(6);
    }

    public String getTemplate() {
        return (String) getProperty(7);
    }

    public String getLastAuthor() {
        return (String) getProperty(8);
    }

    public String getRevNumber() {
        return (String) getProperty(9);
    }

    public Date getEditTime() {
        return (Date) getProperty(10);
    }

    public Date getLastPrinted() {
        return (Date) getProperty(11);
    }

    public Date getCreateDateTime() {
        return (Date) getProperty(12);
    }

    public Date getLastSaveDateTime() {
        return (Date) getProperty(13);
    }

    public int getPageCount() {
        return getPropertyIntValue(14);
    }

    public int getWordCount() {
        return getPropertyIntValue(15);
    }

    public int getCharCount() {
        return getPropertyIntValue(16);
    }

    public byte[] getThumbnail() {
        return (byte[]) getProperty(17);
    }

    public String getApplicationName() {
        return (String) getProperty(18);
    }

    public int getSecurity() {
        return getPropertyIntValue(19);
    }
}

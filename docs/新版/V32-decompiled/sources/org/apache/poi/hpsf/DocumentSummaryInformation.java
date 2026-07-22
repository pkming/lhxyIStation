package org.apache.poi.hpsf;

/* JADX INFO: loaded from: classes3.dex */
public class DocumentSummaryInformation extends SpecialPropertySet {
    public static final String DEFAULT_STREAM_NAME = "\u0005DocumentSummaryInformation";

    public DocumentSummaryInformation(PropertySet propertySet) throws UnexpectedPropertySetTypeException {
        super(propertySet);
        if (!isDocumentSummaryInformation()) {
            throw new UnexpectedPropertySetTypeException(new StringBuffer().append("Not a ").append(getClass().getName()).toString());
        }
    }

    public String getCategory() {
        return (String) getProperty(2);
    }

    public String getPresentationFormat() {
        return (String) getProperty(3);
    }

    public int getByteCount() {
        return getPropertyIntValue(4);
    }

    public int getLineCount() {
        return getPropertyIntValue(5);
    }

    public int getParCount() {
        return getPropertyIntValue(6);
    }

    public int getSlideCount() {
        return getPropertyIntValue(7);
    }

    public int getNoteCount() {
        return getPropertyIntValue(8);
    }

    public int getHiddenCount() {
        return getPropertyIntValue(9);
    }

    public int getMMClipCount() {
        return getPropertyIntValue(10);
    }

    public boolean getScale() {
        return getPropertyBooleanValue(11);
    }

    public byte[] getHeadingPair() {
        throw new UnsupportedOperationException("FIXME");
    }

    public byte[] getDocparts() {
        throw new UnsupportedOperationException("FIXME");
    }

    public String getManager() {
        return (String) getProperty(14);
    }

    public String getCompany() {
        return (String) getProperty(15);
    }

    public boolean getLinksDirty() {
        return getPropertyBooleanValue(16);
    }
}

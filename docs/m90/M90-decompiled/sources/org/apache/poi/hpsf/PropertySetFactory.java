package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class PropertySetFactory {
    public static PropertySet create(InputStream inputStream) throws UnexpectedPropertySetTypeException, MarkUnsupportedException, NoPropertySetStreamException, IOException {
        PropertySet propertySet = new PropertySet(inputStream);
        if (propertySet.isSummaryInformation()) {
            return new SummaryInformation(propertySet);
        }
        return propertySet.isDocumentSummaryInformation() ? new DocumentSummaryInformation(propertySet) : propertySet;
    }
}

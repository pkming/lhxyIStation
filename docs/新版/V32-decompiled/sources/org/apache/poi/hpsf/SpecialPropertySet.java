package org.apache.poi.hpsf;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SpecialPropertySet extends PropertySet {
    private PropertySet delegate;

    public SpecialPropertySet(PropertySet propertySet) {
        this.delegate = propertySet;
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public int getByteOrder() {
        return this.delegate.getByteOrder();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public int getFormat() {
        return this.delegate.getFormat();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public long getOSVersion() {
        return this.delegate.getOSVersion();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public ClassID getClassID() {
        return this.delegate.getClassID();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public long getSectionCount() {
        return this.delegate.getSectionCount();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public List getSections() {
        return this.delegate.getSections();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public boolean isSummaryInformation() {
        return this.delegate.isSummaryInformation();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public boolean isDocumentSummaryInformation() {
        return this.delegate.isDocumentSummaryInformation();
    }

    @Override // org.apache.poi.hpsf.PropertySet
    public Section getSingleSection() {
        return this.delegate.getSingleSection();
    }
}

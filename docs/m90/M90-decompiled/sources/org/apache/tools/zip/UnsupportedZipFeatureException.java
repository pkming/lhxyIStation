package org.apache.tools.zip;

import java.util.zip.ZipException;

/* JADX INFO: loaded from: classes3.dex */
public class UnsupportedZipFeatureException extends ZipException {
    private static final long serialVersionUID = 4430521921766595597L;
    private final ZipEntry entry;
    private final Feature reason;

    public UnsupportedZipFeatureException(Feature feature, ZipEntry zipEntry) {
        super("unsupported feature " + feature + " used in entry " + zipEntry.getName());
        this.reason = feature;
        this.entry = zipEntry;
    }

    public Feature getFeature() {
        return this.reason;
    }

    public ZipEntry getEntry() {
        return this.entry;
    }

    public static class Feature {
        private final String name;
        public static final Feature ENCRYPTION = new Feature("encryption");
        public static final Feature METHOD = new Feature("compression method");
        public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");

        private Feature(String str) {
            this.name = str;
        }

        public String toString() {
            return this.name;
        }
    }
}

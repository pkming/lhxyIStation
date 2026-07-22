package org.apache.tools.ant.types;

import java.net.URL;

/* JADX INFO: loaded from: classes3.dex */
public class ResourceLocation {
    private String publicId = null;
    private String location = null;
    private URL base = null;

    public void setPublicId(String str) {
        this.publicId = str;
    }

    public void setLocation(String str) {
        this.location = str;
    }

    public void setBase(URL url) {
        this.base = url;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getLocation() {
        return this.location;
    }

    public URL getBase() {
        return this.base;
    }
}

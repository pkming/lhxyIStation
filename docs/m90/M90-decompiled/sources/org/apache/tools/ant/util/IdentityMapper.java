package org.apache.tools.ant.util;

/* JADX INFO: loaded from: classes3.dex */
public class IdentityMapper implements FileNameMapper {
    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        return new String[]{str};
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) {
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
    }
}

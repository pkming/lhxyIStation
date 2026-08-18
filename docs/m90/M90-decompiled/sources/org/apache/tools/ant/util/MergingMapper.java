package org.apache.tools.ant.util;

/* JADX INFO: loaded from: classes3.dex */
public class MergingMapper implements FileNameMapper {
    protected String[] mergedFile = null;

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) {
    }

    public MergingMapper() {
    }

    public MergingMapper(String str) {
        setTo(str);
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
        this.mergedFile = new String[]{str};
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        return this.mergedFile;
    }
}

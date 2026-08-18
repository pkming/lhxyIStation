package org.apache.tools.ant.types.mappers;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileNameMapper;

/* JADX INFO: loaded from: classes3.dex */
public class CutDirsMapper implements FileNameMapper {
    private int dirs = 0;

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) {
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
    }

    public void setDirs(int i) {
        this.dirs = i;
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        if (this.dirs <= 0) {
            throw new BuildException("dirs must be set to a positive number");
        }
        char c = File.separatorChar;
        String strReplace = str.replace('/', c).replace('\\', c);
        int iIndexOf = strReplace.indexOf(c);
        for (int i = 1; iIndexOf > -1 && i < this.dirs; i++) {
            iIndexOf = strReplace.indexOf(c, iIndexOf + 1);
        }
        if (iIndexOf == -1) {
            return null;
        }
        return new String[]{str.substring(iIndexOf + 1)};
    }
}

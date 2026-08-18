package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class HashvalueAlgorithm implements Algorithm {
    @Override // org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
    public boolean isValid() {
        return true;
    }

    public String toString() {
        return "HashvalueAlgorithm";
    }

    @Override // org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
    public String getValue(File file) throws Throwable {
        FileReader fileReader;
        FileReader fileReader2 = null;
        try {
            if (file.canRead()) {
                fileReader = new FileReader(file);
                try {
                    String string = Integer.toString(FileUtils.readFully(fileReader).hashCode());
                    FileUtils.close(fileReader);
                    return string;
                } catch (Exception unused) {
                } catch (Throwable th) {
                    th = th;
                    fileReader2 = fileReader;
                    FileUtils.close(fileReader2);
                    throw th;
                }
            } else {
                FileUtils.close((Reader) null);
                return null;
            }
        } catch (Exception unused2) {
            fileReader = null;
        } catch (Throwable th2) {
            th = th2;
        }
        FileUtils.close(fileReader);
        return null;
    }
}

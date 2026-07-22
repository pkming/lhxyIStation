package org.apache.tools.ant.types;

import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.TarResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/* JADX INFO: loaded from: classes3.dex */
public class TarScanner extends ArchiveScanner {
    @Override // org.apache.tools.ant.types.ArchiveScanner
    protected void fillMapsFromArchive(Resource resource, String str, Map<String, Resource> map, Map<String, Resource> map2, Map<String, Resource> map3, Map<String, Resource> map4) throws Throwable {
        TarInputStream tarInputStream = null;
        try {
            try {
                TarInputStream tarInputStream2 = new TarInputStream(resource.getInputStream());
                while (true) {
                    try {
                        TarEntry nextEntry = tarInputStream2.getNextEntry();
                        if (nextEntry != null) {
                            TarResource tarResource = new TarResource(resource, nextEntry);
                            String name = nextEntry.getName();
                            if (nextEntry.isDirectory()) {
                                String strTrimSeparator = trimSeparator(name);
                                map3.put(strTrimSeparator, tarResource);
                                if (match(strTrimSeparator)) {
                                    map4.put(strTrimSeparator, tarResource);
                                }
                            } else {
                                map.put(name, tarResource);
                                if (match(name)) {
                                    map2.put(name, tarResource);
                                }
                            }
                        } else {
                            FileUtils.close(tarInputStream2);
                            return;
                        }
                    } catch (IOException e) {
                        e = e;
                        tarInputStream = tarInputStream2;
                        throw new BuildException("problem reading " + this.srcFile, e);
                    } catch (Throwable th) {
                        th = th;
                        tarInputStream = tarInputStream2;
                        FileUtils.close(tarInputStream);
                        throw th;
                    }
                }
            } catch (IOException e2) {
                try {
                    throw new BuildException("problem opening " + this.srcFile, e2);
                } catch (IOException e3) {
                    e = e3;
                    throw new BuildException("problem reading " + this.srcFile, e);
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }
}

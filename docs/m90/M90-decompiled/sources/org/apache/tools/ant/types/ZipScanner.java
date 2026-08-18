package org.apache.tools.ant.types;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.ZipResource;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/* JADX INFO: loaded from: classes3.dex */
public class ZipScanner extends ArchiveScanner {
    @Override // org.apache.tools.ant.types.ArchiveScanner
    protected void fillMapsFromArchive(Resource resource, String str, Map<String, Resource> map, Map<String, Resource> map2, Map<String, Resource> map3, Map<String, Resource> map4) throws Throwable {
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        if (fileProvider != null) {
            File file = fileProvider.getFile();
            ZipFile zipFile = null;
            try {
                try {
                    ZipFile zipFile2 = new ZipFile(file, str);
                    try {
                        Enumeration<ZipEntry> entries = zipFile2.getEntries();
                        while (entries.hasMoreElements()) {
                            ZipEntry zipEntryNextElement = entries.nextElement();
                            ZipResource zipResource = new ZipResource(file, str, zipEntryNextElement);
                            String name = zipEntryNextElement.getName();
                            if (zipEntryNextElement.isDirectory()) {
                                String strTrimSeparator = trimSeparator(name);
                                map3.put(strTrimSeparator, zipResource);
                                if (match(strTrimSeparator)) {
                                    map4.put(strTrimSeparator, zipResource);
                                }
                            } else {
                                map.put(name, zipResource);
                                if (match(name)) {
                                    map2.put(name, zipResource);
                                }
                            }
                        }
                        ZipFile.closeQuietly(zipFile2);
                    } catch (Throwable th) {
                        th = th;
                        zipFile = zipFile2;
                        ZipFile.closeQuietly(zipFile);
                        throw th;
                    }
                } catch (ZipException e) {
                    throw new BuildException("Problem reading " + file, e);
                } catch (IOException e2) {
                    throw new BuildException("Problem opening " + file, e2);
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            throw new BuildException("Only file provider resources are supported");
        }
    }
}

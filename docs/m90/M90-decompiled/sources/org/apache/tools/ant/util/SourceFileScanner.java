package org.apache.tools.ant.util;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileResource;

/* JADX INFO: loaded from: classes3.dex */
public class SourceFileScanner implements ResourceFactory {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File destDir;
    protected Task task;

    public SourceFileScanner(Task task) {
        this.task = task;
    }

    public String[] restrict(String[] strArr, File file, File file2, FileNameMapper fileNameMapper) {
        return restrict(strArr, file, file2, fileNameMapper, FILE_UTILS.getFileTimestampGranularity());
    }

    public String[] restrict(String[] strArr, File file, File file2, FileNameMapper fileNameMapper, long j) {
        this.destDir = file2;
        Vector vector = new Vector();
        for (final String str : strArr) {
            vector.addElement(new FileResource(file, str) { // from class: org.apache.tools.ant.util.SourceFileScanner.1
                @Override // org.apache.tools.ant.types.resources.FileResource, org.apache.tools.ant.types.Resource
                public String getName() {
                    return str;
                }
            });
        }
        Resource[] resourceArr = new Resource[vector.size()];
        vector.copyInto(resourceArr);
        Resource[] resourceArrSelectOutOfDateSources = ResourceUtils.selectOutOfDateSources(this.task, resourceArr, fileNameMapper, this, j);
        String[] strArr2 = new String[resourceArrSelectOutOfDateSources.length];
        for (int i = 0; i < resourceArrSelectOutOfDateSources.length; i++) {
            strArr2[i] = resourceArrSelectOutOfDateSources[i].getName();
        }
        return strArr2;
    }

    public File[] restrictAsFiles(String[] strArr, File file, File file2, FileNameMapper fileNameMapper) {
        return restrictAsFiles(strArr, file, file2, fileNameMapper, FILE_UTILS.getFileTimestampGranularity());
    }

    public File[] restrictAsFiles(String[] strArr, File file, File file2, FileNameMapper fileNameMapper, long j) {
        String[] strArrRestrict = restrict(strArr, file, file2, fileNameMapper, j);
        File[] fileArr = new File[strArrRestrict.length];
        for (int i = 0; i < strArrRestrict.length; i++) {
            fileArr[i] = new File(file, strArrRestrict[i]);
        }
        return fileArr;
    }

    @Override // org.apache.tools.ant.types.ResourceFactory
    public Resource getResource(String str) {
        return new FileResource(this.destDir, str);
    }
}

package org.apache.tools.ant.types.resources.comparators;

import java.io.File;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class FileSystem extends ResourceComparator {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    @Override // org.apache.tools.ant.types.resources.comparators.ResourceComparator
    protected int resourceCompare(Resource resource, Resource resource2) {
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        if (fileProvider == null) {
            throw new ClassCastException(resource.getClass() + " doesn't provide files");
        }
        File file = fileProvider.getFile();
        FileProvider fileProvider2 = (FileProvider) resource2.as(FileProvider.class);
        if (fileProvider2 == null) {
            throw new ClassCastException(resource2.getClass() + " doesn't provide files");
        }
        File file2 = fileProvider2.getFile();
        if (file.equals(file2)) {
            return 0;
        }
        FileUtils fileUtils = FILE_UTILS;
        if (fileUtils.isLeadingPath(file, file2)) {
            return -1;
        }
        return fileUtils.normalize(file.getAbsolutePath()).compareTo(fileUtils.normalize(file2.getAbsolutePath()));
    }
}

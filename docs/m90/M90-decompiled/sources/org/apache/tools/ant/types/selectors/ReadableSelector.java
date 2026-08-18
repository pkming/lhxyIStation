package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

/* JADX INFO: loaded from: classes3.dex */
public class ReadableSelector implements FileSelector, ResourceSelector {
    @Override // org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        return file2 != null && file2.canRead();
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        if (fileProvider != null) {
            return isSelected(null, null, fileProvider.getFile());
        }
        return false;
    }
}

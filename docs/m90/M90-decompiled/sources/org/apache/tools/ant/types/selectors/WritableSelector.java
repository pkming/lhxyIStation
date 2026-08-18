package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

/* JADX INFO: loaded from: classes3.dex */
public class WritableSelector implements FileSelector, ResourceSelector {
    @Override // org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        return file2 != null && file2.canWrite();
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        return fileProvider != null && isSelected(null, null, fileProvider.getFile());
    }
}

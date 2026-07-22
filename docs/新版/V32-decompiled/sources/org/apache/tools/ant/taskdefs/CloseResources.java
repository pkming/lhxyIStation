package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.util.Iterator;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class CloseResources extends Task {
    private Union resources = new Union();

    public void add(ResourceCollection resourceCollection) {
        this.resources.add(resourceCollection);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        Iterator<Resource> it = this.resources.iterator();
        while (it.hasNext()) {
            URLProvider uRLProvider = (URLProvider) it.next().as(URLProvider.class);
            if (uRLProvider != null) {
                try {
                    FileUtils.close(uRLProvider.getURL().openConnection());
                } catch (IOException unused) {
                }
            }
        }
    }
}

package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.types.resources.FileResource;

/* JADX INFO: loaded from: classes3.dex */
public class LoadFile extends LoadResource {
    public final void setSrcFile(File file) {
        addConfigured(new FileResource(file));
    }
}

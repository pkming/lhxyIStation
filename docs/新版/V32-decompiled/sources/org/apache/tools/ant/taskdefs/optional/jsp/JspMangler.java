package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;

/* JADX INFO: loaded from: classes3.dex */
public interface JspMangler {
    String mapJspToJavaName(File file);

    String mapPath(String str);
}

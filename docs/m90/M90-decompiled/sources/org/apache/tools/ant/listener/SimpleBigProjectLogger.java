package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.NoBannerLogger;

/* JADX INFO: loaded from: classes3.dex */
public class SimpleBigProjectLogger extends NoBannerLogger {
    @Override // org.apache.tools.ant.NoBannerLogger
    protected String extractTargetName(BuildEvent buildEvent) {
        String strExtractTargetName = super.extractTargetName(buildEvent);
        String strExtractProjectName = extractProjectName(buildEvent);
        return (strExtractProjectName == null || strExtractTargetName == null) ? strExtractTargetName : strExtractProjectName + '.' + strExtractTargetName;
    }
}

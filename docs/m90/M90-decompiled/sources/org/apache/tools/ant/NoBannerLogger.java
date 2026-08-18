package org.apache.tools.ant;

import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class NoBannerLogger extends DefaultLogger {
    protected String targetName;

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public synchronized void targetStarted(BuildEvent buildEvent) {
        this.targetName = extractTargetName(buildEvent);
    }

    protected String extractTargetName(BuildEvent buildEvent) {
        return buildEvent.getTarget().getName();
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public synchronized void targetFinished(BuildEvent buildEvent) {
        this.targetName = null;
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void messageLogged(BuildEvent buildEvent) throws Throwable {
        if (buildEvent.getPriority() > this.msgOutputLevel || buildEvent.getMessage() == null || "".equals(buildEvent.getMessage().trim())) {
            return;
        }
        synchronized (this) {
            if (this.targetName != null) {
                this.out.println(StringUtils.LINE_SEP + this.targetName + ":");
                this.targetName = null;
            }
        }
        super.messageLogged(buildEvent);
    }
}

package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

/* JADX INFO: loaded from: classes3.dex */
public class SilentLogger extends DefaultLogger {
    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void buildStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) {
        if (buildEvent.getException() != null) {
            super.buildFinished(buildEvent);
        }
    }
}

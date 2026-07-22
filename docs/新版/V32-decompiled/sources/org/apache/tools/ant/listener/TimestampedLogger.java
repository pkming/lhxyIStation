package org.apache.tools.ant.listener;

import org.apache.tools.ant.DefaultLogger;

/* JADX INFO: loaded from: classes3.dex */
public class TimestampedLogger extends DefaultLogger {
    public static final String SPACER = " - at ";

    @Override // org.apache.tools.ant.DefaultLogger
    protected String getBuildFailedMessage() {
        return super.getBuildFailedMessage() + SPACER + getTimestamp();
    }

    @Override // org.apache.tools.ant.DefaultLogger
    protected String getBuildSuccessfulMessage() {
        return super.getBuildSuccessfulMessage() + SPACER + getTimestamp();
    }
}

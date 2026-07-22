package org.apache.tools.ant.listener;

import java.io.File;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class BigProjectLogger extends SimpleBigProjectLogger implements SubBuildListener {
    public static final String FOOTER = "======================================================================";
    public static final String HEADER = "======================================================================";
    private volatile boolean subBuildStartedRaised = false;
    private final Object subBuildLock = new Object();

    protected String getFooter() {
        return "======================================================================";
    }

    protected String getHeader() {
        return "======================================================================";
    }

    @Override // org.apache.tools.ant.DefaultLogger
    protected String getBuildFailedMessage() {
        return super.getBuildFailedMessage() + TimestampedLogger.SPACER + getTimestamp();
    }

    @Override // org.apache.tools.ant.DefaultLogger
    protected String getBuildSuccessfulMessage() {
        return super.getBuildSuccessfulMessage() + TimestampedLogger.SPACER + getTimestamp();
    }

    @Override // org.apache.tools.ant.NoBannerLogger, org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
        maybeRaiseSubBuildStarted(buildEvent);
        super.targetStarted(buildEvent);
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
        maybeRaiseSubBuildStarted(buildEvent);
        super.taskStarted(buildEvent);
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) {
        maybeRaiseSubBuildStarted(buildEvent);
        subBuildFinished(buildEvent);
        super.buildFinished(buildEvent);
    }

    @Override // org.apache.tools.ant.NoBannerLogger, org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void messageLogged(BuildEvent buildEvent) {
        maybeRaiseSubBuildStarted(buildEvent);
        super.messageLogged(buildEvent);
    }

    @Override // org.apache.tools.ant.SubBuildListener
    public void subBuildStarted(BuildEvent buildEvent) {
        String strExtractNameOrDefault = extractNameOrDefault(buildEvent);
        Project project = buildEvent.getProject();
        File baseDir = project == null ? null : project.getBaseDir();
        printMessage(StringUtils.LINE_SEP + getHeader() + StringUtils.LINE_SEP + "Entering project " + strExtractNameOrDefault + StringUtils.LINE_SEP + (baseDir == null ? "With no base directory" : "In " + baseDir.getAbsolutePath()) + StringUtils.LINE_SEP + getFooter(), this.out, buildEvent.getPriority());
    }

    protected String extractNameOrDefault(BuildEvent buildEvent) {
        String strExtractProjectName = extractProjectName(buildEvent);
        return strExtractProjectName == null ? "" : '\"' + strExtractProjectName + '\"';
    }

    @Override // org.apache.tools.ant.SubBuildListener
    public void subBuildFinished(BuildEvent buildEvent) {
        printMessage(StringUtils.LINE_SEP + getHeader() + StringUtils.LINE_SEP + "Exiting " + (buildEvent.getException() != null ? "failing " : "") + "project " + extractNameOrDefault(buildEvent) + StringUtils.LINE_SEP + getFooter(), this.out, buildEvent.getPriority());
    }

    private void maybeRaiseSubBuildStarted(BuildEvent buildEvent) {
        if (this.subBuildStartedRaised) {
            return;
        }
        synchronized (this.subBuildLock) {
            if (!this.subBuildStartedRaised) {
                this.subBuildStartedRaised = true;
                subBuildStarted(buildEvent);
            }
        }
    }
}

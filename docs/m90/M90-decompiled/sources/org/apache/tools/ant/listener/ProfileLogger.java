package org.apache.tools.ant.listener;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ProfileLogger extends DefaultLogger {
    private Map<Object, Date> profileData = new ConcurrentHashMap();

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
        Date date = new Date();
        logStart(buildEvent, date, "Target " + buildEvent.getTarget().getName());
        this.profileData.put(buildEvent.getTarget(), date);
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
        logFinish(buildEvent, this.profileData.remove(buildEvent.getTarget()), "Target " + buildEvent.getTarget().getName());
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
        String taskName = buildEvent.getTask().getTaskName();
        Date date = new Date();
        logStart(buildEvent, date, taskName);
        this.profileData.put(buildEvent.getTask(), date);
    }

    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
        logFinish(buildEvent, this.profileData.remove(buildEvent.getTask()), buildEvent.getTask().getTaskName());
    }

    private void logFinish(BuildEvent buildEvent, Date date, String str) {
        String str2;
        Date date2 = new Date();
        if (date != null) {
            str2 = StringUtils.LINE_SEP + str + ": finished " + date2 + " (" + (date2.getTime() - date.getTime()) + "ms)";
        } else {
            str2 = StringUtils.LINE_SEP + str + ": finished " + date2 + " (unknown duration, start not detected)";
        }
        printMessage(str2, this.out, buildEvent.getPriority());
        log(str2);
    }

    private void logStart(BuildEvent buildEvent, Date date, String str) {
        String str2 = StringUtils.LINE_SEP + str + ": started " + date;
        printMessage(str2, this.out, buildEvent.getPriority());
        log(str2);
    }
}

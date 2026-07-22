package org.apache.tools.ant.taskdefs;

import com.unisound.common.r;
import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.LogLevel;

/* JADX INFO: loaded from: classes3.dex */
public class Recorder extends Task implements SubBuildListener {
    private static Hashtable recorderEntries = new Hashtable();
    private String filename = null;
    private Boolean append = null;
    private Boolean start = null;
    private int loglevel = -1;
    private boolean emacsMode = false;

    public static class VerbosityLevelChoices extends LogLevel {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void messageLogged(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.SubBuildListener
    public void subBuildStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.Task
    public void init() {
        getProject().addBuildListener(this);
    }

    public void setName(String str) {
        this.filename = str;
    }

    public void setAction(ActionChoices actionChoices) {
        if (actionChoices.getValue().equalsIgnoreCase(r.w)) {
            this.start = Boolean.TRUE;
        } else {
            this.start = Boolean.FALSE;
        }
    }

    public void setAppend(boolean z) {
        this.append = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setEmacsMode(boolean z) {
        this.emacsMode = z;
    }

    public void setLoglevel(VerbosityLevelChoices verbosityLevelChoices) {
        this.loglevel = verbosityLevelChoices.getLevel();
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.filename == null) {
            throw new BuildException("No filename specified");
        }
        getProject().log("setting a recorder for name " + this.filename, 4);
        RecorderEntry recorder = getRecorder(this.filename, getProject());
        recorder.setMessageOutputLevel(this.loglevel);
        recorder.setEmacsMode(this.emacsMode);
        Boolean bool = this.start;
        if (bool != null) {
            if (bool.booleanValue()) {
                recorder.reopenFile();
                recorder.setRecordState(this.start);
            } else {
                recorder.setRecordState(this.start);
                recorder.closeFile();
            }
        }
    }

    public static class ActionChoices extends EnumeratedAttribute {
        private static final String[] VALUES = {r.w, r.y};

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return VALUES;
        }
    }

    protected RecorderEntry getRecorder(String str, Project project) throws BuildException {
        Object obj = recorderEntries.get(str);
        if (obj == null) {
            RecorderEntry recorderEntry = new RecorderEntry(str);
            Boolean bool = this.append;
            if (bool == null) {
                recorderEntry.openFile(false);
            } else {
                recorderEntry.openFile(bool.booleanValue());
            }
            recorderEntry.setProject(project);
            recorderEntries.put(str, recorderEntry);
            return recorderEntry;
        }
        return (RecorderEntry) obj;
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) {
        cleanup();
    }

    @Override // org.apache.tools.ant.SubBuildListener
    public void subBuildFinished(BuildEvent buildEvent) {
        if (buildEvent.getProject() == getProject()) {
            cleanup();
        }
    }

    private void cleanup() {
        for (Map.Entry entry : ((Hashtable) recorderEntries.clone()).entrySet()) {
            if (((RecorderEntry) entry.getValue()).getProject() == getProject()) {
                recorderEntries.remove(entry.getKey());
            }
        }
        getProject().removeBuildListener(this);
    }
}

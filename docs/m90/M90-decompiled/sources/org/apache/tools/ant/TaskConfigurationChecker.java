package org.apache.tools.ant;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class TaskConfigurationChecker {
    private List<String> errors = new ArrayList();
    private final Task task;

    public TaskConfigurationChecker(Task task) {
        this.task = task;
    }

    public void assertConfig(boolean z, String str) {
        if (z) {
            return;
        }
        this.errors.add(str);
    }

    public void fail(String str) {
        this.errors.add(str);
    }

    public void checkErrors() throws BuildException {
        if (this.errors.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Configurationerror on <");
        stringBuffer.append(this.task.getTaskName());
        stringBuffer.append(">:");
        stringBuffer.append(System.getProperty("line.separator"));
        for (String str : this.errors) {
            stringBuffer.append("- ");
            stringBuffer.append(str);
            stringBuffer.append(System.getProperty("line.separator"));
        }
        throw new BuildException(stringBuffer.toString(), this.task.getLocation());
    }
}

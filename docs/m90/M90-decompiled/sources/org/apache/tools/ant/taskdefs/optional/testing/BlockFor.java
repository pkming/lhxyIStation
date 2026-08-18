package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.taskdefs.WaitFor;

/* JADX INFO: loaded from: classes3.dex */
public class BlockFor extends WaitFor {
    private String text;

    public BlockFor() {
        super("blockfor");
        this.text = getTaskName() + " timed out";
    }

    public BlockFor(String str) {
        super(str);
    }

    @Override // org.apache.tools.ant.taskdefs.WaitFor
    protected void processTimeout() throws BuildTimeoutException {
        super.processTimeout();
        throw new BuildTimeoutException(this.text, getLocation());
    }

    public void addText(String str) {
        this.text = getProject().replaceProperties(str);
    }
}

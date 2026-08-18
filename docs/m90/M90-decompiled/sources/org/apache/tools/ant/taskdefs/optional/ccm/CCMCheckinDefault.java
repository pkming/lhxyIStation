package org.apache.tools.ant.taskdefs.optional.ccm;

/* JADX INFO: loaded from: classes3.dex */
public class CCMCheckinDefault extends CCMCheck {
    public static final String DEFAULT_TASK = "default";

    public CCMCheckinDefault() {
        setCcmAction(Continuus.COMMAND_CHECKIN);
        setTask("default");
    }
}

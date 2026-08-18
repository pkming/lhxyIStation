package org.apache.tools.ant.taskdefs.optional.ccm;

import java.util.Date;

/* JADX INFO: loaded from: classes3.dex */
public class CCMCheckin extends CCMCheck {
    public CCMCheckin() {
        setCcmAction(Continuus.COMMAND_CHECKIN);
        setComment("Checkin " + new Date());
    }
}

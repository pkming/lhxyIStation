package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.taskdefs.email.EmailTask;

/* JADX INFO: loaded from: classes3.dex */
public class SendEmail extends EmailTask {
    public void setMailport(Integer num) {
        setMailport(num.intValue());
    }
}

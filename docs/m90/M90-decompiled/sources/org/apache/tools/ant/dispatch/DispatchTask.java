package org.apache.tools.ant.dispatch;

import android.hardware.Camera;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public abstract class DispatchTask extends Task implements Dispatchable {
    private String action;

    @Override // org.apache.tools.ant.dispatch.Dispatchable
    public String getActionParameterName() {
        return Camera.Parameters.SCENE_MODE_ACTION;
    }

    public void setAction(String str) {
        this.action = str;
    }

    public String getAction() {
        return this.action;
    }
}

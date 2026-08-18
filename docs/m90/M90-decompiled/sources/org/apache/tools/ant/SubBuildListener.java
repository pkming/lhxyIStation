package org.apache.tools.ant;

/* JADX INFO: loaded from: classes3.dex */
public interface SubBuildListener extends BuildListener {
    void subBuildFinished(BuildEvent buildEvent);

    void subBuildStarted(BuildEvent buildEvent);
}

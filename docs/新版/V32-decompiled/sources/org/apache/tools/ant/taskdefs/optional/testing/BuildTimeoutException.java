package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

/* JADX INFO: loaded from: classes3.dex */
public class BuildTimeoutException extends BuildException {
    private static final long serialVersionUID = -8057644603246297562L;

    public BuildTimeoutException() {
    }

    public BuildTimeoutException(String str) {
        super(str);
    }

    public BuildTimeoutException(String str, Throwable th) {
        super(str, th);
    }

    public BuildTimeoutException(String str, Throwable th, Location location) {
        super(str, th, location);
    }

    public BuildTimeoutException(Throwable th) {
        super(th);
    }

    public BuildTimeoutException(String str, Location location) {
        super(str, location);
    }

    public BuildTimeoutException(Throwable th, Location location) {
        super(th, location);
    }
}

package org.apache.tools.ant.taskdefs.condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

/* JADX INFO: loaded from: classes3.dex */
public class IsReachable extends ProjectComponent implements Condition {
    public static final int DEFAULT_TIMEOUT = 30;
    public static final String ERROR_BAD_TIMEOUT = "Invalid timeout value";
    public static final String ERROR_BAD_URL = "Bad URL ";
    public static final String ERROR_BOTH_TARGETS = "Both url and host have been specified";
    public static final String ERROR_NO_HOSTNAME = "No hostname defined";
    public static final String ERROR_NO_HOST_IN_URL = "No hostname in URL ";
    public static final String ERROR_ON_NETWORK = "network error to ";
    public static final String METHOD_NAME = "isReachable";
    public static final String MSG_NO_REACHABLE_TEST = "cannot do a proper reachability test on this Java version";
    private static final int SECOND = 1000;
    private static final String WARN_UNKNOWN_HOST = "Unknown host: ";
    private static Class[] parameterTypes = {Integer.TYPE};
    private String host;
    private int timeout = 30;
    private String url;

    public void setHost(String str) {
        this.host = str;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setTimeout(int i) {
        this.timeout = i;
    }

    private boolean empty(String str) {
        return str == null || str.length() == 0;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        if (empty(this.host) && empty(this.url)) {
            throw new BuildException(ERROR_NO_HOSTNAME);
        }
        if (this.timeout < 0) {
            throw new BuildException(ERROR_BAD_TIMEOUT);
        }
        String host = this.host;
        if (!empty(this.url)) {
            if (!empty(this.host)) {
                throw new BuildException(ERROR_BOTH_TARGETS);
            }
            try {
                host = new URL(this.url).getHost();
                if (empty(host)) {
                    throw new BuildException(ERROR_NO_HOST_IN_URL + this.url);
                }
            } catch (MalformedURLException e) {
                throw new BuildException(ERROR_BAD_URL + this.url, e);
            }
        }
        log("Probing host " + host, 3);
        boolean zBooleanValue = false;
        try {
            InetAddress byName = InetAddress.getByName(host);
            log("Host address = " + byName.getHostAddress(), 3);
            boolean z = true;
            try {
                Method method = InetAddress.class.getMethod(METHOD_NAME, parameterTypes);
                try {
                    zBooleanValue = ((Boolean) method.invoke(byName, new Integer(this.timeout * 1000))).booleanValue();
                } catch (IllegalAccessException unused) {
                    throw new BuildException("When calling " + method);
                } catch (InvocationTargetException e2) {
                    log(ERROR_ON_NETWORK + host + ": " + e2.getTargetException().toString());
                }
                z = zBooleanValue;
            } catch (NoSuchMethodException unused2) {
                log("Not found: InetAddress.isReachable", 3);
                log(MSG_NO_REACHABLE_TEST);
            }
            log("host is" + (z ? "" : " not") + " reachable", 3);
            return z;
        } catch (UnknownHostException unused3) {
            log(WARN_UNKNOWN_HOST + host);
            return false;
        }
    }
}

package org.apache.tools.ant.taskdefs.cvslib;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

/* JADX INFO: loaded from: classes3.dex */
public class CvsVersion extends AbstractCvsTask {
    static final long MULTIPLY = 100;
    static final long VERSION_1_11_2 = 11102;
    private String clientVersion;
    private String clientVersionProperty;
    private String serverVersion;
    private String serverVersionProperty;

    public String getClientVersion() {
        return this.clientVersion;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public void setClientVersionProperty(String str) {
        this.clientVersionProperty = str;
    }

    public void setServerVersionProperty(String str) {
        this.serverVersionProperty = str;
    }

    public boolean supportsCvsLogWithSOption() {
        if (this.serverVersion == null) {
            return false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(this.serverVersion, ".");
        long j = 10000;
        long j2 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            int i = 0;
            while (i < strNextToken.length() && Character.isDigit(strNextToken.charAt(i))) {
                i++;
            }
            j2 += Long.parseLong(strNextToken.substring(0, i)) * j;
            if (j == 1) {
                break;
            }
            j /= MULTIPLY;
        }
        return j2 >= VERSION_1_11_2;
    }

    @Override // org.apache.tools.ant.taskdefs.AbstractCvsTask, org.apache.tools.ant.Task
    public void execute() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        setOutputStream(byteArrayOutputStream);
        setErrorStream(new ByteArrayOutputStream());
        setCommand("version");
        super.execute();
        String string = byteArrayOutputStream.toString();
        log("Received version response \"" + string + "\"", 4);
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        String strNextToken = null;
        String str = null;
        boolean z = false;
        boolean z2 = false;
        loop0: while (true) {
            boolean z3 = z2;
            while (true) {
                if (!z && !stringTokenizer.hasMoreTokens()) {
                    break loop0;
                }
                String strNextToken2 = z ? strNextToken : stringTokenizer.nextToken();
                if (strNextToken2.equals("Client:")) {
                    z2 = true;
                } else if (strNextToken2.equals("Server:")) {
                    z3 = true;
                } else if (strNextToken2.startsWith("(CVS") && strNextToken2.endsWith(")")) {
                    str = strNextToken2.length() == 5 ? "" : " " + strNextToken2;
                }
                if (!z2 && !z3 && str != null && strNextToken == null && stringTokenizer.hasMoreTokens()) {
                    strNextToken = stringTokenizer.nextToken();
                    z = true;
                } else if (z2 && str != null) {
                    if (stringTokenizer.hasMoreTokens()) {
                        this.clientVersion = stringTokenizer.nextToken() + str;
                    }
                    str = null;
                    z = false;
                    z2 = false;
                } else if (z3 && str != null) {
                    if (stringTokenizer.hasMoreTokens()) {
                        this.serverVersion = stringTokenizer.nextToken() + str;
                    }
                    str = null;
                    z = false;
                    z3 = false;
                } else if (!strNextToken2.equals("(client/server)") || str == null || strNextToken == null || z2 || z3) {
                    z = false;
                }
            }
            String str2 = strNextToken + str;
            this.serverVersion = str2;
            this.clientVersion = str2;
            strNextToken = null;
            str = null;
            z = false;
            z2 = true;
        }
        if (this.clientVersionProperty != null) {
            getProject().setNewProperty(this.clientVersionProperty, this.clientVersion);
        }
        if (this.serverVersionProperty != null) {
            getProject().setNewProperty(this.serverVersionProperty, this.serverVersion);
        }
    }
}

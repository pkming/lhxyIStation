package org.apache.tools.ant.taskdefs.optional.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ProxySetup;

/* JADX INFO: loaded from: classes3.dex */
public class SetProxy extends Task {
    private static final int HTTP_PORT = 80;
    private static final int SOCKS_PORT = 1080;
    protected String proxyHost = null;
    protected int proxyPort = 80;
    private String socksProxyHost = null;
    private int socksProxyPort = 1080;
    private String nonProxyHosts = null;
    private String proxyUser = null;
    private String proxyPassword = null;

    public void setProxyHost(String str) {
        this.proxyHost = str;
    }

    public void setProxyPort(int i) {
        this.proxyPort = i;
    }

    public void setSocksProxyHost(String str) {
        this.socksProxyHost = str;
    }

    public void setSocksProxyPort(int i) {
        this.socksProxyPort = i;
    }

    public void setNonProxyHosts(String str) {
        this.nonProxyHosts = str;
    }

    public void setProxyUser(String str) {
        this.proxyUser = str;
    }

    public void setProxyPassword(String str) {
        this.proxyPassword = str;
    }

    public void applyWebProxySettings() {
        boolean z;
        Properties properties = System.getProperties();
        String str = this.proxyHost;
        boolean z2 = false;
        boolean z3 = true;
        if (str == null) {
            z = false;
        } else if (str.length() != 0) {
            traceSettingInfo();
            properties.put(ProxySetup.HTTP_PROXY_HOST, this.proxyHost);
            String string = Integer.toString(this.proxyPort);
            properties.put(ProxySetup.HTTP_PROXY_PORT, string);
            properties.put(ProxySetup.HTTPS_PROXY_HOST, this.proxyHost);
            properties.put(ProxySetup.HTTPS_PROXY_PORT, string);
            properties.put(ProxySetup.FTP_PROXY_HOST, this.proxyHost);
            properties.put(ProxySetup.FTP_PROXY_PORT, string);
            String str2 = this.nonProxyHosts;
            if (str2 != null) {
                properties.put(ProxySetup.HTTP_NON_PROXY_HOSTS, str2);
                properties.put(ProxySetup.HTTPS_NON_PROXY_HOSTS, this.nonProxyHosts);
                properties.put(ProxySetup.FTP_NON_PROXY_HOSTS, this.nonProxyHosts);
            }
            String str3 = this.proxyUser;
            if (str3 != null) {
                properties.put(ProxySetup.HTTP_PROXY_USERNAME, str3);
                properties.put(ProxySetup.HTTP_PROXY_PASSWORD, this.proxyPassword);
            }
            z = true;
            z2 = true;
        } else {
            log("resetting http proxy", 3);
            properties.remove(ProxySetup.HTTP_PROXY_HOST);
            properties.remove(ProxySetup.HTTP_PROXY_PORT);
            properties.remove(ProxySetup.HTTP_PROXY_USERNAME);
            properties.remove(ProxySetup.HTTP_PROXY_PASSWORD);
            properties.remove(ProxySetup.HTTPS_PROXY_HOST);
            properties.remove(ProxySetup.HTTPS_PROXY_PORT);
            properties.remove(ProxySetup.FTP_PROXY_HOST);
            properties.remove(ProxySetup.FTP_PROXY_PORT);
            z = false;
            z2 = true;
        }
        String str4 = this.socksProxyHost;
        if (str4 == null) {
            z3 = z;
        } else if (str4.length() != 0) {
            properties.put(ProxySetup.SOCKS_PROXY_HOST, this.socksProxyHost);
            properties.put(ProxySetup.SOCKS_PROXY_PORT, Integer.toString(this.socksProxyPort));
            String str5 = this.proxyUser;
            if (str5 != null) {
                properties.put(ProxySetup.SOCKS_PROXY_USERNAME, str5);
                properties.put(ProxySetup.SOCKS_PROXY_PASSWORD, this.proxyPassword);
            }
            z2 = true;
        } else {
            log("resetting socks proxy", 3);
            properties.remove(ProxySetup.SOCKS_PROXY_HOST);
            properties.remove(ProxySetup.SOCKS_PROXY_PORT);
            properties.remove(ProxySetup.SOCKS_PROXY_USERNAME);
            properties.remove(ProxySetup.SOCKS_PROXY_PASSWORD);
            z2 = true;
            z3 = z;
        }
        if (this.proxyUser != null) {
            if (z3) {
                Authenticator.setDefault(new ProxyAuth(this.proxyUser, this.proxyPassword));
            } else if (z2) {
                String str6 = "";
                Authenticator.setDefault(new ProxyAuth(str6, str6));
            }
        }
    }

    private void traceSettingInfo() {
        StringBuilder sbAppend = new StringBuilder().append("Setting proxy to ");
        String str = this.proxyHost;
        if (str == null) {
            str = "''";
        }
        log(sbAppend.append(str).append(":").append(this.proxyPort).toString(), 3);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        applyWebProxySettings();
    }

    private static final class ProxyAuth extends Authenticator {
        private PasswordAuthentication auth;

        private ProxyAuth(String str, String str2) {
            this.auth = new PasswordAuthentication(str, str2.toCharArray());
        }

        @Override // java.net.Authenticator
        protected PasswordAuthentication getPasswordAuthentication() {
            return this.auth;
        }
    }
}

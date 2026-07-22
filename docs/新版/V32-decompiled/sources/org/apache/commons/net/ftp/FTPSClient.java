package org.apache.commons.net.ftp;

import android.widget.ExpandableListView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import org.apache.commons.net.util.Base64;
import org.apache.commons.net.util.SSLContextUtils;
import org.apache.commons.net.util.TrustManagerUtils;

/* JADX INFO: loaded from: classes3.dex */
public class FTPSClient extends FTPClient {
    private static final String CMD_ADAT = "ADAT";
    private static final String CMD_AUTH = "AUTH";
    private static final String CMD_CCC = "CCC";
    private static final String CMD_CONF = "CONF";
    private static final String CMD_ENC = "ENC";
    private static final String CMD_MIC = "MIC";
    private static final String CMD_PBSZ = "PBSZ";
    private static final String CMD_PROT = "PROT";
    public static final int DEFAULT_FTPS_DATA_PORT = 989;
    public static final int DEFAULT_FTPS_PORT = 990;
    private static final String DEFAULT_PROTOCOL = "TLS";

    @Deprecated
    public static String KEYSTORE_ALGORITHM;

    @Deprecated
    public static String PROVIDER;

    @Deprecated
    public static String STORE_TYPE;

    @Deprecated
    public static String TRUSTSTORE_ALGORITHM;
    private String auth;
    private SSLContext context;
    private boolean isClientMode;
    private boolean isCreation;
    private final boolean isImplicit;
    private boolean isNeedClientAuth;
    private boolean isWantClientAuth;
    private KeyManager keyManager;
    private Socket plainSocket;
    private final String protocol;
    private String[] protocols;
    private String[] suites;
    private TrustManager trustManager;
    private static final String DEFAULT_PROT = "C";
    private static final String[] PROT_COMMAND_VALUE = {DEFAULT_PROT, "E", "S", "P"};

    protected void _prepareDataSocket_(Socket socket) throws IOException {
    }

    public FTPSClient() {
        this("TLS", false);
    }

    public FTPSClient(boolean z) {
        this("TLS", z);
    }

    public FTPSClient(String str) {
        this(str, false);
    }

    public FTPSClient(String str, boolean z) {
        this.auth = "TLS";
        this.isCreation = true;
        this.isClientMode = true;
        this.isNeedClientAuth = false;
        this.isWantClientAuth = false;
        this.suites = null;
        this.protocols = null;
        this.trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        this.keyManager = null;
        this.protocol = str;
        this.isImplicit = z;
        if (z) {
            setDefaultPort(DEFAULT_FTPS_PORT);
        }
    }

    public FTPSClient(boolean z, SSLContext sSLContext) {
        this("TLS", z);
        this.context = sSLContext;
    }

    public FTPSClient(SSLContext sSLContext) {
        this(false, sSLContext);
    }

    public void setAuthValue(String str) {
        this.auth = str;
    }

    public String getAuthValue() {
        return this.auth;
    }

    @Override // org.apache.commons.net.ftp.FTPClient, org.apache.commons.net.ftp.FTP, org.apache.commons.net.SocketClient
    protected void _connectAction_() throws IOException {
        if (this.isImplicit) {
            sslNegotiation();
        }
        super._connectAction_();
        if (this.isImplicit) {
            return;
        }
        execAUTH();
        sslNegotiation();
    }

    protected void execAUTH() throws IOException {
        int iSendCommand = sendCommand(CMD_AUTH, this.auth);
        if (334 != iSendCommand && 234 != iSendCommand) {
            throw new SSLException(getReplyString());
        }
    }

    private void initSslContext() throws IOException {
        if (this.context == null) {
            this.context = SSLContextUtils.createSSLContext(this.protocol, getKeyManager(), getTrustManager());
        }
    }

    protected void sslNegotiation() throws IOException {
        this.plainSocket = this._socket_;
        initSslContext();
        SSLSocket sSLSocket = (SSLSocket) this.context.getSocketFactory().createSocket(this._socket_, this._socket_.getInetAddress().getHostAddress(), this._socket_.getPort(), false);
        sSLSocket.setEnableSessionCreation(this.isCreation);
        sSLSocket.setUseClientMode(this.isClientMode);
        if (!this.isClientMode) {
            sSLSocket.setNeedClientAuth(this.isNeedClientAuth);
            sSLSocket.setWantClientAuth(this.isWantClientAuth);
        }
        String[] strArr = this.protocols;
        if (strArr != null) {
            sSLSocket.setEnabledProtocols(strArr);
        }
        String[] strArr2 = this.suites;
        if (strArr2 != null) {
            sSLSocket.setEnabledCipherSuites(strArr2);
        }
        sSLSocket.startHandshake();
        this._socket_ = sSLSocket;
        this._controlInput_ = new BufferedReader(new InputStreamReader(sSLSocket.getInputStream(), getControlEncoding()));
        this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(sSLSocket.getOutputStream(), getControlEncoding()));
    }

    private KeyManager getKeyManager() {
        return this.keyManager;
    }

    public void setKeyManager(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    public void setEnabledSessionCreation(boolean z) {
        this.isCreation = z;
    }

    public boolean getEnableSessionCreation() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket) this._socket_).getEnableSessionCreation();
        }
        return false;
    }

    public void setNeedClientAuth(boolean z) {
        this.isNeedClientAuth = z;
    }

    public boolean getNeedClientAuth() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket) this._socket_).getNeedClientAuth();
        }
        return false;
    }

    public void setWantClientAuth(boolean z) {
        this.isWantClientAuth = z;
    }

    public boolean getWantClientAuth() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket) this._socket_).getWantClientAuth();
        }
        return false;
    }

    public void setUseClientMode(boolean z) {
        this.isClientMode = z;
    }

    public boolean getUseClientMode() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket) this._socket_).getUseClientMode();
        }
        return false;
    }

    public void setEnabledCipherSuites(String[] strArr) {
        String[] strArr2 = new String[strArr.length];
        this.suites = strArr2;
        System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
    }

    public String[] getEnabledCipherSuites() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket) this._socket_).getEnabledCipherSuites();
        }
        return null;
    }

    public void setEnabledProtocols(String[] strArr) {
        String[] strArr2 = new String[strArr.length];
        this.protocols = strArr2;
        System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
    }

    public String[] getEnabledProtocols() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket) this._socket_).getEnabledProtocols();
        }
        return null;
    }

    public void execPBSZ(long j) throws IOException {
        if (j < 0 || ExpandableListView.PACKED_POSITION_VALUE_NULL < j) {
            throw new IllegalArgumentException();
        }
        if (200 != sendCommand(CMD_PBSZ, String.valueOf(j))) {
            throw new SSLException(getReplyString());
        }
    }

    public long parsePBSZ(long j) throws IOException {
        execPBSZ(j);
        String strExtractPrefixedData = extractPrefixedData("PBSZ=", getReplyString());
        if (strExtractPrefixedData == null) {
            return j;
        }
        long j2 = Long.parseLong(strExtractPrefixedData);
        return j2 < j ? j2 : j;
    }

    public void execPROT(String str) throws IOException {
        if (str == null) {
            str = DEFAULT_PROT;
        }
        if (!checkPROTValue(str)) {
            throw new IllegalArgumentException();
        }
        if (200 != sendCommand(CMD_PROT, str)) {
            throw new SSLException(getReplyString());
        }
        if (DEFAULT_PROT.equals(str)) {
            setSocketFactory(null);
            setServerSocketFactory(null);
        } else {
            setSocketFactory(new FTPSSocketFactory(this.context));
            setServerSocketFactory(new FTPSServerSocketFactory(this.context));
            initSslContext();
        }
    }

    private boolean checkPROTValue(String str) {
        for (String str2 : PROT_COMMAND_VALUE) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.commons.net.ftp.FTP
    public int sendCommand(String str, String str2) throws IOException {
        int iSendCommand = super.sendCommand(str, str2);
        if (CMD_CCC.equals(str)) {
            if (200 == iSendCommand) {
                this._socket_.close();
                this._socket_ = this.plainSocket;
                this._controlInput_ = new BufferedReader(new InputStreamReader(this._socket_.getInputStream(), getControlEncoding()));
                this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._socket_.getOutputStream(), getControlEncoding()));
            } else {
                throw new SSLException(getReplyString());
            }
        }
        return iSendCommand;
    }

    @Override // org.apache.commons.net.ftp.FTPClient
    @Deprecated
    protected Socket _openDataConnection_(int i, String str) throws IOException {
        return _openDataConnection_(FTPCommand.getCommand(i), str);
    }

    @Override // org.apache.commons.net.ftp.FTPClient
    protected Socket _openDataConnection_(String str, String str2) throws IOException {
        Socket socket_openDataConnection_ = super._openDataConnection_(str, str2);
        _prepareDataSocket_(socket_openDataConnection_);
        if (socket_openDataConnection_ instanceof SSLSocket) {
            SSLSocket sSLSocket = (SSLSocket) socket_openDataConnection_;
            sSLSocket.setUseClientMode(this.isClientMode);
            sSLSocket.setEnableSessionCreation(this.isCreation);
            if (!this.isClientMode) {
                sSLSocket.setNeedClientAuth(this.isNeedClientAuth);
                sSLSocket.setWantClientAuth(this.isWantClientAuth);
            }
            String[] strArr = this.suites;
            if (strArr != null) {
                sSLSocket.setEnabledCipherSuites(strArr);
            }
            String[] strArr2 = this.protocols;
            if (strArr2 != null) {
                sSLSocket.setEnabledProtocols(strArr2);
            }
            sSLSocket.startHandshake();
        }
        return socket_openDataConnection_;
    }

    public TrustManager getTrustManager() {
        return this.trustManager;
    }

    public void setTrustManager(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    @Override // org.apache.commons.net.ftp.FTPClient, org.apache.commons.net.ftp.FTP, org.apache.commons.net.SocketClient
    public void disconnect() throws IOException {
        super.disconnect();
        setSocketFactory(null);
        setServerSocketFactory(null);
    }

    public int execAUTH(String str) throws IOException {
        return sendCommand(CMD_AUTH, str);
    }

    public int execADAT(byte[] bArr) throws IOException {
        if (bArr != null) {
            return sendCommand(CMD_ADAT, Base64.encodeBase64StringUnChunked(bArr));
        }
        return sendCommand(CMD_ADAT);
    }

    public int execCCC() throws IOException {
        return sendCommand(CMD_CCC);
    }

    public int execMIC(byte[] bArr) throws IOException {
        if (bArr != null) {
            return sendCommand(CMD_MIC, Base64.encodeBase64StringUnChunked(bArr));
        }
        return sendCommand(CMD_MIC, "");
    }

    public int execCONF(byte[] bArr) throws IOException {
        if (bArr != null) {
            return sendCommand(CMD_CONF, Base64.encodeBase64StringUnChunked(bArr));
        }
        return sendCommand(CMD_CONF, "");
    }

    public int execENC(byte[] bArr) throws IOException {
        if (bArr != null) {
            return sendCommand(CMD_ENC, Base64.encodeBase64StringUnChunked(bArr));
        }
        return sendCommand(CMD_ENC, "");
    }

    public byte[] parseADATReply(String str) {
        if (str == null) {
            return null;
        }
        return Base64.decodeBase64(extractPrefixedData("ADAT=", str));
    }

    private String extractPrefixedData(String str, String str2) {
        int iIndexOf = str2.indexOf(str);
        if (iIndexOf == -1) {
            return null;
        }
        return str2.substring(iIndexOf + str.length()).trim();
    }
}

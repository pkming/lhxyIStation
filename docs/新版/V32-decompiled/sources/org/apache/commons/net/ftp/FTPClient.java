package org.apache.commons.net.ftp;

import android.telephony.PhoneNumberUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.MLSxEntryParser;
import org.apache.commons.net.io.CRLFLineReader;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.FromNetASCIIInputStream;
import org.apache.commons.net.io.SocketInputStream;
import org.apache.commons.net.io.SocketOutputStream;
import org.apache.commons.net.io.ToNetASCIIOutputStream;
import org.apache.commons.net.io.Util;

/* JADX INFO: loaded from: classes3.dex */
public class FTPClient extends FTP implements Configurable {
    public static final int ACTIVE_LOCAL_DATA_CONNECTION_MODE = 0;
    public static final int ACTIVE_REMOTE_DATA_CONNECTION_MODE = 1;
    public static final String FTP_SYSTEM_TYPE = "org.apache.commons.net.ftp.systemType";
    public static final String FTP_SYSTEM_TYPE_DEFAULT = "org.apache.commons.net.ftp.systemType.default";
    public static final int PASSIVE_LOCAL_DATA_CONNECTION_MODE = 2;
    public static final int PASSIVE_REMOTE_DATA_CONNECTION_MODE = 3;
    public static final String SYSTEM_TYPE_PROPERTIES = "/systemType.properties";
    private static final Pattern __PARMS_PAT = Pattern.compile("(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})");
    private InetAddress __activeExternalHost;
    private int __activeMaxPort;
    private int __activeMinPort;
    private int __bufferSize;
    private FTPClientConfig __configuration;
    private long __controlKeepAliveTimeout;
    private CopyStreamListener __copyStreamListener;
    private int __dataConnectionMode;
    private int __dataTimeout;
    private FTPFileEntryParser __entryParser;
    private String __entryParserKey;
    private HashMap<String, Set<String>> __featuresMap;
    private int __fileFormat;
    private int __fileStructure;
    private int __fileTransferMode;
    private int __fileType;
    private boolean __listHiddenFiles;
    private FTPFileEntryParserFactory __parserFactory;
    private String __passiveHost;
    private InetAddress __passiveLocalHost;
    private int __passivePort;
    private final Random __random;
    private int __receiveDataSocketBufferSize;
    private boolean __remoteVerificationEnabled;
    private InetAddress __reportActiveExternalHost;
    private long __restartOffset;
    private int __sendDataSocketBufferSize;
    private String __systemName;
    private boolean __useEPSVwithIPv4;
    private int __controlKeepAliveReplyTimeout = 1000;
    private boolean __passiveNatWorkaround = true;
    private boolean __autodetectEncoding = false;

    private static class PropertiesSingleton {
        static final Properties PROPERTIES;

        private PropertiesSingleton() {
        }

        static {
            Properties properties;
            InputStream resourceAsStream = FTPClient.class.getResourceAsStream(FTPClient.SYSTEM_TYPE_PROPERTIES);
            if (resourceAsStream != null) {
                properties = new Properties();
                try {
                    properties.load(resourceAsStream);
                } catch (IOException unused) {
                } catch (Throwable th) {
                    try {
                        resourceAsStream.close();
                    } catch (IOException unused2) {
                    }
                    throw th;
                }
                try {
                    resourceAsStream.close();
                } catch (IOException unused3) {
                }
            } else {
                properties = null;
            }
            PROPERTIES = properties;
        }
    }

    private static Properties getOverrideProperties() {
        return PropertiesSingleton.PROPERTIES;
    }

    public FTPClient() {
        __initDefaults();
        this.__dataTimeout = -1;
        this.__remoteVerificationEnabled = true;
        this.__parserFactory = new DefaultFTPFileEntryParserFactory();
        this.__configuration = null;
        this.__listHiddenFiles = false;
        this.__useEPSVwithIPv4 = false;
        this.__random = new Random();
        this.__passiveLocalHost = null;
    }

    private void __initDefaults() {
        this.__dataConnectionMode = 0;
        this.__passiveHost = null;
        this.__passivePort = -1;
        this.__activeExternalHost = null;
        this.__reportActiveExternalHost = null;
        this.__activeMinPort = 0;
        this.__activeMaxPort = 0;
        this.__fileType = 0;
        this.__fileStructure = 7;
        this.__fileFormat = 4;
        this.__fileTransferMode = 10;
        this.__restartOffset = 0L;
        this.__systemName = null;
        this.__entryParser = null;
        this.__entryParserKey = "";
        this.__featuresMap = null;
    }

    static String __parsePathname(String str) {
        String strSubstring = str.substring(4);
        if (!strSubstring.startsWith("\"")) {
            return strSubstring;
        }
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (int i = 1; i < strSubstring.length(); i++) {
            char cCharAt = strSubstring.charAt(i);
            if (cCharAt != '\"') {
                if (z) {
                    return sb.toString();
                }
                sb.append(cCharAt);
            } else if (z) {
                sb.append(cCharAt);
                z = false;
            } else {
                z = true;
            }
        }
        return z ? sb.toString() : strSubstring;
    }

    protected void _parsePassiveModeReply(String str) throws MalformedServerReplyException {
        Matcher matcher = __PARMS_PAT.matcher(str);
        if (!matcher.find()) {
            throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + str);
        }
        this.__passiveHost = matcher.group(1).replace(PhoneNumberUtils.PAUSE, '.');
        try {
            this.__passivePort = Integer.parseInt(matcher.group(3)) | (Integer.parseInt(matcher.group(2)) << 8);
            if (this.__passiveNatWorkaround) {
                try {
                    if (InetAddress.getByName(this.__passiveHost).isSiteLocalAddress()) {
                        InetAddress remoteAddress = getRemoteAddress();
                        if (remoteAddress.isSiteLocalAddress()) {
                            return;
                        }
                        String hostAddress = remoteAddress.getHostAddress();
                        fireReplyReceived(0, "[Replacing site local address " + this.__passiveHost + " with " + hostAddress + "]\n");
                        this.__passiveHost = hostAddress;
                    }
                } catch (UnknownHostException unused) {
                    throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + str);
                }
            }
        } catch (NumberFormatException unused2) {
            throw new MalformedServerReplyException("Could not parse passive port information.\nServer Reply: " + str);
        }
    }

    protected void _parseExtendedPassiveModeReply(String str) throws MalformedServerReplyException {
        String strTrim = str.substring(str.indexOf(40) + 1, str.indexOf(41)).trim();
        char cCharAt = strTrim.charAt(0);
        char cCharAt2 = strTrim.charAt(1);
        char cCharAt3 = strTrim.charAt(2);
        char cCharAt4 = strTrim.charAt(strTrim.length() - 1);
        if (cCharAt != cCharAt2 || cCharAt2 != cCharAt3 || cCharAt3 != cCharAt4) {
            throw new MalformedServerReplyException("Could not parse extended passive host information.\nServer Reply: " + strTrim);
        }
        try {
            int i = Integer.parseInt(strTrim.substring(3, strTrim.length() - 1));
            this.__passiveHost = getRemoteAddress().getHostAddress();
            this.__passivePort = i;
        } catch (NumberFormatException unused) {
            throw new MalformedServerReplyException("Could not parse extended passive host information.\nServer Reply: " + strTrim);
        }
    }

    private boolean __storeFile(FTPCmd fTPCmd, String str, InputStream inputStream) throws IOException {
        return _storeFile(fTPCmd.getCommand(), str, inputStream);
    }

    protected boolean _storeFile(String str, String str2, InputStream inputStream) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(str, str2);
        if (socket_openDataConnection_ == null) {
            return false;
        }
        OutputStream bufferedOutputStream = getBufferedOutputStream(socket_openDataConnection_.getOutputStream());
        if (this.__fileType == 0) {
            bufferedOutputStream = new ToNetASCIIOutputStream(bufferedOutputStream);
        }
        CSL csl = this.__controlKeepAliveTimeout > 0 ? new CSL(this, this.__controlKeepAliveTimeout, this.__controlKeepAliveReplyTimeout) : null;
        try {
            Util.copyStream(inputStream, bufferedOutputStream, getBufferSize(), -1L, __mergeListeners(csl), false);
            bufferedOutputStream.close();
            socket_openDataConnection_.close();
            if (csl != null) {
                csl.cleanUp();
            }
            return completePendingCommand();
        } catch (IOException e) {
            Util.closeQuietly(socket_openDataConnection_);
            if (csl != null) {
                csl.cleanUp();
            }
            throw e;
        }
    }

    private OutputStream __storeFileStream(FTPCmd fTPCmd, String str) throws IOException {
        return _storeFileStream(fTPCmd.getCommand(), str);
    }

    protected OutputStream _storeFileStream(String str, String str2) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(str, str2);
        if (socket_openDataConnection_ == null) {
            return null;
        }
        OutputStream outputStream = socket_openDataConnection_.getOutputStream();
        if (this.__fileType == 0) {
            outputStream = new ToNetASCIIOutputStream(getBufferedOutputStream(outputStream));
        }
        return new SocketOutputStream(socket_openDataConnection_, outputStream);
    }

    @Deprecated
    protected Socket _openDataConnection_(int i, String str) throws IOException {
        return _openDataConnection_(FTPCommand.getCommand(i), str);
    }

    protected Socket _openDataConnection_(FTPCmd fTPCmd, String str) throws IOException {
        return _openDataConnection_(fTPCmd.getCommand(), str);
    }

    protected Socket _openDataConnection_(String str, String str2) throws IOException {
        Socket socketAccept;
        int i = this.__dataConnectionMode;
        if (i != 0 && i != 2) {
            return null;
        }
        boolean z = getRemoteAddress() instanceof Inet6Address;
        boolean z2 = true;
        if (this.__dataConnectionMode == 0) {
            ServerSocket serverSocketCreateServerSocket = this._serverSocketFactory_.createServerSocket(getActivePort(), 1, getHostAddress());
            try {
                if (z) {
                    if (!FTPReply.isPositiveCompletion(eprt(getReportHostAddress(), serverSocketCreateServerSocket.getLocalPort()))) {
                        return null;
                    }
                } else if (!FTPReply.isPositiveCompletion(port(getReportHostAddress(), serverSocketCreateServerSocket.getLocalPort()))) {
                    return null;
                }
                long j = this.__restartOffset;
                if (j > 0 && !restart(j)) {
                    return null;
                }
                if (!FTPReply.isPositivePreliminary(sendCommand(str, str2))) {
                    return null;
                }
                int i2 = this.__dataTimeout;
                if (i2 >= 0) {
                    serverSocketCreateServerSocket.setSoTimeout(i2);
                }
                socketAccept = serverSocketCreateServerSocket.accept();
                int i3 = this.__dataTimeout;
                if (i3 >= 0) {
                    socketAccept.setSoTimeout(i3);
                }
                int i4 = this.__receiveDataSocketBufferSize;
                if (i4 > 0) {
                    socketAccept.setReceiveBufferSize(i4);
                }
                int i5 = this.__sendDataSocketBufferSize;
                if (i5 > 0) {
                    socketAccept.setSendBufferSize(i5);
                }
            } finally {
                serverSocketCreateServerSocket.close();
            }
        } else {
            if (!isUseEPSVwithIPv4() && !z) {
                z2 = false;
            }
            if (z2 && epsv() == 229) {
                _parseExtendedPassiveModeReply(this._replyLines.get(0));
            } else {
                if (z || pasv() != 227) {
                    return null;
                }
                _parsePassiveModeReply(this._replyLines.get(0));
            }
            Socket socketCreateSocket = this._socketFactory_.createSocket();
            int i6 = this.__receiveDataSocketBufferSize;
            if (i6 > 0) {
                socketCreateSocket.setReceiveBufferSize(i6);
            }
            int i7 = this.__sendDataSocketBufferSize;
            if (i7 > 0) {
                socketCreateSocket.setSendBufferSize(i7);
            }
            if (this.__passiveLocalHost != null) {
                socketCreateSocket.bind(new InetSocketAddress(this.__passiveLocalHost, 0));
            }
            int i8 = this.__dataTimeout;
            if (i8 >= 0) {
                socketCreateSocket.setSoTimeout(i8);
            }
            socketCreateSocket.connect(new InetSocketAddress(this.__passiveHost, this.__passivePort), this.connectTimeout);
            long j2 = this.__restartOffset;
            if (j2 > 0 && !restart(j2)) {
                socketCreateSocket.close();
                return null;
            }
            if (!FTPReply.isPositivePreliminary(sendCommand(str, str2))) {
                socketCreateSocket.close();
                return null;
            }
            socketAccept = socketCreateSocket;
        }
        if (!this.__remoteVerificationEnabled || verifyRemote(socketAccept)) {
            return socketAccept;
        }
        socketAccept.close();
        throw new IOException("Host attempting data connection " + socketAccept.getInetAddress().getHostAddress() + " is not same as server " + getRemoteAddress().getHostAddress());
    }

    @Override // org.apache.commons.net.ftp.FTP, org.apache.commons.net.SocketClient
    protected void _connectAction_() throws IOException {
        super._connectAction_();
        __initDefaults();
        if (this.__autodetectEncoding) {
            ArrayList arrayList = new ArrayList(this._replyLines);
            int i = this._replyCode;
            if (hasFeature("UTF8") || hasFeature("UTF-8")) {
                setControlEncoding("UTF-8");
                this._controlInput_ = new CRLFLineReader(new InputStreamReader(this._input_, getControlEncoding()));
                this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._output_, getControlEncoding()));
            }
            this._replyLines.clear();
            this._replyLines.addAll(arrayList);
            this._replyCode = i;
        }
    }

    public void setDataTimeout(int i) {
        this.__dataTimeout = i;
    }

    public void setParserFactory(FTPFileEntryParserFactory fTPFileEntryParserFactory) {
        this.__parserFactory = fTPFileEntryParserFactory;
    }

    @Override // org.apache.commons.net.ftp.FTP, org.apache.commons.net.SocketClient
    public void disconnect() throws IOException {
        super.disconnect();
        __initDefaults();
    }

    public void setRemoteVerificationEnabled(boolean z) {
        this.__remoteVerificationEnabled = z;
    }

    public boolean isRemoteVerificationEnabled() {
        return this.__remoteVerificationEnabled;
    }

    public boolean login(String str, String str2) throws IOException {
        user(str);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (FTPReply.isPositiveIntermediate(this._replyCode)) {
            return FTPReply.isPositiveCompletion(pass(str2));
        }
        return false;
    }

    public boolean login(String str, String str2, String str3) throws IOException {
        user(str);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (!FTPReply.isPositiveIntermediate(this._replyCode)) {
            return false;
        }
        pass(str2);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (FTPReply.isPositiveIntermediate(this._replyCode)) {
            return FTPReply.isPositiveCompletion(acct(str3));
        }
        return false;
    }

    public boolean logout() throws IOException {
        return FTPReply.isPositiveCompletion(quit());
    }

    public boolean changeWorkingDirectory(String str) throws IOException {
        return FTPReply.isPositiveCompletion(cwd(str));
    }

    public boolean changeToParentDirectory() throws IOException {
        return FTPReply.isPositiveCompletion(cdup());
    }

    public boolean structureMount(String str) throws IOException {
        return FTPReply.isPositiveCompletion(smnt(str));
    }

    boolean reinitialize() throws IOException {
        rein();
        if (!FTPReply.isPositiveCompletion(this._replyCode) && (!FTPReply.isPositivePreliminary(this._replyCode) || !FTPReply.isPositiveCompletion(getReply()))) {
            return false;
        }
        __initDefaults();
        return true;
    }

    public void enterLocalActiveMode() {
        this.__dataConnectionMode = 0;
        this.__passiveHost = null;
        this.__passivePort = -1;
    }

    public void enterLocalPassiveMode() {
        this.__dataConnectionMode = 2;
        this.__passiveHost = null;
        this.__passivePort = -1;
    }

    public boolean enterRemoteActiveMode(InetAddress inetAddress, int i) throws IOException {
        if (!FTPReply.isPositiveCompletion(port(inetAddress, i))) {
            return false;
        }
        this.__dataConnectionMode = 1;
        this.__passiveHost = null;
        this.__passivePort = -1;
        return true;
    }

    public boolean enterRemotePassiveMode() throws IOException {
        if (pasv() != 227) {
            return false;
        }
        this.__dataConnectionMode = 3;
        _parsePassiveModeReply(this._replyLines.get(0));
        return true;
    }

    public String getPassiveHost() {
        return this.__passiveHost;
    }

    public int getPassivePort() {
        return this.__passivePort;
    }

    public int getDataConnectionMode() {
        return this.__dataConnectionMode;
    }

    private int getActivePort() {
        int i;
        int i2 = this.__activeMinPort;
        if (i2 <= 0 || (i = this.__activeMaxPort) < i2) {
            return 0;
        }
        return i == i2 ? i : this.__random.nextInt((i - i2) + 1) + this.__activeMinPort;
    }

    private InetAddress getHostAddress() {
        InetAddress inetAddress = this.__activeExternalHost;
        return inetAddress != null ? inetAddress : getLocalAddress();
    }

    private InetAddress getReportHostAddress() {
        InetAddress inetAddress = this.__reportActiveExternalHost;
        return inetAddress != null ? inetAddress : getHostAddress();
    }

    public void setActivePortRange(int i, int i2) {
        this.__activeMinPort = i;
        this.__activeMaxPort = i2;
    }

    public void setActiveExternalIPAddress(String str) throws UnknownHostException {
        this.__activeExternalHost = InetAddress.getByName(str);
    }

    public void setPassiveLocalIPAddress(String str) throws UnknownHostException {
        this.__passiveLocalHost = InetAddress.getByName(str);
    }

    public void setPassiveLocalIPAddress(InetAddress inetAddress) {
        this.__passiveLocalHost = inetAddress;
    }

    public InetAddress getPassiveLocalIPAddress() {
        return this.__passiveLocalHost;
    }

    public void setReportActiveExternalIPAddress(String str) throws UnknownHostException {
        this.__reportActiveExternalHost = InetAddress.getByName(str);
    }

    public boolean setFileType(int i) throws IOException {
        if (!FTPReply.isPositiveCompletion(type(i))) {
            return false;
        }
        this.__fileType = i;
        this.__fileFormat = 4;
        return true;
    }

    public boolean setFileType(int i, int i2) throws IOException {
        if (!FTPReply.isPositiveCompletion(type(i, i2))) {
            return false;
        }
        this.__fileType = i;
        this.__fileFormat = i2;
        return true;
    }

    public boolean setFileStructure(int i) throws IOException {
        if (!FTPReply.isPositiveCompletion(stru(i))) {
            return false;
        }
        this.__fileStructure = i;
        return true;
    }

    public boolean setFileTransferMode(int i) throws IOException {
        if (!FTPReply.isPositiveCompletion(mode(i))) {
            return false;
        }
        this.__fileTransferMode = i;
        return true;
    }

    public boolean remoteRetrieve(String str) throws IOException {
        int i = this.__dataConnectionMode;
        if (i == 1 || i == 3) {
            return FTPReply.isPositivePreliminary(retr(str));
        }
        return false;
    }

    public boolean remoteStore(String str) throws IOException {
        int i = this.__dataConnectionMode;
        if (i == 1 || i == 3) {
            return FTPReply.isPositivePreliminary(stor(str));
        }
        return false;
    }

    public boolean remoteStoreUnique(String str) throws IOException {
        int i = this.__dataConnectionMode;
        if (i == 1 || i == 3) {
            return FTPReply.isPositivePreliminary(stou(str));
        }
        return false;
    }

    public boolean remoteStoreUnique() throws IOException {
        int i = this.__dataConnectionMode;
        if (i == 1 || i == 3) {
            return FTPReply.isPositivePreliminary(stou());
        }
        return false;
    }

    public boolean remoteAppend(String str) throws IOException {
        int i = this.__dataConnectionMode;
        if (i == 1 || i == 3) {
            return FTPReply.isPositivePreliminary(appe(str));
        }
        return false;
    }

    public boolean completePendingCommand() throws IOException {
        return FTPReply.isPositiveCompletion(getReply());
    }

    public boolean retrieveFile(String str, OutputStream outputStream) throws IOException {
        return _retrieveFile(FTPCmd.RETR.getCommand(), str, outputStream);
    }

    protected boolean _retrieveFile(String str, String str2, OutputStream outputStream) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(str, str2);
        if (socket_openDataConnection_ == null) {
            return false;
        }
        InputStream bufferedInputStream = getBufferedInputStream(socket_openDataConnection_.getInputStream());
        if (this.__fileType == 0) {
            bufferedInputStream = new FromNetASCIIInputStream(bufferedInputStream);
        }
        CSL csl = this.__controlKeepAliveTimeout > 0 ? new CSL(this, this.__controlKeepAliveTimeout, this.__controlKeepAliveReplyTimeout) : null;
        try {
            Util.copyStream(bufferedInputStream, outputStream, getBufferSize(), -1L, __mergeListeners(csl), false);
            return completePendingCommand();
        } finally {
            Util.closeQuietly(bufferedInputStream);
            Util.closeQuietly(socket_openDataConnection_);
            if (csl != null) {
                csl.cleanUp();
            }
        }
    }

    public InputStream retrieveFileStream(String str) throws IOException {
        return _retrieveFileStream(FTPCmd.RETR.getCommand(), str);
    }

    protected InputStream _retrieveFileStream(String str, String str2) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(str, str2);
        if (socket_openDataConnection_ == null) {
            return null;
        }
        InputStream inputStream = socket_openDataConnection_.getInputStream();
        if (this.__fileType == 0) {
            inputStream = new FromNetASCIIInputStream(getBufferedInputStream(inputStream));
        }
        return new SocketInputStream(socket_openDataConnection_, inputStream);
    }

    public boolean storeFile(String str, InputStream inputStream) throws IOException {
        return __storeFile(FTPCmd.STOR, str, inputStream);
    }

    public OutputStream storeFileStream(String str) throws IOException {
        return __storeFileStream(FTPCmd.STOR, str);
    }

    public boolean appendFile(String str, InputStream inputStream) throws IOException {
        return __storeFile(FTPCmd.APPE, str, inputStream);
    }

    public OutputStream appendFileStream(String str) throws IOException {
        return __storeFileStream(FTPCmd.APPE, str);
    }

    public boolean storeUniqueFile(String str, InputStream inputStream) throws IOException {
        return __storeFile(FTPCmd.STOU, str, inputStream);
    }

    public OutputStream storeUniqueFileStream(String str) throws IOException {
        return __storeFileStream(FTPCmd.STOU, str);
    }

    public boolean storeUniqueFile(InputStream inputStream) throws IOException {
        return __storeFile(FTPCmd.STOU, null, inputStream);
    }

    public OutputStream storeUniqueFileStream() throws IOException {
        return __storeFileStream(FTPCmd.STOU, null);
    }

    public boolean allocate(int i) throws IOException {
        return FTPReply.isPositiveCompletion(allo(i));
    }

    public boolean features() throws IOException {
        return FTPReply.isPositiveCompletion(feat());
    }

    public String[] featureValues(String str) throws IOException {
        Set<String> set;
        if (initFeatureMap() && (set = this.__featuresMap.get(str.toUpperCase(Locale.ENGLISH))) != null) {
            return (String[]) set.toArray(new String[set.size()]);
        }
        return null;
    }

    public String featureValue(String str) throws IOException {
        String[] strArrFeatureValues = featureValues(str);
        if (strArrFeatureValues != null) {
            return strArrFeatureValues[0];
        }
        return null;
    }

    public boolean hasFeature(String str) throws IOException {
        if (initFeatureMap()) {
            return this.__featuresMap.containsKey(str.toUpperCase(Locale.ENGLISH));
        }
        return false;
    }

    public boolean hasFeature(String str, String str2) throws IOException {
        Set<String> set;
        if (initFeatureMap() && (set = this.__featuresMap.get(str.toUpperCase(Locale.ENGLISH))) != null) {
            return set.contains(str2);
        }
        return false;
    }

    private boolean initFeatureMap() throws IOException {
        String strSubstring;
        String strSubstring2;
        if (this.__featuresMap == null) {
            boolean zIsPositiveCompletion = FTPReply.isPositiveCompletion(feat());
            this.__featuresMap = new HashMap<>();
            if (!zIsPositiveCompletion) {
                return false;
            }
            for (String str : getReplyStrings()) {
                if (str.startsWith(" ")) {
                    int iIndexOf = str.indexOf(32, 1);
                    if (iIndexOf > 0) {
                        strSubstring = str.substring(1, iIndexOf);
                        strSubstring2 = str.substring(iIndexOf + 1);
                    } else {
                        strSubstring = str.substring(1);
                        strSubstring2 = "";
                    }
                    String upperCase = strSubstring.toUpperCase(Locale.ENGLISH);
                    Set<String> hashSet = this.__featuresMap.get(upperCase);
                    if (hashSet == null) {
                        hashSet = new HashSet<>();
                        this.__featuresMap.put(upperCase, hashSet);
                    }
                    hashSet.add(strSubstring2);
                }
            }
        }
        return true;
    }

    public boolean allocate(int i, int i2) throws IOException {
        return FTPReply.isPositiveCompletion(allo(i, i2));
    }

    public boolean doCommand(String str, String str2) throws IOException {
        return FTPReply.isPositiveCompletion(sendCommand(str, str2));
    }

    public String[] doCommandAsStrings(String str, String str2) throws IOException {
        if (FTPReply.isPositiveCompletion(sendCommand(str, str2))) {
            return getReplyStrings();
        }
        return null;
    }

    public FTPFile mlistFile(String str) throws IOException {
        if (FTPReply.isPositiveCompletion(sendCommand(FTPCmd.MLST, str))) {
            return MLSxEntryParser.parseEntry(getReplyStrings()[1].substring(1));
        }
        return null;
    }

    public FTPFile[] mlistDir() throws IOException {
        return mlistDir(null);
    }

    public FTPFile[] mlistDir(String str) throws IOException {
        return initiateMListParsing(str).getFiles();
    }

    public FTPFile[] mlistDir(String str, FTPFileFilter fTPFileFilter) throws IOException {
        return initiateMListParsing(str).getFiles(fTPFileFilter);
    }

    protected boolean restart(long j) throws IOException {
        this.__restartOffset = 0L;
        return FTPReply.isPositiveIntermediate(rest(Long.toString(j)));
    }

    public void setRestartOffset(long j) {
        if (j >= 0) {
            this.__restartOffset = j;
        }
    }

    public long getRestartOffset() {
        return this.__restartOffset;
    }

    public boolean rename(String str, String str2) throws IOException {
        if (FTPReply.isPositiveIntermediate(rnfr(str))) {
            return FTPReply.isPositiveCompletion(rnto(str2));
        }
        return false;
    }

    public boolean abort() throws IOException {
        return FTPReply.isPositiveCompletion(abor());
    }

    public boolean deleteFile(String str) throws IOException {
        return FTPReply.isPositiveCompletion(dele(str));
    }

    public boolean removeDirectory(String str) throws IOException {
        return FTPReply.isPositiveCompletion(rmd(str));
    }

    public boolean makeDirectory(String str) throws IOException {
        return FTPReply.isPositiveCompletion(mkd(str));
    }

    public String printWorkingDirectory() throws IOException {
        if (pwd() != 257) {
            return null;
        }
        return __parsePathname(this._replyLines.get(this._replyLines.size() - 1));
    }

    public boolean sendSiteCommand(String str) throws IOException {
        return FTPReply.isPositiveCompletion(site(str));
    }

    public String getSystemType() throws IOException {
        if (this.__systemName == null) {
            if (FTPReply.isPositiveCompletion(syst())) {
                this.__systemName = this._replyLines.get(this._replyLines.size() - 1).substring(4);
            } else {
                String property = System.getProperty(FTP_SYSTEM_TYPE_DEFAULT);
                if (property != null) {
                    this.__systemName = property;
                } else {
                    throw new IOException("Unable to determine system type - response: " + getReplyString());
                }
            }
        }
        return this.__systemName;
    }

    public String listHelp() throws IOException {
        if (FTPReply.isPositiveCompletion(help())) {
            return getReplyString();
        }
        return null;
    }

    public String listHelp(String str) throws IOException {
        if (FTPReply.isPositiveCompletion(help(str))) {
            return getReplyString();
        }
        return null;
    }

    public boolean sendNoOp() throws IOException {
        return FTPReply.isPositiveCompletion(noop());
    }

    public String[] listNames(String str) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(FTPCmd.NLST, getListArguments(str));
        if (socket_openDataConnection_ == null) {
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket_openDataConnection_.getInputStream(), getControlEncoding()));
        ArrayList arrayList = new ArrayList();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            arrayList.add(line);
        }
        bufferedReader.close();
        socket_openDataConnection_.close();
        if (completePendingCommand()) {
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }
        return null;
    }

    public String[] listNames() throws IOException {
        return listNames(null);
    }

    public FTPFile[] listFiles(String str) throws IOException {
        return initiateListParsing((String) null, str).getFiles();
    }

    public FTPFile[] listFiles() throws IOException {
        return listFiles((String) null);
    }

    public FTPFile[] listFiles(String str, FTPFileFilter fTPFileFilter) throws IOException {
        return initiateListParsing((String) null, str).getFiles(fTPFileFilter);
    }

    public FTPFile[] listDirectories() throws IOException {
        return listDirectories((String) null);
    }

    public FTPFile[] listDirectories(String str) throws IOException {
        return listFiles(str, FTPFileFilters.DIRECTORIES);
    }

    public FTPListParseEngine initiateListParsing() throws IOException {
        return initiateListParsing((String) null);
    }

    public FTPListParseEngine initiateListParsing(String str) throws IOException {
        return initiateListParsing((String) null, str);
    }

    public FTPListParseEngine initiateListParsing(String str, String str2) throws IOException {
        String property;
        if (this.__entryParser == null || !this.__entryParserKey.equals(str)) {
            if (str != null) {
                this.__entryParser = this.__parserFactory.createFileEntryParser(str);
                this.__entryParserKey = str;
            } else {
                FTPClientConfig fTPClientConfig = this.__configuration;
                if (fTPClientConfig != null) {
                    this.__entryParser = this.__parserFactory.createFileEntryParser(fTPClientConfig);
                    this.__entryParserKey = this.__configuration.getServerSystemKey();
                } else {
                    String property2 = System.getProperty(FTP_SYSTEM_TYPE);
                    if (property2 == null) {
                        property2 = getSystemType();
                        Properties overrideProperties = getOverrideProperties();
                        if (overrideProperties != null && (property = overrideProperties.getProperty(property2)) != null) {
                            property2 = property;
                        }
                    }
                    this.__entryParser = this.__parserFactory.createFileEntryParser(property2);
                    this.__entryParserKey = property2;
                }
            }
        }
        return initiateListParsing(this.__entryParser, str2);
    }

    private FTPListParseEngine initiateListParsing(FTPFileEntryParser fTPFileEntryParser, String str) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(FTPCmd.LIST, getListArguments(str));
        FTPListParseEngine fTPListParseEngine = new FTPListParseEngine(fTPFileEntryParser);
        if (socket_openDataConnection_ == null) {
            return fTPListParseEngine;
        }
        try {
            fTPListParseEngine.readServerList(socket_openDataConnection_.getInputStream(), getControlEncoding());
            Util.closeQuietly(socket_openDataConnection_);
            completePendingCommand();
            return fTPListParseEngine;
        } catch (Throwable th) {
            Util.closeQuietly(socket_openDataConnection_);
            throw th;
        }
    }

    private FTPListParseEngine initiateMListParsing(String str) throws IOException {
        Socket socket_openDataConnection_ = _openDataConnection_(FTPCmd.MLSD, str);
        FTPListParseEngine fTPListParseEngine = new FTPListParseEngine(MLSxEntryParser.getInstance());
        if (socket_openDataConnection_ == null) {
            return fTPListParseEngine;
        }
        try {
            fTPListParseEngine.readServerList(socket_openDataConnection_.getInputStream(), getControlEncoding());
            return fTPListParseEngine;
        } finally {
            Util.closeQuietly(socket_openDataConnection_);
            completePendingCommand();
        }
    }

    protected String getListArguments(String str) {
        if (!getListHiddenFiles()) {
            return str;
        }
        if (str == null) {
            return "-a";
        }
        StringBuilder sb = new StringBuilder(str.length() + 3);
        sb.append("-a ");
        sb.append(str);
        return sb.toString();
    }

    public String getStatus() throws IOException {
        if (FTPReply.isPositiveCompletion(stat())) {
            return getReplyString();
        }
        return null;
    }

    public String getStatus(String str) throws IOException {
        if (FTPReply.isPositiveCompletion(stat(str))) {
            return getReplyString();
        }
        return null;
    }

    public String getModificationTime(String str) throws IOException {
        if (FTPReply.isPositiveCompletion(mdtm(str))) {
            return getReplyString();
        }
        return null;
    }

    public boolean setModificationTime(String str, String str2) throws IOException {
        return FTPReply.isPositiveCompletion(mfmt(str, str2));
    }

    public void setBufferSize(int i) {
        this.__bufferSize = i;
    }

    public int getBufferSize() {
        return this.__bufferSize;
    }

    public void setSendDataSocketBufferSize(int i) {
        this.__sendDataSocketBufferSize = i;
    }

    public int getSendDataSocketBufferSize() {
        return this.__sendDataSocketBufferSize;
    }

    public void setReceieveDataSocketBufferSize(int i) {
        this.__receiveDataSocketBufferSize = i;
    }

    public int getReceiveDataSocketBufferSize() {
        return this.__receiveDataSocketBufferSize;
    }

    @Override // org.apache.commons.net.ftp.Configurable
    public void configure(FTPClientConfig fTPClientConfig) {
        this.__configuration = fTPClientConfig;
    }

    public void setListHiddenFiles(boolean z) {
        this.__listHiddenFiles = z;
    }

    public boolean getListHiddenFiles() {
        return this.__listHiddenFiles;
    }

    public boolean isUseEPSVwithIPv4() {
        return this.__useEPSVwithIPv4;
    }

    public void setUseEPSVwithIPv4(boolean z) {
        this.__useEPSVwithIPv4 = z;
    }

    public void setCopyStreamListener(CopyStreamListener copyStreamListener) {
        this.__copyStreamListener = copyStreamListener;
    }

    public CopyStreamListener getCopyStreamListener() {
        return this.__copyStreamListener;
    }

    public void setControlKeepAliveTimeout(long j) {
        this.__controlKeepAliveTimeout = j * 1000;
    }

    public long getControlKeepAliveTimeout() {
        return this.__controlKeepAliveTimeout / 1000;
    }

    public void setControlKeepAliveReplyTimeout(int i) {
        this.__controlKeepAliveReplyTimeout = i;
    }

    public int getControlKeepAliveReplyTimeout() {
        return this.__controlKeepAliveReplyTimeout;
    }

    public void setPassiveNatWorkaround(boolean z) {
        this.__passiveNatWorkaround = z;
    }

    private OutputStream getBufferedOutputStream(OutputStream outputStream) {
        if (this.__bufferSize > 0) {
            return new BufferedOutputStream(outputStream, this.__bufferSize);
        }
        return new BufferedOutputStream(outputStream);
    }

    private InputStream getBufferedInputStream(InputStream inputStream) {
        if (this.__bufferSize > 0) {
            return new BufferedInputStream(inputStream, this.__bufferSize);
        }
        return new BufferedInputStream(inputStream);
    }

    private static class CSL implements CopyStreamListener {
        private final int currentSoTimeout;
        private final long idle;
        private int notAcked;
        private final FTPClient parent;
        private long time = System.currentTimeMillis();

        CSL(FTPClient fTPClient, long j, int i) throws SocketException {
            this.idle = j;
            this.parent = fTPClient;
            this.currentSoTimeout = fTPClient.getSoTimeout();
            fTPClient.setSoTimeout(i);
        }

        @Override // org.apache.commons.net.io.CopyStreamListener
        public void bytesTransferred(CopyStreamEvent copyStreamEvent) {
            bytesTransferred(copyStreamEvent.getTotalBytesTransferred(), copyStreamEvent.getBytesTransferred(), copyStreamEvent.getStreamSize());
        }

        @Override // org.apache.commons.net.io.CopyStreamListener
        public void bytesTransferred(long j, int i, long j2) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.time > this.idle) {
                try {
                    this.parent.__noop();
                } catch (SocketTimeoutException unused) {
                    this.notAcked++;
                } catch (IOException unused2) {
                }
                this.time = jCurrentTimeMillis;
            }
        }

        void cleanUp() throws IOException {
            while (true) {
                try {
                    int i = this.notAcked;
                    this.notAcked = i - 1;
                    if (i <= 0) {
                        return;
                    } else {
                        this.parent.__getReplyNoReport();
                    }
                } finally {
                    this.parent.setSoTimeout(this.currentSoTimeout);
                }
            }
        }
    }

    private CopyStreamListener __mergeListeners(CopyStreamListener copyStreamListener) {
        if (copyStreamListener == null) {
            return this.__copyStreamListener;
        }
        if (this.__copyStreamListener == null) {
            return copyStreamListener;
        }
        CopyStreamAdapter copyStreamAdapter = new CopyStreamAdapter();
        copyStreamAdapter.addCopyStreamListener(copyStreamListener);
        copyStreamAdapter.addCopyStreamListener(this.__copyStreamListener);
        return copyStreamAdapter;
    }

    public void setAutodetectUTF8(boolean z) {
        this.__autodetectEncoding = z;
    }

    public boolean getAutodetectUTF8() {
        return this.__autodetectEncoding;
    }

    @Deprecated
    public String getSystemName() throws IOException {
        if (this.__systemName == null && FTPReply.isPositiveCompletion(syst())) {
            this.__systemName = this._replyLines.get(this._replyLines.size() - 1).substring(4);
        }
        return this.__systemName;
    }
}

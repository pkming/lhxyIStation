package org.apache.commons.net.imap;

import android.text.format.DateFormat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.io.CRLFLineReader;

/* JADX INFO: loaded from: classes3.dex */
public class IMAP extends SocketClient {
    public static final int DEFAULT_PORT = 143;
    protected static final String __DEFAULT_ENCODING = "ISO-8859-1";
    private IMAPState __state;
    protected BufferedWriter __writer;
    private final char[] _initialID = {DateFormat.CAPITAL_AM_PM, DateFormat.CAPITAL_AM_PM, DateFormat.CAPITAL_AM_PM, DateFormat.CAPITAL_AM_PM};
    protected BufferedReader _reader;
    private int _replyCode;
    private final List<String> _replyLines;

    public enum IMAPState {
        DISCONNECTED_STATE,
        NOT_AUTH_STATE,
        AUTH_STATE,
        LOGOUT_STATE
    }

    public IMAP() {
        setDefaultPort(143);
        this.__state = IMAPState.DISCONNECTED_STATE;
        this._reader = null;
        this.__writer = null;
        this._replyLines = new ArrayList();
        createCommandSupport();
    }

    private void __getReply() throws IOException {
        __getReply(true);
    }

    private void __getReply(boolean z) throws IOException {
        this._replyLines.clear();
        String line = this._reader.readLine();
        if (line == null) {
            throw new EOFException("Connection closed without indication.");
        }
        this._replyLines.add(line);
        if (z) {
            while (IMAPReply.isUntagged(line)) {
                int iLiteralCount = IMAPReply.literalCount(line);
                while (iLiteralCount >= 0) {
                    String line2 = this._reader.readLine();
                    if (line2 == null) {
                        throw new EOFException("Connection closed without indication.");
                    }
                    this._replyLines.add(line2);
                    iLiteralCount -= line2.length() + 2;
                }
                line = this._reader.readLine();
                if (line == null) {
                    throw new EOFException("Connection closed without indication.");
                }
                this._replyLines.add(line);
            }
            this._replyCode = IMAPReply.getReplyCode(line);
        } else {
            this._replyCode = IMAPReply.getUntaggedReplyCode(line);
        }
        fireReplyReceived(this._replyCode, getReplyString());
    }

    @Override // org.apache.commons.net.SocketClient
    protected void _connectAction_() throws IOException {
        super._connectAction_();
        this._reader = new CRLFLineReader(new InputStreamReader(this._input_, "ISO-8859-1"));
        this.__writer = new BufferedWriter(new OutputStreamWriter(this._output_, "ISO-8859-1"));
        int soTimeout = getSoTimeout();
        if (soTimeout <= 0) {
            setSoTimeout(this.connectTimeout);
        }
        __getReply(false);
        if (soTimeout <= 0) {
            setSoTimeout(soTimeout);
        }
        setState(IMAPState.NOT_AUTH_STATE);
    }

    protected void setState(IMAPState iMAPState) {
        this.__state = iMAPState;
    }

    public IMAPState getState() {
        return this.__state;
    }

    @Override // org.apache.commons.net.SocketClient
    public void disconnect() throws IOException {
        super.disconnect();
        this._reader = null;
        this.__writer = null;
        this._replyLines.clear();
        setState(IMAPState.DISCONNECTED_STATE);
    }

    private int sendCommandWithID(String str, String str2, String str3) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append(str);
            sb.append(' ');
        }
        sb.append(str2);
        if (str3 != null) {
            sb.append(' ');
            sb.append(str3);
        }
        sb.append("\r\n");
        String string = sb.toString();
        this.__writer.write(string);
        this.__writer.flush();
        fireCommandSent(str2, string);
        __getReply();
        return this._replyCode;
    }

    public int sendCommand(String str, String str2) throws IOException {
        return sendCommandWithID(generateCommandID(), str, str2);
    }

    public int sendCommand(String str) throws IOException {
        return sendCommand(str, (String) null);
    }

    public int sendCommand(IMAPCommand iMAPCommand, String str) throws IOException {
        return sendCommand(iMAPCommand.getIMAPCommand(), str);
    }

    public boolean doCommand(IMAPCommand iMAPCommand, String str) throws IOException {
        return IMAPReply.isSuccess(sendCommand(iMAPCommand, str));
    }

    public int sendCommand(IMAPCommand iMAPCommand) throws IOException {
        return sendCommand(iMAPCommand, (String) null);
    }

    public boolean doCommand(IMAPCommand iMAPCommand) throws IOException {
        return IMAPReply.isSuccess(sendCommand(iMAPCommand));
    }

    public int sendData(String str) throws IOException {
        return sendCommandWithID(null, str, null);
    }

    public String[] getReplyStrings() {
        List<String> list = this._replyLines;
        return (String[]) list.toArray(new String[list.size()]);
    }

    public String getReplyString() {
        StringBuilder sb = new StringBuilder(256);
        Iterator<String> it = this._replyLines.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append("\r\n");
        }
        return sb.toString();
    }

    protected String generateCommandID() {
        String str = new String(this._initialID);
        boolean z = true;
        for (int length = this._initialID.length - 1; z && length >= 0; length--) {
            char[] cArr = this._initialID;
            if (cArr[length] == 'Z') {
                cArr[length] = DateFormat.CAPITAL_AM_PM;
            } else {
                cArr[length] = (char) (cArr[length] + 1);
                z = false;
            }
        }
        return str;
    }
}

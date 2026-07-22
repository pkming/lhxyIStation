package org.apache.tools.mail;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.Manifest;

/* JADX INFO: loaded from: classes3.dex */
public class MailMessage {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 25;
    private static final int OK_DATA = 354;
    private static final int OK_DOT = 250;
    private static final int OK_FROM = 250;
    private static final int OK_HELO = 250;
    private static final int OK_QUIT = 221;
    private static final int OK_RCPT_1 = 250;
    private static final int OK_RCPT_2 = 251;
    private static final int OK_READY = 220;
    private Vector cc;
    private String from;
    private Vector headersKeys;
    private Vector headersValues;
    private String host;
    private SmtpResponseReader in;
    private MailPrintStream out;
    private int port;
    private Vector replyto;
    private Socket socket;
    private Vector to;

    public MailMessage() throws IOException {
        this("localhost", 25);
    }

    public MailMessage(String str) throws IOException {
        this(str, 25);
    }

    public MailMessage(String str, int i) throws IOException {
        this.port = 25;
        this.port = i;
        this.host = str;
        this.replyto = new Vector();
        this.to = new Vector();
        this.cc = new Vector();
        this.headersKeys = new Vector();
        this.headersValues = new Vector();
        connect();
        sendHelo();
    }

    public void setPort(int i) {
        this.port = i;
    }

    public void from(String str) throws IOException {
        sendFrom(str);
        this.from = str;
    }

    public void replyto(String str) {
        this.replyto.addElement(str);
    }

    public void to(String str) throws IOException {
        sendRcpt(str);
        this.to.addElement(str);
    }

    public void cc(String str) throws IOException {
        sendRcpt(str);
        this.cc.addElement(str);
    }

    public void bcc(String str) throws IOException {
        sendRcpt(str);
    }

    public void setSubject(String str) {
        setHeader("Subject", str);
    }

    public void setHeader(String str, String str2) {
        this.headersKeys.add(str);
        this.headersValues.add(str2);
    }

    public PrintStream getPrintStream() throws IOException {
        setFromHeader();
        setReplyToHeader();
        setToHeader();
        setCcHeader();
        setHeader("X-Mailer", "org.apache.tools.mail.MailMessage (ant.apache.org)");
        sendData();
        flushHeaders();
        return this.out;
    }

    void setFromHeader() {
        setHeader(Manifest.ATTRIBUTE_FROM, this.from);
    }

    void setReplyToHeader() {
        if (this.replyto.isEmpty()) {
            return;
        }
        setHeader("Reply-To", vectorToList(this.replyto));
    }

    void setToHeader() {
        if (this.to.isEmpty()) {
            return;
        }
        setHeader("To", vectorToList(this.to));
    }

    void setCcHeader() {
        if (this.cc.isEmpty()) {
            return;
        }
        setHeader("Cc", vectorToList(this.cc));
    }

    String vectorToList(Vector vector) {
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration enumerationElements = vector.elements();
        while (enumerationElements.hasMoreElements()) {
            stringBuffer.append(enumerationElements.nextElement());
            if (enumerationElements.hasMoreElements()) {
                stringBuffer.append(", ");
            }
        }
        return stringBuffer.toString();
    }

    void flushHeaders() throws IOException {
        int size = this.headersKeys.size();
        for (int i = 0; i < size; i++) {
            this.out.println(((String) this.headersKeys.elementAt(i)) + ": " + ((String) this.headersValues.elementAt(i)));
        }
        this.out.println();
        this.out.flush();
    }

    public void sendAndClose() throws IOException {
        try {
            sendDot();
            sendQuit();
        } finally {
            disconnect();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0020 A[PHI: r4
      0x0020: PHI (r4v2 int) = (r4v1 int), (r4v3 int) binds: [B:16:0x0027, B:12:0x001e] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0016 A[PHI: r4
      0x0016: PHI (r4v5 int) = (r4v1 int), (r4v6 int) binds: [B:20:0x002e, B:7:0x0014] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static java.lang.String sanitizeAddress(java.lang.String r7) {
        /*
            int r0 = r7.length()
            r1 = 0
            r2 = r1
            r3 = r2
            r4 = r3
        L8:
            if (r1 >= r0) goto L34
            char r5 = r7.charAt(r1)
            r6 = 40
            if (r5 != r6) goto L18
            int r4 = r4 + 1
            if (r3 != 0) goto L31
        L16:
            r2 = r1
            goto L31
        L18:
            r6 = 41
            if (r5 != r6) goto L23
            int r4 = r4 + (-1)
            if (r2 != 0) goto L31
        L20:
            int r3 = r1 + 1
            goto L31
        L23:
            if (r4 != 0) goto L2a
            r6 = 60
            if (r5 != r6) goto L2a
            goto L20
        L2a:
            if (r4 != 0) goto L31
            r6 = 62
            if (r5 != r6) goto L31
            goto L16
        L31:
            int r1 = r1 + 1
            goto L8
        L34:
            if (r2 != 0) goto L37
            goto L38
        L37:
            r0 = r2
        L38:
            java.lang.String r7 = r7.substring(r3, r0)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.mail.MailMessage.sanitizeAddress(java.lang.String):java.lang.String");
    }

    void connect() throws IOException {
        this.socket = new Socket(this.host, this.port);
        this.out = new MailPrintStream(new BufferedOutputStream(this.socket.getOutputStream()));
        this.in = new SmtpResponseReader(this.socket.getInputStream());
        getReady();
    }

    void getReady() throws IOException {
        String response = this.in.getResponse();
        if (!isResponseOK(response, new int[]{220})) {
            throw new IOException("Didn't get introduction from server: " + response);
        }
    }

    void sendHelo() throws IOException {
        send("HELO " + InetAddress.getLocalHost().getHostName(), new int[]{250});
    }

    void sendFrom(String str) throws IOException {
        send("MAIL FROM: <" + sanitizeAddress(str) + ">", new int[]{250});
    }

    void sendRcpt(String str) throws IOException {
        send("RCPT TO: <" + sanitizeAddress(str) + ">", new int[]{250, 251});
    }

    void sendData() throws IOException {
        send("DATA", new int[]{354});
    }

    void sendDot() throws IOException {
        send("\r\n.", new int[]{250});
    }

    void sendQuit() throws IOException {
        try {
            send("QUIT", new int[]{221});
        } catch (IOException e) {
            throw new ErrorInQuitException(e);
        }
    }

    void send(String str, int[] iArr) throws IOException {
        this.out.rawPrint(str + "\r\n");
        String response = this.in.getResponse();
        if (!isResponseOK(response, iArr)) {
            throw new IOException("Unexpected reply to command: " + str + ": " + response);
        }
    }

    boolean isResponseOK(String str, int[] iArr) {
        for (int i : iArr) {
            if (str.startsWith("" + i)) {
                return true;
            }
        }
        return false;
    }

    void disconnect() throws IOException {
        MailPrintStream mailPrintStream = this.out;
        if (mailPrintStream != null) {
            mailPrintStream.close();
        }
        SmtpResponseReader smtpResponseReader = this.in;
        if (smtpResponseReader != null) {
            try {
                smtpResponseReader.close();
            } catch (IOException unused) {
            }
        }
        Socket socket = this.socket;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException unused2) {
            }
        }
    }
}

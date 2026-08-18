package org.apache.tools.ant.listener;

import android.accounts.AccountManager;
import android.content.Context;
import android.hardware.Camera;
import android.security.KeyChain;
import com.unisound.common.r;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.email.EmailAddress;
import org.apache.tools.ant.taskdefs.email.Mailer;
import org.apache.tools.ant.taskdefs.email.Message;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.mail.MailMessage;

/* JADX INFO: loaded from: classes3.dex */
public class MailLogger extends DefaultLogger {
    private static final String DEFAULT_MIME_TYPE = "text/plain";
    private StringBuffer buffer = new StringBuffer();

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.tools.ant.DefaultLogger, org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) throws Throwable {
        FileInputStream fileInputStream;
        super.buildFinished(buildEvent);
        Project project = buildEvent.getProject();
        Hashtable<String, Object> properties = project.getProperties();
        Properties properties2 = new Properties();
        String str = (String) properties.get("MailLogger.properties.file");
        FileInputStream fileInputStream2 = null;
        Object[] objArr = 0;
        if (str != null) {
            try {
                fileInputStream = new FileInputStream(str);
                try {
                    properties2.load(fileInputStream);
                } catch (IOException unused) {
                } catch (Throwable th) {
                    th = th;
                    fileInputStream2 = fileInputStream;
                    FileUtils.close(fileInputStream2);
                    throw th;
                }
            } catch (IOException unused2) {
                fileInputStream = null;
            } catch (Throwable th2) {
                th = th2;
            }
            FileUtils.close(fileInputStream);
        }
        Enumeration enumerationKeys = properties2.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str2 = (String) enumerationKeys.nextElement();
            properties.put(str2, project.replaceProperties(properties2.getProperty(str2)));
        }
        boolean z = buildEvent.getException() == null;
        String str3 = z ? r.C : "failure";
        try {
            if (Project.toBoolean(getValue(properties, str3 + ".notify", Camera.Parameters.FLASH_MODE_ON))) {
                Values valuesSubject = new Values().mailhost(getValue(properties, "mailhost", "localhost")).port(Integer.parseInt(getValue(properties, KeyChain.EXTRA_PORT, String.valueOf(25)))).user(getValue(properties, Context.USER_SERVICE, "")).password(getValue(properties, AccountManager.KEY_PASSWORD, "")).ssl(Project.toBoolean(getValue(properties, "ssl", "off"))).starttls(Project.toBoolean(getValue(properties, "starttls.enable", "off"))).from(getValue(properties, "from", null)).replytoList(getValue(properties, "replyto", "")).toList(getValue(properties, str3 + ".to", null)).mimeType(getValue(properties, "mimeType", "text/plain")).charset(getValue(properties, "charset", "")).body(getValue(properties, str3 + ".body", "")).subject(getValue(properties, str3 + ".subject", z ? "Build Success" : "Build Failure"));
                if (valuesSubject.user().equals("") && valuesSubject.password().equals("") && !valuesSubject.ssl() && !valuesSubject.starttls()) {
                    sendMail(valuesSubject, this.buffer.substring(0));
                } else {
                    sendMimeMail(buildEvent.getProject(), valuesSubject, this.buffer.substring(0));
                }
            }
        } catch (Exception e) {
            System.out.println("MailLogger failed to send e-mail!");
            e.printStackTrace(System.err);
        }
    }

    private static class Values {
        private String body;
        private String charset;
        private String from;
        private String mailhost;
        private String mimeType;
        private String password;
        private int port;
        private String replytoList;
        private boolean ssl;
        private boolean starttls;
        private String subject;
        private String toList;
        private String user;

        private Values() {
        }

        public String mailhost() {
            return this.mailhost;
        }

        public Values mailhost(String str) {
            this.mailhost = str;
            return this;
        }

        public int port() {
            return this.port;
        }

        public Values port(int i) {
            this.port = i;
            return this;
        }

        public String user() {
            return this.user;
        }

        public Values user(String str) {
            this.user = str;
            return this;
        }

        public String password() {
            return this.password;
        }

        public Values password(String str) {
            this.password = str;
            return this;
        }

        public boolean ssl() {
            return this.ssl;
        }

        public Values ssl(boolean z) {
            this.ssl = z;
            return this;
        }

        public String from() {
            return this.from;
        }

        public Values from(String str) {
            this.from = str;
            return this;
        }

        public String replytoList() {
            return this.replytoList;
        }

        public Values replytoList(String str) {
            this.replytoList = str;
            return this;
        }

        public String toList() {
            return this.toList;
        }

        public Values toList(String str) {
            this.toList = str;
            return this;
        }

        public String subject() {
            return this.subject;
        }

        public Values subject(String str) {
            this.subject = str;
            return this;
        }

        public String charset() {
            return this.charset;
        }

        public Values charset(String str) {
            this.charset = str;
            return this;
        }

        public String mimeType() {
            return this.mimeType;
        }

        public Values mimeType(String str) {
            this.mimeType = str;
            return this;
        }

        public String body() {
            return this.body;
        }

        public Values body(String str) {
            this.body = str;
            return this;
        }

        public boolean starttls() {
            return this.starttls;
        }

        public Values starttls(boolean z) {
            this.starttls = z;
            return this;
        }
    }

    @Override // org.apache.tools.ant.DefaultLogger
    protected void log(String str) {
        this.buffer.append(str).append(StringUtils.LINE_SEP);
    }

    private String getValue(Hashtable<String, Object> hashtable, String str, String str2) throws Exception {
        String str3 = "MailLogger." + str;
        String str4 = (String) hashtable.get(str3);
        if (str4 != null) {
            str2 = str4;
        }
        if (str2 != null) {
            return str2;
        }
        throw new Exception("Missing required parameter: " + str3);
    }

    private void sendMail(Values values, String str) throws IOException {
        MailMessage mailMessage = new MailMessage(values.mailhost(), values.port());
        mailMessage.setHeader("Date", DateUtils.getDateForHeader());
        mailMessage.from(values.from());
        if (!values.replytoList().equals("")) {
            StringTokenizer stringTokenizer = new StringTokenizer(values.replytoList(), ", ", false);
            while (stringTokenizer.hasMoreTokens()) {
                mailMessage.replyto(stringTokenizer.nextToken());
            }
        }
        StringTokenizer stringTokenizer2 = new StringTokenizer(values.toList(), ", ", false);
        while (stringTokenizer2.hasMoreTokens()) {
            mailMessage.to(stringTokenizer2.nextToken());
        }
        mailMessage.setSubject(values.subject());
        if (values.charset().length() > 0) {
            mailMessage.setHeader("Content-Type", values.mimeType() + "; charset=\"" + values.charset() + "\"");
        } else {
            mailMessage.setHeader("Content-Type", values.mimeType());
        }
        PrintStream printStream = mailMessage.getPrintStream();
        if (values.body().length() > 0) {
            str = values.body();
        }
        printStream.println(str);
        mailMessage.sendAndClose();
    }

    private void sendMimeMail(Project project, Values values, String str) {
        try {
            Mailer mailer = (Mailer) ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.MimeMailer", MailLogger.class.getClassLoader(), Mailer.class);
            Vector<EmailAddress> vectorVectorizeEmailAddresses = vectorizeEmailAddresses(values.replytoList());
            mailer.setHost(values.mailhost());
            mailer.setPort(values.port());
            mailer.setUser(values.user());
            mailer.setPassword(values.password());
            mailer.setSSL(values.ssl());
            mailer.setEnableStartTLS(values.starttls());
            if (values.body().length() > 0) {
                str = values.body();
            }
            Message message = new Message(str);
            message.setProject(project);
            message.setMimeType(values.mimeType());
            if (values.charset().length() > 0) {
                message.setCharset(values.charset());
            }
            mailer.setMessage(message);
            mailer.setFrom(new EmailAddress(values.from()));
            mailer.setReplyToList(vectorVectorizeEmailAddresses);
            mailer.setToList(vectorizeEmailAddresses(values.toList()));
            mailer.setCcList(new Vector<>());
            mailer.setBccList(new Vector<>());
            mailer.setFiles(new Vector<>());
            mailer.setSubject(values.subject());
            mailer.setHeaders(new Vector<>());
            mailer.send();
        } catch (BuildException e) {
            Throwable cause = e.getCause();
            BuildException cause2 = e;
            if (cause != null) {
                cause2 = e.getCause();
            }
            log("Failed to initialise MIME mail: " + cause2.getMessage());
        }
    }

    private Vector<EmailAddress> vectorizeEmailAddresses(String str) {
        Vector<EmailAddress> vector = new Vector<>();
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(new EmailAddress(stringTokenizer.nextToken()));
        }
        return vector;
    }
}

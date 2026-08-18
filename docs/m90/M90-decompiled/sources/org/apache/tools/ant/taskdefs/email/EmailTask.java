package org.apache.tools.ant.taskdefs.email;

import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.ClasspathUtils;

/* JADX INFO: loaded from: classes3.dex */
public class EmailTask extends Task {
    public static final String AUTO = "auto";
    public static final String MIME = "mime";
    public static final String PLAIN = "plain";
    private static final int SMTP_PORT = 25;
    public static final String UU = "uu";
    private String messageFileInputEncoding;
    private String encoding = "auto";
    private String host = "localhost";
    private Integer port = null;
    private String subject = null;
    private Message message = null;
    private boolean failOnError = true;
    private boolean includeFileNames = false;
    private String messageMimeType = null;
    private EmailAddress from = null;
    private Vector replyToList = new Vector();
    private Vector toList = new Vector();
    private Vector ccList = new Vector();
    private Vector bccList = new Vector();
    private Vector headers = new Vector();
    private Path attachments = null;
    private String charset = null;
    private String user = null;
    private String password = null;
    private boolean ssl = false;
    private boolean starttls = false;
    private boolean ignoreInvalidRecipients = false;

    public static class Encoding extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"auto", "mime", EmailTask.UU, EmailTask.PLAIN};
        }
    }

    public void setUser(String str) {
        this.user = str;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setSSL(boolean z) {
        this.ssl = z;
    }

    public void setEnableStartTLS(boolean z) {
        this.starttls = z;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding.getValue();
    }

    public void setMailport(int i) {
        this.port = new Integer(i);
    }

    public void setMailhost(String str) {
        this.host = str;
    }

    public void setSubject(String str) {
        this.subject = str;
    }

    public void setMessage(String str) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        Message message = new Message(str);
        this.message = message;
        message.setProject(getProject());
    }

    public void setMessageFile(File file) {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        Message message = new Message(file);
        this.message = message;
        message.setProject(getProject());
    }

    public void setMessageMimeType(String str) {
        this.messageMimeType = str;
    }

    public void addMessage(Message message) throws BuildException {
        if (this.message != null) {
            throw new BuildException("Only one message can be sent in an email");
        }
        this.message = message;
    }

    public void addFrom(EmailAddress emailAddress) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }
        this.from = emailAddress;
    }

    public void setFrom(String str) {
        if (this.from != null) {
            throw new BuildException("Emails can only be from one address");
        }
        this.from = new EmailAddress(str);
    }

    public void addReplyTo(EmailAddress emailAddress) {
        this.replyToList.add(emailAddress);
    }

    public void setReplyTo(String str) {
        this.replyToList.add(new EmailAddress(str));
    }

    public void addTo(EmailAddress emailAddress) {
        this.toList.addElement(emailAddress);
    }

    public void setToList(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            this.toList.addElement(new EmailAddress(stringTokenizer.nextToken()));
        }
    }

    public void addCc(EmailAddress emailAddress) {
        this.ccList.addElement(emailAddress);
    }

    public void setCcList(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            this.ccList.addElement(new EmailAddress(stringTokenizer.nextToken()));
        }
    }

    public void addBcc(EmailAddress emailAddress) {
        this.bccList.addElement(emailAddress);
    }

    public void setBccList(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            this.bccList.addElement(new EmailAddress(stringTokenizer.nextToken()));
        }
    }

    public void setFailOnError(boolean z) {
        this.failOnError = z;
    }

    public void setFiles(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ", ");
        while (stringTokenizer.hasMoreTokens()) {
            createAttachments().add(new FileResource(getProject().resolveFile(stringTokenizer.nextToken())));
        }
    }

    public void addFileset(FileSet fileSet) {
        createAttachments().add(fileSet);
    }

    public Path createAttachments() {
        if (this.attachments == null) {
            this.attachments = new Path(getProject());
        }
        return this.attachments.createPath();
    }

    public Header createHeader() {
        Header header = new Header();
        this.headers.add(header);
        return header;
    }

    public void setIncludefilenames(boolean z) {
        this.includeFileNames = z;
    }

    public boolean getIncludeFileNames() {
        return this.includeFileNames;
    }

    public void setIgnoreInvalidRecipients(boolean z) {
        this.ignoreInvalidRecipients = z;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() {
        boolean z;
        Mailer mailer;
        BuildException e;
        BuildException e2;
        Mailer mailer2;
        Message message = this.message;
        Mailer plainMailer = null;
        try {
            try {
                try {
                    if (this.encoding.equals("mime") || this.encoding.equals("auto")) {
                        try {
                            Class.forName("javax.activation.DataHandler");
                            Class.forName("javax.mail.internet.MimeMessage");
                            mailer = (Mailer) ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.MimeMailer", EmailTask.class.getClassLoader(), Mailer.class);
                            try {
                                log("Using MIME mail", 3);
                                z = true;
                            } catch (BuildException e3) {
                                e = e3;
                                z = true;
                                logBuildException("Failed to initialise MIME mail: ", e);
                            }
                        } catch (BuildException e4) {
                            z = false;
                            mailer = null;
                            e = e4;
                        }
                        plainMailer = mailer;
                    } else {
                        z = false;
                    }
                    if (!z && ((this.user != null || this.password != null) && (this.encoding.equals(UU) || this.encoding.equals(PLAIN)))) {
                        throw new BuildException("SMTP auth only possible with MIME mail");
                    }
                    if (!z && ((this.ssl || this.starttls) && (this.encoding.equals(UU) || this.encoding.equals(PLAIN)))) {
                        throw new BuildException("SSL and STARTTLS only possible with MIME mail");
                    }
                    if (this.encoding.equals(UU) || (this.encoding.equals("auto") && !z)) {
                        try {
                            mailer2 = (Mailer) ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.UUMailer", EmailTask.class.getClassLoader(), Mailer.class);
                            try {
                                log("Using UU mail", 3);
                                plainMailer = mailer2;
                                z = true;
                            } catch (BuildException e5) {
                                e2 = e5;
                                z = true;
                                logBuildException("Failed to initialise UU mail: ", e2);
                                plainMailer = mailer2;
                            }
                        } catch (BuildException e6) {
                            Mailer mailer3 = plainMailer;
                            e2 = e6;
                            mailer2 = mailer3;
                        }
                    }
                    if (this.encoding.equals(PLAIN) || (this.encoding.equals("auto") && !z)) {
                        plainMailer = new PlainMailer();
                        log("Using plain mail", 3);
                    }
                } catch (BuildException e7) {
                    logBuildException("Failed to send email: ", e7);
                    if (this.failOnError) {
                        throw e7;
                    }
                }
            } catch (Exception e8) {
                log("Failed to send email: " + e8.getMessage(), 1);
                if (this.failOnError) {
                    throw new BuildException(e8);
                }
            }
            if (plainMailer == null) {
                throw new BuildException("Failed to initialise encoding: " + this.encoding);
            }
            if (this.message == null) {
                Message message2 = new Message();
                this.message = message2;
                message2.setProject(getProject());
            }
            EmailAddress emailAddress = this.from;
            if (emailAddress == null || emailAddress.getAddress() == null) {
                throw new BuildException("A from element is required");
            }
            if (this.toList.isEmpty() && this.ccList.isEmpty() && this.bccList.isEmpty()) {
                throw new BuildException("At least one of to, cc or bcc must be supplied");
            }
            if (this.messageMimeType != null) {
                if (this.message.isMimeTypeSpecified()) {
                    throw new BuildException("The mime type can only be specified in one location");
                }
                this.message.setMimeType(this.messageMimeType);
            }
            if (this.charset != null) {
                if (this.message.getCharset() != null) {
                    throw new BuildException("The charset can only be specified in one location");
                }
                this.message.setCharset(this.charset);
            }
            this.message.setInputEncoding(this.messageFileInputEncoding);
            Vector<File> vector = new Vector<>();
            Path path = this.attachments;
            if (path != null) {
                Iterator<Resource> it = path.iterator();
                while (it.hasNext()) {
                    vector.addElement(((FileProvider) it.next().as(FileProvider.class)).getFile());
                }
            }
            log("Sending email: " + this.subject, 2);
            log("From " + this.from, 3);
            log("ReplyTo " + this.replyToList, 3);
            log("To " + this.toList, 3);
            log("Cc " + this.ccList, 3);
            log("Bcc " + this.bccList, 3);
            plainMailer.setHost(this.host);
            Integer num = this.port;
            if (num != null) {
                plainMailer.setPort(num.intValue());
                plainMailer.setPortExplicitlySpecified(true);
            } else {
                plainMailer.setPort(25);
                plainMailer.setPortExplicitlySpecified(false);
            }
            plainMailer.setUser(this.user);
            plainMailer.setPassword(this.password);
            plainMailer.setSSL(this.ssl);
            plainMailer.setEnableStartTLS(this.starttls);
            plainMailer.setMessage(this.message);
            plainMailer.setFrom(this.from);
            plainMailer.setReplyToList(this.replyToList);
            plainMailer.setToList(this.toList);
            plainMailer.setCcList(this.ccList);
            plainMailer.setBccList(this.bccList);
            plainMailer.setFiles(vector);
            plainMailer.setSubject(this.subject);
            plainMailer.setTask(this);
            plainMailer.setIncludeFileNames(this.includeFileNames);
            plainMailer.setHeaders(this.headers);
            plainMailer.setIgnoreInvalidRecipients(this.ignoreInvalidRecipients);
            plainMailer.send();
            int size = vector.size();
            log("Sent email with " + size + " attachment" + (size == 1 ? "" : "s"), 2);
        } finally {
            this.message = message;
        }
    }

    private void logBuildException(String str, BuildException buildException) {
        Throwable cause = buildException.getCause();
        BuildException cause2 = buildException;
        if (cause != null) {
            cause2 = buildException.getCause();
        }
        log(str + cause2.getMessage(), 1);
    }

    public void setCharset(String str) {
        this.charset = str;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setMessageFileInputEncoding(String str) {
        this.messageFileInputEncoding = str;
    }
}

package org.apache.tools.ant.taskdefs.email;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.DateUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Mailer {
    protected EmailAddress from;
    protected Message message;
    protected Task task;
    protected String host = null;
    protected int port = -1;
    protected String user = null;
    protected String password = null;
    protected boolean SSL = false;
    protected Vector<EmailAddress> replyToList = null;
    protected Vector<EmailAddress> toList = null;
    protected Vector<EmailAddress> ccList = null;
    protected Vector<EmailAddress> bccList = null;
    protected Vector<File> files = null;
    protected String subject = null;
    protected boolean includeFileNames = false;
    protected Vector<Header> headers = null;
    private boolean ignoreInvalidRecipients = false;
    private boolean starttls = false;
    private boolean portExplicitlySpecified = false;

    public abstract void send() throws BuildException;

    public void setHost(String str) {
        this.host = str;
    }

    public void setPort(int i) {
        this.port = i;
    }

    public void setPortExplicitlySpecified(boolean z) {
        this.portExplicitlySpecified = z;
    }

    protected boolean isPortExplicitlySpecified() {
        return this.portExplicitlySpecified;
    }

    public void setUser(String str) {
        this.user = str;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setSSL(boolean z) {
        this.SSL = z;
    }

    public void setEnableStartTLS(boolean z) {
        this.starttls = z;
    }

    protected boolean isStartTLSEnabled() {
        return this.starttls;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setFrom(EmailAddress emailAddress) {
        this.from = emailAddress;
    }

    public void setReplyToList(Vector<EmailAddress> vector) {
        this.replyToList = vector;
    }

    public void setToList(Vector<EmailAddress> vector) {
        this.toList = vector;
    }

    public void setCcList(Vector<EmailAddress> vector) {
        this.ccList = vector;
    }

    public void setBccList(Vector<EmailAddress> vector) {
        this.bccList = vector;
    }

    public void setFiles(Vector<File> vector) {
        this.files = vector;
    }

    public void setSubject(String str) {
        this.subject = str;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setIncludeFileNames(boolean z) {
        this.includeFileNames = z;
    }

    public void setHeaders(Vector<Header> vector) {
        this.headers = vector;
    }

    public void setIgnoreInvalidRecipients(boolean z) {
        this.ignoreInvalidRecipients = z;
    }

    protected boolean shouldIgnoreInvalidRecipients() {
        return this.ignoreInvalidRecipients;
    }

    protected final String getDate() {
        return DateUtils.getDateForHeader();
    }
}

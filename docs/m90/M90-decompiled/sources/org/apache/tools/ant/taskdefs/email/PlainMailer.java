package org.apache.tools.ant.taskdefs.email;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.mail.MailMessage;

/* JADX INFO: loaded from: classes3.dex */
class PlainMailer extends Mailer {
    PlainMailer() {
    }

    @Override // org.apache.tools.ant.taskdefs.email.Mailer
    public void send() {
        try {
            MailMessage mailMessage = new MailMessage(this.host, this.port);
            mailMessage.from(this.from.toString());
            boolean z = false;
            Enumeration<EmailAddress> enumerationElements = this.replyToList.elements();
            while (enumerationElements.hasMoreElements()) {
                mailMessage.replyto(enumerationElements.nextElement().toString());
            }
            Enumeration<EmailAddress> enumerationElements2 = this.toList.elements();
            while (enumerationElements2.hasMoreElements()) {
                String string = enumerationElements2.nextElement().toString();
                try {
                    mailMessage.to(string);
                    z = true;
                } catch (IOException e) {
                    badRecipient(string, e);
                }
            }
            Enumeration<EmailAddress> enumerationElements3 = this.ccList.elements();
            while (enumerationElements3.hasMoreElements()) {
                String string2 = enumerationElements3.nextElement().toString();
                try {
                    mailMessage.cc(string2);
                    z = true;
                } catch (IOException e2) {
                    badRecipient(string2, e2);
                }
            }
            Enumeration<EmailAddress> enumerationElements4 = this.bccList.elements();
            while (enumerationElements4.hasMoreElements()) {
                String string3 = enumerationElements4.nextElement().toString();
                try {
                    mailMessage.bcc(string3);
                    z = true;
                } catch (IOException e3) {
                    badRecipient(string3, e3);
                }
            }
            if (!z) {
                throw new BuildException("Couldn't reach any recipient");
            }
            if (this.subject != null) {
                mailMessage.setSubject(this.subject);
            }
            mailMessage.setHeader("Date", getDate());
            if (this.message.getCharset() != null) {
                mailMessage.setHeader("Content-Type", this.message.getMimeType() + "; charset=\"" + this.message.getCharset() + "\"");
            } else {
                mailMessage.setHeader("Content-Type", this.message.getMimeType());
            }
            if (this.headers != null) {
                Enumeration<Header> enumerationElements5 = this.headers.elements();
                while (enumerationElements5.hasMoreElements()) {
                    Header headerNextElement = enumerationElements5.nextElement();
                    mailMessage.setHeader(headerNextElement.getName(), headerNextElement.getValue());
                }
            }
            PrintStream printStream = mailMessage.getPrintStream();
            this.message.print(printStream);
            Enumeration<File> enumerationElements6 = this.files.elements();
            while (enumerationElements6.hasMoreElements()) {
                attach(enumerationElements6.nextElement(), printStream);
            }
            mailMessage.sendAndClose();
        } catch (IOException e4) {
            throw new BuildException("IO error sending mail", e4);
        }
    }

    protected void attach(File file, PrintStream printStream) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new BuildException("File \"" + file.getName() + "\" does not exist or is not readable.");
        }
        if (this.includeFileNames) {
            printStream.println();
            String name = file.getName();
            int length = name.length();
            printStream.println(name);
            for (int i = 0; i < length; i++) {
                printStream.print('=');
            }
            printStream.println();
        }
        byte[] bArr = new byte[1024];
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 1024);
            while (true) {
                int i2 = bufferedInputStream.read(bArr);
                if (i2 == -1) {
                    return;
                } else {
                    printStream.write(bArr, 0, i2);
                }
            }
        } finally {
            fileInputStream.close();
        }
    }

    private void badRecipient(String str, IOException iOException) {
        String str2 = "Failed to send mail to " + str;
        if (shouldIgnoreInvalidRecipients()) {
            String str3 = str2 + " because of :" + iOException.getMessage();
            if (this.task != null) {
                this.task.log(str3, 1);
                return;
            } else {
                System.err.println(str3);
                return;
            }
        }
        throw new BuildException(str2, iOException);
    }
}

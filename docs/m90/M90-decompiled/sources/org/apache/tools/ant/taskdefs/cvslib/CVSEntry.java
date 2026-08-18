package org.apache.tools.ant.taskdefs.cvslib;

import java.util.Date;
import java.util.Vector;

/* JADX INFO: loaded from: classes3.dex */
public class CVSEntry {
    private String author;
    private final String comment;
    private Date date;
    private final Vector files = new Vector();

    public CVSEntry(Date date, String str, String str2) {
        this.date = date;
        this.author = str;
        this.comment = str2;
    }

    public void addFile(String str, String str2) {
        this.files.addElement(new RCSFile(str, str2));
    }

    public void addFile(String str, String str2, String str3) {
        this.files.addElement(new RCSFile(str, str2, str3));
    }

    public Date getDate() {
        return this.date;
    }

    public void setAuthor(String str) {
        this.author = str;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getComment() {
        return this.comment;
    }

    public Vector getFiles() {
        return this.files;
    }

    public String toString() {
        return getAuthor() + "\n" + getDate() + "\n" + getFiles() + "\n" + getComment();
    }
}

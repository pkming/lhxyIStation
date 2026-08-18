package org.apache.tools.ant;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.tools.ant.taskdefs.condition.Os;

/* JADX INFO: loaded from: classes3.dex */
public class PathTokenizer {
    private boolean dosStyleFilesystem;
    private String lookahead = null;
    private boolean onNetWare;
    private StringTokenizer tokenizer;

    public PathTokenizer(String str) {
        boolean zIsFamily = Os.isFamily(Os.FAMILY_NETWARE);
        this.onNetWare = zIsFamily;
        if (zIsFamily) {
            this.tokenizer = new StringTokenizer(str, ":;", true);
        } else {
            this.tokenizer = new StringTokenizer(str, ":;", false);
        }
        this.dosStyleFilesystem = File.pathSeparatorChar == ';';
    }

    public boolean hasMoreTokens() {
        if (this.lookahead != null) {
            return true;
        }
        return this.tokenizer.hasMoreTokens();
    }

    public String nextToken() throws NoSuchElementException {
        String strTrim = this.lookahead;
        if (strTrim != null) {
            this.lookahead = null;
        } else {
            strTrim = this.tokenizer.nextToken().trim();
        }
        if (!this.onNetWare) {
            if (strTrim.length() != 1 || !Character.isLetter(strTrim.charAt(0)) || !this.dosStyleFilesystem || !this.tokenizer.hasMoreTokens()) {
                return strTrim;
            }
            String strTrim2 = this.tokenizer.nextToken().trim();
            if (strTrim2.startsWith("\\") || strTrim2.startsWith("/")) {
                return strTrim + ":" + strTrim2;
            }
            this.lookahead = strTrim2;
            return strTrim;
        }
        if (strTrim.equals(File.pathSeparator) || strTrim.equals(":")) {
            strTrim = this.tokenizer.nextToken().trim();
        }
        if (!this.tokenizer.hasMoreTokens()) {
            return strTrim;
        }
        String strTrim3 = this.tokenizer.nextToken().trim();
        if (strTrim3.equals(File.pathSeparator)) {
            return strTrim;
        }
        if (strTrim3.equals(":")) {
            if (strTrim.startsWith("/") || strTrim.startsWith("\\") || strTrim.startsWith(".") || strTrim.startsWith("..")) {
                return strTrim;
            }
            String strTrim4 = this.tokenizer.nextToken().trim();
            if (!strTrim4.equals(File.pathSeparator)) {
                return strTrim + ":" + strTrim4;
            }
            String str = strTrim + ":";
            this.lookahead = strTrim4;
            return str;
        }
        this.lookahead = strTrim3;
        return strTrim;
    }
}

package org.apache.tools.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes3.dex */
public class SmtpResponseReader {
    protected BufferedReader reader;
    private StringBuffer result = new StringBuffer();

    public SmtpResponseReader(InputStream inputStream) {
        this.reader = null;
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String getResponse() throws IOException {
        this.result.setLength(0);
        String line = this.reader.readLine();
        if (line != null && line.length() >= 3) {
            this.result.append(line.substring(0, 3));
            this.result.append(" ");
        }
        while (line != null) {
            append(line);
            if (!hasMoreLines(line)) {
                break;
            }
            line = this.reader.readLine();
        }
        return this.result.toString().trim();
    }

    public void close() throws IOException {
        this.reader.close();
    }

    protected boolean hasMoreLines(String str) {
        return str.length() > 3 && str.charAt(3) == '-';
    }

    private void append(String str) {
        if (str.length() > 4) {
            this.result.append(str.substring(4));
            this.result.append(" ");
        }
    }
}

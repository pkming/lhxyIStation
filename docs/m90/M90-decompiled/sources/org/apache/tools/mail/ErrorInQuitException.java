package org.apache.tools.mail;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class ErrorInQuitException extends IOException {
    public ErrorInQuitException(IOException iOException) {
        super(iOException.getMessage());
    }
}

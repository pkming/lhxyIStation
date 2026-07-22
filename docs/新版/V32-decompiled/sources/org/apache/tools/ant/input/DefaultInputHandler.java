package org.apache.tools.ant.input;

import java.io.InputStream;
import org.apache.tools.ant.util.KeepAliveInputStream;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultInputHandler implements InputHandler {
    /* JADX WARN: Code restructure failed: missing block: B:10:0x0030, code lost:
    
        r7 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0036, code lost:
    
        throw new org.apache.tools.ant.BuildException("Failed to close input.", r7);
     */
    @Override // org.apache.tools.ant.input.InputHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void handleInput(org.apache.tools.ant.input.InputRequest r7) throws java.lang.Throwable {
        /*
            r6 = this;
            java.lang.String r0 = "Failed to close input."
            java.lang.String r1 = r6.getPrompt(r7)
            r2 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L43
            java.io.InputStreamReader r4 = new java.io.InputStreamReader     // Catch: java.lang.Throwable -> L43
            java.io.InputStream r5 = r6.getInputStream()     // Catch: java.lang.Throwable -> L43
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L43
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L43
        L15:
            java.io.PrintStream r2 = java.lang.System.err     // Catch: java.lang.Throwable -> L40
            r2.println(r1)     // Catch: java.lang.Throwable -> L40
            java.io.PrintStream r2 = java.lang.System.err     // Catch: java.lang.Throwable -> L40
            r2.flush()     // Catch: java.lang.Throwable -> L40
            java.lang.String r2 = r3.readLine()     // Catch: java.io.IOException -> L37 java.lang.Throwable -> L40
            r7.setInput(r2)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> L40
            boolean r2 = r7.isInputValid()     // Catch: java.lang.Throwable -> L40
            if (r2 == 0) goto L15
            r3.close()     // Catch: java.io.IOException -> L30
            return
        L30:
            r7 = move-exception
            org.apache.tools.ant.BuildException r1 = new org.apache.tools.ant.BuildException
            r1.<init>(r0, r7)
            throw r1
        L37:
            r7 = move-exception
            org.apache.tools.ant.BuildException r1 = new org.apache.tools.ant.BuildException     // Catch: java.lang.Throwable -> L40
            java.lang.String r2 = "Failed to read input from Console."
            r1.<init>(r2, r7)     // Catch: java.lang.Throwable -> L40
            throw r1     // Catch: java.lang.Throwable -> L40
        L40:
            r7 = move-exception
            r2 = r3
            goto L44
        L43:
            r7 = move-exception
        L44:
            if (r2 == 0) goto L51
            r2.close()     // Catch: java.io.IOException -> L4a
            goto L51
        L4a:
            r7 = move-exception
            org.apache.tools.ant.BuildException r1 = new org.apache.tools.ant.BuildException
            r1.<init>(r0, r7)
            throw r1
        L51:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.input.DefaultInputHandler.handleInput(org.apache.tools.ant.input.InputRequest):void");
    }

    protected String getPrompt(InputRequest inputRequest) {
        String prompt = inputRequest.getPrompt();
        String defaultValue = inputRequest.getDefaultValue();
        if (!(inputRequest instanceof MultipleChoiceInputRequest)) {
            return defaultValue != null ? prompt + " [" + defaultValue + "]" : prompt;
        }
        StringBuilder sbAppend = new StringBuilder(prompt).append(" (");
        boolean z = true;
        for (String str : ((MultipleChoiceInputRequest) inputRequest).getChoices()) {
            if (!z) {
                sbAppend.append(", ");
            }
            if (str.equals(defaultValue)) {
                sbAppend.append('[');
            }
            sbAppend.append(str);
            if (str.equals(defaultValue)) {
                sbAppend.append(']');
            }
            z = false;
        }
        sbAppend.append(")");
        return sbAppend.toString();
    }

    protected InputStream getInputStream() {
        return KeepAliveInputStream.wrapSystemIn();
    }
}

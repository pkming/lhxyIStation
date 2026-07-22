package org.apache.tools.ant.taskdefs;

import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import java.io.File;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class CVSPass extends Task {
    private File passFile;
    private String cvsRoot = null;
    private String password = null;
    private final char[] shifts = {0, 1, 2, 3, 4, 5, 6, 7, '\b', '\t', '\n', 11, '\f', '\r', 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 'r', 'x', '5', 'O', '`', DateFormat.MINUTE, 'H', 'l', 'F', '@', DateFormat.STANDALONE_MONTH, 'C', 't', 'J', 'D', 'W', 'o', '4', 'K', 'w', '1', '\"', 'R', 'Q', '_', DateFormat.CAPITAL_AM_PM, 'p', 'V', 'v', 'n', DateFormat.TIME_ZONE, 'i', ')', '9', 'S', '+', '.', 'f', '(', 'Y', '&', 'g', '-', '2', '*', '{', '[', '#', '}', '7', '6', 'B', '|', '~', PhoneNumberUtils.WAIT, '/', '\\', 'G', 's', PhoneNumberUtils.WILD, 'X', DateFormat.HOUR_OF_DAY, 'j', '8', '$', DateFormat.YEAR, 'u', DateFormat.HOUR, 'e', DateFormat.DATE, DateFormat.DAY, 'I', 'c', '?', '^', ']', DateFormat.QUOTE, '%', '=', '0', ':', 'q', ' ', 'Z', PhoneNumberUtils.PAUSE, 'b', '<', '3', '!', DateFormat.AM_PM, '>', DateFormat.MONTH, 'T', 'P', 'U', 223, 225, 216, 187, 166, 229, 189, 222, 188, 141, 249, 148, 200, 184, 136, 248, 190, 199, 170, 181, 204, 138, 232, 218, 183, 255, 234, 220, 247, 213, 203, 226, 193, 174, 172, 228, 252, 217, 201, 131, 230, 197, 211, 145, 238, 161, 179, 160, 212, 207, 221, 254, 173, 202, 146, 224, 151, 140, 196, 205, 130, 135, 133, 143, 246, 192, 159, 244, 239, 185, 168, 215, 144, 139, 165, 180, 157, 147, 186, 214, 176, 227, 231, 219, 169, 175, 156, 206, 198, 129, 164, 150, 210, 154, 177, 134, 127, 182, 128, 158, 208, 162, 132, 167, 209, 149, 241, 153, 251, 237, 236, 171, 195, 243, 233, 253, 240, 194, 250, 191, 155, 142, 137, 245, 235, 163, 242, 178, 152};

    public CVSPass() {
        this.passFile = null;
        this.passFile = new File(System.getProperty("cygwin.user.home", System.getProperty("user.home")) + File.separatorChar + ".cvspass");
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x00fc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // org.apache.tools.ant.Task
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void execute() throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 275
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.CVSPass.execute():void");
    }

    private final String mangle(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            stringBuffer.append(this.shifts[str.charAt(i)]);
        }
        return stringBuffer.toString();
    }

    public void setCvsroot(String str) {
        this.cvsRoot = str;
    }

    public void setPassfile(File file) {
        this.passFile = file;
    }

    public void setPassword(String str) {
        this.password = str;
    }
}

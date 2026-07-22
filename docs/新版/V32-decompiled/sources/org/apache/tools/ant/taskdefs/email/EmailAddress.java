package org.apache.tools.ant.taskdefs.email;

/* JADX INFO: loaded from: classes3.dex */
public class EmailAddress {
    private String address;
    private String name;

    public EmailAddress() {
    }

    public EmailAddress(String str) {
        int length = str.length();
        if (length > 9 && ((str.charAt(0) == '<' || str.charAt(1) == '<') && (str.charAt(length - 1) == '>' || str.charAt(length - 2) == '>'))) {
            this.address = trim(str, true);
            return;
        }
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i < length) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '(') {
                i4++;
                i = i5 != 0 ? i + 1 : i;
            } else {
                if (cCharAt == ')') {
                    i4--;
                    if (i2 == 0) {
                        i5 = i + 1;
                        i3 = i;
                    }
                } else if (i4 == 0 && cCharAt == '<') {
                    i3 = i5 == 0 ? i : i3;
                    i5 = i + 1;
                } else if (i4 == 0 && cCharAt == '>') {
                    i6 = i != length + (-1) ? i + 1 : i6;
                }
            }
            i2 = i;
        }
        i2 = i2 == 0 ? length : i2;
        i3 = i3 == 0 ? length : i3;
        this.address = trim(str.substring(i5, i2), true);
        String strTrim = trim(str.substring(i6, i3), false);
        this.name = strTrim;
        if (strTrim.length() + this.address.length() > length) {
            this.name = null;
        }
    }

    private String trim(String str, boolean z) {
        boolean z2;
        boolean z3;
        int length = str.length();
        int i = 0;
        do {
            int i2 = length - 1;
            z2 = true;
            if (str.charAt(i2) == ')' || ((str.charAt(i2) == '>' && z) || ((str.charAt(i2) == '\"' && str.charAt(length - 2) != '\\') || str.charAt(i2) <= ' '))) {
                length--;
                z3 = true;
            } else {
                z3 = false;
            }
            if (str.charAt(i) == '(' || ((str.charAt(i) == '<' && z) || str.charAt(i) == '\"' || str.charAt(i) <= ' ')) {
                i++;
            } else {
                z2 = z3;
            }
        } while (z2);
        return str.substring(i, length);
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public String toString() {
        if (this.name == null) {
            return this.address;
        }
        return this.name + " <" + this.address + ">";
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }
}

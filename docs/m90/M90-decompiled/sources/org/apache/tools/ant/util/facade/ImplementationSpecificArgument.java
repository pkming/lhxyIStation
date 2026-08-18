package org.apache.tools.ant.util.facade;

import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class ImplementationSpecificArgument extends Commandline.Argument {
    private String impl;

    public void setImplementation(String str) {
        this.impl = str;
    }

    public final String[] getParts(String str) {
        String str2 = this.impl;
        return (str2 == null || str2.equals(str)) ? super.getParts() : new String[0];
    }
}

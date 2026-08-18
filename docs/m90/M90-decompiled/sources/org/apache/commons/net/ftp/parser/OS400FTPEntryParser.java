package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

/* JADX INFO: loaded from: classes3.dex */
public class OS400FTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "yy/MM/dd HH:mm:ss";
    private static final String REGEX = "(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\*\\S+)\\s+(\\S+/?)\\s*";

    public OS400FTPEntryParser() {
        this(null);
    }

    public OS400FTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        configure(fTPClientConfig);
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParser
    public FTPFile parseFTPEntry(String str) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(str);
        if (!matches(str)) {
            return null;
        }
        String strGroup = group(1);
        String strGroup2 = group(2);
        int i = 3;
        String str2 = group(3) + " " + group(4);
        String strGroup3 = group(5);
        String strGroup4 = group(6);
        try {
            fTPFile.setTimestamp(super.parseTimestamp(str2));
        } catch (ParseException unused) {
        }
        if (strGroup3.equalsIgnoreCase("*STMF")) {
            i = 0;
        } else if (strGroup3.equalsIgnoreCase("*DIR")) {
            i = 1;
        }
        fTPFile.setType(i);
        fTPFile.setUser(strGroup);
        try {
            fTPFile.setSize(Long.parseLong(strGroup2));
        } catch (NumberFormatException unused2) {
        }
        if (strGroup4.endsWith("/")) {
            strGroup4 = strGroup4.substring(0, strGroup4.length() - 1);
        }
        int iLastIndexOf = strGroup4.lastIndexOf(47);
        if (iLastIndexOf > -1) {
            strGroup4 = strGroup4.substring(iLastIndexOf + 1);
        }
        fTPFile.setName(strGroup4);
        return fTPFile;
    }

    @Override // org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(FTPClientConfig.SYST_OS400, DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

/* JADX INFO: loaded from: classes3.dex */
public class NetwareFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "MMM dd yyyy";
    private static final String DEFAULT_RECENT_DATE_FORMAT = "MMM dd HH:mm";
    private static final String REGEX = "(d|-){1}\\s+\\[(.*)\\]\\s+(\\S+)\\s+(\\d+)\\s+(\\S+\\s+\\S+\\s+((\\d+:\\d+)|(\\d{4})))\\s+(.*)";

    public NetwareFTPEntryParser() {
        this(null);
    }

    public NetwareFTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        configure(fTPClientConfig);
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParser
    public FTPFile parseFTPEntry(String str) {
        FTPFile fTPFile = new FTPFile();
        if (!matches(str)) {
            return null;
        }
        String strGroup = group(1);
        String strGroup2 = group(2);
        String strGroup3 = group(3);
        String strGroup4 = group(4);
        String strGroup5 = group(5);
        String strGroup6 = group(9);
        try {
            fTPFile.setTimestamp(super.parseTimestamp(strGroup5));
        } catch (ParseException unused) {
        }
        if (strGroup.trim().equals("d")) {
            fTPFile.setType(1);
        } else {
            fTPFile.setType(0);
        }
        fTPFile.setUser(strGroup3);
        fTPFile.setName(strGroup6.trim());
        fTPFile.setSize(Long.parseLong(strGroup4.trim()));
        if (strGroup2.indexOf("R") != -1) {
            fTPFile.setPermission(0, 0, true);
        }
        if (strGroup2.indexOf("W") != -1) {
            fTPFile.setPermission(0, 1, true);
        }
        return fTPFile;
    }

    @Override // org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(FTPClientConfig.SYST_NETWARE, DEFAULT_DATE_FORMAT, DEFAULT_RECENT_DATE_FORMAT, null, null, null);
    }
}

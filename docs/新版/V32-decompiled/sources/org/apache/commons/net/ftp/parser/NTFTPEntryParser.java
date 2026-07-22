package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

/* JADX INFO: loaded from: classes3.dex */
public class NTFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "MM-dd-yy hh:mma";
    private static final String DEFAULT_DATE_FORMAT2 = "MM-dd-yy kk:mm";
    private static final String REGEX = "(\\S+)\\s+(\\S+)\\s+(?:(<DIR>)|([0-9]+))\\s+(\\S.*)";
    private final FTPTimestampParser timestampParser;

    public NTFTPEntryParser() {
        this(null);
    }

    public NTFTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        configure(fTPClientConfig);
        FTPClientConfig fTPClientConfig2 = new FTPClientConfig(FTPClientConfig.SYST_NT, DEFAULT_DATE_FORMAT2, null, null, null, null);
        fTPClientConfig2.setDefaultDateFormatStr(DEFAULT_DATE_FORMAT2);
        FTPTimestampParserImpl fTPTimestampParserImpl = new FTPTimestampParserImpl();
        this.timestampParser = fTPTimestampParserImpl;
        fTPTimestampParserImpl.configure(fTPClientConfig2);
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParser
    public FTPFile parseFTPEntry(String str) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(str);
        if (matches(str)) {
            String str2 = group(1) + " " + group(2);
            String strGroup = group(3);
            String strGroup2 = group(4);
            String strGroup3 = group(5);
            try {
                try {
                    fTPFile.setTimestamp(super.parseTimestamp(str2));
                } catch (ParseException unused) {
                }
            } catch (ParseException unused2) {
                fTPFile.setTimestamp(this.timestampParser.parseTimestamp(str2));
            }
            if (strGroup3 != null && !strGroup3.equals(".") && !strGroup3.equals("..")) {
                fTPFile.setName(strGroup3);
                if ("<DIR>".equals(strGroup)) {
                    fTPFile.setType(1);
                    fTPFile.setSize(0L);
                } else {
                    fTPFile.setType(0);
                    if (strGroup2 != null) {
                        fTPFile.setSize(Long.parseLong(strGroup2));
                    }
                }
                return fTPFile;
            }
        }
        return null;
    }

    @Override // org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl
    public FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(FTPClientConfig.SYST_NT, DEFAULT_DATE_FORMAT, null, null, null, null);
    }
}

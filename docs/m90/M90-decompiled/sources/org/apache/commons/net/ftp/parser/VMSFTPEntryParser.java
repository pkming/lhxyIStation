package org.apache.commons.net.ftp.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.StringTokenizer;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;

/* JADX INFO: loaded from: classes3.dex */
public class VMSFTPEntryParser extends ConfigurableFTPFileEntryParserImpl {
    private static final String DEFAULT_DATE_FORMAT = "d-MMM-yyyy HH:mm:ss";
    private static final String REGEX = "(.*;[0-9]+)\\s*(\\d+)/\\d+\\s*(\\S+)\\s+(\\S+)\\s+\\[(([0-9$A-Za-z_]+)|([0-9$A-Za-z_]+),([0-9$a-zA-Z_]+))\\]?\\s*\\([a-zA-Z]*,([a-zA-Z]*),([a-zA-Z]*),([a-zA-Z]*)\\)";

    protected boolean isVersioning() {
        return false;
    }

    public VMSFTPEntryParser() {
        this(null);
    }

    public VMSFTPEntryParser(FTPClientConfig fTPClientConfig) {
        super(REGEX);
        configure(fTPClientConfig);
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParser
    public FTPFile parseFTPEntry(String str) {
        String strNextToken;
        String strNextToken2 = null;
        if (!matches(str)) {
            return null;
        }
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(str);
        String strGroup = group(1);
        String strGroup2 = group(2);
        String str2 = group(3) + " " + group(4);
        String strGroup3 = group(5);
        String[] strArr = {group(9), group(10), group(11)};
        try {
            fTPFile.setTimestamp(super.parseTimestamp(str2));
        } catch (ParseException unused) {
        }
        StringTokenizer stringTokenizer = new StringTokenizer(strGroup3, ",");
        int iCountTokens = stringTokenizer.countTokens();
        if (iCountTokens == 1) {
            strNextToken = stringTokenizer.nextToken();
        } else if (iCountTokens != 2) {
            strNextToken = null;
        } else {
            strNextToken2 = stringTokenizer.nextToken();
            strNextToken = stringTokenizer.nextToken();
        }
        if (strGroup.lastIndexOf(".DIR") != -1) {
            fTPFile.setType(1);
        } else {
            fTPFile.setType(0);
        }
        if (isVersioning()) {
            fTPFile.setName(strGroup);
        } else {
            fTPFile.setName(strGroup.substring(0, strGroup.lastIndexOf(";")));
        }
        fTPFile.setSize(Long.parseLong(strGroup2) * 512);
        fTPFile.setGroup(strNextToken2);
        fTPFile.setUser(strNextToken);
        for (int i = 0; i < 3; i++) {
            String str3 = strArr[i];
            fTPFile.setPermission(i, 0, str3.indexOf(82) >= 0);
            fTPFile.setPermission(i, 1, str3.indexOf(87) >= 0);
            fTPFile.setPermission(i, 2, str3.indexOf(69) >= 0);
        }
        return fTPFile;
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParserImpl, org.apache.commons.net.ftp.FTPFileEntryParser
    public String readNextEntry(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            if (line.startsWith("Directory") || line.startsWith("Total")) {
                line = bufferedReader.readLine();
            } else {
                sb.append(line);
                if (line.trim().endsWith(")")) {
                    break;
                }
                line = bufferedReader.readLine();
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    @Override // org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(FTPClientConfig.SYST_VMS, DEFAULT_DATE_FORMAT, null, null, null, null);
    }

    @Deprecated
    public FTPFile[] parseFileList(InputStream inputStream) throws IOException {
        FTPListParseEngine fTPListParseEngine = new FTPListParseEngine(this);
        fTPListParseEngine.readServerList(inputStream, null);
        return fTPListParseEngine.getFiles();
    }
}

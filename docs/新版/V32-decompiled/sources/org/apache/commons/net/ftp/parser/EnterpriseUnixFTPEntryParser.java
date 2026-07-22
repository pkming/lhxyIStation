package org.apache.commons.net.ftp.parser;

import java.util.Calendar;
import org.apache.commons.net.ftp.FTPFile;

/* JADX INFO: loaded from: classes3.dex */
public class EnterpriseUnixFTPEntryParser extends RegexFTPFileEntryParserImpl {
    private static final String MONTHS = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
    private static final String REGEX = "(([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z])([\\-]|[A-Z]))(\\S*)\\s*(\\S+)\\s*(\\S*)\\s*(\\d*)\\s*(\\d*)\\s*(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*((?:[012]\\d*)|(?:3[01]))\\s*((\\d\\d\\d\\d)|((?:[01]\\d)|(?:2[0123])):([012345]\\d))\\s(\\S*)(\\s*.*)";

    public EnterpriseUnixFTPEntryParser() {
        super(REGEX);
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParser
    public FTPFile parseFTPEntry(String str) {
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(str);
        if (!matches(str)) {
            return null;
        }
        String strGroup = group(14);
        String strGroup2 = group(15);
        String strGroup3 = group(16);
        String strGroup4 = group(17);
        String strGroup5 = group(18);
        String strGroup6 = group(20);
        String strGroup7 = group(21);
        String strGroup8 = group(22);
        String strGroup9 = group(23);
        fTPFile.setType(0);
        fTPFile.setUser(strGroup);
        fTPFile.setGroup(strGroup2);
        try {
            fTPFile.setSize(Long.parseLong(strGroup3));
        } catch (NumberFormatException unused) {
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(11, 0);
        try {
            int iIndexOf = MONTHS.indexOf(strGroup4) / 4;
            if (strGroup6 != null) {
                calendar.set(1, Integer.parseInt(strGroup6));
            } else {
                int i = calendar.get(1);
                if (calendar.get(2) < iIndexOf) {
                    i--;
                }
                calendar.set(1, i);
                calendar.set(11, Integer.parseInt(strGroup7));
                calendar.set(12, Integer.parseInt(strGroup8));
            }
            calendar.set(2, iIndexOf);
            calendar.set(5, Integer.parseInt(strGroup5));
            fTPFile.setTimestamp(calendar);
        } catch (NumberFormatException unused2) {
        }
        fTPFile.setName(strGroup9);
        return fTPFile;
    }
}

package org.apache.commons.net.ftp.parser;

import android.net.wifi.WifiConfiguration;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParserImpl;

/* JADX INFO: loaded from: classes3.dex */
public class MLSxEntryParser extends FTPFileEntryParserImpl {
    private static final MLSxEntryParser PARSER = new MLSxEntryParser();
    private static final HashMap<String, Integer> TYPE_TO_INT;
    private static int[] UNIX_GROUPS;
    private static int[][] UNIX_PERMS;

    static {
        HashMap<String, Integer> map = new HashMap<>();
        TYPE_TO_INT = map;
        map.put("file", 0);
        map.put("cdir", 1);
        map.put("pdir", 1);
        map.put("dir", 1);
        UNIX_GROUPS = new int[]{0, 1, 2};
        UNIX_PERMS = new int[][]{new int[0], new int[]{2}, new int[]{1}, new int[]{2, 1}, new int[]{0}, new int[]{0, 2}, new int[]{0, 1}, new int[]{0, 1, 2}};
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParser
    public FTPFile parseFTPEntry(String str) {
        SimpleDateFormat simpleDateFormat;
        int i = 2;
        String[] strArrSplit = str.split(" ", 2);
        if (strArrSplit.length != 2) {
            return null;
        }
        FTPFile fTPFile = new FTPFile();
        fTPFile.setRawListing(str);
        fTPFile.setName(strArrSplit[1]);
        String[] strArrSplit2 = strArrSplit[0].split(";");
        boolean zContains = strArrSplit[0].toLowerCase(Locale.ENGLISH).contains("unix.mode=");
        int length = strArrSplit2.length;
        int i2 = 0;
        while (i2 < length) {
            String[] strArrSplit3 = strArrSplit2[i2].split("=");
            if (strArrSplit3.length == i) {
                String lowerCase = strArrSplit3[0].toLowerCase(Locale.ENGLISH);
                String str2 = strArrSplit3[1];
                String lowerCase2 = str2.toLowerCase(Locale.ENGLISH);
                if ("size".equals(lowerCase) || "sizd".equals(lowerCase)) {
                    fTPFile.setSize(Long.parseLong(str2));
                } else if ("modify".equals(lowerCase)) {
                    if (str2.contains(".")) {
                        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
                    } else {
                        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    }
                    TimeZone timeZone = TimeZone.getTimeZone("GMT");
                    simpleDateFormat.setTimeZone(timeZone);
                    GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
                    try {
                        gregorianCalendar.setTime(simpleDateFormat.parse(str2));
                    } catch (ParseException unused) {
                    }
                    fTPFile.setTimestamp(gregorianCalendar);
                } else if ("type".equals(lowerCase)) {
                    Integer num = TYPE_TO_INT.get(lowerCase2);
                    if (num == null) {
                        fTPFile.setType(3);
                    } else {
                        fTPFile.setType(num.intValue());
                    }
                } else if (lowerCase.startsWith("unix.")) {
                    String lowerCase3 = lowerCase.substring(5).toLowerCase(Locale.ENGLISH);
                    if (WifiConfiguration.GroupCipher.varName.equals(lowerCase3)) {
                        fTPFile.setGroup(str2);
                    } else if ("owner".equals(lowerCase3)) {
                        fTPFile.setUser(str2);
                    } else if ("mode".equals(lowerCase3)) {
                        int length2 = str2.length() - 3;
                        for (int i3 = 0; i3 < 3; i3++) {
                            int iCharAt = str2.charAt(length2 + i3) - '0';
                            if (iCharAt >= 0 && iCharAt <= 7) {
                                for (int i4 : UNIX_PERMS[iCharAt]) {
                                    fTPFile.setPermission(UNIX_GROUPS[i3], i4, true);
                                }
                            }
                        }
                    }
                } else if (!zContains && "perm".equals(lowerCase)) {
                    doUnixPerms(fTPFile, lowerCase2);
                }
            }
            i2++;
            i = 2;
        }
        return fTPFile;
    }

    private void doUnixPerms(FTPFile fTPFile, String str) {
        for (char c : str.toCharArray()) {
            if (c == 'a') {
                fTPFile.setPermission(0, 1, true);
            } else if (c == 'p') {
                fTPFile.setPermission(0, 1, true);
            } else if (c == 'r') {
                fTPFile.setPermission(0, 0, true);
            } else if (c == 'w') {
                fTPFile.setPermission(0, 1, true);
            } else if (c == 'l') {
                fTPFile.setPermission(0, 2, true);
            } else if (c != 'm') {
                switch (c) {
                    case 'c':
                        fTPFile.setPermission(0, 1, true);
                        break;
                    case 'd':
                        fTPFile.setPermission(0, 1, true);
                        break;
                    case 'e':
                        fTPFile.setPermission(0, 0, true);
                        break;
                }
            } else {
                fTPFile.setPermission(0, 1, true);
            }
        }
    }

    public static FTPFile parseEntry(String str) {
        return PARSER.parseFTPEntry(str);
    }

    public static MLSxEntryParser getInstance() {
        return PARSER;
    }
}

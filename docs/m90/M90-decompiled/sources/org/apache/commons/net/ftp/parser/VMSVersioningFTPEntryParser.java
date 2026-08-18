package org.apache.commons.net.ftp.parser;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.net.ftp.FTPClientConfig;

/* JADX INFO: loaded from: classes3.dex */
public class VMSVersioningFTPEntryParser extends VMSFTPEntryParser {
    private static final String PRE_PARSE_REGEX = "(.*);([0-9]+)\\s*.*";
    private final Pattern _preparse_pattern_;

    @Override // org.apache.commons.net.ftp.parser.VMSFTPEntryParser
    protected boolean isVersioning() {
        return true;
    }

    public VMSVersioningFTPEntryParser() {
        this(null);
    }

    public VMSVersioningFTPEntryParser(FTPClientConfig fTPClientConfig) {
        configure(fTPClientConfig);
        try {
            this._preparse_pattern_ = Pattern.compile(PRE_PARSE_REGEX);
        } catch (PatternSyntaxException unused) {
            throw new IllegalArgumentException("Unparseable regex supplied:  (.*);([0-9]+)\\s*.*");
        }
    }

    @Override // org.apache.commons.net.ftp.FTPFileEntryParserImpl, org.apache.commons.net.ftp.FTPFileEntryParser
    public List<String> preParse(List<String> list) {
        HashMap map = new HashMap();
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            Matcher matcher = this._preparse_pattern_.matcher(listIterator.next().trim());
            if (matcher.matches()) {
                MatchResult matchResult = matcher.toMatchResult();
                String strGroup = matchResult.group(1);
                Integer numValueOf = Integer.valueOf(matchResult.group(2));
                Integer num = (Integer) map.get(strGroup);
                if (num != null && numValueOf.intValue() < num.intValue()) {
                    listIterator.remove();
                } else {
                    map.put(strGroup, numValueOf);
                }
            }
        }
        while (listIterator.hasPrevious()) {
            Matcher matcher2 = this._preparse_pattern_.matcher(listIterator.previous().trim());
            if (matcher2.matches()) {
                MatchResult matchResult2 = matcher2.toMatchResult();
                String strGroup2 = matchResult2.group(1);
                Integer numValueOf2 = Integer.valueOf(matchResult2.group(2));
                Integer num2 = (Integer) map.get(strGroup2);
                if (num2 != null && numValueOf2.intValue() < num2.intValue()) {
                    listIterator.remove();
                }
            }
        }
        return list;
    }
}

package org.apache.commons.net.ftp.parser;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTPClientConfig;

/* JADX INFO: loaded from: classes3.dex */
public class FTPTimestampParserImpl implements FTPTimestampParser, Configurable {
    private SimpleDateFormat defaultDateFormat;
    private boolean lenientFutureDates = false;
    private SimpleDateFormat recentDateFormat;

    public FTPTimestampParserImpl() {
        setDefaultDateFormat(FTPTimestampParser.DEFAULT_SDF);
        setRecentDateFormat(FTPTimestampParser.DEFAULT_RECENT_SDF);
    }

    @Override // org.apache.commons.net.ftp.parser.FTPTimestampParser
    public Calendar parseTimestamp(String str) throws ParseException {
        return parseTimestamp(str, Calendar.getInstance());
    }

    public Calendar parseTimestamp(String str, Calendar calendar) throws ParseException {
        Calendar calendar2 = (Calendar) calendar.clone();
        calendar2.setTimeZone(getServerTimeZone());
        if (this.recentDateFormat != null) {
            Calendar calendar3 = (Calendar) calendar.clone();
            calendar3.setTimeZone(getServerTimeZone());
            if (this.lenientFutureDates) {
                calendar3.add(5, 1);
            }
            String str2 = str + " " + Integer.toString(calendar3.get(1));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.recentDateFormat.toPattern() + " yyyy", this.recentDateFormat.getDateFormatSymbols());
            simpleDateFormat.setLenient(false);
            simpleDateFormat.setTimeZone(this.recentDateFormat.getTimeZone());
            ParsePosition parsePosition = new ParsePosition(0);
            Date date = simpleDateFormat.parse(str2, parsePosition);
            if (date != null && parsePosition.getIndex() == str2.length()) {
                calendar2.setTime(date);
                if (calendar2.after(calendar3)) {
                    calendar2.add(1, -1);
                }
                return calendar2;
            }
        }
        ParsePosition parsePosition2 = new ParsePosition(0);
        Date date2 = this.defaultDateFormat.parse(str, parsePosition2);
        if (date2 != null && parsePosition2.getIndex() == str.length()) {
            calendar2.setTime(date2);
            return calendar2;
        }
        throw new ParseException("Timestamp '" + str + "' could not be parsed using a server time of " + calendar.getTime().toString(), parsePosition2.getErrorIndex());
    }

    public SimpleDateFormat getDefaultDateFormat() {
        return this.defaultDateFormat;
    }

    public String getDefaultDateFormatString() {
        return this.defaultDateFormat.toPattern();
    }

    private void setDefaultDateFormat(String str) {
        if (str != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
            this.defaultDateFormat = simpleDateFormat;
            simpleDateFormat.setLenient(false);
        }
    }

    public SimpleDateFormat getRecentDateFormat() {
        return this.recentDateFormat;
    }

    public String getRecentDateFormatString() {
        return this.recentDateFormat.toPattern();
    }

    private void setRecentDateFormat(String str) {
        if (str != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
            this.recentDateFormat = simpleDateFormat;
            simpleDateFormat.setLenient(false);
        }
    }

    public String[] getShortMonths() {
        return this.defaultDateFormat.getDateFormatSymbols().getShortMonths();
    }

    public TimeZone getServerTimeZone() {
        return this.defaultDateFormat.getTimeZone();
    }

    private void setServerTimeZone(String str) {
        TimeZone timeZone = TimeZone.getDefault();
        if (str != null) {
            timeZone = TimeZone.getTimeZone(str);
        }
        this.defaultDateFormat.setTimeZone(timeZone);
        SimpleDateFormat simpleDateFormat = this.recentDateFormat;
        if (simpleDateFormat != null) {
            simpleDateFormat.setTimeZone(timeZone);
        }
    }

    @Override // org.apache.commons.net.ftp.Configurable
    public void configure(FTPClientConfig fTPClientConfig) {
        DateFormatSymbols dateFormatSymbolsLookupDateFormatSymbols;
        String serverLanguageCode = fTPClientConfig.getServerLanguageCode();
        String shortMonthNames = fTPClientConfig.getShortMonthNames();
        if (shortMonthNames != null) {
            dateFormatSymbolsLookupDateFormatSymbols = FTPClientConfig.getDateFormatSymbols(shortMonthNames);
        } else if (serverLanguageCode != null) {
            dateFormatSymbolsLookupDateFormatSymbols = FTPClientConfig.lookupDateFormatSymbols(serverLanguageCode);
        } else {
            dateFormatSymbolsLookupDateFormatSymbols = FTPClientConfig.lookupDateFormatSymbols("en");
        }
        String recentDateFormatStr = fTPClientConfig.getRecentDateFormatStr();
        if (recentDateFormatStr == null) {
            this.recentDateFormat = null;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(recentDateFormatStr, dateFormatSymbolsLookupDateFormatSymbols);
            this.recentDateFormat = simpleDateFormat;
            simpleDateFormat.setLenient(false);
        }
        String defaultDateFormatStr = fTPClientConfig.getDefaultDateFormatStr();
        if (defaultDateFormatStr == null) {
            throw new IllegalArgumentException("defaultFormatString cannot be null");
        }
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(defaultDateFormatStr, dateFormatSymbolsLookupDateFormatSymbols);
        this.defaultDateFormat = simpleDateFormat2;
        simpleDateFormat2.setLenient(false);
        setServerTimeZone(fTPClientConfig.getServerTimeZoneId());
        this.lenientFutureDates = fTPClientConfig.isLenientFutureDates();
    }

    boolean isLenientFutureDates() {
        return this.lenientFutureDates;
    }

    void setLenientFutureDates(boolean z) {
        this.lenientFutureDates = z;
    }
}

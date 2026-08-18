package org.apache.commons.net.ftp.parser;

import java.text.ParseException;
import java.util.Calendar;

/* JADX INFO: loaded from: classes3.dex */
public interface FTPTimestampParser {
    public static final String DEFAULT_RECENT_SDF = "MMM d HH:mm";
    public static final String DEFAULT_SDF = "MMM d yyyy";

    Calendar parseTimestamp(String str) throws ParseException;
}

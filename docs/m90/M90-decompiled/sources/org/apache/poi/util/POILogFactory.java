package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class POILogFactory {
    private static Map _loggers = new HashMap();

    private POILogFactory() {
    }

    public static POILogger getLogger(Class cls) {
        return getLogger(cls.getName());
    }

    public static POILogger getLogger(String str) {
        POILogger nullLogger;
        if (_loggers.containsKey(str)) {
            return (POILogger) _loggers.get(str);
        }
        try {
            nullLogger = (POILogger) Class.forName(System.getProperty("org.apache.poi.util.POILogger")).newInstance();
        } catch (Exception unused) {
            nullLogger = new NullLogger();
        }
        nullLogger.initialize(str);
        _loggers.put(str, nullLogger);
        return nullLogger;
    }
}

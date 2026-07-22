package org.apache.poi.hssf.usermodel;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/* JADX INFO: loaded from: classes3.dex */
class StaticFontMetrics {
    static /* synthetic */ Class class$org$apache$poi$hssf$usermodel$FontDetails;
    private static Map fontDetailsMap = new HashMap();
    private static Properties fontMetricsProps;

    StaticFontMetrics() {
    }

    public static FontDetails getFontDetails(Font font) {
        InputStream resourceAsStream;
        if (fontMetricsProps == null) {
            InputStream inputStream = null;
            try {
                try {
                    fontMetricsProps = new Properties();
                    if (System.getProperty("font.metrics.filename") != null) {
                        File file = new File(System.getProperty("font.metrics.filename"));
                        if (!file.exists()) {
                            throw new FileNotFoundException(new StringBuffer().append("font_metrics.properties not found at path ").append(file.getAbsolutePath()).toString());
                        }
                        resourceAsStream = new FileInputStream(file);
                    } else {
                        Class clsClass$ = class$org$apache$poi$hssf$usermodel$FontDetails;
                        if (clsClass$ == null) {
                            clsClass$ = class$("org.apache.poi.hssf.usermodel.FontDetails");
                            class$org$apache$poi$hssf$usermodel$FontDetails = clsClass$;
                        }
                        resourceAsStream = clsClass$.getResourceAsStream("/font_metrics.properties");
                        if (resourceAsStream == null) {
                            throw new FileNotFoundException("font_metrics.properties not found in classpath");
                        }
                    }
                    fontMetricsProps.load(resourceAsStream);
                    if (resourceAsStream != null) {
                        try {
                            resourceAsStream.close();
                        } catch (IOException unused) {
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(new StringBuffer().append("Could not load font metrics: ").append(e.getMessage()).toString());
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        inputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        }
        String name = font.getName();
        if (fontDetailsMap.get(name) == null) {
            FontDetails fontDetailsCreate = FontDetails.create(name, fontMetricsProps);
            fontDetailsMap.put(name, fontDetailsCreate);
            return fontDetailsCreate;
        }
        return (FontDetails) fontDetailsMap.get(name);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}

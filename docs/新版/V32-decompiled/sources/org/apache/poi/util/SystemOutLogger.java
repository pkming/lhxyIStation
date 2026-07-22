package org.apache.poi.util;

/* JADX INFO: loaded from: classes3.dex */
public class SystemOutLogger extends POILogger {
    private String cat;

    @Override // org.apache.poi.util.POILogger
    public void initialize(String str) {
        this.cat = str;
    }

    @Override // org.apache.poi.util.POILogger
    public void log(int i, Object obj) {
        if (check(i)) {
            System.out.println(new StringBuffer().append("[").append(this.cat).append("] ").append(obj).toString());
        }
    }

    @Override // org.apache.poi.util.POILogger
    public boolean check(int i) {
        return i >= Integer.parseInt(System.getProperty("poi.log.level", "5"));
    }
}

package org.apache.poi.hssf.util;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class SheetReferences {
    Map map = new HashMap(5);

    public void addSheetReference(String str, int i) {
        this.map.put(new Integer(i), str);
    }

    public String getSheetName(int i) {
        return (String) this.map.get(new Integer(i));
    }
}

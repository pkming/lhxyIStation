package org.apache.tools.ant.attribute;

import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.UnknownElement;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseIfAttribute extends ProjectComponent implements EnableAttribute {
    private boolean positive = true;

    protected void setPositive(boolean z) {
        this.positive = z;
    }

    protected boolean isPositive() {
        return this.positive;
    }

    protected boolean convertResult(boolean z) {
        return this.positive ? z : !z;
    }

    protected Map getParams(UnknownElement unknownElement) {
        HashMap map = new HashMap();
        for (Map.Entry<String, Object> entry : unknownElement.getWrapper().getAttributeMap().entrySet()) {
            String key = entry.getKey();
            String str = (String) entry.getValue();
            if (key.startsWith("ant-attribute:param")) {
                map.put(key.substring(key.lastIndexOf(58) + 1), unknownElement.getProject().replaceProperties(str));
            }
        }
        return map;
    }
}

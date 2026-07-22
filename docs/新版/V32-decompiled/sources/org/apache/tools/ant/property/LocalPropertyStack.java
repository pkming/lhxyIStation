package org.apache.tools.ant.property;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.tools.ant.PropertyHelper;

/* JADX INFO: loaded from: classes3.dex */
public class LocalPropertyStack {
    private final LinkedList<Map<String, Object>> stack = new LinkedList<>();

    public void addLocal(String str) {
        if (this.stack.isEmpty()) {
            return;
        }
        this.stack.getFirst().put(str, NullReturn.NULL);
    }

    public void enterScope() {
        this.stack.addFirst(new HashMap());
    }

    public void exitScope() {
        this.stack.removeFirst().clear();
    }

    public LocalPropertyStack copy() {
        LocalPropertyStack localPropertyStack = new LocalPropertyStack();
        localPropertyStack.stack.addAll(this.stack);
        return localPropertyStack;
    }

    public Object evaluate(String str, PropertyHelper propertyHelper) {
        Iterator<Map<String, Object>> it = this.stack.iterator();
        while (it.hasNext()) {
            Object obj = it.next().get(str);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    public boolean setNew(String str, Object obj, PropertyHelper propertyHelper) {
        Map<String, Object> mapForProperty = getMapForProperty(str);
        if (mapForProperty == null) {
            return false;
        }
        if (mapForProperty.get(str) != NullReturn.NULL) {
            return true;
        }
        mapForProperty.put(str, obj);
        return true;
    }

    public boolean set(String str, Object obj, PropertyHelper propertyHelper) {
        Map<String, Object> mapForProperty = getMapForProperty(str);
        if (mapForProperty == null) {
            return false;
        }
        mapForProperty.put(str, obj);
        return true;
    }

    private Map<String, Object> getMapForProperty(String str) {
        for (Map<String, Object> map : this.stack) {
            if (map.get(str) != null) {
                return map;
            }
        }
        return null;
    }
}

package org.apache.tools.ant.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class ResolvePropertyMap implements GetProperty {
    private Map<String, Object> map;
    private final GetProperty master;
    private final ParseProperties parseProperties;
    private String prefix;
    private final Set<String> seen = new HashSet();
    private boolean prefixValues = false;
    private boolean expandingLHS = true;

    public ResolvePropertyMap(Project project, GetProperty getProperty, Collection<PropertyExpander> collection) {
        this.master = getProperty;
        this.parseProperties = new ParseProperties(project, collection, this);
    }

    @Override // org.apache.tools.ant.property.GetProperty
    public Object getProperty(String str) {
        if (this.seen.contains(str)) {
            throw new BuildException("Property " + str + " was circularly defined.");
        }
        try {
            Object property = this.master.getProperty((this.prefix == null || !(this.expandingLHS || this.prefixValues)) ? str : this.prefix + str);
            if (property == null) {
                this.seen.add(str);
                String str2 = (this.prefix == null || this.expandingLHS || this.prefixValues) ? str : this.prefix + str;
                this.expandingLHS = false;
                property = this.parseProperties.parseProperties((String) this.map.get(str2));
            }
            return property;
        } finally {
            this.seen.remove(str);
        }
    }

    public void resolveAllProperties(Map<String, Object> map) {
        resolveAllProperties(map, null, false);
    }

    public void resolveAllProperties(Map<String, Object> map, String str) {
        resolveAllProperties(map, null, false);
    }

    public void resolveAllProperties(Map<String, Object> map, String str, boolean z) {
        this.map = map;
        this.prefix = str;
        this.prefixValues = z;
        for (String str2 : map.keySet()) {
            this.expandingLHS = true;
            Object property = getProperty(str2);
            map.put(str2, property == null ? "" : property.toString());
        }
    }
}

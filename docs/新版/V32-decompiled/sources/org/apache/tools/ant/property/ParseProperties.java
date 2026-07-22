package org.apache.tools.ant.property;

import java.text.ParsePosition;
import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class ParseProperties implements ParseNextProperty {
    private final Collection<PropertyExpander> expanders;
    private final GetProperty getProperty;
    private final Project project;

    public ParseProperties(Project project, Collection<PropertyExpander> collection, GetProperty getProperty) {
        this.project = project;
        this.expanders = collection;
        this.getProperty = getProperty;
    }

    @Override // org.apache.tools.ant.property.ParseNextProperty
    public Project getProject() {
        return this.project;
    }

    public Object parseProperties(String str) {
        if (str == null || "".equals(str)) {
            return str;
        }
        int length = str.length();
        ParsePosition parsePosition = new ParsePosition(0);
        Object nextProperty = parseNextProperty(str, parsePosition);
        if (nextProperty != null && parsePosition.getIndex() >= length) {
            return nextProperty;
        }
        StringBuffer stringBuffer = new StringBuffer(length * 2);
        if (nextProperty == null) {
            stringBuffer.append(str.charAt(parsePosition.getIndex()));
            parsePosition.setIndex(parsePosition.getIndex() + 1);
        } else {
            stringBuffer.append(nextProperty);
        }
        while (parsePosition.getIndex() < length) {
            Object nextProperty2 = parseNextProperty(str, parsePosition);
            if (nextProperty2 == null) {
                stringBuffer.append(str.charAt(parsePosition.getIndex()));
                parsePosition.setIndex(parsePosition.getIndex() + 1);
            } else {
                stringBuffer.append(nextProperty2);
            }
        }
        return stringBuffer.toString();
    }

    public boolean containsProperties(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        ParsePosition parsePosition = new ParsePosition(0);
        while (parsePosition.getIndex() < length) {
            if (parsePropertyName(str, parsePosition) != null) {
                return true;
            }
            parsePosition.setIndex(parsePosition.getIndex() + 1);
        }
        return false;
    }

    @Override // org.apache.tools.ant.property.ParseNextProperty
    public Object parseNextProperty(String str, ParsePosition parsePosition) {
        String propertyName;
        int index = parsePosition.getIndex();
        if (index > str.length() || (propertyName = parsePropertyName(str, parsePosition)) == null) {
            return null;
        }
        Object property = getProperty(propertyName);
        if (property != null) {
            return property;
        }
        Project project = this.project;
        if (project != null) {
            project.log("Property \"" + propertyName + "\" has not been set", 3);
        }
        return str.substring(index, parsePosition.getIndex());
    }

    private String parsePropertyName(String str, ParsePosition parsePosition) {
        Iterator<PropertyExpander> it = this.expanders.iterator();
        while (it.hasNext()) {
            String propertyName = it.next().parsePropertyName(str, parsePosition, this);
            if (propertyName != null) {
                return propertyName;
            }
        }
        return null;
    }

    private Object getProperty(String str) {
        return this.getProperty.getProperty(str);
    }
}

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.JAXPUtils;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes3.dex */
public class ParserSupports extends ProjectComponent implements Condition {
    public static final String ERROR_BOTH_ATTRIBUTES = "Property and feature attributes are exclusive";
    public static final String ERROR_NO_ATTRIBUTES = "Neither feature or property are set";
    public static final String ERROR_NO_VALUE = "A value is needed when testing for property support";
    public static final String FEATURE = "feature";
    public static final String NOT_RECOGNIZED = " not recognized: ";
    public static final String NOT_SUPPORTED = " not supported: ";
    public static final String PROPERTY = "property";
    private String feature;
    private String property;
    private String value;

    public void setFeature(String str) {
        this.feature = str;
    }

    public void setProperty(String str) {
        this.property = str;
    }

    public void setValue(String str) {
        this.value = str;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        String str = this.feature;
        if (str != null && this.property != null) {
            throw new BuildException(ERROR_BOTH_ATTRIBUTES);
        }
        if (str == null && this.property == null) {
            throw new BuildException(ERROR_NO_ATTRIBUTES);
        }
        if (str != null) {
            return evalFeature();
        }
        if (this.value == null) {
            throw new BuildException(ERROR_NO_VALUE);
        }
        return evalProperty();
    }

    private XMLReader getReader() {
        JAXPUtils.getParser();
        return JAXPUtils.getXMLReader();
    }

    public boolean evalFeature() {
        XMLReader reader = getReader();
        if (this.value == null) {
            this.value = "true";
        }
        try {
            reader.setFeature(this.feature, Project.toBoolean(this.value));
            return true;
        } catch (SAXNotRecognizedException unused) {
            log("feature not recognized: " + this.feature, 3);
            return false;
        } catch (SAXNotSupportedException unused2) {
            log("feature not supported: " + this.feature, 3);
            return false;
        }
    }

    public boolean evalProperty() {
        try {
            getReader().setProperty(this.property, this.value);
            return true;
        } catch (SAXNotRecognizedException unused) {
            log("property not recognized: " + this.property, 3);
            return false;
        } catch (SAXNotSupportedException unused2) {
            log("property not supported: " + this.property, 3);
            return false;
        }
    }
}

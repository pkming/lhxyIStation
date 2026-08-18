package org.apache.tools.ant.taskdefs.optional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes3.dex */
public class EchoProperties extends Task {
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private static final String PROPERTIES = "properties";
    private static final String PROPERTY = "property";
    private String prefix;
    private String regex;
    private File inFile = null;
    private File destfile = null;
    private boolean failonerror = true;
    private Vector propertySets = new Vector();
    private String format = "text";

    public void setSrcfile(File file) {
        this.inFile = file;
    }

    public void setDestfile(File file) {
        this.destfile = file;
    }

    public void setFailOnError(boolean z) {
        this.failonerror = z;
    }

    public void setPrefix(String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        this.prefix = str;
        PropertySet propertySet = new PropertySet();
        propertySet.setProject(getProject());
        propertySet.appendPrefix(str);
        addPropertyset(propertySet);
    }

    public void setRegex(String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        this.regex = str;
        PropertySet propertySet = new PropertySet();
        propertySet.setProject(getProject());
        propertySet.appendRegex(str);
        addPropertyset(propertySet);
    }

    public void addPropertyset(PropertySet propertySet) {
        this.propertySets.addElement(propertySet);
    }

    public void setFormat(FormatAttribute formatAttribute) {
        this.format = formatAttribute.getValue();
    }

    public static class FormatAttribute extends EnumeratedAttribute {
        private String[] formats = {"xml", "text"};

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return this.formats;
        }
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        FileInputStream fileInputStream;
        File file;
        if (this.prefix != null && this.regex != null) {
            throw new BuildException("Please specify either prefix or regex, but not both", getLocation());
        }
        Hashtable hashtable = new Hashtable();
        FileInputStream fileInputStream2 = null;
        OutputStream outputStream = null;
        OutputStream outputStream2 = null;
        FileInputStream fileInputStream3 = null;
        FileInputStream fileInputStream4 = null;
        OutputStream fileOutputStream = null;
        if (this.inFile == null && this.propertySets.size() == 0) {
            hashtable.putAll(getProject().getProperties());
        } else {
            File file2 = this.inFile;
            if (file2 != null) {
                if (file2.exists() && this.inFile.isDirectory()) {
                    if (this.failonerror) {
                        throw new BuildException("srcfile is a directory!", getLocation());
                    }
                    log("srcfile is a directory!", 0);
                    return;
                }
                if (this.inFile.exists() && !this.inFile.canRead()) {
                    if (this.failonerror) {
                        throw new BuildException("Can not read from the specified srcfile!", getLocation());
                    }
                    log("Can not read from the specified srcfile!", 0);
                    return;
                }
                try {
                    try {
                        fileInputStream = new FileInputStream(this.inFile);
                    } catch (Throwable th) {
                        th = th;
                    }
                } catch (FileNotFoundException e) {
                    e = e;
                } catch (IOException e2) {
                    e = e2;
                }
                try {
                    Properties properties = new Properties();
                    properties.load(fileInputStream);
                    hashtable.putAll(properties);
                    FileUtils.close(fileInputStream);
                } catch (FileNotFoundException e3) {
                    e = e3;
                    fileInputStream4 = fileInputStream;
                    String str = "Could not find file " + this.inFile.getAbsolutePath();
                    if (this.failonerror) {
                        throw new BuildException(str, e, getLocation());
                    }
                    log(str, 1);
                    FileUtils.close(fileInputStream4);
                    return;
                } catch (IOException e4) {
                    e = e4;
                    fileInputStream2 = fileInputStream;
                    String str2 = "Could not read file " + this.inFile.getAbsolutePath();
                    if (this.failonerror) {
                        throw new BuildException(str2, e, getLocation());
                    }
                    log(str2, 1);
                    FileUtils.close(fileInputStream2);
                    return;
                } catch (Throwable th2) {
                    th = th2;
                    fileInputStream3 = fileInputStream;
                    FileUtils.close(fileInputStream3);
                    throw th;
                }
            }
        }
        Enumeration enumerationElements = this.propertySets.elements();
        while (enumerationElements.hasMoreElements()) {
            hashtable.putAll(((PropertySet) enumerationElements.nextElement()).getProperties());
        }
        try {
            try {
                try {
                    file = this.destfile;
                } catch (IOException unused) {
                    return;
                }
            } catch (IOException e5) {
                e = e5;
            }
        } catch (Throwable th3) {
            th = th3;
        }
        try {
            if (file == null) {
                fileOutputStream = new ByteArrayOutputStream();
                saveProperties(hashtable, fileOutputStream);
                log(fileOutputStream.toString(), 2);
            } else if (file.exists() && this.destfile.isDirectory()) {
                if (this.failonerror) {
                    throw new BuildException("destfile is a directory!", getLocation());
                }
                log("destfile is a directory!", 0);
                return;
            } else {
                if (this.destfile.exists() && !this.destfile.canWrite()) {
                    if (this.failonerror) {
                        throw new BuildException("Can not write to the specified destfile!", getLocation());
                    }
                    log("Can not write to the specified destfile!", 0);
                    return;
                }
                fileOutputStream = new FileOutputStream(this.destfile);
                saveProperties(hashtable, fileOutputStream);
            }
            fileOutputStream.close();
        } catch (IOException e6) {
            e = e6;
            outputStream = fileOutputStream;
            if (this.failonerror) {
                throw new BuildException(e, getLocation());
            }
            log(e.getMessage(), 2);
            if (outputStream == null) {
            } else {
                outputStream.close();
            }
        } catch (Throwable th4) {
            th = th4;
            outputStream2 = fileOutputStream;
            if (outputStream2 != null) {
                try {
                    outputStream2.close();
                } catch (IOException unused2) {
                }
            }
            throw th;
        }
    }

    protected void saveProperties(Hashtable hashtable, OutputStream outputStream) throws Throwable {
        final ArrayList arrayList = new ArrayList(hashtable.keySet());
        Collections.sort(arrayList);
        Properties properties = new Properties() { // from class: org.apache.tools.ant.taskdefs.optional.EchoProperties.1
            private static final long serialVersionUID = 5090936442309201654L;

            @Override // java.util.Hashtable, java.util.Dictionary
            public Enumeration keys() {
                return CollectionUtils.asEnumeration(arrayList.iterator());
            }

            @Override // java.util.Hashtable, java.util.Map
            public Set entrySet() {
                Set setEntrySet = super.entrySet();
                if (!JavaEnvUtils.isKaffe()) {
                    return setEntrySet;
                }
                TreeSet treeSet = new TreeSet(new Comparator() { // from class: org.apache.tools.ant.taskdefs.optional.EchoProperties.1.1
                    @Override // java.util.Comparator
                    public int compare(Object obj, Object obj2) {
                        return ((String) ((Map.Entry) obj).getKey()).compareTo((String) ((Map.Entry) obj2).getKey());
                    }
                });
                treeSet.addAll(setEntrySet);
                return treeSet;
            }
        };
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            String string = arrayList.get(i).toString();
            properties.setProperty(string, hashtable.get(string).toString());
        }
        if ("text".equals(this.format)) {
            jdkSaveProperties(properties, outputStream, "Ant properties");
        } else if ("xml".equals(this.format)) {
            xmlSaveProperties(properties, outputStream);
        }
    }

    private static final class Tuple implements Comparable {
        private String key;
        private String value;

        private Tuple(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        @Override // java.lang.Comparable
        public int compareTo(Object obj) {
            return this.key.compareTo(((Tuple) obj).key);
        }
    }

    private List sortProperties(Properties properties) {
        ArrayList arrayList = new ArrayList(properties.size());
        Enumeration<?> enumerationPropertyNames = properties.propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str = (String) enumerationPropertyNames.nextElement();
            arrayList.add(new Tuple(str, properties.getProperty(str)));
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    protected void xmlSaveProperties(Properties properties, OutputStream outputStream) throws Throwable {
        OutputStreamWriter outputStreamWriter;
        Throwable th;
        IOException e;
        Document documentNewDocument = getDocumentBuilder().newDocument();
        Element elementCreateElement = documentNewDocument.createElement(PROPERTIES);
        for (Tuple tuple : sortProperties(properties)) {
            Element elementCreateElement2 = documentNewDocument.createElement("property");
            elementCreateElement2.setAttribute("name", tuple.key);
            elementCreateElement2.setAttribute("value", tuple.value);
            elementCreateElement.appendChild(elementCreateElement2);
        }
        try {
            outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8");
            try {
                try {
                    outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    new DOMElementWriter().write(elementCreateElement, outputStreamWriter, 0, "\t");
                    outputStreamWriter.flush();
                    FileUtils.close(outputStreamWriter);
                } catch (IOException e2) {
                    e = e2;
                    throw new BuildException("Unable to write XML file", e);
                }
            } catch (Throwable th2) {
                th = th2;
                FileUtils.close(outputStreamWriter);
                throw th;
            }
        } catch (IOException e3) {
            outputStreamWriter = null;
            e = e3;
        } catch (Throwable th3) {
            outputStreamWriter = null;
            th = th3;
            FileUtils.close(outputStreamWriter);
            throw th;
        }
    }

    protected void jdkSaveProperties(Properties properties, OutputStream outputStream, String str) throws IOException {
        try {
            try {
                properties.store(outputStream, str);
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException unused) {
                        log("Failed to close output stream");
                    }
                }
            } catch (IOException e) {
                throw new BuildException(e, getLocation());
            }
        } catch (Throwable th) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException unused2) {
                    log("Failed to close output stream");
                }
            }
            throw th;
        }
    }

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

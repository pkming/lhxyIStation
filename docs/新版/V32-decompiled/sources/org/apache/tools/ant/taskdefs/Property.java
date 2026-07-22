package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.property.ResolvePropertyMap;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Property extends Task {
    private File basedir;
    protected Path classpath;
    protected String env;
    private Project fallback;
    protected File file;
    protected String name;
    protected String prefix;
    private boolean prefixValues;
    protected Reference ref;
    private boolean relative;
    protected String resource;
    private Object untypedValue;
    protected URL url;
    protected boolean userProperty;
    protected String value;
    private boolean valueAttributeUsed;

    public Property() {
        this(false);
    }

    protected Property(boolean z) {
        this(z, null);
    }

    protected Property(boolean z, Project project) {
        this.valueAttributeUsed = false;
        this.relative = false;
        this.prefixValues = false;
        this.userProperty = z;
        this.fallback = project;
    }

    public void setRelative(boolean z) {
        this.relative = z;
    }

    public void setBasedir(File file) {
        this.basedir = file;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public void setLocation(File file) {
        if (this.relative) {
            internalSetValue(file);
        } else {
            setValue(file.getAbsolutePath());
        }
    }

    public void setValue(Object obj) {
        this.valueAttributeUsed = true;
        internalSetValue(obj);
    }

    private void internalSetValue(Object obj) {
        this.untypedValue = obj;
        this.value = obj == null ? null : obj.toString();
    }

    public void setValue(String str) {
        setValue((Object) str);
    }

    public void addText(String str) {
        if (!this.valueAttributeUsed) {
            String strReplaceProperties = getProject().replaceProperties(str);
            String value = getValue();
            if (value != null) {
                strReplaceProperties = value + strReplaceProperties;
            }
            internalSetValue(strReplaceProperties);
            return;
        }
        if (str.trim().length() > 0) {
            throw new BuildException("can't combine nested text with value attribute");
        }
    }

    public String getValue() {
        return this.value;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setPrefix(String str) {
        this.prefix = str;
        if (str == null || str.endsWith(".")) {
            return;
        }
        this.prefix += ".";
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefixValues(boolean z) {
        this.prefixValues = z;
    }

    public boolean getPrefixValues() {
        return this.prefixValues;
    }

    public void setRefid(Reference reference) {
        this.ref = reference;
    }

    public Reference getRefid() {
        return this.ref;
    }

    public void setResource(String str) {
        this.resource = str;
    }

    public String getResource() {
        return this.resource;
    }

    public void setEnvironment(String str) {
        this.env = str;
    }

    public String getEnvironment() {
        return this.env;
    }

    public void setClasspath(Path path) {
        Path path2 = this.classpath;
        if (path2 == null) {
            this.classpath = path;
        } else {
            path2.append(path);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference reference) {
        createClasspath().setRefid(reference);
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public void setUserProperty(boolean z) {
        log("DEPRECATED: Ignoring request to set user property in Property task.", 1);
    }

    public String toString() {
        String str = this.value;
        return str == null ? "" : str;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        Reference reference;
        Object obj;
        if (getProject() == null) {
            throw new IllegalStateException("project has not been set");
        }
        String str = this.name;
        if (str != null) {
            if (this.untypedValue == null && this.ref == null) {
                throw new BuildException("You must specify value, location or refid with the name attribute", getLocation());
            }
        } else if (this.url == null && this.file == null && this.resource == null && this.env == null) {
            throw new BuildException("You must specify url, file, resource or environment when not using the name attribute", getLocation());
        }
        if (this.url == null && this.file == null && this.resource == null && this.prefix != null) {
            throw new BuildException("Prefix is only valid when loading from a url, file or resource", getLocation());
        }
        if (str != null && (obj = this.untypedValue) != null) {
            if (this.relative) {
                try {
                    File file = obj instanceof File ? (File) obj : new File(this.untypedValue.toString());
                    File baseDir = this.basedir;
                    if (baseDir == null) {
                        baseDir = getProject().getBaseDir();
                    }
                    addProperty(this.name, FileUtils.getRelativePath(baseDir, file).replace('/', File.separatorChar));
                } catch (Exception e) {
                    throw new BuildException(e, getLocation());
                }
            } else {
                addProperty(str, obj);
            }
        }
        File file2 = this.file;
        if (file2 != null) {
            loadFile(file2);
        }
        URL url = this.url;
        if (url != null) {
            loadUrl(url);
        }
        String str2 = this.resource;
        if (str2 != null) {
            loadResource(str2);
        }
        String str3 = this.env;
        if (str3 != null) {
            loadEnvironment(str3);
        }
        String str4 = this.name;
        if (str4 == null || (reference = this.ref) == null) {
            return;
        }
        try {
            addProperty(str4, reference.getReferencedObject(getProject()).toString());
        } catch (BuildException e2) {
            Project project = this.fallback;
            if (project != null) {
                addProperty(this.name, this.ref.getReferencedObject(project).toString());
                return;
            }
            throw e2;
        }
    }

    protected void loadUrl(URL url) throws BuildException {
        Properties properties = new Properties();
        log("Loading " + url, 3);
        try {
            InputStream inputStreamOpenStream = url.openStream();
            try {
                loadProperties(properties, inputStreamOpenStream, url.getFile().endsWith(".xml"));
                addProperties(properties);
            } finally {
                if (inputStreamOpenStream != null) {
                    inputStreamOpenStream.close();
                }
            }
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    private void loadProperties(Properties properties, InputStream inputStream, boolean z) throws IOException {
        if (z) {
            properties.loadFromXML(inputStream);
        } else {
            properties.load(inputStream);
        }
    }

    protected void loadFile(File file) throws Throwable {
        Properties properties = new Properties();
        log("Loading " + file.getAbsolutePath(), 3);
        try {
            if (file.exists()) {
                FileInputStream fileInputStream = null;
                try {
                    FileInputStream fileInputStream2 = new FileInputStream(file);
                    try {
                        loadProperties(properties, fileInputStream2, file.getName().endsWith(".xml"));
                        FileUtils.close(fileInputStream2);
                        addProperties(properties);
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream = fileInputStream2;
                        FileUtils.close(fileInputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } else {
                log("Unable to find property file: " + file.getAbsolutePath(), 3);
            }
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:41:0x009c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0097 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:? A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void loadResource(java.lang.String r8) throws java.lang.Throwable {
        /*
            r7 = this;
            java.util.Properties r0 = new java.util.Properties
            r0.<init>()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Resource Loading "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.String r1 = r1.toString()
            r2 = 3
            r7.log(r1, r2)
            r1 = 1
            r2 = 0
            r3 = 0
            org.apache.tools.ant.types.Path r4 = r7.classpath     // Catch: java.lang.Throwable -> L83 java.io.IOException -> L87
            if (r4 == 0) goto L33
            org.apache.tools.ant.Project r3 = r7.getProject()     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L31
            org.apache.tools.ant.types.Path r4 = r7.classpath     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L31
            org.apache.tools.ant.AntClassLoader r3 = r3.createClassLoader(r4)     // Catch: java.lang.Throwable -> L2f java.io.IOException -> L31
            r4 = r1
            goto L3e
        L2f:
            r8 = move-exception
            goto L85
        L31:
            r8 = move-exception
            goto L89
        L33:
            java.lang.Class r4 = r7.getClass()     // Catch: java.lang.Throwable -> L83 java.io.IOException -> L87
            java.lang.ClassLoader r4 = r4.getClassLoader()     // Catch: java.lang.Throwable -> L83 java.io.IOException -> L87
            r6 = r4
            r4 = r3
            r3 = r6
        L3e:
            if (r3 != 0) goto L45
            java.io.InputStream r2 = java.lang.ClassLoader.getSystemResourceAsStream(r8)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            goto L49
        L45:
            java.io.InputStream r2 = r3.getResourceAsStream(r8)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
        L49:
            if (r2 == 0) goto L58
            java.lang.String r1 = ".xml"
            boolean r8 = r8.endsWith(r1)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            r7.loadProperties(r0, r2, r8)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            r7.addProperties(r0)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            goto L6e
        L58:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            r0.<init>()     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            java.lang.String r5 = "Unable to find resource "
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            java.lang.StringBuilder r8 = r0.append(r8)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
            r7.log(r8, r1)     // Catch: java.lang.Throwable -> L7d java.io.IOException -> L80
        L6e:
            if (r2 == 0) goto L73
            r2.close()     // Catch: java.io.IOException -> L73
        L73:
            if (r4 == 0) goto L7c
            if (r3 == 0) goto L7c
            org.apache.tools.ant.AntClassLoader r3 = (org.apache.tools.ant.AntClassLoader) r3
            r3.cleanup()
        L7c:
            return
        L7d:
            r8 = move-exception
            r1 = r4
            goto L95
        L80:
            r8 = move-exception
            r1 = r4
            goto L8a
        L83:
            r8 = move-exception
            r1 = r3
        L85:
            r3 = r2
            goto L95
        L87:
            r8 = move-exception
            r1 = r3
        L89:
            r3 = r2
        L8a:
            org.apache.tools.ant.BuildException r0 = new org.apache.tools.ant.BuildException     // Catch: java.lang.Throwable -> L94
            org.apache.tools.ant.Location r4 = r7.getLocation()     // Catch: java.lang.Throwable -> L94
            r0.<init>(r8, r4)     // Catch: java.lang.Throwable -> L94
            throw r0     // Catch: java.lang.Throwable -> L94
        L94:
            r8 = move-exception
        L95:
            if (r2 == 0) goto L9a
            r2.close()     // Catch: java.io.IOException -> L9a
        L9a:
            if (r1 == 0) goto La3
            if (r3 == 0) goto La3
            org.apache.tools.ant.AntClassLoader r3 = (org.apache.tools.ant.AntClassLoader) r3
            r3.cleanup()
        La3:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.taskdefs.Property.loadResource(java.lang.String):void");
    }

    protected void loadEnvironment(String str) {
        Properties properties = new Properties();
        if (!str.endsWith(".")) {
            str = str + ".";
        }
        log("Loading Environment " + str, 3);
        for (Map.Entry<String, String> entry : Execute.getEnvironmentVariables().entrySet()) {
            properties.put(str + ((Object) entry.getKey()), entry.getValue());
        }
        addProperties(properties);
    }

    protected void addProperties(Properties properties) {
        HashMap map = new HashMap(properties);
        resolveAllProperties(map);
        for (Object obj : map.keySet()) {
            if (obj instanceof String) {
                String str = (String) obj;
                if (this.prefix != null) {
                    str = this.prefix + str;
                }
                addProperty(str, map.get(obj));
            }
        }
    }

    protected void addProperty(String str, String str2) {
        addProperty(str, (Object) str2);
    }

    protected void addProperty(String str, Object obj) {
        PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(getProject());
        if (this.userProperty) {
            if (propertyHelper.getUserProperty(str) == null) {
                propertyHelper.setInheritedProperty(str, obj);
                return;
            } else {
                log("Override ignored for " + str, 3);
                return;
            }
        }
        propertyHelper.setNewProperty(str, obj);
    }

    private void resolveAllProperties(Map map) throws BuildException {
        PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(getProject());
        new ResolvePropertyMap(getProject(), propertyHelper, propertyHelper.getExpanders()).resolveAllProperties(map, getPrefix(), getPrefixValues());
    }
}

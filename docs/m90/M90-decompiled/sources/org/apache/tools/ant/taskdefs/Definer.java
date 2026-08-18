package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class Definer extends DefBase {
    private static final String ANTLIB_XML = "/antlib.xml";
    private static final ThreadLocal<Map<URL, Location>> RESOURCE_STACK = new ThreadLocal<Map<URL, Location>>() { // from class: org.apache.tools.ant.taskdefs.Definer.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public Map<URL, Location> initialValue() {
            return new HashMap();
        }
    };
    private String adaptTo;
    private Class<?> adaptToClass;
    private String adapter;
    private Class<?> adapterClass;
    private String classname;
    private File file;
    private String name;
    private String resource;
    private boolean restrict = false;
    private int format = 0;
    private boolean definerSet = false;
    private int onError = 0;

    public static class OnError extends EnumeratedAttribute {
        public static final int FAIL = 0;
        public static final int FAIL_ALL = 3;
        public static final int IGNORE = 2;
        public static final String POLICY_FAIL = "fail";
        public static final String POLICY_FAILALL = "failall";
        public static final String POLICY_IGNORE = "ignore";
        public static final String POLICY_REPORT = "report";
        public static final int REPORT = 1;

        public OnError() {
        }

        public OnError(String str) {
            setValue(str);
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"fail", POLICY_REPORT, POLICY_IGNORE, POLICY_FAILALL};
        }
    }

    public static class Format extends EnumeratedAttribute {
        public static final int PROPERTIES = 0;
        public static final int XML = 1;

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"properties", "xml"};
        }
    }

    protected void setRestrict(boolean z) {
        this.restrict = z;
    }

    public void setOnError(OnError onError) {
        this.onError = onError.getIndex();
    }

    public void setFormat(Format format) {
        this.format = format.getIndex();
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public String getResource() {
        return this.resource;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Enumeration<URL> enumeration;
        ClassLoader classLoaderCreateLoader = createLoader();
        if (!this.definerSet) {
            if (getURI() == null) {
                throw new BuildException("name, file or resource attribute of " + getTaskName() + " is undefined", getLocation());
            }
            if (getURI().startsWith("antlib:")) {
                setResource(makeResourceFromURI(getURI()));
            } else {
                throw new BuildException("Only antlib URIs can be located from the URI alone, not the URI '" + getURI() + "'");
            }
        }
        String str = this.name;
        if (str != null) {
            String str2 = this.classname;
            if (str2 == null) {
                throw new BuildException("classname attribute of " + getTaskName() + " element is undefined", getLocation());
            }
            addDefinition(classLoaderCreateLoader, str, str2);
            return;
        }
        if (this.classname != null) {
            throw new BuildException("You must not specify classname together with file or resource.", getLocation());
        }
        if (this.file == null) {
            enumeration = resourceToURLs(classLoaderCreateLoader);
        } else {
            URL urlFileToURL = fileToURL();
            if (urlFileToURL == null) {
                return;
            } else {
                enumeration = Collections.enumeration(Collections.singleton(urlFileToURL));
            }
        }
        while (enumeration.hasMoreElements()) {
            URL urlNextElement = enumeration.nextElement();
            int i = this.format;
            if (urlNextElement.toString().toLowerCase(Locale.ENGLISH).endsWith(".xml")) {
                i = 1;
            }
            if (i == 0) {
                loadProperties(classLoaderCreateLoader, urlNextElement);
                return;
            }
            ThreadLocal<Map<URL, Location>> threadLocal = RESOURCE_STACK;
            if (threadLocal.get().get(urlNextElement) != null) {
                log("Warning: Recursive loading of " + urlNextElement + " ignored at " + getLocation() + " originally loaded at " + threadLocal.get().get(urlNextElement), 1);
            } else {
                try {
                    threadLocal.get().put(urlNextElement, getLocation());
                    loadAntlib(classLoaderCreateLoader, urlNextElement);
                    threadLocal.get().remove(urlNextElement);
                } catch (Throwable th) {
                    RESOURCE_STACK.get().remove(urlNextElement);
                    throw th;
                }
            }
        }
    }

    public static String makeResourceFromURI(String str) {
        String strSubstring = str.substring(7);
        if (strSubstring.startsWith("//")) {
            String strSubstring2 = strSubstring.substring(2);
            return !strSubstring2.endsWith(".xml") ? strSubstring2 + ANTLIB_XML : strSubstring2;
        }
        return strSubstring.replace('.', '/') + ANTLIB_XML;
    }

    private URL fileToURL() {
        String str = !this.file.exists() ? "File " + this.file + " does not exist" : null;
        if (str == null && !this.file.isFile()) {
            str = "File " + this.file + " is not a file";
        }
        if (str == null) {
            try {
                return FileUtils.getFileUtils().getFileURL(this.file);
            } catch (Exception e) {
                str = "File " + this.file + " cannot use as URL: " + e.toString();
            }
        }
        int i = this.onError;
        if (i == 0 || i == 1) {
            log(str, 1);
        } else if (i == 2) {
            log(str, 3);
        } else if (i == 3) {
            throw new BuildException(str);
        }
        return null;
    }

    private Enumeration<URL> resourceToURLs(ClassLoader classLoader) {
        try {
            Enumeration<URL> resources = classLoader.getResources(this.resource);
            if (!resources.hasMoreElements()) {
                String str = "Could not load definitions from resource " + this.resource + Rmic.ERROR_NOT_FOUND;
                int i = this.onError;
                if (i == 0 || i == 1) {
                    log(str, 1);
                } else if (i == 2) {
                    log(str, 3);
                } else if (i == 3) {
                    throw new BuildException(str);
                }
            }
            return resources;
        } catch (IOException e) {
            throw new BuildException("Could not fetch resources named " + this.resource, e, getLocation());
        }
    }

    protected void loadProperties(ClassLoader classLoader, URL url) {
        try {
            try {
                InputStream inputStreamOpenStream = url.openStream();
                if (inputStreamOpenStream == null) {
                    log("Could not load definitions from " + url, 1);
                    FileUtils.close(inputStreamOpenStream);
                    return;
                }
                Properties properties = new Properties();
                properties.load(inputStreamOpenStream);
                Enumeration enumerationKeys = properties.keys();
                while (enumerationKeys.hasMoreElements()) {
                    String str = (String) enumerationKeys.nextElement();
                    this.name = str;
                    String property = properties.getProperty(str);
                    this.classname = property;
                    addDefinition(classLoader, this.name, property);
                }
                FileUtils.close(inputStreamOpenStream);
            } catch (IOException e) {
                throw new BuildException(e, getLocation());
            }
        } catch (Throwable th) {
            FileUtils.close((InputStream) null);
            throw th;
        }
    }

    private void loadAntlib(ClassLoader classLoader, URL url) {
        try {
            Antlib antlibCreateAntlib = Antlib.createAntlib(getProject(), url, getURI());
            antlibCreateAntlib.setClassLoader(classLoader);
            antlibCreateAntlib.setURI(getURI());
            antlibCreateAntlib.execute();
        } catch (BuildException e) {
            throw ProjectHelper.addLocationToBuildException(e, getLocation());
        }
    }

    public void setFile(File file) {
        if (this.definerSet) {
            tooManyDefinitions();
        }
        this.definerSet = true;
        this.file = file;
    }

    public void setResource(String str) {
        if (this.definerSet) {
            tooManyDefinitions();
        }
        this.definerSet = true;
        this.resource = str;
    }

    public void setAntlib(String str) {
        if (this.definerSet) {
            tooManyDefinitions();
        }
        if (!str.startsWith("antlib:")) {
            throw new BuildException("Invalid antlib attribute - it must start with antlib:");
        }
        setURI(str);
        this.resource = str.substring(7).replace('.', '/') + ANTLIB_XML;
        this.definerSet = true;
    }

    public void setName(String str) {
        if (this.definerSet) {
            tooManyDefinitions();
        }
        this.definerSet = true;
        this.name = str;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String str) {
        this.classname = str;
    }

    public void setAdapter(String str) {
        this.adapter = str;
    }

    protected void setAdapterClass(Class<?> cls) {
        this.adapterClass = cls;
    }

    public void setAdaptTo(String str) {
        this.adaptTo = str;
    }

    protected void setAdaptToClass(Class<?> cls) {
        this.adaptToClass = cls;
    }

    protected void addDefinition(ClassLoader classLoader, String str, String str2) throws BuildException {
        try {
            try {
                try {
                    String strGenComponentName = ProjectHelper.genComponentName(getURI(), str);
                    Class<?> cls = this.onError != 2 ? Class.forName(str2, true, classLoader) : null;
                    String str3 = this.adapter;
                    if (str3 != null) {
                        this.adapterClass = Class.forName(str3, true, classLoader);
                    }
                    String str4 = this.adaptTo;
                    if (str4 != null) {
                        this.adaptToClass = Class.forName(str4, true, classLoader);
                    }
                    AntTypeDefinition antTypeDefinition = new AntTypeDefinition();
                    antTypeDefinition.setName(strGenComponentName);
                    antTypeDefinition.setClassName(str2);
                    antTypeDefinition.setClass(cls);
                    antTypeDefinition.setAdapterClass(this.adapterClass);
                    antTypeDefinition.setAdaptToClass(this.adaptToClass);
                    antTypeDefinition.setRestrict(this.restrict);
                    antTypeDefinition.setClassLoader(classLoader);
                    if (cls != null) {
                        antTypeDefinition.checkClass(getProject());
                    }
                    ComponentHelper.getComponentHelper(getProject()).addDataTypeDefinition(antTypeDefinition);
                } catch (NoClassDefFoundError e) {
                    throw new BuildException(getTaskName() + " A class needed by class " + str2 + " cannot be found: " + e.getMessage() + "\n using the classloader " + classLoader, e, getLocation());
                }
            } catch (ClassNotFoundException e2) {
                throw new BuildException(getTaskName() + " class " + str2 + " cannot be found\n using the classloader " + classLoader, e2, getLocation());
            }
        } catch (BuildException e3) {
            int i = this.onError;
            if (i != 0) {
                if (i == 1) {
                    log(e3.getLocation() + "Warning: " + e3.getMessage(), 1);
                    return;
                } else if (i != 3) {
                    log(e3.getLocation() + e3.getMessage(), 4);
                    return;
                }
            }
            throw e3;
        }
    }

    private void tooManyDefinitions() {
        throw new BuildException("Only one of the attributes name, file and resource can be set", getLocation());
    }
}

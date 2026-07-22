package org.apache.tools.ant;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.tools.ant.property.GetProperty;
import org.apache.tools.ant.property.NullReturn;
import org.apache.tools.ant.property.ParseNextProperty;
import org.apache.tools.ant.property.ParseProperties;
import org.apache.tools.ant.property.PropertyExpander;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyHelper implements GetProperty {
    private PropertyHelper next;
    private Project project;
    private static final PropertyEvaluator TO_STRING = new PropertyEvaluator() { // from class: org.apache.tools.ant.PropertyHelper.1
        private final String PREFIX = "toString:";
        private final int PREFIX_LEN = 9;

        @Override // org.apache.tools.ant.PropertyHelper.PropertyEvaluator
        public Object evaluate(String str, PropertyHelper propertyHelper) {
            Object reference = (!str.startsWith("toString:") || propertyHelper.getProject() == null) ? null : propertyHelper.getProject().getReference(str.substring(this.PREFIX_LEN));
            if (reference == null) {
                return null;
            }
            return reference.toString();
        }
    };
    private static final PropertyExpander DEFAULT_EXPANDER = new PropertyExpander() { // from class: org.apache.tools.ant.PropertyHelper.2
        @Override // org.apache.tools.ant.property.PropertyExpander
        public String parsePropertyName(String str, ParsePosition parsePosition, ParseNextProperty parseNextProperty) {
            int index = parsePosition.getIndex();
            if (str.length() - index < 3 || '$' != str.charAt(index) || '{' != str.charAt(index + 1)) {
                return null;
            }
            int i = index + 2;
            int iIndexOf = str.indexOf(125, i);
            if (iIndexOf < 0) {
                throw new BuildException("Syntax error in property: " + str.substring(index));
            }
            parsePosition.setIndex(iIndexOf + 1);
            return i == iIndexOf ? "" : str.substring(i, iIndexOf);
        }
    };
    private static final PropertyExpander SKIP_DOUBLE_DOLLAR = new PropertyExpander() { // from class: org.apache.tools.ant.PropertyHelper.3
        @Override // org.apache.tools.ant.property.PropertyExpander
        public String parsePropertyName(String str, ParsePosition parsePosition, ParseNextProperty parseNextProperty) {
            int index = parsePosition.getIndex();
            if (str.length() - index < 2 || '$' != str.charAt(index)) {
                return null;
            }
            int i = index + 1;
            if ('$' != str.charAt(i)) {
                return null;
            }
            parsePosition.setIndex(i);
            return null;
        }
    };
    private static final PropertyEvaluator FROM_REF = new PropertyEvaluator() { // from class: org.apache.tools.ant.PropertyHelper.4
        private final String PREFIX = "ant.refid:";
        private final int PREFIX_LEN = 10;

        @Override // org.apache.tools.ant.PropertyHelper.PropertyEvaluator
        public Object evaluate(String str, PropertyHelper propertyHelper) {
            if (!str.startsWith("ant.refid:") || propertyHelper.getProject() == null) {
                return null;
            }
            return propertyHelper.getProject().getReference(str.substring(this.PREFIX_LEN));
        }
    };
    private final Hashtable<Class<? extends Delegate>, List<Delegate>> delegates = new Hashtable<>();
    private Hashtable<String, Object> properties = new Hashtable<>();
    private Hashtable<String, Object> userProperties = new Hashtable<>();
    private Hashtable<String, Object> inheritedProperties = new Hashtable<>();

    public interface Delegate {
    }

    public interface PropertyEvaluator extends Delegate {
        Object evaluate(String str, PropertyHelper propertyHelper);
    }

    public interface PropertySetter extends Delegate {
        boolean set(String str, Object obj, PropertyHelper propertyHelper);

        boolean setNew(String str, Object obj, PropertyHelper propertyHelper);
    }

    protected PropertyHelper() {
        add(FROM_REF);
        add(TO_STRING);
        add(SKIP_DOUBLE_DOLLAR);
        add(DEFAULT_EXPANDER);
    }

    public static Object getProperty(Project project, String str) {
        return getPropertyHelper(project).getProperty(str);
    }

    public static void setProperty(Project project, String str, Object obj) {
        getPropertyHelper(project).setProperty(str, obj, true);
    }

    public static void setNewProperty(Project project, String str, Object obj) {
        getPropertyHelper(project).setNewProperty(str, obj);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void setNext(PropertyHelper propertyHelper) {
        this.next = propertyHelper;
    }

    public PropertyHelper getNext() {
        return this.next;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0013 A[DONT_GENERATE] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0015 A[Catch: all -> 0x000f, TRY_ENTER, TryCatch #0 {, blocks: (B:6:0x0006, B:13:0x0015, B:15:0x001f), top: B:20:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static synchronized org.apache.tools.ant.PropertyHelper getPropertyHelper(org.apache.tools.ant.Project r3) {
        /*
            java.lang.Class<org.apache.tools.ant.PropertyHelper> r0 = org.apache.tools.ant.PropertyHelper.class
            monitor-enter(r0)
            r1 = 0
            if (r3 == 0) goto L11
            java.lang.String r1 = "ant.PropertyHelper"
            java.lang.Object r1 = r3.getReference(r1)     // Catch: java.lang.Throwable -> Lf
            org.apache.tools.ant.PropertyHelper r1 = (org.apache.tools.ant.PropertyHelper) r1     // Catch: java.lang.Throwable -> Lf
            goto L11
        Lf:
            r3 = move-exception
            goto L26
        L11:
            if (r1 == 0) goto L15
            monitor-exit(r0)
            return r1
        L15:
            org.apache.tools.ant.PropertyHelper r1 = new org.apache.tools.ant.PropertyHelper     // Catch: java.lang.Throwable -> Lf
            r1.<init>()     // Catch: java.lang.Throwable -> Lf
            r1.setProject(r3)     // Catch: java.lang.Throwable -> Lf
            if (r3 == 0) goto L24
            java.lang.String r2 = "ant.PropertyHelper"
            r3.addReference(r2, r1)     // Catch: java.lang.Throwable -> Lf
        L24:
            monitor-exit(r0)
            return r1
        L26:
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.PropertyHelper.getPropertyHelper(org.apache.tools.ant.Project):org.apache.tools.ant.PropertyHelper");
    }

    public Collection<PropertyExpander> getExpanders() {
        return getDelegates(PropertyExpander.class);
    }

    public boolean setPropertyHook(String str, String str2, Object obj, boolean z, boolean z2, boolean z3) {
        return getNext() != null && getNext().setPropertyHook(str, str2, obj, z, z2, z3);
    }

    public Object getPropertyHook(String str, String str2, boolean z) {
        Object propertyHook;
        if (getNext() != null && (propertyHook = getNext().getPropertyHook(str, str2, z)) != null) {
            return propertyHook;
        }
        if (this.project == null || !str2.startsWith("toString:")) {
            return null;
        }
        Object reference = this.project.getReference(str2.substring(9));
        if (reference == null) {
            return null;
        }
        return reference.toString();
    }

    public void parsePropertyString(String str, Vector<String> vector, Vector<String> vector2) throws BuildException {
        parsePropertyStringDefault(str, vector, vector2);
    }

    public String replaceProperties(String str, String str2, Hashtable<String, Object> hashtable) throws BuildException {
        return replaceProperties(str2);
    }

    public String replaceProperties(String str) throws BuildException {
        Object properties = parseProperties(str);
        return (properties == null || (properties instanceof String)) ? (String) properties : properties.toString();
    }

    public Object parseProperties(String str) throws BuildException {
        return new ParseProperties(getProject(), getExpanders(), this).parseProperties(str);
    }

    public boolean containsProperties(String str) {
        return new ParseProperties(getProject(), getExpanders(), this).containsProperties(str);
    }

    public boolean setProperty(String str, String str2, Object obj, boolean z) {
        return setProperty(str2, obj, z);
    }

    public boolean setProperty(String str, Object obj, boolean z) {
        Iterator it = getDelegates(PropertySetter.class).iterator();
        while (it.hasNext()) {
            if (((PropertySetter) it.next()).set(str, obj, this)) {
                return true;
            }
        }
        synchronized (this) {
            if (this.userProperties.containsKey(str)) {
                Project project = this.project;
                if (project != null && z) {
                    project.log("Override ignored for user property \"" + str + "\"", 3);
                }
                return false;
            }
            if (this.project != null && z) {
                if (this.properties.containsKey(str)) {
                    this.project.log("Overriding previous definition of property \"" + str + "\"", 3);
                }
                this.project.log("Setting project property: " + str + " -> " + obj, 4);
            }
            if (str != null && obj != null) {
                this.properties.put(str, obj);
            }
            return true;
        }
    }

    public void setNewProperty(String str, String str2, Object obj) {
        setNewProperty(str2, obj);
    }

    public void setNewProperty(String str, Object obj) {
        Iterator it = getDelegates(PropertySetter.class).iterator();
        while (it.hasNext()) {
            if (((PropertySetter) it.next()).setNew(str, obj, this)) {
                return;
            }
        }
        synchronized (this) {
            if (this.project != null && this.properties.containsKey(str)) {
                this.project.log("Override ignored for property \"" + str + "\"", 3);
                return;
            }
            Project project = this.project;
            if (project != null) {
                project.log("Setting project property: " + str + " -> " + obj, 4);
            }
            if (str != null && obj != null) {
                this.properties.put(str, obj);
            }
        }
    }

    public void setUserProperty(String str, String str2, Object obj) {
        setUserProperty(str2, obj);
    }

    public void setUserProperty(String str, Object obj) {
        Project project = this.project;
        if (project != null) {
            project.log("Setting ro project property: " + str + " -> " + obj, 4);
        }
        synchronized (this) {
            this.userProperties.put(str, obj);
            this.properties.put(str, obj);
        }
    }

    public void setInheritedProperty(String str, String str2, Object obj) {
        setInheritedProperty(str2, obj);
    }

    public void setInheritedProperty(String str, Object obj) {
        Project project = this.project;
        if (project != null) {
            project.log("Setting ro project property: " + str + " -> " + obj, 4);
        }
        synchronized (this) {
            this.inheritedProperties.put(str, obj);
            this.userProperties.put(str, obj);
            this.properties.put(str, obj);
        }
    }

    public Object getProperty(String str, String str2) {
        return getProperty(str2);
    }

    @Override // org.apache.tools.ant.property.GetProperty
    public Object getProperty(String str) {
        if (str == null) {
            return null;
        }
        Iterator it = getDelegates(PropertyEvaluator.class).iterator();
        while (it.hasNext()) {
            Object objEvaluate = ((PropertyEvaluator) it.next()).evaluate(str, this);
            if (objEvaluate != null) {
                if (objEvaluate instanceof NullReturn) {
                    return null;
                }
                return objEvaluate;
            }
        }
        return this.properties.get(str);
    }

    public Object getUserProperty(String str, String str2) {
        return getUserProperty(str2);
    }

    public Object getUserProperty(String str) {
        if (str == null) {
            return null;
        }
        return this.userProperties.get(str);
    }

    public Hashtable<String, Object> getProperties() {
        Hashtable<String, Object> hashtable;
        synchronized (this.properties) {
            hashtable = new Hashtable<>(this.properties);
        }
        return hashtable;
    }

    public Hashtable<String, Object> getUserProperties() {
        Hashtable<String, Object> hashtable;
        synchronized (this.userProperties) {
            hashtable = new Hashtable<>(this.userProperties);
        }
        return hashtable;
    }

    public Hashtable<String, Object> getInheritedProperties() {
        Hashtable<String, Object> hashtable;
        synchronized (this.inheritedProperties) {
            hashtable = new Hashtable<>(this.inheritedProperties);
        }
        return hashtable;
    }

    protected Hashtable<String, Object> getInternalProperties() {
        return this.properties;
    }

    protected Hashtable<String, Object> getInternalUserProperties() {
        return this.userProperties;
    }

    protected Hashtable<String, Object> getInternalInheritedProperties() {
        return this.inheritedProperties;
    }

    public void copyInheritedProperties(Project project) {
        synchronized (this.inheritedProperties) {
            Enumeration<String> enumerationKeys = this.inheritedProperties.keys();
            while (enumerationKeys.hasMoreElements()) {
                String string = enumerationKeys.nextElement().toString();
                if (project.getUserProperty(string) == null) {
                    project.setInheritedProperty(string, this.inheritedProperties.get(string).toString());
                }
            }
        }
    }

    public void copyUserProperties(Project project) {
        synchronized (this.userProperties) {
            Enumeration<String> enumerationKeys = this.userProperties.keys();
            while (enumerationKeys.hasMoreElements()) {
                String strNextElement = enumerationKeys.nextElement();
                if (!this.inheritedProperties.containsKey(strNextElement)) {
                    project.setUserProperty(strNextElement.toString(), this.userProperties.get(strNextElement).toString());
                }
            }
        }
    }

    static void parsePropertyStringDefault(String str, Vector<String> vector, Vector<String> vector2) throws BuildException {
        int i = 0;
        while (true) {
            int iIndexOf = str.indexOf("$", i);
            if (iIndexOf >= 0) {
                if (iIndexOf > 0) {
                    vector.addElement(str.substring(i, iIndexOf));
                }
                if (iIndexOf == str.length() - 1) {
                    vector.addElement("$");
                    i = iIndexOf + 1;
                } else {
                    int i2 = iIndexOf + 1;
                    if (str.charAt(i2) != '{') {
                        if (str.charAt(i2) == '$') {
                            vector.addElement("$");
                            i = iIndexOf + 2;
                        } else {
                            i = iIndexOf + 2;
                            vector.addElement(str.substring(iIndexOf, i));
                        }
                    } else {
                        int iIndexOf2 = str.indexOf(125, iIndexOf);
                        if (iIndexOf2 < 0) {
                            throw new BuildException("Syntax error in property: " + str);
                        }
                        String strSubstring = str.substring(iIndexOf + 2, iIndexOf2);
                        vector.addElement(null);
                        vector2.addElement(strSubstring);
                        i = iIndexOf2 + 1;
                    }
                }
            } else {
                if (i < str.length()) {
                    vector.addElement(str.substring(i));
                    return;
                }
                return;
            }
        }
    }

    public void add(Delegate delegate) {
        ArrayList arrayList;
        synchronized (this.delegates) {
            for (Class<? extends Delegate> cls : getDelegateInterfaces(delegate)) {
                List<Delegate> list = this.delegates.get(cls);
                if (list == null) {
                    arrayList = new ArrayList();
                } else {
                    ArrayList arrayList2 = new ArrayList(list);
                    arrayList2.remove(delegate);
                    arrayList = arrayList2;
                }
                arrayList.add(0, delegate);
                this.delegates.put(cls, Collections.unmodifiableList(arrayList));
            }
        }
    }

    protected <D extends Delegate> List<D> getDelegates(Class<D> cls) {
        List<D> list = (List) this.delegates.get(cls);
        return list == null ? Collections.emptyList() : list;
    }

    protected static Set<Class<? extends Delegate>> getDelegateInterfaces(Delegate delegate) {
        HashSet hashSet = new HashSet();
        for (Class<?> superclass = delegate.getClass(); superclass != null; superclass = superclass.getSuperclass()) {
            Class<?>[] interfaces = superclass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (Delegate.class.isAssignableFrom(interfaces[i])) {
                    hashSet.add(interfaces[i]);
                }
            }
        }
        hashSet.remove(Delegate.class);
        return hashSet;
    }

    public static Boolean toBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (!(obj instanceof String)) {
            return null;
        }
        String str = (String) obj;
        if (Project.toBoolean(str)) {
            return Boolean.TRUE;
        }
        if ("off".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str) || "no".equalsIgnoreCase(str)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static boolean nullOrEmpty(Object obj) {
        return obj == null || "".equals(obj);
    }

    private boolean evalAsBooleanOrPropertyName(Object obj) {
        Boolean bool = toBoolean(obj);
        if (bool != null) {
            return bool.booleanValue();
        }
        return getProperty(String.valueOf(obj)) != null;
    }

    public boolean testIfCondition(Object obj) {
        return nullOrEmpty(obj) || evalAsBooleanOrPropertyName(obj);
    }

    public boolean testUnlessCondition(Object obj) {
        return nullOrEmpty(obj) || !evalAsBooleanOrPropertyName(obj);
    }
}

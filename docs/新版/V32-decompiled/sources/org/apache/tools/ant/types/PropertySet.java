package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.resources.MappedResource;
import org.apache.tools.ant.types.resources.PropertyResource;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;

/* JADX INFO: loaded from: classes3.dex */
public class PropertySet extends DataType implements ResourceCollection {
    private Set<String> cachedNames;
    private Mapper mapper;
    private boolean dynamic = true;
    private boolean negate = false;
    private List<PropertyRef> ptyRefs = new ArrayList();
    private List<PropertySet> setRefs = new ArrayList();
    private boolean noAttributeSet = true;

    public static class PropertyRef {
        private String builtin;
        private int count;
        private String name;
        private String prefix;
        private String regex;

        public void setName(String str) {
            assertValid("name", str);
            this.name = str;
        }

        public void setRegex(String str) {
            assertValid(FilenameSelector.REGEX_KEY, str);
            this.regex = str;
        }

        public void setPrefix(String str) {
            assertValid("prefix", str);
            this.prefix = str;
        }

        public void setBuiltin(BuiltinPropertySetName builtinPropertySetName) {
            String value = builtinPropertySetName.getValue();
            assertValid("builtin", value);
            this.builtin = value;
        }

        private void assertValid(String str, String str2) {
            if (str2 == null || str2.length() < 1) {
                throw new BuildException("Invalid attribute: " + str);
            }
            int i = this.count + 1;
            this.count = i;
            if (i != 1) {
                throw new BuildException("Attributes name, regex, and prefix are mutually exclusive");
            }
        }

        public String toString() {
            return "name=" + this.name + ", regex=" + this.regex + ", prefix=" + this.prefix + ", builtin=" + this.builtin;
        }
    }

    public void appendName(String str) {
        PropertyRef propertyRef = new PropertyRef();
        propertyRef.setName(str);
        addPropertyref(propertyRef);
    }

    public void appendRegex(String str) {
        PropertyRef propertyRef = new PropertyRef();
        propertyRef.setRegex(str);
        addPropertyref(propertyRef);
    }

    public void appendPrefix(String str) {
        PropertyRef propertyRef = new PropertyRef();
        propertyRef.setPrefix(str);
        addPropertyref(propertyRef);
    }

    public void appendBuiltin(BuiltinPropertySetName builtinPropertySetName) {
        PropertyRef propertyRef = new PropertyRef();
        propertyRef.setBuiltin(builtinPropertySetName);
        addPropertyref(propertyRef);
    }

    public void setMapper(String str, String str2, String str3) {
        Mapper mapperCreateMapper = createMapper();
        Mapper.MapperType mapperType = new Mapper.MapperType();
        mapperType.setValue(str);
        mapperCreateMapper.setType(mapperType);
        mapperCreateMapper.setFrom(str2);
        mapperCreateMapper.setTo(str3);
    }

    public void addPropertyref(PropertyRef propertyRef) {
        assertNotReference();
        setChecked(false);
        this.ptyRefs.add(propertyRef);
    }

    public void addPropertyset(PropertySet propertySet) {
        assertNotReference();
        setChecked(false);
        this.setRefs.add(propertySet);
    }

    public Mapper createMapper() {
        assertNotReference();
        if (this.mapper != null) {
            throw new BuildException("Too many <mapper>s!");
        }
        this.mapper = new Mapper(getProject());
        setChecked(false);
        return this.mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }

    public void setDynamic(boolean z) {
        assertNotReference();
        this.dynamic = z;
    }

    public void setNegate(boolean z) {
        assertNotReference();
        this.negate = z;
    }

    public boolean getDynamic() {
        if (isReference()) {
            return getRef().dynamic;
        }
        dieOnCircularReference();
        return this.dynamic;
    }

    public Mapper getMapper() {
        if (isReference()) {
            return getRef().mapper;
        }
        dieOnCircularReference();
        return this.mapper;
    }

    private Hashtable<String, Object> getAllSystemProperties() {
        Hashtable<String, Object> hashtable = new Hashtable<>();
        Enumeration<?> enumerationPropertyNames = System.getProperties().propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            String str = (String) enumerationPropertyNames.nextElement();
            hashtable.put(str, System.getProperties().getProperty(str));
        }
        return hashtable;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(getPropertyMap());
        return properties;
    }

    private Map<String, Object> getPropertyMap() {
        String[] strArrMapFileName;
        if (isReference()) {
            return getRef().getPropertyMap();
        }
        dieOnCircularReference();
        Mapper mapper = getMapper();
        FileNameMapper implementation = mapper == null ? null : mapper.getImplementation();
        Map<String, Object> effectiveProperties = getEffectiveProperties();
        Set<String> propertyNames = getPropertyNames(effectiveProperties);
        HashMap map = new HashMap();
        for (String str : propertyNames) {
            Object obj = effectiveProperties.get(str);
            if (obj != null) {
                if (implementation != null && (strArrMapFileName = implementation.mapFileName(str)) != null) {
                    str = strArrMapFileName[0];
                }
                map.put(str, obj);
            }
        }
        return map;
    }

    private Map<String, Object> getEffectiveProperties() {
        Project project = getProject();
        Hashtable<String, Object> allSystemProperties = project == null ? getAllSystemProperties() : project.getProperties();
        Iterator<PropertySet> it = this.setRefs.iterator();
        while (it.hasNext()) {
            allSystemProperties.putAll(it.next().getPropertyMap());
        }
        return allSystemProperties;
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x000a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.Set<java.lang.String> getPropertyNames(java.util.Map<java.lang.String, java.lang.Object> r4) {
        /*
            r3 = this;
            boolean r0 = r3.getDynamic()
            if (r0 != 0) goto La
            java.util.Set<java.lang.String> r0 = r3.cachedNames
            if (r0 != 0) goto L49
        La:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r3.addPropertyNames(r0, r4)
            java.util.List<org.apache.tools.ant.types.PropertySet> r1 = r3.setRefs
            java.util.Iterator r1 = r1.iterator()
        L18:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L30
            java.lang.Object r2 = r1.next()
            org.apache.tools.ant.types.PropertySet r2 = (org.apache.tools.ant.types.PropertySet) r2
            java.util.Map r2 = r2.getPropertyMap()
            java.util.Set r2 = r2.keySet()
            r0.addAll(r2)
            goto L18
        L30:
            boolean r1 = r3.negate
            if (r1 == 0) goto L41
            java.util.HashSet r1 = new java.util.HashSet
            java.util.Set r4 = r4.keySet()
            r1.<init>(r4)
            r1.removeAll(r0)
            r0 = r1
        L41:
            boolean r4 = r3.getDynamic()
            if (r4 != 0) goto L49
            r3.cachedNames = r0
        L49:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.types.PropertySet.getPropertyNames(java.util.Map):java.util.Set");
    }

    private void addPropertyNames(Set<String> set, Map<String, Object> map) {
        if (isReference()) {
            getRef().addPropertyNames(set, map);
        }
        dieOnCircularReference();
        for (PropertyRef propertyRef : this.ptyRefs) {
            if (propertyRef.name != null) {
                if (map.get(propertyRef.name) != null) {
                    set.add(propertyRef.name);
                }
            } else if (propertyRef.prefix == null) {
                if (propertyRef.regex == null) {
                    if (propertyRef.builtin != null) {
                        if (!propertyRef.builtin.equals("all")) {
                            if (!propertyRef.builtin.equals("system")) {
                                if (propertyRef.builtin.equals("commandline")) {
                                    set.addAll(getProject().getUserProperties().keySet());
                                } else {
                                    throw new BuildException("Impossible: Invalid builtin attribute!");
                                }
                            } else {
                                set.addAll(getAllSystemProperties().keySet());
                            }
                        } else {
                            set.addAll(map.keySet());
                        }
                    } else {
                        throw new BuildException("Impossible: Invalid PropertyRef!");
                    }
                } else {
                    RegexpMatcher regexpMatcherNewRegexpMatcher = new RegexpMatcherFactory().newRegexpMatcher();
                    regexpMatcherNewRegexpMatcher.setPattern(propertyRef.regex);
                    for (String str : map.keySet()) {
                        if (regexpMatcherNewRegexpMatcher.matches(str)) {
                            set.add(str);
                        }
                    }
                }
            } else {
                for (String str2 : map.keySet()) {
                    if (str2.startsWith(propertyRef.prefix)) {
                        set.add(str2);
                    }
                }
            }
        }
    }

    protected PropertySet getRef() {
        return (PropertySet) getCheckedRef(PropertySet.class, "propertyset");
    }

    @Override // org.apache.tools.ant.types.DataType
    public final void setRefid(Reference reference) {
        if (!this.noAttributeSet) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    protected final void assertNotReference() {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.noAttributeSet = false;
    }

    public static class BuiltinPropertySetName extends EnumeratedAttribute {
        static final String ALL = "all";
        static final String COMMANDLINE = "commandline";
        static final String SYSTEM = "system";

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"all", SYSTEM, COMMANDLINE};
        }
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        if (isReference()) {
            return getRef().toString();
        }
        dieOnCircularReference();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : new TreeMap(getPropertyMap()).entrySet()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append((String) entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        if (isReference()) {
            return getRef().iterator();
        }
        dieOnCircularReference();
        Set<String> propertyNames = getPropertyNames(getEffectiveProperties());
        Mapper mapper = getMapper();
        final FileNameMapper implementation = mapper == null ? null : mapper.getImplementation();
        final Iterator<String> it = propertyNames.iterator();
        return new Iterator<Resource>() { // from class: org.apache.tools.ant.types.PropertySet.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override // java.util.Iterator
            public Resource next() {
                PropertyResource propertyResource = new PropertyResource(PropertySet.this.getProject(), (String) it.next());
                return implementation == null ? propertyResource : new MappedResource(propertyResource, implementation);
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        return isReference() ? getRef().size() : getProperties().size();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        if (isReference()) {
            return getRef().isFilesystemOnly();
        }
        dieOnCircularReference();
        return false;
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            Mapper mapper = this.mapper;
            if (mapper != null) {
                pushAndInvokeCircularReferenceCheck(mapper, stack, project);
            }
            Iterator<PropertySet> it = this.setRefs.iterator();
            while (it.hasNext()) {
                pushAndInvokeCircularReferenceCheck(it.next(), stack, project);
            }
            setChecked(true);
        }
    }
}

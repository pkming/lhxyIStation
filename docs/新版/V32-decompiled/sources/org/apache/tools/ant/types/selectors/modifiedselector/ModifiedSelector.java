package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.BaseExtendSelector;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ModifiedSelector extends BaseExtendSelector implements BuildListener, ResourceSelector {
    private static final String ALGORITHM_PREFIX = "algorithm.";
    private static final String CACHE_PREFIX = "cache.";
    private static final String COMPARATOR_PREFIX = "comparator.";
    private String algorithmClass;
    private String cacheClass;
    private String comparatorClass;
    private CacheName cacheName = null;
    private AlgorithmName algoName = null;
    private ComparatorName compName = null;
    private boolean update = true;
    private boolean selectDirectories = true;
    private boolean selectResourcesWithoutInputStream = true;
    private boolean delayUpdate = true;
    private Comparator<? super String> comparator = null;
    private Algorithm algorithm = null;
    private Cache cache = null;
    private int modified = 0;
    private boolean isConfigured = false;
    private Vector<Parameter> configParameter = new Vector<>();
    private Vector<Parameter> specialParameter = new Vector<>();
    private ClassLoader myClassLoader = null;
    private Path classpath = null;

    @Override // org.apache.tools.ant.BuildListener
    public void buildStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void messageLogged(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        configure();
        Cache cache = this.cache;
        if (cache == null) {
            setError("Cache must be set.");
            return;
        }
        if (this.algorithm == null) {
            setError("Algorithm must be set.");
        } else if (!cache.isValid()) {
            setError("Cache must be proper configured.");
        } else {
            if (this.algorithm.isValid()) {
                return;
            }
            setError("Algorithm must be proper configured.");
        }
    }

    public void configure() {
        File file;
        if (this.isConfigured) {
            return;
        }
        this.isConfigured = true;
        Project project = getProject();
        if (project != null) {
            file = new File(project.getBaseDir(), "cache.properties");
            getProject().addBuildListener(this);
        } else {
            file = new File("cache.properties");
            setDelayUpdate(false);
        }
        PropertiesfileCache propertiesfileCache = new PropertiesfileCache(file);
        DigestAlgorithm digestAlgorithm = new DigestAlgorithm();
        EqualComparator equalComparator = new EqualComparator();
        for (Parameter parameter : this.configParameter) {
            if (parameter.getName().indexOf(".") > 0) {
                this.specialParameter.add(parameter);
            } else {
                useParameter(parameter);
            }
        }
        this.configParameter = new Vector<>();
        AlgorithmName algorithmName = this.algoName;
        if (algorithmName != null) {
            if ("hashvalue".equals(algorithmName.getValue())) {
                this.algorithm = new HashvalueAlgorithm();
            } else if ("digest".equals(this.algoName.getValue())) {
                this.algorithm = new DigestAlgorithm();
            } else if ("checksum".equals(this.algoName.getValue())) {
                this.algorithm = new ChecksumAlgorithm();
            }
        } else {
            String str = this.algorithmClass;
            if (str != null) {
                this.algorithm = (Algorithm) loadClass(str, "is not an Algorithm.", Algorithm.class);
            } else {
                this.algorithm = digestAlgorithm;
            }
        }
        CacheName cacheName = this.cacheName;
        if (cacheName != null) {
            if ("propertyfile".equals(cacheName.getValue())) {
                this.cache = new PropertiesfileCache();
            }
        } else {
            String str2 = this.cacheClass;
            if (str2 != null) {
                this.cache = (Cache) loadClass(str2, "is not a Cache.", Cache.class);
            } else {
                this.cache = propertiesfileCache;
            }
        }
        ComparatorName comparatorName = this.compName;
        if (comparatorName != null) {
            if ("equal".equals(comparatorName.getValue())) {
                this.comparator = new EqualComparator();
            } else if ("rule".equals(this.compName.getValue())) {
                throw new BuildException("RuleBasedCollator not yet supported.");
            }
        } else {
            String str3 = this.comparatorClass;
            if (str3 != null) {
                this.comparator = (Comparator) loadClass(str3, "is not a Comparator.", Comparator.class);
            } else {
                this.comparator = equalComparator;
            }
        }
        Iterator<Parameter> it = this.specialParameter.iterator();
        while (it.hasNext()) {
            useParameter(it.next());
        }
        this.specialParameter = new Vector<>();
    }

    protected <T> T loadClass(String str, String str2, Class<? extends T> cls) {
        Class<?> cls2;
        try {
            ClassLoader classLoader = getClassLoader();
            if (classLoader != null) {
                cls2 = classLoader.loadClass(str);
            } else {
                cls2 = Class.forName(str);
            }
            T t = (T) cls2.newInstance();
            if (cls.isInstance(t)) {
                return t;
            }
            throw new BuildException("Specified class (" + str + ") " + str2);
        } catch (ClassNotFoundException unused) {
            throw new BuildException("Specified class (" + str + ") not found.");
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.types.resources.selectors.ResourceSelector
    public boolean isSelected(Resource resource) {
        if (resource.isFilesystemOnly()) {
            FileResource fileResource = (FileResource) resource;
            return isSelected(fileResource.getBaseDir(), fileResource.getName(), fileResource.getFile());
        }
        try {
            File fileCreateTempFile = FileUtils.getFileUtils().createTempFile("modified-", ".tmp", null, true, false);
            ResourceUtils.copyResource(resource, new FileResource(fileCreateTempFile));
            boolean zIsSelected = isSelected(fileCreateTempFile.getParentFile(), fileCreateTempFile.getName(), resource.toLongString());
            fileCreateTempFile.delete();
            return zIsSelected;
        } catch (UnsupportedOperationException unused) {
            log("The resource '" + resource.getName() + "' does not provide an InputStream, so it is not checked. Akkording to 'selres' attribute value it is " + (this.selectResourcesWithoutInputStream ? "" : " not") + "selected.", 2);
            return this.selectResourcesWithoutInputStream;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        return isSelected(file, str, file2.getAbsolutePath());
    }

    private boolean isSelected(File file, String str, String str2) {
        validate();
        File file2 = new File(file, str);
        if (file2.isDirectory()) {
            return this.selectDirectories;
        }
        String strValueOf = String.valueOf(this.cache.get(file2.getAbsolutePath()));
        String value = this.algorithm.getValue(file2);
        boolean z = this.comparator.compare(strValueOf, value) != 0;
        if (this.update && z) {
            this.cache.put(file2.getAbsolutePath(), value);
            setModified(getModified() + 1);
            if (!getDelayUpdate()) {
                saveCache();
            }
        }
        return z;
    }

    protected void saveCache() {
        if (getModified() > 0) {
            this.cache.save();
            setModified(0);
        }
    }

    public void setAlgorithmClass(String str) {
        this.algorithmClass = str;
    }

    public void setComparatorClass(String str) {
        this.comparatorClass = str;
    }

    public void setCacheClass(String str) {
        this.cacheClass = str;
    }

    public void setUpdate(boolean z) {
        this.update = z;
    }

    public void setSeldirs(boolean z) {
        this.selectDirectories = z;
    }

    public void setSelres(boolean z) {
        this.selectResourcesWithoutInputStream = z;
    }

    public int getModified() {
        return this.modified;
    }

    public void setModified(int i) {
        this.modified = i;
    }

    public boolean getDelayUpdate() {
        return this.delayUpdate;
    }

    public void setDelayUpdate(boolean z) {
        this.delayUpdate = z;
    }

    public void addClasspath(Path path) {
        if (this.classpath != null) {
            throw new BuildException("<classpath> can be set only once.");
        }
        this.classpath = path;
    }

    public ClassLoader getClassLoader() {
        if (this.myClassLoader == null) {
            this.myClassLoader = this.classpath == null ? getClass().getClassLoader() : getProject().createClassLoader(this.classpath);
        }
        return this.myClassLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.myClassLoader = classLoader;
    }

    public void addParam(String str, Object obj) {
        Parameter parameter = new Parameter();
        parameter.setName(str);
        parameter.setValue(String.valueOf(obj));
        this.configParameter.add(parameter);
    }

    public void addParam(Parameter parameter) {
        this.configParameter.add(parameter);
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.Parameterizable
    public void setParameters(Parameter[] parameterArr) {
        if (parameterArr != null) {
            for (Parameter parameter : parameterArr) {
                this.configParameter.add(parameter);
            }
        }
    }

    public void useParameter(Parameter parameter) {
        String name = parameter.getName();
        String value = parameter.getValue();
        if ("cache".equals(name)) {
            CacheName cacheName = new CacheName();
            cacheName.setValue(value);
            setCache(cacheName);
            return;
        }
        if ("algorithm".equals(name)) {
            AlgorithmName algorithmName = new AlgorithmName();
            algorithmName.setValue(value);
            setAlgorithm(algorithmName);
            return;
        }
        if ("comparator".equals(name)) {
            ComparatorName comparatorName = new ComparatorName();
            comparatorName.setValue(value);
            setComparator(comparatorName);
            return;
        }
        if ("update".equals(name)) {
            setUpdate("true".equalsIgnoreCase(value));
            return;
        }
        if ("delayupdate".equals(name)) {
            setDelayUpdate("true".equalsIgnoreCase(value));
            return;
        }
        if ("seldirs".equals(name)) {
            setSeldirs("true".equalsIgnoreCase(value));
            return;
        }
        if (name.startsWith(CACHE_PREFIX)) {
            tryToSetAParameter(this.cache, name.substring(6), value);
        } else if (name.startsWith(ALGORITHM_PREFIX)) {
            tryToSetAParameter(this.algorithm, name.substring(10), value);
        } else if (name.startsWith(COMPARATOR_PREFIX)) {
            tryToSetAParameter(this.comparator, name.substring(11), value);
        } else {
            setError("Invalid parameter " + name);
        }
    }

    protected void tryToSetAParameter(Object obj, String str, String str2) {
        Project project = getProject() != null ? getProject() : new Project();
        try {
            IntrospectionHelper.getHelper(project, obj.getClass()).setAttribute(project, obj, str, str2);
        } catch (BuildException unused) {
        }
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("{modifiedselector");
        stringBuffer.append(" update=").append(this.update);
        stringBuffer.append(" seldirs=").append(this.selectDirectories);
        stringBuffer.append(" cache=").append(this.cache);
        stringBuffer.append(" algorithm=").append(this.algorithm);
        stringBuffer.append(" comparator=").append(this.comparator);
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) {
        if (getDelayUpdate()) {
            saveCache();
        }
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
        if (getDelayUpdate()) {
            saveCache();
        }
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
        if (getDelayUpdate()) {
            saveCache();
        }
    }

    public Cache getCache() {
        return this.cache;
    }

    public void setCache(CacheName cacheName) {
        this.cacheName = cacheName;
    }

    public static class CacheName extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"propertyfile"};
        }
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(AlgorithmName algorithmName) {
        this.algoName = algorithmName;
    }

    public static class AlgorithmName extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"hashvalue", "digest", "checksum"};
        }
    }

    public Comparator<? super String> getComparator() {
        return this.comparator;
    }

    public void setComparator(ComparatorName comparatorName) {
        this.compName = comparatorName;
    }

    public static class ComparatorName extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"equal", "rule"};
        }
    }
}

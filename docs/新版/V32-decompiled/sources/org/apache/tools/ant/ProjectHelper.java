package org.apache.tools.ant;

import android.app.Instrumentation;
import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.LoaderUtils;
import org.xml.sax.AttributeList;

/* JADX INFO: loaded from: classes3.dex */
public class ProjectHelper {
    public static final String ANTLIB_URI = "antlib:";
    public static final String ANT_ATTRIBUTE_URI = "ant:attribute";
    public static final String ANT_CORE_URI = "antlib:org.apache.tools.ant";
    public static final String ANT_CURRENT_URI = "ant:current";
    public static final String ANT_TYPE = "ant-type";
    public static final String HELPER_PROPERTY = "org.apache.tools.ant.ProjectHelper";
    public static final String PROJECTHELPER_REFERENCE = "ant.projectHelper";
    public static final String SERVICE_ID = "META-INF/services/org.apache.tools.ant.ProjectHelper";
    public static final String USE_PROJECT_NAME_AS_TARGET_PREFIX = "USE_PROJECT_NAME_AS_TARGET_PREFIX";
    private static final ThreadLocal<String> targetPrefix = new ThreadLocal<>();
    private static final ThreadLocal<String> prefixSeparator = new ThreadLocal<String>() { // from class: org.apache.tools.ant.ProjectHelper.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public String initialValue() {
            return ".";
        }
    };
    private static final ThreadLocal<Boolean> inIncludeMode = new ThreadLocal<Boolean>() { // from class: org.apache.tools.ant.ProjectHelper.2
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Boolean initialValue() {
            return Boolean.FALSE;
        }
    };
    private Vector<Object> importStack = new Vector<>();
    private List<String[]> extensionStack = new LinkedList();

    public boolean canParseAntlibDescriptor(Resource resource) {
        return false;
    }

    public boolean canParseBuildFile(Resource resource) {
        return true;
    }

    public String getDefaultBuildFile() {
        return Main.DEFAULT_BUILD_FILENAME;
    }

    public static void configureProject(Project project, File file) throws BuildException {
        ProjectHelper projectHelperForBuildFile = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(new FileResource(file));
        project.addReference("ant.projectHelper", projectHelperForBuildFile);
        projectHelperForBuildFile.parse(project, file);
    }

    public static final class OnMissingExtensionPoint {
        public static final OnMissingExtensionPoint FAIL;
        public static final OnMissingExtensionPoint IGNORE;
        public static final OnMissingExtensionPoint WARN;
        private static final OnMissingExtensionPoint[] values;
        private final String name;

        static {
            OnMissingExtensionPoint onMissingExtensionPoint = new OnMissingExtensionPoint("fail");
            FAIL = onMissingExtensionPoint;
            OnMissingExtensionPoint onMissingExtensionPoint2 = new OnMissingExtensionPoint("warn");
            WARN = onMissingExtensionPoint2;
            OnMissingExtensionPoint onMissingExtensionPoint3 = new OnMissingExtensionPoint(Definer.OnError.POLICY_IGNORE);
            IGNORE = onMissingExtensionPoint3;
            values = new OnMissingExtensionPoint[]{onMissingExtensionPoint, onMissingExtensionPoint2, onMissingExtensionPoint3};
        }

        private OnMissingExtensionPoint(String str) {
            this.name = str;
        }

        public String name() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public static OnMissingExtensionPoint valueOf(String str) {
            Objects.requireNonNull(str);
            int i = 0;
            while (true) {
                OnMissingExtensionPoint[] onMissingExtensionPointArr = values;
                if (i < onMissingExtensionPointArr.length) {
                    if (str.equals(onMissingExtensionPointArr[i].name())) {
                        return onMissingExtensionPointArr[i];
                    }
                    i++;
                } else {
                    throw new IllegalArgumentException("Unknown onMissingExtensionPoint " + str);
                }
            }
        }
    }

    public Vector<Object> getImportStack() {
        return this.importStack;
    }

    public List<String[]> getExtensionStack() {
        return this.extensionStack;
    }

    public static String getCurrentTargetPrefix() {
        return targetPrefix.get();
    }

    public static void setCurrentTargetPrefix(String str) {
        targetPrefix.set(str);
    }

    public static String getCurrentPrefixSeparator() {
        return prefixSeparator.get();
    }

    public static void setCurrentPrefixSeparator(String str) {
        prefixSeparator.set(str);
    }

    public static boolean isInIncludeMode() {
        return Boolean.TRUE.equals(inIncludeMode.get());
    }

    public static void setInIncludeMode(boolean z) {
        inIncludeMode.set(Boolean.valueOf(z));
    }

    public void parse(Project project, Object obj) throws BuildException {
        throw new BuildException("ProjectHelper.parse() must be implemented in a helper plugin " + getClass().getName());
    }

    public static ProjectHelper getProjectHelper() {
        return ProjectHelperRepository.getInstance().getHelpers().next();
    }

    public static ClassLoader getContextClassLoader() {
        if (LoaderUtils.isContextLoaderAvailable()) {
            return LoaderUtils.getContextClassLoader();
        }
        return null;
    }

    public static void configure(Object obj, AttributeList attributeList, Project project) throws BuildException {
        if (obj instanceof TypeAdapter) {
            obj = ((TypeAdapter) obj).getProxy();
        }
        IntrospectionHelper helper = IntrospectionHelper.getHelper(project, obj.getClass());
        int length = attributeList.getLength();
        for (int i = 0; i < length; i++) {
            try {
                helper.setAttribute(project, obj, attributeList.getName(i).toLowerCase(Locale.ENGLISH), replaceProperties(project, attributeList.getValue(i), project.getProperties()));
            } catch (BuildException e) {
                if (!attributeList.getName(i).equals(Instrumentation.REPORT_KEY_IDENTIFIER)) {
                    throw e;
                }
            }
        }
    }

    public static void addText(Project project, Object obj, char[] cArr, int i, int i2) throws BuildException {
        addText(project, obj, new String(cArr, i, i2));
    }

    public static void addText(Project project, Object obj, String str) throws BuildException {
        if (str == null) {
            return;
        }
        if (obj instanceof TypeAdapter) {
            obj = ((TypeAdapter) obj).getProxy();
        }
        IntrospectionHelper.getHelper(project, obj.getClass()).addText(project, obj, str);
    }

    public static void storeChild(Project project, Object obj, Object obj2, String str) {
        IntrospectionHelper.getHelper(project, obj.getClass()).storeElement(project, obj, obj2, str);
    }

    public static String replaceProperties(Project project, String str) throws BuildException {
        return project.replaceProperties(str);
    }

    public static String replaceProperties(Project project, String str, Hashtable<String, Object> hashtable) throws BuildException {
        return PropertyHelper.getPropertyHelper(project).replaceProperties(null, str, hashtable);
    }

    public static void parsePropertyString(String str, Vector<String> vector, Vector<String> vector2) throws BuildException {
        PropertyHelper.parsePropertyStringDefault(str, vector, vector2);
    }

    public static String genComponentName(String str, String str2) {
        return (str == null || str.equals("") || str.equals(ANT_CORE_URI)) ? str2 : str + ":" + str2;
    }

    public static String extractUriFromComponentName(String str) {
        int iLastIndexOf;
        return (str == null || (iLastIndexOf = str.lastIndexOf(58)) == -1) ? "" : str.substring(0, iLastIndexOf);
    }

    public static String extractNameFromComponentName(String str) {
        int iLastIndexOf = str.lastIndexOf(58);
        return iLastIndexOf == -1 ? str : str.substring(iLastIndexOf + 1);
    }

    public static String nsToComponentName(String str) {
        return "attribute namespace:" + str;
    }

    public static BuildException addLocationToBuildException(BuildException buildException, Location location) {
        if (buildException.getLocation() == null || buildException.getMessage() == null) {
            return buildException;
        }
        String str = "The following error occurred while executing this line:" + System.getProperty("line.separator") + buildException.getLocation().toString() + buildException.getMessage();
        if (location == null) {
            return new BuildException(str, buildException);
        }
        return new BuildException(str, buildException, location);
    }

    public UnknownElement parseAntlibDescriptor(Project project, Resource resource) {
        throw new BuildException("can't parse antlib descriptors");
    }

    public void resolveExtensionOfAttributes(Project project) throws BuildException {
        Target target;
        for (String[] strArr : getExtensionStack()) {
            String str = strArr[0];
            String str2 = strArr[1];
            OnMissingExtensionPoint onMissingExtensionPointValueOf = OnMissingExtensionPoint.valueOf(strArr[2]);
            String str3 = strArr.length > 3 ? strArr[3] : null;
            Hashtable<String, Target> targets = project.getTargets();
            if (str3 == null || (target = targets.get(str3 + str)) == null) {
                target = targets.get(str);
            }
            if (target == null) {
                String str4 = "can't add target " + str2 + " to extension-point " + str + " because the extension-point is unknown.";
                if (onMissingExtensionPointValueOf == OnMissingExtensionPoint.FAIL) {
                    throw new BuildException(str4);
                }
                if (onMissingExtensionPointValueOf == OnMissingExtensionPoint.WARN) {
                    project.log(targets.get(str2), "Warning: " + str4, 1);
                }
            } else {
                if (!(target instanceof ExtensionPoint)) {
                    throw new BuildException("referenced target " + str + " is not an extension-point");
                }
                target.addDependency(str2);
            }
        }
    }
}

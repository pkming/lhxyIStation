package org.apache.tools.ant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.LoaderUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ProjectHelperRepository {
    private static Constructor<ProjectHelper2> PROJECTHELPER2_CONSTRUCTOR;
    private List<Constructor<? extends ProjectHelper>> helpers = new ArrayList();
    private static final String DEBUG_PROJECT_HELPER_REPOSITORY = "ant.project-helper-repo.debug";
    private static final boolean DEBUG = "true".equals(System.getProperty(DEBUG_PROJECT_HELPER_REPOSITORY));
    private static ProjectHelperRepository instance = new ProjectHelperRepository();

    static {
        try {
            PROJECTHELPER2_CONSTRUCTOR = ProjectHelper2.class.getConstructor(new Class[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ProjectHelperRepository getInstance() {
        return instance;
    }

    private ProjectHelperRepository() {
        collectProjectHelpers();
    }

    private void collectProjectHelpers() {
        registerProjectHelper(getProjectHelperBySystemProperty());
        try {
            ClassLoader contextClassLoader = LoaderUtils.getContextClassLoader();
            if (contextClassLoader != null) {
                Enumeration<URL> resources = contextClassLoader.getResources("META-INF/services/org.apache.tools.ant.ProjectHelper");
                while (resources.hasMoreElements()) {
                    URLConnection uRLConnectionOpenConnection = resources.nextElement().openConnection();
                    uRLConnectionOpenConnection.setUseCaches(false);
                    registerProjectHelper(getProjectHelperByService(uRLConnectionOpenConnection.getInputStream()));
                }
            }
            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("META-INF/services/org.apache.tools.ant.ProjectHelper");
            if (systemResourceAsStream != null) {
                registerProjectHelper(getProjectHelperByService(systemResourceAsStream));
            }
        } catch (Exception e) {
            System.err.println("Unable to load ProjectHelper from service META-INF/services/org.apache.tools.ant.ProjectHelper (" + e.getClass().getName() + ": " + e.getMessage() + ")");
            if (DEBUG) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void registerProjectHelper(String str) throws BuildException {
        registerProjectHelper(getHelperConstructor(str));
    }

    public void registerProjectHelper(Class<? extends ProjectHelper> cls) throws BuildException {
        try {
            registerProjectHelper(cls.getConstructor(new Class[0]));
        } catch (NoSuchMethodException unused) {
            throw new BuildException("Couldn't find no-arg constructor in " + cls.getName());
        }
    }

    private void registerProjectHelper(Constructor<? extends ProjectHelper> constructor) {
        if (constructor == null) {
            return;
        }
        if (DEBUG) {
            System.out.println("ProjectHelper " + constructor.getClass().getName() + " registered.");
        }
        this.helpers.add(constructor);
    }

    private Constructor<? extends ProjectHelper> getProjectHelperBySystemProperty() {
        String property = System.getProperty("org.apache.tools.ant.ProjectHelper");
        if (property == null) {
            return null;
        }
        try {
            return getHelperConstructor(property);
        } catch (SecurityException e) {
            System.err.println("Unable to load ProjectHelper class \"" + property + " specified in system property org.apache.tools.ant.ProjectHelper (" + e.getMessage() + ")");
            if (!DEBUG) {
                return null;
            }
            e.printStackTrace(System.err);
            return null;
        }
    }

    private Constructor<? extends ProjectHelper> getProjectHelperByService(InputStream inputStream) {
        InputStreamReader inputStreamReader;
        try {
            try {
                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            } catch (UnsupportedEncodingException unused) {
                inputStreamReader = new InputStreamReader(inputStream);
            }
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            bufferedReader.close();
            if (line == null || "".equals(line)) {
                return null;
            }
            return getHelperConstructor(line);
        } catch (Exception e) {
            System.out.println("Unable to load ProjectHelper from service META-INF/services/org.apache.tools.ant.ProjectHelper (" + e.getMessage() + ")");
            if (!DEBUG) {
                return null;
            }
            e.printStackTrace(System.err);
            return null;
        }
    }

    private Constructor<? extends ProjectHelper> getHelperConstructor(String str) throws BuildException {
        Class<?> clsLoadClass;
        ClassLoader contextClassLoader = LoaderUtils.getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                try {
                    clsLoadClass = contextClassLoader.loadClass(str);
                } catch (Exception e) {
                    throw new BuildException(e);
                }
            } catch (ClassNotFoundException unused) {
                clsLoadClass = null;
            }
        } else {
            clsLoadClass = null;
        }
        if (clsLoadClass == null) {
            clsLoadClass = Class.forName(str);
        }
        return clsLoadClass.asSubclass(ProjectHelper.class).getConstructor(new Class[0]);
    }

    public ProjectHelper getProjectHelperForBuildFile(Resource resource) throws BuildException {
        Iterator<ProjectHelper> helpers = getHelpers();
        while (helpers.hasNext()) {
            ProjectHelper next = helpers.next();
            if (next.canParseBuildFile(resource)) {
                if (DEBUG) {
                    System.out.println("ProjectHelper " + next.getClass().getName() + " selected for the build file " + resource);
                }
                return next;
            }
        }
        throw new RuntimeException("BUG: at least the ProjectHelper2 should have supported the file " + resource);
    }

    public ProjectHelper getProjectHelperForAntlib(Resource resource) throws BuildException {
        Iterator<ProjectHelper> helpers = getHelpers();
        while (helpers.hasNext()) {
            ProjectHelper next = helpers.next();
            if (next.canParseAntlibDescriptor(resource)) {
                if (DEBUG) {
                    System.out.println("ProjectHelper " + next.getClass().getName() + " selected for the antlib " + resource);
                }
                return next;
            }
        }
        throw new RuntimeException("BUG: at least the ProjectHelper2 should have supported the file " + resource);
    }

    public Iterator<ProjectHelper> getHelpers() {
        return new ConstructingIterator(this.helpers.iterator());
    }

    private static class ConstructingIterator implements Iterator<ProjectHelper> {
        private boolean empty = false;
        private final Iterator<Constructor<? extends ProjectHelper>> nested;

        ConstructingIterator(Iterator<Constructor<? extends ProjectHelper>> it) {
            this.nested = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.nested.hasNext() || !this.empty;
        }

        @Override // java.util.Iterator
        public ProjectHelper next() {
            Constructor<? extends ProjectHelper> next;
            if (this.nested.hasNext()) {
                next = this.nested.next();
            } else {
                this.empty = true;
                next = ProjectHelperRepository.PROJECTHELPER2_CONSTRUCTOR;
            }
            try {
                return (ProjectHelper) next.newInstance(new Object[0]);
            } catch (Exception unused) {
                throw new BuildException("Failed to invoke no-arg constructor on " + next.getName());
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported");
        }
    }
}

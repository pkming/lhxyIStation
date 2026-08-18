package org.apache.tools.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.tools.ant.util.LoaderUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ArgumentProcessorRegistry {
    private static final String SERVICE_ID = "META-INF/services/org.apache.tools.ant.ArgumentProcessor";
    private List<ArgumentProcessor> processors = new ArrayList();
    private static final String DEBUG_ARGUMENT_PROCESSOR_REPOSITORY = "ant.argument-processor-repo.debug";
    private static final boolean DEBUG = "true".equals(System.getProperty(DEBUG_ARGUMENT_PROCESSOR_REPOSITORY));
    private static ArgumentProcessorRegistry instance = new ArgumentProcessorRegistry();

    public static ArgumentProcessorRegistry getInstance() {
        return instance;
    }

    private ArgumentProcessorRegistry() {
        collectArgumentProcessors();
    }

    public List<ArgumentProcessor> getProcessors() {
        return this.processors;
    }

    private void collectArgumentProcessors() {
        try {
            ClassLoader contextClassLoader = LoaderUtils.getContextClassLoader();
            if (contextClassLoader != null) {
                Enumeration<URL> resources = contextClassLoader.getResources(SERVICE_ID);
                while (resources.hasMoreElements()) {
                    URLConnection uRLConnectionOpenConnection = resources.nextElement().openConnection();
                    uRLConnectionOpenConnection.setUseCaches(false);
                    registerArgumentProcessor(getProcessorByService(uRLConnectionOpenConnection.getInputStream()));
                }
            }
            InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream(SERVICE_ID);
            if (systemResourceAsStream != null) {
                registerArgumentProcessor(getProcessorByService(systemResourceAsStream));
            }
        } catch (Exception e) {
            System.err.println("Unable to load ArgumentProcessor from service META-INF/services/org.apache.tools.ant.ArgumentProcessor (" + e.getClass().getName() + ": " + e.getMessage() + ")");
            if (DEBUG) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void registerArgumentProcessor(String str) throws BuildException {
        registerArgumentProcessor(getProcessor(str));
    }

    public void registerArgumentProcessor(Class<? extends ArgumentProcessor> cls) throws BuildException {
        registerArgumentProcessor(getProcessor(cls));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ArgumentProcessor getProcessor(String str) {
        try {
            return getProcessor((Class<? extends ArgumentProcessor>) Class.forName(str));
        } catch (ClassNotFoundException e) {
            throw new BuildException("Argument processor class " + str + " was not found", e);
        }
    }

    private ArgumentProcessor getProcessor(Class<? extends ArgumentProcessor> cls) {
        try {
            return cls.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            throw new BuildException("The argument processor class" + cls.getClass().getName() + " could not be instanciated with a default constructor", e);
        }
    }

    public void registerArgumentProcessor(ArgumentProcessor argumentProcessor) {
        if (argumentProcessor == null) {
            return;
        }
        this.processors.add(argumentProcessor);
        if (DEBUG) {
            System.out.println("Argument processor " + argumentProcessor.getClass().getName() + " registered.");
        }
    }

    private ArgumentProcessor getProcessorByService(InputStream inputStream) throws Throwable {
        InputStreamReader inputStreamReader;
        InputStreamReader inputStreamReader2 = null;
        try {
            try {
                inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            } catch (UnsupportedEncodingException unused) {
                inputStreamReader = new InputStreamReader(inputStream);
            }
        } catch (Throwable th) {
            th = th;
            try {
                inputStreamReader2.close();
            } catch (IOException unused2) {
            }
            throw th;
        }
        try {
            String line = new BufferedReader(inputStreamReader).readLine();
            if (line == null || "".equals(line)) {
                try {
                    inputStreamReader.close();
                } catch (IOException unused3) {
                }
                return null;
            }
            ArgumentProcessor processor = getProcessor(line);
            try {
                inputStreamReader.close();
            } catch (IOException unused4) {
            }
            return processor;
        } catch (Throwable th2) {
            th = th2;
            inputStreamReader2 = inputStreamReader;
            inputStreamReader2.close();
            throw th;
        }
    }
}

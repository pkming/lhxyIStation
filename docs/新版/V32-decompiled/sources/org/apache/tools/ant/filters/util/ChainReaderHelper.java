package org.apache.tools.ant.filters.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.AntFilterReader;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Parameterizable;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public final class ChainReaderHelper {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    public Reader primaryReader;
    public int bufferSize = 8192;
    public Vector<FilterChain> filterChains = new Vector<>();
    private Project project = null;

    public void setPrimaryReader(Reader reader) {
        this.primaryReader = reader;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public void setBufferSize(int i) {
        this.bufferSize = i;
    }

    public void setFilterChains(Vector<FilterChain> vector) {
        this.filterChains = vector;
    }

    public Reader getAssembledReader() throws BuildException {
        Reader readerChain = this.primaryReader;
        if (readerChain == null) {
            throw new BuildException("primaryReader must not be null.");
        }
        int size = this.filterChains.size();
        Vector vector = new Vector();
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < size; i++) {
            Vector<Object> filterReaders = this.filterChains.elementAt(i).getFilterReaders();
            int size2 = filterReaders.size();
            for (int i2 = 0; i2 < size2; i2++) {
                vector.addElement(filterReaders.elementAt(i2));
            }
        }
        int size3 = vector.size();
        if (size3 > 0) {
            for (int i3 = 0; i3 < size3; i3++) {
                try {
                    Object objElementAt = vector.elementAt(i3);
                    if (objElementAt instanceof AntFilterReader) {
                        readerChain = expandReader((AntFilterReader) vector.elementAt(i3), readerChain, arrayList);
                    } else if (objElementAt instanceof ChainableReader) {
                        setProjectOnObject(objElementAt);
                        readerChain = ((ChainableReader) objElementAt).chain(readerChain);
                        setProjectOnObject(readerChain);
                    }
                } catch (Throwable th) {
                    if (arrayList.size() > 0) {
                        cleanUpClassLoaders(arrayList);
                    }
                    throw th;
                }
            }
        }
        return arrayList.size() == 0 ? readerChain : new FilterReader(readerChain) { // from class: org.apache.tools.ant.filters.util.ChainReaderHelper.1
            @Override // java.io.FilterReader, java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                FileUtils.close(this.in);
                ChainReaderHelper.cleanUpClassLoaders(arrayList);
            }

            protected void finalize() throws Throwable {
                try {
                    close();
                } finally {
                    super.finalize();
                }
            }
        };
    }

    private void setProjectOnObject(Object obj) {
        Project project = this.project;
        if (project == null) {
            return;
        }
        if (obj instanceof BaseFilterReader) {
            ((BaseFilterReader) obj).setProject(project);
        } else {
            project.setProjectReference(obj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void cleanUpClassLoaders(List<AntClassLoader> list) {
        Iterator<AntClassLoader> it = list.iterator();
        while (it.hasNext()) {
            it.next().cleanup();
        }
    }

    public String readFully(Reader reader) throws IOException {
        return FileUtils.readFully(reader, this.bufferSize);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Reader expandReader(AntFilterReader antFilterReader, Reader reader, List<AntClassLoader> list) {
        Class<?> cls;
        boolean z;
        String className = antFilterReader.getClassName();
        Path classpath = antFilterReader.getClasspath();
        Project project = antFilterReader.getProject();
        if (className != null) {
            try {
                if (classpath == null) {
                    cls = Class.forName(className);
                } else {
                    AntClassLoader antClassLoaderCreateClassLoader = project.createClassLoader(classpath);
                    list.add(antClassLoaderCreateClassLoader);
                    cls = Class.forName(className, true, antClassLoaderCreateClassLoader);
                }
                if (cls != null) {
                    if (!FilterReader.class.isAssignableFrom(cls)) {
                        throw new BuildException(className + " does not extend java.io.FilterReader");
                    }
                    Constructor<?>[] constructors = cls.getConstructors();
                    int i = 0;
                    while (true) {
                        if (i >= constructors.length) {
                            z = false;
                            break;
                        }
                        Class<?>[] parameterTypes = constructors[i].getParameterTypes();
                        if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(Reader.class)) {
                            z = true;
                            break;
                        }
                        i++;
                    }
                    if (!z) {
                        throw new BuildException(className + " does not define a public constructor that takes in a Reader as its single argument.");
                    }
                    Reader reader2 = (Reader) constructors[i].newInstance(reader);
                    setProjectOnObject(reader2);
                    if (Parameterizable.class.isAssignableFrom(cls)) {
                        ((Parameterizable) reader2).setParameters(antFilterReader.getParams());
                    }
                    return reader2;
                }
            } catch (ClassNotFoundException e) {
                throw new BuildException(e);
            } catch (IllegalAccessException e2) {
                throw new BuildException(e2);
            } catch (InstantiationException e3) {
                throw new BuildException(e3);
            } catch (InvocationTargetException e4) {
                throw new BuildException(e4);
            }
        }
        return reader;
    }
}

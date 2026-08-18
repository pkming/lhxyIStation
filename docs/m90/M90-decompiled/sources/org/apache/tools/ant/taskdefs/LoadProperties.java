package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.JavaResource;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class LoadProperties extends Task {
    private Resource src = null;
    private final Vector<FilterChain> filterChains = new Vector<>();
    private String encoding = null;
    private String prefix = null;
    private boolean prefixValues = true;

    public final void setSrcFile(File file) {
        addConfigured(new FileResource(file));
    }

    public void setResource(String str) {
        getRequiredJavaResource().setName(str);
    }

    public final void setEncoding(String str) {
        this.encoding = str;
    }

    public void setClasspath(Path path) {
        getRequiredJavaResource().setClasspath(path);
    }

    public Path createClasspath() {
        return getRequiredJavaResource().createClasspath();
    }

    public void setClasspathRef(Reference reference) {
        getRequiredJavaResource().setClasspathRef(reference);
    }

    public Path getClasspath() {
        return getRequiredJavaResource().getClasspath();
    }

    public void setPrefix(String str) {
        this.prefix = str;
    }

    public void setPrefixValues(boolean z) {
        this.prefixValues = z;
    }

    @Override // org.apache.tools.ant.Task
    public final void execute() throws Throwable {
        ByteArrayInputStream byteArrayInputStream;
        BufferedInputStream bufferedInputStream;
        BufferedInputStream bufferedInputStream2;
        InputStreamReader inputStreamReader;
        Resource resource = this.src;
        if (resource == null) {
            throw new BuildException("A source resource is required.");
        }
        if (!resource.isExists()) {
            if (this.src instanceof JavaResource) {
                log("Unable to find resource " + this.src, 1);
                return;
            }
            throw new BuildException("Source resource does not exist: " + this.src);
        }
        InputStream inputStream = null;
        try {
            BufferedInputStream bufferedInputStream3 = new BufferedInputStream(this.src.getInputStream());
            try {
                if (this.encoding == null) {
                    inputStreamReader = new InputStreamReader(bufferedInputStream3);
                } else {
                    inputStreamReader = new InputStreamReader(bufferedInputStream3, this.encoding);
                }
                ChainReaderHelper chainReaderHelper = new ChainReaderHelper();
                chainReaderHelper.setPrimaryReader(inputStreamReader);
                chainReaderHelper.setFilterChains(this.filterChains);
                chainReaderHelper.setProject(getProject());
                String fully = chainReaderHelper.readFully(chainReaderHelper.getAssembledReader());
                if (fully != null && fully.length() != 0) {
                    if (!fully.endsWith("\n")) {
                        fully = fully + "\n";
                    }
                    ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(fully.getBytes("ISO-8859-1"));
                    try {
                        Properties properties = new Properties();
                        properties.load(byteArrayInputStream2);
                        Property property = new Property();
                        property.bindToOwner(this);
                        property.setPrefix(this.prefix);
                        property.setPrefixValues(this.prefixValues);
                        property.addProperties(properties);
                        inputStream = byteArrayInputStream2;
                    } catch (IOException e) {
                        bufferedInputStream2 = bufferedInputStream3;
                        byteArrayInputStream = byteArrayInputStream2;
                        e = e;
                        inputStream = bufferedInputStream2;
                        try {
                            throw new BuildException("Unable to load file: " + e, e, getLocation());
                        } catch (Throwable th) {
                            th = th;
                            FileUtils.close(inputStream);
                            FileUtils.close(byteArrayInputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        bufferedInputStream = bufferedInputStream3;
                        byteArrayInputStream = byteArrayInputStream2;
                        th = th2;
                        inputStream = bufferedInputStream;
                        FileUtils.close(inputStream);
                        FileUtils.close(byteArrayInputStream);
                        throw th;
                    }
                }
                FileUtils.close(bufferedInputStream3);
                FileUtils.close(inputStream);
            } catch (IOException e2) {
                e = e2;
                bufferedInputStream2 = bufferedInputStream3;
                byteArrayInputStream = null;
            } catch (Throwable th3) {
                th = th3;
                bufferedInputStream = bufferedInputStream3;
                byteArrayInputStream = null;
            }
        } catch (IOException e3) {
            e = e3;
            byteArrayInputStream = null;
        } catch (Throwable th4) {
            th = th4;
            byteArrayInputStream = null;
        }
    }

    public final void addFilterChain(FilterChain filterChain) {
        this.filterChains.addElement(filterChain);
    }

    public synchronized void addConfigured(ResourceCollection resourceCollection) {
        if (this.src != null) {
            throw new BuildException("only a single source is supported");
        }
        if (resourceCollection.size() != 1) {
            throw new BuildException("only single-element resource collections are supported");
        }
        this.src = resourceCollection.iterator().next();
    }

    private synchronized JavaResource getRequiredJavaResource() {
        Resource resource = this.src;
        if (resource == null) {
            JavaResource javaResource = new JavaResource();
            this.src = javaResource;
            javaResource.setProject(getProject());
        } else if (!(resource instanceof JavaResource)) {
            throw new BuildException("expected a java resource as source");
        }
        return (JavaResource) this.src;
    }
}

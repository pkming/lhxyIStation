package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class LoadResource extends Task {
    private Resource src;
    private boolean failOnError = true;
    private boolean quiet = false;
    private String encoding = null;
    private String property = null;
    private final Vector<FilterChain> filterChains = new Vector<>();

    public final void setEncoding(String str) {
        this.encoding = str;
    }

    public final void setProperty(String str) {
        this.property = str;
    }

    public final void setFailonerror(boolean z) {
        this.failOnError = z;
    }

    public void setQuiet(boolean z) {
        this.quiet = z;
        if (z) {
            this.failOnError = false;
        }
    }

    @Override // org.apache.tools.ant.Task
    public final void execute() throws BuildException {
        InputStreamReader inputStreamReader;
        Resource resource = this.src;
        if (resource == null) {
            throw new BuildException("source resource not defined");
        }
        if (this.property == null) {
            throw new BuildException("output property not defined");
        }
        if (this.quiet && this.failOnError) {
            throw new BuildException("quiet and failonerror cannot both be set to true");
        }
        if (!resource.isExists()) {
            String str = this.src + " doesn't exist";
            if (this.failOnError) {
                throw new BuildException(str);
            }
            log(str, this.quiet ? 1 : 0);
            return;
        }
        InputStream inputStream = null;
        int i = 3;
        log("loading " + this.src + " into property " + this.property, 3);
        try {
            try {
                long size = this.src.getSize();
                log("resource size = " + (size != -1 ? String.valueOf(size) : "unknown"), 4);
                int i2 = (int) size;
                inputStream = this.src.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                if (this.encoding == null) {
                    inputStreamReader = new InputStreamReader(bufferedInputStream);
                } else {
                    inputStreamReader = new InputStreamReader(bufferedInputStream, this.encoding);
                }
                String fully = "";
                if (i2 != 0) {
                    ChainReaderHelper chainReaderHelper = new ChainReaderHelper();
                    if (size != -1) {
                        chainReaderHelper.setBufferSize(i2);
                    }
                    chainReaderHelper.setPrimaryReader(inputStreamReader);
                    chainReaderHelper.setFilterChains(this.filterChains);
                    chainReaderHelper.setProject(getProject());
                    fully = chainReaderHelper.readFully(chainReaderHelper.getAssembledReader());
                } else {
                    log("Do not set property " + this.property + " as its length is 0.", this.quiet ? 3 : 2);
                }
                if (fully != null && fully.length() > 0) {
                    getProject().setNewProperty(this.property, fully);
                    log("loaded " + fully.length() + " characters", 3);
                    log(this.property + " := " + fully, 4);
                }
            } catch (IOException e) {
                String str2 = "Unable to load resource: " + e.toString();
                if (this.failOnError) {
                    throw new BuildException(str2, e, getLocation());
                }
                if (!this.quiet) {
                    i = 0;
                }
                log(str2, i);
            } catch (BuildException e2) {
                if (this.failOnError) {
                    throw e2;
                }
                String message = e2.getMessage();
                if (!this.quiet) {
                    i = 0;
                }
                log(message, i);
            }
        } finally {
            FileUtils.close(inputStream);
        }
    }

    public final void addFilterChain(FilterChain filterChain) {
        this.filterChains.addElement(filterChain);
    }

    public void addConfigured(ResourceCollection resourceCollection) {
        if (resourceCollection.size() != 1) {
            throw new BuildException("only single argument resource collections are supported");
        }
        this.src = resourceCollection.iterator().next();
    }
}

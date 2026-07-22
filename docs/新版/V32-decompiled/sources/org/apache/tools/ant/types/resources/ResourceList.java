package org.apache.tools.ant.types.resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ResourceList extends DataType implements ResourceCollection {
    private volatile boolean cached;
    private final Union cachedResources;
    private String encoding;
    private final Vector<FilterChain> filterChains = new Vector<>();
    private final ArrayList<ResourceCollection> textDocuments = new ArrayList<>();

    public ResourceList() {
        Union union = new Union();
        this.cachedResources = union;
        this.cached = false;
        this.encoding = null;
        union.setCache(true);
    }

    public void add(ResourceCollection resourceCollection) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.textDocuments.add(resourceCollection);
        setChecked(false);
    }

    public final void addFilterChain(FilterChain filterChain) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.filterChains.add(filterChain);
        setChecked(false);
    }

    public final void setEncoding(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.encoding = str;
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (this.encoding != null) {
            throw tooManyAttributes();
        }
        if (this.filterChains.size() > 0 || this.textDocuments.size() > 0) {
            throw noChildrenAllowed();
        }
        super.setRefid(reference);
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public final synchronized Iterator<Resource> iterator() {
        if (isReference()) {
            return ((ResourceList) getCheckedRef()).iterator();
        }
        return cache().iterator();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized int size() {
        if (isReference()) {
            return ((ResourceList) getCheckedRef()).size();
        }
        return cache().size();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public synchronized boolean isFilesystemOnly() {
        if (isReference()) {
            return ((ResourceList) getCheckedRef()).isFilesystemOnly();
        }
        return cache().isFilesystemOnly();
    }

    @Override // org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            for (Object obj : this.textDocuments) {
                if (obj instanceof DataType) {
                    pushAndInvokeCircularReferenceCheck((DataType) obj, stack, project);
                }
            }
            Iterator<FilterChain> it = this.filterChains.iterator();
            while (it.hasNext()) {
                pushAndInvokeCircularReferenceCheck(it.next(), stack, project);
            }
            setChecked(true);
        }
    }

    private synchronized ResourceCollection cache() {
        if (!this.cached) {
            dieOnCircularReference();
            Iterator<ResourceCollection> it = this.textDocuments.iterator();
            while (it.hasNext()) {
                Iterator<Resource> it2 = it.next().iterator();
                while (it2.hasNext()) {
                    this.cachedResources.add(read(it2.next()));
                }
            }
            this.cached = true;
        }
        return this.cachedResources;
    }

    /* JADX WARN: Not initialized variable reg: 1, insn: 0x0087: MOVE (r0 I:??[OBJECT, ARRAY]) = (r1 I:??[OBJECT, ARRAY]), block:B:24:0x0087 */
    private ResourceCollection read(Resource resource) throws Throwable {
        IOException e;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        InputStream inputStream2 = null;
        try {
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(resource.getInputStream());
                try {
                    if (this.encoding == null) {
                        inputStreamReader = new InputStreamReader(bufferedInputStream);
                    } else {
                        inputStreamReader = new InputStreamReader(bufferedInputStream, this.encoding);
                    }
                    ChainReaderHelper chainReaderHelper = new ChainReaderHelper();
                    chainReaderHelper.setPrimaryReader(inputStreamReader);
                    chainReaderHelper.setFilterChains(this.filterChains);
                    chainReaderHelper.setProject(getProject());
                    BufferedReader bufferedReader = new BufferedReader(chainReaderHelper.getAssembledReader());
                    Union union = new Union();
                    union.setCache(true);
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line != null) {
                            union.add(parse(line));
                        } else {
                            FileUtils.close(bufferedInputStream);
                            return union;
                        }
                    }
                } catch (IOException e2) {
                    e = e2;
                    throw new BuildException("Unable to read resource " + resource.getName() + ": " + e, e, getLocation());
                }
            } catch (Throwable th) {
                th = th;
                inputStream2 = inputStream;
                FileUtils.close(inputStream2);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (Throwable th2) {
            th = th2;
            FileUtils.close(inputStream2);
            throw th;
        }
    }

    private Resource parse(String str) {
        Object properties = PropertyHelper.getPropertyHelper(getProject()).parseProperties(str);
        if (properties instanceof Resource) {
            return (Resource) properties;
        }
        String string = properties.toString();
        if (string.indexOf(":") != -1) {
            try {
                return new URLResource(string);
            } catch (BuildException unused) {
            }
        }
        return new FileResource(getProject(), string);
    }
}

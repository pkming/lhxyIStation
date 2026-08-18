package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.types.Mapper;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ContainerMapper implements FileNameMapper {
    private List mappers = new ArrayList();

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) {
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
    }

    public void addConfiguredMapper(Mapper mapper) {
        add(mapper.getImplementation());
    }

    public void addConfigured(FileNameMapper fileNameMapper) {
        add(fileNameMapper);
    }

    public synchronized void add(FileNameMapper fileNameMapper) {
        if (this != fileNameMapper) {
            if (!(fileNameMapper instanceof ContainerMapper) || !((ContainerMapper) fileNameMapper).contains(this)) {
                this.mappers.add(fileNameMapper);
            }
        }
        throw new IllegalArgumentException("Circular mapper containment condition detected");
    }

    protected synchronized boolean contains(FileNameMapper fileNameMapper) {
        boolean z;
        Iterator it = this.mappers.iterator();
        loop0: while (true) {
            z = false;
            while (it.hasNext() && !z) {
                FileNameMapper fileNameMapper2 = (FileNameMapper) it.next();
                if (fileNameMapper2 == fileNameMapper || ((fileNameMapper2 instanceof ContainerMapper) && ((ContainerMapper) fileNameMapper2).contains(fileNameMapper))) {
                    z = true;
                }
            }
        }
        return z;
    }

    public synchronized List getMappers() {
        return Collections.unmodifiableList(this.mappers);
    }
}

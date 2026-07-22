package org.apache.tools.ant.types;

import android.telephony.PhoneNumberUtils;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileResourceIterator;

/* JADX INFO: loaded from: classes3.dex */
public class DirSet extends AbstractFileSet implements ResourceCollection {
    @Override // org.apache.tools.ant.types.ResourceCollection
    public boolean isFilesystemOnly() {
        return true;
    }

    public DirSet() {
    }

    protected DirSet(DirSet dirSet) {
        super(dirSet);
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        if (isReference()) {
            return ((DirSet) getRef(getProject())).clone();
        }
        return super.clone();
    }

    @Override // org.apache.tools.ant.types.ResourceCollection, java.lang.Iterable
    public Iterator<Resource> iterator() {
        if (isReference()) {
            return ((DirSet) getRef(getProject())).iterator();
        }
        return new FileResourceIterator(getProject(), getDir(getProject()), getDirectoryScanner(getProject()).getIncludedDirectories());
    }

    @Override // org.apache.tools.ant.types.ResourceCollection
    public int size() {
        if (isReference()) {
            return ((DirSet) getRef(getProject())).size();
        }
        return getDirectoryScanner(getProject()).getIncludedDirsCount();
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType
    public String toString() {
        String[] includedDirectories = getDirectoryScanner(getProject()).getIncludedDirectories();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < includedDirectories.length; i++) {
            if (i > 0) {
                stringBuffer.append(PhoneNumberUtils.WAIT);
            }
            stringBuffer.append(includedDirectories[i]);
        }
        return stringBuffer.toString();
    }
}

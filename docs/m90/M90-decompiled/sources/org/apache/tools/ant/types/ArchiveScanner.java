package org.apache.tools.ant.types;

import android.net.LinkQualityInfo;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.selectors.SelectorUtils;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ArchiveScanner extends DirectoryScanner {
    private String encoding;
    private Resource lastScannedResource;
    private Resource src;
    protected File srcFile;
    private Map<String, Resource> fileEntries = new TreeMap();
    private Map<String, Resource> dirEntries = new TreeMap();
    private Map<String, Resource> matchFileEntries = new TreeMap();
    private Map<String, Resource> matchDirEntries = new TreeMap();
    private boolean errorOnMissingArchive = true;

    protected abstract void fillMapsFromArchive(Resource resource, String str, Map<String, Resource> map, Map<String, Resource> map2, Map<String, Resource> map3, Map<String, Resource> map4);

    public void setErrorOnMissingArchive(boolean z) {
        this.errorOnMissingArchive = z;
    }

    @Override // org.apache.tools.ant.DirectoryScanner, org.apache.tools.ant.FileScanner
    public void scan() {
        Resource resource = this.src;
        if (resource != null) {
            if (resource.isExists() || this.errorOnMissingArchive) {
                super.scan();
            }
        }
    }

    public void setSrc(File file) {
        setSrc(new FileResource(file));
    }

    public void setSrc(Resource resource) {
        this.src = resource;
        FileProvider fileProvider = (FileProvider) resource.as(FileProvider.class);
        if (fileProvider != null) {
            this.srcFile = fileProvider.getFile();
        }
    }

    public void setEncoding(String str) {
        this.encoding = str;
    }

    @Override // org.apache.tools.ant.DirectoryScanner, org.apache.tools.ant.FileScanner
    public String[] getIncludedFiles() {
        if (this.src == null) {
            return super.getIncludedFiles();
        }
        scanme();
        return (String[]) this.matchFileEntries.keySet().toArray(new String[this.matchFileEntries.size()]);
    }

    @Override // org.apache.tools.ant.DirectoryScanner
    public int getIncludedFilesCount() {
        if (this.src == null) {
            return super.getIncludedFilesCount();
        }
        scanme();
        return this.matchFileEntries.size();
    }

    @Override // org.apache.tools.ant.DirectoryScanner, org.apache.tools.ant.FileScanner
    public String[] getIncludedDirectories() {
        if (this.src == null) {
            return super.getIncludedDirectories();
        }
        scanme();
        return (String[]) this.matchDirEntries.keySet().toArray(new String[this.matchDirEntries.size()]);
    }

    @Override // org.apache.tools.ant.DirectoryScanner
    public int getIncludedDirsCount() {
        if (this.src == null) {
            return super.getIncludedDirsCount();
        }
        scanme();
        return this.matchDirEntries.size();
    }

    Iterator<Resource> getResourceFiles(Project project) {
        if (this.src == null) {
            return new FileResourceIterator(project, getBasedir(), getIncludedFiles());
        }
        scanme();
        return this.matchFileEntries.values().iterator();
    }

    Iterator<Resource> getResourceDirectories(Project project) {
        if (this.src == null) {
            return new FileResourceIterator(project, getBasedir(), getIncludedDirectories());
        }
        scanme();
        return this.matchDirEntries.values().iterator();
    }

    public void init() {
        if (this.includes == null) {
            this.includes = new String[1];
            this.includes[0] = SelectorUtils.DEEP_TREE_MATCH;
        }
        if (this.excludes == null) {
            this.excludes = new String[0];
        }
    }

    public boolean match(String str) {
        if (str.length() > 0) {
            str = str.replace('/', File.separatorChar).replace('\\', File.separatorChar);
            if (str.charAt(0) == File.separatorChar) {
                str = str.substring(1);
            }
        }
        return isIncluded(str) && !isExcluded(str);
    }

    @Override // org.apache.tools.ant.DirectoryScanner, org.apache.tools.ant.types.ResourceFactory
    public Resource getResource(String str) {
        if (this.src == null) {
            return super.getResource(str);
        }
        if (str.equals("")) {
            return new Resource("", true, LinkQualityInfo.UNKNOWN_LONG, true);
        }
        scanme();
        if (this.fileEntries.containsKey(str)) {
            return this.fileEntries.get(str);
        }
        String strTrimSeparator = trimSeparator(str);
        if (this.dirEntries.containsKey(strTrimSeparator)) {
            return this.dirEntries.get(strTrimSeparator);
        }
        return new Resource(strTrimSeparator);
    }

    private void scanme() {
        if (this.src.isExists() || this.errorOnMissingArchive) {
            Resource resource = new Resource(this.src.getName(), this.src.isExists(), this.src.getLastModified());
            Resource resource2 = this.lastScannedResource;
            if (resource2 != null && resource2.getName().equals(resource.getName()) && this.lastScannedResource.getLastModified() == resource.getLastModified()) {
                return;
            }
            init();
            this.fileEntries.clear();
            this.dirEntries.clear();
            this.matchFileEntries.clear();
            this.matchDirEntries.clear();
            fillMapsFromArchive(this.src, this.encoding, this.fileEntries, this.matchFileEntries, this.dirEntries, this.matchDirEntries);
            this.lastScannedResource = resource;
        }
    }

    protected static final String trimSeparator(String str) {
        return str.endsWith("/") ? str.substring(0, str.length() - 1) : str;
    }
}

package org.apache.tools.ant.types;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.VectorSet;

/* JADX INFO: loaded from: classes3.dex */
public class FilterSet extends DataType implements Cloneable {
    public static final String DEFAULT_TOKEN_END = "@";
    public static final String DEFAULT_TOKEN_START = "@";
    private boolean duplicateToken;
    private String endOfToken;
    private Hashtable<String, String> filterHash;
    private Vector<Filter> filters;
    private Vector<File> filtersFiles;
    private OnMissing onMissingFiltersFile;
    private Vector<String> passedTokens;
    private boolean readingFiles;
    private boolean recurse;
    private int recurseDepth;
    private String startOfToken;

    public static class Filter {
        String token;
        String value;

        public Filter(String str, String str2) {
            setToken(str);
            setValue(str2);
        }

        public Filter() {
        }

        public void setToken(String str) {
            this.token = str;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public String getToken() {
            return this.token;
        }

        public String getValue() {
            return this.value;
        }
    }

    public class FiltersFile {
        public FiltersFile() {
        }

        public void setFile(File file) {
            FilterSet.this.filtersFiles.add(file);
        }
    }

    public static class OnMissing extends EnumeratedAttribute {
        private static final int FAIL_INDEX = 0;
        private static final int IGNORE_INDEX = 2;
        private static final int WARN_INDEX = 1;
        private static final String[] VALUES = {"fail", "warn", Definer.OnError.POLICY_IGNORE};
        public static final OnMissing FAIL = new OnMissing("fail");
        public static final OnMissing WARN = new OnMissing("warn");
        public static final OnMissing IGNORE = new OnMissing(Definer.OnError.POLICY_IGNORE);

        public OnMissing() {
        }

        public OnMissing(String str) {
            setValue(str);
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return VALUES;
        }
    }

    public FilterSet() {
        this.startOfToken = "@";
        this.endOfToken = "@";
        this.duplicateToken = false;
        this.recurse = true;
        this.filterHash = null;
        this.filtersFiles = new Vector<>();
        this.onMissingFiltersFile = OnMissing.FAIL;
        this.readingFiles = false;
        this.recurseDepth = 0;
        this.filters = new Vector<>();
    }

    protected FilterSet(FilterSet filterSet) {
        this.startOfToken = "@";
        this.endOfToken = "@";
        this.duplicateToken = false;
        this.recurse = true;
        this.filterHash = null;
        this.filtersFiles = new Vector<>();
        this.onMissingFiltersFile = OnMissing.FAIL;
        this.readingFiles = false;
        this.recurseDepth = 0;
        this.filters = new Vector<>();
        this.filters = (Vector) filterSet.getFilters().clone();
    }

    protected synchronized Vector<Filter> getFilters() {
        if (isReference()) {
            return getRef().getFilters();
        }
        dieOnCircularReference();
        if (!this.readingFiles) {
            this.readingFiles = true;
            int size = this.filtersFiles.size();
            for (int i = 0; i < size; i++) {
                readFiltersFromFile(this.filtersFiles.get(i));
            }
            this.filtersFiles.clear();
            this.readingFiles = false;
        }
        return this.filters;
    }

    protected FilterSet getRef() {
        return (FilterSet) getCheckedRef(FilterSet.class, "filterset");
    }

    public synchronized Hashtable<String, String> getFilterHash() {
        if (isReference()) {
            return getRef().getFilterHash();
        }
        dieOnCircularReference();
        if (this.filterHash == null) {
            this.filterHash = new Hashtable<>(getFilters().size());
            Enumeration<Filter> enumerationElements = getFilters().elements();
            while (enumerationElements.hasMoreElements()) {
                Filter filterNextElement = enumerationElements.nextElement();
                this.filterHash.put(filterNextElement.getToken(), filterNextElement.getValue());
            }
        }
        return this.filterHash;
    }

    public void setFiltersfile(File file) throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.filtersFiles.add(file);
    }

    public void setBeginToken(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (str == null || "".equals(str)) {
            throw new BuildException("beginToken must not be empty");
        }
        this.startOfToken = str;
    }

    public String getBeginToken() {
        if (isReference()) {
            return getRef().getBeginToken();
        }
        return this.startOfToken;
    }

    public void setEndToken(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (str == null || "".equals(str)) {
            throw new BuildException("endToken must not be empty");
        }
        this.endOfToken = str;
    }

    public String getEndToken() {
        if (isReference()) {
            return getRef().getEndToken();
        }
        return this.endOfToken;
    }

    public void setRecurse(boolean z) {
        this.recurse = z;
    }

    public boolean isRecurse() {
        return this.recurse;
    }

    public synchronized void readFiltersFromFile(File file) throws BuildException {
        Properties properties;
        FileInputStream fileInputStream;
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (!file.exists()) {
            handleMissingFile("Could not read filters from file " + file + " as it doesn't exist.");
        }
        FileInputStream fileInputStream2 = null;
        if (file.isFile()) {
            log("Reading filters from " + file, 3);
            try {
                try {
                    properties = new Properties();
                    fileInputStream = new FileInputStream(file);
                } catch (Exception e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                properties.load(fileInputStream);
                Enumeration<?> enumerationPropertyNames = properties.propertyNames();
                Vector<Filter> filters = getFilters();
                while (enumerationPropertyNames.hasMoreElements()) {
                    String str = (String) enumerationPropertyNames.nextElement();
                    filters.addElement(new Filter(str, properties.getProperty(str)));
                }
                FileUtils.close(fileInputStream);
            } catch (Exception e2) {
                e = e2;
                fileInputStream2 = fileInputStream;
                throw new BuildException("Could not read filters from file: " + file, e);
            } catch (Throwable th2) {
                th = th2;
                fileInputStream2 = fileInputStream;
                FileUtils.close(fileInputStream2);
                throw th;
            }
        } else {
            handleMissingFile("Must specify a file rather than a directory in the filtersfile attribute:" + file);
        }
        this.filterHash = null;
    }

    public synchronized String replaceTokens(String str) {
        return iReplaceTokens(str);
    }

    public synchronized void addFilter(Filter filter) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        this.filters.addElement(filter);
        this.filterHash = null;
    }

    public FiltersFile createFiltersfile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        return new FiltersFile();
    }

    public synchronized void addFilter(String str, String str2) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        addFilter(new Filter(str, str2));
    }

    public synchronized void addConfiguredFilterSet(FilterSet filterSet) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        Iterator<Filter> it = filterSet.getFilters().iterator();
        while (it.hasNext()) {
            addFilter(it.next());
        }
    }

    public synchronized void addConfiguredPropertySet(PropertySet propertySet) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        for (Map.Entry entry : propertySet.getProperties().entrySet()) {
            addFilter(new Filter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
        }
    }

    public synchronized boolean hasFilters() {
        return getFilters().size() > 0;
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public synchronized Object clone() throws BuildException {
        if (isReference()) {
            return getRef().clone();
        }
        try {
            FilterSet filterSet = (FilterSet) super.clone();
            filterSet.filters = (Vector) getFilters().clone();
            filterSet.setProject(getProject());
            return filterSet;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void setOnMissingFiltersFile(OnMissing onMissing) {
        this.onMissingFiltersFile = onMissing;
    }

    public OnMissing getOnMissingFiltersFile() {
        return this.onMissingFiltersFile;
    }

    private synchronized String iReplaceTokens(String str) {
        int length;
        String beginToken = getBeginToken();
        String endToken = getEndToken();
        int iIndexOf = str.indexOf(beginToken);
        if (iIndexOf <= -1) {
            return str;
        }
        Hashtable<String, String> filterHash = getFilterHash();
        try {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (iIndexOf > -1) {
                int iIndexOf2 = str.indexOf(endToken, beginToken.length() + iIndexOf + 1);
                if (iIndexOf2 == -1) {
                    break;
                }
                String strSubstring = str.substring(beginToken.length() + iIndexOf, iIndexOf2);
                sb.append(str.substring(i, iIndexOf));
                if (filterHash.containsKey(strSubstring)) {
                    String strReplaceTokens = filterHash.get(strSubstring);
                    if (this.recurse && !strReplaceTokens.equals(strSubstring)) {
                        strReplaceTokens = replaceTokens(strReplaceTokens, strSubstring);
                    }
                    log("Replacing: " + beginToken + strSubstring + endToken + " -> " + strReplaceTokens, 3);
                    sb.append(strReplaceTokens);
                    length = iIndexOf + beginToken.length() + strSubstring.length() + endToken.length();
                } else {
                    sb.append(beginToken.charAt(0));
                    length = iIndexOf + 1;
                }
                i = length;
                iIndexOf = str.indexOf(beginToken, i);
            }
            sb.append(str.substring(i));
            return sb.toString();
        } catch (StringIndexOutOfBoundsException unused) {
            return str;
        }
    }

    private synchronized String replaceTokens(String str, String str2) throws BuildException {
        String beginToken = getBeginToken();
        String endToken = getEndToken();
        if (this.recurseDepth == 0) {
            this.passedTokens = new VectorSet();
        }
        this.recurseDepth++;
        if (this.passedTokens.contains(str2) && !this.duplicateToken) {
            this.duplicateToken = true;
            System.out.println("Infinite loop in tokens. Currently known tokens : " + this.passedTokens.toString() + "\nProblem token : " + beginToken + str2 + endToken + " called from " + beginToken + this.passedTokens.lastElement().toString() + endToken);
            this.recurseDepth--;
            return str2;
        }
        this.passedTokens.addElement(str2);
        String strIReplaceTokens = iReplaceTokens(str);
        if (strIReplaceTokens.indexOf(beginToken) == -1 && !this.duplicateToken && this.recurseDepth == 1) {
            this.passedTokens = null;
        } else if (this.duplicateToken) {
            if (this.passedTokens.size() > 0) {
                Vector<String> vector = this.passedTokens;
                strIReplaceTokens = vector.remove(vector.size() - 1);
                if (this.passedTokens.size() == 0) {
                    strIReplaceTokens = beginToken + strIReplaceTokens + endToken;
                    this.duplicateToken = false;
                }
            }
        } else if (this.passedTokens.size() > 0) {
            Vector<String> vector2 = this.passedTokens;
            vector2.remove(vector2.size() - 1);
        }
        this.recurseDepth--;
        return strIReplaceTokens;
    }

    private void handleMissingFile(String str) {
        int index = this.onMissingFiltersFile.getIndex();
        if (index == 0) {
            throw new BuildException(str);
        }
        if (index == 1) {
            log(str, 1);
        } else if (index != 2) {
            throw new BuildException("Invalid value for onMissingFiltersFile");
        }
    }
}

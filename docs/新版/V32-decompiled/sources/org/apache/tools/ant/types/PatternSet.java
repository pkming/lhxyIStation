package org.apache.tools.ant.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class PatternSet extends DataType implements Cloneable {
    private List<NameEntry> includeList = new ArrayList();
    private List<NameEntry> excludeList = new ArrayList();
    private List<NameEntry> includesFileList = new ArrayList();
    private List<NameEntry> excludesFileList = new ArrayList();

    public class NameEntry {
        private Object ifCond;
        private String name;
        private Object unlessCond;

        public NameEntry() {
        }

        public void setName(String str) {
            this.name = str;
        }

        public void setIf(Object obj) {
            this.ifCond = obj;
        }

        public void setIf(String str) {
            setIf((Object) str);
        }

        public void setUnless(Object obj) {
            this.unlessCond = obj;
        }

        public void setUnless(String str) {
            setUnless((Object) str);
        }

        public String getName() {
            return this.name;
        }

        public String evalName(Project project) {
            if (valid(project)) {
                return this.name;
            }
            return null;
        }

        private boolean valid(Project project) {
            PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(project);
            return propertyHelper.testIfCondition(this.ifCond) && propertyHelper.testUnlessCondition(this.unlessCond);
        }

        public String toString() {
            String str;
            StringBuffer stringBuffer = new StringBuffer();
            String str2 = this.name;
            if (str2 == null) {
                stringBuffer.append("noname");
            } else {
                stringBuffer.append(str2);
            }
            if (this.ifCond != null || this.unlessCond != null) {
                stringBuffer.append(":");
                if (this.ifCond != null) {
                    stringBuffer.append("if->");
                    stringBuffer.append(this.ifCond);
                    str = ";";
                } else {
                    str = "";
                }
                if (this.unlessCond != null) {
                    stringBuffer.append(str);
                    stringBuffer.append("unless->");
                    stringBuffer.append(this.unlessCond);
                }
            }
            return stringBuffer.toString();
        }
    }

    private static final class InvertedPatternSet extends PatternSet {
        private InvertedPatternSet(PatternSet patternSet) {
            setProject(patternSet.getProject());
            addConfiguredPatternset(patternSet);
        }

        @Override // org.apache.tools.ant.types.PatternSet
        public String[] getIncludePatterns(Project project) {
            return super.getExcludePatterns(project);
        }

        @Override // org.apache.tools.ant.types.PatternSet
        public String[] getExcludePatterns(Project project) {
            return super.getIncludePatterns(project);
        }
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (!this.includeList.isEmpty() || !this.excludeList.isEmpty()) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    public void addConfiguredPatternset(PatternSet patternSet) throws Throwable {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        String[] includePatterns = patternSet.getIncludePatterns(getProject());
        String[] excludePatterns = patternSet.getExcludePatterns(getProject());
        if (includePatterns != null) {
            for (String str : includePatterns) {
                createInclude().setName(str);
            }
        }
        if (excludePatterns != null) {
            for (String str2 : excludePatterns) {
                createExclude().setName(str2);
            }
        }
    }

    public NameEntry createInclude() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        return addPatternToList(this.includeList);
    }

    public NameEntry createIncludesFile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        return addPatternToList(this.includesFileList);
    }

    public NameEntry createExclude() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        return addPatternToList(this.excludeList);
    }

    public NameEntry createExcludesFile() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        return addPatternToList(this.excludesFileList);
    }

    public void setIncludes(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (str == null || str.length() <= 0) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, ", ", false);
        while (stringTokenizer.hasMoreTokens()) {
            createInclude().setName(stringTokenizer.nextToken());
        }
    }

    public void setExcludes(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (str == null || str.length() <= 0) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, ", ", false);
        while (stringTokenizer.hasMoreTokens()) {
            createExclude().setName(stringTokenizer.nextToken());
        }
    }

    private NameEntry addPatternToList(List<NameEntry> list) {
        NameEntry nameEntry = new NameEntry();
        list.add(nameEntry);
        return nameEntry;
    }

    public void setIncludesfile(File file) throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createIncludesFile().setName(file.getAbsolutePath());
    }

    public void setExcludesfile(File file) throws BuildException {
        if (isReference()) {
            throw tooManyAttributes();
        }
        createExcludesFile().setName(file.getAbsolutePath());
    }

    private void readPatterns(File file, List<NameEntry> list, Project project) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.length() > 0) {
                    addPatternToList(list).setName(project.replaceProperties(line));
                }
            }
            FileUtils.close(bufferedReader);
        } catch (IOException e2) {
            e = e2;
            throw new BuildException("An error occurred while reading from pattern file: " + file, e);
        } catch (Throwable th2) {
            th = th2;
            bufferedReader2 = bufferedReader;
            FileUtils.close(bufferedReader2);
            throw th;
        }
    }

    public void append(PatternSet patternSet, Project project) throws Throwable {
        if (isReference()) {
            throw new BuildException("Cannot append to a reference");
        }
        dieOnCircularReference(project);
        String[] includePatterns = patternSet.getIncludePatterns(project);
        if (includePatterns != null) {
            for (String str : includePatterns) {
                createInclude().setName(str);
            }
        }
        String[] excludePatterns = patternSet.getExcludePatterns(project);
        if (excludePatterns != null) {
            for (String str2 : excludePatterns) {
                createExclude().setName(str2);
            }
        }
    }

    public String[] getIncludePatterns(Project project) throws Throwable {
        if (isReference()) {
            return getRef(project).getIncludePatterns(project);
        }
        dieOnCircularReference(project);
        readFiles(project);
        return makeArray(this.includeList, project);
    }

    public String[] getExcludePatterns(Project project) throws Throwable {
        if (isReference()) {
            return getRef(project).getExcludePatterns(project);
        }
        dieOnCircularReference(project);
        readFiles(project);
        return makeArray(this.excludeList, project);
    }

    public boolean hasPatterns(Project project) {
        if (isReference()) {
            return getRef(project).hasPatterns(project);
        }
        dieOnCircularReference(project);
        return this.includesFileList.size() > 0 || this.excludesFileList.size() > 0 || this.includeList.size() > 0 || this.excludeList.size() > 0;
    }

    private PatternSet getRef(Project project) {
        return (PatternSet) getCheckedRef(project);
    }

    private String[] makeArray(List<NameEntry> list, Project project) {
        if (list.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<NameEntry> it = list.iterator();
        while (it.hasNext()) {
            String strEvalName = it.next().evalName(project);
            if (strEvalName != null && strEvalName.length() > 0) {
                arrayList.add(strEvalName);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    private void readFiles(Project project) throws Throwable {
        if (this.includesFileList.size() > 0) {
            Iterator<NameEntry> it = this.includesFileList.iterator();
            while (it.hasNext()) {
                String strEvalName = it.next().evalName(project);
                if (strEvalName != null) {
                    File fileResolveFile = project.resolveFile(strEvalName);
                    if (!fileResolveFile.exists()) {
                        throw new BuildException("Includesfile " + fileResolveFile.getAbsolutePath() + " not found.");
                    }
                    readPatterns(fileResolveFile, this.includeList, project);
                }
            }
            this.includesFileList.clear();
        }
        if (this.excludesFileList.size() > 0) {
            Iterator<NameEntry> it2 = this.excludesFileList.iterator();
            while (it2.hasNext()) {
                String strEvalName2 = it2.next().evalName(project);
                if (strEvalName2 != null) {
                    File fileResolveFile2 = project.resolveFile(strEvalName2);
                    if (!fileResolveFile2.exists()) {
                        throw new BuildException("Excludesfile " + fileResolveFile2.getAbsolutePath() + " not found.");
                    }
                    readPatterns(fileResolveFile2, this.excludeList, project);
                }
            }
            this.excludesFileList.clear();
        }
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        return "patternSet{ includes: " + this.includeList + " excludes: " + this.excludeList + " }";
    }

    @Override // org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        try {
            PatternSet patternSet = (PatternSet) super.clone();
            patternSet.includeList = new ArrayList(this.includeList);
            patternSet.excludeList = new ArrayList(this.excludeList);
            patternSet.includesFileList = new ArrayList(this.includesFileList);
            patternSet.excludesFileList = new ArrayList(this.excludesFileList);
            return patternSet;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void addConfiguredInvert(PatternSet patternSet) throws Throwable {
        addConfiguredPatternset(new InvertedPatternSet());
    }
}

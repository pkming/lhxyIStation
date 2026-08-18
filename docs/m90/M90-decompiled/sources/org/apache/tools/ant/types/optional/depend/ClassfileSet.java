package org.apache.tools.ant.types.optional.depend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class ClassfileSet extends FileSet {
    private List<String> rootClasses;
    private List<FileSet> rootFileSets;

    public static class ClassRoot {
        private String rootClass;

        public void setClassname(String str) {
            this.rootClass = str;
        }

        public String getClassname() {
            return this.rootClass;
        }
    }

    public ClassfileSet() {
        this.rootClasses = new ArrayList();
        this.rootFileSets = new ArrayList();
    }

    public void addRootFileset(FileSet fileSet) {
        this.rootFileSets.add(fileSet);
        setChecked(false);
    }

    protected ClassfileSet(ClassfileSet classfileSet) {
        super(classfileSet);
        this.rootClasses = new ArrayList();
        this.rootFileSets = new ArrayList();
        this.rootClasses.addAll(classfileSet.rootClasses);
    }

    public void setRootClass(String str) {
        this.rootClasses.add(str);
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet
    public DirectoryScanner getDirectoryScanner(Project project) {
        if (isReference()) {
            return getRef(project).getDirectoryScanner(project);
        }
        dieOnCircularReference(project);
        DependScanner dependScanner = new DependScanner(super.getDirectoryScanner(project));
        Vector<String> vector = new Vector<>(this.rootClasses);
        for (FileSet fileSet : this.rootFileSets) {
            String[] includedFiles = fileSet.getDirectoryScanner(project).getIncludedFiles();
            for (int i = 0; i < includedFiles.length; i++) {
                if (includedFiles[i].endsWith(".class")) {
                    vector.addElement(StringUtils.removeSuffix(includedFiles[i], ".class").replace('/', '.').replace('\\', '.'));
                }
            }
            dependScanner.addBasedir(fileSet.getDir(project));
        }
        dependScanner.setBasedir(getDir(project));
        dependScanner.setRootClasses(vector);
        dependScanner.scan();
        return dependScanner;
    }

    public void addConfiguredRoot(ClassRoot classRoot) {
        this.rootClasses.add(classRoot.getClassname());
    }

    @Override // org.apache.tools.ant.types.FileSet, org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType, org.apache.tools.ant.ProjectComponent
    public Object clone() {
        return new ClassfileSet(isReference() ? (ClassfileSet) getRef(getProject()) : this);
    }

    @Override // org.apache.tools.ant.types.AbstractFileSet, org.apache.tools.ant.types.DataType
    protected synchronized void dieOnCircularReference(Stack<Object> stack, Project project) {
        if (isChecked()) {
            return;
        }
        super.dieOnCircularReference(stack, project);
        if (!isReference()) {
            Iterator<FileSet> it = this.rootFileSets.iterator();
            while (it.hasNext()) {
                pushAndInvokeCircularReferenceCheck(it.next(), stack, project);
            }
            setChecked(true);
        }
    }
}

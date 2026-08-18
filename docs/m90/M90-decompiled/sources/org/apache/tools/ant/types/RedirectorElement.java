package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Redirector;
import org.apache.tools.ant.util.MergingMapper;

/* JADX INFO: loaded from: classes3.dex */
public class RedirectorElement extends DataType {
    private Boolean alwaysLog;
    private Boolean append;
    private Boolean createEmptyFiles;
    private String errorEncoding;
    private Mapper errorMapper;
    private String errorProperty;
    private String inputEncoding;
    private Mapper inputMapper;
    private String inputString;
    private Boolean logError;
    private Boolean logInputString;
    private String outputEncoding;
    private Mapper outputMapper;
    private String outputProperty;
    private boolean usingInput = false;
    private boolean usingOutput = false;
    private boolean usingError = false;
    private Vector<FilterChain> inputFilterChains = new Vector<>();
    private Vector<FilterChain> outputFilterChains = new Vector<>();
    private Vector<FilterChain> errorFilterChains = new Vector<>();
    private boolean outputIsBinary = false;

    public void addConfiguredInputMapper(Mapper mapper) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.inputMapper != null) {
            if (this.usingInput) {
                throw new BuildException("attribute \"input\" cannot coexist with a nested <inputmapper>");
            }
            throw new BuildException("Cannot have > 1 <inputmapper>");
        }
        setChecked(false);
        this.inputMapper = mapper;
    }

    public void addConfiguredOutputMapper(Mapper mapper) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.outputMapper != null) {
            if (this.usingOutput) {
                throw new BuildException("attribute \"output\" cannot coexist with a nested <outputmapper>");
            }
            throw new BuildException("Cannot have > 1 <outputmapper>");
        }
        setChecked(false);
        this.outputMapper = mapper;
    }

    public void addConfiguredErrorMapper(Mapper mapper) {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        if (this.errorMapper != null) {
            if (this.usingError) {
                throw new BuildException("attribute \"error\" cannot coexist with a nested <errormapper>");
            }
            throw new BuildException("Cannot have > 1 <errormapper>");
        }
        setChecked(false);
        this.errorMapper = mapper;
    }

    @Override // org.apache.tools.ant.types.DataType
    public void setRefid(Reference reference) throws BuildException {
        if (this.usingInput || this.usingOutput || this.usingError || this.inputString != null || this.logError != null || this.append != null || this.createEmptyFiles != null || this.inputEncoding != null || this.outputEncoding != null || this.errorEncoding != null || this.outputProperty != null || this.errorProperty != null || this.logInputString != null) {
            throw tooManyAttributes();
        }
        super.setRefid(reference);
    }

    public void setInput(File file) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (this.inputString != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.usingInput = true;
        this.inputMapper = createMergeMapper(file);
    }

    public void setInputString(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (this.usingInput) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.inputString = str;
    }

    public void setLogInputString(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.logInputString = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setOutput(File file) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (file == null) {
            throw new IllegalArgumentException("output file specified as null");
        }
        this.usingOutput = true;
        this.outputMapper = createMergeMapper(file);
    }

    public void setOutputEncoding(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.outputEncoding = str;
    }

    public void setErrorEncoding(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.errorEncoding = str;
    }

    public void setInputEncoding(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.inputEncoding = str;
    }

    public void setLogError(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.logError = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setError(File file) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        if (file == null) {
            throw new IllegalArgumentException("error file specified as null");
        }
        this.usingError = true;
        this.errorMapper = createMergeMapper(file);
    }

    public void setOutputProperty(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.outputProperty = str;
    }

    public void setAppend(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.append = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setAlwaysLog(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.alwaysLog = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setCreateEmptyFiles(boolean z) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.createEmptyFiles = z ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setErrorProperty(String str) {
        if (isReference()) {
            throw tooManyAttributes();
        }
        this.errorProperty = str;
    }

    public FilterChain createInputFilterChain() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        FilterChain filterChain = new FilterChain();
        filterChain.setProject(getProject());
        this.inputFilterChains.add(filterChain);
        setChecked(false);
        return filterChain;
    }

    public FilterChain createOutputFilterChain() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        FilterChain filterChain = new FilterChain();
        filterChain.setProject(getProject());
        this.outputFilterChains.add(filterChain);
        setChecked(false);
        return filterChain;
    }

    public FilterChain createErrorFilterChain() {
        if (isReference()) {
            throw noChildrenAllowed();
        }
        FilterChain filterChain = new FilterChain();
        filterChain.setProject(getProject());
        this.errorFilterChains.add(filterChain);
        setChecked(false);
        return filterChain;
    }

    public void setBinaryOutput(boolean z) {
        this.outputIsBinary = z;
    }

    public void configure(Redirector redirector) {
        configure(redirector, null);
    }

    public void configure(Redirector redirector, String str) {
        String[] strArrMapFileName;
        String[] strArrMapFileName2;
        if (isReference()) {
            getRef().configure(redirector, str);
            return;
        }
        dieOnCircularReference();
        Boolean bool = this.alwaysLog;
        if (bool != null) {
            redirector.setAlwaysLog(bool.booleanValue());
        }
        Boolean bool2 = this.logError;
        if (bool2 != null) {
            redirector.setLogError(bool2.booleanValue());
        }
        Boolean bool3 = this.append;
        if (bool3 != null) {
            redirector.setAppend(bool3.booleanValue());
        }
        Boolean bool4 = this.createEmptyFiles;
        if (bool4 != null) {
            redirector.setCreateEmptyFiles(bool4.booleanValue());
        }
        String str2 = this.outputProperty;
        if (str2 != null) {
            redirector.setOutputProperty(str2);
        }
        String str3 = this.errorProperty;
        if (str3 != null) {
            redirector.setErrorProperty(str3);
        }
        String str4 = this.inputString;
        if (str4 != null) {
            redirector.setInputString(str4);
        }
        Boolean bool5 = this.logInputString;
        if (bool5 != null) {
            redirector.setLogInputString(bool5.booleanValue());
        }
        Mapper mapper = this.inputMapper;
        String[] strArrMapFileName3 = null;
        if (mapper != null) {
            try {
                strArrMapFileName = mapper.getImplementation().mapFileName(str);
            } catch (NullPointerException e) {
                if (str != null) {
                    throw e;
                }
                strArrMapFileName = null;
            }
            if (strArrMapFileName != null && strArrMapFileName.length > 0) {
                redirector.setInput(toFileArray(strArrMapFileName));
            }
        }
        Mapper mapper2 = this.outputMapper;
        if (mapper2 != null) {
            try {
                strArrMapFileName2 = mapper2.getImplementation().mapFileName(str);
            } catch (NullPointerException e2) {
                if (str != null) {
                    throw e2;
                }
                strArrMapFileName2 = null;
            }
            if (strArrMapFileName2 != null && strArrMapFileName2.length > 0) {
                redirector.setOutput(toFileArray(strArrMapFileName2));
            }
        }
        Mapper mapper3 = this.errorMapper;
        if (mapper3 != null) {
            try {
                strArrMapFileName3 = mapper3.getImplementation().mapFileName(str);
            } catch (NullPointerException e3) {
                if (str != null) {
                    throw e3;
                }
            }
            if (strArrMapFileName3 != null && strArrMapFileName3.length > 0) {
                redirector.setError(toFileArray(strArrMapFileName3));
            }
        }
        if (this.inputFilterChains.size() > 0) {
            redirector.setInputFilterChains(this.inputFilterChains);
        }
        if (this.outputFilterChains.size() > 0) {
            redirector.setOutputFilterChains(this.outputFilterChains);
        }
        if (this.errorFilterChains.size() > 0) {
            redirector.setErrorFilterChains(this.errorFilterChains);
        }
        String str5 = this.inputEncoding;
        if (str5 != null) {
            redirector.setInputEncoding(str5);
        }
        String str6 = this.outputEncoding;
        if (str6 != null) {
            redirector.setOutputEncoding(str6);
        }
        String str7 = this.errorEncoding;
        if (str7 != null) {
            redirector.setErrorEncoding(str7);
        }
        redirector.setBinaryOutput(this.outputIsBinary);
    }

    protected Mapper createMergeMapper(File file) {
        Mapper mapper = new Mapper(getProject());
        mapper.setClassname(MergingMapper.class.getName());
        mapper.setTo(file.getAbsolutePath());
        return mapper;
    }

    protected File[] toFileArray(String[] strArr) {
        if (strArr == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(strArr.length);
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i] != null) {
                arrayList.add(getProject().resolveFile(strArr[i]));
            }
        }
        return (File[]) arrayList.toArray(new File[arrayList.size()]);
    }

    @Override // org.apache.tools.ant.types.DataType
    protected void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (isChecked()) {
            return;
        }
        if (isReference()) {
            super.dieOnCircularReference(stack, project);
            return;
        }
        Mapper[] mapperArr = {this.inputMapper, this.outputMapper, this.errorMapper};
        for (int i = 0; i < 3; i++) {
            if (mapperArr[i] != null) {
                stack.push(mapperArr[i]);
                mapperArr[i].dieOnCircularReference(stack, project);
                stack.pop();
            }
        }
        for (List list : Arrays.asList(this.inputFilterChains, this.outputFilterChains, this.errorFilterChains)) {
            if (list != null) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    pushAndInvokeCircularReferenceCheck((FilterChain) it.next(), stack, project);
                }
            }
        }
        setChecked(true);
    }

    private RedirectorElement getRef() {
        return (RedirectorElement) getCheckedRef();
    }
}

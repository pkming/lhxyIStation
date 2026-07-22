package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.compilers.AptExternalCompilerAdapter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.JavaEnvUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Apt extends Javac {
    public static final String ERROR_IGNORING_COMPILER_OPTION = "Ignoring compiler attribute for the APT task, as it is fixed";
    public static final String ERROR_WRONG_JAVA_VERSION = "Apt task requires Java 1.5+";
    public static final String EXECUTABLE_NAME = "apt";
    public static final String WARNING_IGNORING_FORK = "Apt only runs in its own JVM; fork=false option ignored";
    private String factory;
    private Path factoryPath;
    private File preprocessDir;
    private boolean compile = true;
    private Vector<Option> options = new Vector<>();

    public static final class Option {
        private String name;
        private String value;

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String str) {
            this.value = str;
        }
    }

    public Apt() {
        super.setCompiler(AptExternalCompilerAdapter.class.getName());
        super.setFork(true);
    }

    public String getAptExecutable() {
        String executable = getExecutable();
        return executable != null ? executable : JavaEnvUtils.getJdkExecutable(EXECUTABLE_NAME);
    }

    @Override // org.apache.tools.ant.taskdefs.Javac
    public void setCompiler(String str) {
        log(ERROR_IGNORING_COMPILER_OPTION, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.Javac
    public void setFork(boolean z) {
        if (z) {
            return;
        }
        log(WARNING_IGNORING_FORK, 1);
    }

    @Override // org.apache.tools.ant.taskdefs.Javac
    public String getCompiler() {
        return super.getCompiler();
    }

    public boolean isCompile() {
        return this.compile;
    }

    public void setCompile(boolean z) {
        this.compile = z;
    }

    public String getFactory() {
        return this.factory;
    }

    public void setFactory(String str) {
        this.factory = str;
    }

    public void setFactoryPathRef(Reference reference) {
        createFactoryPath().setRefid(reference);
    }

    public Path createFactoryPath() {
        if (this.factoryPath == null) {
            this.factoryPath = new Path(getProject());
        }
        return this.factoryPath.createPath();
    }

    public Path getFactoryPath() {
        return this.factoryPath;
    }

    public Option createOption() {
        Option option = new Option();
        this.options.add(option);
        return option;
    }

    public Vector<Option> getOptions() {
        return this.options;
    }

    public File getPreprocessDir() {
        return this.preprocessDir;
    }

    public void setPreprocessDir(File file) {
        this.preprocessDir = file;
    }

    @Override // org.apache.tools.ant.taskdefs.Javac, org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (JavaEnvUtils.getJavaVersionNumber() >= 18) {
            throw new BuildException("apt does not exist under Java 1.8 and higher");
        }
        super.execute();
    }
}

package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.IsSigned;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.ResourceUtils;

/* JADX INFO: loaded from: classes3.dex */
public class SignJar extends AbstractJarSignerTask {
    public static final String ERROR_BAD_MAP = "Cannot map source file to anything sensible: ";
    public static final String ERROR_MAPPER_WITHOUT_DEST = "The destDir attribute is required if a mapper is set";
    public static final String ERROR_NO_ALIAS = "alias attribute must be set";
    public static final String ERROR_NO_STOREPASS = "storepass attribute must be set";
    public static final String ERROR_SIGNEDJAR_AND_PATHS = "You cannot specify the signed JAR when using paths or filesets";
    public static final String ERROR_TODIR_AND_SIGNEDJAR = "'destdir' and 'signedjar' cannot both be set";
    public static final String ERROR_TOO_MANY_MAPPERS = "Too many mappers";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    protected File destDir;
    private String digestAlg;
    private boolean force = false;
    protected boolean internalsf;
    protected boolean lazy;
    private FileNameMapper mapper;
    private boolean preserveLastModified;
    protected boolean sectionsonly;
    private String sigAlg;
    protected String sigfile;
    protected File signedjar;
    protected String tsacert;
    protected String tsaurl;

    public void setSigfile(String str) {
        this.sigfile = str;
    }

    public void setSignedjar(File file) {
        this.signedjar = file;
    }

    public void setInternalsf(boolean z) {
        this.internalsf = z;
    }

    public void setSectionsonly(boolean z) {
        this.sectionsonly = z;
    }

    public void setLazy(boolean z) {
        this.lazy = z;
    }

    public void setDestDir(File file) {
        this.destDir = file;
    }

    public void add(FileNameMapper fileNameMapper) {
        if (this.mapper != null) {
            throw new BuildException(ERROR_TOO_MANY_MAPPERS);
        }
        this.mapper = fileNameMapper;
    }

    public FileNameMapper getMapper() {
        return this.mapper;
    }

    public String getTsaurl() {
        return this.tsaurl;
    }

    public void setTsaurl(String str) {
        this.tsaurl = str;
    }

    public String getTsacert() {
        return this.tsacert;
    }

    public void setTsacert(String str) {
        this.tsacert = str;
    }

    public void setForce(boolean z) {
        this.force = z;
    }

    public boolean isForce() {
        return this.force;
    }

    public void setSigAlg(String str) {
        this.sigAlg = str;
    }

    public String getSigAlg() {
        return this.sigAlg;
    }

    public void setDigestAlg(String str) {
        this.digestAlg = str;
    }

    public String getDigestAlg() {
        return this.digestAlg;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        FileNameMapper identityMapper;
        boolean z = this.jar != null;
        boolean z2 = this.signedjar != null;
        boolean z3 = this.destDir != null;
        boolean z4 = this.mapper != null;
        if (!z && !hasResources()) {
            throw new BuildException(AbstractJarSignerTask.ERROR_NO_SOURCE);
        }
        if (this.alias == null) {
            throw new BuildException(ERROR_NO_ALIAS);
        }
        if (this.storepass == null) {
            throw new BuildException(ERROR_NO_STOREPASS);
        }
        if (z3 && z2) {
            throw new BuildException(ERROR_TODIR_AND_SIGNEDJAR);
        }
        if (hasResources() && z2) {
            throw new BuildException(ERROR_SIGNEDJAR_AND_PATHS);
        }
        if (!z3 && z4) {
            throw new BuildException(ERROR_MAPPER_WITHOUT_DEST);
        }
        beginExecution();
        try {
            if (z && z2) {
                signOneJar(this.jar, this.signedjar);
                return;
            }
            Path pathCreateUnifiedSourcePath = createUnifiedSourcePath();
            if (z4) {
                identityMapper = this.mapper;
            } else {
                identityMapper = new IdentityMapper();
            }
            Iterator<Resource> it = pathCreateUnifiedSourcePath.iterator();
            while (it.hasNext()) {
                FileResource fileResourceAsFileResource = ResourceUtils.asFileResource((FileProvider) it.next().as(FileProvider.class));
                File baseDir = z3 ? this.destDir : fileResourceAsFileResource.getBaseDir();
                String[] strArrMapFileName = identityMapper.mapFileName(fileResourceAsFileResource.getName());
                if (strArrMapFileName == null || strArrMapFileName.length != 1) {
                    throw new BuildException(ERROR_BAD_MAP + fileResourceAsFileResource.getFile());
                }
                signOneJar(fileResourceAsFileResource.getFile(), new File(baseDir, strArrMapFileName[0]));
            }
        } finally {
            endExecution();
        }
    }

    private void signOneJar(File file, File file2) throws BuildException {
        if (file2 == null) {
            file2 = file;
        }
        if (isUpToDate(file, file2)) {
            return;
        }
        long jLastModified = file.lastModified();
        ExecTask execTaskCreateJarSigner = createJarSigner();
        setCommonOptions(execTaskCreateJarSigner);
        bindToKeystore(execTaskCreateJarSigner);
        if (this.sigfile != null) {
            addValue(execTaskCreateJarSigner, "-sigfile");
            addValue(execTaskCreateJarSigner, this.sigfile);
        }
        try {
            FileUtils fileUtils = FILE_UTILS;
            if (!fileUtils.areSame(file, file2)) {
                addValue(execTaskCreateJarSigner, "-signedjar");
                addValue(execTaskCreateJarSigner, file2.getPath());
            }
            if (this.internalsf) {
                addValue(execTaskCreateJarSigner, "-internalsf");
            }
            if (this.sectionsonly) {
                addValue(execTaskCreateJarSigner, "-sectionsonly");
            }
            if (this.sigAlg != null) {
                addValue(execTaskCreateJarSigner, "-sigalg");
                addValue(execTaskCreateJarSigner, this.sigAlg);
            }
            if (this.digestAlg != null) {
                addValue(execTaskCreateJarSigner, "-digestalg");
                addValue(execTaskCreateJarSigner, this.digestAlg);
            }
            addTimestampAuthorityCommands(execTaskCreateJarSigner);
            addValue(execTaskCreateJarSigner, file.getPath());
            addValue(execTaskCreateJarSigner, this.alias);
            log("Signing JAR: " + file.getAbsolutePath() + " to " + file2.getAbsolutePath() + " as " + this.alias);
            execTaskCreateJarSigner.execute();
            if (this.preserveLastModified) {
                fileUtils.setFileLastModified(file2, jLastModified);
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void addTimestampAuthorityCommands(ExecTask execTask) {
        if (this.tsaurl != null) {
            addValue(execTask, "-tsa");
            addValue(execTask, this.tsaurl);
        }
        if (this.tsacert != null) {
            addValue(execTask, "-tsacert");
            addValue(execTask, this.tsacert);
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    protected boolean isUpToDate(File file, File file2) {
        if (!isForce() && file != null) {
            File file3 = file2;
            if (file.exists()) {
                if (file2 == null) {
                    file3 = file;
                }
                if (file.equals(file3)) {
                    if (this.lazy) {
                        return isSigned(file);
                    }
                    return false;
                }
                return FILE_UTILS.isUpToDate(file, file3);
            }
        }
        return false;
    }

    protected boolean isSigned(File file) {
        try {
            String str = this.sigfile;
            if (str == null) {
                str = this.alias;
            }
            return IsSigned.isSigned(file, str);
        } catch (IOException e) {
            log(e.toString(), 3);
            return false;
        }
    }

    public void setPreserveLastModified(boolean z) {
        this.preserveLastModified = z;
    }
}

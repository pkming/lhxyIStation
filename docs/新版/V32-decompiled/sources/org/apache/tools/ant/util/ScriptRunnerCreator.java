package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/* JADX INFO: loaded from: classes3.dex */
public class ScriptRunnerCreator {
    private static final String AUTO = "auto";
    private static final String BSF = "bsf";
    private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
    private static final String BSF_PACK = "org.apache.bsf";
    private static final String BSF_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
    private static final String JAVAX = "javax";
    private static final String JAVAX_MANAGER = "javax.script.ScriptEngineManager";
    private static final String JAVAX_RUNNER = "org.apache.tools.ant.util.optional.JavaxScriptRunner";
    private static final String OATAU = "org.apache.tools.ant.util";
    private static final String UTIL_OPT = "org.apache.tools.ant.util.optional";
    private String language;
    private String manager;
    private Project project;
    private ClassLoader scriptLoader = null;

    public ScriptRunnerCreator(Project project) {
        this.project = project;
    }

    public synchronized ScriptRunnerBase createRunner(String str, String str2, ClassLoader classLoader) {
        ScriptRunnerBase scriptRunnerBaseCreateRunner;
        this.manager = str;
        this.language = str2;
        this.scriptLoader = classLoader;
        if (str2 == null) {
            throw new BuildException("script language must be specified");
        }
        if (!str.equals("auto") && !str.equals(JAVAX) && !str.equals(BSF)) {
            throw new BuildException("Unsupported language prefix " + str);
        }
        scriptRunnerBaseCreateRunner = createRunner(BSF, BSF_MANAGER, BSF_RUNNER);
        if (scriptRunnerBaseCreateRunner == null) {
            scriptRunnerBaseCreateRunner = createRunner(JAVAX, JAVAX_MANAGER, JAVAX_RUNNER);
        }
        if (scriptRunnerBaseCreateRunner == null) {
            if (JAVAX.equals(str)) {
                throw new BuildException("Unable to load the script engine manager (javax.script.ScriptEngineManager)");
            }
            if (BSF.equals(str)) {
                throw new BuildException("Unable to load the BSF script engine manager (org.apache.bsf.BSFManager)");
            }
            throw new BuildException("Unable to load a script engine manager (org.apache.bsf.BSFManager or javax.script.ScriptEngineManager)");
        }
        return scriptRunnerBaseCreateRunner;
    }

    private ScriptRunnerBase createRunner(String str, String str2, String str3) {
        if ((!this.manager.equals("auto") && !this.manager.equals(str)) || this.scriptLoader.getResource(LoaderUtils.classNameToResource(str2)) == null) {
            return null;
        }
        if (str2.equals(BSF_MANAGER)) {
            new ScriptFixBSFPath().fixClassLoader(this.scriptLoader, this.language);
        }
        try {
            ScriptRunnerBase scriptRunnerBase = (ScriptRunnerBase) Class.forName(str3, true, this.scriptLoader).newInstance();
            scriptRunnerBase.setProject(this.project);
            scriptRunnerBase.setLanguage(this.language);
            scriptRunnerBase.setScriptClassLoader(this.scriptLoader);
            return scriptRunnerBase;
        } catch (Exception e) {
            throw ReflectUtil.toBuildException(e);
        }
    }
}

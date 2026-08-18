package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.types.Mapper;

/* JADX INFO: loaded from: classes3.dex */
public class RenameExtensions extends MatchingTask {
    private Mapper.MapperType globType;
    private File srcDir;
    private String fromExtension = "";
    private String toExtension = "";
    private boolean replace = false;

    public RenameExtensions() {
        Mapper.MapperType mapperType = new Mapper.MapperType();
        this.globType = mapperType;
        mapperType.setValue("glob");
    }

    public void setFromExtension(String str) {
        this.fromExtension = str;
    }

    public void setToExtension(String str) {
        this.toExtension = str;
    }

    public void setReplace(boolean z) {
        this.replace = z;
    }

    public void setSrcDir(File file) {
        this.srcDir = file;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.fromExtension == null || this.toExtension == null || this.srcDir == null) {
            throw new BuildException("srcDir, fromExtension and toExtension attributes must be set!");
        }
        log("DEPRECATED - The renameext task is deprecated.  Use move instead.", 1);
        log("Replace this with:", 2);
        log("<move todir=\"" + this.srcDir + "\" overwrite=\"" + this.replace + "\">", 2);
        log("  <fileset dir=\"" + this.srcDir + "\" />", 2);
        log("  <mapper type=\"glob\"", 2);
        log("          from=\"*" + this.fromExtension + "\"", 2);
        log("          to=\"*" + this.toExtension + "\" />", 2);
        log("</move>", 2);
        log("using the same patterns on <fileset> as you've used here", 2);
        Move move = new Move();
        move.bindToOwner(this);
        move.setOwningTarget(getOwningTarget());
        move.setTaskName(getTaskName());
        move.setLocation(getLocation());
        move.setTodir(this.srcDir);
        move.setOverwrite(this.replace);
        this.fileset.setDir(this.srcDir);
        move.addFileset(this.fileset);
        Mapper mapperCreateMapper = move.createMapper();
        mapperCreateMapper.setType(this.globType);
        mapperCreateMapper.setFrom("*" + this.fromExtension);
        mapperCreateMapper.setTo("*" + this.toExtension);
        move.execute();
    }
}

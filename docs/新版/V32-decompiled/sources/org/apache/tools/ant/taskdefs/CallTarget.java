package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.types.PropertySet;

/* JADX INFO: loaded from: classes3.dex */
public class CallTarget extends Task {
    private Ant callee;
    private boolean inheritAll = true;
    private boolean inheritRefs = false;
    private boolean targetSet = false;

    public void setInheritAll(boolean z) {
        this.inheritAll = z;
    }

    public void setInheritRefs(boolean z) {
        this.inheritRefs = z;
    }

    @Override // org.apache.tools.ant.Task
    public void init() {
        Ant ant = new Ant(this);
        this.callee = ant;
        ant.init();
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (this.callee == null) {
            init();
        }
        if (!this.targetSet) {
            throw new BuildException("Attribute target or at least one nested target is required.", getLocation());
        }
        this.callee.setAntfile(getProject().getProperty(MagicNames.ANT_FILE));
        this.callee.setInheritAll(this.inheritAll);
        this.callee.setInheritRefs(this.inheritRefs);
        this.callee.execute();
    }

    public Property createParam() {
        if (this.callee == null) {
            init();
        }
        return this.callee.createProperty();
    }

    public void addReference(Ant.Reference reference) {
        if (this.callee == null) {
            init();
        }
        this.callee.addReference(reference);
    }

    public void addPropertyset(PropertySet propertySet) {
        if (this.callee == null) {
            init();
        }
        this.callee.addPropertyset(propertySet);
    }

    public void setTarget(String str) {
        if (this.callee == null) {
            init();
        }
        this.callee.setTarget(str);
        this.targetSet = true;
    }

    public void addConfiguredTarget(Ant.TargetElement targetElement) {
        if (this.callee == null) {
            init();
        }
        this.callee.addConfiguredTarget(targetElement);
        this.targetSet = true;
    }

    @Override // org.apache.tools.ant.Task
    public void handleOutput(String str) {
        Ant ant = this.callee;
        if (ant != null) {
            ant.handleOutput(str);
        } else {
            super.handleOutput(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public int handleInput(byte[] bArr, int i, int i2) throws IOException {
        Ant ant = this.callee;
        if (ant != null) {
            return ant.handleInput(bArr, i, i2);
        }
        return super.handleInput(bArr, i, i2);
    }

    @Override // org.apache.tools.ant.Task
    public void handleFlush(String str) {
        Ant ant = this.callee;
        if (ant != null) {
            ant.handleFlush(str);
        } else {
            super.handleFlush(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleErrorOutput(String str) {
        Ant ant = this.callee;
        if (ant != null) {
            ant.handleErrorOutput(str);
        } else {
            super.handleErrorOutput(str);
        }
    }

    @Override // org.apache.tools.ant.Task
    public void handleErrorFlush(String str) {
        Ant ant = this.callee;
        if (ant != null) {
            ant.handleErrorFlush(str);
        } else {
            super.handleErrorFlush(str);
        }
    }
}

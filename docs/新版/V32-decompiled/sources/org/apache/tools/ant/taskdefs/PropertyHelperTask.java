package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class PropertyHelperTask extends Task {
    private List delegates;
    private PropertyHelper propertyHelper;

    public final class DelegateElement {
        private String refid;

        private DelegateElement() {
        }

        public String getRefid() {
            return this.refid;
        }

        public void setRefid(String str) {
            this.refid = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PropertyHelper.Delegate resolve() {
            if (this.refid == null) {
                throw new BuildException("refid required for generic delegate");
            }
            return (PropertyHelper.Delegate) PropertyHelperTask.this.getProject().getReference(this.refid);
        }
    }

    public synchronized void addConfigured(PropertyHelper propertyHelper) {
        if (this.propertyHelper != null) {
            throw new BuildException("Only one PropertyHelper can be installed");
        }
        this.propertyHelper = propertyHelper;
    }

    public synchronized void addConfigured(PropertyHelper.Delegate delegate) {
        getAddDelegateList().add(delegate);
    }

    public DelegateElement createDelegate() {
        DelegateElement delegateElement = new DelegateElement();
        getAddDelegateList().add(delegateElement);
        return delegateElement;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        if (getProject() == null) {
            throw new BuildException("Project instance not set");
        }
        PropertyHelper propertyHelper = this.propertyHelper;
        if (propertyHelper == null && this.delegates == null) {
            throw new BuildException("Either a new PropertyHelper or one or more PropertyHelper delegates are required");
        }
        if (propertyHelper == null) {
            propertyHelper = PropertyHelper.getPropertyHelper(getProject());
        }
        synchronized (propertyHelper) {
            List list = this.delegates;
            if (list != null) {
                for (Object obj : list) {
                    PropertyHelper.Delegate delegateResolve = obj instanceof DelegateElement ? ((DelegateElement) obj).resolve() : (PropertyHelper.Delegate) obj;
                    log("Adding PropertyHelper delegate " + delegateResolve, 4);
                    propertyHelper.add(delegateResolve);
                }
            }
        }
        if (this.propertyHelper != null) {
            log("Installing PropertyHelper " + this.propertyHelper, 4);
            getProject().addReference(MagicNames.REFID_PROPERTY_HELPER, this.propertyHelper);
        }
    }

    private synchronized List getAddDelegateList() {
        if (this.delegates == null) {
            this.delegates = new ArrayList();
        }
        return this.delegates;
    }
}

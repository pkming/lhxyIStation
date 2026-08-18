package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class ProjectHelperTask extends Task {
    private List projectHelpers = new ArrayList();

    public synchronized void addConfigured(ProjectHelper projectHelper) {
        this.projectHelpers.add(projectHelper);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        ProjectHelperRepository projectHelperRepository = ProjectHelperRepository.getInstance();
        Iterator it = this.projectHelpers.iterator();
        while (it.hasNext()) {
            projectHelperRepository.registerProjectHelper((Class<? extends ProjectHelper>) ((ProjectHelper) it.next()).getClass());
        }
    }
}

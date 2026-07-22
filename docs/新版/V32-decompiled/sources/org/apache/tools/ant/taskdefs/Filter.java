package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/* JADX INFO: loaded from: classes3.dex */
public class Filter extends Task {
    private File filtersFile;
    private String token;
    private String value;

    public void setToken(String str) {
        this.token = str;
    }

    public void setValue(String str) {
        this.value = str;
    }

    public void setFiltersfile(File file) {
        this.filtersFile = file;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        File file = this.filtersFile;
        boolean z = file != null && this.token == null && this.value == null;
        boolean z2 = (file != null || this.token == null || this.value == null) ? false : true;
        if (!z && !z2) {
            throw new BuildException("both token and value parameters, or only a filtersFile parameter is required", getLocation());
        }
        if (z2) {
            getProject().getGlobalFilterSet().addFilter(this.token, this.value);
        }
        if (z) {
            readFilters();
        }
    }

    protected void readFilters() throws BuildException {
        log("Reading filters from " + this.filtersFile, 3);
        getProject().getGlobalFilterSet().readFiltersFromFile(this.filtersFile);
    }
}

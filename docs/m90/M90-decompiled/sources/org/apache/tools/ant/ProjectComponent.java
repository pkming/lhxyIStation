package org.apache.tools.ant;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ProjectComponent implements Cloneable {
    protected String description;
    protected Location location = Location.UNKNOWN_LOCATION;
    protected Project project;

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void log(String str) {
        log(str, 2);
    }

    public void log(String str, int i) {
        if (getProject() != null) {
            getProject().log(str, i);
        } else if (i <= 2) {
            System.err.println(str);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        ProjectComponent projectComponent = (ProjectComponent) super.clone();
        projectComponent.setLocation(getLocation());
        projectComponent.setProject(getProject());
        return projectComponent;
    }
}

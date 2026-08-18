package org.apache.tools.ant;

/* JADX INFO: loaded from: classes3.dex */
public class BuildException extends RuntimeException {
    private static final long serialVersionUID = -5419014565354664240L;
    private Location location;

    public BuildException() {
        this.location = Location.UNKNOWN_LOCATION;
    }

    public BuildException(String str) {
        super(str);
        this.location = Location.UNKNOWN_LOCATION;
    }

    public BuildException(String str, Throwable th) {
        super(str, th);
        this.location = Location.UNKNOWN_LOCATION;
    }

    public BuildException(String str, Throwable th, Location location) {
        this(str, th);
        this.location = location;
    }

    public BuildException(Throwable th) {
        super(th);
        this.location = Location.UNKNOWN_LOCATION;
    }

    public BuildException(String str, Location location) {
        super(str);
        this.location = Location.UNKNOWN_LOCATION;
        this.location = location;
    }

    public BuildException(Throwable th, Location location) {
        this(th);
        this.location = location;
    }

    public Throwable getException() {
        return getCause();
    }

    @Override // java.lang.Throwable
    public String toString() {
        return this.location.toString() + getMessage();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }
}

package android.content.pm;

import java.util.HashSet;

/* JADX INFO: loaded from: classes.dex */
public class PackageUserState {
    public boolean blocked;
    public HashSet<String> disabledComponents;
    public int enabled;
    public HashSet<String> enabledComponents;
    public boolean installed;
    public String lastDisableAppCaller;
    public boolean notLaunched;
    public boolean stopped;

    public PackageUserState() {
        this.installed = true;
        this.blocked = false;
        this.enabled = 0;
    }

    public PackageUserState(PackageUserState packageUserState) {
        this.installed = packageUserState.installed;
        this.stopped = packageUserState.stopped;
        this.notLaunched = packageUserState.notLaunched;
        this.enabled = packageUserState.enabled;
        this.blocked = packageUserState.blocked;
        this.lastDisableAppCaller = packageUserState.lastDisableAppCaller;
        this.disabledComponents = packageUserState.disabledComponents != null ? new HashSet<>(packageUserState.disabledComponents) : null;
        this.enabledComponents = packageUserState.enabledComponents != null ? new HashSet<>(packageUserState.enabledComponents) : null;
    }
}

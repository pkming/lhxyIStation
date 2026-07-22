package android.content.res;

import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
public final class ResourcesKey {
    public final int mDisplayId;
    private final int mHash;
    public final Configuration mOverrideConfiguration;
    final String mResDir;
    final float mScale;
    private final IBinder mToken;

    public ResourcesKey(String str, int i, Configuration configuration, float f, IBinder iBinder) {
        Configuration configuration2 = new Configuration();
        this.mOverrideConfiguration = configuration2;
        this.mResDir = str;
        this.mDisplayId = i;
        if (configuration != null) {
            configuration2.setTo(configuration);
        }
        this.mScale = f;
        this.mToken = iBinder;
        this.mHash = ((((((527 + (str == null ? 0 : str.hashCode())) * 31) + i) * 31) + (configuration2 != null ? configuration2.hashCode() : 0)) * 31) + Float.floatToIntBits(f);
    }

    public boolean hasOverrideConfiguration() {
        return !Configuration.EMPTY.equals(this.mOverrideConfiguration);
    }

    public int hashCode() {
        return this.mHash;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ResourcesKey)) {
            return false;
        }
        ResourcesKey resourcesKey = (ResourcesKey) obj;
        if (!this.mResDir.equals(resourcesKey.mResDir) || this.mDisplayId != resourcesKey.mDisplayId) {
            return false;
        }
        Configuration configuration = this.mOverrideConfiguration;
        Configuration configuration2 = resourcesKey.mOverrideConfiguration;
        return (configuration == configuration2 || !(configuration == null || configuration2 == null || !configuration.equals(configuration2))) && this.mScale == resourcesKey.mScale;
    }

    public String toString() {
        return Integer.toHexString(this.mHash);
    }
}

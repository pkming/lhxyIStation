package android.security;

import android.content.Context;
import java.security.KeyStore;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class KeyStoreParameter implements KeyStore.ProtectionParameter {
    private int mFlags;

    private KeyStoreParameter(int i) {
        this.mFlags = i;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isEncryptionRequired() {
        return (this.mFlags & 1) != 0;
    }

    public static final class Builder {
        private int mFlags;

        public Builder(Context context) {
            Objects.requireNonNull(context, "context == null");
        }

        public Builder setEncryptionRequired(boolean z) {
            if (z) {
                this.mFlags |= 1;
            } else {
                this.mFlags &= -2;
            }
            return this;
        }

        public KeyStoreParameter build() {
            return new KeyStoreParameter(this.mFlags);
        }
    }
}

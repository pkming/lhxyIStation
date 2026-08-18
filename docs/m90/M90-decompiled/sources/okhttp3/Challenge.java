package okhttp3;

import java.util.Objects;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes2.dex */
public final class Challenge {
    private final String realm;
    private final String scheme;

    public Challenge(String str, String str2) {
        Objects.requireNonNull(str, "scheme == null");
        Objects.requireNonNull(str2, "realm == null");
        this.scheme = str;
        this.realm = str2;
    }

    public String scheme() {
        return this.scheme;
    }

    public String realm() {
        return this.realm;
    }

    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Challenge) {
            Challenge challenge = (Challenge) obj;
            if (challenge.scheme.equals(this.scheme) && challenge.realm.equals(this.realm)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return ((899 + this.realm.hashCode()) * 31) + this.scheme.hashCode();
    }

    public String toString() {
        return this.scheme + " realm=\"" + this.realm + "\"";
    }
}

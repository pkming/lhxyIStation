package android.webkit;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class WebStorage {

    @Deprecated
    public interface QuotaUpdater {
        void updateQuota(long j);
    }

    public void deleteAllData() {
    }

    public void deleteOrigin(String str) {
    }

    public void getOrigins(ValueCallback<Map> valueCallback) {
    }

    public void getQuotaForOrigin(String str, ValueCallback<Long> valueCallback) {
    }

    public void getUsageForOrigin(String str, ValueCallback<Long> valueCallback) {
    }

    @Deprecated
    public void setQuotaForOrigin(String str, long j) {
    }

    public static class Origin {
        private String mOrigin;
        private long mQuota;
        private long mUsage;

        protected Origin(String str, long j, long j2) {
            this.mOrigin = null;
            this.mQuota = 0L;
            this.mUsage = 0L;
            this.mOrigin = str;
            this.mQuota = j;
            this.mUsage = j2;
        }

        protected Origin(String str, long j) {
            this.mOrigin = null;
            this.mQuota = 0L;
            this.mUsage = 0L;
            this.mOrigin = str;
            this.mQuota = j;
        }

        protected Origin(String str) {
            this.mOrigin = null;
            this.mQuota = 0L;
            this.mUsage = 0L;
            this.mOrigin = str;
        }

        public String getOrigin() {
            return this.mOrigin;
        }

        public long getQuota() {
            return this.mQuota;
        }

        public long getUsage() {
            return this.mUsage;
        }
    }

    public static WebStorage getInstance() {
        return WebViewFactory.getProvider().getWebStorage();
    }
}

package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class SyncRequest implements Parcelable {
    public static final Parcelable.Creator<SyncRequest> CREATOR = new Parcelable.Creator<SyncRequest>() { // from class: android.content.SyncRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SyncRequest createFromParcel(Parcel parcel) {
            return new SyncRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SyncRequest[] newArray(int i) {
            return new SyncRequest[i];
        }
    };
    private static final String TAG = "SyncRequest";
    private final Account mAccountToSync;
    private final String mAuthority;
    private final ComponentName mComponentInfo;
    private final boolean mDisallowMetered;
    private final Bundle mExtras;
    private final boolean mIsAuthority;
    private final boolean mIsExpedited;
    private final boolean mIsPeriodic;
    private final long mRxBytes;
    private final long mSyncFlexTimeSecs;
    private final long mSyncRunTimeSecs;
    private final long mTxBytes;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean isPeriodic() {
        return this.mIsPeriodic;
    }

    public boolean isExpedited() {
        return this.mIsExpedited;
    }

    public boolean hasAuthority() {
        return this.mIsAuthority;
    }

    public Account getAccount() {
        if (!hasAuthority()) {
            throw new IllegalArgumentException("Cannot getAccount() for a sync that does notspecify an authority.");
        }
        return this.mAccountToSync;
    }

    public String getProvider() {
        if (!hasAuthority()) {
            throw new IllegalArgumentException("Cannot getProvider() for a sync that does notspecify a provider.");
        }
        return this.mAuthority;
    }

    public Bundle getBundle() {
        return this.mExtras;
    }

    public long getSyncFlexTime() {
        return this.mSyncFlexTimeSecs;
    }

    public long getSyncRunTime() {
        return this.mSyncRunTimeSecs;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(this.mExtras);
        parcel.writeLong(this.mSyncFlexTimeSecs);
        parcel.writeLong(this.mSyncRunTimeSecs);
        parcel.writeInt(this.mIsPeriodic ? 1 : 0);
        parcel.writeInt(this.mDisallowMetered ? 1 : 0);
        parcel.writeLong(this.mTxBytes);
        parcel.writeLong(this.mRxBytes);
        parcel.writeInt(this.mIsAuthority ? 1 : 0);
        parcel.writeInt(this.mIsExpedited ? 1 : 0);
        if (this.mIsAuthority) {
            parcel.writeParcelable(this.mAccountToSync, i);
            parcel.writeString(this.mAuthority);
        } else {
            parcel.writeParcelable(this.mComponentInfo, i);
        }
    }

    private SyncRequest(Parcel parcel) {
        this.mExtras = parcel.readBundle();
        this.mSyncFlexTimeSecs = parcel.readLong();
        this.mSyncRunTimeSecs = parcel.readLong();
        this.mIsPeriodic = parcel.readInt() != 0;
        this.mDisallowMetered = parcel.readInt() != 0;
        this.mTxBytes = parcel.readLong();
        this.mRxBytes = parcel.readLong();
        boolean z = parcel.readInt() != 0;
        this.mIsAuthority = z;
        this.mIsExpedited = parcel.readInt() != 0;
        if (z) {
            this.mComponentInfo = null;
            this.mAccountToSync = (Account) parcel.readParcelable(null);
            this.mAuthority = parcel.readString();
        } else {
            this.mComponentInfo = (ComponentName) parcel.readParcelable(null);
            this.mAccountToSync = null;
            this.mAuthority = null;
        }
    }

    protected SyncRequest(Builder builder) {
        this.mSyncFlexTimeSecs = builder.mSyncFlexTimeSecs;
        this.mSyncRunTimeSecs = builder.mSyncRunTimeSecs;
        this.mAccountToSync = builder.mAccount;
        this.mAuthority = builder.mAuthority;
        this.mComponentInfo = builder.mComponentName;
        this.mIsPeriodic = builder.mSyncType == 1;
        this.mIsAuthority = builder.mSyncTarget == 2;
        this.mIsExpedited = builder.mExpedited;
        Bundle bundle = new Bundle(builder.mCustomExtras);
        this.mExtras = bundle;
        bundle.putAll(builder.mSyncConfigExtras);
        this.mDisallowMetered = builder.mDisallowMetered;
        this.mTxBytes = builder.mTxBytes;
        this.mRxBytes = builder.mRxBytes;
    }

    public static class Builder {
        private static final int SYNC_TARGET_ADAPTER = 2;
        private static final int SYNC_TARGET_SERVICE = 1;
        private static final int SYNC_TARGET_UNKNOWN = 0;
        private static final int SYNC_TYPE_ONCE = 2;
        private static final int SYNC_TYPE_PERIODIC = 1;
        private static final int SYNC_TYPE_UNKNOWN = 0;
        private Account mAccount;
        private String mAuthority;
        private ComponentName mComponentName;
        private Bundle mCustomExtras;
        private boolean mDisallowMetered;
        private boolean mExpedited;
        private boolean mIgnoreBackoff;
        private boolean mIgnoreSettings;
        private boolean mIsManual;
        private boolean mNoRetry;
        private Bundle mSyncConfigExtras;
        private long mSyncFlexTimeSecs;
        private long mSyncRunTimeSecs;
        private long mTxBytes = -1;
        private long mRxBytes = -1;
        private int mPriority = 0;
        private int mSyncType = 0;
        private int mSyncTarget = 0;

        public Builder syncOnce() {
            if (this.mSyncType != 0) {
                throw new IllegalArgumentException("Sync type has already been defined.");
            }
            this.mSyncType = 2;
            setupInterval(0L, 0L);
            return this;
        }

        public Builder syncPeriodic(long j, long j2) {
            if (this.mSyncType != 0) {
                throw new IllegalArgumentException("Sync type has already been defined.");
            }
            this.mSyncType = 1;
            setupInterval(j, j2);
            return this;
        }

        private void setupInterval(long j, long j2) {
            if (j2 > j) {
                throw new IllegalArgumentException("Specified run time for the sync must be after the specified flex time.");
            }
            this.mSyncRunTimeSecs = j;
            this.mSyncFlexTimeSecs = j2;
        }

        public Builder setTransferSize(long j, long j2) {
            this.mRxBytes = j;
            this.mTxBytes = j2;
            return this;
        }

        public Builder setDisallowMetered(boolean z) {
            this.mDisallowMetered = z;
            return this;
        }

        public Builder setSyncAdapter(Account account, String str) {
            if (this.mSyncTarget != 0) {
                throw new IllegalArgumentException("Sync target has already been defined.");
            }
            if (str != null && str.length() == 0) {
                throw new IllegalArgumentException("Authority must be non-empty");
            }
            this.mSyncTarget = 2;
            this.mAccount = account;
            this.mAuthority = str;
            this.mComponentName = null;
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            this.mCustomExtras = bundle;
            return this;
        }

        public Builder setNoRetry(boolean z) {
            this.mNoRetry = z;
            return this;
        }

        public Builder setIgnoreSettings(boolean z) {
            this.mIgnoreSettings = z;
            return this;
        }

        public Builder setIgnoreBackoff(boolean z) {
            this.mIgnoreBackoff = z;
            return this;
        }

        public Builder setManual(boolean z) {
            this.mIsManual = z;
            return this;
        }

        public Builder setExpedited(boolean z) {
            this.mExpedited = z;
            return this;
        }

        public Builder setPriority(int i) {
            if (i < -2 || i > 2) {
                throw new IllegalArgumentException("Priority must be within range [-2, 2]");
            }
            this.mPriority = i;
            return this;
        }

        public SyncRequest build() {
            if (this.mCustomExtras == null) {
                this.mCustomExtras = new Bundle();
            }
            ContentResolver.validateSyncExtrasBundle(this.mCustomExtras);
            Bundle bundle = new Bundle();
            this.mSyncConfigExtras = bundle;
            if (this.mIgnoreBackoff) {
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true);
            }
            if (this.mDisallowMetered) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_DISALLOW_METERED, true);
            }
            if (this.mIgnoreSettings) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true);
            }
            if (this.mNoRetry) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
            }
            if (this.mExpedited) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            }
            if (this.mIsManual) {
                this.mSyncConfigExtras.putBoolean("force", true);
            }
            this.mSyncConfigExtras.putLong(ContentResolver.SYNC_EXTRAS_EXPECTED_UPLOAD, this.mTxBytes);
            this.mSyncConfigExtras.putLong(ContentResolver.SYNC_EXTRAS_EXPECTED_DOWNLOAD, this.mRxBytes);
            this.mSyncConfigExtras.putInt(ContentResolver.SYNC_EXTRAS_PRIORITY, this.mPriority);
            int i = this.mSyncType;
            if (i == 1) {
                validatePeriodicExtras(this.mCustomExtras);
                validatePeriodicExtras(this.mSyncConfigExtras);
                if (this.mAccount == null) {
                    throw new IllegalArgumentException("Account must not be null for periodic sync.");
                }
                if (this.mAuthority == null) {
                    throw new IllegalArgumentException("Authority must not be null for periodic sync.");
                }
            } else if (i == 0) {
                throw new IllegalArgumentException("Must call either syncOnce() or syncPeriodic()");
            }
            if (this.mSyncTarget == 0) {
                throw new IllegalArgumentException("Must specify an adapter with setSyncAdapter(Account, String");
            }
            return new SyncRequest(this);
        }

        private void validatePeriodicExtras(Bundle bundle) {
            if (bundle.getBoolean("force", false) || bundle.getBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false) || bundle.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, false) || bundle.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, false) || bundle.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false) || bundle.getBoolean("force", false) || bundle.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false)) {
                throw new IllegalArgumentException("Illegal extras were set");
            }
        }
    }
}

package android.database.sqlite;

import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDebug;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.LruCache;
import android.util.Printer;
import cn.yunzhisheng.asr.JniUscClient;
import dalvik.system.BlockGuard;
import dalvik.system.CloseGuard;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public final class SQLiteConnection implements CancellationSignal.OnCancelListener {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean DEBUG = false;
    private static final String TAG = "SQLiteConnection";
    private int mCancellationSignalAttachCount;
    private final CloseGuard mCloseGuard;
    private final SQLiteDatabaseConfiguration mConfiguration;
    private final int mConnectionId;
    private int mConnectionPtr;
    private final boolean mIsPrimaryConnection;
    private final boolean mIsReadOnlyConnection;
    private boolean mOnlyAllowReadOnlyOperations;
    private final SQLiteConnectionPool mPool;
    private final PreparedStatementCache mPreparedStatementCache;
    private PreparedStatement mPreparedStatementPool;
    private final OperationLog mRecentOperations;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final Pattern TRIM_SQL_PATTERN = Pattern.compile("[\\s]*\\n+[\\s]*");

    private static boolean isCacheable(int i) {
        return i == 2 || i == 1;
    }

    private static native void nativeBindBlob(int i, int i2, int i3, byte[] bArr);

    private static native void nativeBindDouble(int i, int i2, int i3, double d);

    private static native void nativeBindLong(int i, int i2, int i3, long j);

    private static native void nativeBindNull(int i, int i2, int i3);

    private static native void nativeBindString(int i, int i2, int i3, String str);

    private static native void nativeCancel(int i);

    private static native void nativeClose(int i);

    private static native void nativeExecute(int i, int i2);

    private static native int nativeExecuteForBlobFileDescriptor(int i, int i2);

    private static native int nativeExecuteForChangedRowCount(int i, int i2);

    private static native long nativeExecuteForCursorWindow(int i, int i2, int i3, int i4, int i5, boolean z);

    private static native long nativeExecuteForLastInsertedRowId(int i, int i2);

    private static native long nativeExecuteForLong(int i, int i2);

    private static native String nativeExecuteForString(int i, int i2);

    private static native void nativeFinalizeStatement(int i, int i2);

    private static native int nativeGetColumnCount(int i, int i2);

    private static native String nativeGetColumnName(int i, int i2, int i3);

    private static native int nativeGetDbLookaside(int i);

    private static native int nativeGetParameterCount(int i, int i2);

    private static native boolean nativeIsReadOnly(int i, int i2);

    private static native int nativeOpen(String str, int i, String str2, boolean z, boolean z2);

    private static native int nativePrepareStatement(int i, String str);

    private static native void nativeRegisterCustomFunction(int i, SQLiteCustomFunction sQLiteCustomFunction);

    private static native void nativeRegisterLocalizedCollators(int i, String str);

    private static native void nativeResetCancel(int i, boolean z);

    private static native void nativeResetStatementAndClearBindings(int i, int i2);

    private SQLiteConnection(SQLiteConnectionPool sQLiteConnectionPool, SQLiteDatabaseConfiguration sQLiteDatabaseConfiguration, int i, boolean z) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mRecentOperations = new OperationLog();
        this.mPool = sQLiteConnectionPool;
        SQLiteDatabaseConfiguration sQLiteDatabaseConfiguration2 = new SQLiteDatabaseConfiguration(sQLiteDatabaseConfiguration);
        this.mConfiguration = sQLiteDatabaseConfiguration2;
        this.mConnectionId = i;
        this.mIsPrimaryConnection = z;
        this.mIsReadOnlyConnection = (sQLiteDatabaseConfiguration.openFlags & 1) != 0;
        this.mPreparedStatementCache = new PreparedStatementCache(sQLiteDatabaseConfiguration2.maxSqlCacheSize);
        closeGuard.open(JniUscClient.r);
    }

    protected void finalize() throws Throwable {
        try {
            SQLiteConnectionPool sQLiteConnectionPool = this.mPool;
            if (sQLiteConnectionPool != null && this.mConnectionPtr != 0) {
                sQLiteConnectionPool.onConnectionLeaked();
            }
            dispose(true);
        } finally {
            super.finalize();
        }
    }

    static SQLiteConnection open(SQLiteConnectionPool sQLiteConnectionPool, SQLiteDatabaseConfiguration sQLiteDatabaseConfiguration, int i, boolean z) {
        SQLiteConnection sQLiteConnection = new SQLiteConnection(sQLiteConnectionPool, sQLiteDatabaseConfiguration, i, z);
        try {
            sQLiteConnection.open();
            return sQLiteConnection;
        } catch (SQLiteException e) {
            sQLiteConnection.dispose(false);
            throw e;
        }
    }

    void close() {
        dispose(false);
    }

    private void open() {
        this.mConnectionPtr = nativeOpen(this.mConfiguration.path, this.mConfiguration.openFlags, this.mConfiguration.label, SQLiteDebug.DEBUG_SQL_STATEMENTS, SQLiteDebug.DEBUG_SQL_TIME);
        setPageSize();
        setForeignKeyModeFromConfiguration();
        setWalModeFromConfiguration();
        setJournalSizeLimit();
        setAutoCheckpointInterval();
        setLocaleFromConfiguration();
        int size = this.mConfiguration.customFunctions.size();
        for (int i = 0; i < size; i++) {
            nativeRegisterCustomFunction(this.mConnectionPtr, this.mConfiguration.customFunctions.get(i));
        }
    }

    private void dispose(boolean z) {
        CloseGuard closeGuard = this.mCloseGuard;
        if (closeGuard != null) {
            if (z) {
                closeGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        if (this.mConnectionPtr != 0) {
            int iBeginOperation = this.mRecentOperations.beginOperation(JniUscClient.r, null, null);
            try {
                this.mPreparedStatementCache.evictAll();
                nativeClose(this.mConnectionPtr);
                this.mConnectionPtr = 0;
            } finally {
                this.mRecentOperations.endOperation(iBeginOperation);
            }
        }
    }

    private void setPageSize() {
        if (this.mConfiguration.isInMemoryDb() || this.mIsReadOnlyConnection) {
            return;
        }
        long defaultPageSize = SQLiteGlobal.getDefaultPageSize();
        if (executeForLong("PRAGMA page_size", null, null) != defaultPageSize) {
            execute("PRAGMA page_size=" + defaultPageSize, null, null);
        }
    }

    private void setAutoCheckpointInterval() {
        if (this.mConfiguration.isInMemoryDb() || this.mIsReadOnlyConnection) {
            return;
        }
        long wALAutoCheckpoint = SQLiteGlobal.getWALAutoCheckpoint();
        if (executeForLong("PRAGMA wal_autocheckpoint", null, null) != wALAutoCheckpoint) {
            executeForLong("PRAGMA wal_autocheckpoint=" + wALAutoCheckpoint, null, null);
        }
    }

    private void setJournalSizeLimit() {
        if (this.mConfiguration.isInMemoryDb() || this.mIsReadOnlyConnection) {
            return;
        }
        long journalSizeLimit = SQLiteGlobal.getJournalSizeLimit();
        if (executeForLong("PRAGMA journal_size_limit", null, null) != journalSizeLimit) {
            executeForLong("PRAGMA journal_size_limit=" + journalSizeLimit, null, null);
        }
    }

    private void setForeignKeyModeFromConfiguration() {
        if (this.mIsReadOnlyConnection) {
            return;
        }
        long j = this.mConfiguration.foreignKeyConstraintsEnabled ? 1L : 0L;
        if (executeForLong("PRAGMA foreign_keys", null, null) != j) {
            execute("PRAGMA foreign_keys=" + j, null, null);
        }
    }

    private void setWalModeFromConfiguration() {
        if (this.mConfiguration.isInMemoryDb() || this.mIsReadOnlyConnection) {
            return;
        }
        if ((this.mConfiguration.openFlags & 536870912) != 0) {
            setJournalMode("WAL");
            setSyncMode(SQLiteGlobal.getWALSyncMode());
        } else {
            setJournalMode(SQLiteGlobal.getDefaultJournalMode());
            setSyncMode(SQLiteGlobal.getDefaultSyncMode());
        }
    }

    private void setSyncMode(String str) {
        if (canonicalizeSyncMode(executeForString("PRAGMA synchronous", null, null)).equalsIgnoreCase(canonicalizeSyncMode(str))) {
            return;
        }
        execute("PRAGMA synchronous=" + str, null, null);
    }

    private static String canonicalizeSyncMode(String str) {
        return str.equals("0") ? "OFF" : str.equals("1") ? "NORMAL" : str.equals("2") ? "FULL" : str;
    }

    private void setJournalMode(String str) {
        String strExecuteForString = executeForString("PRAGMA journal_mode", null, null);
        if (strExecuteForString.equalsIgnoreCase(str)) {
            return;
        }
        try {
            if (executeForString("PRAGMA journal_mode=" + str, null, null).equalsIgnoreCase(str)) {
                return;
            }
        } catch (SQLiteDatabaseLockedException unused) {
        }
        Log.w(TAG, "Could not change the database journal mode of '" + this.mConfiguration.label + "' from '" + strExecuteForString + "' to '" + str + "' because the database is locked.  This usually means that there are other open connections to the database which prevents the database from enabling or disabling write-ahead logging mode.  Proceeding without changing the journal mode.");
    }

    private void setLocaleFromConfiguration() {
        if ((this.mConfiguration.openFlags & 16) != 0) {
            return;
        }
        String string = this.mConfiguration.locale.toString();
        nativeRegisterLocalizedCollators(this.mConnectionPtr, string);
        if (this.mIsReadOnlyConnection) {
            return;
        }
        try {
            execute("CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT)", null, null);
            String strExecuteForString = executeForString("SELECT locale FROM android_metadata UNION SELECT NULL ORDER BY locale DESC LIMIT 1", null, null);
            if (strExecuteForString == null || !strExecuteForString.equals(string)) {
                execute("BEGIN", null, null);
                try {
                    execute("DELETE FROM android_metadata", null, null);
                    execute("INSERT INTO android_metadata (locale) VALUES(?)", new Object[]{string}, null);
                    execute("REINDEX LOCALIZED", null, null);
                    execute("COMMIT", null, null);
                } catch (Throwable th) {
                    execute("ROLLBACK", null, null);
                    throw th;
                }
            }
        } catch (RuntimeException e) {
            throw new SQLiteException("Failed to change locale for db '" + this.mConfiguration.label + "' to '" + string + "'.", e);
        }
    }

    void reconfigure(SQLiteDatabaseConfiguration sQLiteDatabaseConfiguration) {
        this.mOnlyAllowReadOnlyOperations = false;
        int size = sQLiteDatabaseConfiguration.customFunctions.size();
        for (int i = 0; i < size; i++) {
            SQLiteCustomFunction sQLiteCustomFunction = sQLiteDatabaseConfiguration.customFunctions.get(i);
            if (!this.mConfiguration.customFunctions.contains(sQLiteCustomFunction)) {
                nativeRegisterCustomFunction(this.mConnectionPtr, sQLiteCustomFunction);
            }
        }
        boolean z = sQLiteDatabaseConfiguration.foreignKeyConstraintsEnabled != this.mConfiguration.foreignKeyConstraintsEnabled;
        boolean z2 = ((sQLiteDatabaseConfiguration.openFlags ^ this.mConfiguration.openFlags) & 536870912) != 0;
        boolean z3 = !sQLiteDatabaseConfiguration.locale.equals(this.mConfiguration.locale);
        this.mConfiguration.updateParametersFrom(sQLiteDatabaseConfiguration);
        this.mPreparedStatementCache.resize(sQLiteDatabaseConfiguration.maxSqlCacheSize);
        if (z) {
            setForeignKeyModeFromConfiguration();
        }
        if (z2) {
            setWalModeFromConfiguration();
        }
        if (z3) {
            setLocaleFromConfiguration();
        }
    }

    void setOnlyAllowReadOnlyOperations(boolean z) {
        this.mOnlyAllowReadOnlyOperations = z;
    }

    boolean isPreparedStatementInCache(String str) {
        return this.mPreparedStatementCache.get(str) != null;
    }

    public int getConnectionId() {
        return this.mConnectionId;
    }

    public boolean isPrimaryConnection() {
        return this.mIsPrimaryConnection;
    }

    public void prepare(String str, SQLiteStatementInfo sQLiteStatementInfo) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("prepare", str, null);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                if (sQLiteStatementInfo != null) {
                    try {
                        sQLiteStatementInfo.numParameters = preparedStatementAcquirePreparedStatement.mNumParameters;
                        sQLiteStatementInfo.readOnly = preparedStatementAcquirePreparedStatement.mReadOnly;
                        int iNativeGetColumnCount = nativeGetColumnCount(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                        if (iNativeGetColumnCount == 0) {
                            sQLiteStatementInfo.columnNames = EMPTY_STRING_ARRAY;
                        } else {
                            sQLiteStatementInfo.columnNames = new String[iNativeGetColumnCount];
                            for (int i = 0; i < iNativeGetColumnCount; i++) {
                                sQLiteStatementInfo.columnNames[i] = nativeGetColumnName(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr, i);
                            }
                        }
                    } finally {
                        releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                    }
                }
            } catch (RuntimeException e) {
                this.mRecentOperations.failOperation(iBeginOperation, e);
                throw e;
            }
        } finally {
            this.mRecentOperations.endOperation(iBeginOperation);
        }
    }

    public void execute(String str, Object[] objArr, CancellationSignal cancellationSignal) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("execute", str, objArr);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                try {
                    throwIfStatementForbidden(preparedStatementAcquirePreparedStatement);
                    bindArguments(preparedStatementAcquirePreparedStatement, objArr);
                    applyBlockGuardPolicy(preparedStatementAcquirePreparedStatement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        nativeExecute(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } finally {
                    releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                }
            } finally {
                this.mRecentOperations.endOperation(iBeginOperation);
            }
        } catch (RuntimeException e) {
            this.mRecentOperations.failOperation(iBeginOperation, e);
            throw e;
        }
    }

    public long executeForLong(String str, Object[] objArr, CancellationSignal cancellationSignal) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("executeForLong", str, objArr);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                try {
                    throwIfStatementForbidden(preparedStatementAcquirePreparedStatement);
                    bindArguments(preparedStatementAcquirePreparedStatement, objArr);
                    applyBlockGuardPolicy(preparedStatementAcquirePreparedStatement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        return nativeExecuteForLong(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } finally {
                    releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                }
            } catch (RuntimeException e) {
                this.mRecentOperations.failOperation(iBeginOperation, e);
                throw e;
            }
        } finally {
            this.mRecentOperations.endOperation(iBeginOperation);
        }
    }

    public String executeForString(String str, Object[] objArr, CancellationSignal cancellationSignal) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("executeForString", str, objArr);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                try {
                    throwIfStatementForbidden(preparedStatementAcquirePreparedStatement);
                    bindArguments(preparedStatementAcquirePreparedStatement, objArr);
                    applyBlockGuardPolicy(preparedStatementAcquirePreparedStatement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        return nativeExecuteForString(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } finally {
                    releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                }
            } catch (RuntimeException e) {
                this.mRecentOperations.failOperation(iBeginOperation, e);
                throw e;
            }
        } finally {
            this.mRecentOperations.endOperation(iBeginOperation);
        }
    }

    public ParcelFileDescriptor executeForBlobFileDescriptor(String str, Object[] objArr, CancellationSignal cancellationSignal) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("executeForBlobFileDescriptor", str, objArr);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                try {
                    throwIfStatementForbidden(preparedStatementAcquirePreparedStatement);
                    bindArguments(preparedStatementAcquirePreparedStatement, objArr);
                    applyBlockGuardPolicy(preparedStatementAcquirePreparedStatement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        int iNativeExecuteForBlobFileDescriptor = nativeExecuteForBlobFileDescriptor(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                        return iNativeExecuteForBlobFileDescriptor >= 0 ? ParcelFileDescriptor.adoptFd(iNativeExecuteForBlobFileDescriptor) : null;
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } finally {
                    releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                }
            } catch (RuntimeException e) {
                this.mRecentOperations.failOperation(iBeginOperation, e);
                throw e;
            }
        } finally {
            this.mRecentOperations.endOperation(iBeginOperation);
        }
    }

    public int executeForChangedRowCount(String str, Object[] objArr, CancellationSignal cancellationSignal) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("executeForChangedRowCount", str, objArr);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                try {
                    throwIfStatementForbidden(preparedStatementAcquirePreparedStatement);
                    bindArguments(preparedStatementAcquirePreparedStatement, objArr);
                    applyBlockGuardPolicy(preparedStatementAcquirePreparedStatement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        int iNativeExecuteForChangedRowCount = nativeExecuteForChangedRowCount(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                        if (this.mRecentOperations.endOperationDeferLog(iBeginOperation)) {
                            this.mRecentOperations.logOperation(iBeginOperation, "changedRows=" + iNativeExecuteForChangedRowCount);
                        }
                        return iNativeExecuteForChangedRowCount;
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } finally {
                    releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                }
            } catch (RuntimeException e) {
                this.mRecentOperations.failOperation(iBeginOperation, e);
                throw e;
            }
        } catch (Throwable th) {
            if (this.mRecentOperations.endOperationDeferLog(iBeginOperation)) {
                this.mRecentOperations.logOperation(iBeginOperation, "changedRows=0");
            }
            throw th;
        }
    }

    public long executeForLastInsertedRowId(String str, Object[] objArr, CancellationSignal cancellationSignal) {
        if (str == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int iBeginOperation = this.mRecentOperations.beginOperation("executeForLastInsertedRowId", str, objArr);
        try {
            try {
                PreparedStatement preparedStatementAcquirePreparedStatement = acquirePreparedStatement(str);
                try {
                    throwIfStatementForbidden(preparedStatementAcquirePreparedStatement);
                    bindArguments(preparedStatementAcquirePreparedStatement, objArr);
                    applyBlockGuardPolicy(preparedStatementAcquirePreparedStatement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        return nativeExecuteForLastInsertedRowId(this.mConnectionPtr, preparedStatementAcquirePreparedStatement.mStatementPtr);
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } finally {
                    releasePreparedStatement(preparedStatementAcquirePreparedStatement);
                }
            } catch (RuntimeException e) {
                this.mRecentOperations.failOperation(iBeginOperation, e);
                throw e;
            }
        } finally {
            this.mRecentOperations.endOperation(iBeginOperation);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x0123 A[Catch: all -> 0x015a, TryCatch #13 {all -> 0x015a, blocks: (B:6:0x001e, B:18:0x0062, B:20:0x006a, B:57:0x011b, B:59:0x0123, B:60:0x0159), top: B:85:0x001e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int executeForCursorWindow(java.lang.String r21, java.lang.Object[] r22, android.database.CursorWindow r23, int r24, int r25, boolean r26, android.os.CancellationSignal r27) {
        /*
            Method dump skipped, instruction units count: 369
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.database.sqlite.SQLiteConnection.executeForCursorWindow(java.lang.String, java.lang.Object[], android.database.CursorWindow, int, int, boolean, android.os.CancellationSignal):int");
    }

    private PreparedStatement acquirePreparedStatement(String str) {
        boolean z;
        PreparedStatement preparedStatementObtainPreparedStatement = this.mPreparedStatementCache.get(str);
        if (preparedStatementObtainPreparedStatement == null) {
            z = false;
        } else {
            if (!preparedStatementObtainPreparedStatement.mInUse) {
                return preparedStatementObtainPreparedStatement;
            }
            z = true;
        }
        int iNativePrepareStatement = nativePrepareStatement(this.mConnectionPtr, str);
        try {
            int iNativeGetParameterCount = nativeGetParameterCount(this.mConnectionPtr, iNativePrepareStatement);
            int sqlStatementType = DatabaseUtils.getSqlStatementType(str);
            preparedStatementObtainPreparedStatement = obtainPreparedStatement(str, iNativePrepareStatement, iNativeGetParameterCount, sqlStatementType, nativeIsReadOnly(this.mConnectionPtr, iNativePrepareStatement));
            if (!z && isCacheable(sqlStatementType)) {
                this.mPreparedStatementCache.put(str, preparedStatementObtainPreparedStatement);
                preparedStatementObtainPreparedStatement.mInCache = true;
            }
            preparedStatementObtainPreparedStatement.mInUse = true;
            return preparedStatementObtainPreparedStatement;
        } catch (RuntimeException e) {
            if (preparedStatementObtainPreparedStatement == null || !preparedStatementObtainPreparedStatement.mInCache) {
                nativeFinalizeStatement(this.mConnectionPtr, iNativePrepareStatement);
            }
            throw e;
        }
    }

    private void releasePreparedStatement(PreparedStatement preparedStatement) {
        preparedStatement.mInUse = false;
        if (preparedStatement.mInCache) {
            try {
                nativeResetStatementAndClearBindings(this.mConnectionPtr, preparedStatement.mStatementPtr);
                return;
            } catch (SQLiteException unused) {
                this.mPreparedStatementCache.remove(preparedStatement.mSql);
                return;
            }
        }
        finalizePreparedStatement(preparedStatement);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finalizePreparedStatement(PreparedStatement preparedStatement) {
        nativeFinalizeStatement(this.mConnectionPtr, preparedStatement.mStatementPtr);
        recyclePreparedStatement(preparedStatement);
    }

    private void attachCancellationSignal(CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
            int i = this.mCancellationSignalAttachCount + 1;
            this.mCancellationSignalAttachCount = i;
            if (i == 1) {
                nativeResetCancel(this.mConnectionPtr, true);
                cancellationSignal.setOnCancelListener(this);
            }
        }
    }

    private void detachCancellationSignal(CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            int i = this.mCancellationSignalAttachCount - 1;
            this.mCancellationSignalAttachCount = i;
            if (i == 0) {
                cancellationSignal.setOnCancelListener(null);
                nativeResetCancel(this.mConnectionPtr, false);
            }
        }
    }

    @Override // android.os.CancellationSignal.OnCancelListener
    public void onCancel() {
        nativeCancel(this.mConnectionPtr);
    }

    private void bindArguments(PreparedStatement preparedStatement, Object[] objArr) {
        int length = objArr != null ? objArr.length : 0;
        if (length != preparedStatement.mNumParameters) {
            throw new SQLiteBindOrColumnIndexOutOfRangeException("Expected " + preparedStatement.mNumParameters + " bind arguments but " + length + " were provided.");
        }
        if (length == 0) {
            return;
        }
        int i = preparedStatement.mStatementPtr;
        for (int i2 = 0; i2 < length; i2++) {
            Object obj = objArr[i2];
            int typeOfObject = DatabaseUtils.getTypeOfObject(obj);
            if (typeOfObject == 0) {
                nativeBindNull(this.mConnectionPtr, i, i2 + 1);
            } else if (typeOfObject == 1) {
                nativeBindLong(this.mConnectionPtr, i, i2 + 1, ((Number) obj).longValue());
            } else if (typeOfObject == 2) {
                nativeBindDouble(this.mConnectionPtr, i, i2 + 1, ((Number) obj).doubleValue());
            } else if (typeOfObject == 4) {
                nativeBindBlob(this.mConnectionPtr, i, i2 + 1, (byte[]) obj);
            } else if (obj instanceof Boolean) {
                nativeBindLong(this.mConnectionPtr, i, i2 + 1, ((Boolean) obj).booleanValue() ? 1L : 0L);
            } else {
                nativeBindString(this.mConnectionPtr, i, i2 + 1, obj.toString());
            }
        }
    }

    private void throwIfStatementForbidden(PreparedStatement preparedStatement) {
        if (this.mOnlyAllowReadOnlyOperations && !preparedStatement.mReadOnly) {
            throw new SQLiteException("Cannot execute this statement because it might modify the database but the connection is read-only.");
        }
    }

    private void applyBlockGuardPolicy(PreparedStatement preparedStatement) {
        if (this.mConfiguration.isInMemoryDb()) {
            return;
        }
        if (preparedStatement.mReadOnly) {
            BlockGuard.getThreadPolicy().onReadFromDisk();
        } else {
            BlockGuard.getThreadPolicy().onWriteToDisk();
        }
    }

    public void dump(Printer printer, boolean z) {
        dumpUnsafe(printer, z);
    }

    void dumpUnsafe(Printer printer, boolean z) {
        printer.println("Connection #" + this.mConnectionId + ":");
        if (z) {
            printer.println("  connectionPtr: 0x" + Integer.toHexString(this.mConnectionPtr));
        }
        printer.println("  isPrimaryConnection: " + this.mIsPrimaryConnection);
        printer.println("  onlyAllowReadOnlyOperations: " + this.mOnlyAllowReadOnlyOperations);
        this.mRecentOperations.dump(printer, z);
        if (z) {
            this.mPreparedStatementCache.dump(printer);
        }
    }

    String describeCurrentOperationUnsafe() {
        return this.mRecentOperations.describeCurrentOperation();
    }

    void collectDbStats(ArrayList<SQLiteDebug.DbStats> arrayList) {
        long jExecuteForLong;
        long jExecuteForLong2;
        long jExecuteForLong3;
        long j;
        long jExecuteForLong4;
        int iNativeGetDbLookaside = nativeGetDbLookaside(this.mConnectionPtr);
        try {
            jExecuteForLong = executeForLong("PRAGMA page_count;", null, null);
        } catch (SQLiteException unused) {
            jExecuteForLong = 0;
        }
        try {
            jExecuteForLong2 = executeForLong("PRAGMA page_size;", null, null);
        } catch (SQLiteException unused2) {
            jExecuteForLong2 = 0;
        }
        arrayList.add(getMainDbStatsUnsafe(iNativeGetDbLookaside, jExecuteForLong, jExecuteForLong2));
        CursorWindow cursorWindow = new CursorWindow("collectDbStats");
        try {
            try {
                executeForCursorWindow("PRAGMA database_list;", null, cursorWindow, 0, 0, false, null);
                for (int i = 1; i < cursorWindow.getNumRows(); i++) {
                    String string = cursorWindow.getString(i, 1);
                    String string2 = cursorWindow.getString(i, 2);
                    try {
                        jExecuteForLong3 = executeForLong("PRAGMA " + string + ".page_count;", null, null);
                        try {
                            j = jExecuteForLong3;
                            jExecuteForLong4 = executeForLong("PRAGMA " + string + ".page_size;", null, null);
                        } catch (SQLiteException unused3) {
                            j = jExecuteForLong3;
                            jExecuteForLong4 = 0;
                        }
                    } catch (SQLiteException unused4) {
                        jExecuteForLong3 = 0;
                    }
                    String str = "  (attached) " + string;
                    if (!string2.isEmpty()) {
                        str = str + ": " + string2;
                    }
                    arrayList.add(new SQLiteDebug.DbStats(str, j, jExecuteForLong4, 0, 0, 0, 0));
                }
            } catch (SQLiteException unused5) {
            }
        } finally {
            cursorWindow.close();
        }
    }

    void collectDbStatsUnsafe(ArrayList<SQLiteDebug.DbStats> arrayList) {
        arrayList.add(getMainDbStatsUnsafe(0, 0L, 0L));
    }

    private SQLiteDebug.DbStats getMainDbStatsUnsafe(int i, long j, long j2) {
        String str = this.mConfiguration.path;
        if (!this.mIsPrimaryConnection) {
            str = str + " (" + this.mConnectionId + ")";
        }
        return new SQLiteDebug.DbStats(str, j, j2, i, this.mPreparedStatementCache.hitCount(), this.mPreparedStatementCache.missCount(), this.mPreparedStatementCache.size());
    }

    public String toString() {
        return "SQLiteConnection: " + this.mConfiguration.path + " (" + this.mConnectionId + ")";
    }

    private PreparedStatement obtainPreparedStatement(String str, int i, int i2, int i3, boolean z) {
        PreparedStatement preparedStatement = this.mPreparedStatementPool;
        if (preparedStatement != null) {
            this.mPreparedStatementPool = preparedStatement.mPoolNext;
            preparedStatement.mPoolNext = null;
            preparedStatement.mInCache = false;
        } else {
            preparedStatement = new PreparedStatement();
        }
        preparedStatement.mSql = str;
        preparedStatement.mStatementPtr = i;
        preparedStatement.mNumParameters = i2;
        preparedStatement.mType = i3;
        preparedStatement.mReadOnly = z;
        return preparedStatement;
    }

    private void recyclePreparedStatement(PreparedStatement preparedStatement) {
        preparedStatement.mSql = null;
        preparedStatement.mPoolNext = this.mPreparedStatementPool;
        this.mPreparedStatementPool = preparedStatement;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String trimSqlForDisplay(String str) {
        return TRIM_SQL_PATTERN.matcher(str).replaceAll(" ");
    }

    private static final class PreparedStatement {
        public boolean mInCache;
        public boolean mInUse;
        public int mNumParameters;
        public PreparedStatement mPoolNext;
        public boolean mReadOnly;
        public String mSql;
        public int mStatementPtr;
        public int mType;

        private PreparedStatement() {
        }
    }

    private final class PreparedStatementCache extends LruCache<String, PreparedStatement> {
        public PreparedStatementCache(int i) {
            super(i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public void entryRemoved(boolean z, String str, PreparedStatement preparedStatement, PreparedStatement preparedStatement2) {
            preparedStatement.mInCache = false;
            if (preparedStatement.mInUse) {
                return;
            }
            SQLiteConnection.this.finalizePreparedStatement(preparedStatement);
        }

        public void dump(Printer printer) {
            printer.println("  Prepared statement cache:");
            Map<String, PreparedStatement> mapSnapshot = snapshot();
            if (!mapSnapshot.isEmpty()) {
                int i = 0;
                for (Map.Entry<String, PreparedStatement> entry : mapSnapshot.entrySet()) {
                    PreparedStatement value = entry.getValue();
                    if (value.mInCache) {
                        printer.println("    " + i + ": statementPtr=0x" + Integer.toHexString(value.mStatementPtr) + ", numParameters=" + value.mNumParameters + ", type=" + value.mType + ", readOnly=" + value.mReadOnly + ", sql=\"" + SQLiteConnection.trimSqlForDisplay(entry.getKey()) + "\"");
                    }
                    i++;
                }
                return;
            }
            printer.println("    <none>");
        }
    }

    private static final class OperationLog {
        private static final int COOKIE_GENERATION_SHIFT = 8;
        private static final int COOKIE_INDEX_MASK = 255;
        private static final int MAX_RECENT_OPERATIONS = 20;
        private int mGeneration;
        private int mIndex;
        private final Operation[] mOperations;

        private OperationLog() {
            this.mOperations = new Operation[20];
        }

        public int beginOperation(String str, String str2, Object[] objArr) {
            int i;
            synchronized (this.mOperations) {
                int i2 = (this.mIndex + 1) % 20;
                Operation operation = this.mOperations[i2];
                if (operation == null) {
                    operation = new Operation();
                    this.mOperations[i2] = operation;
                } else {
                    operation.mFinished = false;
                    operation.mException = null;
                    if (operation.mBindArgs != null) {
                        operation.mBindArgs.clear();
                    }
                }
                operation.mStartTime = System.currentTimeMillis();
                operation.mKind = str;
                operation.mSql = str2;
                if (objArr != null) {
                    if (operation.mBindArgs == null) {
                        operation.mBindArgs = new ArrayList<>();
                    } else {
                        operation.mBindArgs.clear();
                    }
                    for (Object obj : objArr) {
                        if (obj != null && (obj instanceof byte[])) {
                            operation.mBindArgs.add(SQLiteConnection.EMPTY_BYTE_ARRAY);
                        } else {
                            operation.mBindArgs.add(obj);
                        }
                    }
                }
                operation.mCookie = newOperationCookieLocked(i2);
                this.mIndex = i2;
                i = operation.mCookie;
            }
            return i;
        }

        public void failOperation(int i, Exception exc) {
            synchronized (this.mOperations) {
                Operation operationLocked = getOperationLocked(i);
                if (operationLocked != null) {
                    operationLocked.mException = exc;
                }
            }
        }

        public void endOperation(int i) {
            synchronized (this.mOperations) {
                if (endOperationDeferLogLocked(i)) {
                    logOperationLocked(i, null);
                }
            }
        }

        public boolean endOperationDeferLog(int i) {
            boolean zEndOperationDeferLogLocked;
            synchronized (this.mOperations) {
                zEndOperationDeferLogLocked = endOperationDeferLogLocked(i);
            }
            return zEndOperationDeferLogLocked;
        }

        public void logOperation(int i, String str) {
            synchronized (this.mOperations) {
                logOperationLocked(i, str);
            }
        }

        private boolean endOperationDeferLogLocked(int i) {
            Operation operationLocked = getOperationLocked(i);
            if (operationLocked == null) {
                return false;
            }
            operationLocked.mEndTime = System.currentTimeMillis();
            operationLocked.mFinished = true;
            return SQLiteDebug.DEBUG_LOG_SLOW_QUERIES && SQLiteDebug.shouldLogSlowQuery(operationLocked.mEndTime - operationLocked.mStartTime);
        }

        private void logOperationLocked(int i, String str) {
            Operation operationLocked = getOperationLocked(i);
            StringBuilder sb = new StringBuilder();
            operationLocked.describe(sb, false);
            if (str != null) {
                sb.append(", ").append(str);
            }
            Log.d(SQLiteConnection.TAG, sb.toString());
        }

        private int newOperationCookieLocked(int i) {
            int i2 = this.mGeneration;
            this.mGeneration = i2 + 1;
            return i | (i2 << 8);
        }

        private Operation getOperationLocked(int i) {
            Operation operation = this.mOperations[i & 255];
            if (operation.mCookie == i) {
                return operation;
            }
            return null;
        }

        public String describeCurrentOperation() {
            synchronized (this.mOperations) {
                Operation operation = this.mOperations[this.mIndex];
                if (operation == null || operation.mFinished) {
                    return null;
                }
                StringBuilder sb = new StringBuilder();
                operation.describe(sb, false);
                return sb.toString();
            }
        }

        public void dump(Printer printer, boolean z) {
            synchronized (this.mOperations) {
                printer.println("  Most recently executed operations:");
                int i = this.mIndex;
                Operation operation = this.mOperations[i];
                if (operation != null) {
                    int i2 = 0;
                    do {
                        StringBuilder sb = new StringBuilder();
                        sb.append("    ").append(i2).append(": [");
                        sb.append(operation.getFormattedStartTime());
                        sb.append("] ");
                        operation.describe(sb, z);
                        printer.println(sb.toString());
                        i = i > 0 ? i - 1 : 19;
                        i2++;
                        operation = this.mOperations[i];
                        if (operation == null) {
                            break;
                        }
                    } while (i2 < 20);
                } else {
                    printer.println("    <none>");
                }
            }
        }
    }

    private static final class Operation {
        private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        public ArrayList<Object> mBindArgs;
        public int mCookie;
        public long mEndTime;
        public Exception mException;
        public boolean mFinished;
        public String mKind;
        public String mSql;
        public long mStartTime;

        private Operation() {
        }

        public void describe(StringBuilder sb, boolean z) {
            ArrayList<Object> arrayList;
            sb.append(this.mKind);
            if (this.mFinished) {
                sb.append(" took ").append(this.mEndTime - this.mStartTime).append("ms");
            } else {
                sb.append(" started ").append(System.currentTimeMillis() - this.mStartTime).append("ms ago");
            }
            sb.append(" - ").append(getStatus());
            if (this.mSql != null) {
                sb.append(", sql=\"").append(SQLiteConnection.trimSqlForDisplay(this.mSql)).append("\"");
            }
            if (z && (arrayList = this.mBindArgs) != null && arrayList.size() != 0) {
                sb.append(", bindArgs=[");
                int size = this.mBindArgs.size();
                for (int i = 0; i < size; i++) {
                    Object obj = this.mBindArgs.get(i);
                    if (i != 0) {
                        sb.append(", ");
                    }
                    if (obj == null) {
                        sb.append("null");
                    } else if (obj instanceof byte[]) {
                        sb.append("<byte[]>");
                    } else if (obj instanceof String) {
                        sb.append("\"").append((String) obj).append("\"");
                    } else {
                        sb.append(obj);
                    }
                }
                sb.append("]");
            }
            if (this.mException != null) {
                sb.append(", exception=\"").append(this.mException.getMessage()).append("\"");
            }
        }

        private String getStatus() {
            return !this.mFinished ? "running" : this.mException != null ? "failed" : "succeeded";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getFormattedStartTime() {
            return sDateFormat.format(new Date(this.mStartTime));
        }
    }
}

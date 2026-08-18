package android.provider;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import java.io.FileNotFoundException;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public abstract class DocumentsProvider extends ContentProvider {
    private static final int MATCH_CHILDREN = 6;
    private static final int MATCH_DOCUMENT = 5;
    private static final int MATCH_RECENT = 3;
    private static final int MATCH_ROOT = 2;
    private static final int MATCH_ROOTS = 1;
    private static final int MATCH_SEARCH = 4;
    private static final String TAG = "DocumentsProvider";
    private String mAuthority;
    private UriMatcher mMatcher;

    public abstract ParcelFileDescriptor openDocument(String str, String str2, CancellationSignal cancellationSignal) throws FileNotFoundException;

    public abstract Cursor queryChildDocuments(String str, String[] strArr, String str2) throws FileNotFoundException;

    public abstract Cursor queryDocument(String str, String[] strArr) throws FileNotFoundException;

    public abstract Cursor queryRoots(String[] strArr) throws FileNotFoundException;

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo providerInfo) {
        this.mAuthority = providerInfo.authority;
        UriMatcher uriMatcher = new UriMatcher(-1);
        this.mMatcher = uriMatcher;
        uriMatcher.addURI(this.mAuthority, "root", 1);
        this.mMatcher.addURI(this.mAuthority, "root/*", 2);
        this.mMatcher.addURI(this.mAuthority, "root/*/recent", 3);
        this.mMatcher.addURI(this.mAuthority, "root/*/search", 4);
        this.mMatcher.addURI(this.mAuthority, "document/*", 5);
        this.mMatcher.addURI(this.mAuthority, "document/*/children", 6);
        if (!providerInfo.exported) {
            throw new SecurityException("Provider must be exported");
        }
        if (!providerInfo.grantUriPermissions) {
            throw new SecurityException("Provider must grantUriPermissions");
        }
        if (!Manifest.permission.MANAGE_DOCUMENTS.equals(providerInfo.readPermission) || !Manifest.permission.MANAGE_DOCUMENTS.equals(providerInfo.writePermission)) {
            throw new SecurityException("Provider must be protected by MANAGE_DOCUMENTS");
        }
        super.attachInfo(context, providerInfo);
    }

    public String createDocument(String str, String str2, String str3) throws FileNotFoundException {
        throw new UnsupportedOperationException("Create not supported");
    }

    public void deleteDocument(String str) throws FileNotFoundException {
        throw new UnsupportedOperationException("Delete not supported");
    }

    public Cursor queryRecentDocuments(String str, String[] strArr) throws FileNotFoundException {
        throw new UnsupportedOperationException("Recent not supported");
    }

    public Cursor queryChildDocumentsForManage(String str, String[] strArr, String str2) throws FileNotFoundException {
        throw new UnsupportedOperationException("Manage not supported");
    }

    public Cursor querySearchDocuments(String str, String str2, String[] strArr) throws FileNotFoundException {
        throw new UnsupportedOperationException("Search not supported");
    }

    public String getDocumentType(String str) throws FileNotFoundException {
        Cursor cursorQueryDocument = queryDocument(str, null);
        try {
            if (cursorQueryDocument.moveToFirst()) {
                return cursorQueryDocument.getString(cursorQueryDocument.getColumnIndexOrThrow("mime_type"));
            }
            return null;
        } finally {
            IoUtils.closeQuietly(cursorQueryDocument);
        }
    }

    public AssetFileDescriptor openDocumentThumbnail(String str, Point point, CancellationSignal cancellationSignal) throws FileNotFoundException {
        throw new UnsupportedOperationException("Thumbnails not supported");
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        try {
            int iMatch = this.mMatcher.match(uri);
            if (iMatch == 1) {
                return queryRoots(strArr);
            }
            if (iMatch == 3) {
                return queryRecentDocuments(DocumentsContract.getRootId(uri), strArr);
            }
            if (iMatch == 4) {
                return querySearchDocuments(DocumentsContract.getRootId(uri), DocumentsContract.getSearchDocumentsQuery(uri), strArr);
            }
            if (iMatch == 5) {
                return queryDocument(DocumentsContract.getDocumentId(uri), strArr);
            }
            if (iMatch == 6) {
                if (DocumentsContract.isManageMode(uri)) {
                    return queryChildDocumentsForManage(DocumentsContract.getDocumentId(uri), strArr, str2);
                }
                return queryChildDocuments(DocumentsContract.getDocumentId(uri), strArr, str2);
            }
            throw new UnsupportedOperationException("Unsupported Uri " + uri);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed during query", e);
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public final String getType(Uri uri) {
        try {
            int iMatch = this.mMatcher.match(uri);
            if (iMatch == 2) {
                return DocumentsContract.Root.MIME_TYPE_ITEM;
            }
            if (iMatch != 5) {
                return null;
            }
            return getDocumentType(DocumentsContract.getDocumentId(uri));
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed during getType", e);
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Insert not supported");
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Update not supported");
    }

    @Override // android.content.ContentProvider
    public Bundle call(String str, String str2, Bundle bundle) {
        Context context = getContext();
        if (!str.startsWith("android:")) {
            return super.call(str, str2, bundle);
        }
        String string = bundle.getString("document_id");
        Uri uriBuildDocumentUri = DocumentsContract.buildDocumentUri(this.mAuthority, string);
        boolean z = context.checkCallingOrSelfPermission(Manifest.permission.MANAGE_DOCUMENTS) == 0;
        enforceWritePermissionInner(uriBuildDocumentUri);
        Bundle bundle2 = new Bundle();
        try {
            if (DocumentsContract.METHOD_CREATE_DOCUMENT.equals(str)) {
                String strCreateDocument = createDocument(string, bundle.getString("mime_type"), bundle.getString("_display_name"));
                bundle2.putString("document_id", strCreateDocument);
                if (!z) {
                    context.grantUriPermission(getCallingPackage(), DocumentsContract.buildDocumentUri(this.mAuthority, strCreateDocument), 67);
                }
            } else if (DocumentsContract.METHOD_DELETE_DOCUMENT.equals(str)) {
                deleteDocument(string);
                context.revokeUriPermission(uriBuildDocumentUri, 67);
            } else {
                throw new UnsupportedOperationException("Method not supported " + str);
            }
            return bundle2;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Failed call " + str, e);
        }
    }

    @Override // android.content.ContentProvider
    public final ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        return openDocument(DocumentsContract.getDocumentId(uri), str, null);
    }

    @Override // android.content.ContentProvider
    public final ParcelFileDescriptor openFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openDocument(DocumentsContract.getDocumentId(uri), str, cancellationSignal);
    }

    @Override // android.content.ContentProvider
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle) throws FileNotFoundException {
        if (bundle != null && bundle.containsKey(DocumentsContract.EXTRA_THUMBNAIL_SIZE)) {
            return openDocumentThumbnail(DocumentsContract.getDocumentId(uri), (Point) bundle.getParcelable(DocumentsContract.EXTRA_THUMBNAIL_SIZE), null);
        }
        return super.openTypedAssetFile(uri, str, bundle);
    }

    @Override // android.content.ContentProvider
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle, CancellationSignal cancellationSignal) throws FileNotFoundException {
        if (bundle != null && bundle.containsKey(DocumentsContract.EXTRA_THUMBNAIL_SIZE)) {
            return openDocumentThumbnail(DocumentsContract.getDocumentId(uri), (Point) bundle.getParcelable(DocumentsContract.EXTRA_THUMBNAIL_SIZE), cancellationSignal);
        }
        return super.openTypedAssetFile(uri, str, bundle, cancellationSignal);
    }
}

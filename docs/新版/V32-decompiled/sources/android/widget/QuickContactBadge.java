package android.widget;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;
import org.apache.commons.net.telnet.TelnetCommand;

/* JADX INFO: loaded from: classes.dex */
public class QuickContactBadge extends ImageView implements View.OnClickListener {
    static final int EMAIL_ID_COLUMN_INDEX = 0;
    static final int EMAIL_LOOKUP_STRING_COLUMN_INDEX = 1;
    private static final String EXTRA_URI_CONTENT = "uri_content";
    static final int PHONE_ID_COLUMN_INDEX = 0;
    static final int PHONE_LOOKUP_STRING_COLUMN_INDEX = 1;
    private static final int TOKEN_EMAIL_LOOKUP = 0;
    private static final int TOKEN_EMAIL_LOOKUP_AND_TRIGGER = 2;
    private static final int TOKEN_PHONE_LOOKUP = 1;
    private static final int TOKEN_PHONE_LOOKUP_AND_TRIGGER = 3;
    private String mContactEmail;
    private String mContactPhone;
    private Uri mContactUri;
    private Drawable mDefaultAvatar;
    protected String[] mExcludeMimes;
    private Bundle mExtras;
    private Drawable mOverlay;
    private QueryHandler mQueryHandler;
    static final String[] EMAIL_LOOKUP_PROJECTION = {"contact_id", ContactsContract.ContactsColumns.LOOKUP_KEY};
    static final String[] PHONE_LOOKUP_PROJECTION = {"_id", ContactsContract.ContactsColumns.LOOKUP_KEY};

    public void setMode(int i) {
    }

    public QuickContactBadge(Context context) {
        this(context, null);
    }

    public QuickContactBadge(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QuickContactBadge(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mExtras = null;
        this.mExcludeMimes = null;
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(R.styleable.Theme);
        this.mOverlay = typedArrayObtainStyledAttributes.getDrawable(TelnetCommand.EOF);
        typedArrayObtainStyledAttributes.recycle();
        if (!isInEditMode()) {
            this.mQueryHandler = new QueryHandler(this.mContext.getContentResolver());
        }
        setOnClickListener(this);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mOverlay;
        if (drawable == null || !drawable.isStateful()) {
            return;
        }
        this.mOverlay.setState(getDrawableState());
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable;
        super.onDraw(canvas);
        if (!isEnabled() || (drawable = this.mOverlay) == null || drawable.getIntrinsicWidth() == 0 || this.mOverlay.getIntrinsicHeight() == 0) {
            return;
        }
        this.mOverlay.setBounds(0, 0, getWidth(), getHeight());
        if (this.mPaddingTop == 0 && this.mPaddingLeft == 0) {
            this.mOverlay.draw(canvas);
            return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(this.mPaddingLeft, this.mPaddingTop);
        this.mOverlay.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private boolean isAssigned() {
        return (this.mContactUri == null && this.mContactEmail == null && this.mContactPhone == null) ? false : true;
    }

    public void setImageToDefault() {
        if (this.mDefaultAvatar == null) {
            this.mDefaultAvatar = getResources().getDrawable(17302218);
        }
        setImageDrawable(this.mDefaultAvatar);
    }

    public void assignContactUri(Uri uri) {
        this.mContactUri = uri;
        this.mContactEmail = null;
        this.mContactPhone = null;
        onContactUriChanged();
    }

    public void assignContactFromEmail(String str, boolean z) {
        assignContactFromEmail(str, z, null);
    }

    public void assignContactFromEmail(String str, boolean z, Bundle bundle) {
        QueryHandler queryHandler;
        this.mContactEmail = str;
        this.mExtras = bundle;
        if (!z && (queryHandler = this.mQueryHandler) != null) {
            queryHandler.startQuery(0, null, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(this.mContactEmail)), EMAIL_LOOKUP_PROJECTION, null, null, null);
        } else {
            this.mContactUri = null;
            onContactUriChanged();
        }
    }

    public void assignContactFromPhone(String str, boolean z) {
        assignContactFromPhone(str, z, new Bundle());
    }

    public void assignContactFromPhone(String str, boolean z, Bundle bundle) {
        QueryHandler queryHandler;
        this.mContactPhone = str;
        this.mExtras = bundle;
        if (!z && (queryHandler = this.mQueryHandler) != null) {
            queryHandler.startQuery(1, null, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, this.mContactPhone), PHONE_LOOKUP_PROJECTION, null, null, null);
        } else {
            this.mContactUri = null;
            onContactUriChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onContactUriChanged() {
        setEnabled(isAssigned());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            bundle = new Bundle();
        }
        Bundle bundle2 = bundle;
        if (this.mContactUri != null) {
            ContactsContract.QuickContact.showQuickContact(getContext(), this, this.mContactUri, 3, this.mExcludeMimes);
            return;
        }
        String str = this.mContactEmail;
        if (str != null && this.mQueryHandler != null) {
            bundle2.putString(EXTRA_URI_CONTENT, str);
            this.mQueryHandler.startQuery(2, bundle2, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(this.mContactEmail)), EMAIL_LOOKUP_PROJECTION, null, null, null);
            return;
        }
        String str2 = this.mContactPhone;
        if (str2 == null || this.mQueryHandler == null) {
            return;
        }
        bundle2.putString(EXTRA_URI_CONTENT, str2);
        this.mQueryHandler.startQuery(3, bundle2, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, this.mContactPhone), PHONE_LOOKUP_PROJECTION, null, null, null);
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(QuickContactBadge.class.getName());
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(QuickContactBadge.class.getName());
    }

    public void setExcludeMimes(String[] strArr) {
        this.mExcludeMimes = strArr;
    }

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        /* JADX WARN: Removed duplicated region for block: B:32:0x006e  */
        /* JADX WARN: Removed duplicated region for block: B:35:0x007d A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:38:0x008f  */
        /* JADX WARN: Removed duplicated region for block: B:45:? A[RETURN, SYNTHETIC] */
        @Override // android.content.AsyncQueryHandler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onQueryComplete(int r8, java.lang.Object r9, android.database.Cursor r10) {
            /*
                r7 = this;
                if (r9 == 0) goto L5
                android.os.Bundle r9 = (android.os.Bundle) r9
                goto La
            L5:
                android.os.Bundle r9 = new android.os.Bundle
                r9.<init>()
            La:
                r0 = 3
                java.lang.String r1 = "uri_content"
                r2 = 0
                r3 = 0
                r4 = 1
                if (r8 == 0) goto L4e
                if (r8 == r4) goto L37
                r5 = 2
                if (r8 == r5) goto L29
                if (r8 == r0) goto L1c
                r8 = r3
                goto L6c
            L1c:
                java.lang.String r8 = "tel"
                java.lang.String r5 = r9.getString(r1)     // Catch: java.lang.Throwable -> L35
                android.net.Uri r8 = android.net.Uri.fromParts(r8, r5, r3)     // Catch: java.lang.Throwable -> L35
                r5 = r4
                goto L39
            L29:
                java.lang.String r8 = "mailto"
                java.lang.String r5 = r9.getString(r1)     // Catch: java.lang.Throwable -> L35
                android.net.Uri r8 = android.net.Uri.fromParts(r8, r5, r3)     // Catch: java.lang.Throwable -> L35
                r5 = r4
                goto L50
            L35:
                r8 = move-exception
                goto L65
            L37:
                r5 = r2
                r8 = r3
            L39:
                if (r10 == 0) goto L6b
                boolean r6 = r10.moveToFirst()     // Catch: java.lang.Throwable -> L35
                if (r6 == 0) goto L6b
                long r2 = r10.getLong(r2)     // Catch: java.lang.Throwable -> L35
                java.lang.String r4 = r10.getString(r4)     // Catch: java.lang.Throwable -> L35
                android.net.Uri r3 = android.provider.ContactsContract.Contacts.getLookupUri(r2, r4)     // Catch: java.lang.Throwable -> L35
                goto L6b
            L4e:
                r5 = r2
                r8 = r3
            L50:
                if (r10 == 0) goto L6b
                boolean r6 = r10.moveToFirst()     // Catch: java.lang.Throwable -> L35
                if (r6 == 0) goto L6b
                long r2 = r10.getLong(r2)     // Catch: java.lang.Throwable -> L35
                java.lang.String r4 = r10.getString(r4)     // Catch: java.lang.Throwable -> L35
                android.net.Uri r3 = android.provider.ContactsContract.Contacts.getLookupUri(r2, r4)     // Catch: java.lang.Throwable -> L35
                goto L6b
            L65:
                if (r10 == 0) goto L6a
                r10.close()
            L6a:
                throw r8
            L6b:
                r2 = r5
            L6c:
                if (r10 == 0) goto L71
                r10.close()
            L71:
                android.widget.QuickContactBadge r10 = android.widget.QuickContactBadge.this
                android.widget.QuickContactBadge.access$002(r10, r3)
                android.widget.QuickContactBadge r10 = android.widget.QuickContactBadge.this
                android.widget.QuickContactBadge.access$100(r10)
                if (r2 == 0) goto L8d
                if (r3 == 0) goto L8d
                android.widget.QuickContactBadge r8 = android.widget.QuickContactBadge.this
                android.content.Context r8 = r8.getContext()
                android.widget.QuickContactBadge r9 = android.widget.QuickContactBadge.this
                java.lang.String[] r10 = r9.mExcludeMimes
                android.provider.ContactsContract.QuickContact.showQuickContact(r8, r9, r3, r0, r10)
                goto La7
            L8d:
                if (r8 == 0) goto La7
                android.content.Intent r10 = new android.content.Intent
                java.lang.String r0 = "com.android.contacts.action.SHOW_OR_CREATE_CONTACT"
                r10.<init>(r0, r8)
                if (r9 == 0) goto L9e
                r9.remove(r1)
                r10.putExtras(r9)
            L9e:
                android.widget.QuickContactBadge r8 = android.widget.QuickContactBadge.this
                android.content.Context r8 = r8.getContext()
                r8.startActivity(r10)
            La7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.QuickContactBadge.QueryHandler.onQueryComplete(int, java.lang.Object, android.database.Cursor):void");
        }
    }
}

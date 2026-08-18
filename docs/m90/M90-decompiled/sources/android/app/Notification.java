package android.app;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Notification implements Parcelable {
    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() { // from class: android.app.Notification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Notification createFromParcel(Parcel parcel) {
            return new Notification(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Notification[] newArray(int i) {
            return new Notification[i];
        }
    };
    public static final int DEFAULT_ALL = -1;
    public static final int DEFAULT_LIGHTS = 4;
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;
    public static final String EXTRA_AS_HEADS_UP = "headsup";
    public static final String EXTRA_INFO_TEXT = "android.infoText";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
    public static final String EXTRA_PEOPLE = "android.people";
    public static final String EXTRA_PICTURE = "android.picture";
    public static final String EXTRA_PROGRESS = "android.progress";
    public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
    public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
    public static final String EXTRA_SCORE_MODIFIED = "android.scoreModified";
    public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
    public static final String EXTRA_SHOW_WHEN = "android.showWhen";
    public static final String EXTRA_SMALL_ICON = "android.icon";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_TEXT_LINES = "android.textLines";
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TITLE_BIG = "android.title.big";
    public static final int FLAG_AUTO_CANCEL = 16;
    public static final int FLAG_FOREGROUND_SERVICE = 64;
    public static final int FLAG_HIGH_PRIORITY = 128;
    public static final int FLAG_INSISTENT = 4;
    public static final int FLAG_NO_CLEAR = 32;
    public static final int FLAG_ONGOING_EVENT = 2;
    public static final int FLAG_ONLY_ALERT_ONCE = 8;
    public static final int FLAG_SHOW_LIGHTS = 1;
    public static final int HEADS_UP_ALLOWED = 1;
    public static final int HEADS_UP_NEVER = 0;
    public static final int HEADS_UP_REQUESTED = 2;
    public static final String KIND_CALL = "android.call";
    public static final String KIND_EMAIL = "android.email";
    public static final String KIND_EVENT = "android.event";
    public static final String KIND_MESSAGE = "android.message";
    public static final String KIND_PROMO = "android.promo";
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MAX = 2;
    public static final int PRIORITY_MIN = -2;
    public static final int STREAM_DEFAULT = -1;
    private static final String TAG = "Notification";
    public Action[] actions;
    public int audioStreamType;
    public RemoteViews bigContentView;
    public PendingIntent contentIntent;
    public RemoteViews contentView;
    public int defaults;
    public PendingIntent deleteIntent;
    public Bundle extras;
    public int flags;
    public PendingIntent fullScreenIntent;
    public int icon;
    public int iconLevel;
    public String[] kind;
    public Bitmap largeIcon;
    public int ledARGB;
    public int ledOffMS;
    public int ledOnMS;
    public int number;
    public int priority;
    public Uri sound;
    public CharSequence tickerText;
    public RemoteViews tickerView;
    public long[] vibrate;
    public long when;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class Action implements Parcelable {
        public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() { // from class: android.app.Notification.Action.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Action createFromParcel(Parcel parcel) {
                return new Action(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Action[] newArray(int i) {
                return new Action[i];
            }
        };
        public PendingIntent actionIntent;
        public int icon;
        public CharSequence title;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        private Action() {
        }

        private Action(Parcel parcel) {
            this.icon = parcel.readInt();
            this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            if (parcel.readInt() == 1) {
                this.actionIntent = PendingIntent.CREATOR.createFromParcel(parcel);
            }
        }

        public Action(int i, CharSequence charSequence, PendingIntent pendingIntent) {
            this.icon = i;
            this.title = charSequence;
            this.actionIntent = pendingIntent;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public Action m6clone() {
            return new Action(this.icon, this.title, this.actionIntent);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.icon);
            TextUtils.writeToParcel(this.title, parcel, i);
            if (this.actionIntent != null) {
                parcel.writeInt(1);
                this.actionIntent.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
        }
    }

    public Notification() {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        this.when = System.currentTimeMillis();
        this.priority = 0;
    }

    public Notification(Context context, int i, CharSequence charSequence, long j, CharSequence charSequence2, CharSequence charSequence3, Intent intent) {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        this.when = j;
        this.icon = i;
        this.tickerText = charSequence;
        setLatestEventInfo(context, charSequence2, charSequence3, PendingIntent.getActivity(context, 0, intent, 0));
    }

    @Deprecated
    public Notification(int i, CharSequence charSequence, long j) {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        this.icon = i;
        this.tickerText = charSequence;
        this.when = j;
    }

    public Notification(Parcel parcel) {
        this.audioStreamType = -1;
        this.extras = new Bundle();
        parcel.readInt();
        this.when = parcel.readLong();
        this.icon = parcel.readInt();
        this.number = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.contentIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.deleteIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.contentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.largeIcon = Bitmap.CREATOR.createFromParcel(parcel);
        }
        this.defaults = parcel.readInt();
        this.flags = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.sound = Uri.CREATOR.createFromParcel(parcel);
        }
        this.audioStreamType = parcel.readInt();
        this.vibrate = parcel.createLongArray();
        this.ledARGB = parcel.readInt();
        this.ledOnMS = parcel.readInt();
        this.ledOffMS = parcel.readInt();
        this.iconLevel = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.fullScreenIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        this.priority = parcel.readInt();
        this.kind = parcel.createStringArray();
        this.extras = parcel.readBundle();
        this.actions = (Action[]) parcel.createTypedArray(Action.CREATOR);
        if (parcel.readInt() != 0) {
            this.bigContentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Notification m5clone() {
        Notification notification = new Notification();
        cloneInto(notification, true);
        return notification;
    }

    public void cloneInto(Notification notification, boolean z) {
        RemoteViews remoteViews;
        Bitmap bitmap;
        RemoteViews remoteViews2;
        RemoteViews remoteViews3;
        notification.when = this.when;
        notification.icon = this.icon;
        notification.number = this.number;
        notification.contentIntent = this.contentIntent;
        notification.deleteIntent = this.deleteIntent;
        notification.fullScreenIntent = this.fullScreenIntent;
        CharSequence charSequence = this.tickerText;
        if (charSequence != null) {
            notification.tickerText = charSequence.toString();
        }
        if (z && (remoteViews3 = this.tickerView) != null) {
            notification.tickerView = remoteViews3.m22clone();
        }
        if (z && (remoteViews2 = this.contentView) != null) {
            notification.contentView = remoteViews2.m22clone();
        }
        if (z && (bitmap = this.largeIcon) != null) {
            notification.largeIcon = Bitmap.createBitmap(bitmap);
        }
        notification.iconLevel = this.iconLevel;
        notification.sound = this.sound;
        notification.audioStreamType = this.audioStreamType;
        long[] jArr = this.vibrate;
        int i = 0;
        if (jArr != null) {
            int length = jArr.length;
            long[] jArr2 = new long[length];
            notification.vibrate = jArr2;
            System.arraycopy(jArr, 0, jArr2, 0, length);
        }
        notification.ledARGB = this.ledARGB;
        notification.ledOnMS = this.ledOnMS;
        notification.ledOffMS = this.ledOffMS;
        notification.defaults = this.defaults;
        notification.flags = this.flags;
        notification.priority = this.priority;
        String[] strArr = this.kind;
        if (strArr != null) {
            int length2 = strArr.length;
            String[] strArr2 = new String[length2];
            notification.kind = strArr2;
            System.arraycopy(strArr, 0, strArr2, 0, length2);
        }
        if (this.extras != null) {
            try {
                Bundle bundle = new Bundle(this.extras);
                notification.extras = bundle;
                bundle.size();
            } catch (BadParcelableException e) {
                Log.e(TAG, "could not unparcel extras from notification: " + this, e);
                notification.extras = null;
            }
        }
        Action[] actionArr = this.actions;
        if (actionArr != null) {
            notification.actions = new Action[actionArr.length];
            while (true) {
                Action[] actionArr2 = this.actions;
                if (i >= actionArr2.length) {
                    break;
                }
                notification.actions[i] = actionArr2[i].m6clone();
                i++;
            }
        }
        if (z && (remoteViews = this.bigContentView) != null) {
            notification.bigContentView = remoteViews.m22clone();
        }
        if (z) {
            return;
        }
        notification.lightenPayload();
    }

    public final void lightenPayload() {
        this.tickerView = null;
        this.contentView = null;
        this.bigContentView = null;
        this.largeIcon = null;
        Bundle bundle = this.extras;
        if (bundle != null) {
            bundle.remove("android.largeIcon");
            this.extras.remove("android.largeIcon.big");
            this.extras.remove("android.picture");
        }
    }

    public static CharSequence safeCharSequence(CharSequence charSequence) {
        if (!(charSequence instanceof Parcelable)) {
            return charSequence;
        }
        Log.e(TAG, "warning: " + charSequence.getClass().getCanonicalName() + " instance is a custom Parcelable and not allowed in Notification");
        return charSequence.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(1);
        parcel.writeLong(this.when);
        parcel.writeInt(this.icon);
        parcel.writeInt(this.number);
        if (this.contentIntent != null) {
            parcel.writeInt(1);
            this.contentIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.deleteIntent != null) {
            parcel.writeInt(1);
            this.deleteIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.tickerText != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.tickerText, parcel, i);
        } else {
            parcel.writeInt(0);
        }
        if (this.tickerView != null) {
            parcel.writeInt(1);
            this.tickerView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.contentView != null) {
            parcel.writeInt(1);
            this.contentView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.largeIcon != null) {
            parcel.writeInt(1);
            this.largeIcon.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.defaults);
        parcel.writeInt(this.flags);
        if (this.sound != null) {
            parcel.writeInt(1);
            this.sound.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.audioStreamType);
        parcel.writeLongArray(this.vibrate);
        parcel.writeInt(this.ledARGB);
        parcel.writeInt(this.ledOnMS);
        parcel.writeInt(this.ledOffMS);
        parcel.writeInt(this.iconLevel);
        if (this.fullScreenIntent != null) {
            parcel.writeInt(1);
            this.fullScreenIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.priority);
        parcel.writeStringArray(this.kind);
        parcel.writeBundle(this.extras);
        parcel.writeTypedArray(this.actions, 0);
        if (this.bigContentView != null) {
            parcel.writeInt(1);
            this.bigContentView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
    }

    @Deprecated
    public void setLatestEventInfo(Context context, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent) {
        Builder builder = new Builder(context);
        builder.setWhen(this.when);
        builder.setSmallIcon(this.icon);
        builder.setPriority(this.priority);
        builder.setTicker(this.tickerText);
        builder.setNumber(this.number);
        builder.mFlags = this.flags;
        builder.setSound(this.sound, this.audioStreamType);
        builder.setDefaults(this.defaults);
        builder.setVibrate(this.vibrate);
        if (charSequence != null) {
            builder.setContentTitle(charSequence);
        }
        if (charSequence2 != null) {
            builder.setContentText(charSequence2);
        }
        builder.setContentIntent(pendingIntent);
        builder.buildInto(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notification(pri=");
        sb.append(this.priority);
        sb.append(" contentView=");
        RemoteViews remoteViews = this.contentView;
        if (remoteViews != null) {
            sb.append(remoteViews.getPackage());
            sb.append("/0x");
            sb.append(Integer.toHexString(this.contentView.getLayoutId()));
        } else {
            sb.append("null");
        }
        sb.append(" vibrate=");
        if ((this.defaults & 2) != 0) {
            sb.append("default");
        } else {
            long[] jArr = this.vibrate;
            if (jArr != null) {
                int length = jArr.length - 1;
                sb.append("[");
                for (int i = 0; i < length; i++) {
                    sb.append(this.vibrate[i]);
                    sb.append(PhoneNumberUtils.PAUSE);
                }
                if (length != -1) {
                    sb.append(this.vibrate[length]);
                }
                sb.append("]");
            } else {
                sb.append("null");
            }
        }
        sb.append(" sound=");
        if ((this.defaults & 1) != 0) {
            sb.append("default");
        } else {
            Uri uri = this.sound;
            if (uri != null) {
                sb.append(uri.toString());
            } else {
                sb.append("null");
            }
        }
        sb.append(" defaults=0x");
        sb.append(Integer.toHexString(this.defaults));
        sb.append(" flags=0x");
        sb.append(Integer.toHexString(this.flags));
        sb.append(" kind=[");
        if (this.kind == null) {
            sb.append("null");
        } else {
            for (int i2 = 0; i2 < this.kind.length; i2++) {
                if (i2 > 0) {
                    sb.append(",");
                }
                sb.append(this.kind[i2]);
            }
        }
        sb.append("]");
        if (this.actions != null) {
            sb.append(" ");
            sb.append(this.actions.length);
            sb.append(" action");
            if (this.actions.length > 1) {
                sb.append("s");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public void setUser(UserHandle userHandle) {
        if (userHandle.getIdentifier() == -1) {
            userHandle = UserHandle.OWNER;
        }
        RemoteViews remoteViews = this.tickerView;
        if (remoteViews != null) {
            remoteViews.setUser(userHandle);
        }
        RemoteViews remoteViews2 = this.contentView;
        if (remoteViews2 != null) {
            remoteViews2.setUser(userHandle);
        }
        RemoteViews remoteViews3 = this.bigContentView;
        if (remoteViews3 != null) {
            remoteViews3.setUser(userHandle);
        }
    }

    public static class Builder {
        private static final int MAX_ACTION_BUTTONS = 3;
        private CharSequence mContentInfo;
        private PendingIntent mContentIntent;
        private CharSequence mContentText;
        private CharSequence mContentTitle;
        private RemoteViews mContentView;
        private Context mContext;
        private int mDefaults;
        private PendingIntent mDeleteIntent;
        private Bundle mExtras;
        private int mFlags;
        private PendingIntent mFullScreenIntent;
        private Bitmap mLargeIcon;
        private int mLedArgb;
        private int mLedOffMs;
        private int mLedOnMs;
        private int mNumber;
        private int mProgress;
        private boolean mProgressIndeterminate;
        private int mProgressMax;
        private int mSmallIcon;
        private int mSmallIconLevel;
        private Uri mSound;
        private Style mStyle;
        private CharSequence mSubText;
        private CharSequence mTickerText;
        private RemoteViews mTickerView;
        private boolean mUseChronometer;
        private long[] mVibrate;
        private ArrayList<String> mKindList = new ArrayList<>(1);
        private ArrayList<Action> mActions = new ArrayList<>(3);
        private boolean mShowWhen = true;
        private long mWhen = System.currentTimeMillis();
        private int mAudioStreamType = -1;
        private int mPriority = 0;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setWhen(long j) {
            this.mWhen = j;
            return this;
        }

        public Builder setShowWhen(boolean z) {
            this.mShowWhen = z;
            return this;
        }

        public Builder setUsesChronometer(boolean z) {
            this.mUseChronometer = z;
            return this;
        }

        public Builder setSmallIcon(int i) {
            this.mSmallIcon = i;
            return this;
        }

        public Builder setSmallIcon(int i, int i2) {
            this.mSmallIcon = i;
            this.mSmallIconLevel = i2;
            return this;
        }

        public Builder setContentTitle(CharSequence charSequence) {
            this.mContentTitle = Notification.safeCharSequence(charSequence);
            return this;
        }

        public Builder setContentText(CharSequence charSequence) {
            this.mContentText = Notification.safeCharSequence(charSequence);
            return this;
        }

        public Builder setSubText(CharSequence charSequence) {
            this.mSubText = Notification.safeCharSequence(charSequence);
            return this;
        }

        public Builder setNumber(int i) {
            this.mNumber = i;
            return this;
        }

        public Builder setContentInfo(CharSequence charSequence) {
            this.mContentInfo = Notification.safeCharSequence(charSequence);
            return this;
        }

        public Builder setProgress(int i, int i2, boolean z) {
            this.mProgressMax = i;
            this.mProgress = i2;
            this.mProgressIndeterminate = z;
            return this;
        }

        public Builder setContent(RemoteViews remoteViews) {
            this.mContentView = remoteViews;
            return this;
        }

        public Builder setContentIntent(PendingIntent pendingIntent) {
            this.mContentIntent = pendingIntent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent pendingIntent) {
            this.mDeleteIntent = pendingIntent;
            return this;
        }

        public Builder setFullScreenIntent(PendingIntent pendingIntent, boolean z) {
            this.mFullScreenIntent = pendingIntent;
            setFlag(128, z);
            return this;
        }

        public Builder setTicker(CharSequence charSequence) {
            this.mTickerText = Notification.safeCharSequence(charSequence);
            return this;
        }

        public Builder setTicker(CharSequence charSequence, RemoteViews remoteViews) {
            this.mTickerText = Notification.safeCharSequence(charSequence);
            this.mTickerView = remoteViews;
            return this;
        }

        public Builder setLargeIcon(Bitmap bitmap) {
            this.mLargeIcon = bitmap;
            return this;
        }

        public Builder setSound(Uri uri) {
            this.mSound = uri;
            this.mAudioStreamType = -1;
            return this;
        }

        public Builder setSound(Uri uri, int i) {
            this.mSound = uri;
            this.mAudioStreamType = i;
            return this;
        }

        public Builder setVibrate(long[] jArr) {
            this.mVibrate = jArr;
            return this;
        }

        public Builder setLights(int i, int i2, int i3) {
            this.mLedArgb = i;
            this.mLedOnMs = i2;
            this.mLedOffMs = i3;
            return this;
        }

        public Builder setOngoing(boolean z) {
            setFlag(2, z);
            return this;
        }

        public Builder setOnlyAlertOnce(boolean z) {
            setFlag(8, z);
            return this;
        }

        public Builder setAutoCancel(boolean z) {
            setFlag(16, z);
            return this;
        }

        public Builder setDefaults(int i) {
            this.mDefaults = i;
            return this;
        }

        public Builder setPriority(int i) {
            this.mPriority = i;
            return this;
        }

        public Builder addKind(String str) {
            this.mKindList.add(str);
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            this.mExtras = bundle;
            return this;
        }

        public Builder addAction(int i, CharSequence charSequence, PendingIntent pendingIntent) {
            this.mActions.add(new Action(i, Notification.safeCharSequence(charSequence), pendingIntent));
            return this;
        }

        public Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (style != null) {
                    style.setBuilder(this);
                }
            }
            return this;
        }

        private void setFlag(int i, boolean z) {
            if (z) {
                this.mFlags = i | this.mFlags;
            } else {
                this.mFlags = (~i) & this.mFlags;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:32:0x0096  */
        /* JADX WARN: Removed duplicated region for block: B:36:0x00a8  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x00c9  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x0125  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x012d  */
        /* JADX WARN: Removed duplicated region for block: B:61:0x012f  */
        /* JADX WARN: Removed duplicated region for block: B:64:0x0138  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private android.widget.RemoteViews applyStandardTemplate(int r11, boolean r12) {
            /*
                Method dump skipped, instruction units count: 317
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Builder.applyStandardTemplate(int, boolean):android.widget.RemoteViews");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViews applyStandardTemplateWithActions(int i) {
            RemoteViews remoteViewsApplyStandardTemplate = applyStandardTemplate(i, false);
            int size = this.mActions.size();
            if (size > 0) {
                remoteViewsApplyStandardTemplate.setViewVisibility(16909017, 0);
                remoteViewsApplyStandardTemplate.setViewVisibility(16909026, 0);
                if (size > 3) {
                    size = 3;
                }
                remoteViewsApplyStandardTemplate.removeAllViews(16909017);
                for (int i2 = 0; i2 < size; i2++) {
                    remoteViewsApplyStandardTemplate.addView(16909017, generateActionButton(this.mActions.get(i2)));
                }
            }
            return remoteViewsApplyStandardTemplate;
        }

        private RemoteViews makeContentView() {
            RemoteViews remoteViews = this.mContentView;
            return remoteViews != null ? remoteViews : applyStandardTemplate(17367140, true);
        }

        private RemoteViews makeTickerView() {
            RemoteViews remoteViews = this.mTickerView;
            if (remoteViews != null) {
                return remoteViews;
            }
            if (this.mContentView == null) {
                return applyStandardTemplate(this.mLargeIcon == null ? 17367204 : 17367205, true);
            }
            return null;
        }

        private RemoteViews makeBigContentView() {
            if (this.mActions.size() == 0) {
                return null;
            }
            return applyStandardTemplateWithActions(17367141);
        }

        private RemoteViews generateActionButton(Action action) {
            boolean z = action.actionIntent == null;
            RemoteViews remoteViews = new RemoteViews(this.mContext.getPackageName(), z ? 17367138 : 17367136);
            remoteViews.setTextViewCompoundDrawables(16909016, action.icon, 0, 0, 0);
            remoteViews.setTextViewText(16909016, action.title);
            if (!z) {
                remoteViews.setOnClickPendingIntent(16909016, action.actionIntent);
            }
            remoteViews.setContentDescription(16909016, action.title);
            return remoteViews;
        }

        public Notification buildUnstyled() {
            Notification notification = new Notification();
            notification.when = this.mWhen;
            notification.icon = this.mSmallIcon;
            notification.iconLevel = this.mSmallIconLevel;
            notification.number = this.mNumber;
            notification.contentView = makeContentView();
            notification.contentIntent = this.mContentIntent;
            notification.deleteIntent = this.mDeleteIntent;
            notification.fullScreenIntent = this.mFullScreenIntent;
            notification.tickerText = this.mTickerText;
            notification.tickerView = makeTickerView();
            notification.largeIcon = this.mLargeIcon;
            notification.sound = this.mSound;
            notification.audioStreamType = this.mAudioStreamType;
            notification.vibrate = this.mVibrate;
            notification.ledARGB = this.mLedArgb;
            notification.ledOnMS = this.mLedOnMs;
            notification.ledOffMS = this.mLedOffMs;
            notification.defaults = this.mDefaults;
            notification.flags = this.mFlags;
            notification.bigContentView = makeBigContentView();
            if (this.mLedOnMs != 0 || this.mLedOffMs != 0) {
                notification.flags |= 1;
            }
            if ((this.mDefaults & 4) != 0) {
                notification.flags |= 1;
            }
            if (this.mKindList.size() > 0) {
                notification.kind = new String[this.mKindList.size()];
                this.mKindList.toArray(notification.kind);
            } else {
                notification.kind = null;
            }
            notification.priority = this.mPriority;
            if (this.mActions.size() > 0) {
                notification.actions = new Action[this.mActions.size()];
                this.mActions.toArray(notification.actions);
            }
            return notification;
        }

        public void addExtras(Bundle bundle) {
            bundle.putCharSequence("android.title", this.mContentTitle);
            bundle.putCharSequence("android.text", this.mContentText);
            bundle.putCharSequence("android.subText", this.mSubText);
            bundle.putCharSequence("android.infoText", this.mContentInfo);
            bundle.putInt("android.icon", this.mSmallIcon);
            bundle.putInt("android.progress", this.mProgress);
            bundle.putInt("android.progressMax", this.mProgressMax);
            bundle.putBoolean("android.progressIndeterminate", this.mProgressIndeterminate);
            bundle.putBoolean("android.showChronometer", this.mUseChronometer);
            bundle.putBoolean("android.showWhen", this.mShowWhen);
            Bitmap bitmap = this.mLargeIcon;
            if (bitmap != null) {
                bundle.putParcelable("android.largeIcon", bitmap);
            }
        }

        @Deprecated
        public Notification getNotification() {
            return build();
        }

        public Notification build() {
            Notification notificationBuildUnstyled = buildUnstyled();
            Style style = this.mStyle;
            if (style != null) {
                notificationBuildUnstyled = style.buildStyled(notificationBuildUnstyled);
            }
            notificationBuildUnstyled.extras = this.mExtras != null ? new Bundle(this.mExtras) : new Bundle();
            addExtras(notificationBuildUnstyled.extras);
            Style style2 = this.mStyle;
            if (style2 != null) {
                style2.addExtras(notificationBuildUnstyled.extras);
            }
            return notificationBuildUnstyled;
        }

        public Notification buildInto(Notification notification) {
            build().cloneInto(notification, true);
            return notification;
        }
    }

    public static abstract class Style {
        private CharSequence mBigContentTitle;
        protected Builder mBuilder;
        private CharSequence mSummaryText = null;
        private boolean mSummaryTextSet = false;

        public abstract Notification buildStyled(Notification notification);

        protected void internalSetBigContentTitle(CharSequence charSequence) {
            this.mBigContentTitle = charSequence;
        }

        protected void internalSetSummaryText(CharSequence charSequence) {
            this.mSummaryText = charSequence;
            this.mSummaryTextSet = true;
        }

        public void setBuilder(Builder builder) {
            if (this.mBuilder != builder) {
                this.mBuilder = builder;
                if (builder != null) {
                    builder.setStyle(this);
                }
            }
        }

        protected void checkBuilder() {
            if (this.mBuilder == null) {
                throw new IllegalArgumentException("Style requires a valid Builder object");
            }
        }

        protected RemoteViews getStandardView(int i) {
            checkBuilder();
            CharSequence charSequence = this.mBigContentTitle;
            if (charSequence != null) {
                this.mBuilder.setContentTitle(charSequence);
            }
            RemoteViews remoteViewsApplyStandardTemplateWithActions = this.mBuilder.applyStandardTemplateWithActions(i);
            CharSequence charSequence2 = this.mBigContentTitle;
            if (charSequence2 != null && charSequence2.equals("")) {
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(16909021, 8);
            } else {
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(16909021, 0);
            }
            CharSequence charSequence3 = this.mSummaryTextSet ? this.mSummaryText : this.mBuilder.mSubText;
            if (charSequence3 != null) {
                remoteViewsApplyStandardTemplateWithActions.setTextViewText(16908358, charSequence3);
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(16909028, 0);
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(16909023, 0);
            } else {
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(16909028, 8);
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(16909023, 8);
            }
            return remoteViewsApplyStandardTemplateWithActions;
        }

        public void addExtras(Bundle bundle) {
            if (this.mSummaryTextSet) {
                bundle.putCharSequence("android.summaryText", this.mSummaryText);
            }
            CharSequence charSequence = this.mBigContentTitle;
            if (charSequence != null) {
                bundle.putCharSequence("android.title.big", charSequence);
            }
        }

        public Notification build() {
            checkBuilder();
            return this.mBuilder.build();
        }
    }

    public static class BigPictureStyle extends Style {
        private Bitmap mBigLargeIcon;
        private boolean mBigLargeIconSet = false;
        private Bitmap mPicture;

        public BigPictureStyle() {
        }

        public BigPictureStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigPictureStyle setBigContentTitle(CharSequence charSequence) {
            internalSetBigContentTitle(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigPictureStyle setSummaryText(CharSequence charSequence) {
            internalSetSummaryText(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigPictureStyle bigPicture(Bitmap bitmap) {
            this.mPicture = bitmap;
            return this;
        }

        public BigPictureStyle bigLargeIcon(Bitmap bitmap) {
            this.mBigLargeIconSet = true;
            this.mBigLargeIcon = bitmap;
            return this;
        }

        private RemoteViews makeBigContentView() {
            RemoteViews standardView = getStandardView(17367142);
            standardView.setImageViewBitmap(16909027, this.mPicture);
            return standardView;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            if (this.mBigLargeIconSet) {
                bundle.putParcelable("android.largeIcon.big", this.mBigLargeIcon);
            }
            bundle.putParcelable("android.picture", this.mPicture);
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification notification) {
            if (this.mBigLargeIconSet) {
                this.mBuilder.mLargeIcon = this.mBigLargeIcon;
            }
            notification.bigContentView = makeBigContentView();
            return notification;
        }
    }

    public static class BigTextStyle extends Style {
        private CharSequence mBigText;

        public BigTextStyle() {
        }

        public BigTextStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigTextStyle setBigContentTitle(CharSequence charSequence) {
            internalSetBigContentTitle(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigTextStyle setSummaryText(CharSequence charSequence) {
            internalSetSummaryText(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigTextStyle bigText(CharSequence charSequence) {
            this.mBigText = Notification.safeCharSequence(charSequence);
            return this;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            bundle.putCharSequence("android.text", this.mBigText);
        }

        private RemoteViews makeBigContentView() {
            boolean z = (this.mBuilder.mContentText == null || this.mBuilder.mSubText == null) ? false : true;
            this.mBuilder.mContentText = null;
            RemoteViews standardView = getStandardView(17367143);
            if (z) {
                standardView.setViewPadding(16909021, 0, 0, 0, 0);
            }
            standardView.setTextViewText(16909025, this.mBigText);
            standardView.setViewVisibility(16909025, 0);
            standardView.setViewVisibility(R.id.text2, 8);
            return standardView;
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification notification) {
            notification.bigContentView = makeBigContentView();
            notification.extras.putCharSequence("android.text", this.mBigText);
            return notification;
        }
    }

    public static class InboxStyle extends Style {
        private ArrayList<CharSequence> mTexts = new ArrayList<>(5);

        public InboxStyle() {
        }

        public InboxStyle(Builder builder) {
            setBuilder(builder);
        }

        public InboxStyle setBigContentTitle(CharSequence charSequence) {
            internalSetBigContentTitle(Notification.safeCharSequence(charSequence));
            return this;
        }

        public InboxStyle setSummaryText(CharSequence charSequence) {
            internalSetSummaryText(Notification.safeCharSequence(charSequence));
            return this;
        }

        public InboxStyle addLine(CharSequence charSequence) {
            this.mTexts.add(Notification.safeCharSequence(charSequence));
            return this;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            bundle.putCharSequenceArray("android.textLines", (CharSequence[]) this.mTexts.toArray(new CharSequence[this.mTexts.size()]));
        }

        private RemoteViews makeBigContentView() {
            this.mBuilder.mContentText = null;
            RemoteViews standardView = getStandardView(17367144);
            standardView.setViewVisibility(R.id.text2, 8);
            int[] iArr = {16909029, 16909030, 16909031, 16909032, 16909033, 16909034, 16909035};
            for (int i = 0; i < 7; i++) {
                standardView.setViewVisibility(iArr[i], 8);
            }
            for (int i2 = 0; i2 < this.mTexts.size() && i2 < 7; i2++) {
                CharSequence charSequence = this.mTexts.get(i2);
                if (charSequence != null && !charSequence.equals("")) {
                    standardView.setViewVisibility(iArr[i2], 0);
                    standardView.setTextViewText(iArr[i2], charSequence);
                }
            }
            standardView.setViewVisibility(16909037, this.mTexts.size() > 0 ? 0 : 8);
            standardView.setViewVisibility(16909036, this.mTexts.size() > 7 ? 0 : 8);
            return standardView;
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification notification) {
            notification.bigContentView = makeBigContentView();
            return notification;
        }
    }
}

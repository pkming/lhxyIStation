package android.app;

import android.app.BackStackRecord;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: compiled from: BackStackRecord.java */
/* JADX INFO: loaded from: classes.dex */
final class BackStackState implements Parcelable {
    public static final Parcelable.Creator<BackStackState> CREATOR = new Parcelable.Creator<BackStackState>() { // from class: android.app.BackStackState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BackStackState createFromParcel(Parcel parcel) {
            return new BackStackState(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BackStackState[] newArray(int i) {
            return new BackStackState[i];
        }
    };
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int mIndex;
    final String mName;
    final int[] mOps;
    final int mTransition;
    final int mTransitionStyle;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BackStackState(FragmentManagerImpl fragmentManagerImpl, BackStackRecord backStackRecord) {
        int size = 0;
        for (BackStackRecord.Op op = backStackRecord.mHead; op != null; op = op.next) {
            if (op.removed != null) {
                size += op.removed.size();
            }
        }
        this.mOps = new int[(backStackRecord.mNumOp * 7) + size];
        if (!backStackRecord.mAddToBackStack) {
            throw new IllegalStateException("Not on back stack");
        }
        int i = 0;
        for (BackStackRecord.Op op2 = backStackRecord.mHead; op2 != null; op2 = op2.next) {
            int i2 = i + 1;
            this.mOps[i] = op2.cmd;
            int i3 = i2 + 1;
            this.mOps[i2] = op2.fragment != null ? op2.fragment.mIndex : -1;
            int i4 = i3 + 1;
            this.mOps[i3] = op2.enterAnim;
            int i5 = i4 + 1;
            this.mOps[i4] = op2.exitAnim;
            int i6 = i5 + 1;
            this.mOps[i5] = op2.popEnterAnim;
            int i7 = i6 + 1;
            this.mOps[i6] = op2.popExitAnim;
            if (op2.removed != null) {
                int size2 = op2.removed.size();
                int i8 = i7 + 1;
                this.mOps[i7] = size2;
                int i9 = 0;
                while (i9 < size2) {
                    this.mOps[i8] = op2.removed.get(i9).mIndex;
                    i9++;
                    i8++;
                }
                i = i8;
            } else {
                this.mOps[i7] = 0;
                i = i7 + 1;
            }
        }
        this.mTransition = backStackRecord.mTransition;
        this.mTransitionStyle = backStackRecord.mTransitionStyle;
        this.mName = backStackRecord.mName;
        this.mIndex = backStackRecord.mIndex;
        this.mBreadCrumbTitleRes = backStackRecord.mBreadCrumbTitleRes;
        this.mBreadCrumbTitleText = backStackRecord.mBreadCrumbTitleText;
        this.mBreadCrumbShortTitleRes = backStackRecord.mBreadCrumbShortTitleRes;
        this.mBreadCrumbShortTitleText = backStackRecord.mBreadCrumbShortTitleText;
    }

    public BackStackState(Parcel parcel) {
        this.mOps = parcel.createIntArray();
        this.mTransition = parcel.readInt();
        this.mTransitionStyle = parcel.readInt();
        this.mName = parcel.readString();
        this.mIndex = parcel.readInt();
        this.mBreadCrumbTitleRes = parcel.readInt();
        this.mBreadCrumbTitleText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mBreadCrumbShortTitleRes = parcel.readInt();
        this.mBreadCrumbShortTitleText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
    }

    public BackStackRecord instantiate(FragmentManagerImpl fragmentManagerImpl) {
        BackStackRecord backStackRecord = new BackStackRecord(fragmentManagerImpl);
        int i = 0;
        int i2 = 0;
        while (i < this.mOps.length) {
            BackStackRecord.Op op = new BackStackRecord.Op();
            int i3 = i + 1;
            op.cmd = this.mOps[i];
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Instantiate " + backStackRecord + " op #" + i2 + " base fragment #" + this.mOps[i3]);
            }
            int i4 = i3 + 1;
            int i5 = this.mOps[i3];
            if (i5 >= 0) {
                op.fragment = fragmentManagerImpl.mActive.get(i5);
            } else {
                op.fragment = null;
            }
            int i6 = i4 + 1;
            op.enterAnim = this.mOps[i4];
            int i7 = i6 + 1;
            op.exitAnim = this.mOps[i6];
            int i8 = i7 + 1;
            op.popEnterAnim = this.mOps[i7];
            int i9 = i8 + 1;
            op.popExitAnim = this.mOps[i8];
            int i10 = i9 + 1;
            int i11 = this.mOps[i9];
            if (i11 > 0) {
                op.removed = new ArrayList<>(i11);
                int i12 = 0;
                while (i12 < i11) {
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "Instantiate " + backStackRecord + " set remove fragment #" + this.mOps[i10]);
                    }
                    op.removed.add(fragmentManagerImpl.mActive.get(this.mOps[i10]));
                    i12++;
                    i10++;
                }
            }
            i = i10;
            backStackRecord.addOp(op);
            i2++;
        }
        backStackRecord.mTransition = this.mTransition;
        backStackRecord.mTransitionStyle = this.mTransitionStyle;
        backStackRecord.mName = this.mName;
        backStackRecord.mIndex = this.mIndex;
        backStackRecord.mAddToBackStack = true;
        backStackRecord.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
        backStackRecord.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
        backStackRecord.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
        backStackRecord.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
        backStackRecord.bumpBackStackNesting(1);
        return backStackRecord;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.mOps);
        parcel.writeInt(this.mTransition);
        parcel.writeInt(this.mTransitionStyle);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mIndex);
        parcel.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, parcel, 0);
        parcel.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, parcel, 0);
    }
}

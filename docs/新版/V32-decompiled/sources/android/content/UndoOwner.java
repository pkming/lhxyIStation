package android.content;

/* JADX INFO: loaded from: classes.dex */
public class UndoOwner {
    Object mData;
    UndoManager mManager;
    int mOpCount;
    int mSavedIdx;
    int mStateSeq;
    final String mTag;

    UndoOwner(String str) {
        this.mTag = str;
    }

    public String getTag() {
        return this.mTag;
    }

    public Object getData() {
        return this.mData;
    }
}

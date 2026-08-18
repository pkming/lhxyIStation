package android.transition;

import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
class TransitionValuesMaps {
    ArrayMap<View, TransitionValues> viewValues = new ArrayMap<>();
    SparseArray<TransitionValues> idValues = new SparseArray<>();
    LongSparseArray<TransitionValues> itemIdValues = new LongSparseArray<>();

    TransitionValuesMaps() {
    }
}

package android.view;

import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public interface ViewManager {
    void addView(View view, ViewGroup.LayoutParams layoutParams);

    void removeView(View view);

    void updateViewLayout(View view, ViewGroup.LayoutParams layoutParams);
}

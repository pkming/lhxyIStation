package butterknife.internal;

import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public abstract class DebouncingOnClickListener implements View.OnClickListener {
    private static final Runnable ENABLE_AGAIN = new Runnable() { // from class: butterknife.internal.-$$Lambda$DebouncingOnClickListener$9VMd1QuOmdVg_AaYBDeumcPsza8
        @Override // java.lang.Runnable
        public final void run() {
            DebouncingOnClickListener.enabled = true;
        }
    };
    static boolean enabled = true;

    public abstract void doClick(View view);

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        if (enabled) {
            enabled = false;
            view.post(ENABLE_AGAIN);
            doClick(view);
        }
    }
}

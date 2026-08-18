package android.preference;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

/* JADX INFO: loaded from: classes.dex */
public class SeekBarDialogPreference extends DialogPreference {
    private static final String TAG = "SeekBarDialogPreference";
    private Drawable mMyIcon;

    public SeekBarDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(17367192);
        createActionButtons();
        this.mMyIcon = getDialogIcon();
        setDialogIcon((Drawable) null);
    }

    public void createActionButtons() {
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);
    }

    @Override // android.preference.DialogPreference
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        Drawable drawable = this.mMyIcon;
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        } else {
            imageView.setVisibility(8);
        }
    }

    protected static SeekBar getSeekBar(View view) {
        return (SeekBar) view.findViewById(16909056);
    }
}

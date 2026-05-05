package com.lhxy.istationdevice.android11.app.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lhxy.istationdevice.android11.app.R;

import java.util.List;

public final class LegacyWheelPickerDialog {
    private LegacyWheelPickerDialog() {
    }

    public static void show(
            @NonNull Context context,
            @NonNull CharSequence title,
            @NonNull List<String> options,
            int initialIndex,
            @NonNull OnItemConfirmedListener listener
    ) {
        if (options.isEmpty()) {
            return;
        }
        Dialog dialog = new Dialog(context, R.style.wheelDialogStyle);
        dialog.setContentView(R.layout.dlg_legacy_wheel_picker);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextView titleView = dialog.findViewById(R.id.tvWheelTitle);
        TextView cancelView = dialog.findViewById(R.id.tvWheelCancel);
        TextView confirmView = dialog.findViewById(R.id.tvWheelConfirm);
        NumberPicker picker = dialog.findViewById(R.id.wPicker);

        if (titleView != null) {
            titleView.setText(title);
        }
        if (picker != null) {
            String[] values = options.toArray(new String[0]);
            picker.setDisplayedValues(null);
            picker.setMinValue(0);
            picker.setMaxValue(values.length - 1);
            picker.setWrapSelectorWheel(false);
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            picker.setDisplayedValues(values);
            picker.setValue(Math.max(0, Math.min(initialIndex, values.length - 1)));
        }
        if (cancelView != null) {
            cancelView.setOnClickListener(v -> dialog.dismiss());
        }
        if (confirmView != null && picker != null) {
            confirmView.setOnClickListener(v -> {
                int selectedIndex = picker.getValue();
                listener.onItemConfirmed(selectedIndex, options.get(selectedIndex));
                dialog.dismiss();
            });
        }

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.wheelDialogAnimation);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
    }

    public interface OnItemConfirmedListener {
        void onItemConfirmed(int selectedIndex, @NonNull String selectedValue);
    }
}
package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, DatePicker.OnDateChangedListener {
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private final Calendar mCalendar;
    private final OnDateSetListener mCallBack;
    private final DatePicker mDatePicker;
    private boolean mTitleNeedsUpdate;

    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int i, int i2, int i3);
    }

    public DatePickerDialog(Context context, OnDateSetListener onDateSetListener, int i, int i2, int i3) {
        this(context, 0, onDateSetListener, i, i2, i3);
    }

    public DatePickerDialog(Context context, int i, OnDateSetListener onDateSetListener, int i2, int i3, int i4) {
        super(context, i);
        this.mTitleNeedsUpdate = true;
        this.mCallBack = onDateSetListener;
        this.mCalendar = Calendar.getInstance();
        Context context2 = getContext();
        setButton(-1, context2.getText(17040477), this);
        setIcon(0);
        View viewInflate = ((LayoutInflater) context2.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(17367097, (ViewGroup) null);
        setView(viewInflate);
        DatePicker datePicker = (DatePicker) viewInflate.findViewById(16908956);
        this.mDatePicker = datePicker;
        datePicker.init(i2, i3, i4, this);
        updateTitle(i2, i3, i4);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        tryNotifyDateSet();
    }

    @Override // android.widget.DatePicker.OnDateChangedListener
    public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
        this.mDatePicker.init(i, i2, i3, this);
        updateTitle(i, i2, i3);
    }

    public DatePicker getDatePicker() {
        return this.mDatePicker;
    }

    public void updateDate(int i, int i2, int i3) {
        this.mDatePicker.updateDate(i, i2, i3);
    }

    private void tryNotifyDateSet() {
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            OnDateSetListener onDateSetListener = this.mCallBack;
            DatePicker datePicker = this.mDatePicker;
            onDateSetListener.onDateSet(datePicker, datePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }

    @Override // android.app.Dialog
    protected void onStop() {
        tryNotifyDateSet();
        super.onStop();
    }

    private void updateTitle(int i, int i2, int i3) {
        if (!this.mDatePicker.getCalendarViewShown()) {
            this.mCalendar.set(1, i);
            this.mCalendar.set(2, i2);
            this.mCalendar.set(5, i3);
            setTitle(DateUtils.formatDateTime(this.mContext, this.mCalendar.getTimeInMillis(), 98326));
            this.mTitleNeedsUpdate = true;
            return;
        }
        if (this.mTitleNeedsUpdate) {
            this.mTitleNeedsUpdate = false;
            setTitle(17040475);
        }
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle bundleOnSaveInstanceState = super.onSaveInstanceState();
        bundleOnSaveInstanceState.putInt("year", this.mDatePicker.getYear());
        bundleOnSaveInstanceState.putInt(MONTH, this.mDatePicker.getMonth());
        bundleOnSaveInstanceState.putInt("day", this.mDatePicker.getDayOfMonth());
        return bundleOnSaveInstanceState;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mDatePicker.init(bundle.getInt("year"), bundle.getInt(MONTH), bundle.getInt("day"), this);
    }
}

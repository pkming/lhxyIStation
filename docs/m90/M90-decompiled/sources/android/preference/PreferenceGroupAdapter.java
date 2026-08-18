package android.preference;

import android.os.Handler;
import android.preference.Preference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class PreferenceGroupAdapter extends BaseAdapter implements Preference.OnPreferenceChangeInternalListener {
    private static final String TAG = "PreferenceGroupAdapter";
    private PreferenceGroup mPreferenceGroup;
    private ArrayList<PreferenceLayout> mPreferenceLayouts;
    private List<Preference> mPreferenceList;
    private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout();
    private boolean mHasReturnedViewTypeCount = false;
    private volatile boolean mIsSyncing = false;
    private Handler mHandler = new Handler();
    private Runnable mSyncRunnable = new Runnable() { // from class: android.preference.PreferenceGroupAdapter.1
        @Override // java.lang.Runnable
        public void run() {
            PreferenceGroupAdapter.this.syncMyPreferences();
        }
    };

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    private static class PreferenceLayout implements Comparable<PreferenceLayout> {
        private String name;
        private int resId;
        private int widgetResId;

        private PreferenceLayout() {
        }

        @Override // java.lang.Comparable
        public int compareTo(PreferenceLayout preferenceLayout) {
            int iCompareTo = this.name.compareTo(preferenceLayout.name);
            if (iCompareTo != 0) {
                return iCompareTo;
            }
            int i = this.resId;
            int i2 = preferenceLayout.resId;
            if (i != i2) {
                return i - i2;
            }
            int i3 = this.widgetResId;
            int i4 = preferenceLayout.widgetResId;
            if (i3 == i4) {
                return 0;
            }
            return i3 - i4;
        }
    }

    public PreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
        this.mPreferenceGroup = preferenceGroup;
        preferenceGroup.setOnPreferenceChangeInternalListener(this);
        this.mPreferenceList = new ArrayList();
        this.mPreferenceLayouts = new ArrayList<>();
        syncMyPreferences();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncMyPreferences() {
        synchronized (this) {
            if (this.mIsSyncing) {
                return;
            }
            this.mIsSyncing = true;
            ArrayList arrayList = new ArrayList(this.mPreferenceList.size());
            flattenPreferenceGroup(arrayList, this.mPreferenceGroup);
            this.mPreferenceList = arrayList;
            notifyDataSetChanged();
            synchronized (this) {
                this.mIsSyncing = false;
                notifyAll();
            }
        }
    }

    private void flattenPreferenceGroup(List<Preference> list, PreferenceGroup preferenceGroup) {
        preferenceGroup.sortPreferences();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            list.add(preference);
            if (!this.mHasReturnedViewTypeCount && preference.canRecycleLayout()) {
                addPreferenceClassName(preference);
            }
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup2 = (PreferenceGroup) preference;
                if (preferenceGroup2.isOnSameScreenAsChildren()) {
                    flattenPreferenceGroup(list, preferenceGroup2);
                }
            }
            preference.setOnPreferenceChangeInternalListener(this);
        }
    }

    private PreferenceLayout createPreferenceLayout(Preference preference, PreferenceLayout preferenceLayout) {
        if (preferenceLayout == null) {
            preferenceLayout = new PreferenceLayout();
        }
        preferenceLayout.name = preference.getClass().getName();
        preferenceLayout.resId = preference.getLayoutResource();
        preferenceLayout.widgetResId = preference.getWidgetLayoutResource();
        return preferenceLayout;
    }

    private void addPreferenceClassName(Preference preference) {
        PreferenceLayout preferenceLayoutCreatePreferenceLayout = createPreferenceLayout(preference, null);
        if (Collections.binarySearch(this.mPreferenceLayouts, preferenceLayoutCreatePreferenceLayout) < 0) {
            this.mPreferenceLayouts.add((r0 * (-1)) - 1, preferenceLayoutCreatePreferenceLayout);
        }
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mPreferenceList.size();
    }

    @Override // android.widget.Adapter
    public Preference getItem(int i) {
        if (i < 0 || i >= getCount()) {
            return null;
        }
        return this.mPreferenceList.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        if (i < 0 || i >= getCount()) {
            return Long.MIN_VALUE;
        }
        return getItem(i).getId();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        Preference item = getItem(i);
        PreferenceLayout preferenceLayoutCreatePreferenceLayout = createPreferenceLayout(item, this.mTempPreferenceLayout);
        this.mTempPreferenceLayout = preferenceLayoutCreatePreferenceLayout;
        if (Collections.binarySearch(this.mPreferenceLayouts, preferenceLayoutCreatePreferenceLayout) < 0) {
            view = null;
        }
        return item.getView(view, viewGroup);
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int i) {
        if (i < 0 || i >= getCount()) {
            return true;
        }
        return getItem(i).isSelectable();
    }

    @Override // android.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceChange(Preference preference) {
        notifyDataSetChanged();
    }

    @Override // android.preference.Preference.OnPreferenceChangeInternalListener
    public void onPreferenceHierarchyChange(Preference preference) {
        this.mHandler.removeCallbacks(this.mSyncRunnable);
        this.mHandler.post(this.mSyncRunnable);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        if (!this.mHasReturnedViewTypeCount) {
            this.mHasReturnedViewTypeCount = true;
        }
        Preference item = getItem(i);
        if (!item.canRecycleLayout()) {
            return -1;
        }
        PreferenceLayout preferenceLayoutCreatePreferenceLayout = createPreferenceLayout(item, this.mTempPreferenceLayout);
        this.mTempPreferenceLayout = preferenceLayoutCreatePreferenceLayout;
        int iBinarySearch = Collections.binarySearch(this.mPreferenceLayouts, preferenceLayoutCreatePreferenceLayout);
        if (iBinarySearch < 0) {
            return -1;
        }
        return iBinarySearch;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        if (!this.mHasReturnedViewTypeCount) {
            this.mHasReturnedViewTypeCount = true;
        }
        return Math.max(1, this.mPreferenceLayouts.size());
    }
}

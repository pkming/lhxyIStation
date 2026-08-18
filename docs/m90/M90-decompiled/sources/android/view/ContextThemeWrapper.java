package android.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;

/* JADX INFO: loaded from: classes.dex */
public class ContextThemeWrapper extends ContextWrapper {
    private Context mBase;
    private LayoutInflater mInflater;
    private Configuration mOverrideConfiguration;
    private Resources mResources;
    private Resources.Theme mTheme;
    private int mThemeResource;

    public ContextThemeWrapper() {
        super(null);
    }

    public ContextThemeWrapper(Context context, int i) {
        super(context);
        this.mBase = context;
        this.mThemeResource = i;
    }

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        this.mBase = context;
    }

    public void applyOverrideConfiguration(Configuration configuration) {
        if (this.mResources != null) {
            throw new IllegalStateException("getResources() has already been called");
        }
        if (this.mOverrideConfiguration != null) {
            throw new IllegalStateException("Override configuration has already been set");
        }
        this.mOverrideConfiguration = new Configuration(configuration);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources getResources() {
        Resources resources = this.mResources;
        if (resources != null) {
            return resources;
        }
        Configuration configuration = this.mOverrideConfiguration;
        if (configuration == null) {
            Resources resources2 = super.getResources();
            this.mResources = resources2;
            return resources2;
        }
        Resources resources3 = createConfigurationContext(configuration).getResources();
        this.mResources = resources3;
        return resources3;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        this.mThemeResource = i;
        initializeTheme();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public int getThemeResId() {
        return this.mThemeResource;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Resources.Theme getTheme() {
        Resources.Theme theme = this.mTheme;
        if (theme != null) {
            return theme;
        }
        this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getApplicationInfo().targetSdkVersion);
        initializeTheme();
        return this.mTheme;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String str) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(str)) {
            if (this.mInflater == null) {
                this.mInflater = LayoutInflater.from(this.mBase).cloneInContext(this);
            }
            return this.mInflater;
        }
        return this.mBase.getSystemService(str);
    }

    protected void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(i, true);
    }

    private void initializeTheme() {
        boolean z = this.mTheme == null;
        if (z) {
            this.mTheme = getResources().newTheme();
            Resources.Theme theme = this.mBase.getTheme();
            if (theme != null) {
                this.mTheme.setTo(theme);
            }
        }
        onApplyThemeResource(this.mTheme, this.mThemeResource, z);
    }
}

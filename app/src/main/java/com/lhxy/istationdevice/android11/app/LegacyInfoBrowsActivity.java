package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 旧版信息浏览页骨架。
 */
public final class LegacyInfoBrowsActivity extends LegacyBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.act_infobrows;
    }

    @Override
    protected int getTitleResId() {
        return R.string.infobrows_title;
    }

    @Override
    protected void onPageReady(Bundle savedInstanceState) {
        LinearLayout emptyView = findViewById(R.id.lyNotInfo);
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
        Button affirmButton = findViewById(R.id.butAffirm);
        if (affirmButton != null) {
            affirmButton.setOnClickListener(v -> showPendingToast(R.string.infobrows_title));
        }
    }
}

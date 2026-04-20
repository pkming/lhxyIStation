package com.lhxy.istationdevice.android11.app;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 旧版线路选择页骨架。
 */
public final class LegacyLineChoiceActivity extends LegacyBaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.act_linelist;
    }

    @Override
    protected int getTitleResId() {
        return R.string.line_choice_title;
    }

    @Override
    protected void onPageReady(android.os.Bundle savedInstanceState) {
        LinearLayout emptyView = findViewById(R.id.lyNotLine);
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
        Button affirmButton = findViewById(R.id.butAffirm);
        if (affirmButton != null) {
            affirmButton.setOnClickListener(v -> showPendingToast(R.string.line_choice_title));
        }
    }
}

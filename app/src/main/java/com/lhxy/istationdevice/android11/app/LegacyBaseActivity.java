package com.lhxy.istationdevice.android11.app;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

/**
 * 旧版页面骨架公共基类。
 * <p>
 * 统一处理旧版 Toolbar、返回键和右侧占位内容，
 * 这样后面逐页接业务时不会重复写一遍骨架代码。
 */
abstract class LegacyBaseActivity extends AppCompatActivity {
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initToolbar(getTitleResId());
        onPageReady(savedInstanceState);
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @StringRes
    protected abstract int getTitleResId();

    protected void onPageReady(@Nullable Bundle savedInstanceState) {
        // optional
    }

    protected final void initToolbar(@StringRes int titleResId) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = findViewById(R.id.toolbar_title);
        if (title != null) {
            title.setText(titleResId);
        }
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.icon_back_normal);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    protected final void showPendingToast(@StringRes int pageTitleRes) {
        Toast.makeText(
                this,
                getString(pageTitleRes) + getString(R.string.legacy_page_pending_suffix),
                Toast.LENGTH_SHORT
        ).show();
    }

    protected final void showPlaceholder(int containerId, @StringRes int titleResId, @StringRes int messageResId) {
        FrameLayout container = findViewById(containerId);
        if (container == null) {
            return;
        }
        container.removeAllViews();
        TextView textView = new TextView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(this, R.color.c_ffffff));
        textView.setTextSize(22f);
        textView.setText(getString(titleResId) + "\n" + getString(messageResId));
        container.addView(textView);
    }
}

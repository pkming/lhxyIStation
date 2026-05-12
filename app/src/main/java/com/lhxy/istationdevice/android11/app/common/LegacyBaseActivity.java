package com.lhxy.istationdevice.android11.app.common;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import com.lhxy.istationdevice.android11.app.R;

/**
 * 旧版页面骨架公共基类。
 * <p>
 * 统一处理旧版 Toolbar、返回键和右侧占位内容，
 * 这样后面逐页接业务时不会重复写一遍骨架代码。
 * <p>
 * 查找关键字：旧壳基类、Toolbar 初始化、统一返回、占位页。
 */
public abstract class LegacyBaseActivity extends AppCompatActivity {
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initToolbar(getTitleResId());
        onPageReady(savedInstanceState);
    }

    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 返回当前旧壳页面对应的标题资源。
     */
    @StringRes
    protected abstract int getTitleResId();

    /**
     * 子类页面初始化入口。
     * <p>
     * 旧壳 Activity 的控件绑定、数据刷新、Fragment 装载都从这里继续展开。
     */
    protected void onPageReady(@Nullable Bundle savedInstanceState) {
        // optional
    }

    /**
     * 统一初始化顶部标题和返回按钮。
     */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.index) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected final void showPendingToast(@StringRes int pageTitleRes) {
        Toast.makeText(
                this,
                getString(pageTitleRes) + getString(R.string.legacy_page_pending_suffix),
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * 在旧壳容器里放一个纯文本占位块。
     * <p>
     * 适合暂未接入正式业务、但页面骨架已经恢复的区域。
     */
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

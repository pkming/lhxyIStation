package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * 模块基类
 * <p>
 * 统一保存上下文和当前配置，子类只关心自己的编排动作和状态输出。
 */
abstract class AbstractTerminalBusinessModule implements TerminalBusinessModule {
    private Context appContext;
    private ShellConfig shellConfig;
    private long lastActionTimeMillis;
    private String lastActionSummary = "还没有执行业务动作";
    private boolean lastActionSuccess;
    private int actionCount;

    @Override
    public final void updateContext(Context context, ShellConfig shellConfig) {
        this.appContext = context == null ? null : context.getApplicationContext();
        this.shellConfig = shellConfig;
        onContextUpdated();
    }

    /**
     * 子类按需在配置刷新后重做本地缓存。
     */
    protected void onContextUpdated() {
    }

    protected final Context getContext() {
        return appContext;
    }

    protected final ShellConfig requireShellConfig() {
        if (shellConfig == null) {
            throw new IllegalStateException("当前模块还没有拿到配置");
        }
        return shellConfig;
    }

    protected final String yesNo(boolean value) {
        return value ? "是" : "否";
    }

    protected final String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        return unsupportedAction(actionKey);
    }

    protected final ModuleRunResult success(String summary, String detail) {
        rememberAction(true, summary, detail);
        return ModuleRunResult.success(getKey(), getTitle(), summary, detail);
    }

    protected final ModuleRunResult failure(String summary, Exception exception) {
        String detail = exception == null || exception.getMessage() == null || exception.getMessage().trim().isEmpty()
                ? exception == null ? "未知错误" : exception.getClass().getSimpleName()
                : exception.getMessage().trim();
        rememberAction(false, summary, detail);
        return ModuleRunResult.failure(getKey(), getTitle(), summary, detail);
    }

    protected final ModuleRunResult failureText(String summary, String detail) {
        rememberAction(false, summary, detail);
        return ModuleRunResult.failure(getKey(), getTitle(), summary, detail);
    }

    protected final ModuleRunResult unsupportedAction(String actionKey) {
        return failureText("当前动作未接入", "actionKey=" + emptyAsDash(actionKey));
    }

    protected final String describeActionMemory() {
        return "最近动作=" + emptyAsDash(lastActionSummary)
                + "\n- success=" + yesNo(lastActionSuccess)
                + "\n- actionCount=" + actionCount
                + "\n- lastActionTime=" + (lastActionTimeMillis <= 0 ? "-" : String.valueOf(lastActionTimeMillis));
    }

    private void rememberAction(boolean success, String summary, String detail) {
        lastActionSuccess = success;
        lastActionTimeMillis = System.currentTimeMillis();
        actionCount++;
        StringBuilder builder = new StringBuilder();
        builder.append(emptyAsDash(summary));
        if (detail != null && !detail.trim().isEmpty()) {
            builder.append(" / ").append(detail.trim());
        }
        lastActionSummary = builder.toString();
    }
}

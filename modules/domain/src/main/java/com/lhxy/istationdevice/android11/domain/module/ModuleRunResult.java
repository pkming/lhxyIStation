package com.lhxy.istationdevice.android11.domain.module;

/**
 * 模块样例执行结果
 * <p>
 * 首页和调试页统一拿这个结构展示模块跑通情况，
 * 后面真机联调时也沿用这套格式，不再每个页面自己拼提示文案。
 */
public final class ModuleRunResult {
    private final String moduleKey;
    private final String moduleTitle;
    private final boolean success;
    private final String summary;
    private final String detail;

    private ModuleRunResult(String moduleKey, String moduleTitle, boolean success, String summary, String detail) {
        this.moduleKey = moduleKey;
        this.moduleTitle = moduleTitle;
        this.success = success;
        this.summary = summary == null ? "" : summary.trim();
        this.detail = detail == null ? "" : detail.trim();
    }

    /**
     * 创建成功结果。
     */
    public static ModuleRunResult success(String moduleKey, String moduleTitle, String summary, String detail) {
        return new ModuleRunResult(moduleKey, moduleTitle, true, summary, detail);
    }

    /**
     * 创建失败结果。
     */
    public static ModuleRunResult failure(String moduleKey, String moduleTitle, String summary, String detail) {
        return new ModuleRunResult(moduleKey, moduleTitle, false, summary, detail);
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * 输出单行摘要。
     */
    public String describeInline() {
        return moduleTitle + " -> " + (success ? "OK" : "FAIL") + " / " + summary;
    }

    /**
     * 输出多行块摘要。
     */
    public String describeBlock() {
        StringBuilder builder = new StringBuilder();
        builder.append(moduleTitle)
                .append(" [")
                .append(moduleKey)
                .append("] ")
                .append(success ? "[OK]" : "[FAIL]");
        if (!summary.isEmpty()) {
            builder.append("\n- ").append(summary);
        }
        if (!detail.isEmpty()) {
            builder.append("\n- ").append(detail);
        }
        return builder.toString();
    }
}

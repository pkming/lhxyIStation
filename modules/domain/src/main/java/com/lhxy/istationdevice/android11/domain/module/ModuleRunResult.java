package com.lhxy.istationdevice.android11.domain.module;

import java.util.ArrayList;
import java.util.List;

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
    private final List<DiagnosticItem> diagnostics;

    private ModuleRunResult(String moduleKey, String moduleTitle, boolean success, String summary, String detail, List<DiagnosticItem> diagnostics) {
        this.moduleKey = moduleKey;
        this.moduleTitle = moduleTitle;
        this.success = success;
        this.summary = summary == null ? "" : summary.trim();
        this.detail = detail == null ? "" : detail.trim();
        this.diagnostics = diagnostics == null ? new ArrayList<>() : new ArrayList<>(diagnostics);
    }

    /**
     * 创建成功结果。
     */
    public static ModuleRunResult success(String moduleKey, String moduleTitle, String summary, String detail) {
        return success(moduleKey, moduleTitle, summary, detail, null);
    }

    public static ModuleRunResult success(String moduleKey, String moduleTitle, String summary, String detail, List<DiagnosticItem> diagnostics) {
        return new ModuleRunResult(moduleKey, moduleTitle, true, summary, detail, diagnostics);
    }

    /**
     * 创建失败结果。
     */
    public static ModuleRunResult failure(String moduleKey, String moduleTitle, String summary, String detail) {
        return failure(moduleKey, moduleTitle, summary, detail, null);
    }

    public static ModuleRunResult failure(String moduleKey, String moduleTitle, String summary, String detail, List<DiagnosticItem> diagnostics) {
        return new ModuleRunResult(moduleKey, moduleTitle, false, summary, detail, diagnostics);
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

    public String getSummary() {
        return summary;
    }

    public String getDetail() {
        return detail;
    }

    public boolean hasDiagnostics() {
        return !diagnostics.isEmpty();
    }

    public List<DiagnosticItem> getDiagnostics() {
        return new ArrayList<>(diagnostics);
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

    public static final class DiagnosticItem {
        public static final String LEVEL_OK = "OK";
        public static final String LEVEL_WARN = "WARN";
        public static final String LEVEL_FAIL = "FAIL";

        private final String level;
        private final String target;
        private final String message;

        public DiagnosticItem(String level, String target, String message) {
            this.level = level == null || level.trim().isEmpty() ? LEVEL_WARN : level.trim();
            this.target = target == null ? "" : target.trim();
            this.message = message == null ? "" : message.trim();
        }

        public String getLevel() {
            return level;
        }

        public String getTarget() {
            return target;
        }

        public String getMessage() {
            return message;
        }
    }
}

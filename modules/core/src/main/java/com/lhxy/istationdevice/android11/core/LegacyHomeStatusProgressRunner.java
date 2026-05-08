package com.lhxy.istationdevice.android11.core;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 首页提示区进度任务执行器。
 * <p>
 * 用来复用 M90 的 operation/progress 语义，避免每个业务模块自己写 sleep 循环。
 */
public final class LegacyHomeStatusProgressRunner {
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, ScheduledFuture<?>> TASKS = new ConcurrentHashMap<>();

    private LegacyHomeStatusProgressRunner() {
    }

    public static void start(
            @NonNull Context context,
            @NonNull String taskKey,
            int operation,
            @NonNull int[] progressSteps,
            long intervalMillis,
            @Nullable Completion completion
    ) {
        cancel(taskKey);
        Context appContext = context.getApplicationContext();
        AtomicInteger index = new AtomicInteger();
        ScheduledFuture<?>[] futureHolder = new ScheduledFuture<?>[1];
        Runnable tick = () -> {
            int position = index.getAndIncrement();
            if (position >= progressSteps.length) {
                finish(taskKey, appContext, completion, futureHolder[0]);
                return;
            }
            LegacyHomeStatusRepository.setInfoOperation(appContext, operation, progressSteps[position]);
            if (position == progressSteps.length - 1) {
                finish(taskKey, appContext, completion, futureHolder[0]);
            }
        };
        futureHolder[0] = EXECUTOR.scheduleAtFixedRate(tick, 0L, Math.max(50L, intervalMillis), TimeUnit.MILLISECONDS);
        TASKS.put(taskKey, futureHolder[0]);
    }

    public static void cancel(@NonNull String taskKey) {
        ScheduledFuture<?> future = TASKS.remove(taskKey);
        if (future != null) {
            future.cancel(false);
        }
    }

    private static void finish(
            String taskKey,
            Context appContext,
            Completion completion,
            ScheduledFuture<?> future
    ) {
        TASKS.remove(taskKey);
        if (future != null) {
            future.cancel(false);
        }
        if (completion != null) {
            completion.onCompleted(appContext);
        }
    }

    public interface Completion {
        void onCompleted(Context context);
    }
}
package com.wuxiaolong.androidutils.library;

import android.app.Activity;
import android.os.Process;
import java.util.Stack;

/* JADX INFO: loaded from: classes2.dex */
public class ActivityManagerUtil {
    private static ActivityManagerUtil instance;
    private Stack<Activity> activityStack;

    public static ActivityManagerUtil getInstance() {
        if (instance == null) {
            instance = new ActivityManagerUtil();
        }
        return instance;
    }

    public void pushOneActivity(Activity activity) {
        if (this.activityStack == null) {
            this.activityStack = new Stack<>();
        }
        this.activityStack.add(activity);
    }

    public void popOneActivity(Activity activity) {
        Stack<Activity> stack = this.activityStack;
        if (stack == null || stack.size() <= 0 || activity == null) {
            return;
        }
        this.activityStack.remove(activity);
        activity.finish();
    }

    public Activity getLastActivity() {
        return this.activityStack.lastElement();
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activityStack.remove(activity);
            activity.finish();
        }
    }

    public void finishActivity(Class<?> cls) {
        for (Activity activity : this.activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        for (int i = 0; i < this.activityStack.size(); i++) {
            try {
                if (this.activityStack.get(i) != null) {
                    this.activityStack.get(i).finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        this.activityStack.clear();
    }

    public void appExit() {
        try {
            finishAllActivity();
            System.exit(0);
            Process.killProcess(Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

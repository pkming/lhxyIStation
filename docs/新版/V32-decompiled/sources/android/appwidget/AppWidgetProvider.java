package android.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetProvider extends BroadcastReceiver {
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int i, Bundle bundle) {
    }

    public void onDeleted(Context context, int[] iArr) {
    }

    public void onDisabled(Context context) {
    }

    public void onEnabled(Context context) {
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int[] intArray;
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras == null || (intArray = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)) == null || intArray.length <= 0) {
                return;
            }
            onUpdate(context, AppWidgetManager.getInstance(context), intArray);
            return;
        }
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Bundle extras2 = intent.getExtras();
            if (extras2 == null || !extras2.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                return;
            }
            onDeleted(context, new int[]{extras2.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)});
            return;
        }
        if (AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
            Bundle extras3 = intent.getExtras();
            if (extras3 != null && extras3.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID) && extras3.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context), extras3.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID), extras3.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS));
                return;
            }
            return;
        }
        if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            onEnabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            onDisabled(context);
        }
    }
}

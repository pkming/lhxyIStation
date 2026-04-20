package com.lhxy.istationdevice.android11.debugtools;

import android.content.Context;
import android.content.Intent;

public final class DebugIntentFactory {
    private DebugIntentFactory() {
    }

    public static Intent create(Context context) {
        return new Intent(context, DebugToolsActivity.class);
    }
}


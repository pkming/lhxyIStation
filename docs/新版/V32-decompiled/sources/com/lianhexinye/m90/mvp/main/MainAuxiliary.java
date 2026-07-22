package com.lianhexinye.m90.mvp.main;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.lianhexinye.m90.R;

/* JADX INFO: loaded from: classes2.dex */
public class MainAuxiliary {
    public void produceEllipsisLayout(Context context, LinearLayout linearLayout, boolean z) {
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
        linearLayout2.setGravity(17);
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.shape_ellipsis_circular);
        linearLayout2.addView(imageView);
        LinearLayout linearLayout3 = new LinearLayout(context);
        linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
        linearLayout3.setGravity(17);
        ImageView imageView2 = new ImageView(context);
        imageView2.setBackgroundResource(R.drawable.shape_ellipsis_circular);
        linearLayout3.addView(imageView2);
        if (z) {
            linearLayout.addView(linearLayout3);
        }
        LinearLayout linearLayout4 = new LinearLayout(context);
        linearLayout4.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
        linearLayout4.setGravity(17);
        ImageView imageView3 = new ImageView(context);
        imageView3.setBackgroundResource(R.drawable.shape_ellipsis_circular);
        linearLayout4.addView(imageView3);
        if (z) {
            linearLayout.addView(linearLayout4);
        }
        LinearLayout linearLayout5 = new LinearLayout(context);
        linearLayout5.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
        linearLayout5.setGravity(17);
        ImageView imageView4 = new ImageView(context);
        imageView4.setBackgroundResource(R.drawable.shape_ellipsis_circular);
        linearLayout5.addView(imageView4);
        linearLayout.addView(linearLayout5);
        LinearLayout linearLayout6 = new LinearLayout(context);
        linearLayout6.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
        linearLayout6.setGravity(17);
        ImageView imageView5 = new ImageView(context);
        imageView5.setBackgroundResource(R.drawable.shape_ellipsis_circular);
        linearLayout6.addView(imageView5);
        linearLayout.addView(linearLayout6);
        LinearLayout linearLayout7 = new LinearLayout(context);
        linearLayout7.setLayoutParams(new LinearLayout.LayoutParams(-2, -2, 1.0f));
        linearLayout7.setGravity(17);
        ImageView imageView6 = new ImageView(context);
        imageView6.setBackgroundResource(R.drawable.shape_ellipsis_circular);
        linearLayout7.addView(imageView6);
        linearLayout.addView(linearLayout7);
    }
}

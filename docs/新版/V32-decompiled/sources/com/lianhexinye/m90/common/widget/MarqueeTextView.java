package com.lianhexinye.m90.common.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.amap.api.services.core.AMapException;
import com.lianhexinye.m90.R;
import com.lianhexinye.m90.common.utils.AndroidUtils;
import com.lianhexinye.m90.common.utils.log.LogUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class MarqueeTextView extends TextView {
    private int DEFAULT_HORIZONTAL_INTERVAL;
    private int DEFAULT_HORIZONTAL_LOOP_SPEED;
    private int DEFAULT_HORIZONTAL_SPEED;
    private int DEFAULT_VERTICAL_INTERVAL;
    private int DEFAULT_VERTICAL_SPEED;
    private final String TAG;
    private int contentColor;
    private int contentHeight;
    private ArrayList<String> contentList;
    private Paint contentPaint;
    private int contentTextSize;
    private int contentWidth;
    private int currentY;
    private int currnetIndex;
    private int currnetX;
    private boolean hasInited;
    private int horizontalLoopSpeed;
    private boolean horizontalOriLeft;
    private int horizontalScrollInterval;
    private int horizontalScrollSpeed;
    private boolean isHorizontalRunning;
    private boolean isHorizontalScroll;
    private boolean isTextAtMiddle;
    private boolean isVerticalRunning;
    private boolean isVerticalSwitch;
    private int maxContentHeight;
    private int maxContentWidth;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private String singleText;
    private int verticalSwitchInterval;
    private int verticalSwitchSpeed;
    private int viewHeight;
    private int viewWidth;
    private int xOffset;
    private int xStartPos;
    private int yStartPos;

    static /* synthetic */ int access$408(MarqueeTextView marqueeTextView) {
        int i = marqueeTextView.currnetIndex;
        marqueeTextView.currnetIndex = i + 1;
        return i;
    }

    public void setContentList(ArrayList<String> arrayList) {
        this.contentList = arrayList;
        requestLayout();
        postInvalidate();
    }

    public void setSingleText(String str) {
        this.singleText = str;
        requestLayout();
    }

    public void setVerticalSwitch(boolean z) {
        this.isVerticalSwitch = z;
        postInvalidate();
    }

    public void setHorizontalScroll(boolean z) {
        this.isHorizontalScroll = z;
        postInvalidate();
    }

    public void setHorizontalLoopSpeed(int i) {
        this.horizontalLoopSpeed = i;
        postInvalidate();
    }

    public void setVerticalSwitchSpeed(int i) {
        this.verticalSwitchSpeed = i;
        postInvalidate();
    }

    public void setVerticalSwitchInterval(int i) {
        this.verticalSwitchInterval = i;
        postInvalidate();
    }

    public void setHorizontalScrollSpeed(int i) {
        this.horizontalScrollSpeed = i;
        postInvalidate();
    }

    public void setHorizontalScrollInterval(int i) {
        this.horizontalScrollInterval = i;
        postInvalidate();
    }

    public MarqueeTextView(Context context) {
        super(context);
        this.TAG = "MarqueeTextView";
        this.contentList = new ArrayList<>();
        this.singleText = "";
        this.isVerticalSwitch = true;
        this.isHorizontalScroll = true;
        this.DEFAULT_VERTICAL_SPEED = 500;
        this.DEFAULT_VERTICAL_INTERVAL = 1000;
        this.DEFAULT_HORIZONTAL_SPEED = 1000;
        this.DEFAULT_HORIZONTAL_INTERVAL = AMapException.CODE_AMAP_SHARE_LICENSE_IS_EXPIRED;
        this.DEFAULT_HORIZONTAL_LOOP_SPEED = 1000;
        this.contentColor = -917715;
        this.contentTextSize = 36;
        this.hasInited = false;
        this.currentY = 0;
        this.currnetX = 0;
        this.xOffset = 0;
        this.yStartPos = 0;
        this.xStartPos = 0;
        this.currnetIndex = 0;
        this.maxContentWidth = 0;
        this.maxContentHeight = 0;
        this.isHorizontalRunning = false;
        this.isVerticalRunning = false;
        this.isTextAtMiddle = true;
        this.horizontalOriLeft = true;
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.TAG = "MarqueeTextView";
        this.contentList = new ArrayList<>();
        this.singleText = "";
        this.isVerticalSwitch = true;
        this.isHorizontalScroll = true;
        this.DEFAULT_VERTICAL_SPEED = 500;
        this.DEFAULT_VERTICAL_INTERVAL = 1000;
        this.DEFAULT_HORIZONTAL_SPEED = 1000;
        this.DEFAULT_HORIZONTAL_INTERVAL = AMapException.CODE_AMAP_SHARE_LICENSE_IS_EXPIRED;
        this.DEFAULT_HORIZONTAL_LOOP_SPEED = 1000;
        this.contentColor = -917715;
        this.contentTextSize = 36;
        this.hasInited = false;
        this.currentY = 0;
        this.currnetX = 0;
        this.xOffset = 0;
        this.yStartPos = 0;
        this.xStartPos = 0;
        this.currnetIndex = 0;
        this.maxContentWidth = 0;
        this.maxContentHeight = 0;
        this.isHorizontalRunning = false;
        this.isVerticalRunning = false;
        this.isTextAtMiddle = true;
        this.horizontalOriLeft = true;
        initAttrs(attributeSet);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TAG = "MarqueeTextView";
        this.contentList = new ArrayList<>();
        this.singleText = "";
        this.isVerticalSwitch = true;
        this.isHorizontalScroll = true;
        this.DEFAULT_VERTICAL_SPEED = 500;
        this.DEFAULT_VERTICAL_INTERVAL = 1000;
        this.DEFAULT_HORIZONTAL_SPEED = 1000;
        this.DEFAULT_HORIZONTAL_INTERVAL = AMapException.CODE_AMAP_SHARE_LICENSE_IS_EXPIRED;
        this.DEFAULT_HORIZONTAL_LOOP_SPEED = 1000;
        this.contentColor = -917715;
        this.contentTextSize = 36;
        this.hasInited = false;
        this.currentY = 0;
        this.currnetX = 0;
        this.xOffset = 0;
        this.yStartPos = 0;
        this.xStartPos = 0;
        this.currnetIndex = 0;
        this.maxContentWidth = 0;
        this.maxContentHeight = 0;
        this.isHorizontalRunning = false;
        this.isVerticalRunning = false;
        this.isTextAtMiddle = true;
        this.horizontalOriLeft = true;
        initAttrs(attributeSet);
        init();
    }

    public void setContentColor(int i) {
        this.contentColor = i;
    }

    public void setContentTextSize(int i) {
        this.contentTextSize = i;
    }

    private void initAttrs(AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.MarqueeTextView);
        this.verticalSwitchSpeed = typedArrayObtainStyledAttributes.getInt(7, this.DEFAULT_VERTICAL_SPEED);
        this.verticalSwitchInterval = typedArrayObtainStyledAttributes.getInt(6, this.DEFAULT_VERTICAL_INTERVAL);
        this.horizontalScrollSpeed = typedArrayObtainStyledAttributes.getInt(5, this.DEFAULT_HORIZONTAL_SPEED);
        this.horizontalScrollInterval = typedArrayObtainStyledAttributes.getInt(4, this.DEFAULT_HORIZONTAL_INTERVAL);
        this.horizontalLoopSpeed = typedArrayObtainStyledAttributes.getInt(3, this.DEFAULT_HORIZONTAL_LOOP_SPEED);
        this.contentColor = typedArrayObtainStyledAttributes.getColor(1, -16777216);
        this.contentTextSize = (int) typedArrayObtainStyledAttributes.getDimension(2, Sp2Px(getContext(), 15));
        this.singleText = typedArrayObtainStyledAttributes.getString(0);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void init() {
        Paint paint = new Paint();
        this.contentPaint = paint;
        paint.setAntiAlias(true);
        this.contentPaint.setDither(true);
        this.contentPaint.setTextSize(this.contentTextSize);
        this.contentPaint.setColor(this.contentColor);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.viewWidth = getMeasuredWidth();
        this.viewHeight = getMeasuredHeight();
        this.paddingLeft = getPaddingLeft();
        this.paddingTop = getPaddingTop();
        this.paddingRight = getPaddingRight();
        this.paddingBottom = getPaddingBottom();
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i2);
        ArrayList<String> arrayList = this.contentList;
        if (arrayList != null && arrayList.size() > 0) {
            for (int i3 = 0; i3 < this.contentList.size(); i3++) {
                String str = this.contentList.get(i3);
                Rect rect = new Rect();
                this.contentPaint.getTextBounds(str, 0, str.length(), rect);
                int iWidth = rect.width();
                this.maxContentHeight = Math.max(this.maxContentHeight, rect.height());
                this.maxContentWidth = Math.max(this.maxContentWidth, iWidth);
            }
        } else if (!AndroidUtils.isEmpty(this.singleText)) {
            Rect rect2 = new Rect();
            Paint paint = this.contentPaint;
            String str2 = this.singleText;
            paint.getTextBounds(str2, 0, str2.length(), rect2);
            this.maxContentWidth = rect2.width();
            this.maxContentHeight = rect2.height();
        }
        if (mode == 1073741824 && mode2 == 1073741824) {
            setMeasuredDimension(size, size2);
            return;
        }
        if (mode == 1073741824) {
            setMeasuredDimension(size, this.maxContentHeight);
        } else if (mode2 == 1073741824) {
            setMeasuredDimension(this.maxContentWidth, size2);
        } else {
            setMeasuredDimension(this.maxContentWidth, this.maxContentHeight);
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ArrayList<String> arrayList = this.contentList;
        if (arrayList != null && arrayList.size() > 1) {
            if (this.currnetIndex >= this.contentList.size()) {
                this.currnetIndex = 0;
            }
            this.viewHeight = getMeasuredHeight();
            this.viewWidth = getMeasuredWidth();
            String str = this.contentList.get(this.currnetIndex);
            int i = this.currnetIndex;
            int i2 = i + 1;
            if (i + 1 >= this.contentList.size()) {
                i2 = 0;
            }
            String str2 = this.contentList.get(i2);
            Rect rect = new Rect();
            this.contentPaint.getTextBounds(str, 0, str.length(), rect);
            int iWidth = rect.width();
            this.contentWidth = iWidth;
            this.xOffset = iWidth - this.viewWidth;
            Paint.FontMetrics fontMetrics = this.contentPaint.getFontMetrics();
            int i3 = (int) (((-fontMetrics.ascent) - fontMetrics.descent) / 2.0f);
            float f = fontMetrics.top;
            float f2 = fontMetrics.bottom;
            int i4 = (this.viewHeight / 2) + (this.maxContentHeight / 4) + (i3 / 4);
            this.yStartPos = i4;
            if (!this.hasInited) {
                this.hasInited = true;
                this.currentY = i4;
            }
            int i5 = this.xOffset;
            if (i5 > 0) {
                this.xOffset = i5 + (this.contentTextSize * 2);
                if (!this.isHorizontalRunning && !this.isVerticalRunning) {
                    this.isHorizontalRunning = true;
                    startHorizontalScroll();
                    this.currnetX = 0;
                }
            } else if (!this.isVerticalRunning) {
                this.isVerticalRunning = true;
                startVerticalInterval();
                this.currnetX = 0;
            }
            canvas.drawText(str, this.currnetX, this.currentY, this.contentPaint);
            canvas.drawText(str2, 0.0f, this.currentY + this.viewHeight, this.contentPaint);
            return;
        }
        if (AndroidUtils.isEmpty(this.singleText)) {
            return;
        }
        this.viewHeight = getMeasuredHeight();
        this.viewWidth = getMeasuredWidth();
        Rect rect2 = new Rect();
        Paint paint = this.contentPaint;
        String str3 = this.singleText;
        paint.getTextBounds(str3, 0, str3.length(), rect2);
        int iWidth2 = rect2.width();
        this.contentWidth = iWidth2;
        this.xOffset = iWidth2 - this.viewWidth;
        Paint.FontMetrics fontMetrics2 = this.contentPaint.getFontMetrics();
        int i6 = (int) (((-fontMetrics2.ascent) - fontMetrics2.descent) / 2.0f);
        float f3 = fontMetrics2.top;
        float f4 = fontMetrics2.bottom;
        this.yStartPos = (this.viewHeight / 2) + (this.maxContentHeight / 4) + (i6 / 4);
        if (!this.hasInited) {
            this.hasInited = true;
            this.currnetX = 0;
            this.xStartPos = 0;
        }
        int i7 = this.xOffset;
        if (i7 > 0) {
            this.xOffset = i7 + (this.contentTextSize * 2);
            if (!this.isHorizontalRunning) {
                this.isHorizontalRunning = true;
                LogUtils.d("MarqueeTextView", "MarqueeTextView");
                startHorizontalLoop();
            }
        }
        canvas.drawText(this.singleText, this.currnetX, this.yStartPos + 4, this.contentPaint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startVerticalInterval() {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(this.verticalSwitchInterval);
        valueAnimatorOfFloat.setInterpolator(new LinearInterpolator());
        valueAnimatorOfFloat.start();
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                MarqueeTextView.this.startVerticalSwitch();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startVerticalSwitch() {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(this.verticalSwitchSpeed);
        valueAnimatorOfFloat.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorOfFloat.start();
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                MarqueeTextView.this.currentY = (int) (r0.yStartPos - ((fFloatValue * MarqueeTextView.this.viewHeight) * 1.0f));
                MarqueeTextView.this.postInvalidate();
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                MarqueeTextView.access$408(MarqueeTextView.this);
                MarqueeTextView marqueeTextView = MarqueeTextView.this;
                marqueeTextView.currentY = marqueeTextView.yStartPos;
                MarqueeTextView.this.isVerticalRunning = false;
                MarqueeTextView.this.postInvalidate();
            }
        });
    }

    private void startHorizontalScroll() {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        int i = this.horizontalScrollSpeed;
        int i2 = this.xOffset;
        if ((i * i2) / this.contentTextSize < 0) {
            this.isHorizontalRunning = false;
            return;
        }
        valueAnimatorOfFloat.setDuration((i * i2) / r4);
        valueAnimatorOfFloat.setInterpolator(new LinearInterpolator());
        valueAnimatorOfFloat.start();
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                MarqueeTextView.this.currnetX = (int) ((-r0.xOffset) * fFloatValue);
                MarqueeTextView.this.postInvalidate();
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                MarqueeTextView.this.isHorizontalRunning = false;
                MarqueeTextView.this.isVerticalRunning = true;
                MarqueeTextView.this.startVerticalInterval();
                MarqueeTextView.this.postInvalidate();
            }
        });
    }

    private void startHorizontalLoop() {
        ValueAnimator valueAnimatorOfFloat = this.horizontalOriLeft ? ValueAnimator.ofFloat(0.0f, 1.0f) : ValueAnimator.ofFloat(0.0f, -1.0f);
        int i = this.horizontalScrollSpeed;
        int i2 = this.xOffset;
        if ((i * i2) / this.contentTextSize < 0) {
            this.isHorizontalRunning = false;
            return;
        }
        valueAnimatorOfFloat.setDuration((this.horizontalLoopSpeed * i2) / r3);
        valueAnimatorOfFloat.setInterpolator(new LinearInterpolator());
        valueAnimatorOfFloat.start();
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                MarqueeTextView.this.currnetX = (int) (r0.xStartPos - (MarqueeTextView.this.xOffset * fFloatValue));
                MarqueeTextView.this.postInvalidate();
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.lianhexinye.m90.common.widget.MarqueeTextView.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                MarqueeTextView.this.isHorizontalRunning = false;
                MarqueeTextView.this.horizontalOriLeft = !r2.horizontalOriLeft;
                MarqueeTextView marqueeTextView = MarqueeTextView.this;
                marqueeTextView.xStartPos = marqueeTextView.currnetX;
                MarqueeTextView.this.postInvalidate();
            }
        });
    }

    public static int Dp2Px(Context context, int i) {
        return (int) TypedValue.applyDimension(1, i, context.getResources().getDisplayMetrics());
    }

    public static int Px2Dp(Context context, int i) {
        return (int) TypedValue.applyDimension(0, i, context.getResources().getDisplayMetrics());
    }

    public static int Sp2Px(Context context, int i) {
        return (int) TypedValue.applyDimension(2, i, context.getResources().getDisplayMetrics());
    }

    public static int Px2Sp(Context context, int i) {
        return (int) TypedValue.applyDimension(0, i, context.getResources().getDisplayMetrics());
    }
}

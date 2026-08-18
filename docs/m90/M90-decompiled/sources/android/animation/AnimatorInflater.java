package android.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.animation.AnimationUtils;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AnimatorInflater {
    private static final int SEQUENTIALLY = 1;
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_COLOR = 4;
    private static final int VALUE_TYPE_CUSTOM = 5;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;

    public static Animator loadAnimator(Context context, int i) throws Resources.NotFoundException {
        XmlResourceParser animation = null;
        try {
            try {
                animation = context.getResources().getAnimation(i);
                return createAnimatorFromXml(context, animation);
            } catch (IOException e) {
                Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (XmlPullParserException e2) {
                Resources.NotFoundException notFoundException2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                notFoundException2.initCause(e2);
                throw notFoundException2;
            }
        } finally {
            if (animation != null) {
                animation.close();
            }
        }
    }

    private static Animator createAnimatorFromXml(Context context, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0086, code lost:
    
        if (r10 == null) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0088, code lost:
    
        if (r2 == null) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x008a, code lost:
    
        r7 = new android.animation.Animator[r2.size()];
        r8 = r2.iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0098, code lost:
    
        if (r8.hasNext() == false) goto L46;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x009a, code lost:
    
        r7[r6] = (android.animation.Animator) r8.next();
        r6 = r6 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a6, code lost:
    
        if (r11 != 0) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00a8, code lost:
    
        r10.playTogether(r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00ac, code lost:
    
        r10.playSequentially(r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00af, code lost:
    
        return r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.animation.Animator createAnimatorFromXml(android.content.Context r7, org.xmlpull.v1.XmlPullParser r8, android.util.AttributeSet r9, android.animation.AnimatorSet r10, int r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            int r0 = r8.getDepth()
            r1 = 0
            r2 = r1
            r3 = r2
        L7:
            int r4 = r8.next()
            r5 = 3
            r6 = 0
            if (r4 != r5) goto L15
            int r5 = r8.getDepth()
            if (r5 <= r0) goto L86
        L15:
            r5 = 1
            if (r4 == r5) goto L86
            r5 = 2
            if (r4 == r5) goto L1c
            goto L7
        L1c:
            java.lang.String r3 = r8.getName()
            java.lang.String r4 = "objectAnimator"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L2e
            android.animation.ObjectAnimator r3 = loadObjectAnimator(r7, r9)
            goto L5c
        L2e:
            java.lang.String r4 = "animator"
            boolean r4 = r3.equals(r4)
            if (r4 == 0) goto L3b
            android.animation.ValueAnimator r3 = loadAnimator(r7, r9, r1)
            goto L5c
        L3b:
            java.lang.String r4 = "set"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L69
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            int[] r4 = com.android.internal.R.styleable.AnimatorSet
            android.content.res.TypedArray r4 = r7.obtainStyledAttributes(r9, r4)
            int r5 = r4.getInt(r6, r6)
            r6 = r3
            android.animation.AnimatorSet r6 = (android.animation.AnimatorSet) r6
            createAnimatorFromXml(r7, r8, r9, r6, r5)
            r4.recycle()
        L5c:
            if (r10 == 0) goto L7
            if (r2 != 0) goto L65
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
        L65:
            r2.add(r3)
            goto L7
        L69:
            java.lang.RuntimeException r7 = new java.lang.RuntimeException
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Unknown animator name: "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r8 = r8.getName()
            java.lang.StringBuilder r8 = r9.append(r8)
            java.lang.String r8 = r8.toString()
            r7.<init>(r8)
            throw r7
        L86:
            if (r10 == 0) goto Laf
            if (r2 == 0) goto Laf
            int r7 = r2.size()
            android.animation.Animator[] r7 = new android.animation.Animator[r7]
            java.util.Iterator r8 = r2.iterator()
        L94:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto La6
            java.lang.Object r9 = r8.next()
            android.animation.Animator r9 = (android.animation.Animator) r9
            int r0 = r6 + 1
            r7[r6] = r9
            r6 = r0
            goto L94
        La6:
            if (r11 != 0) goto Lac
            r10.playTogether(r7)
            goto Laf
        Lac:
            r10.playSequentially(r7)
        Laf:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorInflater.createAnimatorFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.animation.AnimatorSet, int):android.animation.Animator");
    }

    private static ObjectAnimator loadObjectAnimator(Context context, AttributeSet attributeSet) throws Resources.NotFoundException {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(context, attributeSet, objectAnimator);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PropertyAnimator);
        objectAnimator.setPropertyName(typedArrayObtainStyledAttributes.getString(0));
        typedArrayObtainStyledAttributes.recycle();
        return objectAnimator;
    }

    private static ValueAnimator loadAnimator(Context context, AttributeSet attributeSet, ValueAnimator valueAnimator) throws Resources.NotFoundException {
        int i;
        int color;
        int color2;
        int color3;
        float dimension;
        float dimension2;
        float dimension3;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Animator);
        long j = typedArrayObtainStyledAttributes.getInt(1, 300);
        long j2 = typedArrayObtainStyledAttributes.getInt(2, 0);
        int i2 = typedArrayObtainStyledAttributes.getInt(7, 0);
        ValueAnimator valueAnimator2 = valueAnimator == null ? new ValueAnimator() : valueAnimator;
        boolean z = i2 == 0;
        TypedValue typedValuePeekValue = typedArrayObtainStyledAttributes.peekValue(5);
        boolean z2 = typedValuePeekValue != null;
        int i3 = z2 ? typedValuePeekValue.type : 0;
        TypedValue typedValuePeekValue2 = typedArrayObtainStyledAttributes.peekValue(6);
        boolean z3 = typedValuePeekValue2 != null;
        int i4 = z3 ? typedValuePeekValue2.type : 0;
        if ((z2 && i3 >= 28 && i3 <= 31) || (z3 && i4 >= 28 && i4 <= 31)) {
            valueAnimator2.setEvaluator(new ArgbEvaluator());
            z = false;
        }
        if (!z) {
            i = 0;
            if (z2) {
                if (i3 == 5) {
                    color2 = (int) typedArrayObtainStyledAttributes.getDimension(5, 0.0f);
                } else if (i3 >= 28 && i3 <= 31) {
                    color2 = typedArrayObtainStyledAttributes.getColor(5, 0);
                } else {
                    color2 = typedArrayObtainStyledAttributes.getInt(5, 0);
                }
                if (z3) {
                    if (i4 == 5) {
                        color3 = (int) typedArrayObtainStyledAttributes.getDimension(6, 0.0f);
                    } else if (i4 >= 28 && i4 <= 31) {
                        color3 = typedArrayObtainStyledAttributes.getColor(6, 0);
                    } else {
                        color3 = typedArrayObtainStyledAttributes.getInt(6, 0);
                    }
                    valueAnimator2.setIntValues(color2, color3);
                } else {
                    valueAnimator2.setIntValues(color2);
                }
            } else if (z3) {
                if (i4 == 5) {
                    color = (int) typedArrayObtainStyledAttributes.getDimension(6, 0.0f);
                } else if (i4 >= 28 && i4 <= 31) {
                    color = typedArrayObtainStyledAttributes.getColor(6, 0);
                } else {
                    color = typedArrayObtainStyledAttributes.getInt(6, 0);
                }
                valueAnimator2.setIntValues(color);
            }
        } else if (z2) {
            if (i3 == 5) {
                dimension2 = typedArrayObtainStyledAttributes.getDimension(5, 0.0f);
            } else {
                dimension2 = typedArrayObtainStyledAttributes.getFloat(5, 0.0f);
            }
            if (z3) {
                if (i4 == 5) {
                    dimension3 = typedArrayObtainStyledAttributes.getDimension(6, 0.0f);
                } else {
                    dimension3 = typedArrayObtainStyledAttributes.getFloat(6, 0.0f);
                }
                i = 0;
                valueAnimator2.setFloatValues(dimension2, dimension3);
            } else {
                i = 0;
                valueAnimator2.setFloatValues(dimension2);
            }
        } else {
            i = 0;
            if (i4 == 5) {
                dimension = typedArrayObtainStyledAttributes.getDimension(6, 0.0f);
            } else {
                dimension = typedArrayObtainStyledAttributes.getFloat(6, 0.0f);
            }
            valueAnimator2.setFloatValues(dimension);
        }
        valueAnimator2.setDuration(j);
        valueAnimator2.setStartDelay(j2);
        if (typedArrayObtainStyledAttributes.hasValue(3)) {
            valueAnimator2.setRepeatCount(typedArrayObtainStyledAttributes.getInt(3, i));
        }
        if (typedArrayObtainStyledAttributes.hasValue(4)) {
            valueAnimator2.setRepeatMode(typedArrayObtainStyledAttributes.getInt(4, 1));
        }
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        if (resourceId > 0) {
            valueAnimator2.setInterpolator(AnimationUtils.loadInterpolator(context, resourceId));
        }
        typedArrayObtainStyledAttributes.recycle();
        return valueAnimator2;
    }
}

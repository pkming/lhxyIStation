package android.transition;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class TransitionInflater {
    private Context mContext;

    private TransitionInflater(Context context) {
        this.mContext = context;
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    public Transition inflateTransition(int i) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            try {
                return createTransitionFromXml(xml, Xml.asAttributeSet(xml), null);
            } catch (IOException e) {
                InflateException inflateException = new InflateException(xml.getPositionDescription() + ": " + e.getMessage());
                inflateException.initCause(e);
                throw inflateException;
            } catch (XmlPullParserException e2) {
                InflateException inflateException2 = new InflateException(e2.getMessage());
                inflateException2.initCause(e2);
                throw inflateException2;
            }
        } finally {
            xml.close();
        }
    }

    public TransitionManager inflateTransitionManager(int i, ViewGroup viewGroup) {
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        try {
            try {
                return createTransitionManagerFromXml(xml, Xml.asAttributeSet(xml), viewGroup);
            } catch (IOException e) {
                InflateException inflateException = new InflateException(xml.getPositionDescription() + ": " + e.getMessage());
                inflateException.initCause(e);
                throw inflateException;
            } catch (XmlPullParserException e2) {
                InflateException inflateException2 = new InflateException(e2.getMessage());
                inflateException2.initCause(e2);
                throw inflateException2;
            }
        } finally {
            xml.close();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x00f1, code lost:
    
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.transition.Transition createTransitionFromXml(org.xmlpull.v1.XmlPullParser r9, android.util.AttributeSet r10, android.transition.TransitionSet r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 242
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionInflater.createTransitionFromXml(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.transition.TransitionSet):android.transition.Transition");
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x005f, code lost:
    
        r6 = r1.size();
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0063, code lost:
    
        if (r6 <= 0) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0065, code lost:
    
        if (r4 >= r6) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0067, code lost:
    
        r8.addTarget(((java.lang.Integer) r1.get(r4)).intValue());
        r4 = r4 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0077, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:?, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void getTargetIds(org.xmlpull.v1.XmlPullParser r6, android.util.AttributeSet r7, android.transition.Transition r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r5 = this;
            int r0 = r6.getDepth()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L9:
            int r2 = r6.next()
            r3 = 3
            r4 = 0
            if (r2 != r3) goto L17
            int r3 = r6.getDepth()
            if (r3 <= r0) goto L5f
        L17:
            r3 = 1
            if (r2 == r3) goto L5f
            r3 = 2
            if (r2 == r3) goto L1e
            goto L9
        L1e:
            java.lang.String r2 = r6.getName()
            java.lang.String r3 = "target"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L42
            android.content.Context r2 = r5.mContext
            int[] r3 = com.android.internal.R.styleable.TransitionTarget
            android.content.res.TypedArray r2 = r2.obtainStyledAttributes(r7, r3)
            r3 = -1
            int r2 = r2.getResourceId(r4, r3)
            if (r2 < 0) goto L9
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.add(r2)
            goto L9
        L42:
            java.lang.RuntimeException r7 = new java.lang.RuntimeException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "Unknown scene name: "
            java.lang.StringBuilder r8 = r8.append(r0)
            java.lang.String r6 = r6.getName()
            java.lang.StringBuilder r6 = r8.append(r6)
            java.lang.String r6 = r6.toString()
            r7.<init>(r6)
            throw r7
        L5f:
            int r6 = r1.size()
            if (r6 <= 0) goto L77
        L65:
            if (r4 >= r6) goto L77
            java.lang.Object r7 = r1.get(r4)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            r8.addTarget(r7)
            int r4 = r4 + 1
            goto L65
        L77:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionInflater.getTargetIds(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.transition.Transition):void");
    }

    private Transition loadTransition(Transition transition, AttributeSet attributeSet) throws Resources.NotFoundException {
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.Transition);
        long j = typedArrayObtainStyledAttributes.getInt(1, -1);
        if (j >= 0) {
            transition.setDuration(j);
        }
        long j2 = typedArrayObtainStyledAttributes.getInt(2, -1);
        if (j2 > 0) {
            transition.setStartDelay(j2);
        }
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        if (resourceId > 0) {
            transition.setInterpolator(AnimationUtils.loadInterpolator(this.mContext, resourceId));
        }
        typedArrayObtainStyledAttributes.recycle();
        return transition;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0058, code lost:
    
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.transition.TransitionManager createTransitionManagerFromXml(org.xmlpull.v1.XmlPullParser r5, android.util.AttributeSet r6, android.view.ViewGroup r7) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r4 = this;
            int r0 = r5.getDepth()
            r1 = 0
        L5:
            int r2 = r5.next()
            r3 = 3
            if (r2 != r3) goto L12
            int r3 = r5.getDepth()
            if (r3 <= r0) goto L58
        L12:
            r3 = 1
            if (r2 == r3) goto L58
            r3 = 2
            if (r2 == r3) goto L19
            goto L5
        L19:
            java.lang.String r2 = r5.getName()
            java.lang.String r3 = "transitionManager"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L2c
            android.transition.TransitionManager r1 = new android.transition.TransitionManager
            r1.<init>()
            goto L5
        L2c:
            java.lang.String r3 = "transition"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L3b
            if (r1 == 0) goto L3b
            r4.loadTransition(r6, r7, r1)
            goto L5
        L3b:
            java.lang.RuntimeException r6 = new java.lang.RuntimeException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "Unknown scene name: "
            java.lang.StringBuilder r7 = r7.append(r0)
            java.lang.String r5 = r5.getName()
            java.lang.StringBuilder r5 = r7.append(r5)
            java.lang.String r5 = r5.toString()
            r6.<init>(r5)
            throw r6
        L58:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionInflater.createTransitionManagerFromXml(org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.view.ViewGroup):android.transition.TransitionManager");
    }

    private void loadTransition(AttributeSet attributeSet, ViewGroup viewGroup, TransitionManager transitionManager) throws Resources.NotFoundException {
        Transition transitionInflateTransition;
        TypedArray typedArrayObtainStyledAttributes = this.mContext.obtainStyledAttributes(attributeSet, R.styleable.TransitionManager);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(2, -1);
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(0, -1);
        Scene sceneForLayout = resourceId2 >= 0 ? Scene.getSceneForLayout(viewGroup, resourceId2, this.mContext) : null;
        int resourceId3 = typedArrayObtainStyledAttributes.getResourceId(1, -1);
        Scene sceneForLayout2 = resourceId3 >= 0 ? Scene.getSceneForLayout(viewGroup, resourceId3, this.mContext) : null;
        if (resourceId >= 0 && (transitionInflateTransition = inflateTransition(resourceId)) != null) {
            if (sceneForLayout != null) {
                if (sceneForLayout2 == null) {
                    throw new RuntimeException("No matching toScene for given fromScene for transition ID " + resourceId);
                }
                transitionManager.setTransition(sceneForLayout, sceneForLayout2, transitionInflateTransition);
            } else if (resourceId3 >= 0) {
                transitionManager.setTransition(sceneForLayout2, transitionInflateTransition);
            }
        }
        typedArrayObtainStyledAttributes.recycle();
    }
}

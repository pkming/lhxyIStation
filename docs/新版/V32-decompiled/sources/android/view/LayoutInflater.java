package android.view;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public abstract class LayoutInflater {
    private static final String TAG_1995 = "blink";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_MERGE = "merge";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";
    static final Class<?>[] mConstructorSignature = {Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();
    private final boolean DEBUG;
    final Object[] mConstructorArgs;
    protected final Context mContext;
    private Factory mFactory;
    private Factory2 mFactory2;
    private boolean mFactorySet;
    private Filter mFilter;
    private HashMap<String, Boolean> mFilterMap;
    private Factory2 mPrivateFactory;

    public interface Factory {
        View onCreateView(String str, Context context, AttributeSet attributeSet);
    }

    public interface Factory2 extends Factory {
        View onCreateView(View view, String str, Context context, AttributeSet attributeSet);
    }

    public interface Filter {
        boolean onLoadClass(Class cls);
    }

    public abstract LayoutInflater cloneInContext(Context context);

    private static class FactoryMerger implements Factory2 {
        private final Factory mF1;
        private final Factory2 mF12;
        private final Factory mF2;
        private final Factory2 mF22;

        FactoryMerger(Factory factory, Factory2 factory2, Factory factory3, Factory2 factory22) {
            this.mF1 = factory;
            this.mF2 = factory3;
            this.mF12 = factory2;
            this.mF22 = factory22;
        }

        @Override // android.view.LayoutInflater.Factory
        public View onCreateView(String str, Context context, AttributeSet attributeSet) {
            View viewOnCreateView = this.mF1.onCreateView(str, context, attributeSet);
            return viewOnCreateView != null ? viewOnCreateView : this.mF2.onCreateView(str, context, attributeSet);
        }

        @Override // android.view.LayoutInflater.Factory2
        public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
            Factory2 factory2 = this.mF12;
            View viewOnCreateView = factory2 != null ? factory2.onCreateView(view, str, context, attributeSet) : this.mF1.onCreateView(str, context, attributeSet);
            if (viewOnCreateView != null) {
                return viewOnCreateView;
            }
            Factory2 factory22 = this.mF22;
            return factory22 != null ? factory22.onCreateView(view, str, context, attributeSet) : this.mF2.onCreateView(str, context, attributeSet);
        }
    }

    protected LayoutInflater(Context context) {
        this.DEBUG = false;
        this.mConstructorArgs = new Object[2];
        this.mContext = context;
    }

    protected LayoutInflater(LayoutInflater layoutInflater, Context context) {
        this.DEBUG = false;
        this.mConstructorArgs = new Object[2];
        this.mContext = context;
        this.mFactory = layoutInflater.mFactory;
        this.mFactory2 = layoutInflater.mFactory2;
        this.mPrivateFactory = layoutInflater.mPrivateFactory;
        this.mFilter = layoutInflater.mFilter;
    }

    public static LayoutInflater from(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            return layoutInflater;
        }
        throw new AssertionError("LayoutInflater not found.");
    }

    public Context getContext() {
        return this.mContext;
    }

    public final Factory getFactory() {
        return this.mFactory;
    }

    public final Factory2 getFactory2() {
        return this.mFactory2;
    }

    public void setFactory(Factory factory) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        Objects.requireNonNull(factory, "Given factory can not be null");
        this.mFactorySet = true;
        if (this.mFactory == null) {
            this.mFactory = factory;
        } else {
            this.mFactory = new FactoryMerger(factory, null, this.mFactory, this.mFactory2);
        }
    }

    public void setFactory2(Factory2 factory2) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        Objects.requireNonNull(factory2, "Given factory can not be null");
        this.mFactorySet = true;
        if (this.mFactory == null) {
            this.mFactory2 = factory2;
            this.mFactory = factory2;
        } else {
            this.mFactory = new FactoryMerger(factory2, factory2, this.mFactory, this.mFactory2);
        }
    }

    public void setPrivateFactory(Factory2 factory2) {
        this.mPrivateFactory = factory2;
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public void setFilter(Filter filter) {
        this.mFilter = filter;
        if (filter != null) {
            this.mFilterMap = new HashMap<>();
        }
    }

    public View inflate(int i, ViewGroup viewGroup) {
        return inflate(i, viewGroup, viewGroup != null);
    }

    public View inflate(XmlPullParser xmlPullParser, ViewGroup viewGroup) {
        return inflate(xmlPullParser, viewGroup, viewGroup != null);
    }

    public View inflate(int i, ViewGroup viewGroup, boolean z) {
        XmlResourceParser layout = getContext().getResources().getLayout(i);
        try {
            return inflate(layout, viewGroup, z);
        } finally {
            layout.close();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v0, types: [android.view.View, android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r12v5 */
    /* JADX WARN: Type inference failed for: r12v6, types: [android.view.View] */
    public View inflate(XmlPullParser xmlPullParser, ViewGroup viewGroup, boolean z) {
        int next;
        View viewCreateViewFromTag;
        ViewGroup.LayoutParams layoutParamsGenerateLayoutParams;
        synchronized (this.mConstructorArgs) {
            Trace.traceBegin(8L, "inflate");
            AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlPullParser);
            Object[] objArr = this.mConstructorArgs;
            Context context = (Context) objArr[0];
            objArr[0] = this.mContext;
            do {
                try {
                    try {
                        try {
                            next = xmlPullParser.next();
                            if (next == 2) {
                                break;
                            }
                        } catch (XmlPullParserException e) {
                            InflateException inflateException = new InflateException(e.getMessage());
                            inflateException.initCause(e);
                            throw inflateException;
                        }
                    } catch (IOException e2) {
                        InflateException inflateException2 = new InflateException(xmlPullParser.getPositionDescription() + ": " + e2.getMessage());
                        inflateException2.initCause(e2);
                        throw inflateException2;
                    }
                } catch (Throwable th) {
                    Object[] objArr2 = this.mConstructorArgs;
                    objArr2[0] = context;
                    objArr2[1] = null;
                    throw th;
                }
            } while (next != 1);
            if (next != 2) {
                throw new InflateException(xmlPullParser.getPositionDescription() + ": No start tag found!");
            }
            String name = xmlPullParser.getName();
            if (!TAG_MERGE.equals(name)) {
                if (TAG_1995.equals(name)) {
                    viewCreateViewFromTag = new BlinkLayout(this.mContext, attributeSetAsAttributeSet);
                } else {
                    viewCreateViewFromTag = createViewFromTag(viewGroup, name, attributeSetAsAttributeSet);
                }
                if (viewGroup != 0) {
                    layoutParamsGenerateLayoutParams = viewGroup.generateLayoutParams(attributeSetAsAttributeSet);
                    if (!z) {
                        viewCreateViewFromTag.setLayoutParams(layoutParamsGenerateLayoutParams);
                    }
                } else {
                    layoutParamsGenerateLayoutParams = null;
                }
                rInflate(xmlPullParser, viewCreateViewFromTag, attributeSetAsAttributeSet, true);
                if (viewGroup != 0 && z) {
                    viewGroup.addView(viewCreateViewFromTag, layoutParamsGenerateLayoutParams);
                }
                if (viewGroup == 0 || !z) {
                    viewGroup = viewCreateViewFromTag;
                }
            } else {
                if (viewGroup == 0 || !z) {
                    throw new InflateException("<merge /> can be used only with a valid ViewGroup root and attachToRoot=true");
                }
                rInflate(xmlPullParser, viewGroup, attributeSetAsAttributeSet, false);
            }
            Object[] objArr3 = this.mConstructorArgs;
            objArr3[0] = context;
            objArr3[1] = null;
            Trace.traceEnd(8L);
        }
        return viewGroup;
    }

    public final View createView(String str, String str2, AttributeSet attributeSet) throws InflateException, ClassNotFoundException {
        HashMap<String, Constructor<? extends View>> map = sConstructorMap;
        Constructor<? extends View> constructor = map.get(str);
        Class clsAsSubclass = null;
        try {
            try {
                try {
                    try {
                        Trace.traceBegin(8L, str);
                        if (constructor == null) {
                            clsAsSubclass = this.mContext.getClassLoader().loadClass(str2 != null ? str2 + str : str).asSubclass(View.class);
                            Filter filter = this.mFilter;
                            if (filter != null && clsAsSubclass != null && !filter.onLoadClass(clsAsSubclass)) {
                                failNotAllowed(str, str2, attributeSet);
                            }
                            constructor = clsAsSubclass.getConstructor(mConstructorSignature);
                            map.put(str, constructor);
                        } else if (this.mFilter != null) {
                            Boolean bool = this.mFilterMap.get(str);
                            if (bool == null) {
                                clsAsSubclass = this.mContext.getClassLoader().loadClass(str2 != null ? str2 + str : str).asSubclass(View.class);
                                boolean z = clsAsSubclass != null && this.mFilter.onLoadClass(clsAsSubclass);
                                this.mFilterMap.put(str, Boolean.valueOf(z));
                                if (!z) {
                                    failNotAllowed(str, str2, attributeSet);
                                }
                            } else if (bool.equals(Boolean.FALSE)) {
                                failNotAllowed(str, str2, attributeSet);
                            }
                        }
                        Object[] objArr = this.mConstructorArgs;
                        objArr[1] = attributeSet;
                        View viewNewInstance = constructor.newInstance(objArr);
                        if (viewNewInstance instanceof ViewStub) {
                            ((ViewStub) viewNewInstance).setLayoutInflater(this);
                        }
                        return viewNewInstance;
                    } catch (NoSuchMethodException e) {
                        StringBuilder sbAppend = new StringBuilder().append(attributeSet.getPositionDescription()).append(": Error inflating class ");
                        if (str2 != null) {
                            str = str2 + str;
                        }
                        InflateException inflateException = new InflateException(sbAppend.append(str).toString());
                        inflateException.initCause(e);
                        throw inflateException;
                    }
                } catch (Exception e2) {
                    InflateException inflateException2 = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + (clsAsSubclass == null ? MediaStore.UNKNOWN_STRING : clsAsSubclass.getName()));
                    inflateException2.initCause(e2);
                    throw inflateException2;
                }
            } catch (ClassCastException e3) {
                StringBuilder sbAppend2 = new StringBuilder().append(attributeSet.getPositionDescription()).append(": Class is not a View ");
                if (str2 != null) {
                    str = str2 + str;
                }
                InflateException inflateException3 = new InflateException(sbAppend2.append(str).toString());
                inflateException3.initCause(e3);
                throw inflateException3;
            } catch (ClassNotFoundException e4) {
                throw e4;
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private void failNotAllowed(String str, String str2, AttributeSet attributeSet) {
        StringBuilder sbAppend = new StringBuilder().append(attributeSet.getPositionDescription()).append(": Class not allowed to be inflated ");
        if (str2 != null) {
            str = str2 + str;
        }
        throw new InflateException(sbAppend.append(str).toString());
    }

    protected View onCreateView(String str, AttributeSet attributeSet) throws ClassNotFoundException {
        return createView(str, "android.view.", attributeSet);
    }

    protected View onCreateView(View view, String str, AttributeSet attributeSet) throws ClassNotFoundException {
        return onCreateView(str, attributeSet);
    }

    View createViewFromTag(View view, String str, AttributeSet attributeSet) {
        View viewOnCreateView;
        Factory2 factory2;
        if (str.equals("view")) {
            str = attributeSet.getAttributeValue(null, "class");
        }
        try {
            Factory2 factory22 = this.mFactory2;
            if (factory22 != null) {
                viewOnCreateView = factory22.onCreateView(view, str, this.mContext, attributeSet);
            } else {
                Factory factory = this.mFactory;
                viewOnCreateView = factory != null ? factory.onCreateView(str, this.mContext, attributeSet) : null;
            }
            if (viewOnCreateView == null && (factory2 = this.mPrivateFactory) != null) {
                viewOnCreateView = factory2.onCreateView(view, str, this.mContext, attributeSet);
            }
            if (viewOnCreateView != null) {
                return viewOnCreateView;
            }
            if (-1 == str.indexOf(46)) {
                return onCreateView(view, str, attributeSet);
            }
            return createView(str, null, attributeSet);
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException inflateException = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str);
            inflateException.initCause(e2);
            throw inflateException;
        } catch (Exception e3) {
            InflateException inflateException2 = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str);
            inflateException2.initCause(e3);
            throw inflateException2;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x0082, code lost:
    
        if (r9 == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0084, code lost:
    
        r7.onFinishInflate();
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0087, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:?, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void rInflate(org.xmlpull.v1.XmlPullParser r6, android.view.View r7, android.util.AttributeSet r8, boolean r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r5 = this;
            int r0 = r6.getDepth()
        L4:
            int r1 = r6.next()
            r2 = 3
            if (r1 != r2) goto L11
            int r2 = r6.getDepth()
            if (r2 <= r0) goto L82
        L11:
            r2 = 1
            if (r1 == r2) goto L82
            r3 = 2
            if (r1 == r3) goto L18
            goto L4
        L18:
            java.lang.String r1 = r6.getName()
            java.lang.String r3 = "requestFocus"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L29
            r5.parseRequestFocus(r6, r7)
            goto L4
        L29:
            java.lang.String r3 = "include"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L43
            int r1 = r6.getDepth()
            if (r1 == 0) goto L3b
            r5.parseInclude(r6, r7, r8)
            goto L4
        L3b:
            android.view.InflateException r6 = new android.view.InflateException
            java.lang.String r7 = "<include /> cannot be the root element"
            r6.<init>(r7)
            throw r6
        L43:
            java.lang.String r3 = "merge"
            boolean r3 = r3.equals(r1)
            if (r3 != 0) goto L7a
            java.lang.String r3 = "blink"
            boolean r3 = r3.equals(r1)
            if (r3 == 0) goto L68
            android.view.LayoutInflater$BlinkLayout r1 = new android.view.LayoutInflater$BlinkLayout
            android.content.Context r3 = r5.mContext
            r1.<init>(r3, r8)
            r3 = r7
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            android.view.ViewGroup$LayoutParams r4 = r3.generateLayoutParams(r8)
            r5.rInflate(r6, r1, r8, r2)
            r3.addView(r1, r4)
            goto L4
        L68:
            android.view.View r1 = r5.createViewFromTag(r7, r1, r8)
            r3 = r7
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            android.view.ViewGroup$LayoutParams r4 = r3.generateLayoutParams(r8)
            r5.rInflate(r6, r1, r8, r2)
            r3.addView(r1, r4)
            goto L4
        L7a:
            android.view.InflateException r6 = new android.view.InflateException
            java.lang.String r7 = "<merge /> must be the root element"
            r6.<init>(r7)
            throw r6
        L82:
            if (r9 == 0) goto L87
            r7.onFinishInflate()
        L87:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.LayoutInflater.rInflate(org.xmlpull.v1.XmlPullParser, android.view.View, android.util.AttributeSet, boolean):void");
    }

    private void parseRequestFocus(XmlPullParser xmlPullParser, View view) throws XmlPullParserException, IOException {
        int next;
        view.requestFocus();
        int depth = xmlPullParser.getDepth();
        do {
            next = xmlPullParser.next();
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
        } while (next != 1);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x009e A[Catch: all -> 0x00ed, TryCatch #2 {all -> 0x00ed, blocks: (B:13:0x0047, B:14:0x004b, B:19:0x0058, B:21:0x0064, B:22:0x0068, B:25:0x0074, B:32:0x0081, B:34:0x009e, B:42:0x00b4, B:39:0x00a8, B:40:0x00ac, B:41:0x00b1, B:51:0x00cf, B:52:0x00d0, B:53:0x00ec, B:23:0x006e, B:29:0x007a), top: B:63:0x0047, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00a3  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00b1 A[Catch: all -> 0x00ed, TryCatch #2 {all -> 0x00ed, blocks: (B:13:0x0047, B:14:0x004b, B:19:0x0058, B:21:0x0064, B:22:0x0068, B:25:0x0074, B:32:0x0081, B:34:0x009e, B:42:0x00b4, B:39:0x00a8, B:40:0x00ac, B:41:0x00b1, B:51:0x00cf, B:52:0x00d0, B:53:0x00ec, B:23:0x006e, B:29:0x007a), top: B:63:0x0047, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void parseInclude(org.xmlpull.v1.XmlPullParser r10, android.view.View r11, android.util.AttributeSet r12) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 250
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.LayoutInflater.parseInclude(org.xmlpull.v1.XmlPullParser, android.view.View, android.util.AttributeSet):void");
    }

    private static class BlinkLayout extends FrameLayout {
        private static final int BLINK_DELAY = 500;
        private static final int MESSAGE_BLINK = 66;
        private boolean mBlink;
        private boolean mBlinkState;
        private final Handler mHandler;

        public BlinkLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mHandler = new Handler(new Handler.Callback() { // from class: android.view.LayoutInflater.BlinkLayout.1
                @Override // android.os.Handler.Callback
                public boolean handleMessage(Message message) {
                    if (message.what != 66) {
                        return false;
                    }
                    if (BlinkLayout.this.mBlink) {
                        BlinkLayout.this.mBlinkState = !r3.mBlinkState;
                        BlinkLayout.this.makeBlink();
                    }
                    BlinkLayout.this.invalidate();
                    return true;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void makeBlink() {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(66), 500L);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.mBlink = true;
            this.mBlinkState = true;
            makeBlink();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.mBlink = false;
            this.mBlinkState = true;
            this.mHandler.removeMessages(66);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.mBlinkState) {
                super.dispatchDraw(canvas);
            }
        }
    }
}

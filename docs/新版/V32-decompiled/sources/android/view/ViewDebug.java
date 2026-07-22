package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes.dex */
public class ViewDebug {
    private static final int CAPTURE_TIMEOUT = 4000;
    public static final boolean DEBUG_DRAG = false;
    private static final String REMOTE_COMMAND_CAPTURE = "CAPTURE";
    private static final String REMOTE_COMMAND_CAPTURE_LAYERS = "CAPTURE_LAYERS";
    private static final String REMOTE_COMMAND_DUMP = "DUMP";
    private static final String REMOTE_COMMAND_INVALIDATE = "INVALIDATE";
    private static final String REMOTE_COMMAND_OUTPUT_DISPLAYLIST = "OUTPUT_DISPLAYLIST";
    private static final String REMOTE_COMMAND_REQUEST_LAYOUT = "REQUEST_LAYOUT";
    private static final String REMOTE_PROFILE = "PROFILE";

    @Deprecated
    public static final boolean TRACE_HIERARCHY = false;

    @Deprecated
    public static final boolean TRACE_RECYCLER = false;
    private static HashMap<Class<?>, Field[]> mCapturedViewFieldsForClasses;
    private static HashMap<Class<?>, Method[]> mCapturedViewMethodsForClasses;
    private static HashMap<AccessibleObject, ExportedProperty> sAnnotations;
    private static HashMap<Class<?>, Field[]> sFieldsForClasses;
    private static HashMap<Class<?>, Method[]> sMethodsForClasses;

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CapturedViewProperty {
        boolean retrieveReturn() default false;
    }

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExportedProperty {
        String category() default "";

        boolean deepExport() default false;

        FlagToString[] flagMapping() default {};

        IntToString[] indexMapping() default {};

        IntToString[] mapping() default {};

        String prefix() default "";

        boolean resolveId() default false;
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FlagToString {
        int equals();

        int mask();

        String name();

        boolean outputIf() default true;
    }

    public interface HierarchyHandler {
        void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i);

        View findHierarchyView(String str, int i);
    }

    @Deprecated
    public enum HierarchyTraceType {
        INVALIDATE,
        INVALIDATE_CHILD,
        INVALIDATE_CHILD_IN_PARENT,
        REQUEST_LAYOUT,
        ON_LAYOUT,
        ON_MEASURE,
        DRAW,
        BUILD_CACHE
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IntToString {
        int from();

        String to();
    }

    @Deprecated
    public enum RecyclerTraceType {
        NEW_VIEW,
        BIND_VIEW,
        RECYCLE_FROM_ACTIVE_HEAP,
        RECYCLE_FROM_SCRAP_HEAP,
        MOVE_TO_SCRAP_HEAP,
        MOVE_FROM_ACTIVE_TO_SCRAP_HEAP
    }

    interface ViewOperation<T> {
        void post(T... tArr);

        T[] pre();

        void run(T... tArr);
    }

    @Deprecated
    public static void startHierarchyTracing(String str, View view) {
    }

    @Deprecated
    public static void startRecyclerTracing(String str, View view) {
    }

    @Deprecated
    public static void stopHierarchyTracing() {
    }

    @Deprecated
    public static void stopRecyclerTracing() {
    }

    @Deprecated
    public static void trace(View view, HierarchyTraceType hierarchyTraceType) {
    }

    @Deprecated
    public static void trace(View view, RecyclerTraceType recyclerTraceType, int... iArr) {
    }

    public static long getViewInstanceCount() {
        return Debug.countInstancesOfClass(View.class);
    }

    public static long getViewRootImplCount() {
        return Debug.countInstancesOfClass(ViewRootImpl.class);
    }

    static void dispatchCommand(View view, String str, String str2, OutputStream outputStream) throws Throwable {
        View rootView = view.getRootView();
        if (REMOTE_COMMAND_DUMP.equalsIgnoreCase(str)) {
            dump(rootView, false, true, outputStream);
            return;
        }
        if (REMOTE_COMMAND_CAPTURE_LAYERS.equalsIgnoreCase(str)) {
            captureLayers(rootView, new DataOutputStream(outputStream));
            return;
        }
        String[] strArrSplit = str2.split(" ");
        if (REMOTE_COMMAND_CAPTURE.equalsIgnoreCase(str)) {
            capture(rootView, outputStream, strArrSplit[0]);
            return;
        }
        if (REMOTE_COMMAND_OUTPUT_DISPLAYLIST.equalsIgnoreCase(str)) {
            outputDisplayList(rootView, strArrSplit[0]);
            return;
        }
        if (REMOTE_COMMAND_INVALIDATE.equalsIgnoreCase(str)) {
            invalidate(rootView, strArrSplit[0]);
        } else if (REMOTE_COMMAND_REQUEST_LAYOUT.equalsIgnoreCase(str)) {
            requestLayout(rootView, strArrSplit[0]);
        } else if (REMOTE_PROFILE.equalsIgnoreCase(str)) {
            profile(rootView, outputStream, strArrSplit[0]);
        }
    }

    public static View findView(View view, String str) {
        if (str.indexOf(64) != -1) {
            String[] strArrSplit = str.split("@");
            String str2 = strArrSplit[0];
            int i = (int) Long.parseLong(strArrSplit[1], 16);
            View rootView = view.getRootView();
            if (rootView instanceof ViewGroup) {
                return findView((ViewGroup) rootView, str2, i);
            }
            return null;
        }
        return view.getRootView().findViewById(view.getResources().getIdentifier(str, null, null));
    }

    private static void invalidate(View view, String str) {
        View viewFindView = findView(view, str);
        if (viewFindView != null) {
            viewFindView.postInvalidate();
        }
    }

    private static void requestLayout(View view, String str) {
        final View viewFindView = findView(view, str);
        if (viewFindView != null) {
            view.post(new Runnable() { // from class: android.view.ViewDebug.1
                @Override // java.lang.Runnable
                public void run() {
                    viewFindView.requestLayout();
                }
            });
        }
    }

    private static void profile(View view, OutputStream outputStream, String str) throws Throwable {
        BufferedWriter bufferedWriter;
        View viewFindView = findView(view, str);
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream), 32768);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            if (viewFindView != null) {
                profileViewAndChildren(viewFindView, bufferedWriter);
            } else {
                bufferedWriter.write("-1 -1 -1");
                bufferedWriter.newLine();
            }
            bufferedWriter.write("DONE.");
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e2) {
            e = e2;
            bufferedWriter2 = bufferedWriter;
            Log.w("View", "Problem profiling the view:", e);
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedWriter2 = bufferedWriter;
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
            throw th;
        }
    }

    public static void profileViewAndChildren(View view, BufferedWriter bufferedWriter) throws IOException {
        profileViewAndChildren(view, bufferedWriter, true);
    }

    private static void profileViewAndChildren(final View view, BufferedWriter bufferedWriter, boolean z) throws IOException {
        long jProfileViewOperation = (z || (view.mPrivateFlags & 2048) != 0) ? profileViewOperation(view, new ViewOperation<Void>() { // from class: android.view.ViewDebug.2
            @Override // android.view.ViewDebug.ViewOperation
            public void post(Void... voidArr) {
            }

            @Override // android.view.ViewDebug.ViewOperation
            public Void[] pre() {
                forceLayout(view);
                return null;
            }

            private void forceLayout(View view2) {
                view2.forceLayout();
                if (view2 instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view2;
                    int childCount = viewGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        forceLayout(viewGroup.getChildAt(i));
                    }
                }
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run(Void... voidArr) {
                View view2 = view;
                view2.measure(view2.mOldWidthMeasureSpec, view.mOldHeightMeasureSpec);
            }
        }) : 0L;
        long jProfileViewOperation2 = (z || (view.mPrivateFlags & 8192) != 0) ? profileViewOperation(view, new ViewOperation<Void>() { // from class: android.view.ViewDebug.3
            @Override // android.view.ViewDebug.ViewOperation
            public void post(Void... voidArr) {
            }

            @Override // android.view.ViewDebug.ViewOperation
            public Void[] pre() {
                return null;
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run(Void... voidArr) {
                View view2 = view;
                view2.layout(view2.mLeft, view.mTop, view.mRight, view.mBottom);
            }
        }) : 0L;
        long jProfileViewOperation3 = (!z && view.willNotDraw() && (view.mPrivateFlags & 32) == 0) ? 0L : profileViewOperation(view, new ViewOperation<Object>() { // from class: android.view.ViewDebug.4
            @Override // android.view.ViewDebug.ViewOperation
            public Object[] pre() {
                View view2 = view;
                DisplayMetrics displayMetrics = (view2 == null || view2.getResources() == null) ? null : view.getResources().getDisplayMetrics();
                Bitmap bitmapCreateBitmap = displayMetrics != null ? Bitmap.createBitmap(displayMetrics, displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.RGB_565) : null;
                return new Object[]{bitmapCreateBitmap, bitmapCreateBitmap != null ? new Canvas(bitmapCreateBitmap) : null};
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void run(Object... objArr) {
                if (objArr[1] != null) {
                    view.draw((Canvas) objArr[1]);
                }
            }

            @Override // android.view.ViewDebug.ViewOperation
            public void post(Object... objArr) {
                if (objArr[1] != null) {
                    ((Canvas) objArr[1]).setBitmap(null);
                }
                if (objArr[0] != null) {
                    ((Bitmap) objArr[0]).recycle();
                }
            }
        });
        bufferedWriter.write(String.valueOf(jProfileViewOperation));
        bufferedWriter.write(32);
        bufferedWriter.write(String.valueOf(jProfileViewOperation2));
        bufferedWriter.write(32);
        bufferedWriter.write(String.valueOf(jProfileViewOperation3));
        bufferedWriter.newLine();
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                profileViewAndChildren(viewGroup.getChildAt(i), bufferedWriter, false);
            }
        }
    }

    private static <T> long profileViewOperation(View view, final ViewOperation<T> viewOperation) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final long[] jArr = new long[1];
        view.post(new Runnable() { // from class: android.view.ViewDebug.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Object[] objArrPre = viewOperation.pre();
                    long jThreadCpuTimeNanos = Debug.threadCpuTimeNanos();
                    viewOperation.run(objArrPre);
                    jArr[0] = Debug.threadCpuTimeNanos() - jThreadCpuTimeNanos;
                    viewOperation.post(objArrPre);
                } finally {
                    countDownLatch.countDown();
                }
            }
        });
        try {
            if (!countDownLatch.await(4000L, TimeUnit.MILLISECONDS)) {
                Log.w("View", "Could not complete the profiling of the view " + view);
                return -1L;
            }
            return jArr[0];
        } catch (InterruptedException unused) {
            Log.w("View", "Could not complete the profiling of the view " + view);
            Thread.currentThread().interrupt();
            return -1L;
        }
    }

    public static void captureLayers(View view, DataOutputStream dataOutputStream) throws IOException {
        try {
            Rect rect = new Rect();
            try {
                view.mAttachInfo.mSession.getDisplayFrame(view.mAttachInfo.mWindow, rect);
            } catch (RemoteException unused) {
            }
            dataOutputStream.writeInt(rect.width());
            dataOutputStream.writeInt(rect.height());
            captureViewLayer(view, dataOutputStream, true);
            dataOutputStream.write(2);
        } finally {
            dataOutputStream.close();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v2, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r8v3 */
    private static void captureViewLayer(View view, DataOutputStream dataOutputStream, boolean z) throws IOException {
        ?? r8 = (view.getVisibility() == 0 && z) ? 1 : 0;
        if ((view.mPrivateFlags & 128) != 128) {
            int id = view.getId();
            String simpleName = view.getClass().getSimpleName();
            if (id != -1) {
                simpleName = resolveId(view.getContext(), id).toString();
            }
            dataOutputStream.write(1);
            dataOutputStream.writeUTF(simpleName);
            dataOutputStream.writeByte(r8);
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            dataOutputStream.writeInt(iArr[0]);
            dataOutputStream.writeInt(iArr[1]);
            dataOutputStream.flush();
            Bitmap bitmapPerformViewCapture = performViewCapture(view, true);
            if (bitmapPerformViewCapture != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bitmapPerformViewCapture.getWidth() * bitmapPerformViewCapture.getHeight() * 2);
                bitmapPerformViewCapture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                dataOutputStream.writeInt(byteArrayOutputStream.size());
                byteArrayOutputStream.writeTo(dataOutputStream);
            }
            dataOutputStream.flush();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                captureViewLayer(viewGroup.getChildAt(i), dataOutputStream, r8);
            }
        }
        if (view.mOverlay != null) {
            captureViewLayer(view.getOverlay().mOverlayViewGroup, dataOutputStream, r8);
        }
    }

    private static void outputDisplayList(View view, String str) throws IOException {
        View viewFindView = findView(view, str);
        viewFindView.getViewRootImpl().outputDisplayList(viewFindView);
    }

    public static void outputDisplayList(View view, View view2) {
        view.getViewRootImpl().outputDisplayList(view2);
    }

    private static void capture(View view, OutputStream outputStream, String str) throws Throwable {
        capture(view, outputStream, findView(view, str));
    }

    public static void capture(View view, OutputStream outputStream, View view2) throws Throwable {
        BufferedOutputStream bufferedOutputStream;
        Throwable th;
        Bitmap bitmapPerformViewCapture = performViewCapture(view2, false);
        if (bitmapPerformViewCapture == null) {
            Log.w("View", "Failed to create capture bitmap!");
            bitmapPerformViewCapture = Bitmap.createBitmap(view.getResources().getDisplayMetrics(), 1, 1, Bitmap.Config.ARGB_8888);
        }
        try {
            bufferedOutputStream = new BufferedOutputStream(outputStream, 32768);
            try {
                bitmapPerformViewCapture.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                bitmapPerformViewCapture.recycle();
            } catch (Throwable th2) {
                th = th2;
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                bitmapPerformViewCapture.recycle();
                throw th;
            }
        } catch (Throwable th3) {
            bufferedOutputStream = null;
            th = th3;
        }
    }

    private static Bitmap performViewCapture(final View view, final boolean z) {
        if (view == null) {
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Bitmap[] bitmapArr = new Bitmap[1];
        view.post(new Runnable() { // from class: android.view.ViewDebug.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    try {
                        bitmapArr[0] = view.createSnapshot(Bitmap.Config.ARGB_8888, 0, z);
                    } catch (OutOfMemoryError unused) {
                        Log.w("View", "Out of memory for bitmap");
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }
        });
        try {
            countDownLatch.await(4000L, TimeUnit.MILLISECONDS);
            return bitmapArr[0];
        } catch (InterruptedException unused) {
            Log.w("View", "Could not complete the capture of the view " + view);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static void dump(View view, boolean z, boolean z2, OutputStream outputStream) throws Throwable {
        BufferedWriter bufferedWriter;
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"), 32768);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            View rootView = view.getRootView();
            if (rootView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) rootView;
                dumpViewHierarchy(viewGroup.getContext(), viewGroup, bufferedWriter, 0, z, z2);
            }
            bufferedWriter.write("DONE.");
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (Exception e2) {
            e = e2;
            bufferedWriter2 = bufferedWriter;
            Log.w("View", "Problem dumping the view:", e);
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedWriter2 = bufferedWriter;
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static View findView(ViewGroup viewGroup, String str, int i) {
        View viewFindHierarchyView;
        View viewFindView;
        if (isRequestedView(viewGroup, str, i)) {
            return viewGroup;
        }
        int childCount = viewGroup.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = viewGroup.getChildAt(i2);
            if (childAt instanceof ViewGroup) {
                View viewFindView2 = findView((ViewGroup) childAt, str, i);
                if (viewFindView2 != null) {
                    return viewFindView2;
                }
            } else if (isRequestedView(childAt, str, i)) {
                return childAt;
            }
            if (childAt.mOverlay != null && (viewFindView = findView(childAt.mOverlay.mOverlayViewGroup, str, i)) != null) {
                return viewFindView;
            }
            if ((childAt instanceof HierarchyHandler) && (viewFindHierarchyView = ((HierarchyHandler) childAt).findHierarchyView(str, i)) != null) {
                return viewFindHierarchyView;
            }
        }
        return null;
    }

    private static boolean isRequestedView(View view, String str, int i) {
        if (view.hashCode() != i) {
            return false;
        }
        String name = view.getClass().getName();
        if (str.equals("ViewOverlay")) {
            return name.equals("android.view.ViewOverlay$OverlayViewGroup");
        }
        return str.equals(name);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void dumpViewHierarchy(Context context, ViewGroup viewGroup, BufferedWriter bufferedWriter, int i, boolean z, boolean z2) {
        if (dumpView(context, viewGroup, bufferedWriter, i, z2) && !z) {
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = viewGroup.getChildAt(i2);
                if (childAt instanceof ViewGroup) {
                    dumpViewHierarchy(context, (ViewGroup) childAt, bufferedWriter, i + 1, z, z2);
                } else {
                    dumpView(context, childAt, bufferedWriter, i + 1, z2);
                }
                if (childAt.mOverlay != null) {
                    dumpViewHierarchy(context, childAt.getOverlay().mOverlayViewGroup, bufferedWriter, i + 2, z, z2);
                }
            }
            if (viewGroup instanceof HierarchyHandler) {
                ((HierarchyHandler) viewGroup).dumpViewHierarchyWithProperties(bufferedWriter, i + 1);
            }
        }
    }

    private static boolean dumpView(Context context, View view, BufferedWriter bufferedWriter, int i, boolean z) {
        for (int i2 = 0; i2 < i; i2++) {
            try {
                bufferedWriter.write(32);
            } catch (IOException unused) {
                Log.w("View", "Error while dumping hierarchy tree");
                return false;
            }
        }
        String name = view.getClass().getName();
        if (name.equals("android.view.ViewOverlay$OverlayViewGroup")) {
            name = "ViewOverlay";
        }
        bufferedWriter.write(name);
        bufferedWriter.write(64);
        bufferedWriter.write(Integer.toHexString(view.hashCode()));
        bufferedWriter.write(32);
        if (z) {
            dumpViewProperties(context, view, bufferedWriter);
        }
        bufferedWriter.newLine();
        return true;
    }

    private static Field[] getExportedPropertyFields(Class<?> cls) {
        if (sFieldsForClasses == null) {
            sFieldsForClasses = new HashMap<>();
        }
        if (sAnnotations == null) {
            sAnnotations = new HashMap<>(512);
        }
        HashMap<Class<?>, Field[]> map = sFieldsForClasses;
        Field[] fieldArr = map.get(cls);
        if (fieldArr != null) {
            return fieldArr;
        }
        ArrayList arrayList = new ArrayList();
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExportedProperty.class)) {
                field.setAccessible(true);
                arrayList.add(field);
                sAnnotations.put(field, (ExportedProperty) field.getAnnotation(ExportedProperty.class));
            }
        }
        Field[] fieldArr2 = (Field[]) arrayList.toArray(new Field[arrayList.size()]);
        map.put(cls, fieldArr2);
        return fieldArr2;
    }

    private static Method[] getExportedPropertyMethods(Class<?> cls) {
        if (sMethodsForClasses == null) {
            sMethodsForClasses = new HashMap<>(100);
        }
        if (sAnnotations == null) {
            sAnnotations = new HashMap<>(512);
        }
        HashMap<Class<?>, Method[]> map = sMethodsForClasses;
        Method[] methodArr = map.get(cls);
        if (methodArr != null) {
            return methodArr;
        }
        ArrayList arrayList = new ArrayList();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 0 && method.isAnnotationPresent(ExportedProperty.class) && method.getReturnType() != Void.class) {
                method.setAccessible(true);
                arrayList.add(method);
                sAnnotations.put(method, (ExportedProperty) method.getAnnotation(ExportedProperty.class));
            }
        }
        Method[] methodArr2 = (Method[]) arrayList.toArray(new Method[arrayList.size()]);
        map.put(cls, methodArr2);
        return methodArr2;
    }

    private static void dumpViewProperties(Context context, Object obj, BufferedWriter bufferedWriter) throws IOException {
        dumpViewProperties(context, obj, bufferedWriter, "");
    }

    private static void dumpViewProperties(Context context, Object obj, BufferedWriter bufferedWriter, String str) throws IOException {
        if (obj == null) {
            bufferedWriter.write(str + "=4,null ");
            return;
        }
        Class<?> superclass = obj.getClass();
        do {
            exportFields(context, obj, bufferedWriter, superclass, str);
            exportMethods(context, obj, bufferedWriter, superclass, str);
            superclass = superclass.getSuperclass();
        } while (superclass != Object.class);
    }

    private static Object callMethodOnAppropriateTheadBlocking(final Method method, Object obj) throws IllegalAccessException, TimeoutException, InvocationTargetException {
        if (!(obj instanceof View)) {
            return method.invoke(obj, (Object[]) null);
        }
        final View view = (View) obj;
        FutureTask futureTask = new FutureTask(new Callable<Object>() { // from class: android.view.ViewDebug.7
            @Override // java.util.concurrent.Callable
            public Object call() throws IllegalAccessException, InvocationTargetException {
                return method.invoke(view, (Object[]) null);
            }
        });
        Handler handler = view.getHandler();
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(futureTask);
        while (true) {
            try {
                return futureTask.get(4000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException unused) {
            } catch (CancellationException e) {
                throw new RuntimeException("Unexpected cancellation exception", e);
            } catch (ExecutionException e2) {
                Throwable cause = e2.getCause();
                if (cause instanceof IllegalAccessException) {
                    throw ((IllegalAccessException) cause);
                }
                if (cause instanceof InvocationTargetException) {
                    throw ((InvocationTargetException) cause);
                }
                throw new RuntimeException("Unexpected exception", cause);
            }
        }
    }

    private static void exportMethods(Context context, Object obj, BufferedWriter bufferedWriter, Class<?> cls, String str) throws IOException {
        Object objCallMethodOnAppropriateTheadBlocking;
        Class<?> returnType;
        ExportedProperty exportedProperty;
        String str2;
        boolean z;
        for (Method method : getExportedPropertyMethods(cls)) {
            try {
                objCallMethodOnAppropriateTheadBlocking = callMethodOnAppropriateTheadBlocking(method, obj);
                returnType = method.getReturnType();
                exportedProperty = sAnnotations.get(method);
                str2 = exportedProperty.category().length() != 0 ? exportedProperty.category() + ":" : "";
            } catch (IllegalAccessException | InvocationTargetException | TimeoutException unused) {
            }
            if (returnType == Integer.TYPE) {
                if (exportedProperty.resolveId() && context != null) {
                    objCallMethodOnAppropriateTheadBlocking = resolveId(context, ((Integer) objCallMethodOnAppropriateTheadBlocking).intValue());
                } else {
                    FlagToString[] flagToStringArrFlagMapping = exportedProperty.flagMapping();
                    if (flagToStringArrFlagMapping.length > 0) {
                        exportUnrolledFlags(bufferedWriter, flagToStringArrFlagMapping, ((Integer) objCallMethodOnAppropriateTheadBlocking).intValue(), str2 + str + method.getName() + '_');
                    }
                    IntToString[] intToStringArrMapping = exportedProperty.mapping();
                    if (intToStringArrMapping.length > 0) {
                        int iIntValue = ((Integer) objCallMethodOnAppropriateTheadBlocking).intValue();
                        int length = intToStringArrMapping.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                z = false;
                                break;
                            }
                            IntToString intToString = intToStringArrMapping[i];
                            if (intToString.from() == iIntValue) {
                                objCallMethodOnAppropriateTheadBlocking = intToString.to();
                                z = true;
                                break;
                            }
                            i++;
                        }
                        if (!z) {
                            objCallMethodOnAppropriateTheadBlocking = Integer.valueOf(iIntValue);
                        }
                    }
                }
            } else {
                if (returnType == int[].class) {
                    exportUnrolledArray(context, bufferedWriter, exportedProperty, (int[]) objCallMethodOnAppropriateTheadBlocking, str2 + str + method.getName() + '_', "()");
                    return;
                } else if (!returnType.isPrimitive() && exportedProperty.deepExport()) {
                    dumpViewProperties(context, objCallMethodOnAppropriateTheadBlocking, bufferedWriter, str + exportedProperty.prefix());
                }
            }
            writeEntry(bufferedWriter, str2 + str, method.getName(), "()", objCallMethodOnAppropriateTheadBlocking);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x0115 A[Catch: IllegalAccessException -> 0x0131, TryCatch #0 {IllegalAccessException -> 0x0131, blocks: (B:5:0x0012, B:8:0x002a, B:10:0x0043, B:12:0x0049, B:17:0x0052, B:19:0x0085, B:21:0x008b, B:23:0x0091, B:44:0x0115, B:45:0x0119, B:25:0x00b1, B:28:0x00b9, B:29:0x00c2, B:31:0x00c9, B:32:0x00ed, B:34:0x00f4, B:36:0x00fc, B:38:0x0104, B:42:0x010f, B:39:0x0109), top: B:49:0x0012 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void exportFields(android.content.Context r16, java.lang.Object r17, java.io.BufferedWriter r18, java.lang.Class<?> r19, java.lang.String r20) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 310
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.ViewDebug.exportFields(android.content.Context, java.lang.Object, java.io.BufferedWriter, java.lang.Class, java.lang.String):void");
    }

    private static void writeEntry(BufferedWriter bufferedWriter, String str, String str2, String str3, Object obj) throws IOException {
        bufferedWriter.write(str);
        bufferedWriter.write(str2);
        bufferedWriter.write(str3);
        bufferedWriter.write("=");
        writeValue(bufferedWriter, obj);
        bufferedWriter.write(32);
    }

    private static void exportUnrolledFlags(BufferedWriter bufferedWriter, FlagToString[] flagToStringArr, int i, String str) throws IOException {
        for (FlagToString flagToString : flagToStringArr) {
            boolean zOutputIf = flagToString.outputIf();
            int iMask = flagToString.mask() & i;
            boolean z = iMask == flagToString.equals();
            if ((z && zOutputIf) || (!z && !zOutputIf)) {
                writeEntry(bufferedWriter, str, flagToString.name(), "", "0x" + Integer.toHexString(iMask));
            }
        }
    }

    private static void exportUnrolledArray(Context context, BufferedWriter bufferedWriter, ExportedProperty exportedProperty, int[] iArr, String str, String str2) throws IOException {
        IntToString[] intToStringArrIndexMapping = exportedProperty.indexMapping();
        boolean z = intToStringArrIndexMapping.length > 0;
        IntToString[] intToStringArrMapping = exportedProperty.mapping();
        boolean z2 = intToStringArrMapping.length > 0;
        boolean z3 = exportedProperty.resolveId() && context != null;
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            String strValueOf = null;
            int i2 = iArr[i];
            String strValueOf2 = String.valueOf(i);
            if (z) {
                int length2 = intToStringArrIndexMapping.length;
                int i3 = 0;
                while (true) {
                    if (i3 >= length2) {
                        break;
                    }
                    IntToString intToString = intToStringArrIndexMapping[i3];
                    if (intToString.from() == i) {
                        strValueOf2 = intToString.to();
                        break;
                    }
                    i3++;
                }
            }
            if (z2) {
                int length3 = intToStringArrMapping.length;
                int i4 = 0;
                while (true) {
                    if (i4 >= length3) {
                        break;
                    }
                    IntToString intToString2 = intToStringArrMapping[i4];
                    if (intToString2.from() == i2) {
                        strValueOf = intToString2.to();
                        break;
                    }
                    i4++;
                }
            }
            if (!z3) {
                strValueOf = String.valueOf(i2);
            } else if (strValueOf == null) {
                strValueOf = (String) resolveId(context, i2);
            }
            writeEntry(bufferedWriter, str, strValueOf2, str2, strValueOf);
        }
    }

    static Object resolveId(Context context, int i) {
        Resources resources = context.getResources();
        if (i < 0) {
            return "NO_ID";
        }
        try {
            return resources.getResourceTypeName(i) + '/' + resources.getResourceEntryName(i);
        } catch (Resources.NotFoundException unused) {
            return "id/0x" + Integer.toHexString(i);
        }
    }

    private static void writeValue(BufferedWriter bufferedWriter, Object obj) throws IOException {
        if (obj != null) {
            try {
                String strReplace = obj.toString().replace("\n", "\\n");
                bufferedWriter.write(String.valueOf(strReplace.length()));
                bufferedWriter.write(",");
                bufferedWriter.write(strReplace);
                return;
            } catch (Throwable th) {
                bufferedWriter.write(String.valueOf(11));
                bufferedWriter.write(",");
                bufferedWriter.write("[EXCEPTION]");
                throw th;
            }
        }
        bufferedWriter.write("4,null");
    }

    private static Field[] capturedViewGetPropertyFields(Class<?> cls) {
        if (mCapturedViewFieldsForClasses == null) {
            mCapturedViewFieldsForClasses = new HashMap<>();
        }
        HashMap<Class<?>, Field[]> map = mCapturedViewFieldsForClasses;
        Field[] fieldArr = map.get(cls);
        if (fieldArr != null) {
            return fieldArr;
        }
        ArrayList arrayList = new ArrayList();
        for (Field field : cls.getFields()) {
            if (field.isAnnotationPresent(CapturedViewProperty.class)) {
                field.setAccessible(true);
                arrayList.add(field);
            }
        }
        Field[] fieldArr2 = (Field[]) arrayList.toArray(new Field[arrayList.size()]);
        map.put(cls, fieldArr2);
        return fieldArr2;
    }

    private static Method[] capturedViewGetPropertyMethods(Class<?> cls) {
        if (mCapturedViewMethodsForClasses == null) {
            mCapturedViewMethodsForClasses = new HashMap<>();
        }
        HashMap<Class<?>, Method[]> map = mCapturedViewMethodsForClasses;
        Method[] methodArr = map.get(cls);
        if (methodArr != null) {
            return methodArr;
        }
        ArrayList arrayList = new ArrayList();
        for (Method method : cls.getMethods()) {
            if (method.getParameterTypes().length == 0 && method.isAnnotationPresent(CapturedViewProperty.class) && method.getReturnType() != Void.class) {
                method.setAccessible(true);
                arrayList.add(method);
            }
        }
        Method[] methodArr2 = (Method[]) arrayList.toArray(new Method[arrayList.size()]);
        map.put(cls, methodArr2);
        return methodArr2;
    }

    private static String capturedViewExportMethods(Object obj, Class<?> cls, String str) {
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (Method method : capturedViewGetPropertyMethods(cls)) {
            try {
                Object objInvoke = method.invoke(obj, (Object[]) null);
                Class<?> returnType = method.getReturnType();
                if (((CapturedViewProperty) method.getAnnotation(CapturedViewProperty.class)).retrieveReturn()) {
                    sb.append(capturedViewExportMethods(objInvoke, returnType, method.getName() + "#"));
                } else {
                    sb.append(str);
                    sb.append(method.getName());
                    sb.append("()=");
                    if (objInvoke != null) {
                        sb.append(objInvoke.toString().replace("\n", "\\n"));
                    } else {
                        sb.append("null");
                    }
                    sb.append("; ");
                }
            } catch (IllegalAccessException | InvocationTargetException unused) {
            }
        }
        return sb.toString();
    }

    private static String capturedViewExportFields(Object obj, Class<?> cls, String str) {
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (Field field : capturedViewGetPropertyFields(cls)) {
            try {
                Object obj2 = field.get(obj);
                sb.append(str);
                sb.append(field.getName());
                sb.append("=");
                if (obj2 != null) {
                    sb.append(obj2.toString().replace("\n", "\\n"));
                } else {
                    sb.append("null");
                }
                sb.append(' ');
            } catch (IllegalAccessException unused) {
            }
        }
        return sb.toString();
    }

    public static void dumpCapturedView(String str, Object obj) {
        Class<?> cls = obj.getClass();
        Log.d(str, (cls.getName() + ": ") + capturedViewExportFields(obj, cls, "") + capturedViewExportMethods(obj, cls, ""));
    }

    public static Object invokeViewMethod(final View view, final Method method, final Object[] objArr) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference atomicReference = new AtomicReference();
        final AtomicReference atomicReference2 = new AtomicReference();
        view.post(new Runnable() { // from class: android.view.ViewDebug.8
            @Override // java.lang.Runnable
            public void run() {
                try {
                    atomicReference.set(method.invoke(view, objArr));
                } catch (InvocationTargetException e) {
                    atomicReference2.set(e.getCause());
                } catch (Exception e2) {
                    atomicReference2.set(e2);
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
            if (atomicReference2.get() != null) {
                throw new RuntimeException((Throwable) atomicReference2.get());
            }
            return atomicReference.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLayoutParameter(final View view, String str, int i) throws IllegalAccessException, NoSuchFieldException {
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Field field = layoutParams.getClass().getField(str);
        if (field.getType() != Integer.TYPE) {
            throw new RuntimeException("Only integer layout parameters can be set. Field " + str + " is of type " + field.getType().getSimpleName());
        }
        field.set(layoutParams, Integer.valueOf(i));
        view.post(new Runnable() { // from class: android.view.ViewDebug.9
            @Override // java.lang.Runnable
            public void run() {
                view.setLayoutParams(layoutParams);
            }
        });
    }
}

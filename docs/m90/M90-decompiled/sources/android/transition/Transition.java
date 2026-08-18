package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class Transition implements Cloneable {
    static final boolean DBG = false;
    private static final String LOG_TAG = "Transition";
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal<>();
    private String mName = getClass().getName();
    long mStartDelay = -1;
    long mDuration = -1;
    TimeInterpolator mInterpolator = null;
    ArrayList<Integer> mTargetIds = new ArrayList<>();
    ArrayList<View> mTargets = new ArrayList<>();
    ArrayList<Integer> mTargetIdExcludes = null;
    ArrayList<View> mTargetExcludes = null;
    ArrayList<Class> mTargetTypeExcludes = null;
    ArrayList<Integer> mTargetIdChildExcludes = null;
    ArrayList<View> mTargetChildExcludes = null;
    ArrayList<Class> mTargetTypeChildExcludes = null;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    TransitionSet mParent = null;
    ViewGroup mSceneRoot = null;
    boolean mCanRemoveViews = false;
    private ArrayList<Animator> mCurrentAnimators = new ArrayList<>();
    int mNumInstances = 0;
    boolean mPaused = false;
    private boolean mEnded = false;
    ArrayList<TransitionListener> mListeners = null;
    ArrayList<Animator> mAnimators = new ArrayList<>();

    public interface TransitionListener {
        void onTransitionCancel(Transition transition);

        void onTransitionEnd(Transition transition);

        void onTransitionPause(Transition transition);

        void onTransitionResume(Transition transition);

        void onTransitionStart(Transition transition);
    }

    public static class TransitionListenerAdapter implements TransitionListener {
        @Override // android.transition.Transition.TransitionListener
        public void onTransitionCancel(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
        }

        @Override // android.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition) {
        }
    }

    public abstract void captureEndValues(TransitionValues transitionValues);

    public abstract void captureStartValues(TransitionValues transitionValues);

    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        return null;
    }

    public String[] getTransitionProperties() {
        return null;
    }

    public Transition setDuration(long j) {
        this.mDuration = j;
        return this;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public Transition setStartDelay(long j) {
        this.mStartDelay = j;
        return this;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public Transition setInterpolator(TimeInterpolator timeInterpolator) {
        this.mInterpolator = timeInterpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    /* JADX WARN: Removed duplicated region for block: B:110:0x028f  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x02a0 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void createAnimators(android.view.ViewGroup r18, android.transition.TransitionValuesMaps r19, android.transition.TransitionValuesMaps r20) {
        /*
            Method dump skipped, instruction units count: 680
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.transition.Transition.createAnimators(android.view.ViewGroup, android.transition.TransitionValuesMaps, android.transition.TransitionValuesMaps):void");
    }

    boolean isValidTarget(View view, long j) {
        ArrayList<Integer> arrayList = this.mTargetIdExcludes;
        if (arrayList != null && arrayList.contains(Long.valueOf(j))) {
            return false;
        }
        ArrayList<View> arrayList2 = this.mTargetExcludes;
        if (arrayList2 != null && arrayList2.contains(view)) {
            return false;
        }
        ArrayList<Class> arrayList3 = this.mTargetTypeExcludes;
        if (arrayList3 != null && view != null) {
            int size = arrayList3.size();
            for (int i = 0; i < size; i++) {
                if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                    return false;
                }
            }
        }
        if (this.mTargetIds.size() == 0 && this.mTargets.size() == 0) {
            return true;
        }
        if (this.mTargetIds.size() > 0) {
            for (int i2 = 0; i2 < this.mTargetIds.size(); i2++) {
                if (this.mTargetIds.get(i2).intValue() == j) {
                    return true;
                }
            }
        }
        if (view != null && this.mTargets.size() > 0) {
            for (int i3 = 0; i3 < this.mTargets.size(); i3++) {
                if (this.mTargets.get(i3) == view) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap<Animator, AnimationInfo> arrayMap = sRunningAnimators.get();
        if (arrayMap != null) {
            return arrayMap;
        }
        ArrayMap<Animator, AnimationInfo> arrayMap2 = new ArrayMap<>();
        sRunningAnimators.set(arrayMap2);
        return arrayMap2;
    }

    protected void runAnimators() {
        start();
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (Animator animator : this.mAnimators) {
            if (runningAnimators.containsKey(animator)) {
                start();
                runAnimator(animator, runningAnimators);
            }
        }
        this.mAnimators.clear();
        end();
    }

    private void runAnimator(Animator animator, final ArrayMap<Animator, AnimationInfo> arrayMap) {
        if (animator != null) {
            animator.addListener(new AnimatorListenerAdapter() { // from class: android.transition.Transition.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator2) {
                    Transition.this.mCurrentAnimators.add(animator2);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    arrayMap.remove(animator2);
                    Transition.this.mCurrentAnimators.remove(animator2);
                }
            });
            animate(animator);
        }
    }

    public Transition addTarget(int i) {
        if (i > 0) {
            this.mTargetIds.add(Integer.valueOf(i));
        }
        return this;
    }

    public Transition removeTarget(int i) {
        if (i > 0) {
            this.mTargetIds.remove(i);
        }
        return this;
    }

    public Transition excludeTarget(int i, boolean z) {
        this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, i, z);
        return this;
    }

    public Transition excludeChildren(int i, boolean z) {
        this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, i, z);
        return this;
    }

    private ArrayList<Integer> excludeId(ArrayList<Integer> arrayList, int i, boolean z) {
        if (i <= 0) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, Integer.valueOf(i));
        }
        return ArrayListManager.remove(arrayList, Integer.valueOf(i));
    }

    public Transition excludeTarget(View view, boolean z) {
        this.mTargetExcludes = excludeView(this.mTargetExcludes, view, z);
        return this;
    }

    public Transition excludeChildren(View view, boolean z) {
        this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, view, z);
        return this;
    }

    private ArrayList<View> excludeView(ArrayList<View> arrayList, View view, boolean z) {
        if (view == null) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, view);
        }
        return ArrayListManager.remove(arrayList, view);
    }

    public Transition excludeTarget(Class cls, boolean z) {
        this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, cls, z);
        return this;
    }

    public Transition excludeChildren(Class cls, boolean z) {
        this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, cls, z);
        return this;
    }

    private ArrayList<Class> excludeType(ArrayList<Class> arrayList, Class cls, boolean z) {
        if (cls == null) {
            return arrayList;
        }
        if (z) {
            return ArrayListManager.add(arrayList, cls);
        }
        return ArrayListManager.remove(arrayList, cls);
    }

    public Transition addTarget(View view) {
        this.mTargets.add(view);
        return this;
    }

    public Transition removeTarget(View view) {
        if (view != null) {
            this.mTargets.remove(view);
        }
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    public List<View> getTargets() {
        return this.mTargets;
    }

    void captureValues(ViewGroup viewGroup, boolean z) {
        clearValues(z);
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            if (this.mTargetIds.size() > 0) {
                for (int i = 0; i < this.mTargetIds.size(); i++) {
                    int iIntValue = this.mTargetIds.get(i).intValue();
                    View viewFindViewById = viewGroup.findViewById(iIntValue);
                    if (viewFindViewById != null) {
                        TransitionValues transitionValues = new TransitionValues();
                        transitionValues.view = viewFindViewById;
                        if (z) {
                            captureStartValues(transitionValues);
                        } else {
                            captureEndValues(transitionValues);
                        }
                        if (z) {
                            this.mStartValues.viewValues.put(viewFindViewById, transitionValues);
                            if (iIntValue >= 0) {
                                this.mStartValues.idValues.put(iIntValue, transitionValues);
                            }
                        } else {
                            this.mEndValues.viewValues.put(viewFindViewById, transitionValues);
                            if (iIntValue >= 0) {
                                this.mEndValues.idValues.put(iIntValue, transitionValues);
                            }
                        }
                    }
                }
            }
            if (this.mTargets.size() > 0) {
                for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                    View view = this.mTargets.get(i2);
                    if (view != null) {
                        TransitionValues transitionValues2 = new TransitionValues();
                        transitionValues2.view = view;
                        if (z) {
                            captureStartValues(transitionValues2);
                        } else {
                            captureEndValues(transitionValues2);
                        }
                        if (z) {
                            this.mStartValues.viewValues.put(view, transitionValues2);
                        } else {
                            this.mEndValues.viewValues.put(view, transitionValues2);
                        }
                    }
                }
                return;
            }
            return;
        }
        captureHierarchy(viewGroup, z);
    }

    void clearValues(boolean z) {
        if (z) {
            this.mStartValues.viewValues.clear();
            this.mStartValues.idValues.clear();
            this.mStartValues.itemIdValues.clear();
        } else {
            this.mEndValues.viewValues.clear();
            this.mEndValues.idValues.clear();
            this.mEndValues.itemIdValues.clear();
        }
    }

    private void captureHierarchy(View view, boolean z) {
        if (view == null) {
            return;
        }
        boolean z2 = view.getParent() instanceof ListView;
        if (!z2 || ((ListView) view.getParent()).getAdapter().hasStableIds()) {
            int id = -1;
            long itemIdAtPosition = -1;
            if (!z2) {
                id = view.getId();
            } else {
                ListView listView = (ListView) view.getParent();
                itemIdAtPosition = listView.getItemIdAtPosition(listView.getPositionForView(view));
                view.setHasTransientState(true);
            }
            ArrayList<Integer> arrayList = this.mTargetIdExcludes;
            if (arrayList == null || !arrayList.contains(Integer.valueOf(id))) {
                ArrayList<View> arrayList2 = this.mTargetExcludes;
                if (arrayList2 == null || !arrayList2.contains(view)) {
                    ArrayList<Class> arrayList3 = this.mTargetTypeExcludes;
                    if (arrayList3 != null && view != null) {
                        int size = arrayList3.size();
                        for (int i = 0; i < size; i++) {
                            if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                                return;
                            }
                        }
                    }
                    TransitionValues transitionValues = new TransitionValues();
                    transitionValues.view = view;
                    if (z) {
                        captureStartValues(transitionValues);
                    } else {
                        captureEndValues(transitionValues);
                    }
                    if (z) {
                        if (!z2) {
                            this.mStartValues.viewValues.put(view, transitionValues);
                            if (id >= 0) {
                                this.mStartValues.idValues.put(id, transitionValues);
                            }
                        } else {
                            this.mStartValues.itemIdValues.put(itemIdAtPosition, transitionValues);
                        }
                    } else if (!z2) {
                        this.mEndValues.viewValues.put(view, transitionValues);
                        if (id >= 0) {
                            this.mEndValues.idValues.put(id, transitionValues);
                        }
                    } else {
                        this.mEndValues.itemIdValues.put(itemIdAtPosition, transitionValues);
                    }
                    if (view instanceof ViewGroup) {
                        ArrayList<Integer> arrayList4 = this.mTargetIdChildExcludes;
                        if (arrayList4 == null || !arrayList4.contains(Integer.valueOf(id))) {
                            ArrayList<View> arrayList5 = this.mTargetChildExcludes;
                            if (arrayList5 == null || !arrayList5.contains(view)) {
                                ArrayList<Class> arrayList6 = this.mTargetTypeChildExcludes;
                                if (arrayList6 != null && view != null) {
                                    int size2 = arrayList6.size();
                                    for (int i2 = 0; i2 < size2; i2++) {
                                        if (this.mTargetTypeChildExcludes.get(i2).isInstance(view)) {
                                            return;
                                        }
                                    }
                                }
                                ViewGroup viewGroup = (ViewGroup) view;
                                for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                                    captureHierarchy(viewGroup.getChildAt(i3), z);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public TransitionValues getTransitionValues(View view, boolean z) {
        TransitionSet transitionSet = this.mParent;
        if (transitionSet != null) {
            return transitionSet.getTransitionValues(view, z);
        }
        TransitionValuesMaps transitionValuesMaps = z ? this.mStartValues : this.mEndValues;
        TransitionValues transitionValues = transitionValuesMaps.viewValues.get(view);
        if (transitionValues != null) {
            return transitionValues;
        }
        int id = view.getId();
        if (id >= 0) {
            transitionValues = transitionValuesMaps.idValues.get(id);
        }
        if (transitionValues != null || !(view.getParent() instanceof ListView)) {
            return transitionValues;
        }
        ListView listView = (ListView) view.getParent();
        return transitionValuesMaps.itemIdValues.get(listView.getItemIdAtPosition(listView.getPositionForView(view)));
    }

    public void pause() {
        if (this.mEnded) {
            return;
        }
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (int size = runningAnimators.size() - 1; size >= 0; size--) {
            runningAnimators.keyAt(size).pause();
        }
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList != null && arrayList.size() > 0) {
            ArrayList arrayList2 = (ArrayList) this.mListeners.clone();
            int size2 = arrayList2.size();
            for (int i = 0; i < size2; i++) {
                ((TransitionListener) arrayList2.get(i)).onTransitionPause(this);
            }
        }
        this.mPaused = true;
    }

    public void resume() {
        if (this.mPaused) {
            if (!this.mEnded) {
                ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
                for (int size = runningAnimators.size() - 1; size >= 0; size--) {
                    runningAnimators.keyAt(size).resume();
                }
                ArrayList<TransitionListener> arrayList = this.mListeners;
                if (arrayList != null && arrayList.size() > 0) {
                    ArrayList arrayList2 = (ArrayList) this.mListeners.clone();
                    int size2 = arrayList2.size();
                    for (int i = 0; i < size2; i++) {
                        ((TransitionListener) arrayList2.get(i)).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }

    void playTransition(ViewGroup viewGroup) {
        AnimationInfo animationInfo;
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        for (int size = runningAnimators.size() - 1; size >= 0; size--) {
            Animator animatorKeyAt = runningAnimators.keyAt(size);
            if (animatorKeyAt != null && (animationInfo = runningAnimators.get(animatorKeyAt)) != null && animationInfo.view != null && animationInfo.view.getContext() == viewGroup.getContext()) {
                boolean z = false;
                TransitionValues transitionValues = animationInfo.values;
                View view = animationInfo.view;
                TransitionValues transitionValues2 = this.mEndValues.viewValues != null ? this.mEndValues.viewValues.get(view) : null;
                if (transitionValues2 == null) {
                    transitionValues2 = this.mEndValues.idValues.get(view.getId());
                }
                if (transitionValues != null && transitionValues2 != null) {
                    Iterator<String> it = transitionValues.values.keySet().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        String next = it.next();
                        Object obj = transitionValues.values.get(next);
                        Object obj2 = transitionValues2.values.get(next);
                        if (obj != null && obj2 != null && !obj.equals(obj2)) {
                            z = true;
                            break;
                        }
                    }
                }
                if (z) {
                    if (animatorKeyAt.isRunning() || animatorKeyAt.isStarted()) {
                        animatorKeyAt.cancel();
                    } else {
                        runningAnimators.remove(animatorKeyAt);
                    }
                }
            }
        }
        createAnimators(viewGroup, this.mStartValues, this.mEndValues);
        runAnimators();
    }

    protected void animate(Animator animator) {
        if (animator == null) {
            end();
            return;
        }
        if (getDuration() >= 0) {
            animator.setDuration(getDuration());
        }
        if (getStartDelay() >= 0) {
            animator.setStartDelay(getStartDelay());
        }
        if (getInterpolator() != null) {
            animator.setInterpolator(getInterpolator());
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: android.transition.Transition.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                Transition.this.end();
                animator2.removeListener(this);
            }
        });
        animator.start();
    }

    protected void start() {
        if (this.mNumInstances == 0) {
            ArrayList<TransitionListener> arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                ArrayList arrayList2 = (ArrayList) this.mListeners.clone();
                int size = arrayList2.size();
                for (int i = 0; i < size; i++) {
                    ((TransitionListener) arrayList2.get(i)).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        this.mNumInstances++;
    }

    protected void end() {
        int i = this.mNumInstances - 1;
        this.mNumInstances = i;
        if (i == 0) {
            ArrayList<TransitionListener> arrayList = this.mListeners;
            if (arrayList != null && arrayList.size() > 0) {
                ArrayList arrayList2 = (ArrayList) this.mListeners.clone();
                int size = arrayList2.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ((TransitionListener) arrayList2.get(i2)).onTransitionEnd(this);
                }
            }
            for (int i3 = 0; i3 < this.mStartValues.itemIdValues.size(); i3++) {
                View view = this.mStartValues.itemIdValues.valueAt(i3).view;
                if (view.hasTransientState()) {
                    view.setHasTransientState(false);
                }
            }
            for (int i4 = 0; i4 < this.mEndValues.itemIdValues.size(); i4++) {
                View view2 = this.mEndValues.itemIdValues.valueAt(i4).view;
                if (view2.hasTransientState()) {
                    view2.setHasTransientState(false);
                }
            }
            this.mEnded = true;
        }
    }

    protected void cancel() {
        for (int size = this.mCurrentAnimators.size() - 1; size >= 0; size--) {
            this.mCurrentAnimators.get(size).cancel();
        }
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        ArrayList arrayList2 = (ArrayList) this.mListeners.clone();
        int size2 = arrayList2.size();
        for (int i = 0; i < size2; i++) {
            ((TransitionListener) arrayList2.get(i)).onTransitionCancel(this);
        }
    }

    public Transition addListener(TransitionListener transitionListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(transitionListener);
        return this;
    }

    public Transition removeListener(TransitionListener transitionListener) {
        ArrayList<TransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return this;
        }
        arrayList.remove(transitionListener);
        if (this.mListeners.size() == 0) {
            this.mListeners = null;
        }
        return this;
    }

    Transition setSceneRoot(ViewGroup viewGroup) {
        this.mSceneRoot = viewGroup;
        return this;
    }

    void setCanRemoveViews(boolean z) {
        this.mCanRemoveViews = z;
    }

    public String toString() {
        return toString("");
    }

    @Override // 
    /* JADX INFO: renamed from: clone */
    public Transition mo12clone() {
        Transition transition;
        Transition transition2 = null;
        try {
            transition = (Transition) super.clone();
        } catch (CloneNotSupportedException unused) {
        }
        try {
            transition.mAnimators = new ArrayList<>();
            transition.mStartValues = new TransitionValuesMaps();
            transition.mEndValues = new TransitionValuesMaps();
            return transition;
        } catch (CloneNotSupportedException unused2) {
            transition2 = transition;
            return transition2;
        }
    }

    public String getName() {
        return this.mName;
    }

    String toString(String str) {
        String str2 = str + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
        if (this.mDuration != -1) {
            str2 = str2 + "dur(" + this.mDuration + ") ";
        }
        if (this.mStartDelay != -1) {
            str2 = str2 + "dly(" + this.mStartDelay + ") ";
        }
        if (this.mInterpolator != null) {
            str2 = str2 + "interp(" + this.mInterpolator + ") ";
        }
        if (this.mTargetIds.size() <= 0 && this.mTargets.size() <= 0) {
            return str2;
        }
        String str3 = str2 + "tgts(";
        if (this.mTargetIds.size() > 0) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                if (i > 0) {
                    str3 = str3 + ", ";
                }
                str3 = str3 + this.mTargetIds.get(i);
            }
        }
        if (this.mTargets.size() > 0) {
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                if (i2 > 0) {
                    str3 = str3 + ", ";
                }
                str3 = str3 + this.mTargets.get(i2);
            }
        }
        return str3 + ")";
    }

    private static class AnimationInfo {
        String name;
        TransitionValues values;
        View view;

        AnimationInfo(View view, String str, TransitionValues transitionValues) {
            this.view = view;
            this.name = str;
            this.values = transitionValues;
        }
    }

    private static class ArrayListManager {
        private ArrayListManager() {
        }

        static <T> ArrayList<T> add(ArrayList<T> arrayList, T t) {
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            if (!arrayList.contains(t)) {
                arrayList.add(t);
            }
            return arrayList;
        }

        static <T> ArrayList<T> remove(ArrayList<T> arrayList, T t) {
            if (arrayList == null) {
                return arrayList;
            }
            arrayList.remove(t);
            if (arrayList.isEmpty()) {
                return null;
            }
            return arrayList;
        }
    }
}

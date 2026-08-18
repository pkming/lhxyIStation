package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class Atlas {
    public static final int FLAG_ADD_PADDING = 2;
    public static final int FLAG_ALLOW_ROTATIONS = 1;
    public static final int FLAG_DEFAULTS = 2;
    private final Policy mPolicy;

    public static class Entry {
        public boolean rotated;
        public int x;
        public int y;
    }

    public enum Type {
        SliceMinArea,
        SliceMaxArea,
        SliceShortAxis,
        SliceLongAxis
    }

    public Atlas(Type type, int i, int i2) {
        this(type, i, i2, 2);
    }

    public Atlas(Type type, int i, int i2, int i3) {
        this.mPolicy = findPolicy(type, i, i2, i3);
    }

    public Entry pack(int i, int i2) {
        return pack(i, i2, null);
    }

    public Entry pack(int i, int i2, Entry entry) {
        if (entry == null) {
            entry = new Entry();
        }
        return this.mPolicy.pack(i, i2, entry);
    }

    /* JADX INFO: renamed from: android.graphics.Atlas$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Atlas$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$android$graphics$Atlas$Type = iArr;
            try {
                iArr[Type.SliceMinArea.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceMaxArea.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceShortAxis.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceLongAxis.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private static Policy findPolicy(Type type, int i, int i2, int i3) {
        int i4 = AnonymousClass1.$SwitchMap$android$graphics$Atlas$Type[type.ordinal()];
        AnonymousClass1 anonymousClass1 = null;
        if (i4 == 1) {
            return new SlicePolicy(i, i2, i3, new SlicePolicy.MinAreaSplitDecision(anonymousClass1));
        }
        if (i4 == 2) {
            return new SlicePolicy(i, i2, i3, new SlicePolicy.MaxAreaSplitDecision(anonymousClass1));
        }
        if (i4 == 3) {
            return new SlicePolicy(i, i2, i3, new SlicePolicy.ShorterFreeAxisSplitDecision(anonymousClass1));
        }
        if (i4 != 4) {
            return null;
        }
        return new SlicePolicy(i, i2, i3, new SlicePolicy.LongerFreeAxisSplitDecision(anonymousClass1));
    }

    private static abstract class Policy {
        abstract Entry pack(int i, int i2, Entry entry);

        private Policy() {
        }

        /* synthetic */ Policy(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private static class SlicePolicy extends Policy {
        private final boolean mAllowRotation;
        private final int mPadding;
        private final Cell mRoot;
        private final SplitDecision mSplitDecision;

        private interface SplitDecision {
            boolean splitHorizontal(int i, int i2, int i3, int i4);
        }

        private static class Cell {
            int height;
            Cell next;
            int width;
            int x;
            int y;

            private Cell() {
            }

            /* synthetic */ Cell(AnonymousClass1 anonymousClass1) {
                this();
            }

            public String toString() {
                return String.format("cell[x=%d y=%d width=%d height=%d", Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.width), Integer.valueOf(this.height));
            }
        }

        /* JADX WARN: Illegal instructions before constructor call */
        SlicePolicy(int i, int i2, int i3, SplitDecision splitDecision) {
            AnonymousClass1 anonymousClass1 = null;
            super(anonymousClass1);
            Cell cell = new Cell(anonymousClass1);
            this.mRoot = cell;
            this.mAllowRotation = (i3 & 1) != 0;
            int i4 = (i3 & 2) != 0 ? 1 : 0;
            this.mPadding = i4;
            Cell cell2 = new Cell(anonymousClass1);
            cell2.y = i4;
            cell2.x = i4;
            cell2.width = i - (i4 * 2);
            cell2.height = i2 - (i4 * 2);
            cell.next = cell2;
            this.mSplitDecision = splitDecision;
        }

        @Override // android.graphics.Atlas.Policy
        Entry pack(int i, int i2, Entry entry) {
            Cell cell = this.mRoot;
            for (Cell cell2 = this.mRoot.next; cell2 != null; cell2 = cell2.next) {
                if (insert(cell2, cell, i, i2, entry)) {
                    return entry;
                }
                cell = cell2;
            }
            return null;
        }

        private static class MinAreaSplitDecision implements SplitDecision {
            @Override // android.graphics.Atlas.SlicePolicy.SplitDecision
            public boolean splitHorizontal(int i, int i2, int i3, int i4) {
                return i3 * i2 > i * i4;
            }

            private MinAreaSplitDecision() {
            }

            /* synthetic */ MinAreaSplitDecision(AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        private static class MaxAreaSplitDecision implements SplitDecision {
            @Override // android.graphics.Atlas.SlicePolicy.SplitDecision
            public boolean splitHorizontal(int i, int i2, int i3, int i4) {
                return i3 * i2 <= i * i4;
            }

            private MaxAreaSplitDecision() {
            }

            /* synthetic */ MaxAreaSplitDecision(AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        private static class ShorterFreeAxisSplitDecision implements SplitDecision {
            @Override // android.graphics.Atlas.SlicePolicy.SplitDecision
            public boolean splitHorizontal(int i, int i2, int i3, int i4) {
                return i <= i2;
            }

            private ShorterFreeAxisSplitDecision() {
            }

            /* synthetic */ ShorterFreeAxisSplitDecision(AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        private static class LongerFreeAxisSplitDecision implements SplitDecision {
            @Override // android.graphics.Atlas.SlicePolicy.SplitDecision
            public boolean splitHorizontal(int i, int i2, int i3, int i4) {
                return i > i2;
            }

            private LongerFreeAxisSplitDecision() {
            }

            /* synthetic */ LongerFreeAxisSplitDecision(AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        private boolean insert(Cell cell, Cell cell2, int i, int i2, Entry entry) {
            boolean z = false;
            if (cell.width < i || cell.height < i2) {
                if (!this.mAllowRotation || cell.width < i2 || cell.height < i) {
                    return false;
                }
                z = true;
                i2 = i;
                i = i2;
            }
            int i3 = cell.width - i;
            int i4 = cell.height - i2;
            AnonymousClass1 anonymousClass1 = null;
            Cell cell3 = new Cell(anonymousClass1);
            Cell cell4 = new Cell(anonymousClass1);
            cell3.x = cell.x + i + this.mPadding;
            cell3.y = cell.y;
            cell3.width = i3 - this.mPadding;
            cell4.x = cell.x;
            cell4.y = cell.y + i2 + this.mPadding;
            cell4.height = i4 - this.mPadding;
            if (this.mSplitDecision.splitHorizontal(i3, i4, i, i2)) {
                cell3.height = i2;
                cell4.width = cell.width;
            } else {
                cell3.height = cell.height;
                cell4.width = i;
                cell4 = cell3;
                cell3 = cell4;
            }
            if (cell3.width > 0 && cell3.height > 0) {
                cell2.next = cell3;
                cell2 = cell3;
            }
            if (cell4.width > 0 && cell4.height > 0) {
                cell2.next = cell4;
                cell4.next = cell.next;
            } else {
                cell2.next = cell.next;
            }
            cell.next = null;
            entry.x = cell.x;
            entry.y = cell.y;
            entry.rotated = z;
            return true;
        }
    }
}

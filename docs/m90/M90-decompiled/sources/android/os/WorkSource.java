package android.os;

import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class WorkSource implements Parcelable {
    static final boolean DEBUG = false;
    static final String TAG = "WorkSource";
    static WorkSource sGoneWork;
    static WorkSource sNewbWork;
    String[] mNames;
    int mNum;
    int[] mUids;
    static final WorkSource sTmpWorkSource = new WorkSource(0);
    public static final Parcelable.Creator<WorkSource> CREATOR = new Parcelable.Creator<WorkSource>() { // from class: android.os.WorkSource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WorkSource createFromParcel(Parcel parcel) {
            return new WorkSource(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WorkSource[] newArray(int i) {
            return new WorkSource[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WorkSource() {
        this.mNum = 0;
    }

    public WorkSource(WorkSource workSource) {
        if (workSource == null) {
            this.mNum = 0;
            return;
        }
        this.mNum = workSource.mNum;
        int[] iArr = workSource.mUids;
        if (iArr != null) {
            this.mUids = (int[]) iArr.clone();
            String[] strArr = workSource.mNames;
            this.mNames = strArr != null ? (String[]) strArr.clone() : null;
        } else {
            this.mUids = null;
            this.mNames = null;
        }
    }

    public WorkSource(int i) {
        this.mNum = 1;
        this.mUids = new int[]{i, 0};
        this.mNames = null;
    }

    public WorkSource(int i, String str) {
        Objects.requireNonNull(str, "Name can't be null");
        this.mNum = 1;
        this.mUids = new int[]{i, 0};
        this.mNames = new String[]{str, null};
    }

    WorkSource(Parcel parcel) {
        this.mNum = parcel.readInt();
        this.mUids = parcel.createIntArray();
        this.mNames = parcel.createStringArray();
    }

    public int size() {
        return this.mNum;
    }

    public int get(int i) {
        return this.mUids[i];
    }

    public String getName(int i) {
        String[] strArr = this.mNames;
        if (strArr != null) {
            return strArr[i];
        }
        return null;
    }

    public void clearNames() {
        if (this.mNames != null) {
            this.mNames = null;
            int i = this.mNum;
            int i2 = 1;
            for (int i3 = 1; i3 < this.mNum; i3++) {
                int[] iArr = this.mUids;
                if (iArr[i3] == iArr[i3 - 1]) {
                    i--;
                } else {
                    iArr[i2] = iArr[i3];
                    i2++;
                }
            }
            this.mNum = i;
        }
    }

    public void clear() {
        this.mNum = 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof WorkSource) && !diff((WorkSource) obj);
    }

    public int hashCode() {
        int iHashCode = 0;
        for (int i = 0; i < this.mNum; i++) {
            iHashCode = ((iHashCode >>> 28) | (iHashCode << 4)) ^ this.mUids[i];
        }
        if (this.mNames != null) {
            for (int i2 = 0; i2 < this.mNum; i2++) {
                iHashCode = this.mNames[i2].hashCode() ^ ((iHashCode << 4) | (iHashCode >>> 28));
            }
        }
        return iHashCode;
    }

    public boolean diff(WorkSource workSource) {
        int i = this.mNum;
        if (i != workSource.mNum) {
            return true;
        }
        int[] iArr = this.mUids;
        int[] iArr2 = workSource.mUids;
        String[] strArr = this.mNames;
        String[] strArr2 = workSource.mNames;
        for (int i2 = 0; i2 < i; i2++) {
            if (iArr[i2] != iArr2[i2]) {
                return true;
            }
            if (strArr != null && strArr2 != null && !strArr[i2].equals(strArr2[i2])) {
                return true;
            }
        }
        return false;
    }

    public void set(WorkSource workSource) {
        if (workSource == null) {
            this.mNum = 0;
            return;
        }
        int i = workSource.mNum;
        this.mNum = i;
        int[] iArr = workSource.mUids;
        if (iArr != null) {
            int[] iArr2 = this.mUids;
            if (iArr2 != null && iArr2.length >= i) {
                System.arraycopy(iArr, 0, iArr2, 0, i);
            } else {
                this.mUids = (int[]) iArr.clone();
            }
            String[] strArr = workSource.mNames;
            if (strArr != null) {
                String[] strArr2 = this.mNames;
                if (strArr2 != null) {
                    int length = strArr2.length;
                    int i2 = this.mNum;
                    if (length >= i2) {
                        System.arraycopy(strArr, 0, strArr2, 0, i2);
                        return;
                    }
                }
                this.mNames = (String[]) strArr.clone();
                return;
            }
            this.mNames = null;
            return;
        }
        this.mUids = null;
        this.mNames = null;
    }

    public void set(int i) {
        this.mNum = 1;
        if (this.mUids == null) {
            this.mUids = new int[2];
        }
        this.mUids[0] = i;
        this.mNames = null;
    }

    public void set(int i, String str) {
        Objects.requireNonNull(str, "Name can't be null");
        this.mNum = 1;
        if (this.mUids == null) {
            this.mUids = new int[2];
            this.mNames = new String[2];
        }
        this.mUids[0] = i;
        this.mNames[0] = str;
    }

    public WorkSource[] setReturningDiffs(WorkSource workSource) {
        synchronized (sTmpWorkSource) {
            sNewbWork = null;
            sGoneWork = null;
            updateLocked(workSource, true, true);
            WorkSource workSource2 = sNewbWork;
            if (workSource2 == null && sGoneWork == null) {
                return null;
            }
            return new WorkSource[]{workSource2, sGoneWork};
        }
    }

    public boolean add(WorkSource workSource) {
        boolean zUpdateLocked;
        synchronized (sTmpWorkSource) {
            zUpdateLocked = updateLocked(workSource, false, false);
        }
        return zUpdateLocked;
    }

    public WorkSource addReturningNewbs(WorkSource workSource) {
        WorkSource workSource2;
        synchronized (sTmpWorkSource) {
            sNewbWork = null;
            updateLocked(workSource, false, true);
            workSource2 = sNewbWork;
        }
        return workSource2;
    }

    public boolean add(int i) {
        int i2 = this.mNum;
        if (i2 <= 0) {
            this.mNames = null;
            insert(0, i);
            return true;
        }
        if (this.mNames != null) {
            throw new IllegalArgumentException("Adding without name to named " + this);
        }
        int iBinarySearch = Arrays.binarySearch(this.mUids, 0, i2, i);
        if (iBinarySearch >= 0) {
            return false;
        }
        insert((-iBinarySearch) - 1, i);
        return true;
    }

    public boolean add(int i, String str) {
        if (this.mNum <= 0) {
            insert(0, i, str);
            return true;
        }
        if (this.mNames == null) {
            throw new IllegalArgumentException("Adding name to unnamed " + this);
        }
        int i2 = 0;
        while (i2 < this.mNum) {
            int[] iArr = this.mUids;
            if (iArr[i2] > i) {
                break;
            }
            if (iArr[i2] == i) {
                int iCompareTo = this.mNames[i2].compareTo(str);
                if (iCompareTo > 0) {
                    break;
                }
                if (iCompareTo == 0) {
                    return false;
                }
            }
            i2++;
        }
        insert(i2, i, str);
        return true;
    }

    public WorkSource addReturningNewbs(int i) {
        WorkSource workSource;
        WorkSource workSource2 = sTmpWorkSource;
        synchronized (workSource2) {
            sNewbWork = null;
            workSource2.mUids[0] = i;
            updateLocked(workSource2, false, true);
            workSource = sNewbWork;
        }
        return workSource;
    }

    public boolean remove(WorkSource workSource) {
        if (this.mNum <= 0 || workSource.mNum <= 0) {
            return false;
        }
        String[] strArr = this.mNames;
        if (strArr == null && workSource.mNames == null) {
            return removeUids(workSource);
        }
        if (strArr == null) {
            throw new IllegalArgumentException("Other " + workSource + " has names, but target " + this + " does not");
        }
        if (workSource.mNames == null) {
            throw new IllegalArgumentException("Target " + this + " has names, but other " + workSource + " does not");
        }
        return removeUidsAndNames(workSource);
    }

    public WorkSource stripNames() {
        if (this.mNum <= 0) {
            return new WorkSource();
        }
        WorkSource workSource = new WorkSource();
        for (int i = 0; i < this.mNum; i++) {
            int i2 = this.mUids[i];
            if (i == 0 || -1 != i2) {
                workSource.add(i2);
            }
        }
        return workSource;
    }

    private boolean removeUids(WorkSource workSource) {
        int i = this.mNum;
        int[] iArr = this.mUids;
        int i2 = workSource.mNum;
        int[] iArr2 = workSource.mUids;
        int i3 = 0;
        int i4 = 0;
        boolean z = false;
        while (i3 < i && i4 < i2) {
            if (iArr2[i4] == iArr[i3]) {
                i--;
                if (i3 < i) {
                    System.arraycopy(iArr, i3 + 1, iArr, i3, i - i3);
                }
                i4++;
                z = true;
            } else if (iArr2[i4] > iArr[i3]) {
                i3++;
            } else {
                i4++;
            }
        }
        this.mNum = i;
        return z;
    }

    private boolean removeUidsAndNames(WorkSource workSource) {
        int i = this.mNum;
        int[] iArr = this.mUids;
        String[] strArr = this.mNames;
        int i2 = workSource.mNum;
        int[] iArr2 = workSource.mUids;
        String[] strArr2 = workSource.mNames;
        int i3 = 0;
        int i4 = 0;
        boolean z = false;
        while (i3 < i && i4 < i2) {
            if (iArr2[i4] == iArr[i3] && strArr2[i4].equals(strArr[i3])) {
                i--;
                if (i3 < i) {
                    int i5 = i3 + 1;
                    int i6 = i - i3;
                    System.arraycopy(iArr, i5, iArr, i3, i6);
                    System.arraycopy(strArr, i5, strArr, i3, i6);
                }
                i4++;
                z = true;
            } else if (iArr2[i4] > iArr[i3] || (iArr2[i4] == iArr[i3] && strArr2[i4].compareTo(strArr[i3]) > 0)) {
                i3++;
            } else {
                i4++;
            }
        }
        this.mNum = i;
        return z;
    }

    private boolean updateLocked(WorkSource workSource, boolean z, boolean z2) {
        String[] strArr = this.mNames;
        if (strArr == null && workSource.mNames == null) {
            return updateUidsLocked(workSource, z, z2);
        }
        if (this.mNum > 0 && strArr == null) {
            throw new IllegalArgumentException("Other " + workSource + " has names, but target " + this + " does not");
        }
        if (workSource.mNum > 0 && workSource.mNames == null) {
            throw new IllegalArgumentException("Target " + this + " has names, but other " + workSource + " does not");
        }
        return updateUidsAndNamesLocked(workSource, z, z2);
    }

    private static WorkSource addWork(WorkSource workSource, int i) {
        if (workSource == null) {
            return new WorkSource(i);
        }
        workSource.insert(workSource.mNum, i);
        return workSource;
    }

    private boolean updateUidsLocked(WorkSource workSource, boolean z, boolean z2) {
        int i = this.mNum;
        int[] iArr = this.mUids;
        int i2 = workSource.mNum;
        int[] iArr2 = workSource.mUids;
        int i3 = 0;
        int i4 = 0;
        boolean z3 = false;
        while (true) {
            if (i3 < i || i4 < i2) {
                if (i3 >= i || (i4 < i2 && iArr2[i4] < iArr[i3])) {
                    if (iArr == null) {
                        iArr = new int[4];
                        iArr[0] = iArr2[i4];
                    } else if (i >= iArr.length) {
                        int[] iArr3 = new int[(iArr.length * 3) / 2];
                        if (i3 > 0) {
                            System.arraycopy(iArr, 0, iArr3, 0, i3);
                        }
                        if (i3 < i) {
                            System.arraycopy(iArr, i3, iArr3, i3 + 1, i - i3);
                        }
                        iArr3[i3] = iArr2[i4];
                        iArr = iArr3;
                    } else {
                        if (i3 < i) {
                            System.arraycopy(iArr, i3, iArr, i3 + 1, i - i3);
                        }
                        iArr[i3] = iArr2[i4];
                    }
                    if (z2) {
                        sNewbWork = addWork(sNewbWork, iArr2[i4]);
                    }
                    i++;
                    i3++;
                    i4++;
                    z3 = true;
                } else if (!z) {
                    if (i4 < i2 && iArr2[i4] == iArr[i3]) {
                        i4++;
                    }
                    i3++;
                } else {
                    int i5 = i3;
                    while (i5 < i && (i4 >= i2 || iArr2[i4] > iArr[i5])) {
                        sGoneWork = addWork(sGoneWork, iArr[i5]);
                        i5++;
                    }
                    if (i3 < i5) {
                        System.arraycopy(iArr, i5, iArr, i3, i - i5);
                        i -= i5 - i3;
                    } else {
                        i3 = i5;
                    }
                    if (i3 < i && i4 < i2 && iArr2[i4] == iArr[i3]) {
                        i3++;
                        i4++;
                    }
                }
            } else {
                this.mNum = i;
                this.mUids = iArr;
                return z3;
            }
        }
    }

    private int compare(WorkSource workSource, int i, int i2) {
        int i3 = this.mUids[i] - workSource.mUids[i2];
        return i3 != 0 ? i3 : this.mNames[i].compareTo(workSource.mNames[i2]);
    }

    private static WorkSource addWork(WorkSource workSource, int i, String str) {
        if (workSource == null) {
            return new WorkSource(i, str);
        }
        workSource.insert(workSource.mNum, i, str);
        return workSource;
    }

    private boolean updateUidsAndNamesLocked(WorkSource workSource, boolean z, boolean z2) {
        int iCompare;
        int i = workSource.mNum;
        int[] iArr = workSource.mUids;
        String[] strArr = workSource.mNames;
        int i2 = 0;
        int i3 = 0;
        boolean z3 = false;
        while (true) {
            int i4 = this.mNum;
            if (i2 >= i4 && i3 >= i) {
                return z3;
            }
            if (i2 < i4) {
                if (i3 < i) {
                    iCompare = compare(workSource, i2, i3);
                    if (iCompare > 0) {
                    }
                } else {
                    iCompare = -1;
                }
                if (z) {
                    int i5 = i2;
                    while (iCompare < 0) {
                        sGoneWork = addWork(sGoneWork, this.mUids[i5], this.mNames[i5]);
                        i5++;
                        if (i5 >= this.mNum) {
                            break;
                        }
                        iCompare = i3 < i ? compare(workSource, i5, i3) : -1;
                    }
                    if (i2 < i5) {
                        int[] iArr2 = this.mUids;
                        System.arraycopy(iArr2, i5, iArr2, i2, this.mNum - i5);
                        String[] strArr2 = this.mNames;
                        System.arraycopy(strArr2, i5, strArr2, i2, this.mNum - i5);
                        this.mNum -= i5 - i2;
                    } else {
                        i2 = i5;
                    }
                    if (i2 < this.mNum && iCompare == 0) {
                        i2++;
                        i3++;
                    }
                } else {
                    if (i3 < i && iCompare == 0) {
                        i3++;
                    }
                    i2++;
                }
            }
            insert(i2, iArr[i3], strArr[i3]);
            if (z2) {
                sNewbWork = addWork(sNewbWork, iArr[i3], strArr[i3]);
            }
            i2++;
            i3++;
            z3 = true;
        }
    }

    private void insert(int i, int i2) {
        int[] iArr = this.mUids;
        if (iArr == null) {
            int[] iArr2 = new int[4];
            this.mUids = iArr2;
            iArr2[0] = i2;
            this.mNum = 1;
            return;
        }
        int i3 = this.mNum;
        if (i3 >= iArr.length) {
            int[] iArr3 = new int[(i3 * 3) / 2];
            if (i > 0) {
                System.arraycopy(iArr, 0, iArr3, 0, i);
            }
            int i4 = this.mNum;
            if (i < i4) {
                System.arraycopy(this.mUids, i, iArr3, i + 1, i4 - i);
            }
            this.mUids = iArr3;
            iArr3[i] = i2;
            this.mNum++;
            return;
        }
        if (i < i3) {
            System.arraycopy(iArr, i, iArr, i + 1, i3 - i);
        }
        this.mUids[i] = i2;
        this.mNum++;
    }

    private void insert(int i, int i2, String str) {
        int[] iArr = this.mUids;
        if (iArr == null) {
            int[] iArr2 = new int[4];
            this.mUids = iArr2;
            iArr2[0] = i2;
            String[] strArr = new String[4];
            this.mNames = strArr;
            strArr[0] = str;
            this.mNum = 1;
            return;
        }
        int i3 = this.mNum;
        if (i3 >= iArr.length) {
            int[] iArr3 = new int[(i3 * 3) / 2];
            String[] strArr2 = new String[(i3 * 3) / 2];
            if (i > 0) {
                System.arraycopy(iArr, 0, iArr3, 0, i);
                System.arraycopy(this.mNames, 0, strArr2, 0, i);
            }
            int i4 = this.mNum;
            if (i < i4) {
                int i5 = i + 1;
                System.arraycopy(this.mUids, i, iArr3, i5, i4 - i);
                System.arraycopy(this.mNames, i, strArr2, i5, this.mNum - i);
            }
            this.mUids = iArr3;
            this.mNames = strArr2;
            iArr3[i] = i2;
            strArr2[i] = str;
            this.mNum++;
            return;
        }
        if (i < i3) {
            int i6 = i + 1;
            System.arraycopy(iArr, i, iArr, i6, i3 - i);
            String[] strArr3 = this.mNames;
            System.arraycopy(strArr3, i, strArr3, i6, this.mNum - i);
        }
        this.mUids[i] = i2;
        this.mNames[i] = str;
        this.mNum++;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mNum);
        parcel.writeIntArray(this.mUids);
        parcel.writeStringArray(this.mNames);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WorkSource{");
        for (int i = 0; i < this.mNum; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(this.mUids[i]);
            if (this.mNames != null) {
                sb.append(" ");
                sb.append(this.mNames[i]);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}

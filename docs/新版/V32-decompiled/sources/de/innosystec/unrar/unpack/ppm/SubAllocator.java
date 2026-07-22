package de.innosystec.unrar.unpack.ppm;

import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class SubAllocator {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int FIXED_UNIT_SIZE = 12;
    public static final int N1 = 4;
    public static final int N2 = 4;
    public static final int N3 = 4;
    public static final int N4 = 26;
    public static final int N_INDEXES = 38;
    public static final int UNIT_SIZE = Math.max(PPMContext.size, 12);
    private int fakeUnitsStart;
    private int freeListPos;
    private int glueCount;
    private byte[] heap;
    private int heapEnd;
    private int heapStart;
    private int hiUnit;
    private int loUnit;
    private int pText;
    private int subAllocatorSize;
    private int tempMemBlockPos;
    private int unitsStart;
    private int[] indx2Units = new int[38];
    private int[] units2Indx = new int[128];
    private final RarNode[] freeList = new RarNode[38];
    private RarNode tempRarNode = null;
    private RarMemBlock tempRarMemBlock1 = null;
    private RarMemBlock tempRarMemBlock2 = null;
    private RarMemBlock tempRarMemBlock3 = null;

    public SubAllocator() {
        clean();
    }

    public void clean() {
        this.subAllocatorSize = 0;
    }

    private void insertNode(int i, int i2) {
        RarNode rarNode = this.tempRarNode;
        rarNode.setAddress(i);
        rarNode.setNext(this.freeList[i2].getNext());
        this.freeList[i2].setNext(rarNode);
    }

    public void incPText() {
        this.pText++;
    }

    private int removeNode(int i) {
        int next = this.freeList[i].getNext();
        RarNode rarNode = this.tempRarNode;
        rarNode.setAddress(next);
        this.freeList[i].setNext(rarNode.getNext());
        return next;
    }

    private int U2B(int i) {
        return UNIT_SIZE * i;
    }

    private int MBPtr(int i, int i2) {
        return i + U2B(i2);
    }

    private void splitBlock(int i, int i2, int i3) {
        int[] iArr = this.indx2Units;
        int i4 = iArr[i2] - iArr[i3];
        int iU2B = i + U2B(iArr[i3]);
        int[] iArr2 = this.indx2Units;
        int i5 = this.units2Indx[i4 - 1];
        if (iArr2[i5] != i4) {
            int i6 = i5 - 1;
            insertNode(iU2B, i6);
            int i7 = this.indx2Units[i6];
            iU2B += U2B(i7);
            i4 -= i7;
        }
        insertNode(iU2B, this.units2Indx[i4 - 1]);
    }

    public void stopSubAllocator() {
        if (this.subAllocatorSize != 0) {
            this.subAllocatorSize = 0;
            this.heap = null;
            this.heapStart = 1;
            this.tempRarNode = null;
            this.tempRarMemBlock1 = null;
            this.tempRarMemBlock2 = null;
            this.tempRarMemBlock3 = null;
        }
    }

    public int GetAllocatedMemory() {
        return this.subAllocatorSize;
    }

    public boolean startSubAllocator(int i) {
        int i2 = i << 20;
        if (this.subAllocatorSize == i2) {
            return true;
        }
        stopSubAllocator();
        int i3 = UNIT_SIZE;
        int i4 = ((i2 / 12) * i3) + i3;
        int i5 = i4 + 1 + 152;
        this.tempMemBlockPos = i5;
        this.heap = new byte[i5 + 12];
        this.heapStart = 1;
        this.heapEnd = (1 + i4) - i3;
        this.subAllocatorSize = i2;
        int i6 = i4 + 1;
        this.freeListPos = i6;
        int i7 = 0;
        while (true) {
            RarNode[] rarNodeArr = this.freeList;
            if (i7 < rarNodeArr.length) {
                rarNodeArr[i7] = new RarNode(this.heap);
                this.freeList[i7].setAddress(i6);
                i7++;
                i6 += 4;
            } else {
                this.tempRarNode = new RarNode(this.heap);
                this.tempRarMemBlock1 = new RarMemBlock(this.heap);
                this.tempRarMemBlock2 = new RarMemBlock(this.heap);
                this.tempRarMemBlock3 = new RarMemBlock(this.heap);
                return true;
            }
        }
    }

    private void glueFreeBlocks() {
        RarMemBlock rarMemBlock = this.tempRarMemBlock1;
        rarMemBlock.setAddress(this.tempMemBlockPos);
        RarMemBlock rarMemBlock2 = this.tempRarMemBlock2;
        RarMemBlock rarMemBlock3 = this.tempRarMemBlock3;
        int i = this.loUnit;
        if (i != this.hiUnit) {
            this.heap[i] = 0;
        }
        rarMemBlock.setPrev(rarMemBlock);
        rarMemBlock.setNext(rarMemBlock);
        for (int i2 = 0; i2 < 38; i2++) {
            while (this.freeList[i2].getNext() != 0) {
                rarMemBlock2.setAddress(removeNode(i2));
                rarMemBlock2.insertAt(rarMemBlock);
                rarMemBlock2.setStamp(65535);
                rarMemBlock2.setNU(this.indx2Units[i2]);
            }
        }
        rarMemBlock2.setAddress(rarMemBlock.getNext());
        while (rarMemBlock2.getAddress() != rarMemBlock.getAddress()) {
            rarMemBlock3.setAddress(MBPtr(rarMemBlock2.getAddress(), rarMemBlock2.getNU()));
            while (rarMemBlock3.getStamp() == 65535 && rarMemBlock2.getNU() + rarMemBlock3.getNU() < 65536) {
                rarMemBlock3.remove();
                rarMemBlock2.setNU(rarMemBlock2.getNU() + rarMemBlock3.getNU());
                rarMemBlock3.setAddress(MBPtr(rarMemBlock2.getAddress(), rarMemBlock2.getNU()));
            }
            rarMemBlock2.setAddress(rarMemBlock2.getNext());
        }
        rarMemBlock2.setAddress(rarMemBlock.getNext());
        while (rarMemBlock2.getAddress() != rarMemBlock.getAddress()) {
            rarMemBlock2.remove();
            int nu = rarMemBlock2.getNU();
            while (nu > 128) {
                insertNode(rarMemBlock2.getAddress(), 37);
                nu -= 128;
                rarMemBlock2.setAddress(MBPtr(rarMemBlock2.getAddress(), 128));
            }
            int[] iArr = this.indx2Units;
            int i3 = this.units2Indx[nu - 1];
            if (iArr[i3] != nu) {
                i3--;
                int i4 = nu - iArr[i3];
                insertNode(MBPtr(rarMemBlock2.getAddress(), nu - i4), i4 - 1);
            }
            insertNode(rarMemBlock2.getAddress(), i3);
            rarMemBlock2.setAddress(rarMemBlock.getNext());
        }
    }

    private int allocUnitsRare(int i) {
        if (this.glueCount == 0) {
            this.glueCount = 255;
            glueFreeBlocks();
            if (this.freeList[i].getNext() != 0) {
                return removeNode(i);
            }
        }
        int i2 = i;
        do {
            i2++;
            if (i2 == 38) {
                this.glueCount--;
                int iU2B = U2B(this.indx2Units[i]);
                int i3 = this.indx2Units[i] * 12;
                int i4 = this.fakeUnitsStart;
                if (i4 - this.pText <= i3) {
                    return 0;
                }
                this.fakeUnitsStart = i4 - i3;
                int i5 = this.unitsStart - iU2B;
                this.unitsStart = i5;
                return i5;
            }
        } while (this.freeList[i2].getNext() == 0);
        int iRemoveNode = removeNode(i2);
        splitBlock(iRemoveNode, i2, i);
        return iRemoveNode;
    }

    public int allocUnits(int i) {
        int i2 = this.units2Indx[i - 1];
        if (this.freeList[i2].getNext() != 0) {
            return removeNode(i2);
        }
        int i3 = this.loUnit;
        int iU2B = U2B(this.indx2Units[i2]) + i3;
        this.loUnit = iU2B;
        if (iU2B <= this.hiUnit) {
            return i3;
        }
        this.loUnit = iU2B - U2B(this.indx2Units[i2]);
        return allocUnitsRare(i2);
    }

    public int allocContext() {
        int i = this.hiUnit;
        if (i != this.loUnit) {
            int i2 = i - UNIT_SIZE;
            this.hiUnit = i2;
            return i2;
        }
        if (this.freeList[0].getNext() != 0) {
            return removeNode(0);
        }
        return allocUnitsRare(0);
    }

    public int expandUnits(int i, int i2) {
        int[] iArr = this.units2Indx;
        int i3 = i2 - 1;
        int i4 = iArr[i3];
        if (i4 == iArr[i3 + 1]) {
            return i;
        }
        int iAllocUnits = allocUnits(i2 + 1);
        if (iAllocUnits != 0) {
            byte[] bArr = this.heap;
            System.arraycopy(bArr, i, bArr, iAllocUnits, U2B(i2));
            insertNode(i, i4);
        }
        return iAllocUnits;
    }

    public int shrinkUnits(int i, int i2, int i3) {
        int[] iArr = this.units2Indx;
        int i4 = iArr[i2 - 1];
        int i5 = iArr[i3 - 1];
        if (i4 == i5) {
            return i;
        }
        if (this.freeList[i5].getNext() != 0) {
            int iRemoveNode = removeNode(i5);
            byte[] bArr = this.heap;
            System.arraycopy(bArr, i, bArr, iRemoveNode, U2B(i3));
            insertNode(i, i4);
            return iRemoveNode;
        }
        splitBlock(i, i4, i5);
        return i;
    }

    public void freeUnits(int i, int i2) {
        insertNode(i, this.units2Indx[i2 - 1]);
    }

    public int getFakeUnitsStart() {
        return this.fakeUnitsStart;
    }

    public void setFakeUnitsStart(int i) {
        this.fakeUnitsStart = i;
    }

    public int getHeapEnd() {
        return this.heapEnd;
    }

    public int getPText() {
        return this.pText;
    }

    public void setPText(int i) {
        this.pText = i;
    }

    public void decPText(int i) {
        setPText(getPText() - i);
    }

    public int getUnitsStart() {
        return this.unitsStart;
    }

    public void setUnitsStart(int i) {
        this.unitsStart = i;
    }

    public void initSubAllocator() {
        byte[] bArr = this.heap;
        int i = this.freeListPos;
        Arrays.fill(bArr, i, sizeOfFreeList() + i, (byte) 0);
        int i2 = this.heapStart;
        this.pText = i2;
        int i3 = this.subAllocatorSize;
        int i4 = ((i3 / 8) / 12) * 7 * 12;
        int i5 = UNIT_SIZE;
        int i6 = (i4 / 12) * i5;
        int i7 = i3 - i4;
        this.hiUnit = i3 + i2;
        int i8 = ((i7 / 12) * i5) + (i7 % 12) + i2;
        this.unitsStart = i8;
        this.loUnit = i8;
        this.fakeUnitsStart = i2 + i7;
        this.hiUnit = i8 + i6;
        int i9 = 1;
        int i10 = 0;
        while (i10 < 4) {
            this.indx2Units[i10] = i9 & 255;
            i10++;
            i9++;
        }
        int i11 = i9 + 1;
        while (i10 < 8) {
            this.indx2Units[i10] = i11 & 255;
            i10++;
            i11 += 2;
        }
        int i12 = i11 + 1;
        while (i10 < 12) {
            this.indx2Units[i10] = i12 & 255;
            i10++;
            i12 += 3;
        }
        int i13 = i12 + 1;
        while (i10 < 38) {
            this.indx2Units[i10] = i13 & 255;
            i10++;
            i13 += 4;
        }
        this.glueCount = 0;
        int i14 = 0;
        int i15 = 0;
        while (i14 < 128) {
            int i16 = i14 + 1;
            i15 += this.indx2Units[i15] < i16 ? 1 : 0;
            this.units2Indx[i14] = i15 & 255;
            i14 = i16;
        }
    }

    private int sizeOfFreeList() {
        return this.freeList.length * 4;
    }

    public byte[] getHeap() {
        return this.heap;
    }

    public String toString() {
        return "SubAllocator[\n  subAllocatorSize=" + this.subAllocatorSize + "\n  glueCount=" + this.glueCount + "\n  heapStart=" + this.heapStart + "\n  loUnit=" + this.loUnit + "\n  hiUnit=" + this.hiUnit + "\n  pText=" + this.pText + "\n  unitsStart=" + this.unitsStart + "\n]";
    }
}

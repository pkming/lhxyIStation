package androidx.paging;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

/* JADX INFO: loaded from: classes2.dex */
class PagedStorageDiffHelper {
    private PagedStorageDiffHelper() {
    }

    static <T> DiffUtil.DiffResult computeDiff(final PagedStorage<T> pagedStorage, final PagedStorage<T> pagedStorage2, final DiffUtil.ItemCallback<T> itemCallback) {
        final int iComputeLeadingNulls = pagedStorage.computeLeadingNulls();
        int iComputeLeadingNulls2 = pagedStorage2.computeLeadingNulls();
        final int size = (pagedStorage.size() - iComputeLeadingNulls) - pagedStorage.computeTrailingNulls();
        final int size2 = (pagedStorage2.size() - iComputeLeadingNulls2) - pagedStorage2.computeTrailingNulls();
        return DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: androidx.paging.PagedStorageDiffHelper.1
            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public Object getChangePayload(int i, int i2) {
                Object obj = pagedStorage.get(i + iComputeLeadingNulls);
                PagedStorage pagedStorage3 = pagedStorage2;
                Object obj2 = pagedStorage3.get(i2 + pagedStorage3.getLeadingNullCount());
                if (obj == null || obj2 == null) {
                    return null;
                }
                return itemCallback.getChangePayload(obj, obj2);
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getOldListSize() {
                return size;
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getNewListSize() {
                return size2;
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public boolean areItemsTheSame(int i, int i2) {
                Object obj = pagedStorage.get(i + iComputeLeadingNulls);
                PagedStorage pagedStorage3 = pagedStorage2;
                Object obj2 = pagedStorage3.get(i2 + pagedStorage3.getLeadingNullCount());
                if (obj == obj2) {
                    return true;
                }
                if (obj == null || obj2 == null) {
                    return false;
                }
                return itemCallback.areItemsTheSame(obj, obj2);
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public boolean areContentsTheSame(int i, int i2) {
                Object obj = pagedStorage.get(i + iComputeLeadingNulls);
                PagedStorage pagedStorage3 = pagedStorage2;
                Object obj2 = pagedStorage3.get(i2 + pagedStorage3.getLeadingNullCount());
                if (obj == obj2) {
                    return true;
                }
                if (obj == null || obj2 == null) {
                    return false;
                }
                return itemCallback.areContentsTheSame(obj, obj2);
            }
        }, true);
    }

    private static class OffsettingListUpdateCallback implements ListUpdateCallback {
        private final ListUpdateCallback mCallback;
        private final int mOffset;

        OffsettingListUpdateCallback(int i, ListUpdateCallback listUpdateCallback) {
            this.mOffset = i;
            this.mCallback = listUpdateCallback;
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onInserted(int i, int i2) {
            this.mCallback.onInserted(i + this.mOffset, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onRemoved(int i, int i2) {
            this.mCallback.onRemoved(i + this.mOffset, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onMoved(int i, int i2) {
            ListUpdateCallback listUpdateCallback = this.mCallback;
            int i3 = this.mOffset;
            listUpdateCallback.onMoved(i + i3, i2 + i3);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onChanged(int i, int i2, Object obj) {
            this.mCallback.onChanged(i + this.mOffset, i2, obj);
        }
    }

    static <T> void dispatchDiff(ListUpdateCallback listUpdateCallback, PagedStorage<T> pagedStorage, PagedStorage<T> pagedStorage2, DiffUtil.DiffResult diffResult) {
        int iComputeTrailingNulls = pagedStorage.computeTrailingNulls();
        int iComputeTrailingNulls2 = pagedStorage2.computeTrailingNulls();
        int iComputeLeadingNulls = pagedStorage.computeLeadingNulls();
        int iComputeLeadingNulls2 = pagedStorage2.computeLeadingNulls();
        if (iComputeTrailingNulls == 0 && iComputeTrailingNulls2 == 0 && iComputeLeadingNulls == 0 && iComputeLeadingNulls2 == 0) {
            diffResult.dispatchUpdatesTo(listUpdateCallback);
            return;
        }
        if (iComputeTrailingNulls > iComputeTrailingNulls2) {
            int i = iComputeTrailingNulls - iComputeTrailingNulls2;
            listUpdateCallback.onRemoved(pagedStorage.size() - i, i);
        } else if (iComputeTrailingNulls < iComputeTrailingNulls2) {
            listUpdateCallback.onInserted(pagedStorage.size(), iComputeTrailingNulls2 - iComputeTrailingNulls);
        }
        if (iComputeLeadingNulls > iComputeLeadingNulls2) {
            listUpdateCallback.onRemoved(0, iComputeLeadingNulls - iComputeLeadingNulls2);
        } else if (iComputeLeadingNulls < iComputeLeadingNulls2) {
            listUpdateCallback.onInserted(0, iComputeLeadingNulls2 - iComputeLeadingNulls);
        }
        if (iComputeLeadingNulls2 != 0) {
            diffResult.dispatchUpdatesTo(new OffsettingListUpdateCallback(iComputeLeadingNulls2, listUpdateCallback));
        } else {
            diffResult.dispatchUpdatesTo(listUpdateCallback);
        }
    }

    static int transformAnchorIndex(DiffUtil.DiffResult diffResult, PagedStorage pagedStorage, PagedStorage pagedStorage2, int i) {
        int iComputeLeadingNulls = pagedStorage.computeLeadingNulls();
        int i2 = i - iComputeLeadingNulls;
        int size = (pagedStorage.size() - iComputeLeadingNulls) - pagedStorage.computeTrailingNulls();
        if (i2 >= 0 && i2 < size) {
            for (int i3 = 0; i3 < 30; i3++) {
                int i4 = ((i3 / 2) * (i3 % 2 == 1 ? -1 : 1)) + i2;
                if (i4 >= 0 && i4 < pagedStorage.getStorageCount()) {
                    try {
                        int iConvertOldPositionToNew = diffResult.convertOldPositionToNew(i4);
                        if (iConvertOldPositionToNew != -1) {
                            return iConvertOldPositionToNew + pagedStorage2.getLeadingNullCount();
                        }
                    } catch (IndexOutOfBoundsException unused) {
                    }
                }
            }
        }
        return Math.max(0, Math.min(i, pagedStorage2.size() - 1));
    }
}

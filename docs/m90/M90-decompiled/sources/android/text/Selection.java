package android.text;

/* JADX INFO: loaded from: classes.dex */
public class Selection {
    public static final Object SELECTION_END;
    public static final Object SELECTION_START;

    public interface PositionIterator {
        public static final int DONE = -1;

        int following(int i);

        int preceding(int i);
    }

    private Selection() {
    }

    public static final int getSelectionStart(CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            return ((Spanned) charSequence).getSpanStart(SELECTION_START);
        }
        return -1;
    }

    public static final int getSelectionEnd(CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            return ((Spanned) charSequence).getSpanStart(SELECTION_END);
        }
        return -1;
    }

    public static void setSelection(Spannable spannable, int i, int i2) {
        int selectionStart = getSelectionStart(spannable);
        int selectionEnd = getSelectionEnd(spannable);
        if (selectionStart == i && selectionEnd == i2) {
            return;
        }
        spannable.setSpan(SELECTION_START, i, i, 546);
        spannable.setSpan(SELECTION_END, i2, i2, 34);
    }

    public static final void setSelection(Spannable spannable, int i) {
        setSelection(spannable, i, i);
    }

    public static final void selectAll(Spannable spannable) {
        setSelection(spannable, 0, spannable.length());
    }

    public static final void extendSelection(Spannable spannable, int i) {
        Object obj = SELECTION_END;
        if (spannable.getSpanStart(obj) != i) {
            spannable.setSpan(obj, i, i, 34);
        }
    }

    public static final void removeSelection(Spannable spannable) {
        spannable.removeSpan(SELECTION_START);
        spannable.removeSpan(SELECTION_END);
    }

    public static boolean moveUp(Spannable spannable, Layout layout) {
        int lineStart;
        int selectionStart = getSelectionStart(spannable);
        int selectionEnd = getSelectionEnd(spannable);
        if (selectionStart != selectionEnd) {
            int iMin = Math.min(selectionStart, selectionEnd);
            int iMax = Math.max(selectionStart, selectionEnd);
            setSelection(spannable, iMin);
            return (iMin == 0 && iMax == spannable.length()) ? false : true;
        }
        int lineForOffset = layout.getLineForOffset(selectionEnd);
        if (lineForOffset <= 0) {
            return false;
        }
        int paragraphDirection = layout.getParagraphDirection(lineForOffset);
        int i = lineForOffset - 1;
        if (paragraphDirection == layout.getParagraphDirection(i)) {
            lineStart = layout.getOffsetForHorizontal(i, layout.getPrimaryHorizontal(selectionEnd));
        } else {
            lineStart = layout.getLineStart(i);
        }
        setSelection(spannable, lineStart);
        return true;
    }

    public static boolean moveDown(Spannable spannable, Layout layout) {
        int lineStart;
        int selectionStart = getSelectionStart(spannable);
        int selectionEnd = getSelectionEnd(spannable);
        if (selectionStart != selectionEnd) {
            int iMin = Math.min(selectionStart, selectionEnd);
            int iMax = Math.max(selectionStart, selectionEnd);
            setSelection(spannable, iMax);
            return (iMin == 0 && iMax == spannable.length()) ? false : true;
        }
        int lineForOffset = layout.getLineForOffset(selectionEnd);
        if (lineForOffset >= layout.getLineCount() - 1) {
            return false;
        }
        int paragraphDirection = layout.getParagraphDirection(lineForOffset);
        int i = lineForOffset + 1;
        if (paragraphDirection == layout.getParagraphDirection(i)) {
            lineStart = layout.getOffsetForHorizontal(i, layout.getPrimaryHorizontal(selectionEnd));
        } else {
            lineStart = layout.getLineStart(i);
        }
        setSelection(spannable, lineStart);
        return true;
    }

    public static boolean moveLeft(Spannable spannable, Layout layout) {
        int selectionStart = getSelectionStart(spannable);
        int selectionEnd = getSelectionEnd(spannable);
        if (selectionStart != selectionEnd) {
            setSelection(spannable, chooseHorizontal(layout, -1, selectionStart, selectionEnd));
            return true;
        }
        int offsetToLeftOf = layout.getOffsetToLeftOf(selectionEnd);
        if (offsetToLeftOf == selectionEnd) {
            return false;
        }
        setSelection(spannable, offsetToLeftOf);
        return true;
    }

    public static boolean moveRight(Spannable spannable, Layout layout) {
        int selectionStart = getSelectionStart(spannable);
        int selectionEnd = getSelectionEnd(spannable);
        if (selectionStart != selectionEnd) {
            setSelection(spannable, chooseHorizontal(layout, 1, selectionStart, selectionEnd));
            return true;
        }
        int offsetToRightOf = layout.getOffsetToRightOf(selectionEnd);
        if (offsetToRightOf == selectionEnd) {
            return false;
        }
        setSelection(spannable, offsetToRightOf);
        return true;
    }

    public static boolean extendUp(Spannable spannable, Layout layout) {
        int lineStart;
        int selectionEnd = getSelectionEnd(spannable);
        int lineForOffset = layout.getLineForOffset(selectionEnd);
        if (lineForOffset <= 0) {
            if (selectionEnd != 0) {
                extendSelection(spannable, 0);
            }
            return true;
        }
        int paragraphDirection = layout.getParagraphDirection(lineForOffset);
        int i = lineForOffset - 1;
        if (paragraphDirection == layout.getParagraphDirection(i)) {
            lineStart = layout.getOffsetForHorizontal(i, layout.getPrimaryHorizontal(selectionEnd));
        } else {
            lineStart = layout.getLineStart(i);
        }
        extendSelection(spannable, lineStart);
        return true;
    }

    public static boolean extendDown(Spannable spannable, Layout layout) {
        int lineStart;
        int selectionEnd = getSelectionEnd(spannable);
        int lineForOffset = layout.getLineForOffset(selectionEnd);
        if (lineForOffset < layout.getLineCount() - 1) {
            int paragraphDirection = layout.getParagraphDirection(lineForOffset);
            int i = lineForOffset + 1;
            if (paragraphDirection == layout.getParagraphDirection(i)) {
                lineStart = layout.getOffsetForHorizontal(i, layout.getPrimaryHorizontal(selectionEnd));
            } else {
                lineStart = layout.getLineStart(i);
            }
            extendSelection(spannable, lineStart);
            return true;
        }
        if (selectionEnd != spannable.length()) {
            extendSelection(spannable, spannable.length());
        }
        return true;
    }

    public static boolean extendLeft(Spannable spannable, Layout layout) {
        int selectionEnd = getSelectionEnd(spannable);
        int offsetToLeftOf = layout.getOffsetToLeftOf(selectionEnd);
        if (offsetToLeftOf != selectionEnd) {
            extendSelection(spannable, offsetToLeftOf);
        }
        return true;
    }

    public static boolean extendRight(Spannable spannable, Layout layout) {
        int selectionEnd = getSelectionEnd(spannable);
        int offsetToRightOf = layout.getOffsetToRightOf(selectionEnd);
        if (offsetToRightOf != selectionEnd) {
            extendSelection(spannable, offsetToRightOf);
        }
        return true;
    }

    public static boolean extendToLeftEdge(Spannable spannable, Layout layout) {
        extendSelection(spannable, findEdge(spannable, layout, -1));
        return true;
    }

    public static boolean extendToRightEdge(Spannable spannable, Layout layout) {
        extendSelection(spannable, findEdge(spannable, layout, 1));
        return true;
    }

    public static boolean moveToLeftEdge(Spannable spannable, Layout layout) {
        setSelection(spannable, findEdge(spannable, layout, -1));
        return true;
    }

    public static boolean moveToRightEdge(Spannable spannable, Layout layout) {
        setSelection(spannable, findEdge(spannable, layout, 1));
        return true;
    }

    public static boolean moveToPreceding(Spannable spannable, PositionIterator positionIterator, boolean z) {
        int iPreceding = positionIterator.preceding(getSelectionEnd(spannable));
        if (iPreceding == -1) {
            return true;
        }
        if (z) {
            extendSelection(spannable, iPreceding);
            return true;
        }
        setSelection(spannable, iPreceding);
        return true;
    }

    public static boolean moveToFollowing(Spannable spannable, PositionIterator positionIterator, boolean z) {
        int iFollowing = positionIterator.following(getSelectionEnd(spannable));
        if (iFollowing == -1) {
            return true;
        }
        if (z) {
            extendSelection(spannable, iFollowing);
            return true;
        }
        setSelection(spannable, iFollowing);
        return true;
    }

    private static int findEdge(Spannable spannable, Layout layout, int i) {
        int lineForOffset = layout.getLineForOffset(getSelectionEnd(spannable));
        if (i * layout.getParagraphDirection(lineForOffset) < 0) {
            return layout.getLineStart(lineForOffset);
        }
        int lineEnd = layout.getLineEnd(lineForOffset);
        return lineForOffset == layout.getLineCount() + (-1) ? lineEnd : lineEnd - 1;
    }

    private static int chooseHorizontal(Layout layout, int i, int i2, int i3) {
        if (layout.getLineForOffset(i2) == layout.getLineForOffset(i3)) {
            float primaryHorizontal = layout.getPrimaryHorizontal(i2);
            float primaryHorizontal2 = layout.getPrimaryHorizontal(i3);
            return i < 0 ? primaryHorizontal < primaryHorizontal2 ? i2 : i3 : primaryHorizontal > primaryHorizontal2 ? i2 : i3;
        }
        if (layout.getParagraphDirection(layout.getLineForOffset(i2)) == i) {
            return Math.max(i2, i3);
        }
        return Math.min(i2, i3);
    }

    private static final class START implements NoCopySpan {
        private START() {
        }
    }

    private static final class END implements NoCopySpan {
        private END() {
        }
    }

    static {
        SELECTION_START = new START();
        SELECTION_END = new END();
    }
}

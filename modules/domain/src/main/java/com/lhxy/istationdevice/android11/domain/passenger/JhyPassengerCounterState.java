package com.lhxy.istationdevice.android11.domain.passenger;

public final class JhyPassengerCounterState {
    private static final JhyPassengerCounterState EMPTY = new JhyPassengerCounterState(false, 0, 0, 0, 0, 0);

    private final boolean available;
    private final int frontIn;
    private final int frontOut;
    private final int backIn;
    private final int backOut;
    private final int total;

    private JhyPassengerCounterState(boolean available, int frontIn, int frontOut, int backIn, int backOut, int total) {
        this.available = available;
        this.frontIn = Math.max(frontIn, 0);
        this.frontOut = Math.max(frontOut, 0);
        this.backIn = Math.max(backIn, 0);
        this.backOut = Math.max(backOut, 0);
        this.total = Math.max(total, 0);
    }

    public static JhyPassengerCounterState empty() {
        return EMPTY;
    }

    public static JhyPassengerCounterState of(int frontIn, int frontOut, int backIn, int backOut) {
        int totalIn = Math.max(frontIn, 0) + Math.max(backIn, 0);
        int totalOut = Math.max(frontOut, 0) + Math.max(backOut, 0);
        return new JhyPassengerCounterState(true, frontIn, frontOut, backIn, backOut, Math.max(totalIn - totalOut, 0));
    }

    public boolean isAvailable() {
        return available;
    }

    public int getFrontIn() {
        return frontIn;
    }

    public int getFrontOut() {
        return frontOut;
    }

    public int getBackIn() {
        return backIn;
    }

    public int getBackOut() {
        return backOut;
    }

    public int getTotal() {
        return total;
    }

    public String getFrontInText() {
        return available ? String.valueOf(frontIn) : "N";
    }

    public String getFrontOutText() {
        return available ? String.valueOf(frontOut) : "N";
    }

    public String getBackInText() {
        return available ? String.valueOf(backIn) : "N";
    }

    public String getBackOutText() {
        return available ? String.valueOf(backOut) : "N";
    }

    public String getTotalText() {
        return available ? String.valueOf(total) : "N";
    }
}
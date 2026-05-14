package com.lhxy.istationdevice.android11.devicem90;

final class M90I2CPort {
    private static final boolean LIBRARY_LOADED;
    private static final String LOAD_ERROR_MESSAGE;

    static {
        boolean loaded = false;
        String errorMessage = "";
        try {
            System.loadLibrary("i2c-port");
            loaded = true;
        } catch (Throwable throwable) {
            errorMessage = throwable.getMessage() == null
                    ? throwable.getClass().getSimpleName()
                    : throwable.getMessage();
        }
        LIBRARY_LOADED = loaded;
        LOAD_ERROR_MESSAGE = errorMessage;
    }

    static boolean isLibraryLoaded() {
        return LIBRARY_LOADED;
    }

    static String getLoadErrorMessage() {
        return LOAD_ERROR_MESSAGE;
    }

    native void RfidClose();

    native byte[] RfidGetId(byte channel);

    native void RfidInit();

    native void RfidSetID(byte channel, byte[] value);

    native void RfidWaitCardOff();
}
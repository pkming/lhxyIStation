package de.innosystec.unrar.exception;

/* JADX INFO: loaded from: classes2.dex */
public class RarException extends Exception {
    private RarExceptionType type;

    public enum RarExceptionType {
        notImplementedYet,
        crcError,
        notRarArchive,
        badRarArchive,
        unkownError,
        headerNotInArchive,
        wrongHeaderType,
        ioError,
        rarEncryptedException;

        /* JADX INFO: renamed from: values, reason: to resolve conflict with enum method */
        public static RarExceptionType[] valuesCustom() {
            RarExceptionType[] rarExceptionTypeArrValuesCustom = values();
            int length = rarExceptionTypeArrValuesCustom.length;
            RarExceptionType[] rarExceptionTypeArr = new RarExceptionType[length];
            System.arraycopy(rarExceptionTypeArrValuesCustom, 0, rarExceptionTypeArr, 0, length);
            return rarExceptionTypeArr;
        }
    }

    public RarException(Exception exc) {
        super(RarExceptionType.unkownError.name(), exc);
        this.type = RarExceptionType.unkownError;
    }

    public RarException(RarException rarException) {
        super(rarException.getMessage(), rarException);
        this.type = rarException.getType();
    }

    public RarException(RarExceptionType rarExceptionType) {
        super(rarExceptionType.name());
        this.type = rarExceptionType;
    }

    public RarExceptionType getType() {
        return this.type;
    }

    public void setType(RarExceptionType rarExceptionType) {
        this.type = rarExceptionType;
    }
}

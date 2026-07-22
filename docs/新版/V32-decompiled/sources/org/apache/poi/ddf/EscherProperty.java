package org.apache.poi.ddf;

import de.innosystec.unrar.rarfile.BaseBlock;

/* JADX INFO: loaded from: classes3.dex */
public abstract class EscherProperty {
    private short id;

    public int getPropertySize() {
        return 6;
    }

    public abstract int serializeComplexPart(byte[] bArr, int i);

    public abstract int serializeSimplePart(byte[] bArr, int i);

    public EscherProperty(short s) {
        this.id = s;
    }

    public EscherProperty(short s, boolean z, boolean z2) {
        this.id = (short) (s + (z ? BaseBlock.LONG_BLOCK : (short) 0) + (z2 ? 16384 : 0));
    }

    public short getId() {
        return this.id;
    }

    public short getPropertyNumber() {
        return (short) (this.id & 16383);
    }

    public boolean isComplex() {
        return (this.id & BaseBlock.LONG_BLOCK) != 0;
    }

    public boolean isBlipId() {
        return (this.id & BaseBlock.SKIP_IF_UNKNOWN) != 0;
    }

    public String getName() {
        return EscherProperties.getPropertyName(this.id);
    }
}

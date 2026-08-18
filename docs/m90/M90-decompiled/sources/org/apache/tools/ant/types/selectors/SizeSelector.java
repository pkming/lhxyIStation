package org.apache.tools.ant.types.selectors;

import android.net.TrafficStats;
import com.amap.api.col.p0003sl.ju;
import java.io.File;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public class SizeSelector extends BaseExtendSelector {
    private static final int END_POS = 36;
    private static final long GIBI = 1073741824;
    private static final int GIBI_POS = 22;
    private static final long GIGA = 1000000000;
    private static final int GIGA_POS = 18;
    private static final int KIBI = 1024;
    private static final int KIBI_POS = 4;
    private static final int KILO = 1000;
    private static final int MEBI = 1048576;
    private static final int MEBI_POS = 13;
    private static final int MEGA = 1000000;
    private static final int MEGA_POS = 9;
    public static final String SIZE_KEY = "value";
    private static final long TEBI = 1099511627776L;
    private static final int TEBI_POS = 31;
    private static final long TERA = 1000000000000L;
    private static final int TERA_POS = 27;
    public static final String UNITS_KEY = "units";
    public static final String WHEN_KEY = "when";
    private long size = -1;
    private long multiplier = 1;
    private long sizelimit = -1;
    private Comparison when = Comparison.EQUAL;

    public static class SizeComparisons extends Comparison {
    }

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        StringBuilder sb = new StringBuilder("{sizeselector value: ");
        sb.append(this.sizelimit);
        sb.append("compare: ").append(this.when.getValue());
        sb.append("}");
        return sb.toString();
    }

    public void setValue(long j) {
        this.size = j;
        long j2 = this.multiplier;
        if (j2 == 0 || j <= -1) {
            return;
        }
        this.sizelimit = j * j2;
    }

    public void setUnits(ByteUnits byteUnits) {
        int index = byteUnits.getIndex();
        this.multiplier = 0L;
        if (index > -1 && index < 4) {
            this.multiplier = 1000L;
        } else if (index < 9) {
            this.multiplier = 1024L;
        } else if (index < 13) {
            this.multiplier = 1000000L;
        } else if (index < 18) {
            this.multiplier = TrafficStats.MB_IN_BYTES;
        } else if (index < 22) {
            this.multiplier = GIGA;
        } else if (index < 27) {
            this.multiplier = 1073741824L;
        } else if (index < 31) {
            this.multiplier = TERA;
        } else if (index < 36) {
            this.multiplier = TEBI;
        }
        long j = this.multiplier;
        if (j > 0) {
            long j2 = this.size;
            if (j2 > -1) {
                this.sizelimit = j2 * j;
            }
        }
    }

    public void setWhen(SizeComparisons sizeComparisons) {
        this.when = sizeComparisons;
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.Parameterizable
    public void setParameters(Parameter[] parameterArr) {
        super.setParameters(parameterArr);
        if (parameterArr != null) {
            for (int i = 0; i < parameterArr.length; i++) {
                String name = parameterArr[i].getName();
                if ("value".equalsIgnoreCase(name)) {
                    try {
                        setValue(Long.parseLong(parameterArr[i].getValue()));
                    } catch (NumberFormatException unused) {
                        setError("Invalid size setting " + parameterArr[i].getValue());
                    }
                } else if (UNITS_KEY.equalsIgnoreCase(name)) {
                    ByteUnits byteUnits = new ByteUnits();
                    byteUnits.setValue(parameterArr[i].getValue());
                    setUnits(byteUnits);
                } else if ("when".equalsIgnoreCase(name)) {
                    SizeComparisons sizeComparisons = new SizeComparisons();
                    sizeComparisons.setValue(parameterArr[i].getValue());
                    setWhen(sizeComparisons);
                } else {
                    setError("Invalid parameter " + name);
                }
            }
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        if (this.size < 0) {
            setError("The value attribute is required, and must be positive");
        } else if (this.multiplier < 1) {
            setError("Invalid Units supplied, must be K,Ki,M,Mi,G,Gi,T,or Ti");
        } else if (this.sizelimit < 0) {
            setError("Internal error: Code is not setting sizelimit correctly");
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        validate();
        if (file2.isDirectory()) {
            return true;
        }
        long length = file2.length() - this.sizelimit;
        return this.when.evaluate(length == 0 ? 0 : (int) (length / Math.abs(length)));
    }

    public static class ByteUnits extends EnumeratedAttribute {
        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"K", "k", "kilo", "KILO", "Ki", "KI", "ki", "kibi", "KIBI", "M", "m", "mega", "MEGA", "Mi", "MI", "mi", "mebi", "MEBI", "G", ju.f, "giga", "GIGA", "Gi", "GI", "gi", "gibi", "GIBI", "T", "t", "tera", "TERA", "Ti", "TI", "ti", "tebi", "TEBI"};
        }
    }
}

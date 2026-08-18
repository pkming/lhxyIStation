package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public class DepthSelector extends BaseExtendSelector {
    public static final String MAX_KEY = "max";
    public static final String MIN_KEY = "min";
    public int min = -1;
    public int max = -1;

    @Override // org.apache.tools.ant.types.DataType
    public String toString() {
        return "{depthselector min: " + this.min + " max: " + this.max + "}";
    }

    public void setMin(int i) {
        this.min = i;
    }

    public void setMax(int i) {
        this.max = i;
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.Parameterizable
    public void setParameters(Parameter[] parameterArr) {
        super.setParameters(parameterArr);
        if (parameterArr != null) {
            for (int i = 0; i < parameterArr.length; i++) {
                String name = parameterArr[i].getName();
                if (MIN_KEY.equalsIgnoreCase(name)) {
                    try {
                        setMin(Integer.parseInt(parameterArr[i].getValue()));
                    } catch (NumberFormatException unused) {
                        setError("Invalid minimum value " + parameterArr[i].getValue());
                    }
                } else if (MAX_KEY.equalsIgnoreCase(name)) {
                    try {
                        setMax(Integer.parseInt(parameterArr[i].getValue()));
                    } catch (NumberFormatException unused2) {
                        setError("Invalid maximum value " + parameterArr[i].getValue());
                    }
                } else {
                    setError("Invalid parameter " + name);
                }
            }
        }
    }

    @Override // org.apache.tools.ant.types.selectors.BaseSelector
    public void verifySettings() {
        if (this.min < 0 && this.max < 0) {
            setError("You must set at least one of the min or the max levels.");
        }
        int i = this.max;
        if (i >= this.min || i <= -1) {
            return;
        }
        setError("The maximum depth is lower than the minimum.");
    }

    @Override // org.apache.tools.ant.types.selectors.BaseExtendSelector, org.apache.tools.ant.types.selectors.BaseSelector, org.apache.tools.ant.types.selectors.FileSelector
    public boolean isSelected(File file, String str, File file2) {
        validate();
        String absolutePath = file.getAbsolutePath();
        String absolutePath2 = file2.getAbsolutePath();
        StringTokenizer stringTokenizer = new StringTokenizer(absolutePath, File.separator);
        StringTokenizer stringTokenizer2 = new StringTokenizer(absolutePath2, File.separator);
        int i = -1;
        while (stringTokenizer2.hasMoreTokens()) {
            String strNextToken = stringTokenizer2.nextToken();
            if (stringTokenizer.hasMoreTokens()) {
                if (!stringTokenizer.nextToken().equals(strNextToken)) {
                    throw new BuildException("File " + str + " does not appear within " + absolutePath + "directory");
                }
            } else {
                i++;
                int i2 = this.max;
                if (i2 > -1 && i > i2) {
                    return false;
                }
            }
        }
        if (stringTokenizer.hasMoreTokens()) {
            throw new BuildException("File " + str + " is outside of " + absolutePath + "directory tree");
        }
        int i3 = this.min;
        return i3 <= -1 || i >= i3;
    }
}

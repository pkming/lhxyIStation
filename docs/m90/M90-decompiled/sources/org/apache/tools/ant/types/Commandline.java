package org.apache.tools.ant.types;

import android.text.format.DateFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class Commandline implements Cloneable {
    private List<Argument> arguments = new ArrayList();
    private String executable = null;
    private static final boolean IS_WIN_9X = Os.isFamily(Os.FAMILY_9X);
    protected static final String DISCLAIMER = StringUtils.LINE_SEP + "The ' characters around the executable and arguments are" + StringUtils.LINE_SEP + "not part of the command." + StringUtils.LINE_SEP;

    public Commandline(String str) {
        String[] strArrTranslateCommandline = translateCommandline(str);
        if (strArrTranslateCommandline == null || strArrTranslateCommandline.length <= 0) {
            return;
        }
        setExecutable(strArrTranslateCommandline[0]);
        for (int i = 1; i < strArrTranslateCommandline.length; i++) {
            createArgument().setValue(strArrTranslateCommandline[i]);
        }
    }

    public Commandline() {
    }

    public static class Argument extends ProjectComponent {
        private String[] parts;
        private String prefix = "";
        private String suffix = "";

        public void setValue(String str) {
            this.parts = new String[]{str};
        }

        public void setLine(String str) {
            if (str == null) {
                return;
            }
            this.parts = Commandline.translateCommandline(str);
        }

        public void setPath(Path path) {
            this.parts = new String[]{path.toString()};
        }

        public void setPathref(Reference reference) {
            Path path = new Path(getProject());
            path.setRefid(reference);
            this.parts = new String[]{path.toString()};
        }

        public void setFile(File file) {
            this.parts = new String[]{file.getAbsolutePath()};
        }

        public void setPrefix(String str) {
            if (str == null) {
                str = "";
            }
            this.prefix = str;
        }

        public void setSuffix(String str) {
            if (str == null) {
                str = "";
            }
            this.suffix = str;
        }

        public String[] getParts() {
            String[] strArr = this.parts;
            if (strArr == null || strArr.length == 0 || (this.prefix.length() == 0 && this.suffix.length() == 0)) {
                return this.parts;
            }
            int length = this.parts.length;
            String[] strArr2 = new String[length];
            for (int i = 0; i < length; i++) {
                strArr2[i] = this.prefix + this.parts[i] + this.suffix;
            }
            return strArr2;
        }
    }

    public class Marker {
        private int position;
        private int realPos = -1;
        private String prefix = "";
        private String suffix = "";

        Marker(int i) {
            this.position = i;
        }

        public int getPosition() {
            if (this.realPos == -1) {
                this.realPos = Commandline.this.executable == null ? 0 : 1;
                for (int i = 0; i < this.position; i++) {
                    this.realPos += ((Argument) Commandline.this.arguments.get(i)).getParts().length;
                }
            }
            return this.realPos;
        }

        public void setPrefix(String str) {
            if (str == null) {
                str = "";
            }
            this.prefix = str;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void setSuffix(String str) {
            if (str == null) {
                str = "";
            }
            this.suffix = str;
        }

        public String getSuffix() {
            return this.suffix;
        }
    }

    public Argument createArgument() {
        return createArgument(false);
    }

    public Argument createArgument(boolean z) {
        Argument argument = new Argument();
        if (z) {
            this.arguments.add(0, argument);
        } else {
            this.arguments.add(argument);
        }
        return argument;
    }

    public void setExecutable(String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        this.executable = str.replace('/', File.separatorChar).replace('\\', File.separatorChar);
    }

    public String getExecutable() {
        return this.executable;
    }

    public void addArguments(String[] strArr) {
        for (String str : strArr) {
            createArgument().setValue(str);
        }
    }

    public String[] getCommandline() {
        LinkedList linkedList = new LinkedList();
        addCommandToList(linkedList.listIterator());
        return (String[]) linkedList.toArray(new String[linkedList.size()]);
    }

    public void addCommandToList(ListIterator<String> listIterator) {
        String str = this.executable;
        if (str != null) {
            listIterator.add(str);
        }
        addArgumentsToList(listIterator);
    }

    public String[] getArguments() {
        ArrayList arrayList = new ArrayList(this.arguments.size() * 2);
        addArgumentsToList(arrayList.listIterator());
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public void addArgumentsToList(ListIterator<String> listIterator) {
        int size = this.arguments.size();
        for (int i = 0; i < size; i++) {
            String[] parts = this.arguments.get(i).getParts();
            if (parts != null) {
                for (String str : parts) {
                    listIterator.add(str);
                }
            }
        }
    }

    public String toString() {
        return toString(getCommandline());
    }

    public static String quoteArgument(String str) {
        if (str.indexOf("\"") <= -1) {
            return (str.indexOf("'") > -1 || str.indexOf(" ") > -1 || (IS_WIN_9X && str.indexOf(59) != -1)) ? '\"' + str + '\"' : str;
        }
        if (str.indexOf("'") > -1) {
            throw new BuildException("Can't handle single and double quotes in same argument");
        }
        return DateFormat.QUOTE + str + DateFormat.QUOTE;
    }

    public static String toString(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(quoteArgument(strArr[i]));
        }
        return sb.toString();
    }

    public static String[] translateCommandline(String str) {
        if (str == null || str.length() == 0) {
            return new String[0];
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, "\"' ", true);
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        char c = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (c != 1) {
                if (c == 2) {
                    if ("\"".equals(strNextToken)) {
                        c = 0;
                        z = true;
                    } else {
                        sb.append(strNextToken);
                    }
                } else {
                    if ("'".equals(strNextToken)) {
                        c = 1;
                    } else if ("\"".equals(strNextToken)) {
                        c = 2;
                    } else if (" ".equals(strNextToken)) {
                        if (z || sb.length() != 0) {
                            arrayList.add(sb.toString());
                            sb.setLength(0);
                        }
                    } else {
                        sb.append(strNextToken);
                    }
                    z = false;
                }
            } else if ("'".equals(strNextToken)) {
                c = 0;
                z = true;
            } else {
                sb.append(strNextToken);
            }
        }
        if (z || sb.length() != 0) {
            arrayList.add(sb.toString());
        }
        if (c == 1 || c == 2) {
            throw new BuildException("unbalanced quotes in " + str);
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public int size() {
        return getCommandline().length;
    }

    public Object clone() {
        try {
            Commandline commandline = (Commandline) super.clone();
            commandline.arguments = new ArrayList(this.arguments);
            return commandline;
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void clear() {
        this.executable = null;
        this.arguments.clear();
    }

    public void clearArgs() {
        this.arguments.clear();
    }

    public Marker createMarker() {
        return new Marker(this.arguments.size());
    }

    public String describeCommand() {
        return describeCommand(this);
    }

    public String describeArguments() {
        return describeArguments(this);
    }

    public static String describeCommand(Commandline commandline) {
        return describeCommand(commandline.getCommandline());
    }

    public static String describeArguments(Commandline commandline) {
        return describeArguments(commandline.getArguments());
    }

    public static String describeCommand(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("Executing '");
        stringBuffer.append(strArr[0]);
        stringBuffer.append("'");
        if (strArr.length > 1) {
            stringBuffer.append(" with ");
            stringBuffer.append(describeArguments(strArr, 1));
        } else {
            stringBuffer.append(DISCLAIMER);
        }
        return stringBuffer.toString();
    }

    public static String describeArguments(String[] strArr) {
        return describeArguments(strArr, 0);
    }

    protected static String describeArguments(String[] strArr, int i) {
        if (strArr == null || strArr.length <= i) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("argument");
        if (strArr.length > i) {
            stringBuffer.append("s");
        }
        stringBuffer.append(":").append(StringUtils.LINE_SEP);
        while (i < strArr.length) {
            stringBuffer.append("'").append(strArr[i]).append("'").append(StringUtils.LINE_SEP);
            i++;
        }
        stringBuffer.append(DISCLAIMER);
        return stringBuffer.toString();
    }

    public Iterator<Argument> iterator() {
        return this.arguments.iterator();
    }
}

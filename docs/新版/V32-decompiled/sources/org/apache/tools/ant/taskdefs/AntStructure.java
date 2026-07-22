package org.apache.tools.ant.taskdefs;

import android.app.Instrumentation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Reference;

/* JADX INFO: loaded from: classes3.dex */
public class AntStructure extends Task {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private File output;
    private StructurePrinter printer = new DTDPrinter();

    public interface StructurePrinter {
        void printElementDecl(PrintWriter printWriter, Project project, String str, Class<?> cls);

        void printHead(PrintWriter printWriter, Project project, Hashtable<String, Class<?>> hashtable, Hashtable<String, Class<?>> hashtable2);

        void printTail(PrintWriter printWriter);

        void printTargetDecl(PrintWriter printWriter);
    }

    public void setOutput(File file) {
        this.output = file;
    }

    public void add(StructurePrinter structurePrinter) {
        this.printer = structurePrinter;
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        PrintWriter printWriter;
        if (this.output == null) {
            throw new BuildException("output attribute is required", getLocation());
        }
        PrintWriter printWriter2 = null;
        try {
            try {
                try {
                    printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.output), "UTF8"));
                } catch (Throwable th) {
                    if (printWriter2 != null) {
                        printWriter2.close();
                    }
                    throw th;
                }
            } catch (UnsupportedEncodingException unused) {
                printWriter = new PrintWriter(new FileWriter(this.output));
            }
            printWriter2 = printWriter;
            this.printer.printHead(printWriter2, getProject(), new Hashtable<>(getProject().getTaskDefinitions()), new Hashtable<>(getProject().getDataTypeDefinitions()));
            this.printer.printTargetDecl(printWriter2);
            for (String str : getProject().getCopyOfDataTypeDefinitions().keySet()) {
                this.printer.printElementDecl(printWriter2, getProject(), str, getProject().getDataTypeDefinitions().get(str));
            }
            for (String str2 : getProject().getCopyOfTaskDefinitions().keySet()) {
                this.printer.printElementDecl(printWriter2, getProject(), str2, getProject().getTaskDefinitions().get(str2));
            }
            this.printer.printTail(printWriter2);
            if (printWriter2.checkError()) {
                throw new IOException("Encountered an error writing Ant structure");
            }
            printWriter2.close();
        } catch (IOException e) {
            throw new BuildException("Error writing " + this.output.getAbsolutePath(), e, getLocation());
        }
    }

    private static class DTDPrinter implements StructurePrinter {
        private static final String BOOLEAN = "%boolean;";
        private static final String TASKS = "%tasks;";
        private static final String TYPES = "%types;";
        private Hashtable<String, String> visited;

        private DTDPrinter() {
            this.visited = new Hashtable<>();
        }

        @Override // org.apache.tools.ant.taskdefs.AntStructure.StructurePrinter
        public void printTail(PrintWriter printWriter) {
            this.visited.clear();
        }

        @Override // org.apache.tools.ant.taskdefs.AntStructure.StructurePrinter
        public void printHead(PrintWriter printWriter, Project project, Hashtable<String, Class<?>> hashtable, Hashtable<String, Class<?>> hashtable2) {
            printHead(printWriter, hashtable.keys(), hashtable2.keys());
        }

        private void printHead(PrintWriter printWriter, Enumeration<String> enumeration, Enumeration<String> enumeration2) {
            printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            printWriter.println("<!ENTITY % boolean \"(true|false|on|off|yes|no)\">");
            printWriter.print("<!ENTITY % tasks \"");
            boolean z = true;
            boolean z2 = true;
            while (enumeration.hasMoreElements()) {
                String strNextElement = enumeration.nextElement();
                if (z2) {
                    z2 = false;
                } else {
                    printWriter.print(" | ");
                }
                printWriter.print(strNextElement);
            }
            printWriter.println("\">");
            printWriter.print("<!ENTITY % types \"");
            while (enumeration2.hasMoreElements()) {
                String strNextElement2 = enumeration2.nextElement();
                if (z) {
                    z = false;
                } else {
                    printWriter.print(" | ");
                }
                printWriter.print(strNextElement2);
            }
            printWriter.println("\">");
            printWriter.println("");
            printWriter.print("<!ELEMENT project (target | extension-point | ");
            printWriter.print(TASKS);
            printWriter.print(" | ");
            printWriter.print(TYPES);
            printWriter.println(")*>");
            printWriter.println("<!ATTLIST project");
            printWriter.println("          name    CDATA #IMPLIED");
            printWriter.println("          default CDATA #IMPLIED");
            printWriter.println("          basedir CDATA #IMPLIED>");
            printWriter.println("");
        }

        @Override // org.apache.tools.ant.taskdefs.AntStructure.StructurePrinter
        public void printTargetDecl(PrintWriter printWriter) {
            printWriter.print("<!ELEMENT target (");
            printWriter.print(TASKS);
            printWriter.print(" | ");
            printWriter.print(TYPES);
            printWriter.println(")*>");
            printWriter.println("");
            printTargetAttrs(printWriter, "target");
            printWriter.println("<!ELEMENT extension-point EMPTY>");
            printWriter.println("");
            printTargetAttrs(printWriter, "extension-point");
        }

        private void printTargetAttrs(PrintWriter printWriter, String str) {
            printWriter.print("<!ATTLIST ");
            printWriter.println(str);
            printWriter.println("          id                      ID    #IMPLIED");
            printWriter.println("          name                    CDATA #REQUIRED");
            printWriter.println("          if                      CDATA #IMPLIED");
            printWriter.println("          unless                  CDATA #IMPLIED");
            printWriter.println("          depends                 CDATA #IMPLIED");
            printWriter.println("          extensionOf             CDATA #IMPLIED");
            printWriter.println("          onMissingExtensionPoint CDATA #IMPLIED");
            printWriter.println("          description             CDATA #IMPLIED>");
            printWriter.println("");
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Override // org.apache.tools.ant.taskdefs.AntStructure.StructurePrinter
        public void printElementDecl(PrintWriter printWriter, Project project, String str, Class<?> cls) {
            int i;
            Enumeration<String> enumeration;
            String str2;
            if (this.visited.containsKey(str)) {
                return;
            }
            this.visited.put(str, "");
            try {
                IntrospectionHelper helper = IntrospectionHelper.getHelper(project, cls);
                StringBuffer stringBuffer = new StringBuffer("<!ELEMENT ");
                stringBuffer.append(str).append(" ");
                if (Reference.class.equals(cls)) {
                    stringBuffer.append("EMPTY>").append(AntStructure.LINE_SEP);
                    stringBuffer.append("<!ATTLIST ").append(str);
                    stringBuffer.append(AntStructure.LINE_SEP).append("          id ID #IMPLIED");
                    stringBuffer.append(AntStructure.LINE_SEP).append("          refid IDREF #IMPLIED");
                    stringBuffer.append(">").append(AntStructure.LINE_SEP);
                    printWriter.println(stringBuffer);
                    return;
                }
                Vector vector = new Vector();
                if (helper.supportsCharacters()) {
                    vector.addElement("#PCDATA");
                }
                if (TaskContainer.class.isAssignableFrom(cls)) {
                    vector.addElement(TASKS);
                }
                Enumeration<String> nestedElements = helper.getNestedElements();
                while (nestedElements.hasMoreElements()) {
                    vector.addElement(nestedElements.nextElement());
                }
                String str3 = "(";
                if (vector.isEmpty()) {
                    stringBuffer.append("EMPTY");
                    i = 0;
                } else {
                    stringBuffer.append("(");
                    int size = vector.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        if (i2 != 0) {
                            stringBuffer.append(" | ");
                        }
                        stringBuffer.append((String) vector.elementAt(i2));
                    }
                    stringBuffer.append(")");
                    if (size <= 1) {
                        i = 0;
                        if (!((String) vector.elementAt(0)).equals("#PCDATA")) {
                        }
                    } else {
                        i = 0;
                    }
                    stringBuffer.append("*");
                }
                stringBuffer.append(">");
                printWriter.println(stringBuffer);
                StringBuffer stringBuffer2 = new StringBuffer("<!ATTLIST ");
                stringBuffer2.append(str);
                stringBuffer2.append(AntStructure.LINE_SEP).append("          id ID #IMPLIED");
                Enumeration<String> attributes = helper.getAttributes();
                while (attributes.hasMoreElements()) {
                    String strNextElement = attributes.nextElement();
                    if (!Instrumentation.REPORT_KEY_IDENTIFIER.equals(strNextElement)) {
                        stringBuffer2.append(AntStructure.LINE_SEP).append("          ").append(strNextElement).append(" ");
                        Class<?> attributeType = helper.getAttributeType(strNextElement);
                        if (attributeType.equals(Boolean.class) || attributeType.equals(Boolean.TYPE)) {
                            enumeration = attributes;
                            str2 = str3;
                            stringBuffer2.append(BOOLEAN).append(" ");
                        } else {
                            if (Reference.class.isAssignableFrom(attributeType)) {
                                stringBuffer2.append("IDREF ");
                            } else if (EnumeratedAttribute.class.isAssignableFrom(attributeType)) {
                                try {
                                    String[] values = ((EnumeratedAttribute) attributeType.newInstance()).getValues();
                                    if (values == null || values.length == 0 || !areNmtokens(values)) {
                                        stringBuffer2.append("CDATA ");
                                    } else {
                                        stringBuffer2.append(str3);
                                        for (int i3 = i; i3 < values.length; i3++) {
                                            if (i3 != 0) {
                                                stringBuffer2.append(" | ");
                                            }
                                            stringBuffer2.append(values[i3]);
                                        }
                                        stringBuffer2.append(") ");
                                    }
                                } catch (IllegalAccessException unused) {
                                    stringBuffer2.append("CDATA ");
                                } catch (InstantiationException unused2) {
                                    stringBuffer2.append("CDATA ");
                                }
                            } else if (attributeType.getSuperclass() != null && attributeType.getSuperclass().getName().equals("java.lang.Enum")) {
                                enumeration = attributes;
                                try {
                                    Object[] objArr = (Object[]) attributeType.getMethod("values", (Class[]) null).invoke(null, (Object[]) null);
                                    if (objArr.length == 0) {
                                        stringBuffer2.append("CDATA ");
                                        str2 = str3;
                                    } else {
                                        stringBuffer2.append('(');
                                        int i4 = 0;
                                        while (i4 < objArr.length) {
                                            if (i4 != 0) {
                                                stringBuffer2.append(" | ");
                                            }
                                            str2 = str3;
                                            try {
                                                stringBuffer2.append(attributeType.getMethod("name", (Class[]) null).invoke(objArr[i4], (Object[]) null));
                                                i4++;
                                                str3 = str2;
                                                objArr = objArr;
                                            } catch (Exception unused3) {
                                                stringBuffer2.append("CDATA ");
                                                stringBuffer2.append("#IMPLIED");
                                                attributes = enumeration;
                                                str3 = str2;
                                                i = 0;
                                            }
                                        }
                                        str2 = str3;
                                        stringBuffer2.append(") ");
                                    }
                                } catch (Exception unused4) {
                                    str2 = str3;
                                }
                            } else {
                                enumeration = attributes;
                                str2 = str3;
                                stringBuffer2.append("CDATA ");
                            }
                            enumeration = attributes;
                            str2 = str3;
                        }
                        stringBuffer2.append("#IMPLIED");
                        attributes = enumeration;
                        str3 = str2;
                        i = 0;
                    }
                }
                stringBuffer2.append(">").append(AntStructure.LINE_SEP);
                printWriter.println(stringBuffer2);
                int size2 = vector.size();
                for (int i5 = 0; i5 < size2; i5++) {
                    String str4 = (String) vector.elementAt(i5);
                    if (!"#PCDATA".equals(str4) && !TASKS.equals(str4) && !TYPES.equals(str4)) {
                        printElementDecl(printWriter, project, str4, helper.getElementType(str4));
                    }
                }
            } catch (Throwable unused5) {
            }
        }

        public static final boolean isNmtoken(String str) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char cCharAt = str.charAt(i);
                if (!Character.isLetterOrDigit(cCharAt) && cCharAt != '.' && cCharAt != '-' && cCharAt != '_' && cCharAt != ':') {
                    return false;
                }
            }
            return true;
        }

        public static final boolean areNmtokens(String[] strArr) {
            for (String str : strArr) {
                if (!isNmtoken(str)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected boolean isNmtoken(String str) {
        return DTDPrinter.isNmtoken(str);
    }

    protected boolean areNmtokens(String[] strArr) {
        return DTDPrinter.areNmtokens(strArr);
    }
}

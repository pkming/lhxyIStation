package org.apache.poi.dev;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.taskdefs.optional.clearcase.CCCheckout;
import org.apache.xalan.xslt.Process;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes3.dex */
public class RecordGenerator {
    public static void main(String[] strArr) throws Exception {
        Class.forName("org.apache.poi.generator.FieldIterator");
        if (strArr.length != 4) {
            System.out.println("Usage:");
            System.out.println("  java org.apache.poi.hssf.util.RecordGenerator RECORD_DEFINTIONS RECORD_STYLES DEST_SRC_PATH TEST_SRC_PATH");
        } else {
            generateRecords(strArr[0], strArr[1], strArr[2], strArr[3]);
        }
    }

    private static void generateRecords(String str, String str2, String str3, String str4) throws Exception {
        int i;
        File file = new File(str);
        int i2 = 0;
        int i3 = 0;
        while (i3 < file.listFiles().length) {
            File file2 = file.listFiles()[i3];
            if (file2.isFile() && (file2.getName().endsWith("_record.xml") || file2.getName().endsWith("_type.xml"))) {
                Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file2).getDocumentElement();
                String nodeValue = documentElement.getElementsByTagName("extends").item(i2).getFirstChild().getNodeValue();
                String nodeValue2 = documentElement.getElementsByTagName("suffix").item(i2).getFirstChild().getNodeValue();
                String nodeValue3 = documentElement.getAttributes().getNamedItem("name").getNodeValue();
                String strReplace = documentElement.getAttributes().getNamedItem("package").getNodeValue().replace('.', '/');
                String string = new StringBuffer().append(str3).append("/").append(strReplace).toString();
                new File(string).mkdirs();
                String string2 = new StringBuffer().append(string).append("/").append(nodeValue3).append(nodeValue2).append(".java").toString();
                String[] strArr = new String[7];
                strArr[i2] = "-in";
                strArr[1] = file2.getAbsolutePath();
                strArr[2] = "-xsl";
                strArr[3] = new StringBuffer().append(str2).append("/").append(nodeValue.toLowerCase()).append(".xsl").toString();
                strArr[4] = CCCheckout.FLAG_OUT;
                strArr[5] = string2;
                strArr[6] = "-TEXT";
                Process.main(strArr);
                System.out.println(new StringBuffer().append("Generated ").append(nodeValue2).append(": ").append(string2).toString());
                String string3 = new StringBuffer().append(str4).append("/").append(strReplace).toString();
                new File(string3).mkdirs();
                String string4 = new StringBuffer().append(string3).append("/Test").append(nodeValue3).append(nodeValue2).append(".java").toString();
                if (!new File(string4).exists()) {
                    i = 0;
                    Process.main(new String[]{"-in", file2.getAbsolutePath(), "-xsl", new StringBuffer().append(str2).append("/").append(nodeValue.toLowerCase()).append("_test.xsl").toString(), CCCheckout.FLAG_OUT, string4, "-TEXT"});
                    System.out.println(new StringBuffer().append("Generated test: ").append(string4).toString());
                } else {
                    i = 0;
                    System.out.println(new StringBuffer().append("Skipped test generation: ").append(string4).toString());
                }
            } else {
                i = i2;
            }
            i3++;
            i2 = i;
        }
    }
}

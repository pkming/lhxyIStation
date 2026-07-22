package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class KeySubst extends Task {
    private File source = null;
    private File dest = null;
    private String sep = "*";
    private Hashtable<String, String> replacements = new Hashtable<>();

    @Override // org.apache.tools.ant.Task
    public void execute() throws Throwable {
        BufferedWriter bufferedWriter;
        Throwable th;
        BufferedReader bufferedReader;
        IOException e;
        log("!! KeySubst is deprecated. Use Filter + Copy instead. !!");
        log("Performing Substitutions");
        if (this.source == null || this.dest == null) {
            log("Source and destinations must not be null");
            return;
        }
        try {
            bufferedReader = new BufferedReader(new FileReader(this.source));
        } catch (IOException e2) {
            bufferedWriter = null;
            e = e2;
            bufferedReader = null;
        } catch (Throwable th2) {
            bufferedWriter = null;
            th = th2;
            bufferedReader = null;
        }
        try {
            this.dest.delete();
            bufferedWriter = new BufferedWriter(new FileWriter(this.dest));
            try {
                try {
                    for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                        if (line.length() == 0) {
                            bufferedWriter.newLine();
                        } else {
                            bufferedWriter.write(replace(line, this.replacements));
                            bufferedWriter.newLine();
                        }
                    }
                    bufferedWriter.flush();
                } catch (IOException e3) {
                    e = e3;
                    e.printStackTrace();
                }
            } catch (Throwable th3) {
                th = th3;
                FileUtils.close(bufferedWriter);
                FileUtils.close(bufferedReader);
                throw th;
            }
        } catch (IOException e4) {
            bufferedWriter = null;
            e = e4;
        } catch (Throwable th4) {
            bufferedWriter = null;
            th = th4;
            FileUtils.close(bufferedWriter);
            FileUtils.close(bufferedReader);
            throw th;
        }
        FileUtils.close(bufferedWriter);
        FileUtils.close(bufferedReader);
    }

    public void setSrc(File file) {
        this.source = file;
    }

    public void setDest(File file) {
        this.dest = file;
    }

    public void setSep(String str) {
        this.sep = str;
    }

    public void setKeys(String str) {
        if (str == null || str.length() <= 0) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(str, this.sep, false);
        while (stringTokenizer.hasMoreTokens()) {
            StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken().trim(), "=", false);
            this.replacements.put(stringTokenizer2.nextToken(), stringTokenizer2.nextToken());
        }
    }

    public static void main(String[] strArr) {
        try {
            Hashtable hashtable = new Hashtable();
            hashtable.put("VERSION", "1.0.3");
            hashtable.put("b", "ffff");
            System.out.println(replace("$f ${VERSION} f ${b} jj $", hashtable));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String replace(String str, Hashtable<String, String> hashtable) throws BuildException {
        StringBuffer stringBuffer = new StringBuffer();
        int length = 0;
        while (true) {
            int iIndexOf = str.indexOf("${", length);
            if (iIndexOf > -1) {
                int i = iIndexOf + 3;
                String strSubstring = str.substring(iIndexOf + 2, str.indexOf("}", i));
                stringBuffer.append(str.substring(length, iIndexOf));
                if (hashtable.containsKey(strSubstring)) {
                    stringBuffer.append(hashtable.get(strSubstring));
                } else {
                    stringBuffer.append("${");
                    stringBuffer.append(strSubstring);
                    stringBuffer.append("}");
                }
                length = strSubstring.length() + i;
            } else {
                stringBuffer.append(str.substring(length));
                return stringBuffer.toString();
            }
        }
    }
}

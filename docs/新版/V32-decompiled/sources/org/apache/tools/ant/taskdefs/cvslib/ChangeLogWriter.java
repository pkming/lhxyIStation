package org.apache.tools.ant.taskdefs.cvslib;

import android.text.format.Time;
import androidx.core.app.NotificationCompat;
import com.lianhexinye.m90.gps.Function;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.TimeZone;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes3.dex */
public class ChangeLogWriter {
    private static final DOMElementWriter DOM_WRITER;
    private static final SimpleDateFormat OUTPUT_DATE;
    private static final SimpleDateFormat OUTPUT_TIME;

    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        OUTPUT_DATE = simpleDateFormat;
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(Function.FORMAT_TIME);
        OUTPUT_TIME = simpleDateFormat2;
        DOM_WRITER = new DOMElementWriter();
        TimeZone timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC);
        simpleDateFormat.setTimeZone(timeZone);
        simpleDateFormat2.setTimeZone(timeZone);
    }

    public void printChangeLog(PrintWriter printWriter, CVSEntry[] cVSEntryArr) {
        try {
            printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            Document documentNewDocument = DOMUtils.newDocument();
            Element elementCreateElement = documentNewDocument.createElement("changelog");
            DOM_WRITER.openElement(elementCreateElement, printWriter, 0, "\t");
            printWriter.println();
            for (CVSEntry cVSEntry : cVSEntryArr) {
                printEntry(documentNewDocument, printWriter, cVSEntry);
            }
            DOM_WRITER.closeElement(elementCreateElement, printWriter, 0, "\t", true);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void printEntry(Document document, PrintWriter printWriter, CVSEntry cVSEntry) throws IOException {
        Element elementCreateElement = document.createElement("entry");
        DOMUtils.appendTextElement(elementCreateElement, "date", OUTPUT_DATE.format(cVSEntry.getDate()));
        DOMUtils.appendTextElement(elementCreateElement, "time", OUTPUT_TIME.format(cVSEntry.getDate()));
        DOMUtils.appendCDATAElement(elementCreateElement, "author", cVSEntry.getAuthor());
        Enumeration enumerationElements = cVSEntry.getFiles().elements();
        while (enumerationElements.hasMoreElements()) {
            RCSFile rCSFile = (RCSFile) enumerationElements.nextElement();
            Element elementCreateChildElement = DOMUtils.createChildElement(elementCreateElement, "file");
            DOMUtils.appendCDATAElement(elementCreateChildElement, "name", rCSFile.getName());
            DOMUtils.appendTextElement(elementCreateChildElement, "revision", rCSFile.getRevision());
            String previousRevision = rCSFile.getPreviousRevision();
            if (previousRevision != null) {
                DOMUtils.appendTextElement(elementCreateChildElement, "prevrevision", previousRevision);
            }
        }
        DOMUtils.appendCDATAElement(elementCreateElement, NotificationCompat.CATEGORY_MESSAGE, cVSEntry.getComment());
        DOM_WRITER.write(elementCreateElement, printWriter, 1, "\t");
    }
}

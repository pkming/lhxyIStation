package org.apache.tools.ant;

import android.provider.DocumentsContract;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: classes3.dex */
public class XmlLogger implements BuildLogger {
    private static final String BUILD_TAG = "build";
    private static final String ERROR_ATTR = "error";
    private static final String LOCATION_ATTR = "location";
    private static final String MESSAGE_TAG = "message";
    private static final String NAME_ATTR = "name";
    private static final String PRIORITY_ATTR = "priority";
    private static final String STACKTRACE_TAG = "stacktrace";
    private static final String TARGET_TAG = "target";
    private static final String TASK_TAG = "task";
    private static final String TIME_ATTR = "time";
    private static DocumentBuilder builder = getDocumentBuilder();
    private PrintStream outStream;
    private int msgOutputLevel = 4;
    private Document doc = builder.newDocument();
    private Hashtable<Task, TimedElement> tasks = new Hashtable<>();
    private Hashtable<Target, TimedElement> targets = new Hashtable<>();
    private Hashtable<Thread, Stack<TimedElement>> threadStacks = new Hashtable<>();
    private TimedElement buildElement = null;

    @Override // org.apache.tools.ant.BuildLogger
    public void setEmacsMode(boolean z) {
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setErrorPrintStream(PrintStream printStream) {
    }

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static class TimedElement {
        private Element element;
        private long startTime;

        private TimedElement() {
        }

        public String toString() {
            return this.element.getTagName() + ":" + this.element.getAttribute("name");
        }
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildStarted(BuildEvent buildEvent) {
        TimedElement timedElement = new TimedElement();
        this.buildElement = timedElement;
        timedElement.startTime = System.currentTimeMillis();
        this.buildElement.element = this.doc.createElement(BUILD_TAG);
    }

    @Override // org.apache.tools.ant.BuildListener
    public void buildFinished(BuildEvent buildEvent) throws Throwable {
        OutputStreamWriter outputStreamWriter;
        this.buildElement.element.setAttribute("time", DefaultLogger.formatTime(System.currentTimeMillis() - this.buildElement.startTime));
        if (buildEvent.getException() != null) {
            this.buildElement.element.setAttribute("error", buildEvent.getException().toString());
            CDATASection cDATASectionCreateCDATASection = this.doc.createCDATASection(StringUtils.getStackTrace(buildEvent.getException()));
            Element elementCreateElement = this.doc.createElement(STACKTRACE_TAG);
            elementCreateElement.appendChild(cDATASectionCreateCDATASection);
            synchronizedAppend(this.buildElement.element, elementCreateElement);
        }
        String property = buildEvent.getProject().getProperty("XmlLogger.file");
        if (property == null) {
            property = "log.xml";
        }
        String property2 = buildEvent.getProject().getProperty("ant.XmlLogger.stylesheet.uri");
        if (property2 == null) {
            property2 = "log.xsl";
        }
        OutputStreamWriter outputStreamWriter2 = null;
        try {
            try {
                OutputStream fileOutputStream = this.outStream;
                if (fileOutputStream == null) {
                    fileOutputStream = new FileOutputStream(property);
                }
                outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            if (property2.length() > 0) {
                outputStreamWriter.write("<?xml-stylesheet type=\"text/xsl\" href=\"" + property2 + "\"?>\n\n");
            }
            new DOMElementWriter().write(this.buildElement.element, outputStreamWriter, 0, "\t");
            outputStreamWriter.flush();
            FileUtils.close(outputStreamWriter);
            this.buildElement = null;
        } catch (IOException e2) {
            e = e2;
            throw new BuildException("Unable to write log file", e);
        } catch (Throwable th2) {
            th = th2;
            outputStreamWriter2 = outputStreamWriter;
            FileUtils.close(outputStreamWriter2);
            throw th;
        }
    }

    private Stack<TimedElement> getStack() {
        Stack<TimedElement> stack = this.threadStacks.get(Thread.currentThread());
        if (stack != null) {
            return stack;
        }
        Stack<TimedElement> stack2 = new Stack<>();
        this.threadStacks.put(Thread.currentThread(), stack2);
        return stack2;
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetStarted(BuildEvent buildEvent) {
        Target target = buildEvent.getTarget();
        TimedElement timedElement = new TimedElement();
        timedElement.startTime = System.currentTimeMillis();
        timedElement.element = this.doc.createElement(TARGET_TAG);
        timedElement.element.setAttribute("name", target.getName());
        this.targets.put(target, timedElement);
        getStack().push(timedElement);
    }

    @Override // org.apache.tools.ant.BuildListener
    public void targetFinished(BuildEvent buildEvent) {
        Target target = buildEvent.getTarget();
        TimedElement timedElement = this.targets.get(target);
        if (timedElement != null) {
            timedElement.element.setAttribute("time", DefaultLogger.formatTime(System.currentTimeMillis() - timedElement.startTime));
            TimedElement timedElementPeek = null;
            Stack<TimedElement> stack = getStack();
            if (!stack.empty()) {
                TimedElement timedElementPop = stack.pop();
                if (timedElementPop != timedElement) {
                    throw new RuntimeException("Mismatch - popped element = " + timedElementPop + " finished target element = " + timedElement);
                }
                if (!stack.empty()) {
                    timedElementPeek = stack.peek();
                }
            }
            if (timedElementPeek == null) {
                synchronizedAppend(this.buildElement.element, timedElement.element);
            } else {
                synchronizedAppend(timedElementPeek.element, timedElement.element);
            }
        }
        this.targets.remove(target);
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskStarted(BuildEvent buildEvent) {
        TimedElement timedElement = new TimedElement();
        timedElement.startTime = System.currentTimeMillis();
        timedElement.element = this.doc.createElement(TASK_TAG);
        Task task = buildEvent.getTask();
        String taskName = buildEvent.getTask().getTaskName();
        if (taskName == null) {
            taskName = "";
        }
        timedElement.element.setAttribute("name", taskName);
        timedElement.element.setAttribute("location", buildEvent.getTask().getLocation().toString());
        this.tasks.put(task, timedElement);
        getStack().push(timedElement);
    }

    @Override // org.apache.tools.ant.BuildListener
    public void taskFinished(BuildEvent buildEvent) {
        TimedElement timedElementPop;
        Task task = buildEvent.getTask();
        TimedElement timedElement = this.tasks.get(task);
        if (timedElement != null) {
            timedElement.element.setAttribute("time", DefaultLogger.formatTime(System.currentTimeMillis() - timedElement.startTime));
            Target owningTarget = task.getOwningTarget();
            TimedElement timedElement2 = owningTarget != null ? this.targets.get(owningTarget) : null;
            if (timedElement2 == null) {
                synchronizedAppend(this.buildElement.element, timedElement.element);
            } else {
                synchronizedAppend(timedElement2.element, timedElement.element);
            }
            Stack<TimedElement> stack = getStack();
            if (!stack.empty() && (timedElementPop = stack.pop()) != timedElement) {
                throw new RuntimeException("Mismatch - popped element = " + timedElementPop + " finished task element = " + timedElement);
            }
            this.tasks.remove(task);
            return;
        }
        throw new RuntimeException("Unknown task " + task + " not in " + this.tasks);
    }

    private TimedElement getTaskElement(Task task) {
        TimedElement timedElement = this.tasks.get(task);
        if (timedElement != null) {
            return timedElement;
        }
        Enumeration<Task> enumerationKeys = this.tasks.keys();
        while (enumerationKeys.hasMoreElements()) {
            Task taskNextElement = enumerationKeys.nextElement();
            if ((taskNextElement instanceof UnknownElement) && ((UnknownElement) taskNextElement).getTask() == task) {
                return this.tasks.get(taskNextElement);
            }
        }
        return null;
    }

    @Override // org.apache.tools.ant.BuildListener
    public void messageLogged(BuildEvent buildEvent) {
        int priority = buildEvent.getPriority();
        if (priority > this.msgOutputLevel) {
            return;
        }
        Element elementCreateElement = this.doc.createElement(MESSAGE_TAG);
        elementCreateElement.setAttribute("priority", priority != 0 ? priority != 1 ? priority != 2 ? "debug" : DocumentsContract.EXTRA_INFO : "warn" : "error");
        Throwable exception = buildEvent.getException();
        if (4 <= this.msgOutputLevel && exception != null) {
            CDATASection cDATASectionCreateCDATASection = this.doc.createCDATASection(StringUtils.getStackTrace(exception));
            Element elementCreateElement2 = this.doc.createElement(STACKTRACE_TAG);
            elementCreateElement2.appendChild(cDATASectionCreateCDATASection);
            synchronizedAppend(this.buildElement.element, elementCreateElement2);
        }
        elementCreateElement.appendChild(this.doc.createCDATASection(buildEvent.getMessage()));
        Task task = buildEvent.getTask();
        Target target = buildEvent.getTarget();
        TimedElement taskElement = task != null ? getTaskElement(task) : null;
        if (taskElement == null && target != null) {
            taskElement = this.targets.get(target);
        }
        if (taskElement != null) {
            synchronizedAppend(taskElement.element, elementCreateElement);
        } else {
            synchronizedAppend(this.buildElement.element, elementCreateElement);
        }
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setMessageOutputLevel(int i) {
        this.msgOutputLevel = i;
    }

    @Override // org.apache.tools.ant.BuildLogger
    public void setOutputPrintStream(PrintStream printStream) {
        this.outStream = new PrintStream((OutputStream) printStream, true);
    }

    private void synchronizedAppend(Node node, Node node2) {
        synchronized (node) {
            node.appendChild(node2);
        }
    }
}

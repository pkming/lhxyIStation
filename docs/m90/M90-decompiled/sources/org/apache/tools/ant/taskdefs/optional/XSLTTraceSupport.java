package org.apache.tools.ant.taskdefs.optional;

import javax.xml.transform.Transformer;
import org.apache.tools.ant.taskdefs.XSLTProcess;

/* JADX INFO: loaded from: classes3.dex */
public interface XSLTTraceSupport {
    void configureTrace(Transformer transformer, XSLTProcess.TraceConfiguration traceConfiguration);
}

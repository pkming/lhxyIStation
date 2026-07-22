package org.apache.tools.ant.input;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public class GreedyInputHandler extends DefaultInputHandler {
    @Override // org.apache.tools.ant.input.DefaultInputHandler, org.apache.tools.ant.input.InputHandler
    public void handleInput(InputRequest inputRequest) throws Throwable {
        InputStream inputStream;
        String prompt = getPrompt(inputRequest);
        try {
            inputStream = getInputStream();
            try {
                System.err.println(prompt);
                System.err.flush();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                StreamPumper streamPumper = new StreamPumper(inputStream, byteArrayOutputStream);
                Thread thread = new Thread(streamPumper);
                thread.start();
                try {
                    try {
                        thread.join();
                    } catch (InterruptedException unused) {
                    }
                } catch (InterruptedException unused2) {
                    thread.join();
                }
                inputRequest.setInput(new String(byteArrayOutputStream.toByteArray()));
                if (!inputRequest.isInputValid()) {
                    throw new BuildException("Received invalid console input");
                }
                if (streamPumper.getException() != null) {
                    throw new BuildException("Failed to read input from console", streamPumper.getException());
                }
                FileUtils.close(inputStream);
            } catch (Throwable th) {
                th = th;
                FileUtils.close(inputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            inputStream = null;
        }
    }
}

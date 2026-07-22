package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class PumpStreamHandler implements ExecuteStreamHandler {
    private static final long JOIN_TIMEOUT = 200;
    private OutputStream err;
    private Thread errorThread;
    private InputStream input;
    private Thread inputThread;
    private final boolean nonBlockingRead;
    private OutputStream out;
    private Thread outputThread;

    public PumpStreamHandler(OutputStream outputStream, OutputStream outputStream2, InputStream inputStream, boolean z) {
        this.out = outputStream;
        this.err = outputStream2;
        this.input = inputStream;
        this.nonBlockingRead = z;
    }

    public PumpStreamHandler(OutputStream outputStream, OutputStream outputStream2, InputStream inputStream) {
        this(outputStream, outputStream2, inputStream, false);
    }

    public PumpStreamHandler(OutputStream outputStream, OutputStream outputStream2) {
        this(outputStream, outputStream2, null);
    }

    public PumpStreamHandler(OutputStream outputStream) {
        this(outputStream, outputStream);
    }

    public PumpStreamHandler() {
        this(System.out, System.err);
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessOutputStream(InputStream inputStream) {
        createProcessOutputPump(inputStream, this.out);
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessErrorStream(InputStream inputStream) {
        OutputStream outputStream = this.err;
        if (outputStream != null) {
            createProcessErrorPump(inputStream, outputStream);
        }
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void setProcessInputStream(OutputStream outputStream) {
        InputStream inputStream = this.input;
        if (inputStream != null) {
            this.inputThread = createPump(inputStream, outputStream, true, this.nonBlockingRead);
        } else {
            try {
                outputStream.close();
            } catch (IOException unused) {
            }
        }
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void start() {
        this.outputThread.start();
        this.errorThread.start();
        Thread thread = this.inputThread;
        if (thread != null) {
            thread.start();
        }
    }

    @Override // org.apache.tools.ant.taskdefs.ExecuteStreamHandler
    public void stop() {
        finish(this.inputThread);
        try {
            this.err.flush();
        } catch (IOException unused) {
        }
        try {
            this.out.flush();
        } catch (IOException unused2) {
        }
        finish(this.outputThread);
        finish(this.errorThread);
    }

    protected final void finish(Thread thread) {
        if (thread == null) {
            return;
        }
        try {
            StreamPumper pumper = thread instanceof ThreadWithPumper ? ((ThreadWithPumper) thread).getPumper() : null;
            if ((pumper != null && pumper.isFinished()) || !thread.isAlive()) {
                return;
            }
            if (pumper != null && !pumper.isFinished()) {
                pumper.stop();
            }
            thread.join(JOIN_TIMEOUT);
            while (true) {
                if (pumper != null) {
                    if (pumper.isFinished()) {
                        return;
                    }
                }
                if (!thread.isAlive()) {
                    return;
                }
                thread.interrupt();
                thread.join(JOIN_TIMEOUT);
            }
        } catch (InterruptedException unused) {
        }
    }

    protected OutputStream getErr() {
        return this.err;
    }

    protected OutputStream getOut() {
        return this.out;
    }

    protected void createProcessOutputPump(InputStream inputStream, OutputStream outputStream) {
        this.outputThread = createPump(inputStream, outputStream);
    }

    protected void createProcessErrorPump(InputStream inputStream, OutputStream outputStream) {
        this.errorThread = createPump(inputStream, outputStream);
    }

    protected Thread createPump(InputStream inputStream, OutputStream outputStream) {
        return createPump(inputStream, outputStream, false);
    }

    protected Thread createPump(InputStream inputStream, OutputStream outputStream, boolean z) {
        return createPump(inputStream, outputStream, z, true);
    }

    protected Thread createPump(InputStream inputStream, OutputStream outputStream, boolean z, boolean z2) {
        StreamPumper streamPumper = new StreamPumper(inputStream, outputStream, z, z2);
        streamPumper.setAutoflush(true);
        ThreadWithPumper threadWithPumper = new ThreadWithPumper(streamPumper);
        threadWithPumper.setDaemon(true);
        return threadWithPumper;
    }

    protected static class ThreadWithPumper extends Thread {
        private final StreamPumper pumper;

        public ThreadWithPumper(StreamPumper streamPumper) {
            super(streamPumper);
            this.pumper = streamPumper;
        }

        protected StreamPumper getPumper() {
            return this.pumper;
        }
    }
}

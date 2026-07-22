package android.speech.tts;

import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/* JADX INFO: loaded from: classes.dex */
class FileSynthesisCallback extends AbstractSynthesisCallback {
    private static final boolean DBG = false;
    private static final int MAX_AUDIO_BUFFER_SIZE = 8192;
    private static final String TAG = "FileSynthesisRequest";
    private static final short WAV_FORMAT_PCM = 1;
    private static final int WAV_HEADER_LENGTH = 44;
    private int mAudioFormat;
    private int mChannelCount;
    private FileChannel mFileChannel;
    private int mSampleRateInHz;
    private final Object mStateLock = new Object();
    private boolean mStarted = false;
    private boolean mStopped = false;
    private boolean mDone = false;

    @Override // android.speech.tts.SynthesisCallback
    public int getMaxBufferSize() {
        return 8192;
    }

    FileSynthesisCallback(FileChannel fileChannel) {
        this.mFileChannel = fileChannel;
    }

    @Override // android.speech.tts.AbstractSynthesisCallback
    void stop() {
        synchronized (this.mStateLock) {
            this.mStopped = true;
            cleanUp();
        }
    }

    private void cleanUp() {
        closeFile();
    }

    private void closeFile() {
        try {
            FileChannel fileChannel = this.mFileChannel;
            if (fileChannel != null) {
                fileChannel.close();
                this.mFileChannel = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close output file descriptor", e);
        }
    }

    @Override // android.speech.tts.AbstractSynthesisCallback
    boolean isDone() {
        return this.mDone;
    }

    @Override // android.speech.tts.SynthesisCallback
    public int start(int i, int i2, int i3) {
        synchronized (this.mStateLock) {
            if (this.mStopped) {
                return -1;
            }
            if (this.mStarted) {
                cleanUp();
                throw new IllegalArgumentException("FileSynthesisRequest.start() called twice");
            }
            this.mStarted = true;
            this.mSampleRateInHz = i;
            this.mAudioFormat = i2;
            this.mChannelCount = i3;
            try {
                this.mFileChannel.write(ByteBuffer.allocate(44));
                return 0;
            } catch (IOException e) {
                Log.e(TAG, "Failed to write wav header to output file descriptor" + e);
                cleanUp();
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int audioAvailable(byte[] bArr, int i, int i2) {
        synchronized (this.mStateLock) {
            if (this.mStopped) {
                return -1;
            }
            FileChannel fileChannel = this.mFileChannel;
            if (fileChannel == null) {
                Log.e(TAG, "File not open");
                return -1;
            }
            try {
                fileChannel.write(ByteBuffer.wrap(bArr, i, i2));
                return 0;
            } catch (IOException e) {
                Log.e(TAG, "Failed to write to output file descriptor", e);
                cleanUp();
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int done() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                return -1;
            }
            if (this.mStopped) {
                return -1;
            }
            FileChannel fileChannel = this.mFileChannel;
            if (fileChannel == null) {
                Log.e(TAG, "File not open");
                return -1;
            }
            try {
                fileChannel.position(0L);
                this.mFileChannel.write(makeWavHeader(this.mSampleRateInHz, this.mAudioFormat, this.mChannelCount, (int) (this.mFileChannel.size() - 44)));
                closeFile();
                this.mDone = true;
                return 0;
            } catch (IOException e) {
                Log.e(TAG, "Failed to write to output file descriptor", e);
                cleanUp();
                return -1;
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error() {
        synchronized (this.mStateLock) {
            cleanUp();
        }
    }

    private ByteBuffer makeWavHeader(int i, int i2, int i3, int i4) {
        int i5 = i2 == 3 ? 1 : 2;
        ByteBuffer byteBufferWrap = ByteBuffer.wrap(new byte[44]);
        byteBufferWrap.order(ByteOrder.LITTLE_ENDIAN);
        byteBufferWrap.put(new byte[]{82, 73, 70, 70});
        byteBufferWrap.putInt((i4 + 44) - 8);
        byteBufferWrap.put(new byte[]{87, 65, 86, 69});
        byteBufferWrap.put(new byte[]{102, 109, 116, 32});
        byteBufferWrap.putInt(16);
        byteBufferWrap.putShort((short) 1);
        byteBufferWrap.putShort((short) i3);
        byteBufferWrap.putInt(i);
        byteBufferWrap.putInt(i * i5 * i3);
        byteBufferWrap.putShort((short) (i5 * i3));
        byteBufferWrap.putShort((short) (i5 * 8));
        byteBufferWrap.put(new byte[]{100, 97, 116, 97});
        byteBufferWrap.putInt(i4);
        byteBufferWrap.flip();
        return byteBufferWrap;
    }
}

package android.speech.srec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class WaveHeader {
    public static final short FORMAT_ALAW = 6;
    public static final short FORMAT_PCM = 1;
    public static final short FORMAT_ULAW = 7;
    private static final int HEADER_LENGTH = 44;
    private static final String TAG = "WaveHeader";
    private short mBitsPerSample;
    private short mFormat;
    private int mNumBytes;
    private short mNumChannels;
    private int mSampleRate;

    public WaveHeader() {
    }

    public WaveHeader(short s, short s2, int i, short s3, int i2) {
        this.mFormat = s;
        this.mSampleRate = i;
        this.mNumChannels = s2;
        this.mBitsPerSample = s3;
        this.mNumBytes = i2;
    }

    public short getFormat() {
        return this.mFormat;
    }

    public WaveHeader setFormat(short s) {
        this.mFormat = s;
        return this;
    }

    public short getNumChannels() {
        return this.mNumChannels;
    }

    public WaveHeader setNumChannels(short s) {
        this.mNumChannels = s;
        return this;
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public WaveHeader setSampleRate(int i) {
        this.mSampleRate = i;
        return this;
    }

    public short getBitsPerSample() {
        return this.mBitsPerSample;
    }

    public WaveHeader setBitsPerSample(short s) {
        this.mBitsPerSample = s;
        return this;
    }

    public int getNumBytes() {
        return this.mNumBytes;
    }

    public WaveHeader setNumBytes(int i) {
        this.mNumBytes = i;
        return this;
    }

    public int read(InputStream inputStream) throws IOException {
        readId(inputStream, "RIFF");
        readInt(inputStream);
        readId(inputStream, "WAVE");
        readId(inputStream, "fmt ");
        if (16 != readInt(inputStream)) {
            throw new IOException("fmt chunk length not 16");
        }
        this.mFormat = readShort(inputStream);
        this.mNumChannels = readShort(inputStream);
        this.mSampleRate = readInt(inputStream);
        int i = readInt(inputStream);
        short s = readShort(inputStream);
        short s2 = readShort(inputStream);
        this.mBitsPerSample = s2;
        short s3 = this.mNumChannels;
        if (i != ((this.mSampleRate * s3) * s2) / 8) {
            throw new IOException("fmt.ByteRate field inconsistent");
        }
        if (s != (s3 * s2) / 8) {
            throw new IOException("fmt.BlockAlign field inconsistent");
        }
        readId(inputStream, "data");
        this.mNumBytes = readInt(inputStream);
        return 44;
    }

    private static void readId(InputStream inputStream, String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != inputStream.read()) {
                throw new IOException(str + " tag not present");
            }
        }
    }

    private static int readInt(InputStream inputStream) throws IOException {
        return (inputStream.read() << 24) | inputStream.read() | (inputStream.read() << 8) | (inputStream.read() << 16);
    }

    private static short readShort(InputStream inputStream) throws IOException {
        return (short) ((inputStream.read() << 8) | inputStream.read());
    }

    public int write(OutputStream outputStream) throws IOException {
        writeId(outputStream, "RIFF");
        writeInt(outputStream, this.mNumBytes + 36);
        writeId(outputStream, "WAVE");
        writeId(outputStream, "fmt ");
        writeInt(outputStream, 16);
        writeShort(outputStream, this.mFormat);
        writeShort(outputStream, this.mNumChannels);
        writeInt(outputStream, this.mSampleRate);
        writeInt(outputStream, ((this.mNumChannels * this.mSampleRate) * this.mBitsPerSample) / 8);
        writeShort(outputStream, (short) ((this.mNumChannels * this.mBitsPerSample) / 8));
        writeShort(outputStream, this.mBitsPerSample);
        writeId(outputStream, "data");
        writeInt(outputStream, this.mNumBytes);
        return 44;
    }

    private static void writeId(OutputStream outputStream, String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            outputStream.write(str.charAt(i));
        }
    }

    private static void writeInt(OutputStream outputStream, int i) throws IOException {
        outputStream.write(i >> 0);
        outputStream.write(i >> 8);
        outputStream.write(i >> 16);
        outputStream.write(i >> 24);
    }

    private static void writeShort(OutputStream outputStream, short s) throws IOException {
        outputStream.write(s >> 0);
        outputStream.write(s >> 8);
    }

    public String toString() {
        return String.format("WaveHeader format=%d numChannels=%d sampleRate=%d bitsPerSample=%d numBytes=%d", Short.valueOf(this.mFormat), Short.valueOf(this.mNumChannels), Integer.valueOf(this.mSampleRate), Short.valueOf(this.mBitsPerSample), Integer.valueOf(this.mNumBytes));
    }
}

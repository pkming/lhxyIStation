package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/* JADX INFO: loaded from: classes3.dex */
class NioZipEncoding implements ZipEncoding {
    private final Charset charset;

    public NioZipEncoding(Charset charset) {
        this.charset = charset;
    }

    @Override // org.apache.tools.zip.ZipEncoding
    public boolean canEncode(String str) {
        CharsetEncoder charsetEncoderNewEncoder = this.charset.newEncoder();
        charsetEncoderNewEncoder.onMalformedInput(CodingErrorAction.REPORT);
        charsetEncoderNewEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        return charsetEncoderNewEncoder.canEncode(str);
    }

    @Override // org.apache.tools.zip.ZipEncoding
    public ByteBuffer encode(String str) {
        CharsetEncoder charsetEncoderNewEncoder = this.charset.newEncoder();
        charsetEncoderNewEncoder.onMalformedInput(CodingErrorAction.REPORT);
        charsetEncoderNewEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer charBufferWrap = CharBuffer.wrap(str);
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(str.length() + ((str.length() + 1) / 2));
        while (true) {
            if (charBufferWrap.remaining() <= 0) {
                break;
            }
            CoderResult coderResultEncode = charsetEncoderNewEncoder.encode(charBufferWrap, byteBufferAllocate, true);
            if (coderResultEncode.isUnmappable() || coderResultEncode.isMalformed()) {
                if (coderResultEncode.length() * 6 > byteBufferAllocate.remaining()) {
                    byteBufferAllocate = ZipEncodingHelper.growBuffer(byteBufferAllocate, byteBufferAllocate.position() + (coderResultEncode.length() * 6));
                }
                for (int i = 0; i < coderResultEncode.length(); i++) {
                    ZipEncodingHelper.appendSurrogate(byteBufferAllocate, charBufferWrap.get());
                }
            } else if (coderResultEncode.isOverflow()) {
                byteBufferAllocate = ZipEncodingHelper.growBuffer(byteBufferAllocate, 0);
            } else if (coderResultEncode.isUnderflow()) {
                charsetEncoderNewEncoder.flush(byteBufferAllocate);
                break;
            }
        }
        byteBufferAllocate.limit(byteBufferAllocate.position());
        byteBufferAllocate.rewind();
        return byteBufferAllocate;
    }

    @Override // org.apache.tools.zip.ZipEncoding
    public String decode(byte[] bArr) throws IOException {
        return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bArr)).toString();
    }
}

package android.net.wifi;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class WifiSsid implements Parcelable {
    public static final Parcelable.Creator<WifiSsid> CREATOR = new Parcelable.Creator<WifiSsid>() { // from class: android.net.wifi.WifiSsid.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiSsid createFromParcel(Parcel parcel) {
            WifiSsid wifiSsid = new WifiSsid();
            int i = parcel.readInt();
            byte[] bArr = new byte[i];
            parcel.readByteArray(bArr);
            wifiSsid.octets.write(bArr, 0, i);
            return wifiSsid;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiSsid[] newArray(int i) {
            return new WifiSsid[i];
        }
    };
    private static boolean DBG = false;
    private static final int HEX_RADIX = 16;
    public static final String NONE = "<unknown ssid>";
    private static final String TAG = "WifiSsid";
    public boolean isValidAscii;
    public String mAsciiStr;
    public ByteArrayOutputStream octets;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private WifiSsid() {
        this.octets = new ByteArrayOutputStream(32);
        this.isValidAscii = true;
        this.mAsciiStr = null;
    }

    public static WifiSsid createFromAsciiEncoded(String str) {
        WifiSsid wifiSsid = new WifiSsid();
        wifiSsid.convertToBytes(str);
        return wifiSsid;
    }

    public static WifiSsid createFromHex(String str) {
        int i;
        WifiSsid wifiSsid = new WifiSsid();
        if (str == null) {
            return wifiSsid;
        }
        if (str.startsWith("0x") || str.startsWith("0X")) {
            str = str.substring(2);
        }
        int i2 = 0;
        while (i2 < str.length() - 1) {
            int i3 = i2 + 2;
            try {
                i = Integer.parseInt(str.substring(i2, i3), 16);
            } catch (NumberFormatException unused) {
                i = 0;
            }
            wifiSsid.octets.write(i);
            i2 = i3;
        }
        return wifiSsid;
    }

    private void convertToBytes(String str) {
        int i;
        this.mAsciiStr = str;
        int i2 = 0;
        while (i2 < str.length()) {
            char cCharAt = str.charAt(i2);
            if (cCharAt == '\\') {
                i2++;
                char cCharAt2 = str.charAt(i2);
                if (cCharAt2 == '\"') {
                    this.octets.write(34);
                } else if (cCharAt2 == '\\') {
                    this.octets.write(92);
                } else if (cCharAt2 == 'e') {
                    this.octets.write(27);
                } else if (cCharAt2 == 'n') {
                    this.octets.write(10);
                } else if (cCharAt2 == 'r') {
                    this.octets.write(13);
                } else if (cCharAt2 == 't') {
                    this.octets.write(9);
                } else if (cCharAt2 == 'x') {
                    i2++;
                    int i3 = i2 + 2;
                    try {
                        i = Integer.parseInt(str.substring(i2, i3), 16);
                    } catch (NumberFormatException unused) {
                        i = -1;
                    }
                    if (i < 0) {
                        int iDigit = Character.digit(str.charAt(i2), 16);
                        if (iDigit >= 0) {
                            this.octets.write(iDigit);
                        }
                    } else {
                        this.octets.write(i);
                        i2 = i3;
                    }
                } else {
                    switch (cCharAt2) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                            int iCharAt = str.charAt(i2) - '0';
                            i2++;
                            if (str.charAt(i2) >= '0' && str.charAt(i2) <= '7') {
                                iCharAt = ((iCharAt * 8) + str.charAt(i2)) - 48;
                                i2++;
                            }
                            if (str.charAt(i2) >= '0' && str.charAt(i2) <= '7') {
                                iCharAt = ((iCharAt * 8) + str.charAt(i2)) - 48;
                                i2++;
                            }
                            this.octets.write(iCharAt);
                            break;
                    }
                }
            } else {
                this.octets.write(cCharAt);
            }
            i2++;
        }
    }

    public String toString() {
        byte[] byteArray = this.octets.toByteArray();
        if (this.octets.size() <= 0 || isArrayAllZeroes(byteArray)) {
            return "";
        }
        CharsetDecoder charsetDecoderOnUnmappableCharacter = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer charBufferAllocate = CharBuffer.allocate(32);
        CoderResult coderResultDecode = charsetDecoderOnUnmappableCharacter.decode(ByteBuffer.wrap(byteArray), charBufferAllocate, true);
        charBufferAllocate.flip();
        if (coderResultDecode.isError()) {
            if (DBG) {
                Log.d(TAG, "Try GBK again!!");
            }
            this.isValidAscii = false;
            CharsetDecoder charsetDecoderOnUnmappableCharacter2 = Charset.forName(MediaPlayer.CHARSET_GBK).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            CharBuffer charBufferAllocate2 = CharBuffer.allocate(32);
            CoderResult coderResultDecode2 = charsetDecoderOnUnmappableCharacter2.decode(ByteBuffer.wrap(byteArray), charBufferAllocate2, true);
            charBufferAllocate2.flip();
            if (DBG) {
                for (int i = 0; i < byteArray.length; i++) {
                    Log.d(TAG, "Arigin bytes [" + i + "] = " + Integer.toHexString(byteArray[i]));
                }
            }
            try {
                byteArray = charBufferAllocate2.toString().getBytes(MediaPlayer.CHARSET_GBK);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            if (DBG) {
                for (int i2 = 0; i2 < byteArray.length; i2++) {
                    Log.d(TAG, "After bytes [" + i2 + "] = " + Integer.toHexString(byteArray[i2]));
                }
            }
            charBufferAllocate = charBufferAllocate2;
            coderResultDecode = coderResultDecode2;
        }
        return coderResultDecode.isError() ? NONE : charBufferAllocate.toString();
    }

    private boolean isArrayAllZeroes(byte[] bArr) {
        for (byte b : bArr) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    public byte[] getOctets() {
        return this.octets.toByteArray();
    }

    public String getHexString() {
        byte[] octets = getOctets();
        String str = "0x";
        for (int i = 0; i < this.octets.size(); i++) {
            str = str + String.format(Locale.US, "%02x", Byte.valueOf(octets[i]));
        }
        return str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.octets.size());
        parcel.writeByteArray(this.octets.toByteArray());
    }
}

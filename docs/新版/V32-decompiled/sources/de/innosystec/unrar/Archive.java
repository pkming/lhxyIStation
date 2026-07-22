package de.innosystec.unrar;

import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.io.IReadOnlyAccess;
import de.innosystec.unrar.io.ReadOnlyAccessFile;
import de.innosystec.unrar.rarfile.AVHeader;
import de.innosystec.unrar.rarfile.BaseBlock;
import de.innosystec.unrar.rarfile.BlockHeader;
import de.innosystec.unrar.rarfile.CommentHeader;
import de.innosystec.unrar.rarfile.EAHeader;
import de.innosystec.unrar.rarfile.EndArcHeader;
import de.innosystec.unrar.rarfile.FileHeader;
import de.innosystec.unrar.rarfile.MacInfoHeader;
import de.innosystec.unrar.rarfile.MainHeader;
import de.innosystec.unrar.rarfile.MarkHeader;
import de.innosystec.unrar.rarfile.ProtectHeader;
import de.innosystec.unrar.rarfile.SignHeader;
import de.innosystec.unrar.rarfile.SubBlockHeader;
import de.innosystec.unrar.rarfile.SubBlockHeaderType;
import de.innosystec.unrar.rarfile.UnixOwnersHeader;
import de.innosystec.unrar.rarfile.UnrarHeadertype;
import de.innosystec.unrar.unpack.ComprDataIO;
import de.innosystec.unrar.unpack.Unpack;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: classes2.dex */
public class Archive implements Closeable {
    private static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$rarfile$SubBlockHeaderType;
    private static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$rarfile$UnrarHeadertype;
    private static Logger logger = Logger.getLogger(Archive.class.getName());
    private long arcDataCRC;
    private int currentHeaderIndex;
    private final ComprDataIO dataIO;
    private boolean encrypted;
    private EndArcHeader endHeader;
    private File file;
    private final List<BaseBlock> headers;
    private MarkHeader markHead;
    private MainHeader newMhd;
    private IReadOnlyAccess rof;
    private int sfxSize;
    private long totalPackedRead;
    private long totalPackedSize;
    private Unpack unpack;
    private final UnrarCallback unrarCallback;

    static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$rarfile$SubBlockHeaderType() {
        int[] iArr = $SWITCH_TABLE$de$innosystec$unrar$rarfile$SubBlockHeaderType;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[SubBlockHeaderType.valuesCustom().length];
        try {
            iArr2[SubBlockHeaderType.BEEA_HEAD.ordinal()] = 4;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr2[SubBlockHeaderType.EA_HEAD.ordinal()] = 1;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr2[SubBlockHeaderType.MAC_HEAD.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr2[SubBlockHeaderType.NTACL_HEAD.ordinal()] = 5;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[SubBlockHeaderType.STREAM_HEAD.ordinal()] = 6;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[SubBlockHeaderType.UO_HEAD.ordinal()] = 2;
        } catch (NoSuchFieldError unused6) {
        }
        $SWITCH_TABLE$de$innosystec$unrar$rarfile$SubBlockHeaderType = iArr2;
        return iArr2;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$de$innosystec$unrar$rarfile$UnrarHeadertype() {
        int[] iArr = $SWITCH_TABLE$de$innosystec$unrar$rarfile$UnrarHeadertype;
        if (iArr != null) {
            return iArr;
        }
        int[] iArr2 = new int[UnrarHeadertype.valuesCustom().length];
        try {
            iArr2[UnrarHeadertype.AvHeader.ordinal()] = 5;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr2[UnrarHeadertype.CommHeader.ordinal()] = 4;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr2[UnrarHeadertype.EndArcHeader.ordinal()] = 10;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr2[UnrarHeadertype.FileHeader.ordinal()] = 3;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[UnrarHeadertype.MainHeader.ordinal()] = 1;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[UnrarHeadertype.MarkHeader.ordinal()] = 2;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr2[UnrarHeadertype.NewSubHeader.ordinal()] = 9;
        } catch (NoSuchFieldError unused7) {
        }
        try {
            iArr2[UnrarHeadertype.ProtectHeader.ordinal()] = 7;
        } catch (NoSuchFieldError unused8) {
        }
        try {
            iArr2[UnrarHeadertype.SignHeader.ordinal()] = 8;
        } catch (NoSuchFieldError unused9) {
        }
        try {
            iArr2[UnrarHeadertype.SubHeader.ordinal()] = 6;
        } catch (NoSuchFieldError unused10) {
        }
        $SWITCH_TABLE$de$innosystec$unrar$rarfile$UnrarHeadertype = iArr2;
        return iArr2;
    }

    public Archive(File file) throws RarException, IOException {
        this(file, null);
    }

    public Archive(File file, UnrarCallback unrarCallback) throws RarException, IOException {
        this.headers = new ArrayList();
        this.markHead = null;
        this.newMhd = null;
        this.endHeader = null;
        this.arcDataCRC = -1L;
        this.encrypted = false;
        this.sfxSize = 0;
        this.totalPackedSize = 0L;
        this.totalPackedRead = 0L;
        setFile(file);
        this.unrarCallback = unrarCallback;
        this.dataIO = new ComprDataIO(this);
    }

    public File getFile() {
        return this.file;
    }

    void setFile(File file) throws IOException {
        this.file = file;
        this.totalPackedSize = 0L;
        this.totalPackedRead = 0L;
        close();
        this.rof = new ReadOnlyAccessFile(file);
        try {
            readHeaders();
        } catch (Exception e) {
            logger.log(Level.WARNING, "exception in archive constructor maybe file is encrypted or currupt", (Throwable) e);
        }
        for (BaseBlock baseBlock : this.headers) {
            if (baseBlock.getHeaderType() == UnrarHeadertype.FileHeader) {
                this.totalPackedSize += ((FileHeader) baseBlock).getFullPackSize();
            }
        }
        UnrarCallback unrarCallback = this.unrarCallback;
        if (unrarCallback != null) {
            unrarCallback.volumeProgressChanged(this.totalPackedRead, this.totalPackedSize);
        }
    }

    public void bytesReadRead(int i) {
        if (i > 0) {
            long j = this.totalPackedRead + ((long) i);
            this.totalPackedRead = j;
            UnrarCallback unrarCallback = this.unrarCallback;
            if (unrarCallback != null) {
                unrarCallback.volumeProgressChanged(j, this.totalPackedSize);
            }
        }
    }

    public IReadOnlyAccess getRof() {
        return this.rof;
    }

    public List<FileHeader> getFileHeaders() {
        ArrayList arrayList = new ArrayList();
        for (BaseBlock baseBlock : this.headers) {
            if (baseBlock.getHeaderType().equals(UnrarHeadertype.FileHeader)) {
                arrayList.add((FileHeader) baseBlock);
            }
        }
        return arrayList;
    }

    public FileHeader nextFileHeader() {
        BaseBlock baseBlock;
        int size = this.headers.size();
        do {
            int i = this.currentHeaderIndex;
            if (i >= size) {
                return null;
            }
            List<BaseBlock> list = this.headers;
            this.currentHeaderIndex = i + 1;
            baseBlock = list.get(i);
        } while (baseBlock.getHeaderType() != UnrarHeadertype.FileHeader);
        return (FileHeader) baseBlock;
    }

    public UnrarCallback getUnrarCallback() {
        return this.unrarCallback;
    }

    public boolean isEncrypted() {
        MainHeader mainHeader = this.newMhd;
        Objects.requireNonNull(mainHeader, "mainheader is null");
        return mainHeader.isEncrypted();
    }

    private void readHeaders() throws RarException, IOException {
        EndArcHeader endArcHeader;
        this.markHead = null;
        this.newMhd = null;
        this.endHeader = null;
        this.headers.clear();
        this.currentHeaderIndex = 0;
        long length = this.file.length();
        while (true) {
            byte[] bArr = new byte[7];
            long position = this.rof.getPosition();
            if (position >= length || this.rof.readFully(bArr, 7) == 0) {
                return;
            }
            BaseBlock baseBlock = new BaseBlock(bArr);
            baseBlock.setPositionInFile(position);
            int i = $SWITCH_TABLE$de$innosystec$unrar$rarfile$UnrarHeadertype()[baseBlock.getHeaderType().ordinal()];
            if (i == 1) {
                int i2 = baseBlock.hasEncryptVersion() ? 7 : 6;
                byte[] bArr2 = new byte[i2];
                this.rof.readFully(bArr2, i2);
                MainHeader mainHeader = new MainHeader(baseBlock, bArr2);
                this.headers.add(mainHeader);
                this.newMhd = mainHeader;
                if (mainHeader.isEncrypted()) {
                    throw new RarException(RarException.RarExceptionType.rarEncryptedException);
                }
            } else if (i == 2) {
                MarkHeader markHeader = new MarkHeader(baseBlock);
                this.markHead = markHeader;
                if (!markHeader.isSignature()) {
                    throw new RarException(RarException.RarExceptionType.badRarArchive);
                }
                this.headers.add(this.markHead);
            } else if (i == 4) {
                byte[] bArr3 = new byte[6];
                this.rof.readFully(bArr3, 6);
                CommentHeader commentHeader = new CommentHeader(baseBlock, bArr3);
                this.headers.add(commentHeader);
                this.rof.setPosition(commentHeader.getPositionInFile() + ((long) commentHeader.getHeaderSize()));
            } else if (i == 5) {
                byte[] bArr4 = new byte[7];
                this.rof.readFully(bArr4, 7);
                this.headers.add(new AVHeader(baseBlock, bArr4));
            } else if (i == 8) {
                byte[] bArr5 = new byte[8];
                this.rof.readFully(bArr5, 8);
                this.headers.add(new SignHeader(baseBlock, bArr5));
            } else {
                if (i == 10) {
                    int i3 = baseBlock.hasArchiveDataCRC() ? 4 : 0;
                    if (baseBlock.hasVolumeNumber()) {
                        i3 += 2;
                    }
                    if (i3 > 0) {
                        byte[] bArr6 = new byte[i3];
                        this.rof.readFully(bArr6, i3);
                        endArcHeader = new EndArcHeader(baseBlock, bArr6);
                    } else {
                        endArcHeader = new EndArcHeader(baseBlock, null);
                    }
                    this.headers.add(endArcHeader);
                    this.endHeader = endArcHeader;
                    return;
                }
                byte[] bArr7 = new byte[4];
                this.rof.readFully(bArr7, 4);
                BlockHeader blockHeader = new BlockHeader(baseBlock, bArr7);
                int i4 = $SWITCH_TABLE$de$innosystec$unrar$rarfile$UnrarHeadertype()[blockHeader.getHeaderType().ordinal()];
                if (i4 == 3 || i4 == 9) {
                    int headerSize = (blockHeader.getHeaderSize() - 7) - 4;
                    byte[] bArr8 = new byte[headerSize];
                    this.rof.readFully(bArr8, headerSize);
                    FileHeader fileHeader = new FileHeader(blockHeader, bArr8);
                    this.headers.add(fileHeader);
                    this.rof.setPosition(fileHeader.getPositionInFile() + ((long) fileHeader.getHeaderSize()) + fileHeader.getFullPackSize());
                } else if (i4 == 6) {
                    byte[] bArr9 = new byte[3];
                    this.rof.readFully(bArr9, 3);
                    SubBlockHeader subBlockHeader = new SubBlockHeader(blockHeader, bArr9);
                    subBlockHeader.print();
                    int i5 = $SWITCH_TABLE$de$innosystec$unrar$rarfile$SubBlockHeaderType()[subBlockHeader.getSubType().ordinal()];
                    if (i5 == 1) {
                        byte[] bArr10 = new byte[10];
                        this.rof.readFully(bArr10, 10);
                        EAHeader eAHeader = new EAHeader(subBlockHeader, bArr10);
                        eAHeader.print();
                        this.headers.add(eAHeader);
                    } else if (i5 == 2) {
                        int headerSize2 = ((subBlockHeader.getHeaderSize() - 7) - 4) - 3;
                        byte[] bArr11 = new byte[headerSize2];
                        this.rof.readFully(bArr11, headerSize2);
                        UnixOwnersHeader unixOwnersHeader = new UnixOwnersHeader(subBlockHeader, bArr11);
                        unixOwnersHeader.print();
                        this.headers.add(unixOwnersHeader);
                    } else if (i5 == 3) {
                        byte[] bArr12 = new byte[8];
                        this.rof.readFully(bArr12, 8);
                        MacInfoHeader macInfoHeader = new MacInfoHeader(subBlockHeader, bArr12);
                        macInfoHeader.print();
                        this.headers.add(macInfoHeader);
                    }
                } else if (i4 == 7) {
                    int headerSize3 = (blockHeader.getHeaderSize() - 7) - 4;
                    byte[] bArr13 = new byte[headerSize3];
                    this.rof.readFully(bArr13, headerSize3);
                    ProtectHeader protectHeader = new ProtectHeader(blockHeader, bArr13);
                    this.rof.setPosition(protectHeader.getPositionInFile() + ((long) protectHeader.getHeaderSize()));
                } else {
                    logger.warning("Unknown Header");
                    throw new RarException(RarException.RarExceptionType.notRarArchive);
                }
            }
        }
    }

    public void extractFile(FileHeader fileHeader, OutputStream outputStream) throws RarException {
        if (!this.headers.contains(fileHeader)) {
            throw new RarException(RarException.RarExceptionType.headerNotInArchive);
        }
        try {
            doExtractFile(fileHeader, outputStream);
        } catch (Exception e) {
            if (e instanceof RarException) {
                throw ((RarException) e);
            }
            throw new RarException(e);
        }
    }

    private void doExtractFile(FileHeader fileHeader, OutputStream outputStream) throws RarException, IOException {
        this.dataIO.init(outputStream);
        this.dataIO.init(fileHeader);
        this.dataIO.setUnpFileCRC(isOldFormat() ? 0 : -1);
        if (this.unpack == null) {
            this.unpack = new Unpack(this.dataIO);
        }
        if (!fileHeader.isSolid()) {
            this.unpack.init(null);
        }
        this.unpack.setDestSize(fileHeader.getFullUnpackSize());
        try {
            this.unpack.doUnpack(fileHeader.getUnpVersion(), fileHeader.isSolid());
            if ((~(this.dataIO.getSubHeader().isSplitAfter() ? this.dataIO.getPackedCRC() : this.dataIO.getUnpFileCRC())) == r3.getFileCRC()) {
            } else {
                throw new RarException(RarException.RarExceptionType.crcError);
            }
        } catch (Exception e) {
            this.unpack.cleanUp();
            if (e instanceof RarException) {
                throw ((RarException) e);
            }
            throw new RarException(e);
        }
    }

    public MainHeader getMainHeader() {
        return this.newMhd;
    }

    public boolean isOldFormat() {
        return this.markHead.isOldFormat();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        IReadOnlyAccess iReadOnlyAccess = this.rof;
        if (iReadOnlyAccess != null) {
            iReadOnlyAccess.close();
            this.rof = null;
        }
    }
}

package org.apache.poi.poifs.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutputStream;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class Ole10Native {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    protected static final String ISO1 = "ISO-8859-1";
    public static final String OLE10_NATIVE = "\u0001Ole10Native";
    private String command;
    private byte[] dataBuffer;
    private String fileName;
    private short flags1;
    private short flags2;
    private short flags3;
    private String label;
    private EncodingMode mode;
    private int totalSize;
    private short unknown1;

    private enum EncodingMode {
        parsed,
        unparsed,
        compact
    }

    public static Ole10Native createFromEmbeddedOleObject(POIFSFileSystem poifs) throws Ole10NativeException, IOException {
        return createFromEmbeddedOleObject(poifs.getRoot());
    }

    public static Ole10Native createFromEmbeddedOleObject(DirectoryNode directory) throws Ole10NativeException, IOException {
        DocumentEntry nativeEntry = (DocumentEntry) directory.getEntry(OLE10_NATIVE);
        byte[] data = new byte[nativeEntry.getSize()];
        int readBytes = directory.createDocumentInputStream(nativeEntry).read(data);
        if (readBytes != data.length) {
            throw new AssertionError();
        }
        return new Ole10Native(data, 0);
    }

    public Ole10Native(String label, String filename, String command, byte[] data) {
        this.flags1 = (short) 2;
        this.flags2 = (short) 0;
        this.unknown1 = (short) 3;
        this.flags3 = (short) 0;
        setLabel(label);
        setFileName(filename);
        setCommand(command);
        setDataBuffer(data);
        this.mode = EncodingMode.parsed;
    }

    public Ole10Native(byte[] data, int offset) throws Ole10NativeException {
        int dataSize;
        this.flags1 = (short) 2;
        this.flags2 = (short) 0;
        this.unknown1 = (short) 3;
        this.flags3 = (short) 0;
        if (data.length < offset + 2) {
            throw new Ole10NativeException("data is too small");
        }
        this.totalSize = LittleEndian.getInt(data, offset);
        int ofs = offset + 4;
        this.mode = EncodingMode.unparsed;
        if (LittleEndian.getShort(data, ofs) == 2) {
            if (Character.isISOControl(data[ofs + 2])) {
                this.mode = EncodingMode.compact;
            } else {
                this.mode = EncodingMode.parsed;
            }
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$filesystem$Ole10Native$EncodingMode[this.mode.ordinal()];
        if (i != 1) {
            if (i == 2) {
                this.flags1 = LittleEndian.getShort(data, ofs);
                ofs += 2;
                dataSize = this.totalSize - 2;
            } else {
                dataSize = this.totalSize;
            }
        } else {
            this.flags1 = LittleEndian.getShort(data, ofs);
            int ofs2 = ofs + 2;
            int len = getStringLength(data, ofs2);
            this.label = StringUtil.getFromCompressedUnicode(data, ofs2, len - 1);
            int ofs3 = ofs2 + len;
            int len2 = getStringLength(data, ofs3);
            this.fileName = StringUtil.getFromCompressedUnicode(data, ofs3, len2 - 1);
            int ofs4 = ofs3 + len2;
            this.flags2 = LittleEndian.getShort(data, ofs4);
            int ofs5 = ofs4 + 2;
            this.unknown1 = LittleEndian.getShort(data, ofs5);
            int ofs6 = ofs5 + 2;
            int len3 = LittleEndian.getInt(data, ofs6);
            int ofs7 = ofs6 + 4;
            this.command = StringUtil.getFromCompressedUnicode(data, ofs7, len3 - 1);
            int ofs8 = ofs7 + len3;
            if (this.totalSize < ofs8) {
                throw new Ole10NativeException("Invalid Ole10Native");
            }
            int dataSize2 = LittleEndian.getInt(data, ofs8);
            ofs = ofs8 + 4;
            if (dataSize2 < 0 || this.totalSize - (ofs - 4) < dataSize2) {
                throw new Ole10NativeException("Invalid Ole10Native");
            }
            dataSize = dataSize2;
        }
        if (((long) dataSize) + ((long) ofs) > data.length) {
            throw new Ole10NativeException("Invalid Ole10Native: declared data length > available data");
        }
        byte[] bArr = new byte[dataSize];
        this.dataBuffer = bArr;
        System.arraycopy(data, ofs, bArr, 0, dataSize);
        int i2 = ofs + dataSize;
    }

    /* JADX INFO: renamed from: org.apache.poi.poifs.filesystem.Ole10Native$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$poifs$filesystem$Ole10Native$EncodingMode;

        static {
            int[] iArr = new int[EncodingMode.values().length];
            $SwitchMap$org$apache$poi$poifs$filesystem$Ole10Native$EncodingMode = iArr;
            try {
                iArr[EncodingMode.parsed.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$Ole10Native$EncodingMode[EncodingMode.compact.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$Ole10Native$EncodingMode[EncodingMode.unparsed.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private static int getStringLength(byte[] data, int ofs) {
        int len = 0;
        while (len + ofs < data.length && data[ofs + len] != 0) {
            len++;
        }
        return len + 1;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public short getFlags1() {
        return this.flags1;
    }

    public String getLabel() {
        return this.label;
    }

    public String getFileName() {
        return this.fileName;
    }

    public short getFlags2() {
        return this.flags2;
    }

    public short getUnknown1() {
        return this.unknown1;
    }

    public String getCommand() {
        return this.command;
    }

    public int getDataSize() {
        return this.dataBuffer.length;
    }

    public byte[] getDataBuffer() {
        return this.dataBuffer;
    }

    public short getFlags3() {
        return this.flags3;
    }

    public void writeOut(OutputStream out) throws IOException {
        LittleEndianOutputStream leosOut = new LittleEndianOutputStream(out);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$filesystem$Ole10Native$EncodingMode[this.mode.ordinal()];
        if (i != 1) {
            if (i == 2) {
                leosOut.writeInt(getDataSize() + 2);
                leosOut.writeShort(getFlags1());
                out.write(getDataBuffer());
                return;
            } else {
                leosOut.writeInt(getDataSize());
                out.write(getDataBuffer());
                return;
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LittleEndianOutputStream leos = new LittleEndianOutputStream(bos);
        leos.writeShort(getFlags1());
        leos.write(getLabel().getBytes(ISO1));
        leos.write(0);
        leos.write(getFileName().getBytes(ISO1));
        leos.write(0);
        leos.writeShort(getFlags2());
        leos.writeShort(getUnknown1());
        leos.writeInt(getCommand().length() + 1);
        leos.write(getCommand().getBytes(ISO1));
        leos.write(0);
        leos.writeInt(getDataSize());
        leos.write(getDataBuffer());
        leos.writeShort(getFlags3());
        leos.close();
        leosOut.writeInt(bos.size());
        bos.writeTo(out);
    }

    public void setFlags1(short flags1) {
        this.flags1 = flags1;
    }

    public void setFlags2(short flags2) {
        this.flags2 = flags2;
    }

    public void setFlags3(short flags3) {
        this.flags3 = flags3;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setUnknown1(short unknown1) {
        this.unknown1 = unknown1;
    }

    public void setDataBuffer(byte[] dataBuffer) {
        this.dataBuffer = (byte[]) dataBuffer.clone();
    }
}

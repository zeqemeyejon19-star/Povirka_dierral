package org.apache.poi.hssf.record;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionHeader;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionVerifier;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionHeader;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionVerifier;
import org.apache.poi.poifs.crypt.xor.XOREncryptionHeader;
import org.apache.poi.poifs.crypt.xor.XOREncryptionVerifier;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianOutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class FilePassRecord extends StandardRecord implements Cloneable {
    private static final int ENCRYPTION_OTHER = 1;
    private static final int ENCRYPTION_XOR = 0;
    public static final short sid = 47;
    private EncryptionInfo encryptionInfo;
    private final int encryptionType;

    private FilePassRecord(FilePassRecord other) {
        this.encryptionType = other.encryptionType;
        try {
            this.encryptionInfo = other.encryptionInfo.clone();
        } catch (CloneNotSupportedException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    public FilePassRecord(EncryptionMode encryptionMode) {
        this.encryptionType = encryptionMode == EncryptionMode.xor ? 0 : 1;
        this.encryptionInfo = new EncryptionInfo(encryptionMode);
    }

    public FilePassRecord(RecordInputStream in) {
        EncryptionMode preferredMode;
        int uShort = in.readUShort();
        this.encryptionType = uShort;
        if (uShort == 0) {
            preferredMode = EncryptionMode.xor;
        } else if (uShort == 1) {
            preferredMode = EncryptionMode.cryptoAPI;
        } else {
            throw new EncryptedDocumentException("invalid encryption type");
        }
        try {
            this.encryptionInfo = new EncryptionInfo(in, preferredMode);
        } catch (IOException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.encryptionType);
        byte[] data = new byte[1024];
        LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(data, 0);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$crypt$EncryptionMode[this.encryptionInfo.getEncryptionMode().ordinal()];
        if (i == 1) {
            ((XOREncryptionHeader) this.encryptionInfo.getHeader()).write(bos);
            ((XOREncryptionVerifier) this.encryptionInfo.getVerifier()).write(bos);
        } else if (i == 2) {
            out.writeShort(this.encryptionInfo.getVersionMajor());
            out.writeShort(this.encryptionInfo.getVersionMinor());
            ((BinaryRC4EncryptionHeader) this.encryptionInfo.getHeader()).write(bos);
            ((BinaryRC4EncryptionVerifier) this.encryptionInfo.getVerifier()).write(bos);
        } else if (i == 3) {
            out.writeShort(this.encryptionInfo.getVersionMajor());
            out.writeShort(this.encryptionInfo.getVersionMinor());
            out.writeInt(this.encryptionInfo.getEncryptionFlags());
            ((CryptoAPIEncryptionHeader) this.encryptionInfo.getHeader()).write(bos);
            ((CryptoAPIEncryptionVerifier) this.encryptionInfo.getVerifier()).write(bos);
        } else {
            throw new EncryptedDocumentException("not supported");
        }
        out.write(data, 0, bos.getWriteIndex());
    }

    /* JADX INFO: renamed from: org.apache.poi.hssf.record.FilePassRecord$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$poifs$crypt$EncryptionMode;

        static {
            int[] iArr = new int[EncryptionMode.values().length];
            $SwitchMap$org$apache$poi$poifs$crypt$EncryptionMode = iArr;
            try {
                iArr[EncryptionMode.xor.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$crypt$EncryptionMode[EncryptionMode.binaryRC4.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$crypt$EncryptionMode[EncryptionMode.cryptoAPI.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LittleEndianOutputStream leos = new LittleEndianOutputStream(bos);
        serialize(leos);
        return bos.size();
    }

    public EncryptionInfo getEncryptionInfo() {
        return this.encryptionInfo;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 47;
    }

    @Override // org.apache.poi.hssf.record.Record
    public FilePassRecord clone() {
        return new FilePassRecord(this);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[FILEPASS]\n");
        buffer.append("    .type = ").append(HexDump.shortToHex(this.encryptionType)).append('\n');
        String prefix = "     ." + this.encryptionInfo.getEncryptionMode();
        buffer.append(prefix + ".info = ").append(HexDump.shortToHex(this.encryptionInfo.getVersionMajor())).append('\n');
        buffer.append(prefix + ".ver  = ").append(HexDump.shortToHex(this.encryptionInfo.getVersionMinor())).append('\n');
        buffer.append(prefix + ".salt = ").append(HexDump.toHex(this.encryptionInfo.getVerifier().getSalt())).append('\n');
        buffer.append(prefix + ".verifier = ").append(HexDump.toHex(this.encryptionInfo.getVerifier().getEncryptedVerifier())).append('\n');
        buffer.append(prefix + ".verifierHash = ").append(HexDump.toHex(this.encryptionInfo.getVerifier().getEncryptedVerifierHash())).append('\n');
        buffer.append("[/FILEPASS]\n");
        return buffer.toString();
    }
}

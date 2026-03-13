package org.apache.poi.poifs.crypt.standard;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CipherProvider;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class StandardEncryptionHeader extends EncryptionHeader implements EncryptionRecord, Cloneable {
    /* JADX WARN: Multi-variable type inference failed */
    protected StandardEncryptionHeader(LittleEndianInput littleEndianInput) throws IOException {
        setFlags(littleEndianInput.readInt());
        setSizeExtra(littleEndianInput.readInt());
        setCipherAlgorithm(CipherAlgorithm.fromEcmaId(littleEndianInput.readInt()));
        setHashAlgorithm(HashAlgorithm.fromEcmaId(littleEndianInput.readInt()));
        int keySize = littleEndianInput.readInt();
        setKeySize(keySize == 0 ? 40 : keySize);
        setBlockSize(getKeySize());
        setCipherProvider(CipherProvider.fromEcmaId(littleEndianInput.readInt()));
        littleEndianInput.readLong();
        if (littleEndianInput instanceof RecordInputStream) {
            ((RecordInputStream) littleEndianInput).mark(5);
        } else {
            ((InputStream) littleEndianInput).mark(5);
        }
        int checkForSalt = littleEndianInput.readInt();
        if (littleEndianInput instanceof RecordInputStream) {
            ((RecordInputStream) littleEndianInput).reset();
        } else {
            ((InputStream) littleEndianInput).reset();
        }
        if (checkForSalt == 16) {
            setCspName("");
        } else {
            StringBuilder builder = new StringBuilder();
            while (true) {
                char c = (char) littleEndianInput.readShort();
                if (c == 0) {
                    break;
                } else {
                    builder.append(c);
                }
            }
            setCspName(builder.toString());
        }
        setChainingMode(ChainingMode.ecb);
        setKeySalt(null);
    }

    protected StandardEncryptionHeader(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        setCipherAlgorithm(cipherAlgorithm);
        setHashAlgorithm(hashAlgorithm);
        setKeySize(keyBits);
        setBlockSize(blockSize);
        setCipherProvider(cipherAlgorithm.provider);
        setFlags(EncryptionInfo.flagCryptoAPI.setBoolean(0, true) | EncryptionInfo.flagAES.setBoolean(0, cipherAlgorithm.provider == CipherProvider.aes));
    }

    @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
    public void write(LittleEndianByteArrayOutputStream bos) {
        int startIdx = bos.getWriteIndex();
        LittleEndianOutput sizeOutput = bos.createDelayedOutput(4);
        bos.writeInt(getFlags());
        bos.writeInt(0);
        bos.writeInt(getCipherAlgorithm().ecmaId);
        bos.writeInt(getHashAlgorithm().ecmaId);
        bos.writeInt(getKeySize());
        bos.writeInt(getCipherProvider().ecmaId);
        bos.writeInt(0);
        bos.writeInt(0);
        String cspName = getCspName();
        if (cspName == null) {
            cspName = getCipherProvider().cipherProviderName;
        }
        bos.write(StringUtil.getToUnicodeLE(cspName));
        bos.writeShort(0);
        int headerSize = (bos.getWriteIndex() - startIdx) - 4;
        sizeOutput.writeInt(headerSize);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionHeader
    public StandardEncryptionHeader clone() throws CloneNotSupportedException {
        return (StandardEncryptionHeader) super.clone();
    }
}

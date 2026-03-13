package org.apache.poi.poifs.crypt;

import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public interface EncryptionInfoBuilder {
    void initialize(EncryptionInfo encryptionInfo, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int i, int i2, ChainingMode chainingMode);

    void initialize(EncryptionInfo encryptionInfo, LittleEndianInput littleEndianInput) throws IOException;
}

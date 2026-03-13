package org.apache.poi.poifs.crypt;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class DataSpaceMapUtils {
    public static void addDefaultDataSpace(DirectoryEntry dir) throws IOException {
        DataSpaceMapEntry dsme = new DataSpaceMapEntry(new int[]{0}, new String[]{Decryptor.DEFAULT_POIFS_ENTRY}, "StrongEncryptionDataSpace");
        DataSpaceMap dsm = new DataSpaceMap(new DataSpaceMapEntry[]{dsme});
        createEncryptionEntry(dir, "\u0006DataSpaces/DataSpaceMap", dsm);
        DataSpaceDefinition dsd = new DataSpaceDefinition(new String[]{"StrongEncryptionTransform"});
        createEncryptionEntry(dir, "\u0006DataSpaces/DataSpaceInfo/StrongEncryptionDataSpace", dsd);
        TransformInfoHeader tih = new TransformInfoHeader(1, "{FF9A3F03-56EF-4613-BDD5-5A41C1D07246}", "Microsoft.Container.EncryptionTransform", 1, 0, 1, 0, 1, 0);
        IRMDSTransformInfo irm = new IRMDSTransformInfo(tih, 0, null);
        createEncryptionEntry(dir, "\u0006DataSpaces/TransformInfo/StrongEncryptionTransform/\u0006Primary", irm);
        DataSpaceVersionInfo dsvi = new DataSpaceVersionInfo("Microsoft.Container.DataSpaces", 1, 0, 1, 0, 1, 0);
        createEncryptionEntry(dir, "\u0006DataSpaces/Version", dsvi);
    }

    public static DocumentEntry createEncryptionEntry(DirectoryEntry dir, String path, EncryptionRecord out) throws IOException {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            dir = dir.hasEntry(parts[i]) ? (DirectoryEntry) dir.getEntry(parts[i]) : dir.createDirectory(parts[i]);
        }
        final byte[] buf = new byte[5000];
        LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(buf, 0);
        out.write(bos);
        String fileName = parts[parts.length - 1];
        if (dir.hasEntry(fileName)) {
            dir.getEntry(fileName).delete();
        }
        return dir.createDocument(fileName, bos.getWriteIndex(), new POIFSWriterListener() { // from class: org.apache.poi.poifs.crypt.DataSpaceMapUtils.1
            @Override // org.apache.poi.poifs.filesystem.POIFSWriterListener
            public void processPOIFSWriterEvent(POIFSWriterEvent event) {
                try {
                    event.getStream().write(buf, 0, event.getLimit());
                } catch (IOException e) {
                    throw new EncryptedDocumentException(e);
                }
            }
        });
    }

    public static class DataSpaceMap implements EncryptionRecord {
        DataSpaceMapEntry[] entries;

        public DataSpaceMap(DataSpaceMapEntry[] entries) {
            this.entries = (DataSpaceMapEntry[]) entries.clone();
        }

        public DataSpaceMap(LittleEndianInput is) {
            is.readInt();
            int entryCount = is.readInt();
            this.entries = new DataSpaceMapEntry[entryCount];
            for (int i = 0; i < entryCount; i++) {
                this.entries[i] = new DataSpaceMapEntry(is);
            }
        }

        @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
        public void write(LittleEndianByteArrayOutputStream os) {
            os.writeInt(8);
            os.writeInt(this.entries.length);
            DataSpaceMapEntry[] arr$ = this.entries;
            for (DataSpaceMapEntry dsme : arr$) {
                dsme.write(os);
            }
        }
    }

    public static class DataSpaceMapEntry implements EncryptionRecord {
        final String dataSpaceName;
        final String[] referenceComponent;
        final int[] referenceComponentType;

        public DataSpaceMapEntry(int[] referenceComponentType, String[] referenceComponent, String dataSpaceName) {
            this.referenceComponentType = (int[]) referenceComponentType.clone();
            this.referenceComponent = (String[]) referenceComponent.clone();
            this.dataSpaceName = dataSpaceName;
        }

        public DataSpaceMapEntry(LittleEndianInput is) {
            is.readInt();
            int referenceComponentCount = is.readInt();
            this.referenceComponentType = new int[referenceComponentCount];
            this.referenceComponent = new String[referenceComponentCount];
            for (int i = 0; i < referenceComponentCount; i++) {
                this.referenceComponentType[i] = is.readInt();
                this.referenceComponent[i] = DataSpaceMapUtils.readUnicodeLPP4(is);
            }
            this.dataSpaceName = DataSpaceMapUtils.readUnicodeLPP4(is);
        }

        @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
        public void write(LittleEndianByteArrayOutputStream os) {
            int start = os.getWriteIndex();
            LittleEndianOutput sizeOut = os.createDelayedOutput(4);
            os.writeInt(this.referenceComponent.length);
            for (int i = 0; i < this.referenceComponent.length; i++) {
                os.writeInt(this.referenceComponentType[i]);
                DataSpaceMapUtils.writeUnicodeLPP4(os, this.referenceComponent[i]);
            }
            DataSpaceMapUtils.writeUnicodeLPP4(os, this.dataSpaceName);
            sizeOut.writeInt(os.getWriteIndex() - start);
        }
    }

    public static class DataSpaceDefinition implements EncryptionRecord {
        String[] transformer;

        public DataSpaceDefinition(String[] transformer) {
            this.transformer = (String[]) transformer.clone();
        }

        public DataSpaceDefinition(LittleEndianInput is) {
            is.readInt();
            int transformReferenceCount = is.readInt();
            this.transformer = new String[transformReferenceCount];
            for (int i = 0; i < transformReferenceCount; i++) {
                this.transformer[i] = DataSpaceMapUtils.readUnicodeLPP4(is);
            }
        }

        @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
        public void write(LittleEndianByteArrayOutputStream bos) {
            bos.writeInt(8);
            bos.writeInt(this.transformer.length);
            String[] arr$ = this.transformer;
            for (String str : arr$) {
                DataSpaceMapUtils.writeUnicodeLPP4(bos, str);
            }
        }
    }

    public static class IRMDSTransformInfo implements EncryptionRecord {
        int extensibilityHeader;
        TransformInfoHeader transformInfoHeader;
        String xrMLLicense;

        public IRMDSTransformInfo(TransformInfoHeader transformInfoHeader, int extensibilityHeader, String xrMLLicense) {
            this.transformInfoHeader = transformInfoHeader;
            this.extensibilityHeader = extensibilityHeader;
            this.xrMLLicense = xrMLLicense;
        }

        public IRMDSTransformInfo(LittleEndianInput is) {
            this.transformInfoHeader = new TransformInfoHeader(is);
            this.extensibilityHeader = is.readInt();
            this.xrMLLicense = DataSpaceMapUtils.readUtf8LPP4(is);
        }

        @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
        public void write(LittleEndianByteArrayOutputStream bos) {
            this.transformInfoHeader.write(bos);
            bos.writeInt(this.extensibilityHeader);
            DataSpaceMapUtils.writeUtf8LPP4(bos, this.xrMLLicense);
            bos.writeInt(4);
        }
    }

    public static class TransformInfoHeader implements EncryptionRecord {
        int readerVersionMajor;
        int readerVersionMinor;
        int transformType;
        String transformerId;
        String transformerName;
        int updaterVersionMajor;
        int updaterVersionMinor;
        int writerVersionMajor;
        int writerVersionMinor;

        public TransformInfoHeader(int transformType, String transformerId, String transformerName, int readerVersionMajor, int readerVersionMinor, int updaterVersionMajor, int updaterVersionMinor, int writerVersionMajor, int writerVersionMinor) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            this.transformType = transformType;
            this.transformerId = transformerId;
            this.transformerName = transformerName;
            this.readerVersionMajor = readerVersionMajor;
            this.readerVersionMinor = readerVersionMinor;
            this.updaterVersionMajor = updaterVersionMajor;
            this.updaterVersionMinor = updaterVersionMinor;
            this.writerVersionMajor = writerVersionMajor;
            this.writerVersionMinor = writerVersionMinor;
        }

        public TransformInfoHeader(LittleEndianInput is) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            is.readInt();
            this.transformType = is.readInt();
            this.transformerId = DataSpaceMapUtils.readUnicodeLPP4(is);
            this.transformerName = DataSpaceMapUtils.readUnicodeLPP4(is);
            this.readerVersionMajor = is.readShort();
            this.readerVersionMinor = is.readShort();
            this.updaterVersionMajor = is.readShort();
            this.updaterVersionMinor = is.readShort();
            this.writerVersionMajor = is.readShort();
            this.writerVersionMinor = is.readShort();
        }

        @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
        public void write(LittleEndianByteArrayOutputStream bos) {
            int start = bos.getWriteIndex();
            LittleEndianOutput sizeOut = bos.createDelayedOutput(4);
            bos.writeInt(this.transformType);
            DataSpaceMapUtils.writeUnicodeLPP4(bos, this.transformerId);
            sizeOut.writeInt(bos.getWriteIndex() - start);
            DataSpaceMapUtils.writeUnicodeLPP4(bos, this.transformerName);
            bos.writeShort(this.readerVersionMajor);
            bos.writeShort(this.readerVersionMinor);
            bos.writeShort(this.updaterVersionMajor);
            bos.writeShort(this.updaterVersionMinor);
            bos.writeShort(this.writerVersionMajor);
            bos.writeShort(this.writerVersionMinor);
        }
    }

    public static class DataSpaceVersionInfo implements EncryptionRecord {
        String featureIdentifier;
        int readerVersionMajor;
        int readerVersionMinor;
        int updaterVersionMajor;
        int updaterVersionMinor;
        int writerVersionMajor;
        int writerVersionMinor;

        public DataSpaceVersionInfo(LittleEndianInput is) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            this.featureIdentifier = DataSpaceMapUtils.readUnicodeLPP4(is);
            this.readerVersionMajor = is.readShort();
            this.readerVersionMinor = is.readShort();
            this.updaterVersionMajor = is.readShort();
            this.updaterVersionMinor = is.readShort();
            this.writerVersionMajor = is.readShort();
            this.writerVersionMinor = is.readShort();
        }

        public DataSpaceVersionInfo(String featureIdentifier, int readerVersionMajor, int readerVersionMinor, int updaterVersionMajor, int updaterVersionMinor, int writerVersionMajor, int writerVersionMinor) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            this.featureIdentifier = featureIdentifier;
            this.readerVersionMajor = readerVersionMajor;
            this.readerVersionMinor = readerVersionMinor;
            this.updaterVersionMajor = updaterVersionMajor;
            this.updaterVersionMinor = updaterVersionMinor;
            this.writerVersionMajor = writerVersionMajor;
            this.writerVersionMinor = writerVersionMinor;
        }

        @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
        public void write(LittleEndianByteArrayOutputStream bos) {
            DataSpaceMapUtils.writeUnicodeLPP4(bos, this.featureIdentifier);
            bos.writeShort(this.readerVersionMajor);
            bos.writeShort(this.readerVersionMinor);
            bos.writeShort(this.updaterVersionMajor);
            bos.writeShort(this.updaterVersionMinor);
            bos.writeShort(this.writerVersionMajor);
            bos.writeShort(this.writerVersionMinor);
        }
    }

    public static String readUnicodeLPP4(LittleEndianInput is) {
        int length = is.readInt();
        if (length % 2 != 0) {
            throw new EncryptedDocumentException("UNICODE-LP-P4 structure is a multiple of 4 bytes. If Padding is present, it MUST be exactly 2 bytes long");
        }
        String result = StringUtil.readUnicodeLE(is, length / 2);
        if (length % 4 == 2) {
            is.readShort();
        }
        return result;
    }

    public static void writeUnicodeLPP4(LittleEndianOutput os, String string) {
        byte[] buf = StringUtil.getToUnicodeLE(string);
        os.writeInt(buf.length);
        os.write(buf);
        if (buf.length % 4 == 2) {
            os.writeShort(0);
        }
    }

    public static String readUtf8LPP4(LittleEndianInput is) {
        int length = is.readInt();
        if (length == 0 || length == 4) {
            is.readInt();
            if (length == 0) {
                return null;
            }
            return "";
        }
        byte[] data = new byte[length];
        is.readFully(data);
        int scratchedBytes = length % 4;
        if (scratchedBytes > 0) {
            for (int i = 0; i < 4 - scratchedBytes; i++) {
                is.readByte();
            }
        }
        return new String(data, 0, data.length, Charset.forName("UTF-8"));
    }

    public static void writeUtf8LPP4(LittleEndianOutput os, String str) {
        if (str == null || "".equals(str)) {
            os.writeInt(str == null ? 0 : 4);
            os.writeInt(0);
            return;
        }
        byte[] buf = str.getBytes(Charset.forName("UTF-8"));
        os.writeInt(buf.length);
        os.write(buf);
        int scratchBytes = buf.length % 4;
        if (scratchBytes > 0) {
            for (int i = 0; i < 4 - scratchBytes; i++) {
                os.writeByte(0);
            }
        }
    }
}

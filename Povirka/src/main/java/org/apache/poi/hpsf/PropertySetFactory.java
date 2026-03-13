package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndianInputStream;

/* JADX INFO: loaded from: classes.dex */
public class PropertySetFactory {
    public static PropertySet create(DirectoryEntry dir, String name) throws NoPropertySetStreamException, IOException {
        InputStream inp = null;
        try {
            DocumentEntry entry = (DocumentEntry) dir.getEntry(name);
            inp = new DocumentInputStream(entry);
            try {
                PropertySet propertySetCreate = create(inp);
                inp.close();
                return propertySetCreate;
            } catch (MarkUnsupportedException e) {
                inp.close();
                return null;
            }
        } catch (Throwable th) {
            if (inp != null) {
                inp.close();
            }
            throw th;
        }
    }

    public static PropertySet create(InputStream stream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        stream.mark(45);
        LittleEndianInputStream leis = new LittleEndianInputStream(stream);
        int byteOrder = leis.readUShort();
        int format = leis.readUShort();
        byte[] clsIdBuf = new byte[16];
        leis.readFully(clsIdBuf);
        int sectionCount = (int) leis.readUInt();
        if (byteOrder != 65534 || format != 0 || sectionCount < 0) {
            throw new NoPropertySetStreamException();
        }
        if (sectionCount > 0) {
            leis.readFully(clsIdBuf);
        }
        stream.reset();
        ClassID clsId = new ClassID(clsIdBuf, 0);
        if (sectionCount > 0 && PropertySet.matchesSummary(clsId, SectionIDMap.SUMMARY_INFORMATION_ID)) {
            return new SummaryInformation(stream);
        }
        if (sectionCount > 0 && PropertySet.matchesSummary(clsId, SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID)) {
            return new DocumentSummaryInformation(stream);
        }
        return new PropertySet(stream);
    }

    public static SummaryInformation newSummaryInformation() {
        return new SummaryInformation();
    }

    public static DocumentSummaryInformation newDocumentSummaryInformation() {
        return new DocumentSummaryInformation();
    }
}

package org.apache.poi.hssf.extractor;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.OldFormulaRecord;
import org.apache.poi.hssf.record.OldLabelRecord;
import org.apache.poi.hssf.record.OldSheetRecord;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class OldExcelExtractor implements Closeable {
    private static final int FILE_PASS_RECORD_SID = 47;
    private int biffVersion;
    private int fileType;
    private RecordInputStream ris;
    private Closeable toClose;

    public OldExcelExtractor(InputStream input) throws IOException {
        open(input);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x002a A[PHI: r0
  0x002a: PHI (r0v2 'poifs' org.apache.poi.poifs.filesystem.NPOIFSFileSystem) = 
  (r0v1 'poifs' org.apache.poi.poifs.filesystem.NPOIFSFileSystem)
  (r0v3 'poifs' org.apache.poi.poifs.filesystem.NPOIFSFileSystem)
 binds: [B:23:0x0031, B:19:0x0028] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public OldExcelExtractor(java.io.File r4) throws java.io.IOException {
        /*
            r3 = this;
            r3.<init>()
            r0 = 0
            org.apache.poi.poifs.filesystem.NPOIFSFileSystem r1 = new org.apache.poi.poifs.filesystem.NPOIFSFileSystem     // Catch: java.lang.Throwable -> L15 java.lang.RuntimeException -> L17 java.io.IOException -> L1a org.apache.poi.poifs.filesystem.NotOLE2FileException -> L25 org.apache.poi.hssf.OldExcelFormatException -> L2e
            r1.<init>(r4)     // Catch: java.lang.Throwable -> L15 java.lang.RuntimeException -> L17 java.io.IOException -> L1a org.apache.poi.poifs.filesystem.NotOLE2FileException -> L25 org.apache.poi.hssf.OldExcelFormatException -> L2e
            r0 = r1
            r3.open(r0)     // Catch: java.lang.Throwable -> L15 java.lang.RuntimeException -> L17 java.io.IOException -> L1a org.apache.poi.poifs.filesystem.NotOLE2FileException -> L25 org.apache.poi.hssf.OldExcelFormatException -> L2e
            r3.toClose = r0     // Catch: java.lang.Throwable -> L15 java.lang.RuntimeException -> L17 java.io.IOException -> L1a org.apache.poi.poifs.filesystem.NotOLE2FileException -> L25 org.apache.poi.hssf.OldExcelFormatException -> L2e
            if (r0 != 0) goto L14
            org.apache.poi.util.IOUtils.closeQuietly(r0)
        L14:
            return
        L15:
            r1 = move-exception
            goto L1d
        L17:
            r1 = move-exception
            throw r1     // Catch: java.lang.Throwable -> L15
        L1a:
            r1 = move-exception
            throw r1     // Catch: java.lang.Throwable -> L15
        L1d:
            java.io.Closeable r2 = r3.toClose
            if (r2 != 0) goto L24
            org.apache.poi.util.IOUtils.closeQuietly(r0)
        L24:
            throw r1
        L25:
            r1 = move-exception
            java.io.Closeable r1 = r3.toClose
            if (r1 != 0) goto L34
        L2a:
            org.apache.poi.util.IOUtils.closeQuietly(r0)
            goto L34
        L2e:
            r1 = move-exception
            java.io.Closeable r1 = r3.toClose
            if (r1 != 0) goto L34
            goto L2a
        L34:
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r4)
            r3.open(r1)     // Catch: java.lang.RuntimeException -> L3e java.io.IOException -> L43
            return
        L3e:
            r2 = move-exception
            r1.close()
            throw r2
        L43:
            r2 = move-exception
            r1.close()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hssf.extractor.OldExcelExtractor.<init>(java.io.File):void");
    }

    public OldExcelExtractor(NPOIFSFileSystem fs) throws IOException {
        open(fs);
    }

    public OldExcelExtractor(DirectoryNode directory) throws IOException {
        open(directory);
    }

    private void open(InputStream biffStream) throws IOException {
        BufferedInputStream bis = biffStream instanceof BufferedInputStream ? (BufferedInputStream) biffStream : new BufferedInputStream(biffStream, 8);
        if (NPOIFSFileSystem.hasPOIFSHeader(bis)) {
            NPOIFSFileSystem poifs = new NPOIFSFileSystem(bis);
            try {
                open(poifs);
                return;
            } finally {
                poifs.close();
            }
        }
        this.ris = new RecordInputStream(bis);
        this.toClose = bis;
        prepare();
    }

    private void open(NPOIFSFileSystem fs) throws IOException {
        open(fs.getRoot());
    }

    private void open(DirectoryNode directory) throws IOException {
        DocumentNode book;
        try {
            book = (DocumentNode) directory.getEntry(InternalWorkbook.OLD_WORKBOOK_DIR_ENTRY_NAME);
        } catch (FileNotFoundException e) {
            book = (DocumentNode) directory.getEntry(InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES[0]);
        }
        if (book == null) {
            throw new IOException("No Excel 5/95 Book stream found");
        }
        this.ris = new RecordInputStream(directory.createDocumentInputStream(book));
        prepare();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("   OldExcelExtractor <filename>");
            System.exit(1);
        }
        OldExcelExtractor extractor = new OldExcelExtractor(new File(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }

    private void prepare() {
        if (!this.ris.hasNextRecord()) {
            throw new IllegalArgumentException("File contains no records!");
        }
        this.ris.nextRecord();
        int bofSid = this.ris.getSid();
        if (bofSid == 9) {
            this.biffVersion = 2;
        } else if (bofSid == 521) {
            this.biffVersion = 3;
        } else if (bofSid == 1033) {
            this.biffVersion = 4;
        } else if (bofSid == 2057) {
            this.biffVersion = 5;
        } else {
            throw new IllegalArgumentException("File does not begin with a BOF, found sid of " + bofSid);
        }
        BOFRecord bof = new BOFRecord(this.ris);
        this.fileType = bof.getType();
    }

    public int getBiffVersion() {
        return this.biffVersion;
    }

    public int getFileType() {
        return this.fileType;
    }

    public String getText() {
        StringBuffer text = new StringBuffer();
        CodepageRecord codepage = null;
        while (this.ris.hasNextRecord()) {
            int sid = this.ris.getNextSid();
            this.ris.nextRecord();
            if (sid != 4) {
                if (sid == 47) {
                    throw new EncryptedDocumentException("Encryption not supported for Old Excel files");
                }
                if (sid == 66) {
                    codepage = new CodepageRecord(this.ris);
                } else if (sid == 133) {
                    OldSheetRecord shr = new OldSheetRecord(this.ris);
                    shr.setCodePage(codepage);
                    text.append("Sheet: ");
                    text.append(shr.getSheetname());
                    text.append('\n');
                } else if (sid != 638) {
                    if (sid != 1030 && sid != 6) {
                        if (sid != 7) {
                            if (sid == 515) {
                                NumberRecord nr = new NumberRecord(this.ris);
                                handleNumericCell(text, nr.getValue());
                            } else if (sid != 516) {
                                if (sid != 518) {
                                    if (sid != 519) {
                                        RecordInputStream recordInputStream = this.ris;
                                        recordInputStream.readFully(new byte[recordInputStream.remaining()]);
                                    }
                                }
                            }
                        }
                        OldStringRecord sr = new OldStringRecord(this.ris);
                        sr.setCodePage(codepage);
                        text.append(sr.getString());
                        text.append('\n');
                    }
                    if (this.biffVersion == 5) {
                        FormulaRecord fr = new FormulaRecord(this.ris);
                        if (fr.getCachedResultType() == CellType.NUMERIC.getCode()) {
                            handleNumericCell(text, fr.getValue());
                        }
                    } else {
                        OldFormulaRecord fr2 = new OldFormulaRecord(this.ris);
                        if (fr2.getCachedResultType() == CellType.NUMERIC.getCode()) {
                            handleNumericCell(text, fr2.getValue());
                        }
                    }
                } else {
                    RKRecord rr = new RKRecord(this.ris);
                    handleNumericCell(text, rr.getRKNumber());
                }
            }
            OldLabelRecord lr = new OldLabelRecord(this.ris);
            lr.setCodePage(codepage);
            text.append(lr.getValue());
            text.append('\n');
        }
        close();
        this.ris = null;
        return text.toString();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        Closeable closeable = this.toClose;
        if (closeable != null) {
            IOUtils.closeQuietly(closeable);
            this.toClose = null;
        }
    }

    protected void handleNumericCell(StringBuffer text, double value) {
        text.append(value);
        text.append('\n');
    }
}

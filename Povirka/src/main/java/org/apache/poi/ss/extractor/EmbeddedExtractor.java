package org.apache.poi.ss.extractor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.Ole10NativeException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.ObjectData;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.ss.usermodel.ShapeContainer;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFObjectData;

/* JADX INFO: loaded from: classes.dex */
public class EmbeddedExtractor implements Iterable<EmbeddedExtractor> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String CONTENT_TYPE_BYTES = "binary/octet-stream";
    private static final String CONTENT_TYPE_DOC = "application/msword";
    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) EmbeddedExtractor.class);

    @Override // java.lang.Iterable
    public Iterator<EmbeddedExtractor> iterator() {
        EmbeddedExtractor[] ee = {new Ole10Extractor(), new PdfExtractor(), new BiffExtractor(), new OOXMLExtractor(), new FsExtractor()};
        return Arrays.asList(ee).iterator();
    }

    public EmbeddedData extractOne(DirectoryNode src) throws IOException {
        for (EmbeddedExtractor ee : this) {
            if (ee.canExtract(src)) {
                return ee.extract(src);
            }
        }
        return null;
    }

    public EmbeddedData extractOne(Picture src) throws IOException {
        for (EmbeddedExtractor ee : this) {
            if (ee.canExtract(src)) {
                return ee.extract(src);
            }
        }
        return null;
    }

    public List<EmbeddedData> extractAll(Sheet sheet) throws IOException {
        Drawing<?> patriarch = sheet.getDrawingPatriarch();
        if (patriarch == null) {
            return Collections.emptyList();
        }
        List<EmbeddedData> embeddings = new ArrayList<>();
        extractAll(patriarch, embeddings);
        return embeddings;
    }

    protected void extractAll(ShapeContainer<?> parent, List<EmbeddedData> embeddings) throws IOException {
        Iterator<?> it = parent.iterator();
        while (it.hasNext()) {
            Shape shape = (Shape) it.next();
            EmbeddedData data = null;
            if (shape instanceof ObjectData) {
                ObjectData od = (ObjectData) shape;
                try {
                    if (od.hasDirectoryEntry()) {
                        data = extractOne((DirectoryNode) od.getDirectory());
                    } else {
                        String contentType = CONTENT_TYPE_BYTES;
                        if (od instanceof XSSFObjectData) {
                            contentType = ((XSSFObjectData) od).getObjectPart().getContentType();
                        }
                        data = new EmbeddedData(od.getFileName(), od.getObjectData(), contentType);
                    }
                } catch (Exception e) {
                    LOG.log(5, "Entry not found / readable - ignoring OLE embedding", e);
                }
            } else if (shape instanceof Picture) {
                data = extractOne((Picture) shape);
            } else if (shape instanceof ShapeContainer) {
                extractAll((ShapeContainer) shape, embeddings);
            }
            if (data != null) {
                data.setShape(shape);
                String filename = data.getFilename();
                String extension = (filename == null || filename.lastIndexOf(46) == -1) ? ".bin" : filename.substring(filename.lastIndexOf(46));
                if ((filename == null || "".equals(filename) || filename.startsWith("MBD") || filename.startsWith("Root Entry")) && (filename = shape.getShapeName()) != null) {
                    filename = filename + extension;
                }
                if (filename == null || "".equals(filename)) {
                    filename = "picture_" + embeddings.size() + extension;
                }
                data.setFilename(filename.trim());
                embeddings.add(data);
            }
        }
    }

    public boolean canExtract(DirectoryNode source) {
        return false;
    }

    public boolean canExtract(Picture source) {
        return false;
    }

    protected EmbeddedData extract(DirectoryNode dn) throws IOException {
        if (!canExtract(dn)) {
            throw new AssertionError();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(20000);
        POIFSFileSystem dest = new POIFSFileSystem();
        try {
            copyNodes(dn, dest.getRoot());
            dest.writeFilesystem(bos);
            dest.close();
            return new EmbeddedData(dn.getName(), bos.toByteArray(), CONTENT_TYPE_BYTES);
        } catch (Throwable th) {
            dest.close();
            throw th;
        }
    }

    protected EmbeddedData extract(Picture source) throws IOException {
        return null;
    }

    public static class Ole10Extractor extends EmbeddedExtractor {
        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public boolean canExtract(DirectoryNode dn) {
            ClassID clsId = dn.getStorageClsid();
            return ClassID.OLE10_PACKAGE.equals(clsId);
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public EmbeddedData extract(DirectoryNode dn) throws IOException {
            try {
                Ole10Native ole10 = Ole10Native.createFromEmbeddedOleObject(dn);
                return new EmbeddedData(ole10.getFileName(), ole10.getDataBuffer(), EmbeddedExtractor.CONTENT_TYPE_BYTES);
            } catch (Ole10NativeException e) {
                throw new IOException(e);
            }
        }
    }

    static class PdfExtractor extends EmbeddedExtractor {
        static ClassID PdfClassID = new ClassID("{B801CA65-A1FC-11D0-85AD-444553540000}");

        PdfExtractor() {
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public boolean canExtract(DirectoryNode dn) {
            ClassID clsId = dn.getStorageClsid();
            return PdfClassID.equals(clsId) || dn.hasEntry("CONTENTS");
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public EmbeddedData extract(DirectoryNode dn) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream is = dn.createDocumentInputStream("CONTENTS");
            IOUtils.copy(is, bos);
            is.close();
            return new EmbeddedData(dn.getName() + ".pdf", bos.toByteArray(), EmbeddedExtractor.CONTENT_TYPE_PDF);
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public boolean canExtract(Picture source) {
            PictureData pd = source.getPictureData();
            return pd != null && pd.getPictureType() == 2;
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        protected EmbeddedData extract(Picture source) throws IOException {
            int idxEnd;
            PictureData pd = source.getPictureData();
            if (pd == null || pd.getPictureType() != 2) {
                return null;
            }
            byte[] pictureBytes = pd.getData();
            int idxStart = EmbeddedExtractor.indexOf(pictureBytes, 0, "%PDF-".getBytes(LocaleUtil.CHARSET_1252));
            if (idxStart == -1 || (idxEnd = EmbeddedExtractor.indexOf(pictureBytes, idxStart, "%%EOF".getBytes(LocaleUtil.CHARSET_1252))) == -1) {
                return null;
            }
            int pictureBytesLen = (idxEnd - idxStart) + 6;
            byte[] pdfBytes = new byte[pictureBytesLen];
            System.arraycopy(pictureBytes, idxStart, pdfBytes, 0, pictureBytesLen);
            String filename = source.getShapeName().trim();
            if (!StringUtil.endsWithIgnoreCase(filename, ".pdf")) {
                filename = filename + ".pdf";
            }
            return new EmbeddedData(filename, pdfBytes, EmbeddedExtractor.CONTENT_TYPE_PDF);
        }
    }

    static class OOXMLExtractor extends EmbeddedExtractor {
        OOXMLExtractor() {
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public boolean canExtract(DirectoryNode dn) {
            return dn.hasEntry("package");
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public EmbeddedData extract(DirectoryNode dn) throws IOException {
            String ext;
            String contentType;
            ClassID clsId = dn.getStorageClsid();
            if (ClassID.WORD2007.equals(clsId)) {
                ext = ".docx";
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (ClassID.WORD2007_MACRO.equals(clsId)) {
                ext = ".docm";
                contentType = "application/vnd.ms-word.document.macroEnabled.12";
            } else if (ClassID.EXCEL2007.equals(clsId) || ClassID.EXCEL2003.equals(clsId) || ClassID.EXCEL2010.equals(clsId)) {
                ext = ".xlsx";
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (ClassID.EXCEL2007_MACRO.equals(clsId)) {
                ext = ".xlsm";
                contentType = "application/vnd.ms-excel.sheet.macroEnabled.12";
            } else if (ClassID.EXCEL2007_XLSB.equals(clsId)) {
                ext = ".xlsb";
                contentType = "application/vnd.ms-excel.sheet.binary.macroEnabled.12";
            } else if (ClassID.POWERPOINT2007.equals(clsId)) {
                ext = ".pptx";
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            } else if (ClassID.POWERPOINT2007_MACRO.equals(clsId)) {
                ext = ".ppsm";
                contentType = "application/vnd.ms-powerpoint.slideshow.macroEnabled.12";
            } else {
                ext = ".zip";
                contentType = "application/zip";
            }
            DocumentInputStream dis = dn.createDocumentInputStream("package");
            byte[] data = IOUtils.toByteArray(dis);
            dis.close();
            return new EmbeddedData(dn.getName() + ext, data, contentType);
        }
    }

    static class BiffExtractor extends EmbeddedExtractor {
        BiffExtractor() {
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public boolean canExtract(DirectoryNode dn) {
            return canExtractExcel(dn) || canExtractWord(dn);
        }

        protected boolean canExtractExcel(DirectoryNode dn) {
            ClassID clsId = dn.getStorageClsid();
            return ClassID.EXCEL95.equals(clsId) || ClassID.EXCEL97.equals(clsId) || dn.hasEntry("Workbook");
        }

        protected boolean canExtractWord(DirectoryNode dn) {
            ClassID clsId = dn.getStorageClsid();
            return ClassID.WORD95.equals(clsId) || ClassID.WORD97.equals(clsId) || dn.hasEntry("WordDocument");
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public EmbeddedData extract(DirectoryNode dn) throws IOException {
            EmbeddedData ed = super.extract(dn);
            if (canExtractExcel(dn)) {
                ed.setFilename(dn.getName() + ".xls");
                ed.setContentType(EmbeddedExtractor.CONTENT_TYPE_XLS);
            } else if (canExtractWord(dn)) {
                ed.setFilename(dn.getName() + ".doc");
                ed.setContentType(EmbeddedExtractor.CONTENT_TYPE_DOC);
            }
            return ed;
        }
    }

    static class FsExtractor extends EmbeddedExtractor {
        FsExtractor() {
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public boolean canExtract(DirectoryNode dn) {
            return true;
        }

        @Override // org.apache.poi.ss.extractor.EmbeddedExtractor
        public EmbeddedData extract(DirectoryNode dn) throws IOException {
            EmbeddedData ed = super.extract(dn);
            ed.setFilename(dn.getName() + ".ole");
            return ed;
        }
    }

    protected static void copyNodes(DirectoryNode src, DirectoryNode dest) throws IOException {
        for (Entry e : src) {
            if (e instanceof DirectoryNode) {
                DirectoryNode srcDir = (DirectoryNode) e;
                DirectoryNode destDir = (DirectoryNode) dest.createDirectory(srcDir.getName());
                destDir.setStorageClsid(srcDir.getStorageClsid());
                copyNodes(srcDir, destDir);
            } else {
                InputStream is = src.createDocumentInputStream(e);
                try {
                    dest.createDocument(e.getName(), is);
                } finally {
                    is.close();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int indexOf(byte[] data, int offset, byte[] pattern) {
        int[] failure = computeFailure(pattern);
        int j = 0;
        if (data.length == 0) {
            return -1;
        }
        for (int i = offset; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return (i - pattern.length) + 1;
            }
        }
        return -1;
    }

    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];
        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }
        return failure;
    }
}

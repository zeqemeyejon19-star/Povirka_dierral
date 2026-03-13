package org.apache.poi.ss.extractor;

import org.apache.poi.ss.usermodel.Shape;

/* JADX INFO: loaded from: classes.dex */
public class EmbeddedData {
    private String contentType = "binary/octet-stream";
    private byte[] embeddedData;
    private String filename;
    private Shape shape;

    public EmbeddedData(String filename, byte[] embeddedData, String contentType) {
        setFilename(filename);
        setEmbeddedData(embeddedData);
        setContentType(contentType);
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        if (filename == null) {
            this.filename = "unknown.bin";
        } else {
            this.filename = filename.replaceAll("[^/\\\\]*[/\\\\]", "").trim();
        }
    }

    public byte[] getEmbeddedData() {
        return this.embeddedData;
    }

    public void setEmbeddedData(byte[] embeddedData) {
        this.embeddedData = embeddedData == null ? null : (byte[]) embeddedData.clone();
    }

    public Shape getShape() {
        return this.shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

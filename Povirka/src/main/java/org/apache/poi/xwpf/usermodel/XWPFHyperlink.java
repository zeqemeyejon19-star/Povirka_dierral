package org.apache.poi.xwpf.usermodel;

/* JADX INFO: loaded from: classes.dex */
public class XWPFHyperlink {
    String id;
    String url;

    public XWPFHyperlink(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return this.id;
    }

    public String getURL() {
        return this.url;
    }
}

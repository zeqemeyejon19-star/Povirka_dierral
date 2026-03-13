package org.apache.poi.hpsf;

import java.io.UnsupportedEncodingException;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class MutableSection extends Section {
    public MutableSection() {
    }

    public MutableSection(Section s) {
        super(s);
    }

    public MutableSection(byte[] src, int offset) throws UnsupportedEncodingException {
        super(src, offset);
    }
}

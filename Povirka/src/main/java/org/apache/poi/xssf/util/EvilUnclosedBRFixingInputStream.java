package org.apache.poi.xssf.util;

import java.io.InputStream;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.util.ReplacingInputStream;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
@Internal
@Deprecated
public class EvilUnclosedBRFixingInputStream extends ReplacingInputStream {
    public EvilUnclosedBRFixingInputStream(InputStream source) {
        super(source, "<br>", "<br/>");
    }
}

package org.apache.poi.util;

import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class TempFile {
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    private static TempFileCreationStrategy strategy = new DefaultTempFileCreationStrategy();

    public static class DefaultTempFileCreationStrategy extends org.apache.poi.util.DefaultTempFileCreationStrategy {
    }

    private TempFile() {
    }

    public static void setTempFileCreationStrategy(TempFileCreationStrategy strategy2) {
        if (strategy2 == null) {
            throw new IllegalArgumentException("strategy == null");
        }
        strategy = strategy2;
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return strategy.createTempFile(prefix, suffix);
    }

    public static File createTempDirectory(String name) throws IOException {
        return strategy.createTempDirectory(name);
    }
}

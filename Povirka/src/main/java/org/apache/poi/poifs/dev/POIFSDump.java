package org.apache.poi.poifs.dev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.NPOIFSStream;
import org.apache.poi.poifs.property.NPropertyTable;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class POIFSDump {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to dump");
            System.exit(1);
        }
        int i$ = 0;
        boolean dumpMini = false;
        for (String filename : args) {
            if (filename.equalsIgnoreCase("-dumprops") || filename.equalsIgnoreCase("-dump-props") || filename.equalsIgnoreCase("-dump-properties")) {
                dumpMini = true;
            } else if (filename.equalsIgnoreCase("-dumpmini") || filename.equalsIgnoreCase("-dump-mini") || filename.equalsIgnoreCase("-dump-ministream") || filename.equalsIgnoreCase("-dump-mini-stream")) {
                i$ = 1;
            } else {
                System.out.println("Dumping " + filename);
                FileInputStream is = new FileInputStream(filename);
                try {
                    NPOIFSFileSystem fs = new NPOIFSFileSystem(is);
                    try {
                        DirectoryEntry root = fs.getRoot();
                        String filenameWithoutPath = new File(filename).getName();
                        File dumpDir = new File(filenameWithoutPath + "_dump");
                        File file = new File(dumpDir, root.getName());
                        if (!file.exists() && !file.mkdirs()) {
                            throw new IOException("Could not create directory " + file);
                        }
                        dump(root, file);
                        if (dumpMini) {
                            HeaderBlock header = fs.getHeaderBlock();
                            dump(fs, header.getPropertyStart(), "properties", file);
                        }
                        if (i$ != 0) {
                            NPropertyTable props = fs.getPropertyTable();
                            int startBlock = props.getRoot().getStartBlock();
                            if (startBlock == -2) {
                                System.err.println("No Mini Stream in file");
                            } else {
                                dump(fs, startBlock, "mini-stream", file);
                            }
                        }
                    } finally {
                        fs.close();
                    }
                } finally {
                    is.close();
                }
            }
        }
    }

    public static void dump(DirectoryEntry root, File parent) throws IOException {
        Iterator<Entry> it = root.getEntries();
        while (it.hasNext()) {
            Entry entry = it.next();
            if (entry instanceof DocumentNode) {
                DocumentNode node = (DocumentNode) entry;
                DocumentInputStream is = new DocumentInputStream(node);
                byte[] bytes = IOUtils.toByteArray(is);
                is.close();
                OutputStream out = new FileOutputStream(new File(parent, node.getName().trim()));
                try {
                    out.write(bytes);
                } finally {
                    out.close();
                }
            } else if (entry instanceof DirectoryEntry) {
                DirectoryEntry dir = (DirectoryEntry) entry;
                File file = new File(parent, entry.getName());
                if (!file.exists() && !file.mkdirs()) {
                    throw new IOException("Could not create directory " + file);
                }
                dump(dir, file);
            } else {
                System.err.println("Skipping unsupported POIFS entry: " + entry);
            }
        }
    }

    public static void dump(NPOIFSFileSystem fs, int startBlock, String name, File parent) throws IOException {
        File file = new File(parent, name);
        FileOutputStream out = new FileOutputStream(file);
        try {
            NPOIFSStream stream = new NPOIFSStream(fs, startBlock);
            byte[] b = new byte[fs.getBigBlockSize()];
            for (ByteBuffer bb : stream) {
                int len = bb.remaining();
                bb.get(b);
                out.write(b, 0, len);
            }
        } finally {
            out.close();
        }
    }
}

package org.apache.poi.hssf.dev;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

/* JADX INFO: loaded from: classes.dex */
public class EFBiffViewer {
    String file;

    public void run() throws IOException {
        NPOIFSFileSystem fs = new NPOIFSFileSystem(new File(this.file), true);
        try {
            InputStream din = BiffViewer.getPOIFSInputStream(fs);
            try {
                HSSFRequest req = new HSSFRequest();
                req.addListenerForAllRecords(new HSSFListener() { // from class: org.apache.poi.hssf.dev.EFBiffViewer.1
                    @Override // org.apache.poi.hssf.eventusermodel.HSSFListener
                    public void processRecord(Record rec) {
                        System.out.println(rec);
                    }
                });
                HSSFEventFactory factory = new HSSFEventFactory();
                factory.processEvents(req, din);
            } finally {
                din.close();
            }
        } finally {
            fs.close();
        }
    }

    public void setFile(String file) {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1 && !args[0].equals("--help")) {
            EFBiffViewer viewer = new EFBiffViewer();
            viewer.setFile(args[0]);
            viewer.run();
        } else {
            System.out.println("EFBiffViewer");
            System.out.println("Outputs biffview of records based on HSSFEventFactory");
            System.out.println("usage: java org.apache.poi.hssf.dev.EBBiffViewer filename");
        }
    }
}

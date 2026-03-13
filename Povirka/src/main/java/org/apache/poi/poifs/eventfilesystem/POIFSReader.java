package org.apache.poi.poifs.eventfilesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.OPOIFSDocument;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.property.RootProperty;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.SmallBlockTableReader;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class POIFSReader {
    private boolean notifyEmptyDirectories = false;
    private final POIFSReaderRegistry registry = new POIFSReaderRegistry();
    private boolean registryClosed = false;

    public void read(InputStream stream) throws IOException {
        this.registryClosed = true;
        HeaderBlock header_block = new HeaderBlock(stream);
        RawDataBlockList data_blocks = new RawDataBlockList(stream, header_block.getBigBlockSize());
        new BlockAllocationTableReader(header_block.getBigBlockSize(), header_block.getBATCount(), header_block.getBATArray(), header_block.getXBATCount(), header_block.getXBATIndex(), data_blocks);
        PropertyTable properties = new PropertyTable(header_block, data_blocks);
        RootProperty root = properties.getRoot();
        processProperties(SmallBlockTableReader.getSmallDocumentBlocks(header_block.getBigBlockSize(), data_blocks, root, header_block.getSBATStart()), data_blocks, root.getChildren(), new POIFSDocumentPath());
    }

    public void registerListener(POIFSReaderListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (this.registryClosed) {
            throw new IllegalStateException();
        }
        this.registry.registerListener(listener);
    }

    public void registerListener(POIFSReaderListener listener, String name) {
        registerListener(listener, null, name);
    }

    public void registerListener(POIFSReaderListener listener, POIFSDocumentPath path, String name) {
        if (listener == null || name == null || name.length() == 0) {
            throw new NullPointerException();
        }
        if (this.registryClosed) {
            throw new IllegalStateException();
        }
        this.registry.registerListener(listener, path == null ? new POIFSDocumentPath() : path, name);
    }

    public void setNotifyEmptyDirectories(boolean notifyEmptyDirectories) {
        this.notifyEmptyDirectories = notifyEmptyDirectories;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("at least one argument required: input filename(s)");
            System.exit(1);
        }
        for (String arg : args) {
            POIFSReader reader = new POIFSReader();
            POIFSReaderListener listener = new SampleListener();
            reader.registerListener(listener);
            System.out.println("reading " + arg);
            FileInputStream istream = new FileInputStream(arg);
            reader.read(istream);
            istream.close();
        }
    }

    private void processProperties(BlockList small_blocks, BlockList big_blocks, Iterator<Property> properties, POIFSDocumentPath path) throws IOException {
        OPOIFSDocument document;
        if (!properties.hasNext() && this.notifyEmptyDirectories) {
            Iterator<POIFSReaderListener> listeners = this.registry.getListeners(path, ".");
            while (listeners.hasNext()) {
                POIFSReaderListener pl = listeners.next();
                POIFSReaderEvent pe = new POIFSReaderEvent(null, path, null);
                pl.processPOIFSReaderEvent(pe);
            }
            return;
        }
        while (properties.hasNext()) {
            Property property = properties.next();
            String name = property.getName();
            if (property.isDirectory()) {
                POIFSDocumentPath new_path = new POIFSDocumentPath(path, new String[]{name});
                DirectoryProperty dp = (DirectoryProperty) property;
                processProperties(small_blocks, big_blocks, dp.getChildren(), new_path);
            } else {
                int startBlock = property.getStartBlock();
                Iterator<POIFSReaderListener> listeners2 = this.registry.getListeners(path, name);
                if (listeners2.hasNext()) {
                    int size = property.getSize();
                    if (property.shouldUseSmallBlocks()) {
                        document = new OPOIFSDocument(name, small_blocks.fetchBlocks(startBlock, -1), size);
                    } else {
                        document = new OPOIFSDocument(name, big_blocks.fetchBlocks(startBlock, -1), size);
                    }
                    while (listeners2.hasNext()) {
                        POIFSReaderListener listener = listeners2.next();
                        listener.processPOIFSReaderEvent(new POIFSReaderEvent(new DocumentInputStream(document), path, name));
                    }
                } else if (property.shouldUseSmallBlocks()) {
                    small_blocks.fetchBlocks(startBlock, -1);
                } else {
                    big_blocks.fetchBlocks(startBlock, -1);
                }
            }
        }
    }

    private static class SampleListener implements POIFSReaderListener {
        SampleListener() {
        }

        @Override // org.apache.poi.poifs.eventfilesystem.POIFSReaderListener
        public void processPOIFSReaderEvent(POIFSReaderEvent event) {
            DocumentInputStream istream = event.getStream();
            POIFSDocumentPath path = event.getPath();
            String name = event.getName();
            try {
                byte[] data = IOUtils.toByteArray(istream);
                int pathLength = path.length();
                for (int k = 0; k < pathLength; k++) {
                    System.out.print("/" + path.getComponent(k));
                }
                System.out.println("/" + name + ": " + data.length + " bytes read");
            } catch (IOException e) {
            } catch (Throwable th) {
                IOUtils.closeQuietly(istream);
                throw th;
            }
            IOUtils.closeQuietly(istream);
        }
    }
}

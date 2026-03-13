package org.apache.poi.poifs.filesystem;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.EmptyFileException;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.filesystem.BlockStore;
import org.apache.poi.poifs.nio.ByteArrayBackedDataSource;
import org.apache.poi.poifs.nio.DataSource;
import org.apache.poi.poifs.nio.FileBackedDataSource;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.property.NPropertyTable;
import org.apache.poi.poifs.storage.BATBlock;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.BlockAllocationTableWriter;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.storage.HeaderBlockWriter;
import org.apache.poi.util.CloseIgnoringInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public class NPOIFSFileSystem extends BlockStore implements POIFSViewable, Closeable {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) NPOIFSFileSystem.class);
    private List<BATBlock> _bat_blocks;
    private DataSource _data;
    private HeaderBlock _header;
    private NPOIFSMiniStore _mini_store;
    private NPropertyTable _property_table;
    private DirectoryNode _root;
    private List<BATBlock> _xbat_blocks;
    private POIFSBigBlockSize bigBlockSize;

    public static InputStream createNonClosingInputStream(InputStream is) {
        return new CloseIgnoringInputStream(is);
    }

    private NPOIFSFileSystem(boolean newFS) {
        this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        this._header = new HeaderBlock(this.bigBlockSize);
        this._property_table = new NPropertyTable(this._header);
        this._mini_store = new NPOIFSMiniStore(this, this._property_table.getRoot(), new ArrayList(), this._header);
        this._xbat_blocks = new ArrayList();
        this._bat_blocks = new ArrayList();
        this._root = null;
        if (newFS) {
            this._data = new ByteArrayBackedDataSource(new byte[this.bigBlockSize.getBigBlockSize() * 3]);
        }
    }

    public NPOIFSFileSystem() {
        this(true);
        this._header.setBATCount(1);
        this._header.setBATArray(new int[]{1});
        BATBlock bb = BATBlock.createEmptyBATBlock(this.bigBlockSize, false);
        bb.setOurBlockIndex(1);
        this._bat_blocks.add(bb);
        setNextBlock(0, -2);
        setNextBlock(1, -3);
        this._property_table.setStartBlock(0);
    }

    public NPOIFSFileSystem(File file) throws IOException {
        this(file, true);
    }

    public NPOIFSFileSystem(File file, boolean readOnly) throws IOException {
        this(null, file, readOnly, true);
    }

    public NPOIFSFileSystem(FileChannel channel) throws IOException {
        this(channel, true);
    }

    public NPOIFSFileSystem(FileChannel channel, boolean readOnly) throws IOException {
        this(channel, null, readOnly, false);
    }

    private NPOIFSFileSystem(FileChannel channel, File srcFile, boolean readOnly, boolean closeChannelOnError) throws IOException {
        this(false);
        try {
            if (srcFile == null) {
                this._data = new FileBackedDataSource(channel, readOnly);
            } else {
                if (srcFile.length() == 0) {
                    throw new EmptyFileException();
                }
                FileBackedDataSource d = new FileBackedDataSource(srcFile, readOnly);
                channel = d.getChannel();
                this._data = d;
            }
            ByteBuffer headerBuffer = ByteBuffer.allocate(512);
            IOUtils.readFully(channel, headerBuffer);
            this._header = new HeaderBlock(headerBuffer);
            readCoreContents();
        } catch (IOException e) {
            if (closeChannelOnError && channel != null) {
                channel.close();
            }
            throw e;
        } catch (RuntimeException e2) {
            if (closeChannelOnError && channel != null) {
                channel.close();
            }
            throw e2;
        }
    }

    /* JADX WARN: Finally extract failed */
    public NPOIFSFileSystem(InputStream stream) throws IOException {
        this(false);
        ReadableByteChannel channel = null;
        try {
            ReadableByteChannel channel2 = Channels.newChannel(stream);
            ByteBuffer headerBuffer = ByteBuffer.allocate(512);
            IOUtils.readFully(channel2, headerBuffer);
            HeaderBlock headerBlock = new HeaderBlock(headerBuffer);
            this._header = headerBlock;
            BlockAllocationTableReader.sanityCheckBlockCount(headerBlock.getBATCount());
            long maxSize = BATBlock.calculateMaximumSize(this._header);
            if (maxSize > 2147483647L) {
                throw new IllegalArgumentException("Unable read a >2gb file via an InputStream");
            }
            ByteBuffer data = ByteBuffer.allocate((int) maxSize);
            headerBuffer.position(0);
            data.put(headerBuffer);
            data.position(headerBuffer.capacity());
            IOUtils.readFully(channel2, data);
            this._data = new ByteArrayBackedDataSource(data.array(), data.position());
            if (channel2 != null) {
                channel2.close();
            }
            closeInputStream(stream, true);
            readCoreContents();
        } catch (Throwable th) {
            if (0 != 0) {
                channel.close();
            }
            closeInputStream(stream, false);
            throw th;
        }
    }

    private void closeInputStream(InputStream stream, boolean success) {
        try {
            stream.close();
        } catch (IOException e) {
            if (success) {
                throw new RuntimeException(e);
            }
            LOG.log(7, "can't close input stream", e);
        }
    }

    @Removal(version = "4.0")
    @Deprecated
    public static boolean hasPOIFSHeader(InputStream inp) throws IOException {
        return FileMagic.valueOf(inp) == FileMagic.OLE2;
    }

    @Removal(version = "4.0")
    @Deprecated
    public static boolean hasPOIFSHeader(byte[] header8Bytes) {
        try {
            return hasPOIFSHeader(new ByteArrayInputStream(header8Bytes));
        } catch (IOException e) {
            throw new RuntimeException("invalid header check", e);
        }
    }

    private void readCoreContents() throws IOException {
        this.bigBlockSize = this._header.getBigBlockSize();
        BlockStore.ChainLoopDetector loopDetector = getChainLoopDetector();
        int[] arr$ = this._header.getBATArray();
        for (int i : arr$) {
            readBAT(i, loopDetector);
        }
        int remainingFATs = this._header.getBATCount() - this._header.getBATArray().length;
        int nextAt = this._header.getXBATIndex();
        for (int i2 = 0; i2 < this._header.getXBATCount(); i2++) {
            loopDetector.claim(nextAt);
            ByteBuffer fatData = getBlockAt(nextAt);
            BATBlock xfat = BATBlock.createBATBlock(this.bigBlockSize, fatData);
            xfat.setOurBlockIndex(nextAt);
            nextAt = xfat.getValueAt(this.bigBlockSize.getXBATEntriesPerBlock());
            this._xbat_blocks.add(xfat);
            int xbatFATs = Math.min(remainingFATs, this.bigBlockSize.getXBATEntriesPerBlock());
            for (int j = 0; j < xbatFATs; j++) {
                int fatAt = xfat.getValueAt(j);
                if (fatAt == -1 || fatAt == -2) {
                    break;
                }
                readBAT(fatAt, loopDetector);
            }
            remainingFATs -= xbatFATs;
        }
        this._property_table = new NPropertyTable(this._header, this);
        List<BATBlock> sbats = new ArrayList<>();
        this._mini_store = new NPOIFSMiniStore(this, this._property_table.getRoot(), sbats, this._header);
        int nextAt2 = this._header.getSBATStart();
        for (int i3 = 0; i3 < this._header.getSBATCount() && nextAt2 != -2; i3++) {
            loopDetector.claim(nextAt2);
            ByteBuffer fatData2 = getBlockAt(nextAt2);
            BATBlock sfat = BATBlock.createBATBlock(this.bigBlockSize, fatData2);
            sfat.setOurBlockIndex(nextAt2);
            sbats.add(sfat);
            nextAt2 = getNextBlock(nextAt2);
        }
    }

    private void readBAT(int batAt, BlockStore.ChainLoopDetector loopDetector) throws IOException {
        loopDetector.claim(batAt);
        ByteBuffer fatData = getBlockAt(batAt);
        BATBlock bat = BATBlock.createBATBlock(this.bigBlockSize, fatData);
        bat.setOurBlockIndex(batAt);
        this._bat_blocks.add(bat);
    }

    private BATBlock createBAT(int offset, boolean isBAT) throws IOException {
        BATBlock newBAT = BATBlock.createEmptyBATBlock(this.bigBlockSize, !isBAT);
        newBAT.setOurBlockIndex(offset);
        ByteBuffer buffer = ByteBuffer.allocate(this.bigBlockSize.getBigBlockSize());
        int writeTo = (offset + 1) * this.bigBlockSize.getBigBlockSize();
        this._data.write(buffer, writeTo);
        return newBAT;
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected ByteBuffer getBlockAt(int offset) throws IOException {
        long blockWanted = ((long) offset) + 1;
        long startAt = ((long) this.bigBlockSize.getBigBlockSize()) * blockWanted;
        try {
            return this._data.read(this.bigBlockSize.getBigBlockSize(), startAt);
        } catch (IndexOutOfBoundsException e) {
            IndexOutOfBoundsException wrapped = new IndexOutOfBoundsException("Block " + offset + " not found");
            wrapped.initCause(e);
            throw wrapped;
        }
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected ByteBuffer createBlockIfNeeded(int offset) throws IOException {
        try {
            return getBlockAt(offset);
        } catch (IndexOutOfBoundsException e) {
            long startAt = (((long) offset) + 1) * ((long) this.bigBlockSize.getBigBlockSize());
            ByteBuffer buffer = ByteBuffer.allocate(getBigBlockSize());
            this._data.write(buffer, startAt);
            return getBlockAt(offset);
        }
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected BATBlock.BATBlockAndIndex getBATBlockAndIndex(int offset) {
        return BATBlock.getBATBlockAndIndex(offset, this._header, this._bat_blocks);
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected int getNextBlock(int offset) {
        BATBlock.BATBlockAndIndex bai = getBATBlockAndIndex(offset);
        return bai.getBlock().getValueAt(bai.getIndex());
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected void setNextBlock(int offset, int nextBlock) {
        BATBlock.BATBlockAndIndex bai = getBATBlockAndIndex(offset);
        bai.getBlock().setValueAt(bai.getIndex(), nextBlock);
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected int getFreeBlock() throws IOException {
        int numSectors = this.bigBlockSize.getBATEntriesPerBlock();
        int offset = 0;
        for (BATBlock bat : this._bat_blocks) {
            if (bat.hasFreeSectors()) {
                for (int j = 0; j < numSectors; j++) {
                    int batValue = bat.getValueAt(j);
                    if (batValue == -1) {
                        return offset + j;
                    }
                }
            }
            offset += numSectors;
        }
        BATBlock bat2 = createBAT(offset, true);
        bat2.setValueAt(0, -3);
        this._bat_blocks.add(bat2);
        if (this._header.getBATCount() < 109) {
            int[] newBATs = new int[this._header.getBATCount() + 1];
            System.arraycopy(this._header.getBATArray(), 0, newBATs, 0, newBATs.length - 1);
            newBATs[newBATs.length - 1] = offset;
            this._header.setBATArray(newBATs);
        } else {
            BATBlock xbat = null;
            Iterator<BATBlock> it = this._xbat_blocks.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                BATBlock x = it.next();
                if (x.hasFreeSectors()) {
                    xbat = x;
                    break;
                }
            }
            if (xbat == null) {
                BATBlock xbat2 = createBAT(offset + 1, false);
                xbat2.setValueAt(0, offset);
                bat2.setValueAt(1, -4);
                offset++;
                if (this._xbat_blocks.size() == 0) {
                    this._header.setXBATStart(offset);
                } else {
                    List<BATBlock> list = this._xbat_blocks;
                    list.get(list.size() - 1).setValueAt(this.bigBlockSize.getXBATEntriesPerBlock(), offset);
                }
                this._xbat_blocks.add(xbat2);
                this._header.setXBATCount(this._xbat_blocks.size());
            } else {
                int i = 0;
                while (true) {
                    if (i >= this.bigBlockSize.getXBATEntriesPerBlock()) {
                        break;
                    }
                    if (xbat.getValueAt(i) != -1) {
                        i++;
                    } else {
                        xbat.setValueAt(i, offset);
                        break;
                    }
                }
            }
        }
        this._header.setBATCount(this._bat_blocks.size());
        return offset + 1;
    }

    protected long size() throws IOException {
        return this._data.size();
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected BlockStore.ChainLoopDetector getChainLoopDetector() throws IOException {
        return new BlockStore.ChainLoopDetector(this._data.size());
    }

    NPropertyTable _get_property_table() {
        return this._property_table;
    }

    public NPOIFSMiniStore getMiniStore() {
        return this._mini_store;
    }

    void addDocument(NPOIFSDocument document) {
        this._property_table.addProperty(document.getDocumentProperty());
    }

    void addDirectory(DirectoryProperty directory) {
        this._property_table.addProperty(directory);
    }

    public DocumentEntry createDocument(InputStream stream, String name) throws IOException {
        return getRoot().createDocument(name, stream);
    }

    public DocumentEntry createDocument(String name, int size, POIFSWriterListener writer) throws IOException {
        return getRoot().createDocument(name, size, writer);
    }

    public DirectoryEntry createDirectory(String name) throws IOException {
        return getRoot().createDirectory(name);
    }

    public DocumentEntry createOrUpdateDocument(InputStream stream, String name) throws IOException {
        return getRoot().createOrUpdateDocument(name, stream);
    }

    public boolean isInPlaceWriteable() {
        DataSource dataSource = this._data;
        if ((dataSource instanceof FileBackedDataSource) && ((FileBackedDataSource) dataSource).isWriteable()) {
            return true;
        }
        return false;
    }

    public void writeFilesystem() throws IOException {
        DataSource dataSource = this._data;
        if (!(dataSource instanceof FileBackedDataSource)) {
            throw new IllegalArgumentException("POIFS opened from an inputstream, so writeFilesystem() may not be called. Use writeFilesystem(OutputStream) instead");
        }
        if (!((FileBackedDataSource) dataSource).isWriteable()) {
            throw new IllegalArgumentException("POIFS opened in read only mode, so writeFilesystem() may not be called. Open the FileSystem in read-write mode first");
        }
        syncWithDataSource();
    }

    public void writeFilesystem(OutputStream stream) throws IOException {
        syncWithDataSource();
        this._data.copyTo(stream);
    }

    private void syncWithDataSource() throws IOException {
        this._mini_store.syncWithDataSource();
        NPOIFSStream propStream = new NPOIFSStream(this, this._header.getPropertyStart());
        this._property_table.preWrite();
        this._property_table.write(propStream);
        HeaderBlockWriter hbw = new HeaderBlockWriter(this._header);
        hbw.writeBlock(getBlockAt(-1));
        for (BATBlock bat : this._bat_blocks) {
            ByteBuffer block = getBlockAt(bat.getOurBlockIndex());
            BlockAllocationTableWriter.writeBlock(bat, block);
        }
        for (BATBlock bat2 : this._xbat_blocks) {
            ByteBuffer block2 = getBlockAt(bat2.getOurBlockIndex());
            BlockAllocationTableWriter.writeBlock(bat2, block2);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._data.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("two arguments required: input filename and output filename");
            System.exit(1);
        }
        FileInputStream istream = new FileInputStream(args[0]);
        try {
            FileOutputStream ostream = new FileOutputStream(args[1]);
            try {
                NPOIFSFileSystem fs = new NPOIFSFileSystem(istream);
                try {
                    fs.writeFilesystem(ostream);
                } finally {
                    fs.close();
                }
            } finally {
                ostream.close();
            }
        } finally {
            istream.close();
        }
    }

    public DirectoryNode getRoot() {
        if (this._root == null) {
            this._root = new DirectoryNode(this._property_table.getRoot(), this, (DirectoryNode) null);
        }
        return this._root;
    }

    public DocumentInputStream createDocumentInputStream(String documentName) throws IOException {
        return getRoot().createDocumentInputStream(documentName);
    }

    void remove(EntryNode entry) throws IOException {
        if (entry instanceof DocumentEntry) {
            NPOIFSDocument doc = new NPOIFSDocument((DocumentProperty) entry.getProperty(), this);
            doc.free();
        }
        this._property_table.removeProperty(entry.getProperty());
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        if (preferArray()) {
            return getRoot().getViewableArray();
        }
        return new Object[0];
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator<Object> getViewableIterator() {
        if (!preferArray()) {
            return getRoot().getViewableIterator();
        }
        return Collections.emptyList().iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return getRoot().preferArray();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        return "POIFS FileSystem";
    }

    public int getBigBlockSize() {
        return this.bigBlockSize.getBigBlockSize();
    }

    public POIFSBigBlockSize getBigBlockSizeDetails() {
        return this.bigBlockSize;
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected int getBlockStoreBlockSize() {
        return getBigBlockSize();
    }

    @Internal
    public NPropertyTable getPropertyTable() {
        return this._property_table;
    }

    @Internal
    public HeaderBlock getHeaderBlock() {
        return this._header;
    }
}

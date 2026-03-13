package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.poifs.filesystem.BlockStore;
import org.apache.poi.poifs.property.RootProperty;
import org.apache.poi.poifs.storage.BATBlock;
import org.apache.poi.poifs.storage.BlockAllocationTableWriter;
import org.apache.poi.poifs.storage.HeaderBlock;

/* JADX INFO: loaded from: classes.dex */
public class NPOIFSMiniStore extends BlockStore {
    private NPOIFSFileSystem _filesystem;
    private HeaderBlock _header;
    private NPOIFSStream _mini_stream;
    private RootProperty _root;
    private List<BATBlock> _sbat_blocks;

    protected NPOIFSMiniStore(NPOIFSFileSystem filesystem, RootProperty root, List<BATBlock> sbats, HeaderBlock header) {
        this._filesystem = filesystem;
        this._sbat_blocks = sbats;
        this._header = header;
        this._root = root;
        this._mini_stream = new NPOIFSStream(filesystem, root.getStartBlock());
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected ByteBuffer getBlockAt(int offset) throws IOException {
        int byteOffset = offset * 64;
        int bigBlockNumber = byteOffset / this._filesystem.getBigBlockSize();
        int bigBlockOffset = byteOffset % this._filesystem.getBigBlockSize();
        Iterator<ByteBuffer> it = this._mini_stream.getBlockIterator();
        for (int i = 0; i < bigBlockNumber; i++) {
            it.next();
        }
        ByteBuffer dataBlock = it.next();
        if (dataBlock == null) {
            throw new IndexOutOfBoundsException("Big block " + bigBlockNumber + " outside stream");
        }
        dataBlock.position(dataBlock.position() + bigBlockOffset);
        ByteBuffer miniBuffer = dataBlock.slice();
        miniBuffer.limit(64);
        return miniBuffer;
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected ByteBuffer createBlockIfNeeded(int offset) throws IOException {
        boolean firstInStore = false;
        if (this._mini_stream.getStartBlock() == -2) {
            firstInStore = true;
        }
        if (!firstInStore) {
            try {
                return getBlockAt(offset);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        int newBigBlock = this._filesystem.getFreeBlock();
        this._filesystem.createBlockIfNeeded(newBigBlock);
        if (firstInStore) {
            this._filesystem._get_property_table().getRoot().setStartBlock(newBigBlock);
            this._mini_stream = new NPOIFSStream(this._filesystem, newBigBlock);
        } else {
            BlockStore.ChainLoopDetector loopDetector = this._filesystem.getChainLoopDetector();
            int block = this._mini_stream.getStartBlock();
            while (true) {
                loopDetector.claim(block);
                int next = this._filesystem.getNextBlock(block);
                if (next == -2) {
                    break;
                }
                block = next;
            }
            this._filesystem.setNextBlock(block, newBigBlock);
        }
        this._filesystem.setNextBlock(newBigBlock, -2);
        return createBlockIfNeeded(offset);
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected BATBlock.BATBlockAndIndex getBATBlockAndIndex(int offset) {
        return BATBlock.getSBATBlockAndIndex(offset, this._header, this._sbat_blocks);
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
        int sectorsPerSBAT = this._filesystem.getBigBlockSizeDetails().getBATEntriesPerBlock();
        int offset = 0;
        for (int i = 0; i < this._sbat_blocks.size(); i++) {
            BATBlock sbat = this._sbat_blocks.get(i);
            if (sbat.hasFreeSectors()) {
                for (int j = 0; j < sectorsPerSBAT; j++) {
                    int sbatValue = sbat.getValueAt(j);
                    if (sbatValue == -1) {
                        return offset + j;
                    }
                }
            }
            offset += sectorsPerSBAT;
        }
        BATBlock newSBAT = BATBlock.createEmptyBATBlock(this._filesystem.getBigBlockSizeDetails(), false);
        int batForSBAT = this._filesystem.getFreeBlock();
        newSBAT.setOurBlockIndex(batForSBAT);
        if (this._header.getSBATCount() == 0) {
            this._header.setSBATStart(batForSBAT);
            this._header.setSBATBlockCount(1);
        } else {
            BlockStore.ChainLoopDetector loopDetector = this._filesystem.getChainLoopDetector();
            int batOffset = this._header.getSBATStart();
            while (true) {
                loopDetector.claim(batOffset);
                int nextBat = this._filesystem.getNextBlock(batOffset);
                if (nextBat == -2) {
                    break;
                }
                batOffset = nextBat;
            }
            this._filesystem.setNextBlock(batOffset, batForSBAT);
            HeaderBlock headerBlock = this._header;
            headerBlock.setSBATBlockCount(headerBlock.getSBATCount() + 1);
        }
        this._filesystem.setNextBlock(batForSBAT, -2);
        this._sbat_blocks.add(newSBAT);
        return offset;
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected BlockStore.ChainLoopDetector getChainLoopDetector() throws IOException {
        return new BlockStore.ChainLoopDetector(this._root.getSize());
    }

    @Override // org.apache.poi.poifs.filesystem.BlockStore
    protected int getBlockStoreBlockSize() {
        return 64;
    }

    protected void syncWithDataSource() throws IOException {
        int blocksUsed = 0;
        for (BATBlock sbat : this._sbat_blocks) {
            ByteBuffer block = this._filesystem.getBlockAt(sbat.getOurBlockIndex());
            BlockAllocationTableWriter.writeBlock(sbat, block);
            if (!sbat.hasFreeSectors()) {
                blocksUsed += this._filesystem.getBigBlockSizeDetails().getBATEntriesPerBlock();
            } else {
                blocksUsed += sbat.getUsedSectors(false);
            }
        }
        this._filesystem._get_property_table().getRoot().setSize(blocksUsed);
    }
}

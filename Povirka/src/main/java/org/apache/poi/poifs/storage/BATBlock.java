package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public final class BATBlock extends BigBlock {
    private boolean _has_free_sectors;
    private int[] _values;
    private int ourBlockIndex;

    @Override // org.apache.poi.poifs.storage.BigBlock, org.apache.poi.poifs.storage.BlockWritable
    public /* bridge */ /* synthetic */ void writeBlocks(OutputStream x0) throws IOException {
        super.writeBlocks(x0);
    }

    private BATBlock(POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        int[] iArr = new int[_entries_per_block];
        this._values = iArr;
        this._has_free_sectors = true;
        Arrays.fill(iArr, -1);
    }

    private BATBlock(POIFSBigBlockSize bigBlockSize, int[] entries, int start_index, int end_index) {
        this(bigBlockSize);
        for (int k = start_index; k < end_index; k++) {
            this._values[k - start_index] = entries[k];
        }
        int k2 = end_index - start_index;
        if (k2 == this._values.length) {
            recomputeFree();
        }
    }

    private void recomputeFree() {
        boolean hasFree = false;
        int k = 0;
        while (true) {
            int[] iArr = this._values;
            if (k >= iArr.length) {
                break;
            }
            if (iArr[k] != -1) {
                k++;
            } else {
                hasFree = true;
                break;
            }
        }
        this._has_free_sectors = hasFree;
    }

    public static BATBlock createBATBlock(POIFSBigBlockSize bigBlockSize, ByteBuffer data) {
        BATBlock block = new BATBlock(bigBlockSize);
        byte[] buffer = new byte[4];
        for (int i = 0; i < block._values.length; i++) {
            data.get(buffer);
            block._values[i] = LittleEndian.getInt(buffer);
        }
        block.recomputeFree();
        return block;
    }

    public static BATBlock createEmptyBATBlock(POIFSBigBlockSize bigBlockSize, boolean isXBAT) {
        BATBlock block = new BATBlock(bigBlockSize);
        if (isXBAT) {
            block.setXBATChain(bigBlockSize, -2);
        }
        return block;
    }

    public static BATBlock[] createBATBlocks(POIFSBigBlockSize bigBlockSize, int[] entries) {
        int block_count = calculateStorageRequirements(bigBlockSize, entries.length);
        BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        int j = 0;
        while (j < entries.length) {
            int index2 = index + 1;
            blocks[index] = new BATBlock(bigBlockSize, entries, j, remaining > _entries_per_block ? j + _entries_per_block : entries.length);
            remaining -= _entries_per_block;
            j += _entries_per_block;
            index = index2;
        }
        return blocks;
    }

    public static BATBlock[] createXBATBlocks(POIFSBigBlockSize bigBlockSize, int[] entries, int startBlock) {
        int block_count = calculateXBATStorageRequirements(bigBlockSize, entries.length);
        BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        if (block_count != 0) {
            int j = 0;
            while (j < entries.length) {
                int index2 = index + 1;
                blocks[index] = new BATBlock(bigBlockSize, entries, j, remaining > _entries_per_xbat_block ? j + _entries_per_xbat_block : entries.length);
                remaining -= _entries_per_xbat_block;
                j += _entries_per_xbat_block;
                index = index2;
            }
            int index3 = 0;
            while (index3 < blocks.length - 1) {
                blocks[index3].setXBATChain(bigBlockSize, startBlock + index3 + 1);
                index3++;
            }
            blocks[index3].setXBATChain(bigBlockSize, -2);
        }
        return blocks;
    }

    public static int calculateStorageRequirements(POIFSBigBlockSize bigBlockSize, int entryCount) {
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        return ((entryCount + _entries_per_block) - 1) / _entries_per_block;
    }

    public static int calculateXBATStorageRequirements(POIFSBigBlockSize bigBlockSize, int entryCount) {
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        return ((entryCount + _entries_per_xbat_block) - 1) / _entries_per_xbat_block;
    }

    public static long calculateMaximumSize(POIFSBigBlockSize bigBlockSize, int numBATs) {
        long size = 1 + (((long) numBATs) * ((long) bigBlockSize.getBATEntriesPerBlock()));
        return ((long) bigBlockSize.getBigBlockSize()) * size;
    }

    public static long calculateMaximumSize(HeaderBlock header) {
        return calculateMaximumSize(header.getBigBlockSize(), header.getBATCount());
    }

    public static BATBlockAndIndex getBATBlockAndIndex(int offset, HeaderBlock header, List<BATBlock> bats) {
        POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();
        int entriesPerBlock = bigBlockSize.getBATEntriesPerBlock();
        int whichBAT = offset / entriesPerBlock;
        int index = offset % entriesPerBlock;
        return new BATBlockAndIndex(index, bats.get(whichBAT));
    }

    public static BATBlockAndIndex getSBATBlockAndIndex(int offset, HeaderBlock header, List<BATBlock> sbats) {
        POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();
        int entriesPerBlock = bigBlockSize.getBATEntriesPerBlock();
        int whichSBAT = offset / entriesPerBlock;
        int index = offset % entriesPerBlock;
        return new BATBlockAndIndex(index, sbats.get(whichSBAT));
    }

    private void setXBATChain(POIFSBigBlockSize bigBlockSize, int chainIndex) {
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        this._values[_entries_per_xbat_block] = chainIndex;
    }

    public boolean hasFreeSectors() {
        return this._has_free_sectors;
    }

    public int getUsedSectors(boolean isAnXBAT) {
        int usedSectors = 0;
        int toCheck = this._values.length;
        if (isAnXBAT) {
            toCheck--;
        }
        for (int k = 0; k < toCheck; k++) {
            if (this._values[k] != -1) {
                usedSectors++;
            }
        }
        return usedSectors;
    }

    public int getValueAt(int relativeOffset) {
        int[] iArr = this._values;
        if (relativeOffset >= iArr.length) {
            throw new ArrayIndexOutOfBoundsException("Unable to fetch offset " + relativeOffset + " as the BAT only contains " + this._values.length + " entries");
        }
        return iArr[relativeOffset];
    }

    public void setValueAt(int relativeOffset, int value) {
        int[] iArr = this._values;
        int oldValue = iArr[relativeOffset];
        iArr[relativeOffset] = value;
        if (value == -1) {
            this._has_free_sectors = true;
        } else if (oldValue == -1) {
            recomputeFree();
        }
    }

    public void setOurBlockIndex(int index) {
        this.ourBlockIndex = index;
    }

    public int getOurBlockIndex() {
        return this.ourBlockIndex;
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream stream) throws IOException {
        stream.write(serialize());
    }

    void writeData(ByteBuffer block) throws IOException {
        block.put(serialize());
    }

    private byte[] serialize() {
        byte[] data = new byte[this.bigBlockSize.getBigBlockSize()];
        int offset = 0;
        int i = 0;
        while (true) {
            int[] iArr = this._values;
            if (i < iArr.length) {
                LittleEndian.putInt(data, offset, iArr[i]);
                offset += 4;
                i++;
            } else {
                return data;
            }
        }
    }

    public static class BATBlockAndIndex {
        private final BATBlock block;
        private final int index;

        private BATBlockAndIndex(int index, BATBlock block) {
            this.index = index;
            this.block = block;
        }

        public int getIndex() {
            return this.index;
        }

        public BATBlock getBlock() {
            return this.block;
        }
    }
}

package org.apache.poi.poifs.storage;

import java.io.IOException;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
abstract class BlockListImpl implements BlockList {
    private ListManagedBlock[] _blocks = new ListManagedBlock[0];
    private BlockAllocationTableReader _bat = null;

    protected BlockListImpl() {
    }

    protected void setBlocks(ListManagedBlock[] blocks) {
        this._blocks = (ListManagedBlock[]) blocks.clone();
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public void zap(int index) {
        if (index >= 0) {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            if (index < listManagedBlockArr.length) {
                listManagedBlockArr[index] = null;
            }
        }
    }

    @Internal
    public ListManagedBlock get(int index) {
        return this._blocks[index];
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public ListManagedBlock remove(int index) throws IOException {
        try {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            ListManagedBlock result = listManagedBlockArr[index];
            if (result == null) {
                throw new IOException("block[ " + index + " ] already removed - does your POIFS have circular or duplicate block references?");
            }
            listManagedBlockArr[index] = null;
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Cannot remove block[ " + index + " ]; out of range[ 0 - " + (this._blocks.length - 1) + " ]");
        }
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public ListManagedBlock[] fetchBlocks(int startBlock, int headerPropertiesStartBlock) throws IOException {
        BlockAllocationTableReader blockAllocationTableReader = this._bat;
        if (blockAllocationTableReader == null) {
            throw new IOException("Improperly initialized list: no block allocation table provided");
        }
        return blockAllocationTableReader.fetchBlocks(startBlock, headerPropertiesStartBlock, this);
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public void setBAT(BlockAllocationTableReader bat) throws IOException {
        if (this._bat != null) {
            throw new IOException("Attempt to replace existing BlockAllocationTable");
        }
        this._bat = bat;
    }

    @Override // org.apache.poi.poifs.storage.BlockList
    public int blockCount() {
        return this._blocks.length;
    }

    protected int remainingBlocks() {
        int c = 0;
        int i = 0;
        while (true) {
            ListManagedBlock[] listManagedBlockArr = this._blocks;
            if (i < listManagedBlockArr.length) {
                if (listManagedBlockArr[i] != null) {
                    c++;
                }
                i++;
            } else {
                return c;
            }
        }
    }
}

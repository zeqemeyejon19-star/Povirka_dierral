package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SmallDocumentBlockList extends BlockListImpl {
    @Override // org.apache.poi.poifs.storage.BlockListImpl, org.apache.poi.poifs.storage.BlockList
    public /* bridge */ /* synthetic */ int blockCount() {
        return super.blockCount();
    }

    @Override // org.apache.poi.poifs.storage.BlockListImpl, org.apache.poi.poifs.storage.BlockList
    public /* bridge */ /* synthetic */ ListManagedBlock[] fetchBlocks(int x0, int x1) throws IOException {
        return super.fetchBlocks(x0, x1);
    }

    @Override // org.apache.poi.poifs.storage.BlockListImpl
    public /* bridge */ /* synthetic */ ListManagedBlock get(int x0) {
        return super.get(x0);
    }

    @Override // org.apache.poi.poifs.storage.BlockListImpl, org.apache.poi.poifs.storage.BlockList
    public /* bridge */ /* synthetic */ ListManagedBlock remove(int x0) throws IOException {
        return super.remove(x0);
    }

    @Override // org.apache.poi.poifs.storage.BlockListImpl, org.apache.poi.poifs.storage.BlockList
    public /* bridge */ /* synthetic */ void setBAT(BlockAllocationTableReader x0) throws IOException {
        super.setBAT(x0);
    }

    @Override // org.apache.poi.poifs.storage.BlockListImpl, org.apache.poi.poifs.storage.BlockList
    public /* bridge */ /* synthetic */ void zap(int x0) {
        super.zap(x0);
    }

    public SmallDocumentBlockList(List<SmallDocumentBlock> blocks) {
        setBlocks((ListManagedBlock[]) blocks.toArray(new SmallDocumentBlock[blocks.size()]));
    }
}

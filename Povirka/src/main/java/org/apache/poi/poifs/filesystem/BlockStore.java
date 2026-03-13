package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.poi.poifs.storage.BATBlock;

/* JADX INFO: loaded from: classes.dex */
public abstract class BlockStore {
    protected abstract ByteBuffer createBlockIfNeeded(int i) throws IOException;

    protected abstract BATBlock.BATBlockAndIndex getBATBlockAndIndex(int i);

    protected abstract ByteBuffer getBlockAt(int i) throws IOException;

    protected abstract int getBlockStoreBlockSize();

    protected abstract ChainLoopDetector getChainLoopDetector() throws IOException;

    protected abstract int getFreeBlock() throws IOException;

    protected abstract int getNextBlock(int i);

    protected abstract void setNextBlock(int i, int i2);

    protected class ChainLoopDetector {
        private boolean[] used_blocks;

        protected ChainLoopDetector(long rawSize) {
            int blkSize = BlockStore.this.getBlockStoreBlockSize();
            int numBlocks = (int) (rawSize / ((long) blkSize));
            this.used_blocks = new boolean[rawSize % ((long) blkSize) != 0 ? numBlocks + 1 : numBlocks];
        }

        protected void claim(int offset) {
            boolean[] zArr = this.used_blocks;
            if (offset >= zArr.length) {
                return;
            }
            if (zArr[offset]) {
                throw new IllegalStateException("Potential loop detected - Block " + offset + " was already claimed but was just requested again");
            }
            zArr[offset] = true;
        }
    }
}

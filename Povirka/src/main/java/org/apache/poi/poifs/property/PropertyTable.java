package org.apache.poi.poifs.property;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.storage.BlockWritable;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.storage.PropertyBlock;
import org.apache.poi.poifs.storage.RawDataBlockList;

/* JADX INFO: loaded from: classes.dex */
public final class PropertyTable extends PropertyTableBase implements BlockWritable {
    private POIFSBigBlockSize _bigBigBlockSize;
    private BlockWritable[] _blocks;

    public PropertyTable(HeaderBlock headerBlock) {
        super(headerBlock);
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        this._blocks = null;
    }

    public PropertyTable(HeaderBlock headerBlock, RawDataBlockList blockList) throws IOException {
        super(headerBlock, PropertyFactory.convertToProperties(blockList.fetchBlocks(headerBlock.getPropertyStart(), -1)));
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        this._blocks = null;
    }

    public void preWrite() {
        Property[] properties = (Property[]) this._properties.toArray(new Property[this._properties.size()]);
        for (int k = 0; k < properties.length; k++) {
            properties[k].setIndex(k);
        }
        this._blocks = PropertyBlock.createPropertyBlockArray(this._bigBigBlockSize, this._properties);
        for (Property property : properties) {
            property.preWrite();
        }
    }

    @Override // org.apache.poi.poifs.filesystem.BATManaged
    public int countBlocks() {
        BlockWritable[] blockWritableArr = this._blocks;
        if (blockWritableArr == null) {
            return 0;
        }
        return blockWritableArr.length;
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream stream) throws IOException {
        if (this._blocks != null) {
            BlockWritable[] arr$ = this._blocks;
            for (BlockWritable _block : arr$) {
                _block.writeBlocks(stream);
            }
        }
    }
}

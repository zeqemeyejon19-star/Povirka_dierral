package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.Property;

/* JADX INFO: loaded from: classes.dex */
public final class PropertyBlock extends BigBlock {
    private Property[] _properties;

    @Override // org.apache.poi.poifs.storage.BigBlock, org.apache.poi.poifs.storage.BlockWritable
    public /* bridge */ /* synthetic */ void writeBlocks(OutputStream x0) throws IOException {
        super.writeBlocks(x0);
    }

    private PropertyBlock(POIFSBigBlockSize bigBlockSize, Property[] properties, int offset) {
        super(bigBlockSize);
        this._properties = new Property[bigBlockSize.getPropertiesPerBlock()];
        int j = 0;
        while (true) {
            Property[] propertyArr = this._properties;
            if (j < propertyArr.length) {
                propertyArr[j] = properties[j + offset];
                j++;
            } else {
                return;
            }
        }
    }

    public static BlockWritable[] createPropertyBlockArray(POIFSBigBlockSize bigBlockSize, List<Property> properties) {
        int _properties_per_block = bigBlockSize.getPropertiesPerBlock();
        int block_count = ((properties.size() + _properties_per_block) - 1) / _properties_per_block;
        Property[] to_be_written = new Property[block_count * _properties_per_block];
        System.arraycopy(properties.toArray(new Property[0]), 0, to_be_written, 0, properties.size());
        for (int j = properties.size(); j < to_be_written.length; j++) {
            to_be_written[j] = new Property() { // from class: org.apache.poi.poifs.storage.PropertyBlock.1
                @Override // org.apache.poi.poifs.property.Property
                protected void preWrite() {
                }

                @Override // org.apache.poi.poifs.property.Property
                public boolean isDirectory() {
                    return false;
                }
            };
        }
        BlockWritable[] rvalue = new BlockWritable[block_count];
        for (int j2 = 0; j2 < block_count; j2++) {
            rvalue[j2] = new PropertyBlock(bigBlockSize, to_be_written, j2 * _properties_per_block);
        }
        return rvalue;
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream stream) throws IOException {
        int _properties_per_block = this.bigBlockSize.getPropertiesPerBlock();
        for (int j = 0; j < _properties_per_block; j++) {
            this._properties[j].writeData(stream);
        }
    }
}

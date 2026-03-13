package org.apache.poi.hpsf;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

/* JADX INFO: loaded from: classes.dex */
@Internal
class VersionedStream {
    private final GUID _versionGuid = new GUID();
    private final IndirectPropertyName _streamName = new IndirectPropertyName();

    VersionedStream() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        this._versionGuid.read(lei);
        this._streamName.read(lei);
    }
}

package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class MultiFormatUPCEANReader extends OneDReader {
    private final UPCEANReader[] readers;

    public MultiFormatUPCEANReader(Map<DecodeHintType, ?> hints) {
        Collection<BarcodeFormat> possibleFormats = hints == null ? null : (Collection) hints.get(DecodeHintType.POSSIBLE_FORMATS);
        Collection<UPCEANReader> readers = new ArrayList<>();
        if (possibleFormats != null) {
            if (possibleFormats.contains(BarcodeFormat.EAN_13)) {
                readers.add(new EAN13Reader());
            } else if (possibleFormats.contains(BarcodeFormat.UPC_A)) {
                readers.add(new UPCAReader());
            }
            if (possibleFormats.contains(BarcodeFormat.EAN_8)) {
                readers.add(new EAN8Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.UPC_E)) {
                readers.add(new UPCEReader());
            }
        }
        if (readers.isEmpty()) {
            readers.add(new EAN13Reader());
            readers.add(new EAN8Reader());
            readers.add(new UPCEReader());
        }
        this.readers = (UPCEANReader[]) readers.toArray(new UPCEANReader[readers.size()]);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0033  */
    @Override // com.google.zxing.oned.OneDReader
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.google.zxing.Result decodeRow(int r19, com.google.zxing.common.BitArray r20, java.util.Map<com.google.zxing.DecodeHintType, ?> r21) throws com.google.zxing.NotFoundException {
        /*
            r18 = this;
            r1 = r21
            int[] r2 = com.google.zxing.oned.UPCEANReader.findStartGuardPattern(r20)
            r3 = r18
            com.google.zxing.oned.UPCEANReader[] r4 = r3.readers
            int r5 = r4.length
            r6 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
        L10:
            if (r8 >= r5) goto L90
            r12 = r4[r8]
            r13 = r19
            r14 = r20
            com.google.zxing.Result r0 = r12.decodeRow(r13, r14, r2, r1)     // Catch: com.google.zxing.ReaderException -> L85
            r9 = r0
            com.google.zxing.BarcodeFormat r0 = r0.getBarcodeFormat()     // Catch: com.google.zxing.ReaderException -> L85
            com.google.zxing.BarcodeFormat r15 = com.google.zxing.BarcodeFormat.EAN_13     // Catch: com.google.zxing.ReaderException -> L85
            if (r0 != r15) goto L33
            java.lang.String r0 = r9.getText()     // Catch: com.google.zxing.ReaderException -> L4f
            char r0 = r0.charAt(r6)     // Catch: com.google.zxing.ReaderException -> L4f
            r15 = 48
            if (r0 != r15) goto L33
            r0 = 1
            goto L34
        L33:
            r0 = 0
        L34:
            if (r1 != 0) goto L38
            r15 = 0
            goto L40
        L38:
            com.google.zxing.DecodeHintType r15 = com.google.zxing.DecodeHintType.POSSIBLE_FORMATS     // Catch: com.google.zxing.ReaderException -> L85
            java.lang.Object r15 = r1.get(r15)     // Catch: com.google.zxing.ReaderException -> L85
            java.util.Collection r15 = (java.util.Collection) r15     // Catch: com.google.zxing.ReaderException -> L85
        L40:
            r10 = r15
            if (r15 == 0) goto L53
            com.google.zxing.BarcodeFormat r15 = com.google.zxing.BarcodeFormat.UPC_A     // Catch: com.google.zxing.ReaderException -> L4f
            boolean r15 = r10.contains(r15)     // Catch: com.google.zxing.ReaderException -> L4f
            if (r15 == 0) goto L4d
            goto L53
        L4d:
            r15 = 0
            goto L54
        L4f:
            r0 = move-exception
            r16 = r2
            goto L88
        L53:
            r15 = 1
        L54:
            if (r0 == 0) goto L80
            if (r15 == 0) goto L80
            com.google.zxing.Result r6 = new com.google.zxing.Result     // Catch: com.google.zxing.ReaderException -> L85
            java.lang.String r7 = r9.getText()     // Catch: com.google.zxing.ReaderException -> L85
            r17 = r0
            r0 = 1
            java.lang.String r0 = r7.substring(r0)     // Catch: com.google.zxing.ReaderException -> L85
            byte[] r7 = r9.getRawBytes()     // Catch: com.google.zxing.ReaderException -> L85
            com.google.zxing.ResultPoint[] r1 = r9.getResultPoints()     // Catch: com.google.zxing.ReaderException -> L85
            r16 = r2
            com.google.zxing.BarcodeFormat r2 = com.google.zxing.BarcodeFormat.UPC_A     // Catch: com.google.zxing.ReaderException -> L7e
            r6.<init>(r0, r7, r1, r2)     // Catch: com.google.zxing.ReaderException -> L7e
            r0 = r11
            r11 = r6
            java.util.Map r0 = r9.getResultMetadata()     // Catch: com.google.zxing.ReaderException -> L7e
            r6.putAllMetadata(r0)     // Catch: com.google.zxing.ReaderException -> L7e
            return r11
        L7e:
            r0 = move-exception
            goto L88
        L80:
            r17 = r0
            r16 = r2
            return r9
        L85:
            r0 = move-exception
            r16 = r2
        L88:
            int r8 = r8 + 1
            r1 = r21
            r2 = r16
            r6 = 0
            goto L10
        L90:
            com.google.zxing.NotFoundException r0 = com.google.zxing.NotFoundException.getNotFoundInstance()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.MultiFormatUPCEANReader.decodeRow(int, com.google.zxing.common.BitArray, java.util.Map):com.google.zxing.Result");
    }

    @Override // com.google.zxing.oned.OneDReader, com.google.zxing.Reader
    public void reset() {
        for (UPCEANReader uPCEANReader : this.readers) {
            uPCEANReader.reset();
        }
    }
}

package androidmads.library.qrgenearator;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidmads.library.qrgenearator.QRGContents;
import androidx.core.view.ViewCompat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.util.EnumMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class QRGEncoder {
    private int BLACK;
    private int WHITE;
    private String contents;
    private int dimension;
    private String displayContents;
    private boolean encoded;
    private BarcodeFormat format;
    private String title;

    public void setColorWhite(int color) {
        this.WHITE = color;
    }

    public void setColorBlack(int color) {
        this.BLACK = color;
    }

    public int getColorWhite() {
        return this.WHITE;
    }

    public int getColorBlack() {
        return this.BLACK;
    }

    public QRGEncoder(String data, String type) {
        this.WHITE = -1;
        this.BLACK = ViewCompat.MEASURED_STATE_MASK;
        this.dimension = Integer.MIN_VALUE;
        this.contents = null;
        this.displayContents = null;
        this.title = null;
        this.format = null;
        this.encoded = false;
        this.encoded = encodeContents(data, null, QRGContents.Type.TEXT);
    }

    public QRGEncoder(String data, String type, int dimension) {
        this.WHITE = -1;
        this.BLACK = ViewCompat.MEASURED_STATE_MASK;
        this.dimension = Integer.MIN_VALUE;
        this.contents = null;
        this.displayContents = null;
        this.title = null;
        this.format = null;
        this.encoded = false;
        this.dimension = dimension;
        this.encoded = encodeContents(data, null, QRGContents.Type.TEXT);
    }

    public QRGEncoder(String data, Bundle bundle, String type, int dimension) {
        this.WHITE = -1;
        this.BLACK = ViewCompat.MEASURED_STATE_MASK;
        this.dimension = Integer.MIN_VALUE;
        this.contents = null;
        this.displayContents = null;
        this.title = null;
        this.format = null;
        this.encoded = false;
        this.dimension = dimension;
        this.encoded = encodeContents(data, bundle, type);
    }

    public String getTitle() {
        return this.title;
    }

    private boolean encodeContents(String data, Bundle bundle, String type) {
        BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
        this.format = barcodeFormat;
        if (barcodeFormat == BarcodeFormat.QR_CODE) {
            this.format = BarcodeFormat.QR_CODE;
            encodeQRCodeContents(data, bundle, type);
        } else if (data != null && data.length() > 0) {
            this.contents = data;
            this.displayContents = data;
            this.title = "Text";
        }
        String str = this.contents;
        return str != null && str.length() > 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0049  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void encodeQRCodeContents(java.lang.String r13, android.os.Bundle r14, java.lang.String r15) {
        /*
            Method dump skipped, instruction units count: 676
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: androidmads.library.qrgenearator.QRGEncoder.encodeQRCodeContents(java.lang.String, android.os.Bundle, java.lang.String):void");
    }

    public Bitmap getBitmap() {
        if (!this.encoded) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        try {
            String encoding = guessAppropriateEncoding(this.contents);
            if (encoding != null) {
                hints = new EnumMap<>(EncodeHintType.class);
                hints.put(EncodeHintType.CHARACTER_SET, encoding);
            }
            MultiFormatWriter writer = new MultiFormatWriter();
            String str = this.contents;
            BarcodeFormat barcodeFormat = this.format;
            int i = this.dimension;
            BitMatrix result = writer.encode(str, barcodeFormat, i, i, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? getColorBlack() : getColorWhite();
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    private String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 255) {
                return "UTF-8";
            }
        }
        return null;
    }

    private String trim(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        if (result.length() == 0) {
            return null;
        }
        return result;
    }

    private String escapeVCard(String input) {
        if (input == null || (input.indexOf(58) < 0 && input.indexOf(59) < 0)) {
            return input;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == ':' || c == ';') {
                result.append('\\');
            }
            result.append(c);
        }
        return result.toString();
    }
}

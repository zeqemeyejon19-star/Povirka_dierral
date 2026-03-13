package androidmads.library.qrgenearator;

import android.graphics.Bitmap;
import android.util.Log;
import androidmads.library.qrgenearator.QRGContents;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class QRGSaver {
    public boolean save(String saveLocation, String imageName, Bitmap bitmap, int imageFormat) {
        String imageDetail = saveLocation + imageName + imgFormat(imageFormat);
        File file = new File(saveLocation);
        if (!file.exists()) {
            file.mkdir();
        } else {
            Log.d("QRGSaver", "Folder Exists");
        }
        try {
            FileOutputStream outStream = new FileOutputStream(imageDetail);
            bitmap.compress((Bitmap.CompressFormat) compressFormat(imageFormat), 100, outStream);
            outStream.flush();
            outStream.close();
            return true;
        } catch (IOException e) {
            Log.d("QRGSaver", e.toString());
            return false;
        }
    }

    public boolean save(String saveLocation, String imageName, Bitmap bitmap) {
        return save(saveLocation, imageName, bitmap, QRGContents.ImageType.IMAGE_PNG);
    }

    private String imgFormat(int imageFormat) {
        return imageFormat == QRGContents.ImageType.IMAGE_PNG ? ".png" : ".jpg";
    }

    private Comparable<? extends Comparable<? extends Comparable<?>>> compressFormat(int imageFormat) {
        return imageFormat == QRGContents.ImageType.IMAGE_PNG ? Bitmap.CompressFormat.PNG : imageFormat == QRGContents.ImageType.IMAGE_WEBP ? Bitmap.CompressFormat.WEBP : Bitmap.CompressFormat.JPEG;
    }
}

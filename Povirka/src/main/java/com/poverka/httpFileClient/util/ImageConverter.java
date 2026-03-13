package com.poverka.httpFileClient.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class ImageConverter {
    private ArrayList<Byte> imageByteList = new ArrayList<>();
    private int expectedSize = 0;

    public void addToByteList(String str) {
        String s = str.replaceAll("\\s+", "");
        for (int i = 0; i < s.length() / 2; i++) {
            this.imageByteList.add(Byte.valueOf((byte) ((Character.digit(s.charAt(i * 2), 16) << 4) + Character.digit(s.charAt((i * 2) + 1), 16))));
        }
    }

    public void toByteList(ArrayList<String> list) {
        for (String str : list) {
            String s = str.replaceAll("\\s+", "");
            for (int i = 0; i < s.length() / 2; i++) {
                this.imageByteList.add(Byte.valueOf((byte) ((Character.digit(s.charAt(i * 2), 16) << 4) + Character.digit(s.charAt((i * 2) + 1), 16))));
            }
        }
    }

    public byte[] getImageBytes() {
        byte[] imageByteArray = new byte[this.imageByteList.size()];
        for (int i = 0; i < this.imageByteList.size(); i++) {
            imageByteArray[i] = this.imageByteList.get(i).byteValue();
        }
        return imageByteArray;
    }

    public Bitmap getBitmap() {
        byte[] imageByteArray = getImageBytes();
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }

    public boolean areSizesEqual() {
        Log.d("ImageConverter", String.format("Expected: %s, actual: %s", Integer.valueOf(this.expectedSize), Integer.valueOf(this.imageByteList.size())));
        return this.expectedSize == this.imageByteList.size();
    }

    public void setExpectedSize(int size) {
        this.expectedSize = size;
    }

    public void clearImageByteList() {
        this.imageByteList.clear();
    }
}

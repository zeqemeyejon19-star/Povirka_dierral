package com.poverka.httpFileClient.util;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class RestorationHelper {
    private static List<String> filesToRestore;
    private int measurementNumber;
    private int reiterationNumber;

    public RestorationHelper() {
        filesToRestore = new ArrayList();
    }

    public String getNextFilePath() {
        if (filesToRestore.size() == 0) {
            return null;
        }
        return String.format(Locale.ROOT, "1/current/%s", filesToRestore.get(0));
    }

    public void setCurrentFileRestored() throws IndexOutOfBoundsException {
        filesToRestore.remove(0);
    }

    public boolean setFilesToRestore(List<String> filesToRestore2) {
        List<String> listSortFiles = sortFiles(filesToRestore2);
        filesToRestore = listSortFiles;
        return listSortFiles != null;
    }

    public int getMeasurementNumber() {
        return this.measurementNumber;
    }

    public int getReiterationNumber() {
        return this.reiterationNumber;
    }

    public void setMeasurementParams(int measurementNumber, int reiterationNumber) {
        this.measurementNumber = measurementNumber;
        this.reiterationNumber = reiterationNumber;
    }

    private static List<String> sortFiles(List<String> list) {
        ArrayList<String> result = new ArrayList<>();
        if (!list.contains("system.json")) {
            return null;
        }
        result.add("system.json");
        if (list.contains("counter_info.json")) {
            result.add("counter_info.json");
        }
        if (!list.contains(String.format(Locale.ROOT, "photo_%d%d%d.jpeg", 0, 0, 1))) {
            return null;
        }
        result.add(String.format(Locale.ROOT, "photo_%d%d%d.jpeg", 0, 0, 1));
        for (int measNumber = 1; measNumber <= 3; measNumber++) {
            for (int reitNumber = 0; reitNumber <= 3; reitNumber++) {
                if (list.contains(String.format(Locale.ROOT, "meast_%d%d.json", Integer.valueOf(measNumber), Integer.valueOf(reitNumber))) && list.contains(String.format(Locale.ROOT, "photo_%d%d%d.jpeg", Integer.valueOf(measNumber), Integer.valueOf(reitNumber), 1)) && list.contains(String.format(Locale.ROOT, "photo_%d%d%d.jpeg", Integer.valueOf(measNumber), Integer.valueOf(reitNumber), 2))) {
                    result.add(String.format(Locale.ROOT, "meast_%d%d.json", Integer.valueOf(measNumber), Integer.valueOf(reitNumber)));
                    result.add(String.format(Locale.ROOT, "photo_%d%d%d.jpeg", Integer.valueOf(measNumber), Integer.valueOf(reitNumber), 1));
                    result.add(String.format(Locale.ROOT, "photo_%d%d%d.jpeg", Integer.valueOf(measNumber), Integer.valueOf(reitNumber), 2));
                }
            }
        }
        for (String s : result) {
            Log.d("After sort", s);
        }
        return result;
    }
}

package com.poverka.httpFileClient.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class MyFileReader {
    private static final String LOG_NAME = "file.log";

    public static String readAndroidFile(File path, String fileName) {
        File file = new File(path, fileName);
        StringBuilder text = new StringBuilder();
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    text.append(line);
                } else {
                    br.close();
                    reader.close();
                    return text.toString();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeInternalFile(Context context, String fileName, String text) {
        File file = new File(context.getFilesDir(), fileName);
        try {
            if (file.exists()) {
                if (file.delete()) {
                    Log.d("Internal File Writer", String.format(Locale.ROOT, "file '%s' has been deleted from 'Internal'", file.getName()));
                } else {
                    Log.e("Internal File Writer", String.format(Locale.ROOT, "can not delete file '%s' from 'Internal'", file.getName()));
                }
            } else {
                FileWriter writer = new FileWriter(file);
                writer.write(text);
                writer.flush();
                writer.close();
                Log.d("Internal File Writer", String.format(Locale.ROOT, "file '%s' has been written in 'Internal'", file.getName()));
            }
            if (file.createNewFile()) {
                FileWriter writer2 = new FileWriter(file);
                writer2.write(text);
                writer2.flush();
                writer2.close();
                Log.d("Internal File Writer", String.format(Locale.ROOT, "file '%s' has been written in 'Internal'", file.getName()));
                return;
            }
            Log.e("Internal File Writer", String.format(Locale.ROOT, "can not create file '%s' in 'Internal'", file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeInternalFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        try {
            if (file.exists()) {
                if (file.delete()) {
                    Log.d("Internal File Deleter", String.format(Locale.ROOT, "file '%s' has been deleted from 'Internal'", file.getName()));
                } else {
                    Log.e("Internal File Deleter", String.format(Locale.ROOT, "can not delete file '%s' from 'Internal'", file.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void appendLog(Context context, String tag, String text) {
        File logFile = new File(context.getFilesDir(), LOG_NAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            try {
                buf.append((CharSequence) String.format(Locale.ROOT, "%d\t%s\t%s", Long.valueOf(Calendar.getInstance().getTimeInMillis()), tag, text));
                buf.newLine();
                buf.close();
            } finally {
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void appendLog(Context context, String tag, String text, StackTraceElement[] stack) {
        File logFile = new File(context.getFilesDir(), LOG_NAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            try {
                buf.append((CharSequence) String.format(Locale.ROOT, "%d\t%s\t%s", Long.valueOf(Calendar.getInstance().getTimeInMillis()), tag, text));
                buf.newLine();
                for (StackTraceElement element : stack) {
                    buf.append((CharSequence) "\t").append((CharSequence) element.toString());
                    buf.newLine();
                }
                buf.close();
            } finally {
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static void removeLog(Context context) {
        removeInternalFile(context, LOG_NAME);
    }

    public static File getLogFile(Context context) {
        return new File(context.getFilesDir(), LOG_NAME);
    }

    public static File createPoverkaFolder() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "Poverka");
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    public static File createFolderInPoverkaFolder(String date) {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "Poverka");
        File dayFolder = new File(folder, date.replace('.', '_'));
        if (!dayFolder.exists()) {
            dayFolder.mkdir();
        }
        return dayFolder;
    }

    public static ArrayList<String> getDaysFromPoverka() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "Poverka");
        ArrayList<String> list = new ArrayList<>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                String[] splt = fileEntry.getName().split("_");
                if (splt.length == 3 && isNumeric(splt[0]) && isNumeric(splt[1]) && isNumeric(splt[2])) {
                    list.add(fileEntry.getName());
                }
            }
        }
        Collections.sort(list, new Comparator<String>() { // from class: com.poverka.httpFileClient.util.MyFileReader.1
            @Override // java.util.Comparator
            public int compare(String file1, String file2) {
                String[] splt1 = file1.split("_");
                String[] splt2 = file2.split("_");
                if (Integer.parseInt(splt1[2]) > Integer.parseInt(splt2[2])) {
                    return 1;
                }
                if (Integer.parseInt(splt1[2]) < Integer.parseInt(splt2[2])) {
                    return -1;
                }
                if (Integer.parseInt(splt1[1]) > Integer.parseInt(splt2[1])) {
                    return 1;
                }
                if (Integer.parseInt(splt1[1]) < Integer.parseInt(splt2[1])) {
                    return -1;
                }
                return Integer.compare(Integer.parseInt(splt1[0]), Integer.parseInt(splt2[0]));
            }
        });
        return list;
    }

    public static void cleanOldest(ArrayList<String> list) {
        if (list.size() > 30) {
            Collections.sort(list, new Comparator<String>() { // from class: com.poverka.httpFileClient.util.MyFileReader.2
                @Override // java.util.Comparator
                public int compare(String file1, String file2) {
                    String[] splt1 = file1.split("_");
                    String[] splt2 = file2.split("_");
                    if (Integer.parseInt(splt1[2]) > Integer.parseInt(splt2[2])) {
                        return 1;
                    }
                    if (Integer.parseInt(splt1[2]) < Integer.parseInt(splt2[2])) {
                        return -1;
                    }
                    if (Integer.parseInt(splt1[1]) > Integer.parseInt(splt2[1])) {
                        return 1;
                    }
                    if (Integer.parseInt(splt1[1]) < Integer.parseInt(splt2[1])) {
                        return -1;
                    }
                    return Integer.compare(Integer.parseInt(splt1[0]), Integer.parseInt(splt2[0]));
                }
            });
            File folder = new File(Environment.getExternalStorageDirectory().toString(), "Poverka");
            File oldestFolder = new File(folder, list.get(0));
            deleteAllInside(oldestFolder);
            oldestFolder.delete();
        }
    }

    public static class UpdateStorage implements MediaScannerConnection.MediaScannerConnectionClient {
        private File file;
        private MediaScannerConnection scanner;

        public UpdateStorage(Context context, File f) {
            this.file = f;
            MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(context, this);
            this.scanner = mediaScannerConnection;
            mediaScannerConnection.connect();
        }

        @Override // android.media.MediaScannerConnection.MediaScannerConnectionClient
        public void onMediaScannerConnected() {
            this.scanner.scanFile(this.file.getAbsolutePath(), null);
        }

        @Override // android.media.MediaScannerConnection.OnScanCompletedListener
        public void onScanCompleted(String path, Uri uri) {
            this.scanner.disconnect();
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void deleteAllInside(File folder) {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                deleteAllInside(fileEntry);
            }
            fileEntry.delete();
        }
    }
}

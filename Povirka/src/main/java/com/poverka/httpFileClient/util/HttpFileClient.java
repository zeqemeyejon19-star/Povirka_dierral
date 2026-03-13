package com.poverka.httpFileClient.util;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import com.poverka.httpFileClient.activity.MainActivity;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class HttpFileClient {
    private static final int BUFFER_SIZE = 4096;
    private final OnMessageReceived mMessageListener;

    public interface OnMessageReceived {
        void messageReceived(Type type, String str, InputStream inputStream);
    }

    public enum Type {
        DELETE,
        UPLOAD,
        DOWNLOAD,
        SHOW,
        SERVER
    }

    public HttpFileClient(OnMessageReceived listener) {
        this.mMessageListener = listener;
    }

    public void delete(String filePath) {
        DeleteThread thread = new DeleteThread(filePath);
        thread.start();
    }

    public void upload(String filePath, String data) {
        UploadThread thread = new UploadThread(filePath, data);
        thread.start();
    }

    public void download(String filePath) {
        DownloadThread thread = new DownloadThread(filePath);
        thread.start();
    }

    public void downloadServer(String path, String authorization) {
        DownloadServer thread = new DownloadServer(path, authorization);
        thread.start();
    }

    public void headDownloadServer(String path) {
        HeadDownloadServer thread = new HeadDownloadServer(path);
        thread.start();
    }

    public void uploadServer(String path, String data, String authorization) {
        UploadServer thread = new UploadServer(path, data, authorization);
        thread.start();
    }

    public void headUploadServer(String path, File file) {
        HeadUploadServer thread = new HeadUploadServer(path, file);
        thread.start();
    }

    public void headDownloadApk(int id, String apkName) {
        HeadDownloadApk thread = new HeadDownloadApk(id, apkName);
        thread.start();
    }

    public void show(String path) {
        ShowThread thread = new ShowThread(path);
        thread.start();
    }

    private class DeleteThread extends Thread {
        private String filePath;

        private DeleteThread(String filePath) {
            this.filePath = filePath;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                System.out.println(String.format(Locale.ROOT, "Deleting http://%s/delete/%s", MainActivity.IP, this.filePath));
                URL url = new URL("http://" + MainActivity.IP + "/delete/" + this.filePath);
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setRequestMethod("POST");
                int responseCode = httpUrlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String line = responseStreamReader.readLine();
                        if (line == null) {
                            break;
                        } else if (line.startsWith("<tr><td><a href=")) {
                            Log.d("CatalogClient", line);
                            stringBuilder.append(line).append("\n");
                        }
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("File deleted");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.DELETE, this.filePath, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    System.out.println("Problem with delete. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.DELETE, this.filePath, null);
                }
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class UploadThread extends Thread {
        private String data;
        private String filePath;

        private UploadThread(String filePath, String data) {
            this.filePath = filePath;
            this.data = data;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                System.out.println(String.format(Locale.ROOT, "Uploading http://%s/uploadfile/%s", MainActivity.IP, this.filePath));
                URL url = new URL("http://" + MainActivity.IP + "/uploadfile/" + this.filePath);
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
                DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
                request.write(this.data.getBytes());
                request.flush();
                request.close();
                int responseCode = httpUrlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String line = responseStreamReader.readLine();
                        if (line == null) {
                            break;
                        } else if (line.startsWith("<tr><td><a href=")) {
                            Log.d("CatalogClient", line);
                            stringBuilder.append(line).append("\n");
                        }
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("File uploaded");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.UPLOAD, this.filePath, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    System.out.println("Problem with upload. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.UPLOAD, this.filePath, null);
                }
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadThread extends Thread {
        private String filePath;

        private DownloadThread(String filePath) {
            this.filePath = filePath;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                Thread.sleep(100L);
                System.out.println(String.format(Locale.ROOT, "Downloading http://%s/%s", MainActivity.IP, this.filePath));
                BufferedInputStream inputStream = new BufferedInputStream(new URL("http://" + MainActivity.IP + "/" + this.filePath).openStream());
                System.out.println("File downloaded");
                HttpFileClient.this.mMessageListener.messageReceived(Type.DOWNLOAD, this.filePath, inputStream);
                inputStream.close();
            } catch (IOException | InterruptedException e) {
                System.out.println("No file to download.");
                e.printStackTrace();
                HttpFileClient.this.mMessageListener.messageReceived(Type.DOWNLOAD, this.filePath, null);
            }
        }
    }

    private class DownloadServer extends Thread {
        private static final String domain = "upload.serrp.info:9443";
        private String authorization;
        private String path;

        private DownloadServer(String path, String authorization) {
            this.path = path;
            this.authorization = authorization;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                System.out.println(String.format(Locale.ROOT, "Downloading server https://%s/%s", domain, this.path));
                URL url = new URL("https://upload.serrp.info:9443/" + this.path);
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("Authorization", this.authorization);
                int responseCode = httpUrlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String line = responseStreamReader.readLine();
                        if (line == null) {
                            break;
                        } else {
                            stringBuilder.append(line).append("\n");
                        }
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("Downloaded from server");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    System.out.println("Problem with download. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                }
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                e.printStackTrace();
            }
        }
    }

    private class HeadDownloadServer extends Thread {
        private static final String domain = "head-point.serrp.info:9443";
        private String authorization;
        private String path;

        private HeadDownloadServer(String path) {
            String auth = "station_head1:I8X_V..oe%OX";
            byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
            String authHeaderValue = "Basic " + new String(encodedAuth);
            this.path = path;
            this.authorization = authHeaderValue;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                System.out.println(String.format(Locale.ROOT, "Downloading server https://%s/%s", domain, this.path));
                URL url = new URL("https://head-point.serrp.info:9443/" + this.path);
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("Authorization", this.authorization);
                int responseCode = httpUrlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String line = responseStreamReader.readLine();
                        if (line == null) {
                            break;
                        } else {
                            stringBuilder.append(line).append("\n");
                        }
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("Downloaded from server");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    System.out.println("Problem with download. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                }
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                e.printStackTrace();
            }
        }
    }

    private class UploadServer extends Thread {
        private static final String domain = "upload.serrp.info:9443";
        private String authorization;
        private String data;
        private String path;

        private UploadServer(String path, String data, String authorization) {
            this.path = path;
            this.data = data;
            this.authorization = authorization;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                System.out.println(String.format(Locale.ROOT, "Uploading server https://%s/%s", domain, this.path));
                URL url = new URL("https://upload.serrp.info:9443/" + this.path);
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                httpUrlConnection.setRequestProperty("Authorization", this.authorization);
                DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
                request.write(this.data.getBytes());
                request.flush();
                request.close();
                int responseCode = httpUrlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String line = responseStreamReader.readLine();
                        if (line == null) {
                            break;
                        } else {
                            stringBuilder.append(line).append("\n");
                        }
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("Uploaded to server");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    System.out.println("Problem with upload. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                }
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                e.printStackTrace();
            }
        }
    }

    private class HeadUploadServer extends Thread {
        private static final String domain = "head-point.serrp.info:9443";
        private final String authorization;
        private final File file;
        private final String path;

        private HeadUploadServer(String path, File file) {
            String auth = "station_head1:I8X_V..oe%OX";
            byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
            this.path = path;
            this.file = file;
            this.authorization = "Basic " + new String(encodedAuth);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws IOException {
            HttpURLConnection httpUrlConnection;
            int responseCode;
            BufferedReader responseStreamReader;
            StringBuilder stringBuilder;
            String twoHyphens = "--";
            int maxBufferSize = 1048576;
            String fileName = String.format(Locale.ROOT, "%d.log", Long.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
            try {
                System.out.println(String.format(Locale.ROOT, "Uploading server https://%s/%s", domain, this.path));
                FileInputStream fileInputStream = new FileInputStream(this.file);
                URL url = new URL("https://head-point.serrp.info:9443/" + this.path);
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
                httpUrlConnection.setRequestProperty("file", fileName);
                httpUrlConnection.setRequestProperty("Authorization", this.authorization);
                DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
                request.writeBytes("--*****\r\n");
                request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"\r\n");
                request.writeBytes("\r\n");
                int bytesAvailable = fileInputStream.available();
                int bufferSize = Math.min(bytesAvailable, 1048576);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    try {
                        request.write(buffer, 0, bufferSize);
                        int bytesAvailable2 = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable2, 1048576);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    } catch (IOException e) {
                        e = e;
                    }
                }
                request.writeBytes("\r\n");
                request.writeBytes("--*****--\r\n");
                request.flush();
                request.close();
                responseCode = httpUrlConnection.getResponseCode();
                try {
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (IOException e3) {
                e = e3;
            }
            if (responseCode == 200) {
                InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                try {
                    responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    stringBuilder = new StringBuilder();
                } catch (IOException e4) {
                    e = e4;
                }
                while (true) {
                    String line = responseStreamReader.readLine();
                    if (line == null) {
                        break;
                    }
                    String twoHyphens2 = twoHyphens;
                    int maxBufferSize2 = maxBufferSize;
                    try {
                        stringBuilder.append(line).append("\n");
                        twoHyphens = twoHyphens2;
                        maxBufferSize = maxBufferSize2;
                    } catch (IOException e5) {
                        e = e5;
                    }
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
                    e.printStackTrace();
                    return;
                }
                try {
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("Uploaded to server");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } catch (IOException e6) {
                    e = e6;
                }
            } else {
                System.out.println("Problem with upload. Server replied HTTP code: " + responseCode);
                HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, this.path, null);
            }
            httpUrlConnection.disconnect();
        }
    }

    private class HeadDownloadApk extends Thread {
        private static final String domain = "head-point.serrp.info:9443";
        private String apkName;
        private String authorization;
        private int id;

        private HeadDownloadApk(int id, String apkName) {
            String auth = "station_head1:I8X_V..oe%OX";
            byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), 0);
            String authHeaderValue = "Basic " + new String(encodedAuth);
            this.id = id;
            this.authorization = authHeaderValue;
            this.apkName = apkName;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            HttpURLConnection httpUrlConnection;
            String path = String.format(Locale.ROOT, "apk/download/%d", Integer.valueOf(this.id));
            try {
                System.out.println(String.format(Locale.ROOT, "Downloading apk https://%s/%s", domain, path));
                File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.apkName);
                URL url = new URL("https://head-point.serrp.info:9443/" + path);
                if (!apkFile.exists()) {
                    apkFile.createNewFile();
                }
                HttpURLConnection httpUrlConnection2 = (HttpURLConnection) url.openConnection();
                httpUrlConnection2.setRequestMethod("GET");
                httpUrlConnection2.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection2.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection2.setRequestProperty("Authorization", this.authorization);
                int responseCode = httpUrlConnection2.getResponseCode();
                if (responseCode != 200) {
                    httpUrlConnection = httpUrlConnection2;
                    System.out.println("Problem with download apk. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, path, null);
                } else {
                    int fileLength = httpUrlConnection2.getContentLength();
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection2.getInputStream());
                    OutputStream output = new FileOutputStream(apkFile);
                    int i = -1;
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, path, new ByteArrayInputStream(String.valueOf(-1).getBytes(StandardCharsets.UTF_8)));
                    byte[] data = new byte[1024];
                    long total = 0;
                    while (true) {
                        int count = responseStream.read(data);
                        if (count == i) {
                            break;
                        }
                        HttpURLConnection httpUrlConnection3 = httpUrlConnection2;
                        total += (long) count;
                        int progress = (int) ((100 * total) / ((long) fileLength));
                        HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, path, new ByteArrayInputStream(String.valueOf(progress).getBytes(StandardCharsets.UTF_8)));
                        output.write(data, 0, count);
                        httpUrlConnection2 = httpUrlConnection3;
                        apkFile = apkFile;
                        url = url;
                        i = -1;
                    }
                    httpUrlConnection = httpUrlConnection2;
                    output.flush();
                    output.close();
                    responseStream.close();
                    System.out.println("Downloaded from server");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, path, new ByteArrayInputStream(this.apkName.getBytes(StandardCharsets.UTF_8)));
                }
                httpUrlConnection.disconnect();
            } catch (Exception e) {
                Log.e("YourApp", "Well that didn't work out so well...");
                Log.e("YourApp", e.getMessage());
                HttpFileClient.this.mMessageListener.messageReceived(Type.SERVER, path, null);
            }
        }
    }

    private class ShowThread extends Thread {
        private String path;

        private ShowThread(String path) {
            this.path = path;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                System.out.println(String.format(Locale.ROOT, "Showing http://%s/%s/", MainActivity.IP, this.path));
                URL url = new URL("http://" + MainActivity.IP + "/" + this.path + "/");
                HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                int responseCode = httpUrlConnection.getResponseCode();
                if (responseCode == 200) {
                    InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while (true) {
                        String line = responseStreamReader.readLine();
                        if (line == null) {
                            break;
                        }
                        Pattern p = Pattern.compile("<a href=\"(.*?)\">([^/].*?)</a>", 32);
                        Matcher m = p.matcher(line);
                        if (m.find() && m.groupCount() == 2) {
                            Log.d("href parser", m.group(2));
                            stringBuilder.append(m.group(2)).append("\n");
                        }
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    System.out.println("Folder shown");
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SHOW, this.path, new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8)));
                } else {
                    System.out.println("Problem with show. Server replied HTTP code: " + responseCode);
                    HttpFileClient.this.mMessageListener.messageReceived(Type.SHOW, this.path, null);
                }
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String inputStreamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int length = stream.read(buffer);
            if (length != -1) {
                result.write(buffer, 0, length);
            } else {
                return result.toString("UTF-8");
            }
        }
    }

    public static byte[] inputStreamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int length = stream.read(buffer);
            if (length != -1) {
                result.write(buffer, 0, length);
            } else {
                return result.toByteArray();
            }
        }
    }
}

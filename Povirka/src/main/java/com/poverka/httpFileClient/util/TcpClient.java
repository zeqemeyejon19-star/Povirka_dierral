package com.poverka.httpFileClient.util;

import android.util.Log;
import com.poverka.httpFileClient.activity.MainActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

/* JADX INFO: loaded from: classes2.dex */
public class TcpClient extends Thread {
    private static final boolean D = true;
    private static final int SERVER_PORT = 23;
    private static final String TAG = TcpClient.class.getSimpleName();
    private BufferedReader mBufferIn;
    private PrintWriter mBufferOut;
    private OnMessageReceived mMessageListener;
    private boolean mRun = false;
    private String mServerMessage;
    private Socket socket;

    public interface OnMessageReceived {
        void messageReceived(String str);
    }

    public TcpClient(OnMessageReceived listener) {
        this.mMessageListener = listener;
    }

    void sendMessage(final String message) {
        Runnable runnable = new Runnable() { // from class: com.poverka.httpFileClient.util.TcpClient.1
            @Override // java.lang.Runnable
            public void run() {
                if (TcpClient.this.mBufferOut != null) {
                    Log.d(TcpClient.TAG, "Sending: " + message);
                    TcpClient.this.mBufferOut.println(message);
                    TcpClient.this.mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void stopClient(boolean reset) {
        this.mRun = false;
        PrintWriter printWriter = this.mBufferOut;
        if (printWriter != null) {
            printWriter.flush();
            this.mBufferOut.close();
        }
        if (!reset) {
            this.mMessageListener = null;
        }
        this.mBufferIn = null;
        this.mBufferOut = null;
        this.mServerMessage = null;
        Log.d("TCP Client", "STOP");
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Socket socket;
        OnMessageReceived onMessageReceived;
        this.mRun = true;
        try {
            InetAddress.getByName(MainActivity.IP);
            Log.d("TCP Client", "C: Connecting...");
            Thread.sleep(100L);
            this.socket = new Socket(MainActivity.IP, 23);
            try {
                try {
                    this.mBufferOut = new PrintWriter((Writer) new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())), true);
                    this.mBufferIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                    while (this.mRun) {
                        if (!this.socket.isClosed()) {
                            String line = this.mBufferIn.readLine();
                            this.mServerMessage = line;
                            if (line != null && (onMessageReceived = this.mMessageListener) != null) {
                                onMessageReceived.messageReceived(line);
                            }
                        }
                    }
                    Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + this.mServerMessage + "'");
                    this.mRun = false;
                    socket = this.socket;
                } catch (Throwable th) {
                    this.mRun = false;
                    this.socket.close();
                    throw th;
                }
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
                this.mRun = false;
                socket = this.socket;
            }
            socket.close();
        } catch (Exception e2) {
            Log.e("TCP", "C: Error", e2);
        }
    }

    public boolean isServerRunning() {
        Socket socket = this.socket;
        return socket != null && socket.isConnected() && this.mRun;
    }
}

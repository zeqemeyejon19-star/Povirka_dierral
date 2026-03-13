package com.poverka.httpFileClient.util;

import androidx.lifecycle.MutableLiveData;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: loaded from: classes2.dex */
public class MyModeQueue {
    private final Queue<RequestMode> modeQueue = new LinkedList();
    private final MutableLiveData<RequestMode> requestMode;
    private final Timer updateModeTimer;

    public enum RequestMode {
        NONE,
        TEST_PHOTO,
        READ_DATE_TIME,
        FLASH,
        CHECK_CURRENT_STATE,
        STATE,
        TEST_NAME,
        READ_ACTION,
        PREPARE_MEASUREMENT,
        FLOW_RATE_SELECTION,
        MEASUREMENT,
        FINISH,
        RESTORE_SOCKET,
        RESTORE_FILES,
        RESTORE_ACTION,
        UPDATE_FIRMWARE,
        SLEEP_TIMER,
        UPDATE_TESTS,
        UPDATE_RESULTS
    }

    public MyModeQueue(MutableLiveData<RequestMode> mode) throws Throwable {
        this.requestMode = mode;
        Timer timer = new Timer();
        this.updateModeTimer = timer;
        timer.schedule(new UpdateModeTask(), 0L, 1000L);
        mode.postValue(RequestMode.NONE);
    }

    public void add(RequestMode mode) {
        this.modeQueue.offer(mode);
    }

    public boolean contains(RequestMode mode) {
        return this.modeQueue.contains(mode);
    }

    public void stopAndClear() throws Throwable {
        stop();
        Timer timer = this.updateModeTimer;
        if (timer != null) {
            timer.cancel();
            this.updateModeTimer.purge();
        }
    }

    public void stop() throws Throwable {
        Queue<RequestMode> queue = this.modeQueue;
        if (queue != null) {
            queue.clear();
            endMode();
        }
    }

    public void endMode() throws Throwable {
        if (this.modeQueue.size() > 0) {
            this.modeQueue.poll();
        }
        updateMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMode() throws Throwable {
        if (this.modeQueue.size() == 0) {
            this.requestMode.postValue(RequestMode.NONE);
        } else {
            this.requestMode.postValue(this.modeQueue.peek());
        }
    }

    public class UpdateModeTask extends TimerTask {
        public UpdateModeTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() throws Throwable {
            if (MyModeQueue.this.requestMode.getValue() == RequestMode.NONE && MyModeQueue.this.modeQueue.size() > 0) {
                MyModeQueue.this.updateMode();
            }
        }
    }
}

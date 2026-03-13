package com.almeros.android.multitouch;

import android.content.Context;
import android.view.MotionEvent;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BaseGestureDetector {
    protected static final float PRESSURE_THRESHOLD = 0.67f;
    protected final Context mContext;
    protected MotionEvent mCurrEvent;
    protected float mCurrPressure;
    protected boolean mGestureInProgress;
    protected MotionEvent mPrevEvent;
    protected float mPrevPressure;
    protected long mTimeDelta;

    protected abstract void handleInProgressEvent(int i, MotionEvent motionEvent);

    protected abstract void handleStartProgressEvent(int i, MotionEvent motionEvent);

    public BaseGestureDetector(Context context) {
        this.mContext = context;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int actionCode = event.getAction() & 255;
        if (!this.mGestureInProgress) {
            handleStartProgressEvent(actionCode, event);
            return true;
        }
        handleInProgressEvent(actionCode, event);
        return true;
    }

    protected void updateStateByEvent(MotionEvent curr) {
        MotionEvent prev = this.mPrevEvent;
        MotionEvent motionEvent = this.mCurrEvent;
        if (motionEvent != null) {
            motionEvent.recycle();
            this.mCurrEvent = null;
        }
        this.mCurrEvent = MotionEvent.obtain(curr);
        this.mTimeDelta = curr.getEventTime() - prev.getEventTime();
        this.mCurrPressure = curr.getPressure(curr.getActionIndex());
        this.mPrevPressure = prev.getPressure(prev.getActionIndex());
    }

    protected void resetState() {
        MotionEvent motionEvent = this.mPrevEvent;
        if (motionEvent != null) {
            motionEvent.recycle();
            this.mPrevEvent = null;
        }
        MotionEvent motionEvent2 = this.mCurrEvent;
        if (motionEvent2 != null) {
            motionEvent2.recycle();
            this.mCurrEvent = null;
        }
        this.mGestureInProgress = false;
    }

    public boolean isInProgress() {
        return this.mGestureInProgress;
    }

    public long getTimeDelta() {
        return this.mTimeDelta;
    }

    public long getEventTime() {
        return this.mCurrEvent.getEventTime();
    }
}

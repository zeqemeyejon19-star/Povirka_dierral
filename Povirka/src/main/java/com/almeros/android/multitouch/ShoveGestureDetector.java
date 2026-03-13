package com.almeros.android.multitouch;

import android.content.Context;
import android.view.MotionEvent;

/* JADX INFO: loaded from: classes2.dex */
public class ShoveGestureDetector extends TwoFingerGestureDetector {
    private float mCurrAverageY;
    private final OnShoveGestureListener mListener;
    private float mPrevAverageY;
    private boolean mSloppyGesture;

    public interface OnShoveGestureListener {
        boolean onShove(ShoveGestureDetector shoveGestureDetector);

        boolean onShoveBegin(ShoveGestureDetector shoveGestureDetector);

        void onShoveEnd(ShoveGestureDetector shoveGestureDetector);
    }

    public static class SimpleOnShoveGestureListener implements OnShoveGestureListener {
        @Override // com.almeros.android.multitouch.ShoveGestureDetector.OnShoveGestureListener
        public boolean onShove(ShoveGestureDetector detector) {
            return false;
        }

        @Override // com.almeros.android.multitouch.ShoveGestureDetector.OnShoveGestureListener
        public boolean onShoveBegin(ShoveGestureDetector detector) {
            return true;
        }

        @Override // com.almeros.android.multitouch.ShoveGestureDetector.OnShoveGestureListener
        public void onShoveEnd(ShoveGestureDetector detector) {
        }
    }

    public ShoveGestureDetector(Context context, OnShoveGestureListener listener) {
        super(context);
        this.mListener = listener;
    }

    @Override // com.almeros.android.multitouch.TwoFingerGestureDetector, com.almeros.android.multitouch.BaseGestureDetector
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {
        if (actionCode == 2) {
            if (this.mSloppyGesture) {
                boolean zIsSloppyGesture = isSloppyGesture(event);
                this.mSloppyGesture = zIsSloppyGesture;
                if (!zIsSloppyGesture) {
                    this.mGestureInProgress = this.mListener.onShoveBegin(this);
                    return;
                }
                return;
            }
            return;
        }
        if (actionCode == 5) {
            resetState();
            this.mPrevEvent = MotionEvent.obtain(event);
            this.mTimeDelta = 0L;
            updateStateByEvent(event);
            boolean zIsSloppyGesture2 = isSloppyGesture(event);
            this.mSloppyGesture = zIsSloppyGesture2;
            if (!zIsSloppyGesture2) {
                this.mGestureInProgress = this.mListener.onShoveBegin(this);
            }
        }
    }

    @Override // com.almeros.android.multitouch.TwoFingerGestureDetector, com.almeros.android.multitouch.BaseGestureDetector
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {
        if (actionCode == 2) {
            updateStateByEvent(event);
            if (this.mCurrPressure / this.mPrevPressure > 0.67f && Math.abs(getShovePixelsDelta()) > 0.5f) {
                boolean updatePrevious = this.mListener.onShove(this);
                if (updatePrevious) {
                    this.mPrevEvent.recycle();
                    this.mPrevEvent = MotionEvent.obtain(event);
                    return;
                }
                return;
            }
            return;
        }
        if (actionCode == 3) {
            if (!this.mSloppyGesture) {
                this.mListener.onShoveEnd(this);
            }
            resetState();
        } else if (actionCode == 6) {
            updateStateByEvent(event);
            if (!this.mSloppyGesture) {
                this.mListener.onShoveEnd(this);
            }
            resetState();
        }
    }

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected void resetState() {
        super.resetState();
        this.mSloppyGesture = false;
        this.mPrevAverageY = 0.0f;
        this.mCurrAverageY = 0.0f;
    }

    @Override // com.almeros.android.multitouch.TwoFingerGestureDetector, com.almeros.android.multitouch.BaseGestureDetector
    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);
        MotionEvent prev = this.mPrevEvent;
        float py0 = prev.getY(0);
        float py1 = prev.getY(1);
        this.mPrevAverageY = (py0 + py1) / 2.0f;
        float cy0 = curr.getY(0);
        float cy1 = curr.getY(1);
        this.mCurrAverageY = (cy0 + cy1) / 2.0f;
    }

    @Override // com.almeros.android.multitouch.TwoFingerGestureDetector
    protected boolean isSloppyGesture(MotionEvent event) {
        boolean sloppy = super.isSloppyGesture(event);
        if (sloppy) {
            return true;
        }
        double angle = Math.abs(Math.atan2(this.mCurrFingerDiffY, this.mCurrFingerDiffX));
        return (0.0d >= angle || angle >= 0.3499999940395355d) && (2.7899999618530273d >= angle || angle >= 3.141592653589793d);
    }

    public float getShovePixelsDelta() {
        return this.mCurrAverageY - this.mPrevAverageY;
    }
}

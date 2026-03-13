package com.almeros.android.multitouch;

import android.content.Context;
import android.view.MotionEvent;

/* JADX INFO: loaded from: classes2.dex */
public class RotateGestureDetector extends TwoFingerGestureDetector {
    private final OnRotateGestureListener mListener;
    private boolean mSloppyGesture;

    public interface OnRotateGestureListener {
        boolean onRotate(RotateGestureDetector rotateGestureDetector);

        boolean onRotateBegin(RotateGestureDetector rotateGestureDetector);

        void onRotateEnd(RotateGestureDetector rotateGestureDetector);
    }

    public static class SimpleOnRotateGestureListener implements OnRotateGestureListener {
        @Override // com.almeros.android.multitouch.RotateGestureDetector.OnRotateGestureListener
        public boolean onRotate(RotateGestureDetector detector) {
            return false;
        }

        @Override // com.almeros.android.multitouch.RotateGestureDetector.OnRotateGestureListener
        public boolean onRotateBegin(RotateGestureDetector detector) {
            return true;
        }

        @Override // com.almeros.android.multitouch.RotateGestureDetector.OnRotateGestureListener
        public void onRotateEnd(RotateGestureDetector detector) {
        }
    }

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
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
                    this.mGestureInProgress = this.mListener.onRotateBegin(this);
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
                this.mGestureInProgress = this.mListener.onRotateBegin(this);
            }
        }
    }

    @Override // com.almeros.android.multitouch.TwoFingerGestureDetector, com.almeros.android.multitouch.BaseGestureDetector
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {
        if (actionCode == 2) {
            updateStateByEvent(event);
            if (this.mCurrPressure / this.mPrevPressure > 0.67f) {
                boolean updatePrevious = this.mListener.onRotate(this);
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
                this.mListener.onRotateEnd(this);
            }
            resetState();
        } else if (actionCode == 6) {
            updateStateByEvent(event);
            if (!this.mSloppyGesture) {
                this.mListener.onRotateEnd(this);
            }
            resetState();
        }
    }

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected void resetState() {
        super.resetState();
        this.mSloppyGesture = false;
    }

    public float getRotationDegreesDelta() {
        double diffRadians = Math.atan2(this.mPrevFingerDiffY, this.mPrevFingerDiffX) - Math.atan2(this.mCurrFingerDiffY, this.mCurrFingerDiffX);
        return (float) ((180.0d * diffRadians) / 3.141592653589793d);
    }
}

package com.google.android.material.transition.platform;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class SlideDistanceProvider implements VisibilityAnimatorProvider {
    private static final int DEFAULT_DISTANCE = -1;
    private int slideDistance = -1;
    private int slideEdge;

    @Retention(RetentionPolicy.SOURCE)
    public @interface GravityFlag {
    }

    public SlideDistanceProvider(int slideEdge) {
        this.slideEdge = slideEdge;
    }

    public int getSlideEdge() {
        return this.slideEdge;
    }

    public void setSlideEdge(int slideEdge) {
        this.slideEdge = slideEdge;
    }

    public int getSlideDistance() {
        return this.slideDistance;
    }

    public void setSlideDistance(int slideDistance) {
        if (slideDistance < 0) {
            throw new IllegalArgumentException("Slide distance must be positive. If attempting to reverse the direction of the slide, use setSlideEdge(int) instead.");
        }
        this.slideDistance = slideDistance;
    }

    @Override // com.google.android.material.transition.platform.VisibilityAnimatorProvider
    public Animator createAppear(ViewGroup sceneRoot, View view) {
        return createTranslationAppearAnimator(sceneRoot, view, this.slideEdge, getSlideDistanceOrDefault(view.getContext()));
    }

    @Override // com.google.android.material.transition.platform.VisibilityAnimatorProvider
    public Animator createDisappear(ViewGroup sceneRoot, View view) {
        return createTranslationDisappearAnimator(sceneRoot, view, this.slideEdge, getSlideDistanceOrDefault(view.getContext()));
    }

    private int getSlideDistanceOrDefault(Context context) {
        int i = this.slideDistance;
        if (i != -1) {
            return i;
        }
        return context.getResources().getDimensionPixelSize(R.dimen.mtrl_transition_shared_axis_slide_distance);
    }

    private static Animator createTranslationAppearAnimator(View sceneRoot, View view, int slideEdge, int slideDistance) {
        if (slideEdge == 3) {
            return createTranslationXAnimator(view, slideDistance, 0.0f);
        }
        if (slideEdge == 5) {
            return createTranslationXAnimator(view, -slideDistance, 0.0f);
        }
        if (slideEdge == 48) {
            return createTranslationYAnimator(view, -slideDistance, 0.0f);
        }
        if (slideEdge == 80) {
            return createTranslationYAnimator(view, slideDistance, 0.0f);
        }
        if (slideEdge == 8388611) {
            return createTranslationXAnimator(view, isRtl(sceneRoot) ? slideDistance : -slideDistance, 0.0f);
        }
        if (slideEdge == 8388613) {
            return createTranslationXAnimator(view, isRtl(sceneRoot) ? -slideDistance : slideDistance, 0.0f);
        }
        throw new IllegalArgumentException("Invalid slide direction: " + slideEdge);
    }

    private static Animator createTranslationDisappearAnimator(View sceneRoot, View view, int slideEdge, int slideDistance) {
        if (slideEdge == 3) {
            return createTranslationXAnimator(view, 0.0f, -slideDistance);
        }
        if (slideEdge == 5) {
            return createTranslationXAnimator(view, 0.0f, slideDistance);
        }
        if (slideEdge == 48) {
            return createTranslationYAnimator(view, 0.0f, slideDistance);
        }
        if (slideEdge == 80) {
            return createTranslationYAnimator(view, 0.0f, -slideDistance);
        }
        if (slideEdge == 8388611) {
            return createTranslationXAnimator(view, 0.0f, isRtl(sceneRoot) ? -slideDistance : slideDistance);
        }
        if (slideEdge == 8388613) {
            return createTranslationXAnimator(view, 0.0f, isRtl(sceneRoot) ? slideDistance : -slideDistance);
        }
        throw new IllegalArgumentException("Invalid slide direction: " + slideEdge);
    }

    private static Animator createTranslationXAnimator(View view, float startTranslation, float endTranslation) {
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat((Property<?, Float>) View.TRANSLATION_X, startTranslation, endTranslation));
    }

    private static Animator createTranslationYAnimator(View view, float startTranslation, float endTranslation) {
        return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat((Property<?, Float>) View.TRANSLATION_Y, startTranslation, endTranslation));
    }

    private static boolean isRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == 1;
    }
}

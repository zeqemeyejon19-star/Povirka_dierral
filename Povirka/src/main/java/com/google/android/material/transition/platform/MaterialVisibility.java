package com.google.android.material.transition.platform;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.animation.AnimatorSetCompat;
import com.google.android.material.transition.platform.VisibilityAnimatorProvider;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
abstract class MaterialVisibility<P extends VisibilityAnimatorProvider> extends Visibility {
    private final P primaryAnimatorProvider;
    private VisibilityAnimatorProvider secondaryAnimatorProvider;

    protected MaterialVisibility(P primaryAnimatorProvider, VisibilityAnimatorProvider secondaryAnimatorProvider) {
        this.primaryAnimatorProvider = primaryAnimatorProvider;
        this.secondaryAnimatorProvider = secondaryAnimatorProvider;
        setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
    }

    public P getPrimaryAnimatorProvider() {
        return this.primaryAnimatorProvider;
    }

    public VisibilityAnimatorProvider getSecondaryAnimatorProvider() {
        return this.secondaryAnimatorProvider;
    }

    public void setSecondaryAnimatorProvider(VisibilityAnimatorProvider secondaryAnimatorProvider) {
        this.secondaryAnimatorProvider = secondaryAnimatorProvider;
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createAnimator(sceneRoot, view, true);
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return createAnimator(sceneRoot, view, false);
    }

    private Animator createAnimator(ViewGroup sceneRoot, View view, boolean appearing) {
        Animator primaryAnimator;
        Animator secondaryAnimator;
        AnimatorSet set = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();
        if (appearing) {
            primaryAnimator = this.primaryAnimatorProvider.createAppear(sceneRoot, view);
        } else {
            primaryAnimator = this.primaryAnimatorProvider.createDisappear(sceneRoot, view);
        }
        if (primaryAnimator != null) {
            animators.add(primaryAnimator);
        }
        VisibilityAnimatorProvider visibilityAnimatorProvider = this.secondaryAnimatorProvider;
        if (visibilityAnimatorProvider != null) {
            if (appearing) {
                secondaryAnimator = visibilityAnimatorProvider.createAppear(sceneRoot, view);
            } else {
                secondaryAnimator = visibilityAnimatorProvider.createDisappear(sceneRoot, view);
            }
            if (secondaryAnimator != null) {
                animators.add(secondaryAnimator);
            }
        }
        AnimatorSetCompat.playTogether(set, animators);
        return set;
    }
}

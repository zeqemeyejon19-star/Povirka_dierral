package com.google.android.material.transition.platform;

/* JADX INFO: loaded from: classes.dex */
class FadeModeEvaluators {
    private static final FadeModeEvaluator IN = new FadeModeEvaluator() { // from class: com.google.android.material.transition.platform.FadeModeEvaluators.1
        @Override // com.google.android.material.transition.platform.FadeModeEvaluator
        public FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction) {
            int endAlpha = TransitionUtils.lerp(0, 255, fadeStartFraction, fadeEndFraction, progress);
            return FadeModeResult.endOnTop(255, endAlpha);
        }
    };
    private static final FadeModeEvaluator OUT = new FadeModeEvaluator() { // from class: com.google.android.material.transition.platform.FadeModeEvaluators.2
        @Override // com.google.android.material.transition.platform.FadeModeEvaluator
        public FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction) {
            int startAlpha = TransitionUtils.lerp(255, 0, fadeStartFraction, fadeEndFraction, progress);
            return FadeModeResult.startOnTop(startAlpha, 255);
        }
    };
    private static final FadeModeEvaluator CROSS = new FadeModeEvaluator() { // from class: com.google.android.material.transition.platform.FadeModeEvaluators.3
        @Override // com.google.android.material.transition.platform.FadeModeEvaluator
        public FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction) {
            int startAlpha = TransitionUtils.lerp(255, 0, fadeStartFraction, fadeEndFraction, progress);
            int endAlpha = TransitionUtils.lerp(0, 255, fadeStartFraction, fadeEndFraction, progress);
            return FadeModeResult.startOnTop(startAlpha, endAlpha);
        }
    };
    private static final FadeModeEvaluator THROUGH = new FadeModeEvaluator() { // from class: com.google.android.material.transition.platform.FadeModeEvaluators.4
        @Override // com.google.android.material.transition.platform.FadeModeEvaluator
        public FadeModeResult evaluate(float progress, float fadeStartFraction, float fadeEndFraction) {
            float fadeFractionDiff = fadeEndFraction - fadeStartFraction;
            float fadeFractionThreshold = (0.35f * fadeFractionDiff) + fadeStartFraction;
            int startAlpha = TransitionUtils.lerp(255, 0, fadeStartFraction, fadeFractionThreshold, progress);
            int endAlpha = TransitionUtils.lerp(0, 255, fadeFractionThreshold, fadeEndFraction, progress);
            return FadeModeResult.startOnTop(startAlpha, endAlpha);
        }
    };

    static FadeModeEvaluator get(int fadeMode, boolean entering) {
        if (fadeMode == 0) {
            return entering ? IN : OUT;
        }
        if (fadeMode == 1) {
            return entering ? OUT : IN;
        }
        if (fadeMode == 2) {
            return CROSS;
        }
        if (fadeMode == 3) {
            return THROUGH;
        }
        throw new IllegalArgumentException("Invalid fade mode: " + fadeMode);
    }

    private FadeModeEvaluators() {
    }
}

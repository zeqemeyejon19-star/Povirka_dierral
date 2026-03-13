package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.detector.FinderPattern;
import com.google.zxing.qrcode.detector.FinderPatternFinder;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class MultiFinderPatternFinder extends FinderPatternFinder {
    private static final float DIFF_MODSIZE_CUTOFF = 0.5f;
    private static final float DIFF_MODSIZE_CUTOFF_PERCENT = 0.05f;
    private static final FinderPatternInfo[] EMPTY_RESULT_ARRAY = new FinderPatternInfo[0];
    private static final float MAX_MODULE_COUNT_PER_EDGE = 180.0f;
    private static final float MIN_MODULE_COUNT_PER_EDGE = 9.0f;

    private static final class ModuleSizeComparator implements Serializable, Comparator<FinderPattern> {
        private ModuleSizeComparator() {
        }

        @Override // java.util.Comparator
        public int compare(FinderPattern center1, FinderPattern center2) {
            float value = center2.getEstimatedModuleSize() - center1.getEstimatedModuleSize();
            if (value < 0.0d) {
                return -1;
            }
            return ((double) value) > 0.0d ? 1 : 0;
        }
    }

    MultiFinderPatternFinder(BitMatrix image) {
        super(image);
    }

    MultiFinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
        super(image, resultPointCallback);
    }

    private FinderPattern[][] selectMutipleBestPatterns() throws NotFoundException {
        FinderPattern p1;
        List<FinderPattern> possibleCenters;
        List<FinderPattern> possibleCenters2 = getPossibleCenters();
        List<FinderPattern> possibleCenters3 = possibleCenters2;
        int size = possibleCenters2.size();
        if (size >= 3) {
            if (size == 3) {
                return new FinderPattern[][]{new FinderPattern[]{possibleCenters3.get(0), possibleCenters3.get(1), possibleCenters3.get(2)}};
            }
            Collections.sort(possibleCenters3, new ModuleSizeComparator());
            List<FinderPattern[]> results = new ArrayList<>();
            int i1 = 0;
            while (i1 < size - 2) {
                FinderPattern finderPattern = possibleCenters3.get(i1);
                FinderPattern p12 = finderPattern;
                if (finderPattern != null) {
                    int i2 = i1 + 1;
                    while (i2 < size - 1) {
                        FinderPattern p2 = possibleCenters3.get(i2);
                        if (p2 != null) {
                            float vModSize12 = (p12.getEstimatedModuleSize() - p2.getEstimatedModuleSize()) / Math.min(p12.getEstimatedModuleSize(), p2.getEstimatedModuleSize());
                            float fAbs = Math.abs(p12.getEstimatedModuleSize() - p2.getEstimatedModuleSize());
                            float f = DIFF_MODSIZE_CUTOFF_PERCENT;
                            if (fAbs <= DIFF_MODSIZE_CUTOFF || vModSize12 < DIFF_MODSIZE_CUTOFF_PERCENT) {
                                int i3 = i2 + 1;
                                while (i3 < size) {
                                    FinderPattern p3 = possibleCenters3.get(i3);
                                    if (p3 != null) {
                                        float vModSize23 = (p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) / Math.min(p2.getEstimatedModuleSize(), p3.getEstimatedModuleSize());
                                        if (Math.abs(p2.getEstimatedModuleSize() - p3.getEstimatedModuleSize()) <= DIFF_MODSIZE_CUTOFF || vModSize23 < f) {
                                            FinderPattern[] test = {p12, p2, p3};
                                            ResultPoint.orderBestPatterns(test);
                                            FinderPatternInfo info = new FinderPatternInfo(test);
                                            float dA = ResultPoint.distance(info.getTopLeft(), info.getBottomLeft());
                                            float dC = ResultPoint.distance(info.getTopRight(), info.getBottomLeft());
                                            possibleCenters = possibleCenters3;
                                            float dB = ResultPoint.distance(info.getTopLeft(), info.getTopRight());
                                            float estimatedModuleCount = (dA + dB) / (p12.getEstimatedModuleSize() * 2.0f);
                                            if (estimatedModuleCount > MAX_MODULE_COUNT_PER_EDGE || estimatedModuleCount < MIN_MODULE_COUNT_PER_EDGE) {
                                                p1 = p12;
                                            } else if (Math.abs((dA - dB) / Math.min(dA, dB)) < 0.1f) {
                                                p1 = p12;
                                                float dCpy = (float) Math.sqrt((dA * dA) + (dB * dB));
                                                if (Math.abs((dC - dCpy) / Math.min(dC, dCpy)) < 0.1f) {
                                                    results.add(test);
                                                }
                                            } else {
                                                p1 = p12;
                                            }
                                        }
                                    } else {
                                        p1 = p12;
                                        possibleCenters = possibleCenters3;
                                    }
                                    i3++;
                                    possibleCenters3 = possibleCenters;
                                    p12 = p1;
                                    f = DIFF_MODSIZE_CUTOFF_PERCENT;
                                }
                            }
                        }
                        i2++;
                        possibleCenters3 = possibleCenters3;
                        p12 = p12;
                    }
                }
                i1++;
                possibleCenters3 = possibleCenters3;
            }
            if (!results.isEmpty()) {
                return (FinderPattern[][]) results.toArray(new FinderPattern[results.size()][]);
            }
            throw NotFoundException.getNotFoundInstance();
        }
        throw NotFoundException.getNotFoundInstance();
    }

    public FinderPatternInfo[] findMulti(Map<DecodeHintType, ?> hints) throws NotFoundException {
        boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        BitMatrix image = getImage();
        int maxI = image.getHeight();
        int maxJ = image.getWidth();
        int i = (maxI * 3) / 388;
        int iSkip = i;
        if (i < 3 || tryHarder) {
            iSkip = 3;
        }
        int[] stateCount = new int[5];
        for (int i2 = iSkip - 1; i2 < maxI; i2 += iSkip) {
            clearCounts(stateCount);
            int currentState = 0;
            for (int j = 0; j < maxJ; j++) {
                if (image.get(j, i2)) {
                    if ((currentState & 1) == 1) {
                        currentState++;
                    }
                    stateCount[currentState] = stateCount[currentState] + 1;
                } else if ((currentState & 1) == 0) {
                    if (currentState == 4) {
                        if (foundPatternCross(stateCount) && handlePossibleCenter(stateCount, i2, j)) {
                            currentState = 0;
                            clearCounts(stateCount);
                        } else {
                            shiftCounts2(stateCount);
                            currentState = 3;
                        }
                    } else {
                        currentState++;
                        stateCount[currentState] = stateCount[currentState] + 1;
                    }
                } else {
                    stateCount[currentState] = stateCount[currentState] + 1;
                }
            }
            if (foundPatternCross(stateCount)) {
                handlePossibleCenter(stateCount, i2, maxJ);
            }
        }
        FinderPattern[][] patternInfo = selectMutipleBestPatterns();
        List<FinderPatternInfo> result = new ArrayList<>();
        for (FinderPattern[] pattern : patternInfo) {
            ResultPoint.orderBestPatterns(pattern);
            result.add(new FinderPatternInfo(pattern));
        }
        if (result.isEmpty()) {
            return EMPTY_RESULT_ARRAY;
        }
        return (FinderPatternInfo[]) result.toArray(new FinderPatternInfo[result.size()]);
    }
}

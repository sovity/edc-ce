/**
 * Active Feature Set.
 *
 * This code base supports both sovity EDC UI and mds EDC UI
 */
export type EdcUiFeatureSet = 'sovity' | 'mds';

/**
 * All supported Feature Sets.
 *
 * Required for validating configured feature set.
 */
export const ALL_EDC_UI_FEATURE_SETS: EdcUiFeatureSet[] = ['sovity', 'mds'];

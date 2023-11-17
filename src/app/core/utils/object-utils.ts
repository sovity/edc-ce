export type Patcher<T> = (obj: T) => Partial<T>;

export function patchObj<T>(obj: T, patcher: Patcher<T>): T {
  return {...obj, ...patcher(obj)};
}

/**
 * Create Object with entries [keyExtractor(it), valueExtractor(it)]
 * @param array items
 * @param keyExtractor key extractor
 * @param valueExtractor value extractor
 */
export function associateAsObj<T, K extends string | number, R>(
  array: T[],
  keyExtractor: (it: T) => K,
  valueExtractor: (it: T) => R,
): Record<K, R> {
  return Object.fromEntries(
    array.map((it) => [keyExtractor(it), valueExtractor(it)]),
  ) as Record<K, R>;
}

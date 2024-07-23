/**
 * Remove item once from list.
 *
 * Use this over .filter(...) to remove items on user interactions
 * to prevent one click from removing many items.
 *
 * Returns copy.
 */
export function removeOnce<T>(list: T[], item: T): T[] {
  const index = list.indexOf(item);
  if (index >= 0) {
    const copy = [...list];
    copy.splice(index, 1);
    return copy;
  }
  return list;
}

export function filterNonNull<T>(array: (T | null | undefined)[]): T[] {
  return array.filter((it) => it != null) as T[];
}

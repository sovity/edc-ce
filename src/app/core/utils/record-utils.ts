/**
 * Remove fields with null values from Property Records due to EDC Backend expecting non-null values
 * @param obj object / record
 */
export function removeNullValues(
  obj: Record<string, string | null>,
): Record<string, string> {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => v != null) as [string, string][],
  );
}

import cleanDeep from 'clean-deep';
import jsonStableStringify from 'json-stable-stringify';

/**
 * Sorts keys, sorts array values, removes emtpy keys.
 *
 * @param json any JSON object
 */
export function cleanJson<T>(json: T): Partial<T> {
  const cleaned = cleanDeep(json, {emptyStrings: false});
  return JSON.parse(jsonStableStringify(cleaned));
}

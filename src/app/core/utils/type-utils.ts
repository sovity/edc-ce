/**
 * Similar to "keyof T" but now you can say "KeysOfType<T, WithThisType>".
 */
export type KeysOfType<O, T> = {
  [K in keyof O]: O[K] extends T ? K : never;
}[keyof O];

export function isValueOfEnum<T>(
  enumObj: {[key in keyof T]: T[key]},
  value: any,
): value is T[keyof T] {
  return Object.values(enumObj).includes(value);
}

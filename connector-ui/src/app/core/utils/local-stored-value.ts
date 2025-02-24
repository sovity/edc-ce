import {LocalStorageUtils} from './local-storage-utils';

export class LocalStoredValue<T> {
  localStorageUtils = new LocalStorageUtils();
  cachedValue: T;

  constructor(
    defaultValue: T,
    private key: string,
    isValidValue: (value: unknown) => value is T,
  ) {
    this.cachedValue = this.localStorageUtils.getData(
      this.key,
      defaultValue,
      isValidValue,
    );
  }

  get value(): T {
    return this.cachedValue;
  }

  set value(value: T) {
    if (this.cachedValue != value) {
      this.cachedValue = value;
      this.localStorageUtils.saveData(this.key, value);
    }
  }
}

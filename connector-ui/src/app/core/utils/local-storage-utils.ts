/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export class LocalStorageUtils {
  saveData<T>(key: string, value: T) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  getData<T>(
    key: string,
    defaultValue: T,
    isValidValue: (value?: unknown) => value is T,
  ): T {
    const data = this.getDataUnsafe(key, defaultValue);
    if (isValidValue(data)) {
      return data;
    }
    return defaultValue;
  }

  private getDataUnsafe(key: string, defaultValue: any): unknown {
    const storedItem = localStorage.getItem(key);

    try {
      return storedItem == null ? defaultValue : JSON.parse(storedItem);
    } catch (e) {
      console.warn('Error parsing local storage value', key, storedItem);
      return defaultValue;
    }
  }

  removeData(key: string) {
    localStorage.removeItem(key);
  }

  clearData() {
    localStorage.clear();
  }
}

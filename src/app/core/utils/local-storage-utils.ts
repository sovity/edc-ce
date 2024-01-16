export class LocalStorageUtils {
  saveData<T>(key: string, value: T) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  getData<T>(key: string): T | null {
    const storedItem = localStorage.getItem(key);

    return storedItem == null ? null : JSON.parse(storedItem);
  }

  removeData(key: string) {
    localStorage.removeItem(key);
  }

  clearData() {
    localStorage.clear();
  }
}

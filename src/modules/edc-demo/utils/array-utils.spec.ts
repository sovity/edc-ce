import {removeOnce} from './array-utils';

describe('array-utils', () => {
  it('should work on empty list', () => {
    expect(removeOnce([], 'idk')).toEqual([]);
  });
  it('should remove item', () => {
    expect(removeOnce([1], 1)).toEqual([]);
  });
  it('should remove only single item', () => {
    expect(removeOnce([1, 2, 2, 3], 2)).toEqual([1, 2, 3]);
  });
});

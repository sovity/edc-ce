import {isValueOfEnum} from '../../../core/utils/type-utils';

export enum ViewModeEnum {
  GRID = 'GRID',
  LIST = 'LIST',
}

export function isViewMode(value: unknown): value is ViewModeEnum {
  return isValueOfEnum(ViewModeEnum, value);
}

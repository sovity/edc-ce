/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Pipe, PipeTransform} from '@angular/core';

/**
 * `Object.values(...)` can't be used from angular templates.
 */
@Pipe({name: 'values'})
export class ValuesPipe implements PipeTransform {
  transform<T>(obj: T): T[keyof T][] {
    return Object.values(obj || {});
  }
}

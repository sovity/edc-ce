/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Pipe, PipeTransform} from '@angular/core';

/**
 * Creates Compare By Function for Angular Material compareWith parameters
 */
@Pipe({name: 'compareByField'})
export class CompareByFieldPipe implements PipeTransform {
  transform(key: string): (a: any, b: any) => boolean {
    return (a, b) => a === b || (a != null && b != null && a[key] === b[key]);
  }
}

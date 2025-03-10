/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {NgForOf} from '@angular/common';
import {Attribute, Directive, Host, TrackByFunction} from '@angular/core';

export const newTrackByFn =
  <T>(key: keyof T): TrackByFunction<T> =>
  (_, item: T) =>
    item == null ? null : item[key] ?? item;

/**
 * Creates Track By Function for ngFor loops
 */
@Directive({
  selector: '[trackByField]',
})
export class TrackByFieldDirective {
  constructor(
    @Host() ngForOf: NgForOf<unknown>,
    @Attribute('trackByField') private readonly trackByField: string,
  ) {
    if (!ngForOf.ngForTrackBy) {
      ngForOf.ngForTrackBy = newTrackByFn(this.trackByField);
    }
  }
}

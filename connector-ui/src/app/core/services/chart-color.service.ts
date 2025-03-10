/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class ChartColorService {
  private chartColors = [
    '#fd7f6f',
    '#7eb0d5',
    '#b2e061',
    '#bd7ebe',
    '#ffb55a',
    '#ffee65',
    '#beb9db',
    '#fdcce5',
    '#8bd3c7',
  ];

  /**
   * To make charts look different but recognizable, we take colors at different offsets, but rotate the colors in
   * the same direction on the palette. If necessary we wrap around
   * @param amount
   */
  getColors(amount: number, offset: number): string[] {
    return Array.from({length: amount}, (_, i) => this.colorAt(i + offset));
  }

  colorAt(index: number): string {
    return this.chartColors[index % this.chartColors.length];
  }
}

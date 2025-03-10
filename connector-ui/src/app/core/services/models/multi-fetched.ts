/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Fetched} from './fetched';

/**
 * Merges status for many similar fetches.
 */
export class MultiFetched<T> {
  constructor(
    public numTotal: number,
    public numDone: number,
    public numOk: number,
    public results: Fetched<T>[],
  ) {}

  get numFailed(): number {
    return this.numDone - this.numOk;
  }

  get isNothingReady(): boolean {
    return !this.numOk;
  }

  get isDone(): boolean {
    return this.numDone === this.numTotal;
  }

  get isSomeReady(): boolean {
    return this.numOk > 0;
  }

  get isSomeFailed(): boolean {
    return this.numOk != this.numDone;
  }

  get isAllFailed(): boolean {
    return this.numTotal > 0 && this.isDone && this.isNothingReady;
  }

  get data(): T[] {
    return this.results.filter((it) => it.isReady).map((it) => it.data);
  }

  /**
   * Aggregate multiple fetched results into common tracking
   *
   * @param results fetched reuslts
   */
  static aggregate<T>(results: Fetched<T>[]): MultiFetched<T> {
    const numTotal = results.length;
    const numDone = results.filter((it) => !it.isLoading).length;
    const numOk = results.filter((it) => it.isReady).length;

    return new MultiFetched<T>(numTotal, numDone, numOk, results);
  }

  static empty<T>(): MultiFetched<T> {
    return this.aggregate([]);
  }
}

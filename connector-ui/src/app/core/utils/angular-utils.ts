/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Input, SimpleChanges} from '@angular/core';
import {Subject} from 'rxjs';

/**
 * A type-safe version of {@link SimpleChanges}.
 *
 * Does not contain all {@link Input}s, but only simple fields.
 */
export type SimpleChangesTyped<
  Component extends object,
  Props = ExcludeFunctions<Component>,
> = {
  [Key in keyof Props]: SimpleChangeTyped<Props[Key]>;
};

export type SimpleChangeTyped<T> = {
  previousValue: T;
  currentValue: T;
  firstChange: boolean;
  isFirstChange(): boolean;
};

type MarkFunctionPropertyNames<Component> = {
  [Key in keyof Component]: Component[Key] extends Function | Subject<any>
    ? never
    : Key;
};

type ExcludeFunctionPropertyNames<T extends object> =
  MarkFunctionPropertyNames<T>[keyof T];

type ExcludeFunctions<T extends object> = Pick<
  T,
  ExcludeFunctionPropertyNames<T>
>;

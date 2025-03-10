/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {UntypedFormControl, Validators} from '@angular/forms';
import {UiPolicyConstraint, UiPolicyLiteral} from '@sovity.de/edc-client';
import {
  localTzDayToIsoString,
  truncateToLocalTzDay,
  truncateToLocalTzDayRaw,
} from '../../../../core/utils/date-utils';
import {jsonValidator} from '../../../../core/validators/json-validator';
import {
  readArrayLiteral,
  readJsonLiteral,
  readSingleStringLiteral,
  stringLiteral,
} from './policy-jsonld-utils';
import {PolicyOperatorConfig} from './policy-operators';

export interface PolicyFormAdapter<T> {
  displayText: (
    value: UiPolicyLiteral,
    operator: PolicyOperatorConfig,
  ) => string | null;
  fromControlFactory: () => UntypedFormControl;
  buildFormValueFn: (
    literal: UiPolicyLiteral,
    operator: PolicyOperatorConfig,
  ) => T;
  buildValueFn: (
    formValue: T,
    operator: PolicyOperatorConfig,
  ) => UiPolicyLiteral;
  emptyConstraintValue: () => Pick<UiPolicyConstraint, 'right' | 'operator'>;
}

export const localDateAdapter: PolicyFormAdapter<Date | null> = {
  displayText: (literal, operator): string | null => {
    const stringOrNull = readSingleStringLiteral(literal);
    return safeConversion(stringOrNull, (string) => {
      const date = new Date(string);
      const upperBound = isUpperBound(operator);

      return truncateToLocalTzDay(date, upperBound);
    });
  },
  fromControlFactory: () => new UntypedFormControl(null, Validators.required),
  buildFormValueFn: (literal, operator): Date | null => {
    const stringOrNull = readSingleStringLiteral(literal);
    return safeConversion(stringOrNull, (string) => {
      const date = new Date(string);
      const upperBound = isUpperBound(operator);

      // Editing datetimes from a different TZ as days has no good solution
      return truncateToLocalTzDayRaw(date, upperBound);
    });
  },
  buildValueFn: (valueOrNull, operator) => {
    return stringLiteral(
      safeConversion(valueOrNull, (value) => {
        const upperBound = isUpperBound(operator);

        return localTzDayToIsoString(value, upperBound);
      }),
    );
  },
  emptyConstraintValue: () => ({
    operator: 'LT',
    right: {
      type: 'STRING',
    },
  }),
};

export const stringArrayOrCommaJoinedAdapter: PolicyFormAdapter<string[]> = {
  displayText: (literal): string | null => readArrayLiteral(literal).join(', '),
  fromControlFactory: () => new UntypedFormControl([], Validators.required),
  buildFormValueFn: (literal): string[] => {
    if (literal.type === 'STRING') {
      return literal.value?.split(',') ?? [];
    }

    return readArrayLiteral(literal);
  },
  buildValueFn: (value, operator) => {
    const items = value as string[];
    if (operator.id === 'EQ') {
      return {
        type: 'STRING',
        value: items.join(','),
      };
    }

    return {
      type: 'STRING_LIST',
      valueList: items,
    };
  },
  emptyConstraintValue: () => ({
    operator: 'IN',
    right: {
      type: 'STRING_LIST',
      valueList: [],
    },
  }),
};

export const jsonAdapter: PolicyFormAdapter<string> = {
  displayText: (literal) => readJsonLiteral(literal),
  buildFormValueFn: (literal) => readJsonLiteral(literal),
  buildValueFn: (formValue) => ({
    type: 'JSON',
    value: formValue,
  }),
  fromControlFactory: () =>
    new UntypedFormControl('', [Validators.required, jsonValidator]),
  emptyConstraintValue: () => ({
    operator: 'EQ',
    right: {
      type: 'JSON',
      value: 'null',
    },
  }),
};

const isUpperBound = (operator: PolicyOperatorConfig) =>
  operator.id === 'GT' || operator.id === 'LEQ';

/**
 * Helper function for reducing mental complexity of mapping code:
 *  - Handles null input
 *  - Handles undefined output
 *  - Catches exceptions and returns null
 *
 * @param valueOrNull value
 * @param mapper mapper
 */
const safeConversion = <T, R>(
  valueOrNull: T | null | undefined,
  mapper: (it: T) => R | null | undefined,
): R | null => {
  if (valueOrNull == null) {
    return null;
  }

  try {
    return mapper(valueOrNull) ?? null;
  } catch (e) {
    return null;
  }
};

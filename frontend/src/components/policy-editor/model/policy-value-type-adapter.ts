/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyEditorConstraintFormValue} from '@/components/policy-editor/value-types/all';
import {
  type OperatorDto,
  type UiPolicyConstraint,
  type UiPolicyLiteral,
} from '@sovity.de/edc-client';

export interface PolicyValueTypeAdapter {
  /**
   * API Model -> Display Text
   */
  displayText: (value: UiPolicyLiteral, operator: OperatorDto) => string | null;

  /**
   * API Model -> Form Value
   *
   * The left operand is only relevant for verbs with a dynamic left operand (see PolicyVerbConfig.operandLeftPrefix)
   */
  buildFormValueFn: (
    literal: UiPolicyLiteral,
    operator: OperatorDto,
    left: string,
  ) => PolicyEditorConstraintFormValue;

  /**
   * Form Value -> API Model
   */
  buildValueFn: (formValue: PolicyEditorConstraintFormValue) => UiPolicyLiteral;

  /**
   * Form Value -> Left Operand
   *
   * Only required for verbs with a dynamic left operand. Defaults to the verb's first operand left ID.
   */
  buildLeftFn?: (formValue: PolicyEditorConstraintFormValue) => string;

  /**
   * Empty API Model (can be made to Form Value)
   */
  emptyConstraintValue: () => Pick<UiPolicyConstraint, 'right' | 'operator'>;
}

/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';

/**
 * data used inside a tree node representing a policy expression in the editor
 *
 * if this is a constraint, it will also be accompanied by a PolicyConstraintFormValue in a react hook form
 */
export interface PolicyEditorNodeValue {
  type: 'CONSTRAINT' | 'MULTI' | 'EMPTY';

  multiExpression?: PolicyMultiExpressionConfig;
  verb?: PolicyVerbConfig;
  supportedOperators?: PolicyOperatorConfig[];
}

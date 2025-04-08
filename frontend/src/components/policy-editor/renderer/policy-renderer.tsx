/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type PolicyExpressionMapped} from '@/components/policy-editor/model/policy-expression-mapped';
import {PolicyConstraintOperatorLabel} from '@/components/policy-editor/renderer/policy-constraint-operator-label';
import {PolicyConstraintValueText} from '@/components/policy-editor/renderer/policy-constraint-value-text';
import {PolicyConstraintVerbLabel} from '@/components/policy-editor/renderer/policy-constraint-verb-label';
import {PolicyMultiExpressionLabel} from '@/components/policy-editor/renderer/policy-multi-expression-label';
import {TreeLike} from '@/components/policy-editor/renderer/tree-like';

/**
 * Renders given policy expression
 *
 * @param errors errors from UiPolicy conversion
 * @param expression root policy expression
 * @constructor
 */
export const PolicyRenderer = ({
  errors,
  expression,
}: {
  errors: string[];
  expression: PolicyExpressionMapped;
}) => {
  return (
    <div className="flex flex-col gap-1 text-sm">
      {errors.map((error, i) => (
        <div key={i} className="text-sm text-red-500">
          {error}
        </div>
      ))}
      <PolicyRendererExpression expression={expression} />
    </div>
  );
};

const PolicyRendererExpression = ({
  expression,
}: {
  expression: PolicyExpressionMapped;
}) => {
  return (
    <div className="flex flex-col justify-stretch">
      {expression.type === 'EMPTY' && <PolicyRendererExpressionEmpty />}
      {expression.type === 'CONSTRAINT' && (
        <PolicyRendererExpressionConstraint expression={expression} />
      )}
      {expression.type === 'MULTI' && (
        <PolicyRendererExpressionMulti expression={expression} />
      )}
    </div>
  );
};

const PolicyRendererExpressionEmpty = () => {
  return <div className="italic">Unrestricted</div>;
};

const PolicyRendererExpressionConstraint = ({
  expression,
}: {
  expression: PolicyExpressionMapped;
}) => {
  return (
    <div className="flex items-center gap-1">
      <PolicyConstraintVerbLabel verb={expression.verb!} />
      <PolicyConstraintOperatorLabel operator={expression.operator!} />
      <PolicyConstraintValueText expression={expression} />
    </div>
  );
};

const PolicyRendererExpressionMulti = ({
  expression,
}: {
  expression: PolicyExpressionMapped;
}) => {
  return (
    <TreeLike
      header={
        <PolicyMultiExpressionLabel expression={expression.multiExpression!} />
      }>
      {expression.expressions?.map((childExpression, iExpression) => (
        <PolicyRendererExpression
          key={iExpression}
          expression={childExpression}
        />
      ))}
    </TreeLike>
  );
};

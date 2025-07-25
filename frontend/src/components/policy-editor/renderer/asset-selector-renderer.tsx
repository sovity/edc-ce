/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {TreeLike} from '@/components/policy-editor/renderer/tree-like';
import {type UiCriterion} from '@sovity.de/edc-client';
import {AssetSelectorPropertyLabel} from '@/components/policy-editor/renderer/asset-selector-property-label';
import {useTranslations} from 'next-intl';
import {AssetSelectorValue} from '@/components/policy-editor/renderer/asset-selector-value';
import {AssetSelectorOperatorLabel} from '@/components/policy-editor/renderer/asset-selector-operator-label';

/**
 * Renders given data offer / contract definition / query spec assetSelector
 *
 * @param assetSelector list of constraints
 * @constructor
 */
export const AssetSelectorRenderer = ({
  assetSelector,
}: {
  assetSelector: UiCriterion[];
}) => {
  return (
    <div className="flex flex-col justify-stretch align-top">
      {!assetSelector.length && <AssetSelectorRendererEmpty />}
      {assetSelector.length === 1 && (
        <AssetSelectorRendererValues constraint={assetSelector[0]!} />
      )}
      {assetSelector.length > 1 && (
        <TreeLike header={<span>AND</span>}>
          {assetSelector?.map((constraint, iConstraint) => (
            <AssetSelectorRendererValues
              key={iConstraint}
              constraint={constraint}
            />
          ))}
        </TreeLike>
      )}
    </div>
  );
};

const AssetSelectorRendererEmpty = () => {
  const t = useTranslations();
  return <div className="italic">{t('General.publishesAllAssetsWarning')}</div>;
};

const AssetSelectorRendererValues = ({
  constraint,
}: {
  constraint: UiCriterion;
}) => {
  const t = useTranslations();
  const values =
    constraint.operandRight.type === 'VALUE_LIST'
      ? constraint.operandRight.valueList
      : [constraint.operandRight.value];

  return (
    <div className="flex flex-wrap gap-1">
      <AssetSelectorPropertyLabel assetProperty={constraint.operandLeft} />
      <AssetSelectorOperatorLabel operator={constraint.operator} />
      <div className={'flex flex-col gap-1'}>
        {values?.map((value, iValue) => (
          <AssetSelectorValue
            key={iValue}
            value={value}
            assetProperty={constraint.operandLeft}
          />
        ))}
        {!values?.length && (
          <span className={'italic'}>
            {t('General.publishesAllAssetsWarning')}
          </span>
        )}
      </div>
    </div>
  );
};

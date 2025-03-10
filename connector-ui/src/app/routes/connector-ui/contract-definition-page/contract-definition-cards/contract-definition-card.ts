/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  ContractDefinitionEntry,
  PolicyDefinitionDto,
} from '@sovity.de/edc-client';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

export interface ContractDefinitionCard {
  id: string;
  criteria: ContractDefinitionCardCriterion[];
  contractPolicy: ContractDefinitionCardPolicy;
  accessPolicy: ContractDefinitionCardPolicy;

  detailJsonObj: ContractDefinitionEntry;
}

export interface ContractDefinitionCardPolicy {
  policyDefinitionId: string;
  policyDefinition: PolicyDefinitionDto | null;
}

export interface ContractDefinitionCardCriterion {
  label: string;
  values: ContractDefinitionCardCriterionValue[];
}

export interface ContractDefinitionCardCriterionValue {
  type: 'string' | 'asset' | 'json';
  searchTargets: (string | null)[];
  value?: string;
  asset?: UiAssetMapped;
  json?: any;
}

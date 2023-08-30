import {Injectable} from '@angular/core';
import {
  ContractDefinitionEntry,
  ContractDefinitionPage,
  PolicyDefinitionDto,
  UiCriterionDto,
} from '@sovity.de/edc-client';
import {OPERATOR_SYMBOLS} from '../../../../core/services/api/policy-type-ext';
import {AssetProperties} from '../../../../core/services/asset-properties';
import {Asset} from '../../../../core/services/models/asset';
import {associateBy} from '../../../../core/utils/map-utils';
import {assetSearchTargets} from '../../../../core/utils/search-utils';
import {
  ContractDefinitionCard,
  ContractDefinitionCardCriterionValue,
  ContractDefinitionCardPolicy,
} from './contract-definition-card';

@Injectable({providedIn: 'root'})
export class ContractDefinitionCardBuilder {
  buildContractDefinitionCards(
    contractDefinitionPage: ContractDefinitionPage,
    assets: Asset[],
    policyDefinitions: PolicyDefinitionDto[],
  ): ContractDefinitionCard[] {
    const assetById = associateBy(assets, (asset) => asset.id);
    const policyDefinitionById = associateBy(
      policyDefinitions,
      (policyDefinition) => policyDefinition.policyDefinitionId,
    );

    return contractDefinitionPage.contractDefinitions.map(
      (contractDefinition) =>
        this.buildContractDefinitionCard(
          contractDefinition,
          assetById,
          policyDefinitionById,
        ),
    );
  }

  buildContractDefinitionCard(
    contractDefinition: ContractDefinitionEntry,
    assetById: Map<string, Asset>,
    policyDefinitionById: Map<string, PolicyDefinitionDto>,
  ): ContractDefinitionCard {
    return {
      id: contractDefinition.contractDefinitionId,
      contractPolicy: this.extractPolicy(
        contractDefinition.contractPolicyId,
        policyDefinitionById,
      ),
      accessPolicy: this.extractPolicy(
        contractDefinition.accessPolicyId,
        policyDefinitionById,
      ),

      criteria: contractDefinition.assetSelector.map((criterion) => ({
        label: this.extractCriterionOperation(criterion),
        values: this.extractCriterionValues(criterion, assetById),
      })),
      detailJsonObj: contractDefinition,
    };
  }

  private extractPolicy(
    policyDefinitionId: string,
    policyDefinitionsById: Map<string, PolicyDefinitionDto>,
  ): ContractDefinitionCardPolicy {
    return {
      policyDefinitionId: policyDefinitionId,
      policyDefinition: policyDefinitionsById.get(policyDefinitionId) || null,
    };
  }

  private extractCriterionOperation(criterion: UiCriterionDto): string {
    const {operandLeft, operator} = criterion;
    if (
      operandLeft.toLowerCase() === AssetProperties.id.toLowerCase() &&
      (operator === 'EQ' || operator === 'IN')
    ) {
      return 'Assets';
    }

    let operatorStr = OPERATOR_SYMBOLS[operator] ?? operator;
    return `${operandLeft} ${operatorStr}`;
  }

  private extractCriterionValues(
    criterion: UiCriterionDto,
    assetsById: Map<string, Asset>,
  ): ContractDefinitionCardCriterionValue[] {
    let {operandLeft, operandRight} = criterion;

    let values: string[] = [];
    if (operandRight.type === 'VALUE_LIST') {
      values = operandRight.valueList ?? [];
    } else {
      values = [operandRight.value!!];
    }

    return values.map((it) => {
      const stringType: ContractDefinitionCardCriterionValue = {
        type: 'string',
        value: it,
        searchTargets: [it],
      };

      // Try to find asset
      if (operandLeft === AssetProperties.id) {
        let asset = assetsById.get(it);
        if (asset) {
          return {
            type: 'asset',
            asset,
            searchTargets: assetSearchTargets(asset),
          };
        }

        return stringType;
      }

      return stringType;
    });
  }
}

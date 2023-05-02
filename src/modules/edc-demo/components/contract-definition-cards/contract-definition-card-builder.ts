import {Injectable} from '@angular/core';
import {
  ContractDefinitionDto,
  Criterion,
  PolicyDefinition,
  policyDefinitionId,
} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {AssetProperties} from '../../services/asset-properties';
import {Operator, OperatorSymbols} from '../../services/policy-type-ext';
import {associateBy} from '../../utils/map-utils';
import {assetSearchTargets} from '../../utils/search-utils';
import {
  ContractDefinitionCard,
  ContractDefinitionCardCriterionValue,
  ContractDefinitionCardPolicy,
} from './contract-definition-card';

@Injectable({providedIn: 'root'})
export class ContractDefinitionCardBuilder {
  buildContractDefinitionCards(
    contractDefinitions: ContractDefinitionDto[],
    assets: Asset[],
    policyDefinitions: PolicyDefinition[],
  ): ContractDefinitionCard[] {
    const assetById = associateBy(assets, (asset) => asset.id);
    const policyDefinitionById = associateBy(
      policyDefinitions,
      policyDefinitionId,
    );

    return contractDefinitions.map((contractDefinition) =>
      this.buildContractDefinitionCard(
        contractDefinition,
        assetById,
        policyDefinitionById,
      ),
    );
  }

  buildContractDefinitionCard(
    contractDefinition: ContractDefinitionDto,
    assetById: Map<string, Asset>,
    policyDefinitionById: Map<string, PolicyDefinition>,
  ): ContractDefinitionCard {
    return {
      id: contractDefinition.id,
      contractPolicy: this.extractPolicy(
        contractDefinition.contractPolicyId,
        policyDefinitionById,
      ),
      accessPolicy: this.extractPolicy(
        contractDefinition.accessPolicyId,
        policyDefinitionById,
      ),

      criteria: contractDefinition.criteria.map((criterion) => ({
        label: this.extractCriterionOperation(criterion),
        values: this.extractCriterionValues(criterion, assetById),
      })),
      detailJsonObj: contractDefinition,
    };
  }

  private extractPolicy(
    policyDefinitionId: string,
    policyDefinitionsById: Map<string, PolicyDefinition>,
  ): ContractDefinitionCardPolicy {
    return {
      policyDefinitionId: policyDefinitionId,
      policyDefinition: policyDefinitionsById.get(policyDefinitionId) || null,
    };
  }

  private extractCriterionOperation(criterion: Criterion): string {
    const {operandLeft} = criterion;
    if (
      operandLeft.toLowerCase() === AssetProperties.id &&
      (criterion.operator.toUpperCase() === 'EQ' ||
        criterion.operator.toUpperCase() === 'IN')
    ) {
      return 'Assets';
    }

    let operator =
      OperatorSymbols[criterion.operator as Operator] ?? criterion.operator;
    return `${operandLeft} ${operator}`;
  }

  private extractCriterionValues(
    criterion: Criterion,
    assetsById: Map<string, Asset>,
  ): ContractDefinitionCardCriterionValue[] {
    let {operandLeft, operandRight} = criterion;

    let values: (object | string)[] = [];
    if (Array.isArray(operandRight)) {
      values = operandRight as string[];
    } else {
      values = [operandRight];
    }

    return values.map((it) => {
      if (typeof it === 'string') {
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
        }

        return stringType;
      }

      // fall back to JSON for generic objects
      return {type: 'json', json: it, searchTargets: [JSON.stringify(it)]};
    });
  }
}

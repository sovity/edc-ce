import {Injectable} from '@angular/core';
import {
  Constraint,
  Permission,
  Policy,
  PolicyDefinition,
} from '../../edc-dmgmt-client';
import {
  AtomicConstraint,
  EdcTypes,
  LiteralExpression,
  Operator,
} from './policy-type-ext';

@Injectable({
  providedIn: 'root',
})
export class PolicyDefinitionUtils {
  /**
   * Currently our policies only require a subset of the policy definition type
   *
   * A policy definition is regular, if it only uses one permission with atomic constraints
   *
   * @param policyDefinition
   */
  getPolicyIrregularities(policyDefinition: PolicyDefinition): string[] {
    const irregularities: string[] = [];
    const checkConstraint = (prefix: string, constraint: Constraint) => {
      if (!this.isAtomicConstraint(constraint)) {
        irregularities.push(prefix);
      } else {
        if (!this.isLiteralExpression(constraint.leftExpression)) {
          irregularities.push(
            `${prefix}.leftExpression is not literal expression`,
          );
        }
        if (!this.isLiteralExpression(constraint.rightExpression)) {
          irregularities.push(
            `${prefix}.rightExpression is not literal expression`,
          );
        }
      }
    };

    const checkPermission = (prefix: string, permission: Permission) => {
      if (!this.isPermission(permission)) {
        irregularities.push(prefix);
      } else {
        if (permission.action?.type !== 'USE') {
          irregularities.push(`${prefix}.action was not USE`);
        }
        if (permission.assigner) {
          irregularities.push(`${prefix}.assigner was set`);
        }
        if (permission.assignee) {
          irregularities.push(`${prefix}.assignee was set`);
        }
        if (permission.duties?.length) {
          irregularities.push(`${prefix}.duties was non-empty`);
        }
        permission.constraints?.forEach((constraint, index) =>
          checkConstraint(`${prefix}.constraints[${index}]`, constraint),
        );
      }
    };

    const checkPolicy = (prefix: string, policy: Policy) => {
      if (policy.inheritsFrom) {
        irregularities.push(`${prefix}.inheritsFrom was set`);
      }
      if (policy.assigner) {
        irregularities.push(`${prefix}.assigner was set`);
      }
      if (policy.assignee) {
        irregularities.push(`${prefix}.assignee was set`);
      }
      if (policy.obligations?.length) {
        irregularities.push(`${prefix}.obligations was set`);
      }
      if (policy.prohibitions?.length) {
        irregularities.push(`${prefix}.prohibitions was set`);
      }
      if (!policy.permissions?.length) {
        irregularities.push(`${prefix}.permissions is empty`);
      } else if (policy.permissions.length > 1) {
        irregularities.push(`${prefix}.permissions more than one entry`);
      }
      policy.permissions?.forEach((permission, index) =>
        checkPermission(`${prefix}.permissions[${index}]`, permission),
      );
    };
    checkPolicy('policy', policyDefinition.policy);
    return irregularities;
  }

  isPermission(obj: any): obj is Permission {
    return obj.edctype === EdcTypes.Permission;
  }

  buildPermission(patch: Partial<Permission>): Permission {
    return {
      edctype: EdcTypes.Permission,
      id: null,
      action: {type: 'USE'},
      constraints: [],
      duties: [],
      ...patch,
    } as Permission;
  }

  isAtomicConstraint(obj: any): obj is AtomicConstraint {
    return obj.edctype === EdcTypes.AtomicConstraint;
  }

  buildAtomicConstraint(
    left: string,
    operator: Operator,
    right: string,
  ): AtomicConstraint {
    return {
      edctype: EdcTypes.AtomicConstraint,
      leftExpression: this.buildLiteralExpression(left),
      rightExpression: this.buildLiteralExpression(right),
      operator,
    };
  }

  isLiteralExpression(obj: any): obj is LiteralExpression {
    return obj.edctype === EdcTypes.LiteralExpression;
  }

  buildLiteralExpression(value: string): LiteralExpression {
    return {
      edctype: EdcTypes.LiteralExpression,
      value,
    };
  }
}

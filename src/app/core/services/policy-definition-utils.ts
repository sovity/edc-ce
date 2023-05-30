import {Injectable} from '@angular/core';
import {cleanJson} from '../../component-library/json-dialog/json-dialog/clean-json';
import {
  Action,
  Constraint,
  Permission,
  Policy,
  PolicyDefinition,
} from './api/legacy-managent-api-client';
import {
  AtomicConstraint,
  EdcTypes,
  LiteralExpression,
  Operator,
} from './api/policy-type-ext';

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
    policyDefinition = cleanJson(policyDefinition) as PolicyDefinition;

    const irregularities: string[] = [];

    const defaultKeys = ['edctype', '@type'];
    const checkKeys = <T>(
      prefix: string,
      value: T | null | undefined,
      allowedKeys: (keyof T)[],
    ) => {
      if (value != null) {
        Object.keys(value).forEach((key) => {
          if (
            !allowedKeys.includes(key as keyof T) &&
            !defaultKeys.includes(key)
          ) {
            irregularities.push(`${prefix}.${key} was set`);
          }
        });
      } else {
        irregularities.push(`${prefix} was not set`);
      }
    };

    const checkEdctype = <T, R>(
      prefix: string,
      value: T | null | undefined,
      typeChecker: (item: any) => item is R,
      isNotMessage: string,
      ifOkFn?: (value: R) => void,
    ) => {
      if (typeChecker(value)) {
        if (ifOkFn) ifOkFn(value);
      } else {
        irregularities.push(`${prefix} is not ${isNotMessage}`);
      }
    };

    const checkConstraint = (prefix: string, constraint: Constraint) => {
      checkEdctype(
        prefix,
        constraint,
        this.isAtomicConstraint,
        'an atomic constraint',
        (constraint) => {
          checkKeys(prefix, constraint, [
            'leftExpression',
            'operator',
            'rightExpression',
          ]);
          checkEdctype(
            `${prefix}.leftExpression`,
            constraint.leftExpression,
            this.isLiteralExpression,
            'a literal expression',
          );
          checkEdctype(
            `${prefix}.rightExpression`,
            constraint.rightExpression,
            this.isLiteralExpression,
            'a literal expression',
          );
        },
      );
    };

    const checkAction = (prefix: string, action: Action | undefined) => {
      checkKeys(prefix, action, ['type']);
      if (action && action.type !== 'USE') {
        irregularities.push(`${prefix}.type wasn't 'USE'`);
      }
    };

    const checkPermission = (prefix: string, permission: Permission) => {
      checkEdctype(
        prefix,
        permission,
        this.isPermission,
        'a permission',
        (permission) => {
          checkKeys(prefix, permission, ['constraints', 'action']);
          checkAction(`${prefix}.action`, permission.action);
          permission.constraints?.forEach((constraint, index) =>
            checkConstraint(`${prefix}.constraints[${index}]`, constraint),
          );
        },
      );
    };

    const checkPolicy = (prefix: string, policy: Policy) => {
      checkKeys(prefix, policy, ['type', 'permissions']);
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

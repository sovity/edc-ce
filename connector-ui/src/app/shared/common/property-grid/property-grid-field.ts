/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {PolicyExpressionMapped} from '../../business/policy-editor/model/policy-expression-mapped';

export interface PropertyGridField {
  icon: string;

  /**
   * Title of Property
   */
  label: string;

  /**
   * Adds "title"-Attribute to Label HTML Element
   */
  labelTitle?: string;

  /**
   * Property Value
   */
  text?: string;

  url?: string;
  onclick?: () => void;

  /**
   * Additional classes for the value text.
   */
  additionalClasses?: string;

  /**
   * Additional classes for the container
   */
  additionalContainerClasses?: string;

  /**
   * Additional classes for the icon.
   */
  additionalIconClasses?: string;

  copyButton?: boolean;
  tooltip?: string | null;
  textIconAfter?: string | null;

  policy?: PolicyExpressionMapped;
  policyErrors?: string[];

  /**
   * Hide text
   */
  hideFieldValue?: boolean;
}

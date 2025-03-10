/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
module.exports = {
  tabWidth: 2,
  useTabs: false,
  singleQuote: true,
  semi: true,
  arrowParens: 'always',
  trailingComma: 'all',
  bracketSameLine: true,
  printWidth: 80,
  bracketSpacing: false,
  proseWrap: 'always',

  attributeGroups: [
    '$ANGULAR_STRUCTURAL_DIRECTIVE',
    '^(id|name)$',
    '^class$',
    '$DEFAULT',
    '^aria-',
    '$ANGULAR_INPUT',
    '$ANGULAR_TWO_WAY_BINDING',
    '$ANGULAR_OUTPUT',
  ],

  // @trivago/prettier-plugin-sort-imports
  importOrder: [
    // this import needs to be on top or tests fail
    '^zone.js/testing$',
    // third parties first
    '^@angular/(.*)$',
    '^rxjs(/(.*))?$',
    '<THIRD_PARTY_MODULES>',
    // rest after
    '^[./]',
  ],
  importOrderParserPlugins: [
    'typescript',
    'classProperties',
    'decorators-legacy',
  ],
  importOrderSeparation: false,
  importOrderSortSpecifiers: true,
};

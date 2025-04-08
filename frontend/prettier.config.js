/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * @see https://prettier.io/docs/configuration
 * @type {import("prettier").Config}
 */
const config = {
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

  // @trivago/prettier-plugin-sort-imports
  importOrder: [
    '^react(/(.*))?$',
    '^next/(.*)$',
    '^next-auth(/(.*))?$',
    '^@/server/(.*)$',
    '^@/components/(.*)$',
    '<THIRD_PARTY_MODULES>',
    '^[./]',
  ],
  importOrderParserPlugins: ['typescript', 'jsx', 'decorators-legacy'],
  importOrderSeparation: false,
  importOrderSortSpecifiers: true,

  plugins: [
    'prettier-plugin-tailwindcss',
    '@trivago/prettier-plugin-sort-imports',
  ],
  overrides: [
    {
      files: ['*.tsx', '*.ts'],
      options: {
        parser: 'babel-ts',
      },
    },
  ],
};

export default config;

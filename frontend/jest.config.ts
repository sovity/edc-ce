/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import path from 'node:path';
import {type JestConfigWithTsJest, pathsToModuleNameMapper} from 'ts-jest';
import ts from 'typescript';

// tsconfig.json contains comments, so it cannot be imported as a JSON module.
const {config} = ts.readConfigFile(
  path.join(__dirname, 'tsconfig.json'),
  ts.sys.readFile,
);

const jestConfig: JestConfigWithTsJest = {
  preset: 'ts-jest',
  modulePathIgnorePatterns: ['e2e'],
  // Mirror the path aliases of tsconfig.json
  moduleNameMapper: pathsToModuleNameMapper(config.compilerOptions.paths, {
    prefix: '<rootDir>/',
  }),
};

export default jestConfig;

/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
const os = require('os');
const spawn = require('child_process').spawn;

let executable;
if (os.platform() === 'win32') {
  executable = `gradlew.bat`;
} else {
  executable = `./gradlew`;
}

let args = process.argv.slice(2);
console.log(`CWD: ${process.cwd()}`);
console.log(`Running: ${executable} ${args.join(' ')}`);
const run = spawn(executable, args, {stdio: 'inherit', shell: true});

run.on('close', (/** @type {number | undefined} */ code) => {
  process.exit(code);
});

/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

const fs = require('fs');
const path = require('path');

const dir = 'src';
const messages = 'messages';

const usedStrings = (dir) => {
  const regex = /'[a-zA-Z0-9_.]+'|"[a-zA-Z0-9_.]+"/g;
  const usedStrings = new Set();

  const traverseDirectory = (directory) => {
    const files = fs.readdirSync(directory);

    files.forEach((file) => {
      const filePath = path.join(directory, file);
      const stat = fs.statSync(filePath);

      if (stat.isDirectory()) {
        traverseDirectory(filePath);
      } else if (file.endsWith('.ts') || file.endsWith('.tsx')) {
        const content = fs.readFileSync(filePath, 'utf-8');
        const matches = content.matchAll(regex);
        [...matches].forEach((match) => {
          usedStrings.add(match[0].replace(/['"]/g, ''));
        });
      }
    });
  };

  traverseDirectory(dir);

  return usedStrings;
};

console.log('Collecting all strings');
const strings = usedStrings(dir);
console.log(`Collected ${strings.size} strings`);

fs.readdirSync(messages)
  .filter((it) => it.endsWith('.json'))
  .forEach((filename) => {
    const filePath = path.join(messages, filename);
    const json = JSON.parse(fs.readFileSync(filePath, 'utf-8'));

    for (const key in json) {
      if (!strings.has(key)) {
        console.log(`Unused translation key: ${key} in ${filename}`);
        json[key] = undefined;
      }
    }

    const sortedJson = Object.keys(json)
      .sort()
      .reduce((acc, key) => {
        acc[key] = json[key];
        return acc;
      }, {});

    fs.writeFileSync(filePath, JSON.stringify(sortedJson, null, 2) + '\n');
  });
console.log('Finished checking all translation keys for usage.');

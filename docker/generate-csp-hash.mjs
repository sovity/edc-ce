#!/usr/bin/env node
import {createHash} from 'node:crypto';
import {readFile} from 'node:fs/promises';

async function hashFile(file) {
  const input = await readFile(file);
  // Angular uses sha384 for CSP hashes
  const hash = createHash('sha384').update(input).digest('base64');

  return `sha384-${hash}`;
}

const files = process.argv.slice(2);
const hashes = await Promise.all(files.map(hashFile));

// CSP hashes must be surrounded by single quotes
console.log(hashes.map((s) => `'${s}'`).join(' '));

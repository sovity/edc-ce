/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

const excludes = [
  // Root layout is required to be server component
  'src/app/layout.tsx',

  // Ensure access to env.js
  'src/components/dev-utilities/tailwind-indicator.tsx',
];

const fs = require('fs').promises;
const path = require('path');

async function addUseClient(directory) {
  // Read all files in the directory
  const files = await fs.readdir(directory, {recursive: true});

  // Process each TSX file
  for (const file of files) {
    if (file.endsWith('.tsx')) {
      const filePath = path.join(directory, file);

      try {
        // Read the file contents
        const content = await fs.readFile(filePath, 'utf8');
        const match = content
          .toString()
          .match(
            /^(?<comment>\/\*[^*]*\*+(?:[^/*][^*]*\*+)*\/)?\s*(?<use_client>'use client'(;?))?\s*/,
          );

        const comment = match?.groups?.comment;
        const hasUseClient = match?.groups?.use_client != null;
        const remainingContent = content
          .toString()
          .substring(match?.[0]?.length);

        const fullFilePath = filePath.replace(/\\/g, '/').toString();
        const shouldHaveUseClient = !excludes.includes(fullFilePath);

        if (shouldHaveUseClient) {
          if (hasUseClient) {
            console.log(`OK ${file}: already contains 'use client'`);
          } else {
            // Add 'use client' directive
            let newContent;
            if (comment == null) {
              newContent = `'use client';\n\n${remainingContent}`;
            } else if (comment?.toLowerCase().includes('copyright')) {
              // license header
              newContent = `${comment}\n'use client';\n\n${remainingContent}`;
            } else {
              // license header
              newContent = `'use client';\n\n${comment}\n${remainingContent}`;
            }
            // Write the modified content back to the file
            await fs.writeFile(filePath, newContent);
            console.log(`MODIFIED: Added 'use client' to ${file}`);
          }
        } else {
          if (hasUseClient) {
            console.error(`ERROR: ${file} should not contain 'use client'`);
            return;
          } else {
            console.log(`OK ${file}: does not contain 'use client'`);
          }
        }
      } catch (error) {
        console.error(`Error processing ${file}:`, error.message);
      }
    }
  }
}

// Get the directory path from command line arguments or use current directory
const directory = process.argv[2] || '.';
console.log(`Processing TSX files in directory: ${directory}`);

addUseClient(directory).catch((error) => {
  console.error('An error occurred:', error.message);
  process.exit(1);
});

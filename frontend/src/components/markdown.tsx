/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {markdownToHtml, sanitizeHtml} from '@/lib/utils/html-utils';

export interface MarkdownProps {
  markdown: string;
}

export default function Markdown({markdown}: MarkdownProps) {
  return (
    <article
      className="prose"
      dangerouslySetInnerHTML={{
        __html: sanitizeHtml(markdownToHtml(markdown)),
      }}
    />
  );
}

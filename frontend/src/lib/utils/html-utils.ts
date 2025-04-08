/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import dompurify from 'dompurify';
import {marked} from 'marked';

export const sanitizeHtml = (html: string) => {
  return dompurify.sanitize(html);
};

export const markdownToHtml = (markdown: string) => {
  const renderer = new marked.Renderer();
  renderer.image = ({href, text}) =>
    `<a href="${href}" target="_blank"><img src="${href}" alt="${text}" class="h-72"></a>`;

  marked.use({renderer});

  return marked.parse(markdown, {async: false});
};

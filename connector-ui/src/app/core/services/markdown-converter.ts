/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {marked} from 'marked';

@Injectable({providedIn: 'root'})
export class MarkdownConverter {
  constructor() {
    const renderer = new marked.Renderer();

    renderer.image = function (href, title, alt) {
      return `<a href="${href}" target="_blank"><img src="${href}" alt="${alt}"></a>`;
    };

    marked.use({renderer});
  }

  toHtml(markdown: string) {
    return marked.parse(markdown).toString();
  }
}

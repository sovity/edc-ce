/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {DOCUMENT} from '@angular/common';
import {Inject, Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FaviconService {
  constructor(@Inject(DOCUMENT) private document: Document) {}

  setFavicon(src: string) {
    const link = this.findOrCreateLink('icon');
    link.setAttribute('href', src);
  }

  private findOrCreateLink(rel: string): HTMLLinkElement {
    let link = document.querySelector(`link[rel~='${rel}']`) as HTMLLinkElement;
    if (!link) {
      link = this.document.createElement('link');
      link.setAttribute('rel', rel);

      const head = this.document.querySelector('head') as HTMLHeadElement;
      head.appendChild(link);
    }
    return link;
  }
}

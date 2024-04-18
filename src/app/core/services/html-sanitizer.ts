import {Injectable} from '@angular/core';
import {addHook, sanitize} from 'isomorphic-dompurify';

@Injectable({providedIn: 'root'})
export class HtmlSanitizer {
  constructor() {
    addHook('afterSanitizeAttributes', function (node) {
      // https://developer.chrome.com/docs/lighthouse/best-practices/external-anchors-use-rel-noopener/
      if ('target' in node) {
        node.setAttribute('target', '_blank');
        node.setAttribute('rel', 'noopener noreferrer');
      }
    });
  }

  sanitize(html: string) {
    return sanitize(html);
  }
}

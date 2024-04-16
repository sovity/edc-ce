import {Injectable, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Injectable({providedIn: 'root'})
export class HtmlSanitizer {
  constructor(private readonly domSanitizer: DomSanitizer) {}

  sanitize(html: string) {
    return this.domSanitizer.sanitize(SecurityContext.HTML, html);
  }
}

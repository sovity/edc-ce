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

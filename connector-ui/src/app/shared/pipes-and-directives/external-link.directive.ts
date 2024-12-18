import {AfterViewInit, Directive, ElementRef} from '@angular/core';

@Directive({
  selector: '[externalLink]',
})
export class ExternalLinkDirective implements AfterViewInit {
  constructor(private elementRef: ElementRef) {}

  ngAfterViewInit() {
    const element = this.elementRef.nativeElement as HTMLAnchorElement;
    element.setAttribute('target', '_blank');
    element.setAttribute('rel', 'noopener noreferrer');
  }
}

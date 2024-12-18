import {AfterViewInit, Directive, ElementRef} from '@angular/core';

@Directive({
  selector: '[autofocus]',
})
export class AutofocusDirective implements AfterViewInit {
  constructor(private elementRef: ElementRef) {}

  ngAfterViewInit() {
    const ele = this.elementRef.nativeElement as HTMLInputElement;
    setTimeout(() => {
      ele.focus();
      ele.select();
    }, 100);
  }
}

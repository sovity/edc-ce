import {
  AfterViewChecked,
  AfterViewInit,
  Directive,
  ElementRef,
  Input,
} from '@angular/core';

/**
 * Angular Material automatically adds CSS classes when you use their directives.
 *
 * But if you don't use their directives, the scrollbars in dialogs break, for example.
 */
@Directive({
  selector: '[removeClass]',
})
export class RemoveClassDirective implements AfterViewChecked, AfterViewInit {
  @Input()
  removeClass!: string;

  constructor(private elementRef: ElementRef) {}

  ngAfterViewInit() {
    this.removeClassIfNecessary();
  }

  ngAfterViewChecked() {
    this.removeClassIfNecessary();
  }

  private removeClassIfNecessary() {
    const classList = (this.elementRef.nativeElement as HTMLElement).classList;
    if (classList.contains(this.removeClass)) {
      classList.remove(this.removeClass);
    }
  }
}

/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
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

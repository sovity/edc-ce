/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
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

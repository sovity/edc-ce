/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Fraunhofer FIT - contributed initial internationalization support
 *     sovity - continued development
 */
import {Component, Input, OnChanges, OnDestroy} from '@angular/core';
import {Subject, switchMap} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SimpleChangesTyped} from '../../../core/utils/angular-utils';

@Component({
  selector: 'translate-with-slot',
  templateUrl: './translate-with-slot.component.html',
})
export class TranslateWithSlotComponent implements OnChanges, OnDestroy {
  @Input()
  key!: string;

  @Input()
  html = false;

  key$ = new Subject();

  textBefore = '';
  hasMiddle = false;
  textAfter = '';

  constructor(private translationService: TranslateService) {
    this.splitText();
  }

  splitText() {
    this.key$
      .pipe(
        switchMap(() => this.translationService.stream(this.key)),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe((text) => {
        const parts = text.split('{}');
        this.textBefore = parts[0];
        this.hasMiddle = parts.length > 1;
        this.textAfter = parts[1] || '';
      });
  }

  ngOnChanges(changes: SimpleChangesTyped<TranslateWithSlotComponent>) {
    if (changes.key) {
      this.key$.next(changes.key);
    }
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}

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
import {Injectable} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  TitleStrategy,
} from '@angular/router';
import {Subject, switchMap} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class CustomPageTitleStrategy extends TitleStrategy {
  rawTitle$ = new Subject<string>();

  constructor(
    private translateService: TranslateService,
    private title: Title,
  ) {
    super();
    this.rawTitle$
      .pipe(switchMap((title) => this.translateService.get(title)))
      .subscribe((title) => this.title.setTitle(title));
  }

  override updateTitle(snapshot: RouterStateSnapshot): void {
    const data = this.getRouteDataRecursively(snapshot);
    const titleUntranslated = data.title ?? 'EDC Connector';
    this.rawTitle$.next(titleUntranslated);
  }

  private getRouteDataRecursively(
    routerStateSnapshot: RouterStateSnapshot,
  ): any {
    let snapshot: ActivatedRouteSnapshot | null = routerStateSnapshot.root;
    let data = {};
    while (snapshot) {
      data = {...data, ...snapshot.data};
      snapshot = snapshot.firstChild;
    }
    return data;
  }
}

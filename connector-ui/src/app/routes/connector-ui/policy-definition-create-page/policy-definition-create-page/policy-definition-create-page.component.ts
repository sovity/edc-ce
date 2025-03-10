/*
 * Copyright 2022 Eclipse Foundation and Contributors
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
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
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Component, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import {Subject} from 'rxjs';
import {finalize, takeUntil} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {PolicyDefinitionCreateDto} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {ExpressionFormHandler} from '../../../../shared/business/policy-editor/editor/expression-form-handler';
import {policyFormRequiredViewProviders} from '../../../../shared/business/policy-editor/editor/policy-form-required-providers';
import {PolicyDefinitionCreatePageForm} from './policy-definition-create-page-form';

@Component({
  selector: 'policy-definition-create-page',
  templateUrl: './policy-definition-create-page.component.html',
  viewProviders: [
    ...policyFormRequiredViewProviders,
    PolicyDefinitionCreatePageForm,
  ],
})
export class PolicyDefinitionCreatePageComponent implements OnDestroy {
  loading = false;

  constructor(
    private router: Router,
    public form: PolicyDefinitionCreatePageForm,
    public expressionFormHandler: ExpressionFormHandler,
    public validationMessages: ValidationMessages,
    private edcApiService: EdcApiService,
    private notificationService: NotificationService,
    private translateService: TranslateService,
  ) {}

  onSave() {
    const createDto = this.buildPolicyDefinitionCreateDto();
    this.form.group.disable();
    this.loading = true;
    this.edcApiService
      .createPolicyDefinitionV2(createDto)
      .pipe(
        takeUntil(this.ngOnDestroy$),
        finalize(() => {
          this.form.group.enable();
          this.loading = false;
        }),
      )
      .subscribe({
        complete: () => {
          this.notificationService.showInfo(
            this.translateService.instant('notification.succ_pol'),
          );
          this.router.navigate(['/policies']);
        },
        error: (error) => {
          const message = this.translateService.instant(
            'notification.failed_create_policy',
          );
          console.error(message, error);
          this.notificationService.showError(message);
        },
      });
  }

  private buildPolicyDefinitionCreateDto(): PolicyDefinitionCreateDto {
    return {
      policyDefinitionId: this.form.group.controls.id.value,
      expression: this.expressionFormHandler.toUiPolicyExpression(),
    };
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}

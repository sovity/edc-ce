import {Component, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import {Subject} from 'rxjs';
import {finalize, takeUntil} from 'rxjs/operators';
import {PolicyDefinitionCreateDto} from '@sovity.de/edc-client';
import {ExpressionFormControls} from '../../../../component-library/policy-editor/editor/expression-form-controls';
import {ExpressionFormHandler} from '../../../../component-library/policy-editor/editor/expression-form-handler';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {PolicyDefinitionCreatePageForm} from './policy-definition-create-page-form';

@Component({
  selector: 'policy-definition-create-page',
  templateUrl: './policy-definition-create-page.component.html',
  viewProviders: [
    ExpressionFormHandler,
    ExpressionFormControls,
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
          this.notificationService.showInfo('Successfully created policy.');
          this.router.navigate(['/policies']);
        },
        error: (error) => {
          console.error('Failed creating Policy!', error);
          this.notificationService.showError('Failed creating policy!');
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

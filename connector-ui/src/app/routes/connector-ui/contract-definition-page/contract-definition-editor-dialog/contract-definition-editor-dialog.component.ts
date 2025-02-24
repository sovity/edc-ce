import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {finalize, takeUntil} from 'rxjs/operators';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {AssetService} from '../../../../core/services/asset.service';
import {ContractDefinitionBuilder} from '../../../../core/services/contract-definition-builder';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {ContractDefinitionEditorDialogForm} from './contract-definition-editor-dialog-form';
import {ContractDefinitionEditorDialogResult} from './contract-definition-editor-dialog-result';

@Component({
  selector: 'contract-definition-editor-dialog',
  templateUrl: './contract-definition-editor-dialog.component.html',
  providers: [ContractDefinitionEditorDialogForm],
})
export class ContractDefinitionEditorDialog implements OnInit, OnDestroy {
  policies: PolicyDefinitionDto[] = [];
  assets: UiAssetMapped[] = [];
  loading = false;

  constructor(
    private assetServiceMapped: AssetService,
    public form: ContractDefinitionEditorDialogForm,
    private notificationService: NotificationService,
    private edcApiService: EdcApiService,
    private contractDefinitionBuilder: ContractDefinitionBuilder,
    private dialogRef: MatDialogRef<ContractDefinitionEditorDialog>,
    public validationMessages: ValidationMessages,
  ) {}

  ngOnInit() {
    this.edcApiService
      .getPolicyDefinitionPage()
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((policyDefinitionPage) => {
        this.policies = policyDefinitionPage.policies;
      });
    this.assetServiceMapped
      .fetchAssets()
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((assets) => {
        this.assets = assets;
      });
  }

  onCreate() {
    const formValue = this.form.value;
    const contractDefinition =
      this.contractDefinitionBuilder.buildContractDefinition(formValue);
    this.loading = true;
    this.edcApiService
      .createContractDefinition(contractDefinition)
      .pipe(
        takeUntil(this.ngOnDestroy$),
        finalize(() => {
          this.form.group.enable();
          this.loading = false;
        }),
      )
      .subscribe({
        complete: () => {
          this.notificationService.showInfo('Successfully created data offer.');
          this.close({refreshList: true});
        },
        error: (error) => {
          if (error.status == 409) {
            this.notificationService.showError('Data offer ID already taken.');
          } else if (error.status >= 500) {
            this.notificationService.showError(
              'Error creating data offer: ' + (error?.error?.message ?? '???'),
            );
          }
          console.error('Error creating data offer!', error);
        },
      });
  }

  private close(params: ContractDefinitionEditorDialogResult) {
    this.dialogRef.close(params);
  }
  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}

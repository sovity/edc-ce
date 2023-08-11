import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {finalize, takeUntil} from 'rxjs/operators';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {
  ContractDefinitionService,
  PolicyDefinition,
  PolicyService,
} from '../../../../core/services/api/legacy-managent-api-client';
import {AssetEntryBuilder} from '../../../../core/services/asset-entry-builder';
import {AssetPropertyMapper} from '../../../../core/services/asset-property-mapper';
import {ContractDefinitionBuilder} from '../../../../core/services/contract-definition-builder';
import {Asset} from '../../../../core/services/models/asset';
import {NotificationService} from '../../../../core/services/notification.service';
import {ValidationMessages} from '../../../../core/validators/validation-messages';
import {ContractDefinitionEditorDialogForm} from './contract-definition-editor-dialog-form';
import {ContractDefinitionEditorDialogResult} from './contract-definition-editor-dialog-result';

@Component({
  selector: 'contract-definition-editor-dialog',
  templateUrl: './contract-definition-editor-dialog.component.html',
  providers: [ContractDefinitionEditorDialogForm, AssetEntryBuilder],
})
export class ContractDefinitionEditorDialog implements OnInit, OnDestroy {
  policies: PolicyDefinition[] = [];
  assets: Asset[] = [];
  loading = false;

  constructor(
    private edcApiService: EdcApiService,
    public form: ContractDefinitionEditorDialogForm,
    private notificationService: NotificationService,
    private policyService: PolicyService,
    private assetPropertyMapper: AssetPropertyMapper,
    private contractDefinitionService: ContractDefinitionService,
    private contractDefinitionBuilder: ContractDefinitionBuilder,
    private dialogRef: MatDialogRef<ContractDefinitionEditorDialog>,
    public validationMessages: ValidationMessages,
  ) {}

  ngOnInit() {
    this.policyService
      .getAllPolicies(0, 10_000_000)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((polices) => {
        this.policies = polices;
      });
    this.edcApiService
      .getAssetPage()
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((assetPage) => {
        this.assets = assetPage.assets.map((it) =>
          this.assetPropertyMapper.buildAssetFromProperties(it.properties),
        );
      });
  }

  onCreate() {
    const formValue = this.form.value;
    const contractDefinition =
      this.contractDefinitionBuilder.buildContractDefinition(formValue);
    this.form.group.disable();
    this.loading = true;
    this.contractDefinitionService
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
          this.notificationService.showInfo(
            'Successfully created contract definition.',
          );
          this.close({refreshList: true});
        },
        error: (error) => {
          if (error.status == 409) {
            this.notificationService.showError(
              'Contract Definition ID already taken.',
            );
          } else if (error.status >= 500) {
            this.notificationService.showError(
              'Error creating contract definition: ' +
                (error?.error?.message ?? '???'),
            );
          }
          console.error('Error creating contract definition!', error);
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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {AssetService, ContractDefinitionService, PolicyDefinition, PolicyService} from "../../../edc-dmgmt-client";
import {MatDialogRef} from "@angular/material/dialog";
import {ContractDefinitionEditorDialogForm} from "./contract-definition-editor-dialog-form";
import {AssetEntryBuilder} from "../../services/asset-entry-builder";
import {finalize, takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {NotificationService} from "../../services/notification.service";
import {ContractDefinitionEditorDialogResult} from "./contract-definition-editor-dialog-result";
import {ValidationMessages} from "../../validators/validation-messages";
import {Asset} from "../../models/asset";
import {ContractDefinitionBuilder} from "../../services/contract-definition-builder";
import {AssetPropertyMapper} from "../../services/asset-property-mapper";


@Component({
  selector: 'edc-demo-contract-definition-editor-dialog',
  templateUrl: './contract-definition-editor-dialog.component.html',
  providers: [ContractDefinitionEditorDialogForm, AssetEntryBuilder]
})
export class ContractDefinitionEditorDialog implements OnInit, OnDestroy {
  policies: PolicyDefinition[] = [];
  assets: Asset[] = [];
  loading = false

  constructor(
    public form: ContractDefinitionEditorDialogForm,
    private notificationService: NotificationService,
    private policyService: PolicyService,
    private assetService: AssetService,
    private assetPropertyMapper: AssetPropertyMapper,
    private contractDefinitionService: ContractDefinitionService,
    private contractDefinitionBuilder: ContractDefinitionBuilder,
    private dialogRef: MatDialogRef<ContractDefinitionEditorDialog>,
    public validationMessages: ValidationMessages,
  ) {
  }

  ngOnInit() {
    this.policyService.getAllPolicies().pipe(takeUntil(this.ngOnDestroy$))
      .subscribe(polices => {
        this.policies = polices;
        console.log(this.policies)
      });
    this.assetService.getAllAssets().pipe(takeUntil(this.ngOnDestroy$))
      .subscribe(assets => {
        this.assets = assets.map(it => this.assetPropertyMapper.readProperties(it.properties));
      });
  }

  onSave() {
    const formValue = this.form.value
    const contractDefinition = this.contractDefinitionBuilder.buildContractDefinition(formValue)

    this.form.group.disable();
    this.loading = true;
    this.contractDefinitionService.createContractDefinition(contractDefinition)
      .pipe(
        takeUntil(this.ngOnDestroy$),
        finalize(() => {
          this.form.group.enable();
          this.loading = false;
        })
      )
      .subscribe({
        complete: () => {
          this.notificationService.showInfo("Successfully created policy.");
          this.close({refreshList: true});
        },
        error: error => {
          console.error("Failed creating asset!", error);
          this.notificationService.showError("Failed creating policy!");
        }
      })
  }

  private close(params: ContractDefinitionEditorDialogResult) {
    this.dialogRef.close(params)
  }

  isEqualId(a: { id: string }, b: { id: string }): boolean {
    return a?.id === b?.id;
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}

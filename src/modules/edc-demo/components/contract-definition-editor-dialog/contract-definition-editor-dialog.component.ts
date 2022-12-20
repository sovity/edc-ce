import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {
  AssetService, ContractDefinitionRequestDto,
  CriterionDto, Policy, PolicyDefinitionResponseDto,
  PolicyService
} from "../../../mgmt-api-client";
import {map} from "rxjs/operators";
import {Asset} from "../../models/asset";


@Component({
  selector: 'edc-demo-contract-definition-editor-dialog',
  templateUrl: './contract-definition-editor-dialog.component.html',
  styleUrls: ['./contract-definition-editor-dialog.component.scss']
})
export class ContractDefinitionEditorDialog implements OnInit {

  policies: Array<PolicyDefinitionResponseDto> = [];
  availableAssets: Asset[] = [];
  name: string = '';
  editMode = false;
  accessPolicy?: PolicyDefinitionResponseDto;
  contractPolicy?: PolicyDefinitionResponseDto;
  assets: Asset[] = [];
  contractDefinition: ContractDefinitionRequestDto = {
    id: '',
    criteria: [],
    accessPolicyId: undefined!,
    contractPolicyId: undefined!
  };

  constructor(private policyService: PolicyService,
              private assetService: AssetService,
              private dialogRef: MatDialogRef<ContractDefinitionEditorDialog>,
              @Inject(MAT_DIALOG_DATA) contractDefinition?: ContractDefinitionRequestDto) {
    if (contractDefinition) {
      this.contractDefinition = contractDefinition;
      this.editMode = true;
    }
  }

  ngOnInit(): void {
    this.policyService.getAllPolicies().subscribe(policyDefinitions => {
      this.policies = policyDefinitions;
      this.accessPolicy = this.policies.find(policy => policy.id === this.contractDefinition.accessPolicyId);
      this.contractPolicy = this.policies.find(policy => policy.id === this.contractDefinition.contractPolicyId);
    });
    this.assetService.getAllAssets().pipe(map(asset => asset.map(a => new Asset(a.properties!)))).subscribe(assets => {
      this.availableAssets = assets;
      // preselection
      if (this.contractDefinition) {
        const assetIds = this.contractDefinition.criteria.map((c: CriterionDto) => c.operandRight?.toString());
        this.assets = this.availableAssets.filter(asset => assetIds.includes(asset.id));
      }
    })
  }

  onSave() {
    this.contractDefinition.accessPolicyId = this.accessPolicy!.id!;
    this.contractDefinition.contractPolicyId = this.contractPolicy!.id!;
    this.contractDefinition.criteria = [];

    const ids = this.assets.map(asset => asset.id);
    this.contractDefinition.criteria = [...this.contractDefinition.criteria, {
      operandLeft: 'asset:prop:id',
      operator: 'in',
      operandRight: ids,
    }];

    this.dialogRef.close({
      "contractDefinition": this.contractDefinition
    });
  }
}

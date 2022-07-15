import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AssetService, ContractDefinitionDto, PolicyDefinition, PolicyService} from "../../../edc-dmgmt-client";
import {map} from "rxjs/operators";
import {Asset} from "../../models/asset";


@Component({
  selector: 'edc-demo-contract-definition-editor-dialog',
  templateUrl: './contract-definition-editor-dialog.component.html',
  styleUrls: ['./contract-definition-editor-dialog.component.scss']
})
export class ContractDefinitionEditorDialog implements OnInit {

  policies: PolicyDefinition[] = [];
  availableAssets: Asset[] = [];
  name: string = '';
  editMode = false;
  accessPolicy?: PolicyDefinition;
  contractPolicy?: PolicyDefinition;
  assets: Asset[] = [];
  contractDefinition: ContractDefinitionDto = {
    id: '',
    criteria: [],
    accessPolicyId: undefined!,
    contractPolicyId: undefined!
  };

  constructor(private policyService: PolicyService,
              private assetService: AssetService,
              private dialogRef: MatDialogRef<ContractDefinitionEditorDialog>,
              @Inject(MAT_DIALOG_DATA) contractDefinition?: ContractDefinitionDto) {
    if (contractDefinition) {
      this.contractDefinition = contractDefinition;
      this.editMode = true;
    }
  }

  ngOnInit(): void {
    this.policyService.getAllPolicies().subscribe(polices => {
      this.policies = polices;
      this.accessPolicy = this.policies.find(policy => policy.uid === this.contractDefinition.accessPolicyId);
      this.contractPolicy = this.policies.find(policy => policy.uid === this.contractDefinition.contractPolicyId);
    });
    this.assetService.getAllAssets().pipe(map(asset => asset.map(a => new Asset(a.properties)))).subscribe(assets => {
      this.availableAssets = assets;
      // preselection
      if (this.contractDefinition) {
        const assetIds = this.contractDefinition.criteria.map(c => c.right);
        this.assets = this.availableAssets.filter(asset => assetIds.includes(asset.id));
      }
    })
  }

  onSave() {
    this.contractDefinition.accessPolicyId = this.accessPolicy!.uid;
    this.contractDefinition.contractPolicyId = this.contractPolicy!.uid;
    this.contractDefinition.criteria = [];

    const ids = this.assets.map(asset => asset.id);
    this.contractDefinition.criteria = [...this.contractDefinition.criteria, {
        left: 'asset:prop:id',
        op: 'in',
        right: ids,
      }];

    this.dialogRef.close({
      "contractDefinition": this.contractDefinition
    });
  }
}

import {Component, Inject, OnInit} from '@angular/core';
import {Permission, Policy, PolicyDefinition} from "../../../edc-dmgmt-client";
import TypeEnum = Policy.TypeEnum;
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-new-policy-dialog',
  templateUrl: './new-policy-dialog.component.html',
  styleUrls: ['./new-policy-dialog.component.scss']
})
export class NewPolicyDialogComponent implements OnInit {
  editMode: boolean = false;
  policy: Policy = {
    type: TypeEnum.Set
  };
  policyDefinition: PolicyDefinition = {
    policy: this.policy,
    id: ''
  };
  policyType: string = '';
  range = new FormGroup({
    start: new FormControl(null),
    end: new FormControl(null),
  });
  connectorId: string = '';

  constructor(private dialogRef: MatDialogRef<NewPolicyDialogComponent>) {
  }

  ngOnInit(): void {
    this.editMode = true;
  }

  onSave() {
    this.policyDefinition.id = this.policyDefinition.id.trim()

    if (this.policyType === "Time-Period-Restricted") {
      const permissionTemplate: string = "{    \"edctype\": \"dataspaceconnector:permission\",    \"id\": null,    \"target\": \"urn:artifact:urn:artifact:bitcoin\",    \"action\": {      \"type\": \"USE\",      \"includedIn\": null,      \"constraint\": null    },    \"assignee\": null,    \"assigner\": null,    \"constraints\": [      {        \"edctype\": \"AtomicConstraint\",        \"leftExpression\": {          \"edctype\": \"dataspaceconnector:literalexpression\",          \"value\": \"POLICY_EVALUATION_TIME\"        },        \"operator\": \"GT\",        \"rightExpression\": {          \"edctype\": \"dataspaceconnector:literalexpression\",          \"value\": \"2022-08-31T00:00:00.001Z\"        }      },      {        \"edctype\": \"AtomicConstraint\",        \"leftExpression\": {          \"edctype\": \"dataspaceconnector:literalexpression\",          \"value\": \"POLICY_EVALUATION_TIME\"        },        \"operator\": \"LT\",        \"rightExpression\": {          \"edctype\": \"dataspaceconnector:literalexpression\",          \"value\": \"2023-08-31T23:59:59.000Z\"        }      }    ],    \"duties\": []  }";
      let permission = JSON.parse(permissionTemplate);
      let constraints = permission["constraints"];
      let startDateConstraint = constraints[0]
      let endDateConstraint = constraints[1]
      let startDate: Date = this.range.value["start"] as Date
      let endDate: Date = this.range.value["end"] as Date
      startDateConstraint["rightExpression"]["value"] = startDate.toISOString();
      endDateConstraint["rightExpression"]["value"] = endDate.toISOString();
      this.policy.permissions = [permission]
    } else if (this.policyType === 'Connector-Restricted-Usage') {
      const permissionTemplate: string = "{\"edctype\":\"dataspaceconnector:permission\",\"id\":null,\"target\":\"urn:artifact:urn:artifact:bitcoin\",\"action\":{\"type\":\"USE\",\"includedIn\":null,\"constraint\":null},\"assignee\":null,\"assigner\":null,\"constraints\":[{\"edctype\":\"AtomicConstraint\",\"leftExpression\":{\"edctype\":\"dataspaceconnector:literalexpression\",\"value\":\"REFERRING_CONNECTOR\"},\"operator\":\"EQ\",\"rightExpression\":{\"edctype\":\"dataspaceconnector:literalexpression\",\"value\":\"http://example.org\"}}],\"duties\":[]}\n"
      let permission = JSON.parse(permissionTemplate);
      let constraints = permission["constraints"];
      let connectorConstraint = constraints[0]
      connectorConstraint["rightExpression"]["value"] = this.connectorId as string;
      this.policy.permissions = [permission]
    }

    this.dialogRef.close({
      policy: this.policyDefinition.policy,
      id: this.policyDefinition.id
    })
  }
}

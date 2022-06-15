import {Component, Inject, OnInit} from '@angular/core';
import {Policy} from "../../../edc-dmgmt-client";
import TypeEnum = Policy.TypeEnum;
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-new-policy-dialog',
  templateUrl: './new-policy-dialog.component.html',
  styleUrls: ['./new-policy-dialog.component.scss']
})
export class NewPolicyDialogComponent implements OnInit {
  editMode: boolean = false;
  policy: Policy = {
    uid: '',
    type: TypeEnum.Set
  };
  permissionsJson: string = '';
  prohibitionsJson: string = '';
  obligationsJson: string = '';

  constructor(private dialogRef: MatDialogRef<NewPolicyDialogComponent>) {
  }

  ngOnInit(): void {
    this.editMode = true;
  }

  onSave() {
    if (this.permissionsJson && this.permissionsJson !== '') {
      this.policy.permissions = JSON.parse(this.permissionsJson);
    }

    if (this.prohibitionsJson && this.prohibitionsJson !== '') {
      this.policy.prohibitions = JSON.parse(this.prohibitionsJson);
    }

    if (this.obligationsJson && this.obligationsJson !== '') {
      this.policy.obligations = JSON.parse(this.obligationsJson);
    }

    this.dialogRef.close({
      policy: this.policy
    })
  }
}

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent implements OnInit {


  constructor(public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogModel) {
  }

  ngOnInit(): void {
  }

  onCancel() {
    this.dialogRef.close(false);
  }

  onConfirm() {
    this.dialogRef.close(true);
  }
}

export class ConfirmDialogModel {
  private _confirmText: string = "OK";
  private _cancelText: string = "Cancel";
  private _cancelColor: "accent" | "warn" | "primary" | "" = "";
  private _confirmColor: "accent" | "warn" | "primary" | "" = "";

  constructor(public title: string, public message: string) {
  }

  get cancelColor(): "accent" | "warn" | "primary" | "" {
    return this._cancelColor;
  }

  set cancelColor(value: "accent" | "warn" | "primary" | "") {
    this._cancelColor = value;
  }

  get confirmColor(): "accent" | "warn" | "primary" | "" {
    return this._confirmColor;
  }

  set confirmColor(value: "accent" | "warn" | "primary" | "") {
    this._confirmColor = value;
  }

  get cancelText(): string {
    return this._cancelText;
  }

  set cancelText(value: string) {
    this._cancelText = value;
  }

  get confirmText(): string {
    return this._confirmText;
  }

  set confirmText(value: string) {
    this._confirmText = value;
  }

  public static forDelete(type: string, identifier: string): ConfirmDialogModel {
    const dialogData = new ConfirmDialogModel("Deletion confirmation", `Please confirm you want to delete ${type} ${identifier}. This action cannot be undone.`)
    dialogData.confirmColor = "warn";
    dialogData.confirmText = "Delete";
    dialogData.cancelText = "Cancel";
    return dialogData;
  }
}

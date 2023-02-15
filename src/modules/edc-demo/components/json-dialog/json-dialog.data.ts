import {Observable} from 'rxjs';
import {ConfirmDialogModel} from '../confirmation-dialog/confirmation-dialog.component';

export interface JsonDialogData {
  title: string;
  subtitle: string;
  icon: string;
  objectForJson: unknown;
  actionButton: DialogButton;
}

export interface DialogButton {
  text: string;
  color: 'accent' | 'warn' | 'primary';
  action: () => Observable<any> | any;
  confirmation?: ConfirmDialogModel;
}

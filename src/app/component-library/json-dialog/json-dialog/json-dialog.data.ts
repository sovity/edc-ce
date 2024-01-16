import {Observable} from 'rxjs';
import {ConfirmDialogModel} from '../../confirmation-dialog/confirmation-dialog/confirmation-dialog.component';

export interface JsonDialogData {
  title: string;
  subtitle: string;
  icon: string;
  objectForJson: unknown;
  toolbarButton?: DialogToolbarButton;
}

export interface DialogToolbarButton {
  text: string;
  icon: string;
  action: () => Observable<any> | any;
  confirmation?: ConfirmDialogModel;
}

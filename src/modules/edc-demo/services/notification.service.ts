import {Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarConfig, MatSnackBarDismiss} from "@angular/material/snack-bar";
import {Observable} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {
  }

  /**
   * Shows a snackbar message with a particular text
   * @param message The text to display
   * @param action A string specifying the text on an action button. If left out, no action button is shown.
   * If left out, and onAction is specified, "Done" is used as default.
   * @param onAction A callback that is invoked when the action button is clicked.
   */
  public showInfo(message: string, action?: string, onAction?: () => any): Observable<MatSnackBarDismiss> {

    if (!action && onAction) {
      action = "Done";
    }
    const config: MatSnackBarConfig = {
      duration: onAction ? 5000 : 3000, // no auto-cancel if an action was specified
      verticalPosition: "top",
      politeness: "polite",
      horizontalPosition: "center",
      panelClass: ["snackbar-info-style"] //see styles.scss
    }
    let ref = this.snackBar.open(message, action, config);

    if (onAction) {
      ref.onAction().subscribe(() => onAction());
    }

    return ref.afterDismissed()
  }

  public showError(message: string): Observable<MatSnackBarDismiss> {
    const config: MatSnackBarConfig = {
      duration: 5000, // no auto-cancel if an action was specified
      verticalPosition: "top",
      politeness: "assertive",
      horizontalPosition: "center",
      panelClass: ["snackbar-error-style"] //see styles.scss
    }

    let ref = this.snackBar.open(message, undefined, config)
    return ref.afterDismissed();
  }


}

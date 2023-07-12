import {Component, HostBinding, Input} from '@angular/core';
import {
  ConnectorOnlineStatus,
  getOnlineStatusColor,
  getOnlineStatusIcon,
} from './online-status-utils';

@Component({
  selector: 'icon-with-online-status',
  template: `
    <mat-icon
      *ngIf="onlineStatus"
      class="absolute mat-icon-[16px] mt-[26px] ml-[26px]"
      [ngClass]="onlineStatusColor"
      >{{ onlineStatusIcon }}</mat-icon
    >

    <mat-icon class="mat-icon-[40px]">{{ mainIcon }}</mat-icon>
  `,
})
export class IconWithOnlineStatusComponent {
  @HostBinding('class.mat-icon-[40px]')
  cls = true;

  @Input()
  mainIcon!: string;

  @Input()
  onlineStatus!: ConnectorOnlineStatus;

  get onlineStatusColor(): string {
    return getOnlineStatusColor(this.onlineStatus);
  }

  get onlineStatusIcon(): string {
    return getOnlineStatusIcon(this.onlineStatus);
  }
}

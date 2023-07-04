import {Component, HostBinding, Input} from '@angular/core';
import {ConnectorListEntry} from '@sovity.de/broker-server-client';

@Component({
  selector: 'connector-cards',
  templateUrl: './connector-cards.component.html',
})
export class ConnectorCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  connectors: ConnectorListEntry[] = [];
}

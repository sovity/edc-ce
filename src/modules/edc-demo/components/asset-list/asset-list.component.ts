import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {Asset} from '../../models/asset';

@Component({
  selector: 'edc-demo-asset-list',
  templateUrl: './asset-list.component.html',
})
export class AssetListComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  assets: Asset[] = [];

  @Output()
  assetClick = new EventEmitter<Asset>();
}

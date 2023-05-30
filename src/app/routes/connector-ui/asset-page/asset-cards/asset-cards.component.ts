import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {Asset} from '../../../../core/services/models/asset';

@Component({
  selector: 'asset-cards',
  templateUrl: './asset-cards.component.html',
})
export class AssetCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  assets: Asset[] = [];

  @Output()
  assetClick = new EventEmitter<Asset>();
}

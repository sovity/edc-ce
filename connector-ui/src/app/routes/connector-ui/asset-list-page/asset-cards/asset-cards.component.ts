import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

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
  assets: UiAssetMapped[] = [];

  @Output()
  assetClick = new EventEmitter<UiAssetMapped>();
}

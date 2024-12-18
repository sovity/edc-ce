import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'asset-card-tag-list',
  templateUrl: './asset-card-tag-list.component.html',
})
export class AssetCardTagListComponent {
  @HostBinding('class.block') cls = true;
  @Input() numberOfKeywordsDisplayed: number = 3;
  @Input() keywords: string[] | undefined;
  @Input() version: string | undefined;
}

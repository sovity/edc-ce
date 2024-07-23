import {Component, Input} from '@angular/core';

@Component({
  selector: 'edit-asset-form-group',
  templateUrl: './edit-asset-form-group.component.html',
})
export class EditAssetFormGroupComponent {
  @Input() myTitle!: string;
  @Input() description!: string;
}

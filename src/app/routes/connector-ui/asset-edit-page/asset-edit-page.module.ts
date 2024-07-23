import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {EditAssetFormModule} from '../../../component-library/edit-asset-form/edit-asset-form.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {AssetEditPageComponent} from './asset-edit-page/asset-edit-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    RouterModule,

    // EDC UI Modules
    EditAssetFormModule,
    UiElementsModule,
  ],
  declarations: [AssetEditPageComponent],
  exports: [AssetEditPageComponent],
  providers: [],
})
export class AssetEditPageModule {}

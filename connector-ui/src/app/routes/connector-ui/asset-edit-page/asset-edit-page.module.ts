import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {AssetEditPageComponent} from './asset-edit-page/asset-edit-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    RouterModule,

    // EDC UI Modules
    SharedModule,
  ],
  declarations: [AssetEditPageComponent],
  exports: [AssetEditPageComponent],
  providers: [],
})
export class AssetEditPageModule {}

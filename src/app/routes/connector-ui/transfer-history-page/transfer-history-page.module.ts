import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatTableModule} from '@angular/material/table';
import {RouterModule} from '@angular/router';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {TransferHistoryPageComponent} from './transfer-history-page/transfer-history-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // Angular Material
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,

    // EDC UI Modules
    PipesAndDirectivesModule,
    UiElementsModule,
  ],
  declarations: [TransferHistoryPageComponent],
  exports: [TransferHistoryPageComponent],
})
export class TransferHistoryPageModule {}

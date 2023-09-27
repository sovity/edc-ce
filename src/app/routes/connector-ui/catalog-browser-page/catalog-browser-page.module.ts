import {ClipboardModule} from '@angular/cdk/clipboard';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {CatalogModule} from '../../../component-library/catalog/catalog.module';
import {JsonDialogModule} from '../../../component-library/json-dialog/json-dialog.module';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {PropertyGridModule} from '../../../component-library/property-grid/property-grid.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {CatalogBrowserFetchDetailDialogComponent} from './catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.component';
import {CatalogBrowserPageService} from './catalog-browser-page/catalog-browser-page-service';
import {CatalogBrowserPageComponent} from './catalog-browser-page/catalog-browser-page.component';
import {DataOfferBuilder} from './catalog-browser-page/data-offer-builder';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // Angular CDK
    ClipboardModule,

    // Angular Material
    MatBadgeModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatStepperModule,
    MatTableModule,
    MatTabsModule,
    MatTooltipModule,

    // Feature Modules
    CatalogModule,
    PipesAndDirectivesModule,
    UiElementsModule,
    JsonDialogModule,
    PropertyGridModule,
  ],
  declarations: [
    CatalogBrowserPageComponent,
    CatalogBrowserFetchDetailDialogComponent,
  ],
  exports: [CatalogBrowserPageComponent],
  providers: [CatalogBrowserPageService, DataOfferBuilder, DataOfferBuilder],
})
export class CatalogBrowserPageModule {}

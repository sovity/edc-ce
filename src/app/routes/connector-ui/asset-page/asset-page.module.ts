import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {CatalogModule} from '../../../component-library/catalog/catalog.module';
import {DataAddressModule} from '../../../component-library/data-address/data-address.module';
import {PipesAndDirectivesModule} from '../../../component-library/pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {AssetCardsComponent} from './asset-cards/asset-cards.component';
import {AssetEditDialogComponent} from './asset-edit-dialog/asset-edit-dialog.component';
import {AssetEditDialogService} from './asset-edit-dialog/asset-edit-dialog.service';
import {AssetEditDialogFormMapper} from './asset-edit-dialog/form/asset-edit-dialog-form-mapper';
import {AssetPageComponent} from './asset-page/asset-page.component';
import {DataCategorySelectComponent} from './data-category-select/data-category-select.component';
import {DataSubcategoryItemsPipe} from './data-subcategory-select/data-subcategory-items.pipe';
import {DataSubcategorySelectComponent} from './data-subcategory-select/data-subcategory-select.component';
import {KeywordSelectComponent} from './keyword-select/keyword-select.component';
import {LanguageSelectComponent} from './language-select/language-select.component';
import {TransportModeSelectComponent} from './transport-mode-select/transport-mode-select.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // Angular Material
    MatBadgeModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDividerModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatStepperModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,
    MatNativeDateModule,

    // EDC UI Modules
    CatalogModule,
    DataAddressModule,
    PipesAndDirectivesModule,
    UiElementsModule,
  ],
  declarations: [
    AssetCardsComponent,
    AssetEditDialogComponent,
    AssetPageComponent,
    DataCategorySelectComponent,
    DataSubcategorySelectComponent,
    DataSubcategoryItemsPipe,
    KeywordSelectComponent,
    LanguageSelectComponent,
    TransportModeSelectComponent,
  ],
  exports: [AssetPageComponent],
  providers: [AssetEditDialogService, AssetEditDialogFormMapper],
})
export class AssetPageModule {}

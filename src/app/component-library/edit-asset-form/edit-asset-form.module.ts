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
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatStepperModule} from '@angular/material/stepper';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {CatalogModule} from '../catalog/catalog.module';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {UiElementsModule} from '../ui-elements/ui-elements.module';
import {DataCategorySelectComponent} from './data-category-select/data-category-select.component';
import {DataSubcategoryItemsPipe} from './data-subcategory-select/data-subcategory-items.pipe';
import {DataSubcategorySelectComponent} from './data-subcategory-select/data-subcategory-select.component';
import {EditAssetFormDataAddressTypeSelectComponent} from './edit-asset-form-data-address-type-select/edit-asset-form-data-address-type-select.component';
import {EditAssetFormGroupComponent} from './edit-asset-form-group/edit-asset-form-group.component';
import {EditAssetFormLabelComponent} from './edit-asset-form-label/edit-asset-form-label.component';
import {EditAssetFormComponent} from './edit-asset-form/edit-asset-form.component';
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
    MatRadioModule,
    MatIconModule,
    MatDialogModule,
    MatNativeDateModule,

    // EDC UI Modules
    CatalogModule,
    PipesAndDirectivesModule,
    UiElementsModule,
  ],
  declarations: [
    EditAssetFormComponent,
    EditAssetFormGroupComponent,
    EditAssetFormLabelComponent,
    KeywordSelectComponent,
    DataCategorySelectComponent,
    DataSubcategorySelectComponent,
    DataSubcategoryItemsPipe,
    EditAssetFormDataAddressTypeSelectComponent,
    TransportModeSelectComponent,
    LanguageSelectComponent,
  ],
  exports: [EditAssetFormComponent, EditAssetFormLabelComponent],
  providers: [],
})
export class EditAssetFormModule {}

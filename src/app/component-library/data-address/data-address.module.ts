import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {DataAddressTypeSelectComponent} from './data-address-type-select/data-address-type-select.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    ReactiveFormsModule,

    // Angular Material
    MatInputModule,
    MatSelectModule,
  ],
  declarations: [DataAddressTypeSelectComponent],
  exports: [DataAddressTypeSelectComponent],
})
export class DataAddressModule {}

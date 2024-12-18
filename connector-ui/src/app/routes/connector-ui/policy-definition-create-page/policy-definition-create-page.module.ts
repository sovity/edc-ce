import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {TranslateModule} from '@ngx-translate/core';
import {SharedModule} from '../../../shared/shared.module';
import {PolicyDefinitionCreatePageComponent} from './policy-definition-create-page/policy-definition-create-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    TranslateModule,

    // EDC UI Modules
    SharedModule,
  ],
  declarations: [PolicyDefinitionCreatePageComponent],
  exports: [PolicyDefinitionCreatePageComponent],
})
export class PolicyDefinitionCreatePageModule {}

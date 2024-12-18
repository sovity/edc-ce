import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../../../shared/shared.module';
import {PolicyCardsComponent} from './policy-cards/policy-cards.component';
import {PolicyDefinitionPageComponent} from './policy-definition-page/policy-definition-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    // EDC UI Modules
    SharedModule,
  ],
  declarations: [PolicyCardsComponent, PolicyDefinitionPageComponent],
  exports: [PolicyDefinitionPageComponent],
})
export class PolicyDefinitionPageModule {}

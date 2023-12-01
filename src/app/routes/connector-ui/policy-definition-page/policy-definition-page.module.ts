import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatTooltipModule} from '@angular/material/tooltip';
import {RouterModule} from '@angular/router';
import {JsonDialogModule} from '../../../component-library/json-dialog/json-dialog.module';
import {UiElementsModule} from '../../../component-library/ui-elements/ui-elements.module';
import {NewPolicyDialogComponent} from './new-policy-dialog/new-policy-dialog.component';
import {ParticipantIdSelectComponent} from './participant-id-select/participant-id-select.component';
import {PolicyCardsComponent} from './policy-cards/policy-cards.component';
import {PolicyDefinitionPageComponent} from './policy-definition-page/policy-definition-page.component';

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
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatInputModule,
    MatIconModule,
    MatDialogModule,

    // EDC UI Modules
    UiElementsModule,
    JsonDialogModule,
  ],
  declarations: [
    NewPolicyDialogComponent,
    ParticipantIdSelectComponent,
    PolicyCardsComponent,
    PolicyDefinitionPageComponent,
  ],
  exports: [PolicyDefinitionPageComponent],
})
export class PolicyDefinitionPageModule {}

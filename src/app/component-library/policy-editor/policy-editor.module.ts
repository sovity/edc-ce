import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatMenuModule} from '@angular/material/menu';
import {MatSelectModule} from '@angular/material/select';
import {MatTooltipModule} from '@angular/material/tooltip';
import {PipesAndDirectivesModule} from '../pipes-and-directives/pipes-and-directives.module';
import {ParticipantIdSelectComponent} from './editor/controls/participant-id-select/participant-id-select.component';
import {PolicyFormAddMenuComponent} from './editor/policy-form-add-menu/policy-form-add-menu.component';
import {PolicyFormExpressionConstraintComponent} from './editor/policy-form-expression-constraint/policy-form-expression-constraint.component';
import {PolicyFormExpressionEmptyComponent} from './editor/policy-form-expression-empty/policy-form-expression-empty.component';
import {PolicyFormExpressionMultiComponent} from './editor/policy-form-expression-multi/policy-form-expression-multi.component';
import {PolicyFormExpressionComponent} from './editor/policy-form-expression/policy-form-expression.component';
import {PolicyFormRemoveButton} from './editor/policy-form-remove-button/policy-form-remove-button.component';
import {PolicyOperatorSelectComponent} from './editor/policy-operator-select/policy-operator-select.component';
import {PolicyExpressionRecipeService} from './editor/recipes/policy-expression-recipe.service';
import {TimespanRestrictionDialogComponent} from './editor/recipes/timespan-restriction-dialog/timespan-restriction-dialog.component';
import {PolicyExpressionComponent} from './renderer/policy-expression/policy-expression.component';
import {PolicyRendererComponent} from './renderer/policy-renderer/policy-renderer.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    // Angular Material
    MatChipsModule,
    MatTooltipModule,
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatMenuModule,
    MatSelectModule,
    MatTooltipModule,
    MatInputModule,
    MatIconModule,

    // EDC UI Modules
    PipesAndDirectivesModule,
  ],
  declarations: [
    // ./editor
    PolicyFormAddMenuComponent,
    PolicyFormExpressionComponent,
    PolicyFormExpressionEmptyComponent,
    PolicyFormExpressionConstraintComponent,
    PolicyFormExpressionMultiComponent,
    PolicyFormRemoveButton,
    PolicyOperatorSelectComponent,

    // ./editor/controls
    ParticipantIdSelectComponent,

    // ./editor/recipes
    TimespanRestrictionDialogComponent,

    // ./renderer
    PolicyRendererComponent,
    PolicyExpressionComponent,
  ],
  providers: [PolicyExpressionRecipeService],
  exports: [
    PolicyRendererComponent,
    PolicyFormExpressionComponent,
    TimespanRestrictionDialogComponent,
  ],
})
export class PolicyEditorModule {}

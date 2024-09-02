import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SharedModule} from 'src/app/shared.module';
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
    SharedModule,

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

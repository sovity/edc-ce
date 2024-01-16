import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ViewModeEnum} from './view-mode-enum';

@Component({
  selector: 'view-selection',
  templateUrl: './view-selection.component.html',
})
export class ViewSelectionComponent {
  viewModeEnum = ViewModeEnum;
  @Input() selected!: ViewModeEnum;
  @Output() selectedChange = new EventEmitter<ViewModeEnum>();

  onSelection(view: ViewModeEnum) {
    this.selectedChange.emit(view);
  }
}

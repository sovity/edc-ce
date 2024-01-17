import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ViewModeEnum} from './view-mode-enum';
import {ViewModeItem} from './view-mode-item';

@Component({
  selector: 'view-selection',
  templateUrl: './view-selection.component.html',
})
export class ViewSelectionComponent {
  @Input() selected!: ViewModeEnum;
  @Output() selectedChange = new EventEmitter<ViewModeEnum>();

  items: ViewModeItem[] = [
    {
      value: ViewModeEnum.GRID,
      tooltip: 'Grid View',
      icon: 'apps',
    },
    {
      value: ViewModeEnum.LIST,
      tooltip: 'List View',
      icon: 'view_headline',
    },
  ];

  onSelection(view: ViewModeEnum) {
    this.selectedChange.emit(view);
  }
}

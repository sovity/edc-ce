import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {FilterValueSelectItem} from './filter-value-select-item';
import {FilterValueSelectVisibleState} from './filter-value-select-visible-state';

@Component({
  selector: 'filter-value-select',
  templateUrl: './filter-value-select.component.html',
})
export class FilterValueSelectComponent
  implements OnInit, OnChanges, OnDestroy
{
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.space-y-[10px]')
  cls = true;

  @Input()
  state!: FilterValueSelectVisibleState;

  @Output()
  selectedItemsChange = new EventEmitter<FilterValueSelectItem[]>();
  formControl = new FormControl<FilterValueSelectItem[]>([]);

  ngOnInit(): void {
    this.formControl.valueChanges
      .pipe(map((it) => it ?? []))
      .subscribe((selectedItems) => {
        if (!this.state.isEqualSelectedItems(selectedItems)) {
          this.selectedItemsChange.emit(selectedItems);
        }
      });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.state) {
      let selectedItems = this.formControl.value ?? [];
      if (!this.state.isEqualSelectedItems(selectedItems)) {
        this.formControl.setValue(this.state.model.selectedItems);
      }
    }
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}

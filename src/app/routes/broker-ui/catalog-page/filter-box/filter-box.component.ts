import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {SimpleChangesTyped} from '../../../../core/utils/angular-utils';
import {FilterBoxItem} from './filter-box-item';
import {FilterBoxVisibleState} from './filter-box-visible-state';

@Component({
  selector: 'filter-box',
  templateUrl: './filter-box.component.html',
})
export class FilterBoxComponent implements OnInit, OnChanges, OnDestroy {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.space-y-[10px]')
  cls = true;

  @Input()
  state!: FilterBoxVisibleState;

  @Output()
  selectedItemsChange = new EventEmitter<FilterBoxItem[]>();
  formControl = new FormControl<FilterBoxItem[]>([]);

  ngOnInit(): void {
    this.formControl.valueChanges
      .pipe(map((it) => it ?? []))
      .subscribe((selectedItems) => {
        if (!this.state.isEqualSelectedItems(selectedItems)) {
          this.selectedItemsChange.emit(selectedItems);
        }
      });
  }

  ngOnChanges(changes: SimpleChangesTyped<FilterBoxComponent>) {
    if (changes.state) {
      const selectedItems = this.formControl.value ?? [];
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

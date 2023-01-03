import {Component, HostBinding, Input} from '@angular/core';
import {PaymentModalitySelectItem} from "./payment-modality-select-item";
import {FormControl} from "@angular/forms";
import {PaymentModalitySelectItemService} from "./payment-modality-select-item.service";

@Component({
  selector: 'payment-modality-select',
  templateUrl: 'payment-modality-select.component.html',
})
export class PaymentModalitySelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<PaymentModalitySelectItem>

  @HostBinding("class.flex")
  @HostBinding("class.flex-row")
  cls = true

  constructor(public items: PaymentModalitySelectItemService) {
  }

  compareWith(a: PaymentModalitySelectItem | null, b: PaymentModalitySelectItem | null): boolean {
    return a?.id === b?.id;
  }
}

import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'truncated-short-description',
  templateUrl: './truncated-short-description.component.html',
})
export class TruncatedShortDescription {
  @Input() text!: string | undefined;
  @HostBinding('class.whitespace-pre-line')
  @HostBinding('class.line-clamp-5')
  cls = true;
  @HostBinding('class.italic')
  get italic(): boolean {
    return !this.text;
  }
}

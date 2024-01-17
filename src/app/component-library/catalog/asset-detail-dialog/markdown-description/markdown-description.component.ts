import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostBinding,
  Input,
  OnChanges,
  OnInit,
  ViewChild,
} from '@angular/core';
import {HtmlSanitizer} from 'src/app/core/services/html-sanitizer';
import {MarkdownConverter} from 'src/app/core/services/markdown-converter';
import {SimpleChangesTyped} from '../../../../core/utils/angular-utils';

const COLLAPSED_DESCRIPTION_HEIGHT = 280;

@Component({
  selector: 'markdown-description',
  templateUrl: './markdown-description.component.html',
})
export class MarkdownDescriptionComponent
  implements OnInit, OnChanges, AfterViewInit
{
  @HostBinding('class.block') cls = true;
  @Input() description: string | undefined;
  @ViewChild('content') elementView!: ElementRef;
  isLargeDescription = false;
  collapsedDescriptionHeight!: number;

  get isCollapsed(): boolean {
    return this.isLargeDescription && this.collapsed;
  }

  private collapsed = true;
  private isAfterViewInit = false;

  constructor(
    private cd: ChangeDetectorRef,
    public markdownConverter: MarkdownConverter,
    public htmlSanitizer: HtmlSanitizer,
  ) {}

  ngOnInit(): void {
    this.collapsedDescriptionHeight = COLLAPSED_DESCRIPTION_HEIGHT;
  }

  ngOnChanges(changes: SimpleChangesTyped<MarkdownDescriptionComponent>) {
    if (changes.description && this.isAfterViewInit) {
      // We need to wait for the changes to apply first.
      // setTimeout(..., 0) appends the task to the end of the microtask queue
      setTimeout(() => this.recalculateShowMore(), 0);
    }
  }

  ngAfterViewInit() {
    this.isAfterViewInit = true;
    this.recalculateShowMore();
    this.cd.detectChanges();
  }

  onToggleShowMore() {
    this.collapsed = !this.collapsed;
  }

  private recalculateShowMore() {
    const contentHeight = this.elementView.nativeElement.offsetHeight;
    this.isLargeDescription = contentHeight > this.collapsedDescriptionHeight;
  }
}

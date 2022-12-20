import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'policy-rules',
  templateUrl: './policy-rule-viewer.component.html',
  styleUrls: ['./policy-rule-viewer.component.scss']
})
export class PolicyRuleViewerComponent implements OnInit {

  @Input() rules: any[] | undefined = [];
  @Input() title: string =''

  constructor() {
  }

  ngOnInit(): void {
  }
}

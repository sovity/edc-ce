import {Component, Input, OnInit} from '@angular/core';
import {Rule} from "../../../edc-dmgmt-client/model/rule";

@Component({
  selector: 'policy-rules',
  templateUrl: './policy-rule-viewer.component.html',
  styleUrls: ['./policy-rule-viewer.component.scss']
})
export class PolicyRuleViewerComponent implements OnInit {

  @Input() rules: Rule[] | undefined = [];
  @Input() title: string =''

  constructor() {
  }

  ngOnInit(): void {
  }
}

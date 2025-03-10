/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';
import {UntypedFormControl} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {PolicyOperatorConfig} from '../../business/policy-editor/model/policy-operators';

@Component({
  selector: 'policy-operator-select',
  templateUrl: 'policy-operator-select.component.html',
})
export class PolicyOperatorSelectComponent {
  @Input()
  operators: PolicyOperatorConfig[] = [];

  @Input()
  control!: UntypedFormControl;

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  constructor(public translationService: TranslateService) {}

  label = this.translationService.instant('general.operator');
}

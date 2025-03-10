/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
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

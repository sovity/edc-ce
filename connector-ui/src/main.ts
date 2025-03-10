/*
 * Copyright 2022 Eclipse Foundation and Contributors
 * Copyright 2025 sovity GmbH
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
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 */
import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {AppModule} from './app/app.module';
import {
  loadAppConfig,
  provideAppConfig,
} from './app/core/config/app-config-initializer';
import {environment} from './environments/environment';

if (environment.production) {
  enableProdMode();
}

// We fetch the config here, because we need the config before APP_INITIALIZER,
// because we want to decide our routes based on our config, and ROUTES needs
// to be provided before APP_INITIALIZER.
loadAppConfig()
  .then((appConfig) =>
    platformBrowserDynamic([provideAppConfig(appConfig)]).bootstrapModule(
      AppModule,
    ),
  )
  .catch((err) => console.error(err));

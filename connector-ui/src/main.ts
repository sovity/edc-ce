import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {AppModule} from './app/app.module';
import {loadAppConfig} from './app/core/config/app-config-initializer';
import {environment} from './environments/environment';

if (environment.production) {
  enableProdMode();
}

// We fetch the config here, because we need the config before APP_INITIALIZER,
// because we want to decide our routes based on our config, and ROUTES needs
// to be provided before APP_INITIALIZER.
loadAppConfig()
  .then(() => platformBrowserDynamic().bootstrapModule(AppModule))
  .catch((err) => console.error(err));

import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {APP_CONFIG, AppConfig} from '../../config/app-config';

@Injectable()
export class ApiKeyInterceptor implements HttpInterceptor {
  constructor(@Inject(APP_CONFIG) private config: AppConfig) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    const apiKey = this.getApiKey();
    if (apiKey) {
      req = req.clone({
        setHeaders: {'X-Api-Key': apiKey},
      });
    }
    return next.handle(req);
  }

  private getApiKey() {
    return this.config.managementApiKey;
  }
}

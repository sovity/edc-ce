import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AppConfigService} from './config/app-config.service';

@Injectable()
export class ApiKeyInterceptor implements HttpInterceptor {
  constructor(private appConfigService: AppConfigService) {}

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
    return this.appConfigService.config.dataManagementApiKey;
  }
}

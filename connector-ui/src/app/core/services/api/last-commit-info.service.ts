import {HttpClient} from '@angular/common/http';
import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {APP_CONFIG, AppConfig} from '../../config/app-config';
import {LastCommitInfo} from './model/last-commit-info';

@Injectable({
  providedIn: 'root',
})
export class LastCommitInfoService {
  constructor(
    private http: HttpClient,
    @Inject(APP_CONFIG) public config: AppConfig,
  ) {}

  getLastCommitInfoData(): Observable<LastCommitInfo> {
    const url = `${this.config.managementApiUrl}/last-commit-info`;
    return this.http.get<LastCommitInfo>(url);
  }

  getUiCommitDetails(): Observable<string> {
    const path = '/assets/config/version.txt';
    return this.http.get(path, {responseType: 'text'});
  }

  getUiBuildDateDetails(): Observable<string> {
    const path = '/assets/config/ui-build-date.txt';
    return this.http.get(path, {responseType: 'text'});
  }
}

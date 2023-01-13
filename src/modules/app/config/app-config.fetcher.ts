import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {firstValueFrom} from "rxjs";

@Injectable()
export class AppConfigFetcher {
  constructor(private http: HttpClient) {
  }

  /**
   * Fetch app-config.json.
   */
  fetchConfigJson(): Promise<Record<string, string | null>> {
    return firstValueFrom(this.http.get<Record<string, string | null>>('/assets/config/app-config.json'));
  }
}

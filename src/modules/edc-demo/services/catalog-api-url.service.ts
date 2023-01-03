import {Injectable} from '@angular/core';
import {BehaviorSubject, distinct} from 'rxjs';
import {AppConfigService} from "../../app/app-config.service";

/**
 * Stores Catalog API Url
 */
@Injectable({
  providedIn: 'root'
})
export class CatalogApiUrlService {
  /**
   * From app.config.json
   */
  private readonly presetCatalogApiUrls = new Array<string>();

  /**
   * User-editable
   */
  private customCatalogApiUrls = new Array<string>();

  constructor(private appConfigService: AppConfigService) {
    this.presetCatalogApiUrls = this.splitUrls(appConfigService.getConfig()?.catalogUrl);
  }

  getCatalogApiUrls(): string[] {
    return this.distinct([
      ...this.presetCatalogApiUrls,
      ...this.customCatalogApiUrls
    ])
  }

  setCustomApiUrlString(apiUrlString: string) {
    this.setCustomApiUrls(this.splitUrls(apiUrlString));
  }

  setCustomApiUrls(apiUrls: string[]) {
    this.customCatalogApiUrls = apiUrls;
  }

  getPresetApiUrls(): string[] {
    return this.presetCatalogApiUrls;
  }

  private distinct<T>(array: T[]): T[] {
    return [...new Set(array)];
  }

  private splitUrls(commaJoinedUrls?: string | null): string[] {
    return commaJoinedUrls?.split(",")?.map(url => url.trim())?.filter(url => url.length) ?? [];
  }
}

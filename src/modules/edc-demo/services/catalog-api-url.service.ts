import {Injectable} from '@angular/core';
import {AppConfigService} from "../../app/app-config.service";

/**
 * Stores Catalog API URLs
 */
@Injectable({
  providedIn: 'root'
})
export class CatalogApiUrlService {
  /**
   * From app.config.json, not user editable
   */
  private readonly presetCatalogApiUrls = new Array<string>();

  /**
   * User-editable
   */
  private customCatalogApiUrls = new Array<string>();

  constructor(private appConfigService: AppConfigService) {
    this.presetCatalogApiUrls = this.splitUrls(appConfigService.getConfig()?.catalogUrl);
  }

  /**
   * Get all configured catalog API URLs
   */
  getCatalogApiUrls(): string[] {
    return this.distinct([
      ...this.presetCatalogApiUrls,
      ...this.customCatalogApiUrls
    ])
  }

  /**
   * Get preset catalog API URLs
   */
  getPresetApiUrls(): string[] {
    return this.presetCatalogApiUrls;
  }

  setCustomApiUrlString(apiUrlString: string) {
    this.setCustomApiUrls(this.splitUrls(apiUrlString));
  }

  setCustomApiUrls(apiUrls: string[]) {
    this.customCatalogApiUrls = apiUrls;
  }

  private splitUrls(commaJoinedUrls?: string | null): string[] {
    return commaJoinedUrls?.split(",")?.map(url => url.trim())?.filter(url => url.length) ?? [];
  }

  private distinct<T>(array: T[]): T[] {
    return [...new Set(array)];
  }
}

import {Injectable} from '@angular/core';
import {AppConfigService} from '../../app/config/app-config.service';


/**
 * Stores Catalog API URLs
 */
@Injectable({
  providedIn: 'root',
})
export class CatalogApiUrlService {
  /**
   * From app-config.json, not user editable
   */
  private readonly presetProviders = new Array<string>();

  /**
   * User-editable
   */
  private customProviders = new Array<string>();

  constructor(private appConfigService: AppConfigService) {
    this.presetProviders = this.splitUrls(
      appConfigService.config.catalogUrl,
    );
  }

  /**
   * Get all configured catalog API URLs
   */
  getCatalogApiUrls(): string[] {
    return this.distinct([
      ...this.presetProviders,
      ...this.customProviders,
    ]).map(url => this.buildCatalogApiUrl(url));
  }

  /**
   * Get preset catalog API URLs
   */
  getPresetProviders(): string[] {
    return this.presetProviders;
  }

  setCustomProvidersAsString(apiUrlString: string) {
    this.setCustomProviders(this.splitUrls(apiUrlString));
  }

  setCustomProviders(apiUrls: string[]) {
    this.customProviders = apiUrls;
  }

  /**
   * We fetch catalogs by proxying them through our own backend.
   *
   * @param catalogUrl target connector url (or legacy full url)
   * @return full url for fetching catalog
   */
  private buildCatalogApiUrl(catalogUrl: string) {
    const baseUrl = this.appConfigService.config.dataManagementApiUrl;

    // Still support manually built URLs
    const prefix = `${baseUrl}/catalog?providerUrl=`;
    if (catalogUrl.startsWith(prefix)) {
      return catalogUrl;
    }

    // But prefer just entering connector URLs
    return `${prefix}${encodeURIComponent(catalogUrl)}`;
  }

  private splitUrls(commaJoinedUrls?: string | null): string[] {
    return (
      commaJoinedUrls
        ?.split(',')
        ?.map((url) => url.trim())
        ?.filter((url) => url.length) ?? []
    );
  }

  private distinct<T>(array: T[]): T[] {
    return [...new Set(array)];
  }
}

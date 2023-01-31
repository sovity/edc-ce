import {Injectable} from '@angular/core';
import {AppConfigService} from '../../app/config/app-config.service';

/**
 * Builds Catalog fetch URLs. Stores preset and user configured Connector IDs.
 */
@Injectable({
  providedIn: 'root',
})
export class CatalogApiUrlService {
  /**
   * Preset Connector IDs to be used in catalog.
   *
   * From app-config.json, not user editable
   */
  private readonly presetProviders = new Array<string>();

  /**
   * User-added Connector IDs
   */
  private customProviders = new Array<string>();

  constructor(private appConfigService: AppConfigService) {
    this.presetProviders = this.splitUrls(appConfigService.config.catalogUrl);
  }

  /**
   * Get all configured catalog API URLs
   */
  getCatalogApiUrls(): string[] {
    return this.distinct([
      ...this.presetProviders,
      ...this.customProviders,
    ]).map((url) => this.buildCatalogApiUrl(url));
  }

  /**
   * Get preset catalog API URLs
   */
  getPresetProviders(): string[] {
    return this.presetProviders;
  }

  setCustomProvidersAsString(connectorIdsCommaSeparated: string) {
    this.setCustomProviders(this.splitUrls(connectorIdsCommaSeparated));
  }

  setCustomProviders(connectorIds: string[]) {
    this.customProviders = connectorIds;
  }

  /**
   * We fetch catalogs by proxying them through our own backend.
   *
   * @param connectorId target connector id (or legacy full url
   * targeted at own connector with query param providerUrl)
   * @return full url for fetching catalog
   */
  private buildCatalogApiUrl(connectorId: string) {
    // Detect legacy URLs
    const prefix = `${this.appConfigService.config.dataManagementApiUrl}/catalog?providerUrl=`;
    if (connectorId.startsWith(prefix)) {
      return connectorId;
    }

    // Build Catalog API URL from Connector ID
    return `${prefix}${encodeURIComponent(connectorId)}`;
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

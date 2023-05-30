import {Inject, Injectable} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../../config/app-config';

/**
 * Builds Catalog fetch URLs. Stores preset and user configured Connector Endpoints.
 */
@Injectable({
  providedIn: 'root',
})
export class CatalogApiUrlService {
  /**
   * Preset Connector Endpoints to be used in catalog.
   *
   * From app-config.json, not user editable
   */
  private readonly presetProviders = new Array<string>();

  /**
   * User-added Connector Endpoints
   */
  private customProviders = new Array<string>();

  constructor(@Inject(APP_CONFIG) private config: AppConfig) {
    this.presetProviders = this.splitUrls(this.config.catalogUrls);
  }

  /**
   * Get all configured catalog URLs
   */
  getAllProviders() {
    return this.distinct([...this.presetProviders, ...this.customProviders]);
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
   * @param connectorEndpoint target connector endpoint (or legacy full url
   * targeted at own connector with query param providerUrl)
   * @return full url for fetching catalog
   */
  buildCatalogApiUrl(connectorEndpoint: string) {
    // Detect legacy URLs
    const prefix = `${this.config.managementApiUrl}/catalog?providerUrl=`;
    if (connectorEndpoint.startsWith(prefix)) {
      return connectorEndpoint;
    }

    // Build Catalog API URL from Connector Endpoint
    return `${prefix}${encodeURIComponent(connectorEndpoint)}`;
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

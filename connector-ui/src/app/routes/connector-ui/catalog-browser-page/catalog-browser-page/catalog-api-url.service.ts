/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Inject, Injectable} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../../../../core/config/app-config';

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
   * From app-configuration.json, not user editable
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
